package com.yue.auth.api.feign;

import com.yue.auth.api.feign.fallback.AuthFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "authService",contextId = "auth_boot",fallback = AuthFeignFallback.class)
public interface AuthFeign {

}
