package com.yue.product.api.feign.fallback;

import com.yue.product.api.feign.AuthFeign;
import org.springframework.stereotype.Component;

@Component
public class AuthFeignFallback implements AuthFeign {
}
