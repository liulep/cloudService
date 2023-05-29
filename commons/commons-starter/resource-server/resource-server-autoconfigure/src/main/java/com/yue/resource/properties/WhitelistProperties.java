package com.yue.resource.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "whitelist.ignore")
public class WhitelistProperties {

    private String[] urls;
}
