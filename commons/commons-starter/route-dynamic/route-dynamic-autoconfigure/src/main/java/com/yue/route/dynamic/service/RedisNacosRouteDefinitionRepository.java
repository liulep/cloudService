package com.yue.route.dynamic.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class RedisNacosRouteDefinitionRepository implements RouteDefinitionRepository {

    private static final String ROUTE_REDIS="gateway_routes";
    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    public RedisNacosRouteDefinitionRepository(ReactiveRedisTemplate<String,Object> reactiveRedisTemplate){
        this.reactiveHashOperations= reactiveRedisTemplate.opsForHash();
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return this.reactiveHashOperations.values(ROUTE_REDIS)
                .cast(RouteDefinition.class)
                .onErrorContinue((throwable, routeDefinition) -> {
                    if (log.isErrorEnabled()) {
                        log.error("get routes from redis error cause : {}", throwable.toString(), throwable);
                    }
                });
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(routeDefinition -> this.reactiveHashOperations
                .put(ROUTE_REDIS,routeDefinition.getId(), routeDefinition).flatMap(success -> {
                    if (success) {
                        return Mono.empty();
                    }
                    return Mono.defer(() -> Mono.error(new RuntimeException(
                            String.format("Could not add route to redis repository: %s", routeDefinition))));
                }));
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> this.reactiveHashOperations.remove(ROUTE_REDIS,id).flatMap(num -> {
            if (num > 0) {
                return Mono.empty();
            }
            return Mono.defer(() -> Mono.error(new NotFoundException(
                    String.format("Could not remove route from redis repository with id: %s", routeId))));
        }));
    }
}
