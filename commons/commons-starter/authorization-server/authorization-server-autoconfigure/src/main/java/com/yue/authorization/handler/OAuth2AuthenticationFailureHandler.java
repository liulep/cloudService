package com.yue.authorization.handler;

import com.alibaba.fastjson2.JSON;
import com.yue.common.constant.enums.ResultCodeEnum;
import com.yue.common.model.entity.R;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

//认证失败
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(R.setResult(ResultCodeEnum.ERROR)));
        writer.close();
    }
}
