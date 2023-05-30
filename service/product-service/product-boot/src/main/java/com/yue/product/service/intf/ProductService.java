package com.yue.product.service.intf;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yue.product.core.entity.Product;
import org.apache.ibatis.annotations.Param;

//商品业务层
public interface ProductService extends IService<Product> {

    //修改商品库存
    public boolean updateProductStockById(@Param("id")Long id, @Param("count")Integer count);
}
