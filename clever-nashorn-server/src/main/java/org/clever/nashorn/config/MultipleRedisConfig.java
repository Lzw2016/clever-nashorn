package org.clever.nashorn.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Collections;
import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-10-06 20:38 <br/>
 */
@Data
public class MultipleRedisConfig {

    /**
     * 默认的Redis数据源名称
     */
    private String defaultRedis;
    /**
     * Redis数据源全局配置
     */
    @NestedConfigurationProperty
    private RedisProperties globalConfig;
    /**
     * Redis数据源集合(Redis数据源名称 --> 数据源配置)
     */
    private Map<String, RedisProperties> redisConfigMap = Collections.emptyMap();
}
