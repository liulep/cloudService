package com.yue.order.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yue.common.model.entity.BaseEntity;
import com.yue.common.utils.id.SnowFlakeFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@TableName(value = "order_item")
public class OrderItem extends BaseEntity implements Serializable {

    private final static long serialVersionUID= -972316237814154363L;

    @TableId(value = "id",type = IdType.INPUT)
    @TableField(value = "id",fill = FieldFill.INSERT)
    private Long id;

    @TableField(value = "order_id")
    private Long orderId;

    @TableField(value = "pro_id")
    private Long proId;

    @TableField(value = "pro_price")
    private BigDecimal proPrice;

    @TableField(value = "pro_name")
    private String proName;

    @TableField(value = "number")
    private Integer number;

    public OrderItem(){
        this.id= SnowFlakeFactory.getSnowFlakeFromCache().nextId();
    }

}
