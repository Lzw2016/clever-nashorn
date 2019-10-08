package org.clever.nashorn.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-10-06 22:01 <br/>
 */
@SuppressWarnings("WeakerAccess")
@Slf4j
public class RedisExecutor {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisExecutor(RedisTemplate<String, Object> redisTemplate) {
        if (redisTemplate == null) {
            throw new IllegalArgumentException("redisTemplate 不能为 null");
        }
        this.redisTemplate = redisTemplate;
    }

    // TODO 各种Redis操作

    public void tt(String key, Object value) {
        redisTemplate.boundValueOps(key).set(value);
//        redisTemplate.delete();
    }
}
