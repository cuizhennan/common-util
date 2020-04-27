package commons.route;

import com.fordeal.disney.util.FurionUtil;
import com.fordeal.disney.util.alarm.WXRobotAlarmUtil;
import com.fordeal.furion.client.FurionClient;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * commons.route
 *
 * @Author Moxiao 2019-06-03 14:30
 * @Use data source 注解拦截器
 */
@Component
@Aspect
public class DataSourceAspect {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceAspect.class.getName());
    @Resource
    FurionUtil furionUtil;
    @Resource
    WXRobotAlarmUtil wxRobotAlarmUtil;

    @Before(value = "execution(* com.fordeal.disney..dao..*(..))")
    public void before(JoinPoint point) {
        String dataSource = "";
        Object target = point.getTarget();
        String method = point.getSignature().getName();
        Class<? extends Object> classz = target.getClass();

        Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
        try {
            Method m = classz.getMethod(method, parameterTypes);
            if (m != null && m.isAnnotationPresent(DataSource.class)) {
                DataSource data = m.getAnnotation(DataSource.class);
                RouteDataSource.setDbKey(data.value());
                dataSource = data.value().name();
            }
        } catch (Exception e) {
            logger.error("DATA-SOURCE-ROUTE aspect ERROR", e);
        } finally {
            logger.debug("DATA-SOURCE-ROUTE aspect {}, 【{}】", method, dataSource);
        }
    }

    @After(value = "execution(* com.fordeal.disney..dao..*(..))")
    public void after(JoinPoint point) {
        RouteDataSource.setDbKey(null);
        Object target = point.getTarget();
        String method = point.getSignature().getName();
        String packageName = target.getClass().getName() + "." + method;
        logger.debug("DATA-SOURCE-ROUTE After aspect {}", packageName);
    }

//    /**
//     * 统计sql中方法调用的时间
//     * @param joinPoint
//     * @throws Throwable
//     */
//    @Around(value = "execution(* com.fordeal.disney..dao..*(..))")
//    public Object logSqlMethodAccess(ProceedingJoinPoint joinPoint) throws Throwable {
//        long start = System.currentTimeMillis();
//        Object object = joinPoint.proceed();
//        long end = System.currentTimeMillis();
//        long t = end - start;
//        long sqlLimit =furionUtil.getLongValueWithDefault("sqlTime",100L);
//        if(t>=sqlLimit){
//            String tmp = joinPoint.getSignature().toString();
//            logger.info(String.format("class:%s,invoke_time:%s",tmp,t));
//            wxRobotAlarmUtil.sendAlarmPromGroup(String.format("class:%s,invoke_time:%s",tmp,t));
//        }
//        return object;
//    }
}
