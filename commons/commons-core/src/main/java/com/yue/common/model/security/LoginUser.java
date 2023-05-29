package com.yue.common.model.security;

import lombok.Data;

import java.util.List;

@Data
public class LoginUser {

    private Integer userId;

    private String username;

    private String nickname;

    private List<String> authorities;

    private String jti;

    private Integer exp;
}
