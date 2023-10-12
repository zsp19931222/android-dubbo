package com.zsp.controller;

import com.zsp.service.ApiService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: created by zsp on 2023/10/11 0011 14:43
 */
@RestController
@RequestMapping("/info")
public class InfoController {

    //dumbo提供的Reference注解，用于调用远程服务
    @DubboReference(check = false)
    private ApiService infoService;

    @GetMapping("/getInfo/{s}")
    public String getInfo(@PathVariable(value = "s") String s) {
        return infoService.getInfo(s);
    }
}
