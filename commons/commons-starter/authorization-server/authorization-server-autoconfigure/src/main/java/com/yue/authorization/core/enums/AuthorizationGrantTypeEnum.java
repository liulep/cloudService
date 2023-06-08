package com.yue.authorization.core.enums;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

public enum AuthorizationGrantTypeEnum{

    PHONE("phone"),
    EMAIL("email");

    private final String name;
    private final AuthorizationGrantType grantType;

    private AuthorizationGrantTypeEnum(String name) {
        this.name=name;
        this.grantType=new AuthorizationGrantType(name);
    }

    public AuthorizationGrantType getGrantType() {
        return this.grantType;
    }
    public String getName(){
        return this.name;
    }
}
