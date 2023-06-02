package com.yue.redis.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import reactor.core.publisher.Flux;

@Configuration
@ConditionalOnClass({ReactiveRedisConnectionFactory.class, ReactiveRedisTemplate.class, Flux.class})
@AutoConfiguration(
        before = {RedisReactiveAutoConfiguration.class}
)
public class RedisReactiveCacheAutoConfiguration {

    @Bean
    @ConditionalOnClass(value = ReactiveRedisTemplate.class)
    public RedisSerializationContext<String,Object> redisSerializationContext() {
        RedisSerializationContext.RedisSerializationContextBuilder<String,Object> builder = RedisSerializationContext.newSerializationContext();
        builder.key(StringRedisSerializer.UTF_8);
        builder.value(RedisSerializer.json());
        builder.hashKey(StringRedisSerializer.UTF_8);
        builder.hashValue(RedisSerializer.json());
        return builder.build();
    }

    @Bean
    @ConditionalOnBean(value = ReactiveRedisConnectionFactory.class)
    public ReactiveRedisTemplate<String,Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String,Object> serializationContext = redisSerializationContext();
        return new ReactiveRedisTemplate<>(connectionFactory,serializationContext);
    }
}
