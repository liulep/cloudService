package com.yue.product.api.feign;

import com.yue.product.api.feign.fallback.OrderFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "authService",contextId = "auth_boot",fallback = OrderFeignFallback.class)
public interface OrderFeign {

}
