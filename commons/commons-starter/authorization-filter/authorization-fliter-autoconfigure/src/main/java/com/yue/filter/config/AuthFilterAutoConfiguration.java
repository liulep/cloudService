package com.yue.filter.config;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yue.common.constant.constnt.RequestConstant;
import com.yue.common.constant.constnt.TokenConstant;
import com.yue.common.model.security.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
@Order(Integer.MIN_VALUE)
public class AuthFilterAutoConfiguration extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取请求头中加密的用户信息
        String token = request.getHeader(TokenConstant.TOKEN_NAME);
        if(StringUtils.isNotBlank(token)){
            //解密
            String json = Base64.decodeStr(token);
            JSONObject jsonObject = JSON.parseObject(json);
            //获取用户信息，权限信息
            String jti = jsonObject.getString(TokenConstant.JTI);
            Integer exp = jsonObject.getInteger(TokenConstant.EXPR);
            Integer userId = jsonObject.getInteger(TokenConstant.USER_ID);
            String username = jsonObject.getString(TokenConstant.USER_NAME);
            String nickname = jsonObject.getString(TokenConstant.NICK_NAME);
            List<String> authorities = jsonObject.getList(TokenConstant.AUTHORITIES_NAME, String.class);
            LoginUser loginUser = new LoginUser();
            loginUser.setUserId(userId);
            loginUser.setUsername(username);
            loginUser.setNickname(nickname);
            loginUser.setJti(jti);
            loginUser.setExp(exp);
            loginUser.setAuthorities(authorities);
            request.setAttribute(RequestConstant.LOGIN_VAL_ATTRIBUTE,loginUser);
        }
        filterChain.doFilter(request,response);
    }
}
