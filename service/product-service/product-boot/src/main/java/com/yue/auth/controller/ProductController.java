package com.yue.auth.controller;

import com.yue.auth.core.entity.Product;
import com.yue.auth.core.param.ProductStockParam;
import com.yue.auth.service.intf.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/product")
public class ProductController {

    private final ProductService productService;

    //获取商品详情
    @GetMapping("/get/{pid}")
    public Product getProId(@PathVariable(value = "pid")Long pid){
        return productService.getById(pid);
    }

    //减扣商品库存
    @PostMapping("/update_count")
    public String updateCount(@RequestBody ProductStockParam param){
        Boolean aBoolean = productService.updateProductStockById(param.getPid(), param.getCount());
        if(aBoolean.equals(Boolean.TRUE)){
            return "商品库存减扣成功";
        }
        else return "商品库存减扣失败";
    }
}
