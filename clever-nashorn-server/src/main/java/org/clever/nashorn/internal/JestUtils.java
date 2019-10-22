package org.clever.nashorn.internal;

import java.util.Collections;
import java.util.Map;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/22 16:54 <br/>
 */
public class JestUtils {

    private static Map<String, JestExecutor> JestExecutor_Map;
    private static String defaultJest;

    private synchronized static Map<String, JestExecutor> getJestExecutorMap() {
        return Collections.emptyMap();
    }

    public static final RedisUtils Instance = new RedisUtils();

    /**
     * 获取默认的 JestExecutor
     */
    public JestExecutor getDefaultJestExecutor() {
        Map<String, JestExecutor> redisExecutorMap = getJestExecutorMap();
        return redisExecutorMap.get(defaultJest);
    }

    /**
     * 获取对应数据源的 JestExecutor
     *
     * @param jestName Jest 数据源名称
     */
    public JestExecutor getJestExecutor(String jestName) {
        Map<String, JestExecutor> redisExecutorMap = getJestExecutorMap();
        return redisExecutorMap.get(jestName);
    }
}
