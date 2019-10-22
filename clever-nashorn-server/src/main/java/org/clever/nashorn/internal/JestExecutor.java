package org.clever.nashorn.internal;

import io.searchbox.client.JestClient;
import lombok.extern.slf4j.Slf4j;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/22 16:54 <br/>
 */
@SuppressWarnings({"unused", "DuplicatedCode", "FieldCanBeLocal"})
@Slf4j
public class JestExecutor {
    private final JestClient jestClient;

    public JestExecutor(JestClient jestClient) {
        if (jestClient == null) {
            throw new IllegalArgumentException("jestClient 不能为 null");
        }
        this.jestClient = jestClient;
    }

    // TODO es 增删查改
}
