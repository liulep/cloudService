package com.yue.route.dynamic.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.yue.common.constant.constnt.NacosConstant;
import com.yue.route.dynamic.event.NacosRoutesEvent;
import com.yue.route.dynamic.listener.NacosInstancesChangeEventListener;
import com.yue.route.dynamic.listener.NacosRouteConfigServiceListener;
import com.yue.route.dynamic.properties.NacosDynamicProperties;
import com.yue.route.dynamic.service.DynamicRouteServiceImpl;
import com.yue.route.dynamic.service.NacosRouteDefinitionLocator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = NacosDynamicProperties.class)
public class RouteDynamicAutoConfiguration {

    private final NacosDynamicProperties nacosDynamicProperties;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Bean
    public ConfigService configService() throws NacosException {
        Properties properties = new Properties();
        properties.put(NacosConstant.SERVER_ADDR,nacosDynamicProperties.getServerAddr());
        properties.put(NacosConstant.NAMESPACE,nacosDynamicProperties.getNamespace());
        properties.put(NacosConstant.USERNAME,nacosDynamicProperties.getUsername());
        properties.put(NacosConstant.PASSWORD,nacosDynamicProperties.getPassword());
        log.info("configService start...");
        return NacosFactory.createConfigService(properties);
    }

    //从nacos中获取路由配置，并将路由进行缓存
    @Bean
    @ConditionalOnBean(value = {ConfigService.class})
    public NacosRouteDefinitionLocator nacosRouteDefinitionRepository(ConfigService configService){
        return new NacosRouteDefinitionLocator(configService,nacosDynamicProperties);
    }

    //动态路由(增删改)
    @Bean
    @ConditionalOnBean(value = RouteDefinitionRepository.class)
    public DynamicRouteServiceImpl dynamicRouteService(ConfigService configService,
                                                       RouteDefinitionRepository repository){
        DynamicRouteServiceImpl dynamicRouteService = new DynamicRouteServiceImpl(repository);
        dynamicRouteService.setApplicationEventPublisher(this.applicationEventPublisher);
        return dynamicRouteService;
    }

    //监听nacos路由配置是否发生改变
    @Bean
    @ConditionalOnBean(value = DynamicRouteServiceImpl.class)
    public NacosRouteConfigServiceListener nacosRouteConfigServiceListener(ConfigService configService,
                                                                           DynamicRouteServiceImpl dynamicRouteService){
        NacosRouteConfigServiceListener nacosRouteConfigServiceListener = new NacosRouteConfigServiceListener(configService, dynamicRouteService);
        return new NacosRouteConfigServiceListener(configService,dynamicRouteService);
    }

    //监听nacos实例状态
    @Bean
    @ConditionalOnBean(value = {RouteLocator.class,RouteDefinitionLocator.class})
    public NacosInstancesChangeEventListener nacosInstancesChangeEventListener(RouteDefinitionRepository repository,
                                                                               CachingRouteLocator cachingRouteLocator,
                                                                               ConfigService configService,
                                                                               DynamicRouteServiceImpl routeService){
        //监听nacos实例时监听nacos配置文件
        this.applicationEventPublisher.publishEvent(new NacosRoutesEvent(this,
                this.nacosDynamicProperties.getDataId(),
                this.nacosDynamicProperties.getGroup()));
        log.info("开始监听nacos所有实例...");
        List<RouteDefinition> routeDefinitions = repository.getRouteDefinitions().buffer().blockFirst();
        return new NacosInstancesChangeEventListener(routeService,cachingRouteLocator,configService,nacosDynamicProperties);
    }
}
