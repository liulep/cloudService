package com.yue.authorization.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.yue.authorization.handler.OAuth2AuthenticationFailureHandler;
import com.yue.authorization.handler.OAuth2AuthenticationSuccessHandler;
import com.yue.authorization.service.UserInfo;
import com.yue.authorization.service.UserInfoService;
import com.yue.common.constant.constnt.TokenConstant;
import com.yue.common.model.security.SecurityUser;
import com.yue.common.model.security.SecurityUserMixin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Import(value = {DefaultSecurityConfig.class})
@ConditionalOnBean(value = UserDetailsService.class)
public class AuthorizationServerConfig {

    private final PasswordEncoder passwordEncoder;

    // 用于协议端点的过滤器链
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServiceSecurityChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .tokenEndpoint(token ->
                        token.accessTokenResponseHandler(getAuthenticationSuccessHandler())
                                .errorResponseHandler(getAuthenticationFailureHandler()))
                .oidc(Customizer.withDefaults());
        http.exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate){
        RegisteredClient registeredClient = RegisteredClient.withId("test")
                // 客户端id 需要唯一
                .clientId("test")
                // 客户端密码
                .clientSecret(passwordEncoder.encode("123123"))
                //客户端名字
                .clientName("test")
                // 可以基于 basic 的方式和授权服务器进行认证
                .clientAuthenticationMethods(client ->{
                    client.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                    //client.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                    //client.add(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
                })
                // 授权码
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                // 刷新token
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                // 客户端模式
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                // 密码模式 已过时
//                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                // 简化模式，已过时，不推荐
//                .authorizationGrantType(AuthorizationGrantType.IMPLICIT)
                // 重定向url
                .redirectUri("http://127.0.0.1:9999")
                .redirectUri("http://127.0.0.1:8082/login/oauth2/code/admin")
                // 客户端申请的作用域，也可以理解这个客户端申请访问用户的哪些信息，比如：获取用户信息，获取用户照片等
                .scope("read")
                .scope("write")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .scope(OidcScopes.ADDRESS)
                .scope(OidcScopes.PHONE)
                .clientSettings(ClientSettings.builder()
                        // 是否需要用户确认一下客户端需要获取用户的哪些权限
                        //比如：客户端需要获取用户的 用户信息、用户照片 但是此处用户可以控制只给客户端授权获取 用户信息。
                        .requireAuthorizationConsent(true)
                        .build()
                )
                .tokenSettings(TokenSettings.builder()
                        // accessToken 的有效期
                        .accessTokenTimeToLive(Duration.ofHours(12))
                        // refreshToken 的有效期
                        .refreshTokenTimeToLive(Duration.ofDays(30))
                        // 是否可重用刷新令牌
                        .reuseRefreshTokens(true)
                        .build()
                )
                .build();
        //http://127.0.0.1:8080/auth/oauth2/authorize?response_type=code&client_id=test&scope=profile%20email%20openid%20email%20address&redirect_uri=http://127.0.0.1:9999
        JdbcRegisteredClientRepository jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        if(jdbcRegisteredClientRepository.findByClientId("test")==null){
            jdbcRegisteredClientRepository.save(registeredClient);
        }
        return jdbcRegisteredClientRepository;
    }

    //生成JWK
    @Bean
    public JWKSource<SecurityContext> jwkSource(){
        KeyPair keyPair= generateRsaKey();
        //生成公钥
        RSAPublicKey aPublic = (RSAPublicKey) keyPair.getPublic();
        //生成私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rasKey = new RSAKey
                .Builder(aPublic)
                .privateKey(aPrivate)
                .keyID(UUID.randomUUID()
                        .toString())
                .build();
        JWKSet jwkSet = new JWKSet(rasKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    //jwt解密
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource){
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    //设置端点的路径，比如获取token,授权端点等
    @Bean
    public AuthorizationServerSettings authorizationServerSettings(){
        return AuthorizationServerSettings.builder()
                .issuer("http://127.0.0.1:8081/issuer/yueue")
                .authorizationEndpoint("/auth/oauth2/authorize")
                .tokenEndpoint("/auth/oauth2/token")
                .build();
    }

    //配置存储新授权和查询现有授权的组件
    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository){
        JdbcOAuth2AuthorizationService authorizationService = new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
        class CustomOAuth2AuthorizationRowMapper extends JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper {
            public CustomOAuth2AuthorizationRowMapper(RegisteredClientRepository registeredClientRepository) {
                super(registeredClientRepository);
                this.getObjectMapper().addMixIn(SecurityUser.class, SecurityUserMixin.class);
            }
        }
        CustomOAuth2AuthorizationRowMapper oAuth2AuthorizationRowMapper =
                new CustomOAuth2AuthorizationRowMapper(registeredClientRepository);
        authorizationService.setAuthorizationRowMapper(oAuth2AuthorizationRowMapper);
        return authorizationService;
    }

    //存储新授权同意和查询现有授权同意的组件
    @Bean
    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository){
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate,registeredClientRepository);
    }



    // tokenClaims 增强
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> oAuth2TokenCustomizer(JdbcTemplate jdbcTemplate){
        return context -> {
            if(OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())){
                UserInfo userInfo = new UserInfoService(jdbcTemplate).loadUser(
                        context.getPrincipal().getName());
                context.getClaims().claims(claims ->
                        claims.putAll(userInfo.getClaims()));
                context.getClaims().claim(TokenConstant.AUTHORITIES_NAME,context
                        .getPrincipal()
                        .getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));
            }
           context.getJwsHeader().header("client-id",context.getRegisteredClient().getClientId());
        };
    }

    //登录成功
    public AuthenticationSuccessHandler getAuthenticationSuccessHandler(){
        return new OAuth2AuthenticationSuccessHandler();
    }

    //登录失败
    public AuthenticationFailureHandler getAuthenticationFailureHandler(){
        return new OAuth2AuthenticationFailureHandler();
    }

    private KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
            rsa.initialize(2048);
            return rsa.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
