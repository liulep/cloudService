package com.yue.route.register.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "register.route")
public class RouteProperties {

    @Value("${spring.application.name}")
    private String serverName;

    private String prefix = "lb://";

    private String suffix;

    private Integer endSub=0;

    private Integer beginSub=0;

    private Integer endExclude=0;

    private Integer exclude=0;

    private String route;
}
