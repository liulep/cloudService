package com.yue.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yue.common.model.entity.R;
import com.yue.common.model.entity.YueException;
import com.yue.common.model.security.LoginUser;
import com.yue.common.utils.OauthUtils;
import com.yue.common.utils.id.SnowFlakeFactory;
import com.yue.order.core.entity.Order;
import com.yue.order.core.entity.OrderItem;
import com.yue.auth.core.entity.Product;
import com.yue.user.core.entity.User;
import com.yue.order.core.param.OrderSaveParam;
import com.yue.auth.core.param.ProductStockParam;
import com.yue.auth.mapper.OrderItemMapper;
import com.yue.auth.mapper.OrderMapper;
import com.yue.auth.service.intf.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrder(OrderSaveParam param) {
        //获取用户信息
        LoginUser currentUser = OauthUtils.getCurrentUser();
        if(ObjectUtils.isEmpty(currentUser)){
            throw new YueException("用户不存在");
        }
        User user = restTemplate.getForObject("http://127.0.0.1:8082/user/get/" + currentUser.getUserId(), User.class);
        if(ObjectUtils.isEmpty(user)){
            throw new YueException("未获取到用户信息");
        }
        //获取商品信息
        Product product = restTemplate.getForObject("http://127.0.0.1:8083/product/get/" + param.getPid(), Product.class);
        if(ObjectUtils.isEmpty(product)){
            throw new YueException("未获取到商品信息");
        }
        if(param.getStock() > product.getProStack()){
            throw new YueException("商品库存不足");
        }
        //生成订单
        Order order = Order.builder()
                .id(SnowFlakeFactory.getSnowFlakeFromCache().nextId())
                .userId(user.getId())
                .phone(user.getPhone())
                .username(user.getUsername())
                .totalPrice(product.getProPrice().multiply(new BigDecimal(param.getStock())))
                .build();
        orderMapper.insert(order);
        //生成子订单
        OrderItem orderItem=OrderItem.builder()
                .id(SnowFlakeFactory.getSnowFlakeFromCache().nextId())
                .number(param.getStock())
                .orderId(order.getId())
                .proId(param.getPid())
                .proPrice(product.getProPrice())
                .proName(product.getProName())
                .build();
        orderItemMapper.insert(orderItem);
        //减扣库存
        ProductStockParam productStockParam = new ProductStockParam();
        productStockParam.setPid(param.getPid());
        productStockParam.setCount(param.getStock());
        R<Product> r = restTemplate.postForObject("http://localhost:8083/product/update_count", productStockParam, R.class);
        return r != null && r.getCode() == HttpStatus.OK.value();
    }
}
