package com.yue.filter.api.feign;

import com.yue.filter.api.feign.fallback.ProductFeignFallback;
import com.yue.filter.core.entity.Product;
import com.yue.filter.core.param.ProductStockParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "productService",contextId = "productService",fallback = ProductFeignFallback.class)
public interface ProductFeign {

    //获取商品详情
    @GetMapping("/product/get/{pid}")
    public Product getProId(@PathVariable(value = "pid")Long pid);

    //减扣商品库存
    @PostMapping("/product/update_count")
    public boolean updateCount(@RequestBody ProductStockParam param);
}
