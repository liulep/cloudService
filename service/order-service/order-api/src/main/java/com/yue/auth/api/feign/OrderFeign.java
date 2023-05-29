package com.yue.auth.api.feign;

import com.yue.auth.api.feign.fallback.OrderFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "authService",contextId = "auth_boot",fallback = OrderFeignFallback.class)
public interface OrderFeign {

}
