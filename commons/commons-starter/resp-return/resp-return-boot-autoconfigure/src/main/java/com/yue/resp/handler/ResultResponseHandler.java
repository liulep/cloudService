package com.yue.resp.handler;

import com.alibaba.fastjson2.JSON;
import com.yue.common.model.entity.Error;
import com.yue.common.model.entity.R;
import com.yue.resp.anno.NotControllerResponseAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

// 定义全局返回结果包装
@ControllerAdvice(basePackages = "com.yue")
@ConditionalOnClass(value = ResponseBodyAdvice.class)
public class ResultResponseHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.hasMethodAnnotation(NotControllerResponseAdvice.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(body instanceof String s){
            return JSON.toJSONString(R.ok().message(s));
        }else if(body instanceof Error error){
            return R.error().message(error.getMessage()).code(error.getCode());
        }else if(body instanceof R){
            return body;
        }
        return R.ok(body);
    }
}
