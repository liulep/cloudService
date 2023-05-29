package com.yue.authorization.service;
import org.springframework.security.oauth2.core.oidc.StandardClaimAccessor;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UserInfo implements StandardClaimAccessor, Serializable {

    private static final long serialVersionUID = 2L;

    private final Map<String, Object> claims;

    public UserInfo(Map<String, Object> claims) {
        Assert.notEmpty(claims, "claims cannot be empty");
        this.claims = Collections.unmodifiableMap(new LinkedHashMap(claims));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            UserInfo that = (UserInfo)obj;
            return this.getClaims().equals(that.getClaims());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.getClaims().hashCode();
    }

    @Override
    public Map<String, Object> getClaims() {
        return this.claims;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static final class Builder {
        private final Map<String, Object> claims = new LinkedHashMap();

        private Builder() {
        }

        public Builder claim(String name, Object value) {
            this.claims.put(name, value);
            return this;
        }

        public Builder claims(Consumer<Map<String, Object>> claimsConsumer) {
            claimsConsumer.accept(this.claims);
            return this;
        }

        public Builder userId(Integer userId){
            return this.claim("user_id",userId);
        }

        public Builder username(String name) {
            return this.claim("username", name);
        }

        public Builder nickname(String nickname) {
            return this.claim("nickname", nickname);
        }

        public UserInfo build() {
            return new UserInfo(this.claims);
        }
    }
}
