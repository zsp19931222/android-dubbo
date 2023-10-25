package com.zsp.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

/**
 * @description:
 * @author: created by zsp on 2023/10/25 0025 11:02
 */
@RestController
@RequiredArgsConstructor
public class IdempotentController {

    /**
     * Lua脚本
     */
    private final String LUA_SCRIPT = "local tokenExists = redis.call('EXISTS', KEYS[1])\n" +
            "if tokenExists == 1 then\n" +
            "    return true\n" +
            "else\n" +
            "    return false\n" +
            "end";

    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/submit")
    public String submit(@RequestHeader("token") String token) {
        if (StringUtils.isBlank(token)) {
            return "Missing token";
        }

        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>(LUA_SCRIPT, Boolean.class);

        //如果token不存在，则表示已经实现了业务逻辑

        // 使用Lua脚本执行原子性操作
        Boolean tokenExists = redisTemplate.execute(script, Collections.singletonList(token));

        if (tokenExists == null || !tokenExists) {
            return "Duplicate submission";
        }

        try {
            // 具体的接口处理逻辑，在这里实现业务逻辑


            return "Success";
        } finally {
            // 使用DEL命令删除Token
            redisTemplate.delete(token);
        }
    }


    /**
     * 生成Token接口，用于获取一个唯一的Token
     */
    @GetMapping("/generateToken")
    public String generateToken() {
        // 生成唯一的Token
        String token = UUID.randomUUID().toString();

        // 将Token保存到Redis中，并设置过期时间（例如10分钟）
        redisTemplate.opsForValue().set(token, "true", Duration.ofMinutes(10));
        return token;
    }


}
