package com.yue.common.constant.constnt;

import lombok.Data;

@Data
public class SysConstant {

    /**
     * 请求方法前缀
     */
    public final static String METHOD_PREFIX=":";

    /**
     * oauth2请求路径权限
     */
    public final static String OAUTH2_URIS="oauth2:oauth_urls:";

    public final static String SUPER_ADMIN="ROLE_SUPER_ADMIN";

    public final static String JTI_KEY_PREFIX="oauth2:black:";

    public final static String ROLE_PREFIX="ROLE_";

}
