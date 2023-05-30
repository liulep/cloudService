package com.yue.mybatis.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
@MapperScan(basePackages = {"com.yue.*.mapper","com.yue.*.*.mapper"})
public class MybatisAutoConfiguration {

    @Bean
    public MetaObjectHandler metaObjectHandler(){
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.setFieldValByName("createTime",new Date(),metaObject);
                this.setFieldValByName("updateTime",new Date(),metaObject);
                this.setFieldValByName("isDel",0,metaObject);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.setFieldValByName("updateTime",new Date(),metaObject);
            }
        };
    }
}
