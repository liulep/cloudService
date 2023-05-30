package com.yue.product.api.feign.fallback;

import com.yue.product.api.feign.OrderFeign;
import org.springframework.stereotype.Component;

@Component
public class OrderFeignFallback implements OrderFeign {
}
