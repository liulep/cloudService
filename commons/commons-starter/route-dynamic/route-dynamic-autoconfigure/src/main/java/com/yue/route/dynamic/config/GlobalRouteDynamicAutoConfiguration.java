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
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
@AutoConfiguration(
        after = GatewayAutoConfiguration.class
)
@EnableConfigurationProperties(value = NacosDynamicProperties.class)
public class GlobalRouteDynamicAutoConfiguration {

    private final NacosDynamicProperties nacosDynamicProperties;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Bean
    @ConditionalOnMissingBean
    public ConfigService configService() throws NacosException {
        Properties properties = new Properties();
        properties.put(NacosConstant.SERVER_ADDR,nacosDynamicProperties.getServerAddr());
        properties.put(NacosConstant.NAMESPACE,nacosDynamicProperties.getNamespace());
        properties.put(NacosConstant.USERNAME,nacosDynamicProperties.getUsername());
        properties.put(NacosConstant.PASSWORD,nacosDynamicProperties.getPassword());
        log.info("configService start...");
        return NacosFactory.createConfigService(properties);
    }

    //操作动态路由
    //redis存在时使用redis做缓存
    //redis不存在时使用内存做缓存
    @Bean
    @ConditionalOnBean(value = {RouteDefinitionRepository.class, ConfigService.class})
    public DynamicRouteServiceImpl dynamicRouteService(ConfigService configService,
                                                            RouteDefinitionRepository repository){
        DynamicRouteServiceImpl dynamicRouteService = new DynamicRouteServiceImpl(repository);
        dynamicRouteService.setApplicationEventPublisher(this.applicationEventPublisher);
        return dynamicRouteService;
    }

    //从nacos中获取路由配置，并将路由进行缓存
    @Bean
    @ConditionalOnBean(value = {ConfigService.class})
    public NacosRouteDefinitionLocator nacosRouteDefinitionRepository(ConfigService configService){
        return new NacosRouteDefinitionLocator(configService,nacosDynamicProperties);
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
    @ConditionalOnBean(value = {RouteLocator.class})
    public NacosInstancesChangeEventListener nacosInstancesChangeEventListener(CachingRouteLocator cachingRouteLocator,
                                                                               ConfigService configService,
                                                                               RouteDefinitionRepository repository){
        //监听nacos实例时监听nacos配置文件
        this.applicationEventPublisher.publishEvent(new NacosRoutesEvent(this,
                this.nacosDynamicProperties.getDataId(),
                this.nacosDynamicProperties.getGroup()));
        log.info("开始监听nacos所有实例...");
        return new NacosInstancesChangeEventListener(repository,cachingRouteLocator,configService,nacosDynamicProperties);
    }
}
