package com.yue.product.service.impl;

import com.yue.product.mapper.UserMapper;
import com.yue.common.constant.constnt.SysConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//认证服务初始化时，将权限进行初始化
@Service
@Slf4j
@RequiredArgsConstructor
public class LoadRolePermissionService implements ApplicationRunner {

    private final UserMapper userMapper;
    private final RedisTemplate<String,Object> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.init();
    }
    public void init(){
        List<String> userMenusAndApi = userMapper.findUserMenusAndApi();
        userMenusAndApi.forEach(url ->{
            List<String> roles = userMapper.findUserRolesByUrl(url);
            List<String> data = new ArrayList<>();
            for (String role : roles) {
                data.add(SysConstant.ROLE_PREFIX + role);
            }
            redisTemplate.opsForHash().put(SysConstant.OAUTH2_URIS, url, data);
        });
        log.info("权限加载完成 -> {}条",userMenusAndApi.size());
    }
}
