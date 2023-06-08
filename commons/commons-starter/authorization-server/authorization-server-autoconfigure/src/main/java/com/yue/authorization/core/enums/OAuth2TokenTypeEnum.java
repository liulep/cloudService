package com.yue.authorization.core.enums;

import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

public enum OAuth2TokenTypeEnum {

    SYSTEM_USER("system_user");

    private String name;
    private OAuth2TokenType type;
    private OAuth2TokenTypeEnum(String name){
        this.name=name;
        this.type=new OAuth2TokenType(name);
    }

    public String getName(){
        return this.name;
    }

    public OAuth2TokenType getType(){
        return this.type;
    }
}
