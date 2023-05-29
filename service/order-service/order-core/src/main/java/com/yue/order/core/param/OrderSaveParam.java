package com.yue.order.core.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderSaveParam implements Serializable {

    private final static long serialVersionUID =-32164378243432L;

    private Long pid; //商品Id;

    private Integer stock; //商品库存
}
