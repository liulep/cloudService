package com.yue.route.register.annotation;

import com.yue.route.register.properties.NacosProperties;
import com.yue.route.register.properties.RouteProperties;
import com.yue.route.register.service.RouteRegisterImportSelector;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = RouteRegisterImportSelector.class)
@EnableConfigurationProperties(value = {NacosProperties.class, RouteProperties.class})
public @interface EnableRouteRegister {
}
