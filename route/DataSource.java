package com.fordeal.disney.commons.route;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * com.fordeal.disney.commons.route
 *
 * @Project disney
 * @Author Moxiao 2019-06-03 14:28
 * @Use 主从注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface DataSource {
    RouteDataSourceKeyEnum value();
}
