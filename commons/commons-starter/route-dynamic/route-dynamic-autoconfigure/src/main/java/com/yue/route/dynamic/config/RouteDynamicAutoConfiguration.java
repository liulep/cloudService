package com.yue.route.dynamic.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.yue.common.constant.constnt.NacosConstant;
import com.yue.route.dynamic.properties.NacosDynamicProperties;
import com.yue.route.dynamic.service.DynamicRouteServiceImpl;
import com.yue.route.dynamic.service.NacosRouteDefinitionLocator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = NacosDynamicProperties.class)
public class RouteDynamicAutoConfiguration {

    private final NacosDynamicProperties nacosDynamicProperties;

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

    @Bean
    @ConditionalOnBean(value = {ConfigService.class, RouteDefinitionRepository.class})
    public NacosRouteDefinitionLocator nacosRouteDefinitionRepository(ConfigService configService,
                                                                      RouteDefinitionRepository repository,
                                                                      ApplicationEventPublisher applicationEventPublisher){
        DynamicRouteServiceImpl dynamicRouteService = new DynamicRouteServiceImpl(repository);
        dynamicRouteService.setApplicationEventPublisher(applicationEventPublisher);
        return new NacosRouteDefinitionLocator(configService,nacosDynamicProperties,dynamicRouteService);
    }
}
