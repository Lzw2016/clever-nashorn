package org.clever.nashorn.internal;

import org.clever.common.utils.spring.SpringContextHolder;
import org.clever.nashorn.config.GlobalConfig;
import org.clever.nashorn.config.LettuceClientBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-10-01 09:41 <br/>
 */
public class RedisUtils {

    /**
     * 所有的 RedisExecutor
     */
    private static Map<String, RedisExecutor> RedisExecutor_Map;
    /**
     * 默认的 Redis
     */
    private static String defaultRedis;

    private synchronized static Map<String, RedisExecutor> getRedisExecutorMap() {
        if (RedisExecutor_Map == null) {
            Map<String, LettuceClientBuilder> lettuceClientBuilderMap = SpringContextHolder.getBean("MultipleRedis");
            Map<String, RedisExecutor> tmpMap = new HashMap<>();
            lettuceClientBuilderMap.forEach((name, lettuceClientBuilder) -> {
                RedisExecutor redisExecutor = new RedisExecutor(lettuceClientBuilder.getRedisTemplate());
                tmpMap.put(name, redisExecutor);
            });
            RedisExecutor_Map = Collections.unmodifiableMap(tmpMap);
        }
        if (defaultRedis == null) {
            GlobalConfig globalConfig = SpringContextHolder.getBean(GlobalConfig.class);
            defaultRedis = globalConfig.getMultipleRedis().getDefaultRedis();
        }
        return RedisExecutor_Map;
    }

    public static final RedisUtils Instance = new RedisUtils();

    /**
     * 获取默认的 RedisExecutor
     */
    public RedisExecutor getDefaultRedisExecutor() {
        Map<String, RedisExecutor> redisExecutorMap = getRedisExecutorMap();
        return redisExecutorMap.get(defaultRedis);
    }

    /**
     * 获取对应数据源的 RedisExecutor
     *
     * @param redisName Redis数据源名称
     */
    public RedisExecutor getJdbcExecutor(String redisName) {
        Map<String, RedisExecutor> redisExecutorMap = getRedisExecutorMap();
        return redisExecutorMap.get(redisName);
    }
}
