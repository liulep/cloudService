package com.yue.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

//获取HttpServletRequest工具类
public class RequestContextUtils {
    /**
     * 获取HttpServletRequest
     */
    public static HttpServletRequest getRequest(){
        return ((ServletRequestAttributes)(Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))).getRequest();
    }
}
