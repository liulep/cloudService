package com.yue.route.dynamic.listener;
import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import com.yue.route.dynamic.properties.NacosDynamicProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;

import java.util.List;

@Slf4j
public class NacosInstancesChangeEventListener extends Subscriber<InstancesChangeEvent> {
    private final RouteDefinitionRepository repository;
    private final CachingRouteLocator routeLocator;
    private final ConfigService configService;
    private final NacosDynamicProperties properties;

    public NacosInstancesChangeEventListener(RouteDefinitionRepository repository ,
                                             CachingRouteLocator routeLocator,
                                             ConfigService configService,
                                             NacosDynamicProperties properties){
        this.repository=repository;
        this.routeLocator=routeLocator;
        this.configService=configService;
        this.properties=properties;
        NotifyCenter.registerSubscriber(this);
    }

    @Override
    public void onEvent(InstancesChangeEvent event) {
        if(log.isDebugEnabled()){
            log.info("{}实例状态更新，Gateway开始刷新缓存", event.getServiceName());
        }
        //获取所有实例
        List<RouteDefinition> routeDefinitions = this.repository.getRouteDefinitions().buffer().blockFirst();
        //更新实例缓存
        if(ObjectUtils.isNotEmpty(routeDefinitions)
                && StringUtils.isNotBlank(event.getServiceName())
                && ObjectUtils.isEmpty(event.getHosts())){
            //该实例存在缓存中时进行删除
            List<RouteDefinition> newRouteDefinitions = routeDefinitions.stream()
                    .filter(route -> !route.getId().equals(event.getServiceName()))
                    .toList();
            //清空路由信息
            this.routeLocator.refresh().subscribe();
            this.routeLocator.onApplicationEvent(new RefreshRoutesEvent(this));
            //删除nacos中该实例配置
            this.pushConfig(JSON.toJSONString(newRouteDefinitions));
        }
        log.info("Gateway刷新缓存完成");
    }

    //删除nacos中的该实例的配置
    public void pushConfig(String content){
        try {
            this.configService.publishConfig(properties.getDataId(), properties.getGroup(), content, ConfigType.JSON.getType());
        } catch (NacosException e) {
            throw new RuntimeException("nacos发布配置失败");
        }
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return InstancesChangeEvent.class;
    }

}
