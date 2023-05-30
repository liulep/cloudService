package com.yue.resource.config;

import com.yue.common.constant.constnt.TokenConstant;
import com.yue.redis.config.RedisAutoConfiguration;
import com.yue.resource.filter.CorsFilter;
import com.yue.resource.filter.GlobalAuthenticationFilter;
import com.yue.resource.handler.GlobalErrorExceptionHandler;
import com.yue.resource.handler.RequestAccessDeniedHandler;
import com.yue.resource.handler.RequestAuthenticationEntryPoint;
import com.yue.resource.manager.JwtAuthenticationManager;
import com.yue.resource.manager.JwtAuthorizationManager;
import com.yue.resource.properties.WhitelistProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(value = WhitelistProperties.class)
@AutoConfigureAfter(value = RedisAutoConfiguration.class)
public class ResourceServerAutoConfiguration {

    private final String issuerUri="http://127.0.0.1:8081/issuer/yueue";
    private final String jwkSetUri="http://127.0.0.1:8081/oauth2/jwks";
    private final WhitelistProperties whitelistProperties;

    @Bean
    @ConditionalOnBean(value = RedisTemplate.class)
    public SecurityWebFilterChain ResourceServerSecurityFilterChain(ServerHttpSecurity http,
                                                                    RedisTemplate<String,Object> redisTemplate){
        http.authorizeExchange(exchanges ->
                exchanges
                        .pathMatchers(whitelistProperties.getUrls())
                        .permitAll()
                        .anyExchange()
                        .access(getAuthorizationManager(redisTemplate)))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtDecoder(jwtDecoder())
                                   .jwtAuthenticationConverter(jwtAuthenticationConverter())
                                   .authenticationManager(getAuthenticationManager())
                                )
                                .bearerTokenConverter(bearerTokenResolver())
                                .accessDeniedHandler(accessDeniedHandler())
                                .authenticationEntryPoint(authenticationEntryPoint())
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(authenticationEntryPoint())
                                .accessDeniedHandler(accessDeniedHandler())
                ).addFilterAt(corsFilter(), SecurityWebFiltersOrder.CORS);
        return http.build();
    }

    //配置全局过滤器
    @Bean
    @Order(-1)
    @ConditionalOnBean(value = RedisTemplate.class)
    public GlobalFilter globalAuthenticationFilter(RedisTemplate<String,Object> redisTemplate){
        return new GlobalAuthenticationFilter(whitelistProperties,redisTemplate,serverJwtDecoder());
    }

    //配置全局CORS
    @Bean
    @Order(-1)
    public WebFilter corsFilter(){
        return new CorsFilter();
    }

    //配置全局异常捕获
    @Bean
    @Order(-1)
    public ErrorWebExceptionHandler globalErrorExceptionHandler(){
        return new GlobalErrorExceptionHandler();
    }

    //提取权限,将Jwt转成Authorization实例
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(TokenConstant.AUTHORITIES_NAME);
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    //配置Jwt解密
    public ReactiveJwtDecoder jwtDecoder(){
        NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
        DelegatingOAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(Duration.ofSeconds(120)),
                new JwtIssuerValidator(this.issuerUri));
        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }

    //配置解密
    public JwtDecoder serverJwtDecoder(){
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
        DelegatingOAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(Duration.ofSeconds(120)),
                new JwtIssuerValidator(this.issuerUri));
        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }

    //获取请求头中的token
    private ServerBearerTokenAuthenticationConverter bearerTokenResolver(){
        ServerBearerTokenAuthenticationConverter bearerToken = new ServerBearerTokenAuthenticationConverter();
        bearerToken.setBearerTokenHeaderName(HttpHeaders.AUTHORIZATION);
        bearerToken.setAllowUriQueryParameter(false);
        return bearerToken;
    }

    //令牌无效或时间过期
    private RequestAuthenticationEntryPoint authenticationEntryPoint(){
        return new RequestAuthenticationEntryPoint();
    }

    //无权限
    private RequestAccessDeniedHandler accessDeniedHandler(){
        return new RequestAccessDeniedHandler();
    }

    //认证: 解析、验签、过期时间的判定
    private ReactiveAuthenticationManager getAuthenticationManager() {
        ReactiveJwtDecoder jwtDecoder = jwtDecoder();
        Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter = jwtAuthenticationConverter();
        JwtAuthenticationManager authenticationManager=new JwtAuthenticationManager(jwtDecoder);
        authenticationManager.setJwtAuthenticationConverter(jwtAuthenticationConverter);
        return authenticationManager;
    }

    //鉴权: 是否拥有该权限
    private ReactiveAuthorizationManager<AuthorizationContext> getAuthorizationManager(RedisTemplate<String,Object> redisTemplate){
        return new JwtAuthorizationManager(redisTemplate);
    }
}
