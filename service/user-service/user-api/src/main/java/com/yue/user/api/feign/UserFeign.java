package com.yue.user.api.feign;

import com.yue.user.api.feign.fallback.UserFeignFallback;
import com.yue.user.core.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "userService",contextId = "userService",fallback = UserFeignFallback.class)
public interface UserFeign {

    @GetMapping("/user/get/{uid}")
    public User getUserById(@PathVariable("uid")Integer uid);
}
