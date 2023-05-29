package com.yue.route.register.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.yue.common.constant.constnt.NacosConstant;
import com.yue.route.register.entity.PredicateDefinition;
import com.yue.route.register.entity.RouteDefinition;
import com.yue.route.register.properties.NacosProperties;
import com.yue.route.register.properties.RouteProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.net.URI;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {NacosProperties.class,RouteProperties.class})
public class RouteRegisterBeanPostProcess implements ApplicationRunner {

    private final NacosProperties nacosProperties;
    private final RouteProperties routeProperties;
    private ConfigService configService;
    private List<RouteDefinition> routes = Collections.emptyList();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.initConfigService();
        this.getRoutes();
        this.init();
    }

    //初始化
    public void init(){
        RouteDefinition exist = isExist(routeProperties.getServerName());
        if(ObjectUtils.isEmpty(exist)){
            RouteDefinition routeDefinition=new RouteDefinition();
            createRoute(routeProperties.getServerName(),routeDefinition);
            pushConfig(nacosProperties.getDataId(), nacosProperties.getGroup(), JSON.toJSONString(this.routes));
        } else if (ObjectUtils.isNotEmpty(exist)){
            this.routes.remove(exist);
            createRoute(exist.getId(),exist);
            pushConfig(nacosProperties.getDataId(), nacosProperties.getGroup(), JSON.toJSONString(this.routes));
        }
    }

    //创建路由
    public void createRoute(String routeName,RouteDefinition routeDefinition){
        routeDefinition.setId(routeName);
        routeDefinition.setUri(URI.create(getUri()));
        routeDefinition.setOrder(1);
        routeDefinition.setPredicates(new ArrayList<PredicateDefinition>(){{
            PredicateDefinition predicateDefinition = new PredicateDefinition();
            predicateDefinition.setName("Path");
            predicateDefinition.setArgs(new HashMap<String,String>(){{
                put("pattern","/"+getRouteId()+"/**");
            }});
            add(predicateDefinition);
        }});
        this.routes.add(routeDefinition);
    }

    // 初始化nacosConfigService
    public void initConfigService(){
        try {
            Properties properties = new Properties();
            properties.put(NacosConstant.SERVER_ADDR,nacosProperties.getServerAddr());
            properties.put(NacosConstant.NAMESPACE,nacosProperties.getNamespace());
            properties.put(NacosConstant.USERNAME,nacosProperties.getUsername());
            properties.put(NacosConstant.PASSWORD,nacosProperties.getPassword());
            configService=NacosFactory.createConfigService(properties);
        }catch (NacosException e){
            log.info("获取nacos配置失败");
            throw new RuntimeException("init nacos config error");
        }
    }

    // 发布配置
    public void pushConfig(String dataId, String group, String content){
        try {
            configService.publishConfig(dataId,group,content, ConfigType.JSON.getType());
        }catch (NacosException e){
            log.info("Nacos配置发布失败！");
        }
    }

    //判断是否路由已经存在
    public RouteDefinition isExist(String serviceName){
        if(StringUtils.isBlank(serviceName)){
            throw new RuntimeException("register route not have serviceName");
        }
        for(RouteDefinition route: this.routes){
            if(route.getId().equals(serviceName)){
                return route;
            }
        }
        return null;
    }

    // 获取所有路由
    public void getRoutes(){
        try {
            String config = configService.getConfig(nacosProperties.getDataId(), nacosProperties.getGroup(), NacosProperties.DEFAULT_TIME_OUT);
            this.routes= JSON.parseArray(config, RouteDefinition.class);
        } catch (NacosException e) {
            log.info("获取配置失败");
            throw new RuntimeException(e);
        }
    }

    //进行拼接服务路由Id
    public String getRouteId(){
        String routeId=routeProperties.getServerName();
        int len = routeId.length();
        if(StringUtils.isNotBlank(routeProperties.getRoute())){
            return routeProperties.getRoute();
        }
        Integer exclude = routeProperties.getExclude();
        if(exclude!=0){
            if(Math.abs(exclude) > len){
                throw new RuntimeException("this exclude length is too long");
            }
            if(exclude < 0){
                routeId = routeId.substring(len-1 + exclude,len);
            }else{
                routeId = routeId.substring(0,len - exclude);
            }
        }
        if(StringUtils.isNotBlank(routeProperties.getSuffix())){
            routeId+=routeProperties.getSuffix();
        }
        return routeId;
    }

    //进行路由服务的拼接
    public String getUri(){
        String serviceRemote =routeProperties.getServerName();
        return routeProperties.getPrefix()+serviceRemote;
    }
}
