package com.yue.route.dynamic.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.yue.route.dynamic.properties.NacosDynamicProperties;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher publisher;
    private final RouteDefinitionRepository repository;


    public DynamicRouteServiceImpl(RouteDefinitionRepository repository){
        this.repository=repository;
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.publisher=applicationEventPublisher;
    }

    // 添加路由
    public String add(RouteDefinition definition){
        try {
            log.info("addRoute => {}",definition.getId());
            this.repository.save(Mono.just(definition)).subscribe();
            this.refreshRoute();
        }catch (Exception e){
            return "save fail";
        }
        return "save success";
    }

    // 删除路由
    public String delete(String id){
        try {
            this.repository.delete(Mono.just(id)).subscribe();
            this.refreshRoute();
        }catch (Exception e){
            return "delete fail";
        }
        return "delete success";
    }

    // 更新路由
    public String updateById(RouteDefinition definition){
        try {
            this.delete(definition.getId());
            this.add(definition);
        }catch (Exception e){
            return "update fail";
        }
        return "update success";
    }

    // 更新路由
    public String updateByList(List<RouteDefinition> definitions){
        try {
            // 删除缓存中的路由
            List<RouteDefinition> routeDefinitions = repository.getRouteDefinitions().buffer().blockFirst();
            if(!CollectionUtils.isEmpty(routeDefinitions)){
                routeDefinitions.forEach(item -> this.delete(item.getId()));
            }
            definitions.forEach(this::add);
        }catch (Exception e){
            return "update fail";
        }
        return "update success";
    }

    // 刷新路由
    public void refreshRoute(){
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    //获取所有路由
    public List<RouteDefinition> getRoutes(){
        return repository.getRouteDefinitions().buffer().blockFirst();
    }
}
