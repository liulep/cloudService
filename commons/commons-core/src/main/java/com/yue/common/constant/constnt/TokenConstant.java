package com.yue.common.constant.constnt;

public class TokenConstant {
        /**
         * JWT的秘钥
         * TODO 实际项目中需要统一配置到配置文件中，资源服务也需要用到
         */
        public final static String TOKEN_NAME="jwt-token";

        public final static String PRINCIPAL_NAME="principal";

        public static final String AUTHORITIES_NAME="authorities";

        public static final String SCOPE_NAME="scope";

        public static final String USER_ID="user_id";

        public static final String USER_NAME="username";
        public static final String NICK_NAME="nickname";

        public static final String JTI="jti";

        public static final String KID="kid";

        public static final String EXPR="exp";
}
