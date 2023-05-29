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
@TableName(value = "`order`")
@SuperBuilder
public class Order extends BaseEntity implements Serializable {

    private final static long serialVersionUID= - 12341231231546L;

    @TableId(value = "id",type = IdType.INPUT)
    @TableField(value = "id",fill = FieldFill.INSERT)
    private Long id;

    @TableField(value = "user_id")
    private Integer userId; //用户Id

    @TableField(value = "user_name")
    private String username; //用户名

    @TableField(value = "phone")
    private String phone; //联系方式

    @TableField(value = "address")
    private String address; //收获地址

    @TableField(value = "total_price")
    private BigDecimal totalPrice; //总价

    public Order(){
        this.id= SnowFlakeFactory.getSnowFlake().nextId();
    }
}
