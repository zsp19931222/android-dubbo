package com.zsp.util;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: created by zsp on 2023/10/25 0025 17:37
 */
@Component
public class RBloomFilterUtil {

    public RBloomFilter<String> getBloomFilter(String name) {

        RBloomFilter<String> bloomFilter = Redisson.create().getBloomFilter(name);
        bloomFilter.tryInit(100, 0.01);
        return bloomFilter;
    }
}
