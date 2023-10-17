package com.zsp.service.impl;

import com.zsp.event.DemoPublisher;
import com.zsp.service.ApiService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: created by zsp on 2023/10/10 0010 11:05
 */

@DubboService(interfaceClass = ApiService.class)
public class ApiServiceImpl implements ApiService {
    @Autowired
    private DemoPublisher demoPublisher;

    @Override
    public String getInfo(String s) {
        return s+"访问成功";
    }

    @Override
    public void sendMsg(String s) {
        demoPublisher.sendMsg(s);
    }
}
