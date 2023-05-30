package com.yue.filter.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yue.common.model.entity.BaseEntity;
import com.yue.common.utils.id.SnowFlakeFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

//商品实体类
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
@SuperBuilder
public class Product extends BaseEntity implements Serializable {

    private final static long serialVersionUID=-2907409980909070073L;

    @TableId(value = "id",type = IdType.INPUT)
    @TableField(value = "id",fill = FieldFill.INSERT)
    private Long id;

    @TableField(value = "pro_name")
    private String proName; //商品名称

    @TableField(value = "pro_price")
    private BigDecimal proPrice; //商品价格

    @TableField(value = "pro_stock")
    private Integer proStock; //商品库存

    public Product(){
        this.id = SnowFlakeFactory.getSnowFlakeFromCache().nextId();
    }
}
