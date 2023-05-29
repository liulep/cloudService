package com.yue.auth.controller;

import com.yue.order.core.param.OrderSaveParam;
import com.yue.auth.service.intf.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/save_order")
    public String saveOrder(@RequestBody OrderSaveParam param){
        boolean b = orderService.saveOrder(param);
        if(b){
            return "生成订单成功";
        }
        return "生成订单失败";
    }
}
