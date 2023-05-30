package com.yue.filter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yue.filter.core.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

//商品数据层
@Repository
public interface ProductMapper extends BaseMapper<Product> {

    //修改商品库存
    public Integer updateProductStockById(@Param("id")Long id,@Param("count")Integer count);
}
