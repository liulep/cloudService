package com.yue.authorization.core.enums;

import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

public enum ClientAuthenticationMethodEnum {

    SYSTEM_USER("system_user");

    private String name;
    private ClientAuthenticationMethod method;

    private ClientAuthenticationMethodEnum(String name){
        this.name = name;
        this.method = new ClientAuthenticationMethod(name);
    }

    public String getName(){
        return this.name;
    }

    public ClientAuthenticationMethod getMethod(){
        return this.method;
    }

}
