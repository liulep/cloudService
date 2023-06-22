package com.yue.file.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "file.suffix")
public class FileSuffixProperties {
    private List<String> includes;

    private Integer expiresDay = 1;

    private String urlPrefix = "http://localhost:8085/file/";
}
