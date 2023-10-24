package com.zsp.service;

import com.zsp.model.Order;

/**
 * @description:
 * @author: created by zsp on 2023/10/23 0023 15:29
 */
public interface OrderService {
    Order getById(Long id);

    Order create(Order order);

    Order deliver(Long id);

    Order pay(Long id);

    Order receive(Long id);
}
