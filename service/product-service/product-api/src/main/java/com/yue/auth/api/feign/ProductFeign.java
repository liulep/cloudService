package com.yue.auth.api.feign;

import com.yue.auth.api.feign.fallback.ProductFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "productService",contextId = "product_boot",fallback = ProductFeignFallback.class)
public interface ProductFeign {
}
