package com.yue.product.api.feign;

import com.yue.product.api.feign.fallback.AuthFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "authService",contextId = "auth_boot",fallback = AuthFeignFallback.class)
public interface AuthFeign {

}
