package com.yue.mybatis.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"com.yue.*.mapper","com.yue.*.*.mapper"})
public class MybatisAutoConfiguration {
}
