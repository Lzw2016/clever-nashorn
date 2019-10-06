package org.clever.nashorn.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-10-06 22:01 <br/>
 */
@Slf4j
public class RedisExecutor {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisExecutor(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        // 创建 RedisTemplate
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        redisTemplate.afterPropertiesSet();
    }

    // TODO 各种Redis操作
}
