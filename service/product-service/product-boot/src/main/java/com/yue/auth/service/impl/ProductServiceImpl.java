package com.yue.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yue.auth.core.entity.Product;
import com.yue.auth.mapper.ProductMapper;
import com.yue.auth.service.intf.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;

    /**
     * 修改商品库存
     * @param id
     * @param count
     * @return
     */
    @Override
    public Boolean updateProductStockById(Long id, Integer count) {
        return productMapper.updateProductStockById(id,count)!=0;
    }
}
