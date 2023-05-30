package com.yue.product.controller;

import com.yue.product.core.entity.Product;
import com.yue.product.core.param.ProductStockParam;
import com.yue.product.service.intf.ProductService;
import com.yue.resp.anno.NotControllerResponseAdvice;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/product")
public class ProductController {

    private final ProductService productService;

    //获取商品详情
    @GetMapping("/get/{pid}")
    @NotControllerResponseAdvice
    public Product getProId(@PathVariable(value = "pid")Long pid){
        return productService.getById(pid);
    }

    //减扣商品库存
    @PostMapping("/update_count")
    @NotControllerResponseAdvice
    public boolean updateCount(@RequestBody ProductStockParam param){
        return productService.updateProductStockById(param.getPid(), param.getCount());
    }
}
