package com.zsp.event;

import org.springframework.context.ApplicationEvent;

/**
 * @description:
 * @author: created by zsp on 2023/10/17 0017 09:42
 */

public class DemoEvent extends ApplicationEvent {

    private static final long serialVersionUID = -2753705718295396328L;

    private String msg;

    public DemoEvent(Object source,String msg) {
        super(source);
        this.msg=msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
