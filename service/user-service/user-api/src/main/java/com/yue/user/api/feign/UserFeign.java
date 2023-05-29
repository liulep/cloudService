package com.yue.user.api.feign;

import com.yue.user.api.feign.fallback.UserFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "userService",contextId = "user_boot",fallback = UserFeignFallback.class)
public interface UserFeign {
}
