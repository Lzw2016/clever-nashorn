package org.clever.nashorn.internal;

import org.clever.common.utils.spring.SpringContextHolder;
import org.clever.nashorn.config.GlobalConfig;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JdbcUtils {
    /**
     * 所有的 JdbcExecutor
     */
    private static Map<String, JdbcExecutor> JdbcExecutor_Map;
    /**
     * 默认的 DataSource
     */
    private static String defaultDataSourceName;

    private synchronized static Map<String, JdbcExecutor> getJdbcExecutorMap() {
        if (JdbcExecutor_Map == null) {
            Map<String, DataSource> dataSourceMap = SpringContextHolder.getBean("MultipleDataSource");
            Map<String, JdbcExecutor> tmpMap = new HashMap<>();
            dataSourceMap.forEach((name, dataSource) -> {
                tmpMap.put(name, new JdbcExecutor(dataSource));
            });
            JdbcExecutor_Map = Collections.unmodifiableMap(tmpMap);
        }
        if (defaultDataSourceName == null) {
            GlobalConfig globalConfig = SpringContextHolder.getBean(GlobalConfig.class);
            defaultDataSourceName = globalConfig.getMultipleDataSource().getDefaultDataSource();
        }
        return JdbcExecutor_Map;
    }

    public static final JdbcUtils Instance = new JdbcUtils();

    /**
     * 获取默认的 JdbcExecutor
     */
    public JdbcExecutor getDefaultJdbcExecutor() {
        Map<String, JdbcExecutor> jdbcExecutorMap = getJdbcExecutorMap();
        return jdbcExecutorMap.get(defaultDataSourceName);
    }

    /**
     * 获取对应数据源的 JdbcExecutor
     *
     * @param dataSourceName 数据源名称
     */
    public JdbcExecutor getJdbcExecutor(String dataSourceName) {
        Map<String, JdbcExecutor> jdbcExecutorMap = getJdbcExecutorMap();
        return jdbcExecutorMap.get(dataSourceName);
    }
}
