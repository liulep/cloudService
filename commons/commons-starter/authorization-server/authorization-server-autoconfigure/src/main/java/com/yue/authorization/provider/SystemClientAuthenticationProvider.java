package com.yue.authorization.provider;

import com.yue.authorization.core.enums.AuthorizationGrantTypeEnum;
import com.yue.authorization.core.enums.ClientAuthenticationMethodEnum;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.authentication.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.util.Assert;

import java.security.Principal;
import java.time.Instant;

//自定义的用户登录主处理器
public class SystemClientAuthenticationProvider implements AuthenticationProvider {

    private final RegisteredClientRepository registeredClientRepository;
    private PasswordEncoder passwordEncoder;

    public SystemClientAuthenticationProvider(RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2ClientAuthenticationToken clientAuthentication =
                (OAuth2ClientAuthenticationToken) authentication;
        ClientAuthenticationMethod clientAuthenticationMethod = clientAuthentication.getClientAuthenticationMethod();
        if(!ClientAuthenticationMethodEnum.SYSTEM_USER.getMethod().equals(clientAuthenticationMethod)){
            return null;
        }
        //获取当前登录用户
        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        //获取客户端
        String clientId = clientAuthentication.getPrincipal().toString();
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null) {
            throw new OAuth2AuthenticationException("客户端不存在");
        }
        return new OAuth2ClientAuthenticationToken(registeredClient,
                clientAuthentication.getClientAuthenticationMethod(), clientAuthentication.getCredentials());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
    }

    private static OAuth2Authorization.Builder authorizationBuilder(RegisteredClient registeredClient,
                                                                    OAuth2AuthorizationRequest authorizationRequest,
                                                                    Authentication authentication) {
        return OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(authentication.getName())
                .authorizationGrantType(AuthorizationGrantTypeEnum.PHONE.getGrantType())
                .attribute(OAuth2AuthorizationRequest.class.getName(), authorizationRequest)
                .attribute(Principal.class.getName(),authentication);
    }

    public OAuth2AuthorizationCode generate(OAuth2TokenContext context) {
        if (context.getTokenType() == null ||
                !OAuth2ParameterNames.CODE.equals(context.getTokenType().getValue())) {
            return null;
        }
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(context.getRegisteredClient().getTokenSettings().getAuthorizationCodeTimeToLive());
        return new OAuth2AuthorizationCode(context.get(OAuth2ParameterNames.CODE), issuedAt, expiresAt);
    }

    //token上下文
    private static OAuth2TokenContext createAuthorizationCodeTokenContext(
            Authentication userAuthentication,
            OAuth2ClientAuthenticationToken clientAuthenticationToken,
            RegisteredClient registeredClient) {
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(userAuthentication)
                .put(OAuth2ParameterNames.CODE,userAuthentication.getName())
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .tokenType(new OAuth2TokenType(OAuth2ParameterNames.CODE))
                .authorizedScopes(registeredClient.getScopes())
                .authorizationGrantType(AuthorizationGrantTypeEnum.PHONE.getGrantType())
                .authorizationGrant(clientAuthenticationToken);
        return tokenContextBuilder.build();
    }


}
