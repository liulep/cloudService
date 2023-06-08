package com.yue.authorization.service;

import org.springframework.security.core.userdetails.UserDetails;

//自定义手机登录接口
public interface SmsCodeUserDetailService {

    public UserDetails loadUserByPhone(String phone);
}
