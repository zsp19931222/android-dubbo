package com.zsp.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: created by zsp on 2023/10/17 0017 09:53
 */
@Component
public class DemoPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void sendMsg(String msg) {
        applicationEventPublisher.publishEvent(new DemoEvent(this, msg));
    }
}
