package com.yue.resource.manager;

import com.yue.common.constant.constnt.SysConstant;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//鉴权: 是否拥有该权限
public class JwtAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final RedisTemplate<String, Object> redisTemplate;

    private final AntPathMatcher matcher=new AntPathMatcher();

    public JwtAuthorizationManager(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        //请求路径
        URI uri = context.getExchange().getRequest().getURI();
        //请求方法
        String method = context.getExchange().getRequest().getMethod().name();
        //拼接
        String truePath = method + SysConstant.METHOD_PREFIX + uri.getPath();
        // 获取redis中的权限列表
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(SysConstant.OAUTH2_URIS);
        List<String> authorities= new ArrayList<>(){{
            entries.forEach((path, roles) -> {
                if(matcher.match((String) path,truePath)){
                    addAll(castList(roles,String.class));
                }
            });
        }};
        return authentication.filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(roles -> {
                    if(roles.equals(SysConstant.SUPER_ADMIN)){
                        return true;
                    }
                    return !authorities.isEmpty() && authorities.contains(roles);
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    //将Object转换成List
    public <T> List<T> castList(Object obj,Class<T> clazz){
        return new ArrayList<>(){{
            if(obj instanceof ArrayList<?> res){
                res.forEach(item ->{
                    add(clazz.cast(item));
                });
            }
        }};
    }
}
