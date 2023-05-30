package com.yue.filter.service.intf;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yue.order.core.entity.Order;
import com.yue.order.core.param.OrderSaveParam;

//订单业务层接口
public interface OrderService extends IService<Order> {

    //生成订单
    public boolean saveOrder(OrderSaveParam param);
}
