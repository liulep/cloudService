package com.yue.filter.core.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductStockParam implements Serializable {

    private final static long serialVersionUID= -643782643782324L;

    private Long pid;

    private Integer count;
}
