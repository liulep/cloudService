package com.yue.filter.api.feign.fallback;

import com.yue.filter.api.feign.ProductFeign;
import com.yue.filter.core.entity.Product;
import com.yue.filter.core.param.ProductStockParam;
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
