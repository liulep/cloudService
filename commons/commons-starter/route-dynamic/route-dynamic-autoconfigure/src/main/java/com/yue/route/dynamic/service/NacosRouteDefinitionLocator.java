package com.yue.route.dynamic.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.yue.route.dynamic.properties.NacosDynamicProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import reactor.core.publisher.Flux;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.synchronizedMap;

@Slf4j
public class NacosRouteDefinitionLocator implements RouteDefinitionLocator {

    private final Map<String, RouteDefinition> routes = synchronizedMap(new LinkedHashMap<String, RouteDefinition>());
    private final NacosDynamicProperties nacosDynamicProperties;
    private final ConfigService configService;
//    private final DynamicRouteServiceImpl dynamicRouteService;
    private DynamicRouteServiceImpl dynamicRouteService;

    public NacosRouteDefinitionLocator(ConfigService configService,
                                       NacosDynamicProperties properties,
                                       DynamicRouteServiceImpl dynamicRouteService){
        this.configService=configService;
        this.nacosDynamicProperties=properties;
        this.dynamicRouteService=dynamicRouteService;
        initRouting();
    }

    public NacosRouteDefinitionLocator(ConfigService configService,
                                       NacosDynamicProperties properties){
        this.configService=configService;
        this.nacosDynamicProperties=properties;
        initRouting();
    }


    public void setDynamicRouteService(DynamicRouteServiceImpl dynamicRouteService){
        this.dynamicRouteService=dynamicRouteService;
    }

    public void initRouting(){
        try {
//            log.info("初始化前 routes => {}",routes);
            String config = configService.getConfig(nacosDynamicProperties.getDataId(), nacosDynamicProperties.getGroup(), NacosDynamicProperties.DEFAULT_TIME_OUT);
            List<RouteDefinition> routes = JSON.parseArray(config, RouteDefinition.class);
            if(!routes.isEmpty()) {
                routes.forEach(item -> {
                    this.routes.put(item.getId(), item);
                });
            }
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
//        log.info("初始化后 routes => {}",routes);
        dynamicByNacosListener();
    }

    public void dynamicByNacosListener() {
        try {
            configService.addListener(nacosDynamicProperties.getDataId(), nacosDynamicProperties.getGroup(), new AbstractListener() {
                @Override
                public void receiveConfigInfo(String config) {
                    synchronized (AbstractListener.class){
//                        log.info("监听前 routes => {}",routes);
                        List<RouteDefinition> routes = JSON.parseArray(config, RouteDefinition.class);
                        dynamicRouteService.updateByList(routes);
//                        log.info("监听后 routes => {}" ,routes);
                    }
                }
            });
        }catch (NacosException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(routes.values());
    }
}
