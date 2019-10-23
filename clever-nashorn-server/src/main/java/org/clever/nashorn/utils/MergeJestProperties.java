package org.clever.nashorn.utils;

import org.springframework.boot.autoconfigure.elasticsearch.jest.JestProperties;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/23 15:57 <br/>
 */
public class MergeJestProperties {

    public static JestProperties mergeConfig(JestProperties source, JestProperties target) {
        if (source == null) {
            return target;
        }
        if (target.getUris() == null) {
            target.setUris(source.getUris());
        }
        if (target.getUsername() == null) {
            target.setUsername(source.getUsername());
        }
        if (target.getPassword() == null) {
            target.setPassword(source.getPassword());
        }
        if (target.getConnectionTimeout() == null) {
            target.setConnectionTimeout(source.getConnectionTimeout());
        }
        if (target.getReadTimeout() == null) {
            target.setReadTimeout(source.getReadTimeout());
        }
        if (target.getProxy() != null && source.getProxy() != null) {
            if (target.getProxy().getHost() == null) {
                target.getProxy().setHost(source.getProxy().getHost());
            }
            if (target.getProxy().getPort() == null) {
                target.getProxy().setPort(source.getProxy().getPort());
            }
        }
        return target;
    }
}
