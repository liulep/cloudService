package com.yue.file.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    //对象存储服务的URL
    private String endpoint;

    //地区
    private String region;

    //是否以路径访问
    private Boolean pathStyleAccess=false;

    // Access Key
    private String accessKey;

    // Secret Key
    private String secretKey;

    // 最大线程数，默认：100
    private Integer maxConnections=100;

    // socket连接超时，默认：10000
    private Integer socketTimeOut = 10000;

    //是否分块编码
    private Boolean chunkedEncoding = true;

    //是否切片
    private Boolean slice = false;

    //是否存在APPID
    private String appId;

    //切片大小,默认大小单位MB
    private Integer sliceSize = 100;

    //文件多大时进行切片，默认大小单位MB
    private Integer fileMaxSize = 1024;

}
