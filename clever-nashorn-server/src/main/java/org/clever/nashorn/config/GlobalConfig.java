package org.clever.nashorn.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2017-12-04 12:44 <br/>
 */
@Component
@ConfigurationProperties(prefix = "clever.nashorn.config")
@Data
public class GlobalConfig {
    /**
     * 多数据源配置
     */
    @NestedConfigurationProperty
    private MultipleDataSourceConfig multipleDataSource = new MultipleDataSourceConfig();
}
