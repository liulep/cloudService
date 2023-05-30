package com.yue.product.controller;

import com.yue.common.constant.constnt.SysConstant;
import com.yue.common.model.security.LoginUser;
import com.yue.common.utils.OauthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RedisTemplate<String,Object> redisTemplate;

    //用户退出
    @PostMapping("/logout")
    public String logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token){
        LoginUser currentUser = OauthUtils.getCurrentUser();
        redisTemplate.opsForValue().set(SysConstant.JTI_KEY_PREFIX+currentUser.getJti(),"",currentUser.getExp(), TimeUnit.SECONDS);
        return "退出成功";
    }
}
