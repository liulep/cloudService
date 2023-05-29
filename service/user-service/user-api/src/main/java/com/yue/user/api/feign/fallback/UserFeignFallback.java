package com.yue.user.api.feign.fallback;

import com.yue.user.api.feign.UserFeign;
import org.springframework.stereotype.Component;

@Component
public class UserFeignFallback implements UserFeign {
}
