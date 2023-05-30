package com.yue.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yue.product.api.feign.ProductFeign;
import com.yue.common.model.entity.YueException;
import com.yue.common.model.security.LoginUser;
import com.yue.common.utils.OauthUtils;
import com.yue.common.utils.id.SnowFlakeFactory;
import com.yue.order.core.entity.Order;
import com.yue.order.core.entity.OrderItem;
import com.yue.product.core.entity.Product;
import com.yue.user.api.feign.UserFeign;
import com.yue.user.core.entity.User;
import com.yue.order.core.param.OrderSaveParam;
import com.yue.product.core.param.ProductStockParam;
import com.yue.product.mapper.OrderItemMapper;
import com.yue.product.mapper.OrderMapper;
import com.yue.product.service.intf.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

//订单服务业务层
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;
    private final RestTemplate restTemplate;
    private final UserFeign userFeign;
    private final ProductFeign productFeign;

    @Override
    public boolean saveOrder(OrderSaveParam param) {
        //获取用户信息
        LoginUser currentUser = OauthUtils.getCurrentUser();
        if(ObjectUtils.isEmpty(currentUser)){
            throw new YueException("用户不存在");
        }
        User user = this.userFeign.getUserById(currentUser.getUserId());
        if(ObjectUtils.isEmpty(user)){
            throw new YueException("未获取到用户信息");
        }
        //获取商品信息
        Product product = this.productFeign.getProId(param.getPid());
        if(ObjectUtils.isEmpty(product)){
            throw new YueException("未获取到商品信息");
        }
        if(param.getStock() > product.getProStock()){
            throw new YueException("商品库存不足");
        }
        return saveOrder(param,user,product);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrder(OrderSaveParam param,User user,Product product){
        //生成订单
        Order order = Order.builder()
                .id(SnowFlakeFactory.getSnowFlakeFromCache().nextId())
                .userId(user.getId())
                .phone(user.getPhone())
                .username(user.getUsername())
                .totalPrice(product.getProPrice().multiply(new BigDecimal(param.getStock())))
                .build();
        this.orderMapper.insert(order);
        //生成子订单
        OrderItem orderItem=OrderItem.builder()
                .id(SnowFlakeFactory.getSnowFlakeFromCache().nextId())
                .number(param.getStock())
                .orderId(order.getId())
                .proId(param.getPid())
                .proPrice(product.getProPrice())
                .proName(product.getProName())
                .build();
        this.orderItemMapper.insert(orderItem);
        //减扣库存
        ProductStockParam productStockParam = new ProductStockParam();
        productStockParam.setPid(param.getPid());
        productStockParam.setCount(param.getStock());
        Boolean aBoolean = this.productFeign.updateCount(productStockParam);
        return Boolean.TRUE.equals(aBoolean);
    }
}
