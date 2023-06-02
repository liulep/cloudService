package com.yue.route.dynamic.config;
import com.yue.route.dynamic.service.RedisNacosRouteDefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;

@Slf4j
@Configuration
@RequiredArgsConstructor
@AutoConfiguration(
        after = {RedisReactiveAutoConfiguration.class},
        before = GatewayAutoConfiguration.class
)
@ConditionalOnClass({ReactiveRedisTemplate.class, Flux.class})
public class RedisRouteDynamicAutoConfiguration {

    private final ApplicationEventPublisher applicationEventPublisher;

    //使用redis做缓存
    @Bean
    @ConditionalOnBean(value = {ReactiveRedisTemplate.class})
    public RedisNacosRouteDefinitionRepository redisNacosRouteDefinitionRepository(ReactiveRedisTemplate<String,Object> reactiveRedisTemplate){
        return new RedisNacosRouteDefinitionRepository(reactiveRedisTemplate);
    }

}
