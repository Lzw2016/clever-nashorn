package org.clever.nashorn.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Collections;
import java.util.Map;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/23 15:38 <br/>
 */
@Data
public class MultipleJestConfig {
    /**
     * 默认的Jest数据源名称
     */
    private String defaultJest = "spring-data-jest";
    /**
     * Jest数据源全局配置
     */
    @NestedConfigurationProperty
    private JestProperties globalConfig;
    /**
     * Jest数据源集合(Jest数据源名称 --> 数据源配置)
     */
    private Map<String, JestProperties> jestConfigMap = Collections.emptyMap();
}
