package com.yue.common.utils;

import com.yue.common.constant.constnt.RequestConstant;
import com.yue.common.model.security.LoginUser;

//从请求中获取用户信息
public class OauthUtils {

    public static LoginUser getCurrentUser(){
        return (LoginUser) RequestContextUtils.getRequest().getAttribute(RequestConstant.LOGIN_VAL_ATTRIBUTE);
    }
}
