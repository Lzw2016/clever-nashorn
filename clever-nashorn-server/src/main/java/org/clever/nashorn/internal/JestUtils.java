package org.clever.nashorn.internal;

import io.searchbox.client.JestClient;
import org.clever.common.utils.spring.SpringContextHolder;
import org.clever.nashorn.config.GlobalConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/22 16:54 <br/>
 */
public class JestUtils {

    private static Map<String, JestExecutor> JestExecutor_Map;
    private static String defaultJest;

    private synchronized static Map<String, JestExecutor> getJestExecutorMap() {
        if (JestExecutor_Map == null) {
            Map<String, JestClient> jestClientMap = SpringContextHolder.getBean("MultipleJest");
            Map<String, JestExecutor> tmpMap = new HashMap<>(jestClientMap.size());
            jestClientMap.forEach((name, jestClient) -> {
                JestExecutor jestExecutor = new JestExecutor(jestClient);
                tmpMap.put(name, jestExecutor);
            });
            JestExecutor_Map = Collections.unmodifiableMap(tmpMap);
        }
        if (defaultJest == null) {
            GlobalConfig globalConfig = SpringContextHolder.getBean(GlobalConfig.class);
            defaultJest = globalConfig.getMultipleJest().getDefaultJest();
        }
        return JestExecutor_Map;
    }

    public static final JestUtils Instance = new JestUtils();

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
