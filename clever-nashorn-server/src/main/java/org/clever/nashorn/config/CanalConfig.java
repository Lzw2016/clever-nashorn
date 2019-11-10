package org.clever.nashorn.config;

import lombok.Data;
import org.clever.canal.instance.manager.model.CanalParameter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 单个Canal配置
 */
@Data
public class CanalConfig {
//    /**
//     * 通道名称
//     */
//    private String destination;
    /**
     * 过滤器
     */
    private String filter;
    /**
     * 参数定义
     */
    @NestedConfigurationProperty
    private CanalParameter canalParameter;
}
