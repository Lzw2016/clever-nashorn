package org.clever.nashorn.config;

import com.zaxxer.hikari.HikariConfig;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Collections;
import java.util.Map;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/30 11:47 <br/>
 */
@Data
public class MultipleDataSourceConfig {
    /**
     * 默认的数据源名称
     */
    private String defaultDataSource;
    /**
     * 数据源全局配置
     */
    @NestedConfigurationProperty
    private HikariConfig dataSourceGlobalConfig;
    /**
     * 数据源集合，数据源名称 --> 数据源配置
     */
    private Map<String, HikariConfig> dataSourceMap = Collections.emptyMap();
}
