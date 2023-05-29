package com.yue.feign.config;

import com.yue.feign.intercept.FeignRequestInterceptor;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import(value = FeignRequestInterceptor.class)
public class FeignAutoConfiguration {

    /**
     * 开启openFeign的日志增强，生产环境中关闭
     */
    @Profile(value = {"dev","test"})
    @Bean
    public Logger.Level level(){
        return Logger.Level.FULL;
    }
}
