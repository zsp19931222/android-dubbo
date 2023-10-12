package com.zsp.service.impl;

import com.zsp.service.ApiService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @description:
 * @author: created by zsp on 2023/10/10 0010 11:05
 */

@DubboService(interfaceClass = ApiService.class)
public class ApiServiceImpl implements ApiService {
    @Override
    public String getInfo(String s) {
        return s+"访问成功";
    }
}
