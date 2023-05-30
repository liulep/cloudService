package com.yue.filter.api.feign;

import com.yue.filter.api.feign.fallback.OrderFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "authService",contextId = "auth_boot",fallback = OrderFeignFallback.class)
public interface OrderFeign {

}
