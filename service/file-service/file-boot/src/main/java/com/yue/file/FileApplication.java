package com.yue.file;

import com.yue.file.core.properties.FileSuffixProperties;
import com.yue.route.register.annotation.EnableRouteRegister;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRouteRegister
@EnableConfigurationProperties(value = FileSuffixProperties.class)
public class FileApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class,args);
    }
}
