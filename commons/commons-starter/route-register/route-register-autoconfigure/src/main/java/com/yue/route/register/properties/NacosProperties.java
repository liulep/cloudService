package com.yue.route.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "dynamic.nacos")
public class NacosProperties {

    public static final long DEFAULT_TIME_OUT= 30000;

    private String serverAddr;

    private String namespace;

    private String dataId;

    private String group;

    private String username;

    private String password;
}
