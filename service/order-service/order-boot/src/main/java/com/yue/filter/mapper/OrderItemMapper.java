package com.yue.filter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yue.order.core.entity.OrderItem;
import org.springframework.stereotype.Repository;

//子订单数据层
@Repository
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
