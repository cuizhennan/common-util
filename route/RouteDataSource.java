package commons.route;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * commons.route
 *
 * @Project disney
 * @Author Moxiao 2019-06-03 11:14
 * @Use Mybatis route
 */
public class RouteDataSource extends AbstractRoutingDataSource {
    /**
     * 线程安全容器
     */
    private static ThreadLocal<String> holder = new ThreadLocal<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return holder.get();
    }

    /**
     * 设置dbKey
     *
     * @param dbKey
     */
    public static void setDbKey(RouteDataSourceKeyEnum dbKey) {
        holder.set(null == dbKey ? null : dbKey.name());
    }

    public String getDbKey() {
        return holder.get();
    }
}
