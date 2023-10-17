package com.zsp.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: created by zsp on 2023/10/17 0017 09:47
 */
@Component
public class DemoListener {
    @EventListener(value = {DemoEvent.class})
    public void processApplicationEvent(DemoEvent event) {
        String msg = event.getMsg();
        System.out.println("bean-listener 收到了 publisher 发布的消息: " + msg);
    }
}
