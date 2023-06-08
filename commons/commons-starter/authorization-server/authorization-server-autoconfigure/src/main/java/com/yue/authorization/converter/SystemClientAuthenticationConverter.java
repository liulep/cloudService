package com.yue.authorization.converter;

import com.yue.authorization.core.constnt.OAuth2SystemParameterNames;
import com.yue.authorization.core.enums.AuthorizationGrantTypeEnum;
import com.yue.authorization.core.enums.ClientAuthenticationMethodEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import com.yue.authorization.core.utils.OAuth2ConfigureUtils;

import java.util.Map;

//自定义用户客户端认证
public class SystemClientAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grant_type = request.getParameter(OAuth2SystemParameterNames.GRANT_TYPE);
        if(!grant_type.equals(AuthorizationGrantTypeEnum.PHONE.getName())&&
                !grant_type.equals(AuthorizationGrantTypeEnum.EMAIL.getName())){
            return null;
        }
        String clientId= OAuth2SystemParameterNames.CLIENT_ID;
        String clientSecret=OAuth2SystemParameterNames.CLIENT_SECRET;
        Map<String, Object> additionalParameters = OAuth2ConfigureUtils.getParametersIfMatchesAuthorizationCodeGrantRequest(request);
        return new OAuth2ClientAuthenticationToken(clientId, ClientAuthenticationMethodEnum.SYSTEM_USER.getMethod(), clientSecret,additionalParameters);
    }
}
