package com.yue.auth.service.intf;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yue.auth.core.entity.Product;
import org.apache.ibatis.annotations.Param;

//商品业务层
public interface ProductService extends IService<Product> {

    //修改商品库存
    public Boolean updateProductStockById(@Param("id")Long id, @Param("count")Integer count);
}
