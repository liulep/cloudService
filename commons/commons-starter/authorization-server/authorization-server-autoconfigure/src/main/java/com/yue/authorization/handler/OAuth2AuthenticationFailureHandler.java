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
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

//认证失败
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
        String message ="";
        if(error!=null){
            message = error.getErrorCode();
        }
        else if(StringUtils.hasText(exception.getMessage())){
            message = exception.getMessage();
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(R.setResult(ResultCodeEnum.ERROR)
                .message(message)));
        writer.close();
    }
}
