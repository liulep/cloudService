package com.yue.route.register.listener;

import com.alibaba.fastjson2.JSON;
import com.yue.route.register.entity.RouteDefinition;
import com.yue.route.register.properties.NacosProperties;
import com.yue.route.register.properties.RouteProperties;
import com.yue.route.register.service.RouteRegisterBeanPostProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

//路由删除监听,不建议使用，在多个同服务的情况下会导致下线一个服务，其余服务访问失败
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {NacosProperties.class, RouteProperties.class})
@Deprecated
public class RouteDeleteListener implements DisposableBean {

    private final NacosProperties nacosProperties;
    private final RouteProperties routeProperties;
    private final RouteRegisterBeanPostProcess routeService;

    @Override
    public void destroy() throws Exception {
        List<RouteDefinition> routes = this.routeService.getRoutes();
        if(ObjectUtils.isNotEmpty(routes)){
            List<RouteDefinition> newRoutes = routes.stream()
                    .filter(route -> !route.getId().equals(routeProperties.getServerName()))
                    .toList();
            this.routeService.pushConfig(JSON.toJSONString(newRoutes));
            if(log.isDebugEnabled()){
                log.info("取消注册实例成功");
            }
        }
    }
}
