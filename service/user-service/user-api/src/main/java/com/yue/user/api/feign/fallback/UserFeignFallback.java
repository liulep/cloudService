package com.yue.user.api.feign.fallback;

import com.yue.user.api.feign.UserFeign;
import com.yue.user.core.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFeignFallback implements UserFeign {
    @Override
    public User getUserById(Integer uid) {
        return null;
    }
}
