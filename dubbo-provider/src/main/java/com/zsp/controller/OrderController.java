package com.zsp.controller;

import com.zsp.model.Order;
import com.zsp.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description:
 * @author: created by zsp on 2023/10/23 0023 15:28
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    /**
     * 根据id查询订单
     */
    @RequestMapping("/getById")
    public Order getById(@RequestParam("id") Long id) {
        //根据id查询订单
        orderService.getById(id);
        return null;
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public String create(@RequestBody Order order) {
        //创建订单
        orderService.create(order);
        return "sucess";
    }

    /**
     * 对订单进行支付
     */
    @RequestMapping("/pay")
    public String pay(@RequestParam("id") Long id) {
        //对订单进行支付
        orderService.pay(id);
        return "success";
    }

    /**
     * 对订单进行发货
     */
    @RequestMapping("/deliver")
    public String deliver(@RequestParam("id") Long id) {
        //对订单进行确认收货
        orderService.deliver(id);
        return "success";
    }

    /**
     * 对订单进行确认收货
     */
    @RequestMapping("/receive")
    public String receive(@RequestParam("id") Long id) {
        //对订单进行确认收货
        orderService.receive(id);
        return "success";
    }
}