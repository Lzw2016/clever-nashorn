package org.clever.nashorn.internal;

import com.google.common.collect.Maps;
import org.clever.common.utils.spring.SpringContextHolder;
import org.clever.nashorn.config.GlobalConfig;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class JdbcUtils {
    /**
     * 所有的 JdbcExecutor
     */
    private static Map<String, JdbcExecutor> JdbcExecutorMap = Maps.newConcurrentMap();
    /**
     * 默认的 DataSource
     */
    private static String defaultDataSourceName;

    private synchronized static Map<String, JdbcExecutor> getJdbcExecutorMap() {

        Map<String, DataSource> dataSourceMap = SpringContextHolder.getBean("MultipleDataSource");
        dataSourceMap.forEach((name, dataSource) -> {
            if(!JdbcExecutorMap.containsKey(name)){
                JdbcExecutor jdbcExecutor = new JdbcExecutor(dataSource);
                JdbcExecutorMap.put(name, jdbcExecutor);
            }
        });

        if (defaultDataSourceName == null) {
            GlobalConfig globalConfig = SpringContextHolder.getBean(GlobalConfig.class);
            defaultDataSourceName = globalConfig.getMultipleDataSource().getDefaultDataSource();
        }
        return JdbcExecutorMap;
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
