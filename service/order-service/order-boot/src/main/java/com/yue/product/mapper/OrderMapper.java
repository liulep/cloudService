package com.yue.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yue.order.core.entity.Order;
import org.springframework.stereotype.Repository;

//订单数据层
@Repository
public interface OrderMapper extends BaseMapper<Order> {
}
