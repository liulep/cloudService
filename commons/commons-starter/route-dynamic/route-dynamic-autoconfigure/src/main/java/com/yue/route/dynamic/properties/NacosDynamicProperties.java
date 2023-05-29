package com.yue.route.dynamic.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix ="dynamic.nacos")
public class NacosDynamicProperties {

    public static final long DEFAULT_TIME_OUT= 30000;

    private String serverAddr="127.0.0.1:8848";

    private String namespace;

    private String dataId;

    private String group = "DEFAULT_GROUP";

    private String username;

    private String password;
}
