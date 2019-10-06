package org.clever.nashorn.internal;

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

    public static final RedisUtils Instance = new RedisUtils();

//    /**
//     * 获取默认的 RedisExecutor
//     */
//    public RedisExecutor getDefaultRedisExecutor() {
//
//    }

//    /**
//     * 获取对应数据源的 RedisExecutor
//     *
//     * @param redisName Redis数据源名称
//     */
//    public RedisExecutor getJdbcExecutor(String redisName) {
//
//    }
}
