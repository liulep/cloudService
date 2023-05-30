package com.yue.filter.api.feign;

import com.yue.filter.api.feign.fallback.AuthFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "authService",contextId = "auth_boot",fallback = AuthFeignFallback.class)
public interface AuthFeign {

}
