package com.yue.product.api.feign.fallback;

import com.yue.product.api.feign.ProductFeign;
import com.yue.product.core.entity.Product;
import com.yue.product.core.param.ProductStockParam;
import org.springframework.stereotype.Component;

@Component
public class ProductFeignFallback implements ProductFeign {
    @Override
    public Product getProId(Long pid) {
        return null;
    }

    @Override
    public boolean updateCount(ProductStockParam param) {
        return false;
    }
}
