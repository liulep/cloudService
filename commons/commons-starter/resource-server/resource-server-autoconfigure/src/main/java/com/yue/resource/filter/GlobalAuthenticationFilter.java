package com.yue.resource.filter;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yue.common.constant.constnt.SysConstant;
import com.yue.common.constant.constnt.TokenConstant;
import com.yue.common.constant.enums.ResultCodeEnum;
import com.yue.common.model.entity.R;
import com.yue.resource.properties.WhitelistProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//网关全局过滤器
@Slf4j
public class GlobalAuthenticationFilter implements GlobalFilter, Ordered {

    private final WhitelistProperties whitelistProperties;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtDecoder jwtDecoder;

    public GlobalAuthenticationFilter(WhitelistProperties whitelistProperties,
                                      RedisTemplate<String, Object> redisTemplate,
                                      JwtDecoder jwtDecoder) {
        this.whitelistProperties = whitelistProperties;
        this.redisTemplate = redisTemplate;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String reqUrl = exchange.getRequest().getPath().value();
        //检查白名单
        List<String> urls = Arrays.asList(whitelistProperties.getUrls());
        if (checkUrl(urls, reqUrl)) {
            return chain.filter(exchange);
        }
        //检查Token是否存在
        String token = getToken(exchange);
        if (StringUtils.isBlank(token)) {
            return invalidTokenMono(exchange);
        }
        //检验JWT
        Jwt jwt=validToken(token);
        if(jwt == null){
            return invalidTokenMono(exchange);
        }
        Map<String, Object> claims = jwt.getClaims();
        //获取Jwt唯一ID
        String jti = jwt.getHeaders().get(TokenConstant.KID).toString();
        //查看jti是否存在黑名单中
        Boolean aBoolean = redisTemplate.hasKey(SysConstant.JTI_KEY_PREFIX + jti);
        if (Boolean.TRUE.equals(aBoolean)) {
            return invalidTokenMono(exchange);
        }
        try {
            JSONObject json = new JSONObject();
            //用户id
            json.put(TokenConstant.USER_ID, claims.get(TokenConstant.USER_ID).toString());
            //用户名称
            json.put(TokenConstant.USER_NAME, claims.get(TokenConstant.USER_NAME).toString());
            //用户权限
            json.put(TokenConstant.AUTHORITIES_NAME, claims.get(TokenConstant.AUTHORITIES_NAME));
            //用户昵称
            json.put(TokenConstant.NICK_NAME, claims.get(TokenConstant.NICK_NAME));
            json.put(JwtClaimNames.JTI, jti);
            //过期时间
            Instant exp = jwt.getClaimAsInstant(JwtClaimNames.EXP);
            json.put(JwtClaimNames.EXP,  ChronoUnit.SECONDS.between(Instant.now(), exp));
            String base64 = Base64.encode(json.toJSONString());
            //放入请求头
            ServerHttpRequest request = exchange.getRequest().mutate().header(TokenConstant.TOKEN_NAME, base64).build();
            ServerWebExchange webExchange = exchange.mutate().request(request).build();
            return chain.filter(webExchange);
        } catch (Exception e) {
            return invalidTokenMono(exchange);
        }
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    //检查该url是否存在白名单中
    private boolean checkUrl(List<String> urls, String reqUrl) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String url : urls) {
            if (antPathMatcher.match(url, reqUrl)) {
                return true;
            }
        }
        return false;
    }

    //检查token是否存在
    private String getToken(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(token)) {
            return null;
        }
        token = token.split(" ")[1];
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return token;
    }

    //检验token是否有效
    private Jwt validToken(String token) {
        return this.jwtDecoder.decode(token);
    }

    //无效的token
    private Mono<Void> invalidTokenMono(ServerWebExchange exchange) {
        return buildMono(R.error()
                .code(ResultCodeEnum.INVALID_TOKEN.getCode())
                .message(ResultCodeEnum.INVALID_TOKEN.getMessage())
                .success(ResultCodeEnum.INVALID_TOKEN.getSuccess()), exchange);
    }

    private Mono<Void> buildMono(R r, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        byte[] bytes = JSON.toJSONString(r).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(buffer));
    }

}
