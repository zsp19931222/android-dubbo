package com.zsp.service.impl;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zsp.enums.OrderStatus;
import com.zsp.enums.OrderStatusChangeEvent;
import com.zsp.mapper.OrderMapper;
import com.zsp.model.Order;
import com.zsp.service.OrderService;
import com.zsp.util.RBloomFilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.zsp.constant.BaseConstant.BLOOM_FILTER_NAME;
import static com.zsp.constant.BaseConstant.ORDER_KEY_PREFIX;

/**
 * @description:
 * @author: created by zsp on 2023/10/23 0023 15:30
 */
@Service("orderService")
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final StateMachine<OrderStatus, OrderStatusChangeEvent> orderStateMachine;

    private final StateMachinePersister<OrderStatus, OrderStatusChangeEvent, String> stateMachineMemPersister;

    private final OrderMapper orderMapper;

    private final StringRedisTemplate redisTemplate;

    private final RBloomFilterUtil rBloomFilterUtil;

    /**
     * 创建订单
     */
    @Override
    public Order create(Order order) {
        order.setStatus(OrderStatus.WAIT_PAYMENT.getKey());
        orderMapper.insert(order);
        return order;
    }

    /**
     * 对订单进行支付
     */
    @Override
    public Order pay(Long id) {
        Order order = orderMapper.selectById(id);
        log.info("线程名称：{},尝试支付，订单号：{}", Thread.currentThread().getName(), id);
        if (!sendEvent(OrderStatusChangeEvent.PAYED, order)) {
            log.error("线程名称：{},支付失败, 状态异常，订单信息：{}", Thread.currentThread().getName(), order);
            throw new RuntimeException("支付失败, 订单状态异常");
        }
        return order;
    }

    /**
     * 对订单进行发货
     */
    @Override
    public Order deliver(Long id) {
        Order order = orderMapper.selectById(id);
        log.info("线程名称：{},尝试发货，订单号：{}", Thread.currentThread().getName(), id);
        if (!sendEvent(OrderStatusChangeEvent.DELIVERY, order)) {
            log.error("线程名称：{},发货失败, 状态异常，订单信息：{}", Thread.currentThread().getName(), order);
            throw new RuntimeException("发货失败, 订单状态异常");
        }
        return order;
    }

    /**
     * 对订单进行确认收货
     */
    @Override
    public Order receive(Long id) {
        Order order = orderMapper.selectById(id);
        log.info("线程名称：{},尝试收货，订单号：{}", Thread.currentThread().getName(), id);
        if (!sendEvent(OrderStatusChangeEvent.RECEIVED, order)) {
            log.error("线程名称：{},收货失败, 状态异常，订单信息：{}", Thread.currentThread().getName(), order);
            throw new RuntimeException("收货失败, 订单状态异常");
        }
        return order;
    }

    /**
     * 发送订单状态转换事件
     * synchronized修饰保证这个方法是线程安全的
     */
    private synchronized boolean sendEvent(OrderStatusChangeEvent changeEvent, Order order) {
        boolean result = false;
        try {
            //启动状态机
            orderStateMachine.start();
            //尝试恢复状态机状态
            stateMachineMemPersister.restore(orderStateMachine, String.valueOf(order.getId()));
            Message message = MessageBuilder.withPayload(changeEvent).setHeader("order", order).build();
            result = orderStateMachine.sendEvent(message);
            //持久化状态机状态
            stateMachineMemPersister.persist(orderStateMachine, String.valueOf(order.getId()));
        } catch (Exception e) {
            log.error("订单操作失败:{}", e.getMessage());
        } finally {
            orderStateMachine.stop();
        }
        return result;
    }

    @Override
    public Order getById(Long id) {

        String key = ORDER_KEY_PREFIX + id.toString();
        //检查布隆过滤器中是否可能存在该键
        RBloomFilter<String> filter = rBloomFilterUtil.getBloomFilter(BLOOM_FILTER_NAME);
        boolean contains = filter.contains(key);
        if (!contains) {
            // 不存在的情况下，直接返回空对象或抛出异常等处理方式
            log.info("布隆过滤器中没有当前key：{}", key);
            return null;
        }

        // 从缓存中获取数据
        Order cachedOrder = getCachedOrder(key);
        if (cachedOrder != null) {
            log.info("从缓存获取order：{}", cachedOrder.toString());
            return cachedOrder;
        }

        // 从数据库查询数据
        Order dbOrder = orderMapper.selectById(id);
        if (dbOrder != null) {
            log.info("从数据库查询数据order：{}", dbOrder.toString());
            // 将数据库查询结果放入缓存
            cacheOrder(key, dbOrder);
            filter.add(key);
        }
        return dbOrder;
    }

    private Order getCachedOrder(String key) {
        try {
            log.info("根据缓存逻辑从 Redis 中获取数据");
            ObjectMapper mapper = new ObjectMapper();
            // 获取数据
            String jsonUser = redisTemplate.opsForValue().get(key);
            // 手动反序列化
            return mapper.readValue(jsonUser, Order.class);
        } catch (Exception e) {
            log.info("根据缓存逻辑从 Redis 中获取数据 失败");
            return null;
        }

    }

    private void cacheOrder(String key, Order order) {
        try {
            log.info("将数据写入 Redis 缓存");
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(order);
            redisTemplate.opsForValue().set(key, json, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.info("将数据写入 Redis 缓存 失败");
        }

    }
}
