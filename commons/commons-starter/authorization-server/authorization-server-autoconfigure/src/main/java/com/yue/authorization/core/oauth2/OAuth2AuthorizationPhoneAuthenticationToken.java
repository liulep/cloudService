package com.yue.authorization.core.oauth2;


import com.yue.authorization.core.enums.AuthorizationGrantTypeEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Map;

public class OAuth2AuthorizationPhoneAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {
    private final String username;
    private final String password;

    public OAuth2AuthorizationPhoneAuthenticationToken(Authentication clientPrincipal,
                                                       Map<String, Object> additionalParameters,
                                                       String username,
                                                       String password) {
        super(AuthorizationGrantTypeEnum.PHONE.getGrantType(), clientPrincipal, additionalParameters);
        Assert.hasText(username, "username cannot be empty");
        this.username=username;
        this.password=password;
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }
}
