package com.yue.route.dynamic.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.yue.route.dynamic.event.NacosRoutesEvent;
import com.yue.route.dynamic.service.DynamicRouteServiceImpl;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationListener;

import java.util.List;

//nacos路由配置信息监听
public class NacosRouteConfigServiceListener implements ApplicationListener<NacosRoutesEvent> {

    private final ConfigService configService;
    private final DynamicRouteServiceImpl routeService;

    public NacosRouteConfigServiceListener(ConfigService configService,
                                           DynamicRouteServiceImpl routeService){
        this.configService=configService;
        this.routeService=routeService;
    }

    //发布监听
    @Override
    public void onApplicationEvent(NacosRoutesEvent event) {
        try {
            this.routeService.refreshRoute();
            configService.addListener(event.getDataId(), event.getGroup(), new AbstractListener() {
                @Override
                public void receiveConfigInfo(String config) {
                    synchronized (AbstractListener.class){
                        List<RouteDefinition> routes = JSON.parseArray(config, RouteDefinition.class);
                        routeService.updateByList(routes);
                    }
                }
            });
        }catch (NacosException e){
            throw new RuntimeException(e);
        }
    }
}
