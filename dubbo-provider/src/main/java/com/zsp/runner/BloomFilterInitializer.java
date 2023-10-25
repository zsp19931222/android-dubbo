package com.zsp.runner;

import com.zsp.mapper.OrderMapper;
import com.zsp.util.RBloomFilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import static com.zsp.constant.BaseConstant.BLOOM_FILTER_NAME;

/**
 * @description:
 * @author: created by zsp on 2023/10/25 0025 17:28
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class BloomFilterInitializer implements ApplicationRunner {

    private final OrderMapper orderMapper;

    private final RBloomFilterUtil rBloomFilterUtil;


    @Override
    public void run(ApplicationArguments args) {
        orderMapper.selectList(null).forEach(order -> {
            String key = "order:" + order.getId().toString();
            log.info("预先将一些可能的键加载到布隆过滤器中key:{}",key);
            rBloomFilterUtil.getBloomFilter(BLOOM_FILTER_NAME).add(key);
        });
    }
}
