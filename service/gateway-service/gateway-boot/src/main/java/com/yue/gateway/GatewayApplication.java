package com.yue.gateway;

import com.yue.route.register.annotation.EnableRouteRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableRouteRegister
public class GatewayApplication {

    public static void main(String[] args) {
       SpringApplication.run(GatewayApplication.class, args);
    }
}
