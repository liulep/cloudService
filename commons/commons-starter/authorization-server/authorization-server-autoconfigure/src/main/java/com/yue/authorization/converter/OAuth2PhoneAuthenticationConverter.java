package com.yue.authorization.converter;

import com.yue.authorization.core.constnt.OAuth2SystemParameterNames;
import com.yue.authorization.core.enums.AuthorizationGrantTypeEnum;
import com.yue.authorization.core.oauth2.OAuth2AuthorizationPhoneAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

//从 HttpServletRequest 中提取手机号登录亲求到AuthorizationPhoneAuthenticationToken的使用
public final class OAuth2PhoneAuthenticationConverter implements AuthenticationConverter {

    public OAuth2PhoneAuthenticationConverter() {
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2SystemParameterNames.GRANT_TYPE);
        if (!AuthorizationGrantTypeEnum.PHONE.getGrantType().getValue().equals(grantType)) {
            return null;
        }else{
            Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
            MultiValueMap<String, String> parameters = getParameters(request);
            String login = parameters.getFirst(OAuth2SystemParameterNames.LOGIN_NAME);
            if (!StringUtils.hasText(login) || parameters.get(OAuth2SystemParameterNames.LOGIN_NAME).size() != 1) {
                this.throwError("请添加username字段，这是必须的！", OAuth2SystemParameterNames.LOGIN_NAME, "字段缺少！");
            }
            String password = parameters.getFirst(OAuth2SystemParameterNames.PASSWORD);
            if (!StringUtils.hasText(password) || parameters.get(OAuth2SystemParameterNames.PASSWORD).size() != 1) {
                this.throwError("请添加password字段，这是必须的！", OAuth2SystemParameterNames.PASSWORD, "字段缺少!");
            }
            Map<String, Object> additionalParameters = new HashMap<>();
            parameters.forEach((key, value) -> {
                if (!key.equals(OAuth2SystemParameterNames.GRANT_TYPE)
                        && !key.equals(OAuth2SystemParameterNames.LOGIN_NAME)
                        && !key.equals(OAuth2SystemParameterNames.PASSWORD)) {
                    additionalParameters.put(key, value.get(0));
                }
            });
            return new OAuth2AuthorizationPhoneAuthenticationToken(clientPrincipal,additionalParameters,login,password);
        }
    }

    //从请求中获取参数
    static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
        parameterMap.forEach((key, values) -> {
            if (values.length > 0) {
                int var4 = values.length;
                for (String value : values) {
                    parameters.add(key, value);
                }
            }

        });
        return parameters;
    }

    void throwError(String errorCode,String parameterName,String url){
        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter 字段缺少 => " + parameterName,"");
        throw new OAuth2AuthenticationException(error);
    }
}
