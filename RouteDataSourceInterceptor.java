package com.jing.train.common.route;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * commons.route
 *
 * @Author Moxiao
 * @Use mybatis 动态数据源拦截器
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class RouteDataSourceInterceptor implements Interceptor {
    protected static final Logger logger = LoggerFactory.getLogger(RouteDataSourceInterceptor.class.getName());

    private static final String REGEX = ".*insert\\u0020.*|.*delete\\u0020.*|.*update\\u0020.*";

    private static final Map<String, RouteDataSourceKeyEnum> cacheMap = new ConcurrentHashMap<>();

    @Resource
    private RouteDataSource dataSource;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (null != dataSource) {
            String dbKey = dataSource.getDbKey();
            if (StringUtils.isNotBlank(dbKey)) {
                logger.info("MYBATIS-PLUGINS-ROUTE dataSource {}", dbKey);
                return invocation.proceed();
            }
        }
        /*
         *它首先查看当前是否存在事务管理上下文，并尝试从事务管理上下文获取连接，如果获取失败，直接从数据源中获取连接。
         *在获取连接后，如果当前拥有事务上下文，则将连接绑定到事务上下文中。（此处直接继续下一过程）
         */
        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        if (!synchronizationActive) {
            Object[] objects = invocation.getArgs();
            MappedStatement ms = (MappedStatement) objects[0];

            RouteDataSourceKeyEnum routeDataSourceKeyEnum = null;

            if ((routeDataSourceKeyEnum = cacheMap.get(ms.getId())) == null) {
                //读方法
                if (ms.getSqlCommandType().equals(SqlCommandType.SELECT)) {
                    //!selectKey 为自增id查询主键(SELECT LAST_INSERT_ID() )方法，使用主库
                    if (ms.getId().contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
                        routeDataSourceKeyEnum = RouteDataSourceKeyEnum.MASTER;
                    } else {
                        BoundSql boundSql = ms.getSqlSource().getBoundSql(objects[1]);
                        String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replaceAll("[\\t\\n\\r]", " ");
                        if (sql.matches(REGEX)) {
                            routeDataSourceKeyEnum = RouteDataSourceKeyEnum.MASTER;
                        } else {
                            routeDataSourceKeyEnum = RouteDataSourceKeyEnum.SLAVE;
                        }
                    }
                } else {
                    routeDataSourceKeyEnum = RouteDataSourceKeyEnum.MASTER;
                }
                logger.info("MYBATIS-PLUGINS-ROUTE dataSource 设置方法[{}] use [{}] Strategy, SqlCommandType [{}].", ms.getId(), routeDataSourceKeyEnum.name(), ms.getSqlCommandType().name());
                cacheMap.put(ms.getId(), routeDataSourceKeyEnum);
            }
            RouteDataSource.setDbKey(routeDataSourceKeyEnum);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
