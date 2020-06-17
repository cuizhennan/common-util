package com.jing.train.common.route;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    @Before(value = "execution(* jing*train..dao..*(..))")
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

    @After(value = "execution(* jing*train..dao..*(..))")
    public void after(JoinPoint point) {
        RouteDataSource.setDbKey(null);
        Object target = point.getTarget();
        String method = point.getSignature().getName();
        String packageName = target.getClass().getName() + "." + method;
        logger.debug("DATA-SOURCE-ROUTE After aspect {}", packageName);
    }
}
