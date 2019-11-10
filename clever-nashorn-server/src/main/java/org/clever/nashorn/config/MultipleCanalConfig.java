package org.clever.nashorn.config;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Collections;
import java.util.Map;

/**
 * 多Canal配置
 */
@Data
public class MultipleCanalConfig {
    /**
     * Canal全局配置
     */
    @NestedConfigurationProperty
    private CanalConfig globalConfig;
    /**
     * Canal配置集合(destination --> CanalConfig)
     */
    private Map<String, CanalConfig> canalConfigMap = Collections.emptyMap();
}
