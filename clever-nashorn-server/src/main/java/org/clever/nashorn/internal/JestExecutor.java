package org.clever.nashorn.internal;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

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

    /**
     * 创建索引
     *
     * @param index    索引名称
     * @param settings settings
     * @param mappings mappings
     * @param aliases  aliases
     * @param payload  payload
     * @param refresh  refresh
     */
    public JestResult createIndex(
            String index,
            Map<String, Object> settings,
            Map<String, Object> mappings,
            Map<String, Object> aliases,
            Map<String, Object> payload, Boolean refresh) throws IOException {
        CreateIndex.Builder builder = new CreateIndex.Builder(index)
                .settings(settings)
                .mappings(mappings)
                .aliases(aliases)
                .payload(payload);
        if (refresh != null) {
            builder.refresh(refresh);
        }
        return jestClient.execute(builder.build());
    }

    /**
     * 创建索引
     *
     * @param index    索引名称
     * @param settings settings
     * @param mappings mappings
     * @param aliases  aliases
     */
    public JestResult createIndex(
            String index,
            Map<String, Object> settings,
            Map<String, Object> mappings,
            Map<String, Object> aliases) throws IOException {
        return createIndex(index, settings, mappings, aliases, null, null);
    }

    /**
     * 创建索引
     *
     * @param index    索引名称
     * @param settings settings
     * @param mappings mappings
     */
    public JestResult createIndex(String index, Map<String, Object> settings, Map<String, Object> mappings) throws IOException {
        return createIndex(index, settings, mappings, null, null, null);
    }

    /**
     * 创建索引
     *
     * @param index    索引名称
     * @param settings settings
     */
    public JestResult createIndex(String index, Map<String, Object> settings) throws IOException {
        return createIndex(index, settings, null, null, null, null);
    }

    /**
     * 删除索引
     *
     * @param index   索引名称
     * @param type    文档类型
     * @param refresh refresh
     */
    public JestResult deleteIndex(String index, String type, Boolean refresh) throws IOException {
        DeleteIndex.Builder builder = new DeleteIndex.Builder(index);
        if (StringUtils.isBlank(type)) {
            builder.type(type);
        }
        if (refresh != null) {
            builder.refresh(refresh);
        }
        return jestClient.execute(builder.build());
    }

    /**
     * 删除索引
     *
     * @param index 索引名称
     * @param type  文档类型
     */
    public JestResult deleteIndex(String index, String type) throws IOException {
        return deleteIndex(index, type, null);
    }

    /**
     * 删除索引
     *
     * @param index 索引名称
     */
    public JestResult deleteIndex(String index) throws IOException {
        return deleteIndex(index, null, null);
    }

    /**
     * 新增或者更新数据
     *
     * @param index   索引名称
     * @param type    文档类型
     * @param id      文档ID
     * @param source  文档数据
     * @param refresh refresh
     */
    public DocumentResult saveOrUpdate(String index, String type, String id, Map<String, Object> source, Boolean refresh) throws IOException {
        Index.Builder builder = new Index.Builder(source).index(index).type(type);
        if (StringUtils.isBlank(id)) {
            builder.id(id);
        }
        if (refresh != null) {
            builder.refresh(refresh);
        }
        return jestClient.execute(builder.build());
    }

    /**
     * 新增或者更新数据
     *
     * @param index  索引名称
     * @param type   文档类型
     * @param id     文档ID
     * @param source 文档数据
     */
    public DocumentResult saveOrUpdate(String index, String type, String id, Map<String, Object> source) throws IOException {
        return saveOrUpdate(index, type, id, source, null);
    }

    /**
     * 新增或者更新数据
     *
     * @param index  索引名称
     * @param type   文档类型
     * @param source 文档数据
     */
    public DocumentResult saveOrUpdate(String index, String type, Map<String, Object> source) throws IOException {
        return saveOrUpdate(index, type, null, source, null);
    }

    public void update() {
//        Update
    }

    public void updateByQuery() {
//        UpdateByQuery
    }

    public void delete() {
//        Delete
    }

    public void deleteByQuery() {
//        DeleteByQuery
    }

    public void search() {
//        Search
    }
}
