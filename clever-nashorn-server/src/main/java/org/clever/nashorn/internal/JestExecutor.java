package org.clever.nashorn.internal;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.*;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/22 16:54 <br/>
 */
@SuppressWarnings({"unused", "DuplicatedCode", "WeakerAccess"})
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
        if (StringUtils.isNotBlank(type)) {
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
        if (StringUtils.isNotBlank(id)) {
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

    /**
     * 使用update更新数据，文档：https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update.html
     *
     * @param index   索引名称
     * @param type    文档类型
     * @param id      数据id
     * @param payload payload
     * @param refresh refresh
     */
    public DocumentResult update(String index, String type, String id, Object payload, Boolean refresh) throws IOException {
        Update.Builder builder = new Update.Builder(payload);
        builder.index(index);
        builder.type(type);
        builder.id(id);
        if (refresh != null) {
            builder.refresh(refresh);
        }
        return jestClient.execute(builder.build());
    }

    /**
     * 使用update更新数据，文档：https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update.html
     *
     * @param index   索引名称
     * @param type    文档类型
     * @param id      数据id
     * @param payload payload
     */
    public DocumentResult update(String index, String type, String id, Object payload) throws IOException {
        return update(index, type, id, payload, null);
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param payload    payload
     * @param allow      是否允许不定义索引名称
     * @param ignore     忽略不可用的索引，这包括不存在或已关闭的索引
     * @param refresh    refresh
     */
    public UpdateByQueryResult updateByQuery(
            Collection<String> indexNames,
            Collection<String> indexTypes,
            Object payload,
            Boolean allow,
            Boolean ignore,
            Boolean refresh) throws IOException {
        UpdateByQuery.Builder builder = new UpdateByQuery.Builder(payload);
        builder.addIndices(indexNames);
        builder.addTypes(indexTypes);
        if (allow != null) {
            builder.allowNoIndices(allow);
        }
        if (ignore != null) {
            builder.ignoreUnavailable(ignore);
        }
        if (refresh != null) {
            builder.refresh(refresh);
        }
        return jestClient.execute(builder.build());
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param payload    payload
     * @param refresh    refresh
     */
    public UpdateByQueryResult updateByQuery(Collection<String> indexNames, Collection<String> indexTypes, Object payload, Boolean refresh) throws IOException {
        return updateByQuery(indexNames, indexTypes, payload, null, null, refresh);
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param payload    payload
     */
    public UpdateByQueryResult updateByQuery(Collection<String> indexNames, Collection<String> indexTypes, Object payload) throws IOException {
        return updateByQuery(indexNames, indexTypes, payload, null, null, null);
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param payload    payload
     * @param allow      是否允许不定义索引名称
     * @param ignore     忽略不可用的索引，这包括不存在或已关闭的索引
     * @param refresh    refresh
     */
    public UpdateByQueryResult updateByQuery(
            ScriptObjectMirror indexNames,
            ScriptObjectMirror indexTypes,
            Object payload,
            Boolean allow,
            Boolean ignore,
            Boolean refresh) throws IOException {
        return updateByQuery(scriptObjectToStrArray(indexNames), scriptObjectToStrArray(indexTypes), payload, allow, ignore, refresh);
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param payload    payload
     * @param refresh    refresh
     */
    public UpdateByQueryResult updateByQuery(ScriptObjectMirror indexNames, ScriptObjectMirror indexTypes, Object payload, Boolean refresh) throws IOException {
        return updateByQuery(scriptObjectToStrArray(indexNames), scriptObjectToStrArray(indexTypes), payload, null, null, refresh);
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param payload    payload
     */
    public UpdateByQueryResult updateByQuery(ScriptObjectMirror indexNames, ScriptObjectMirror indexTypes, Object payload) throws IOException {
        return updateByQuery(scriptObjectToStrArray(indexNames), scriptObjectToStrArray(indexTypes), payload, null, null, null);
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param index   索引名称集
     * @param type    文档类型集
     * @param payload payload
     * @param allow   是否允许不定义索引名称
     * @param ignore  忽略不可用的索引，这包括不存在或已关闭的索引
     * @param refresh refresh
     */
    public UpdateByQueryResult updateByQuery(String index, String type, Object payload, Boolean allow, Boolean ignore, Boolean refresh) throws IOException {
        UpdateByQuery.Builder builder = new UpdateByQuery.Builder(payload);
        builder.addIndex(index);
        builder.addType(type);
        if (allow != null) {
            builder.allowNoIndices(allow);
        }
        if (ignore != null) {
            builder.ignoreUnavailable(ignore);
        }
        if (refresh != null) {
            builder.refresh(refresh);
        }
        return jestClient.execute(builder.build());
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param index   索引名称集
     * @param type    文档类型集
     * @param payload payload
     * @param refresh refresh
     */
    public UpdateByQueryResult updateByQuery(String index, String type, Object payload, Boolean refresh) throws IOException {
        return updateByQuery(index, type, payload, null, null, refresh);
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param index   索引名称集
     * @param type    文档类型集
     * @param payload payload
     */
    public UpdateByQueryResult updateByQuery(String index, String type, Object payload) throws IOException {
        return updateByQuery(index, type, payload, null, null, null);
    }

    /**
     * 根据ID删除数据
     *
     * @param index   索引名称集
     * @param type    文档类型集
     * @param id      数据ID
     * @param refresh refresh
     */
    public DocumentResult delete(String index, String type, String id, Boolean refresh) throws IOException {
        Delete.Builder builder = new Delete.Builder(id);
        builder.index(index);
        builder.type(type);
        if (refresh != null) {
            builder.refresh(refresh);
        }
        return jestClient.execute(builder.build());
    }

    /**
     * 根据ID删除数据
     *
     * @param index 索引名称集
     * @param type  文档类型集
     * @param id    数据ID
     */
    public DocumentResult delete(String index, String type, String id) throws IOException {
        return delete(index, type, id, null);
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param query      query
     * @param allow      是否允许不定义索引名称
     * @param ignore     忽略不可用的索引，这包括不存在或已关闭的索引
     * @param refresh    refresh
     */
    public JestResult deleteByQuery(
            Collection<String> indexNames,
            Collection<String> indexTypes,
            String query,
            Boolean allow,
            Boolean ignore,
            Boolean refresh) throws IOException {
        DeleteByQuery.Builder builder = new DeleteByQuery.Builder(query);
        builder.addIndices(indexNames);
        builder.addTypes(indexTypes);
        if (allow != null) {
            builder.allowNoIndices(allow);
        }
        if (ignore != null) {
            builder.ignoreUnavailable(ignore);
        }
        if (refresh != null) {
            builder.refresh(refresh);
        }
        return jestClient.execute(builder.build());
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param query      query
     * @param refresh    refresh
     */
    public JestResult deleteByQuery(Collection<String> indexNames, Collection<String> indexTypes, String query, Boolean refresh) throws IOException {
        return deleteByQuery(indexNames, indexTypes, query, null, null, refresh);
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param query      query
     */
    public JestResult deleteByQuery(Collection<String> indexNames, Collection<String> indexTypes, String query) throws IOException {
        return deleteByQuery(indexNames, indexTypes, query, null, null, null);
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param query      query
     * @param allow      是否允许不定义索引名称
     * @param ignore     忽略不可用的索引，这包括不存在或已关闭的索引
     * @param refresh    refresh
     */
    public JestResult deleteByQuery(
            ScriptObjectMirror indexNames,
            ScriptObjectMirror indexTypes,
            String query,
            Boolean allow,
            Boolean ignore,
            Boolean refresh) throws IOException {
        return deleteByQuery(scriptObjectToStrArray(indexNames), scriptObjectToStrArray(indexTypes), query, allow, ignore, refresh);
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param query      query
     * @param refresh    refresh
     */
    public JestResult deleteByQuery(ScriptObjectMirror indexNames, ScriptObjectMirror indexTypes, String query, Boolean refresh) throws IOException {
        return deleteByQuery(scriptObjectToStrArray(indexNames), scriptObjectToStrArray(indexTypes), query, null, null, refresh);
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param query      query
     */
    public JestResult deleteByQuery(ScriptObjectMirror indexNames, ScriptObjectMirror indexTypes, String query) throws IOException {
        return deleteByQuery(scriptObjectToStrArray(indexNames), scriptObjectToStrArray(indexTypes), query, null, null, null);
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param index   索引名称
     * @param type    文档类型
     * @param query   query
     * @param allow   是否允许不定义索引名称
     * @param ignore  忽略不可用的索引，这包括不存在或已关闭的索引
     * @param refresh refresh
     */
    public JestResult deleteByQuery(String index, String type, String query, Boolean allow, Boolean ignore, Boolean refresh) throws IOException {
        DeleteByQuery.Builder builder = new DeleteByQuery.Builder(query);
        builder.addIndex(index);
        builder.addType(type);
        if (allow != null) {
            builder.allowNoIndices(allow);
        }
        if (ignore != null) {
            builder.ignoreUnavailable(ignore);
        }
        if (refresh != null) {
            builder.refresh(refresh);
        }
        return jestClient.execute(builder.build());
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param index   索引名称
     * @param type    文档类型
     * @param query   query
     * @param refresh refresh
     */
    public JestResult deleteByQuery(String index, String type, String query, Boolean refresh) throws IOException {
        return deleteByQuery(index, type, query, null, null, refresh);
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param index 索引名称
     * @param type  文档类型
     * @param query query
     */
    public JestResult deleteByQuery(String index, String type, String query) throws IOException {
        return deleteByQuery(index, type, query, null, null, null);
    }

    /**
     * 搜索查询
     *
     * @param indexNames        索引名称集合
     * @param indexTypes        文档类型集合
     * @param query             query
     * @param includePattern    includePattern
     * @param excludePattern    excludePattern
     * @param sorts             sorts --> [ {field: 'fieldName', order: 'ASC/DESC'}, ...]
     * @param enableTrackScores enableTrackScores
     * @param allow             是否允许不定义索引名称
     * @param ignore            忽略不可用的索引，这包括不存在或已关闭的索引
     * @param refresh           refresh
     */
    public SearchResult search(
            Collection<String> indexNames,
            Collection<String> indexTypes,
            String query,
            String includePattern,
            String excludePattern,
            Collection<Map<String, String>> sorts,
            Boolean enableTrackScores,
            Boolean allow,
            Boolean ignore,
            Boolean refresh) throws IOException {
        Search.Builder builder = new Search.Builder(query);
        builder.addIndices(indexNames);
        builder.addTypes(indexTypes);
        // builder.setSearchType()
        if (StringUtils.isNotBlank(includePattern)) {
            builder.addSourceIncludePattern(includePattern);
        }
        if (StringUtils.isNotBlank(excludePattern)) {
            builder.addSourceExcludePattern(excludePattern);
        }
        if (enableTrackScores != null) {
            builder.enableTrackScores();
        }
        if (sorts != null && !sorts.isEmpty()) {
            builder.addSort(mapsToSorts(sorts));
        }
        if (allow != null) {
            builder.allowNoIndices(allow);
        }
        if (ignore != null) {
            builder.ignoreUnavailable(ignore);
        }
        if (refresh != null) {
            builder.refresh(refresh);
        }
        return jestClient.execute(builder.build());
    }

    /**
     * 搜索查询
     *
     * @param indexNames     索引名称集合
     * @param indexTypes     文档类型集合
     * @param query          query
     * @param includePattern includePattern
     * @param excludePattern excludePattern
     * @param sorts          sorts --> [ {field: 'fieldName', order: 'ASC/DESC'}, ...]
     */
    public SearchResult search(
            Collection<String> indexNames,
            Collection<String> indexTypes,
            String query,
            String includePattern,
            String excludePattern,
            Collection<Map<String, String>> sorts) throws IOException {
        return search(indexNames, indexTypes, query, includePattern, excludePattern, sorts, null, null, null, null);
    }

    /**
     * 搜索查询
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param query      query
     * @param sorts      sorts --> [ {field: 'fieldName', order: 'ASC/DESC'}, ...]
     */
    public SearchResult search(
            Collection<String> indexNames,
            Collection<String> indexTypes,
            String query,
            Collection<Map<String, String>> sorts) throws IOException {
        return search(indexNames, indexTypes, query, null, null, sorts, null, null, null, null);
    }

    /**
     * 搜索查询
     *
     * @param indexNames        索引名称集合
     * @param indexTypes        文档类型集合
     * @param query             query
     * @param includePattern    includePattern
     * @param excludePattern    excludePattern
     * @param sorts             sorts --> [ {field: 'fieldName', order: 'ASC/DESC'}, ...]
     * @param enableTrackScores enableTrackScores
     * @param allow             是否允许不定义索引名称
     * @param ignore            忽略不可用的索引，这包括不存在或已关闭的索引
     * @param refresh           refresh
     */
    public SearchResult search(
            ScriptObjectMirror indexNames,
            ScriptObjectMirror indexTypes,
            String query,
            String includePattern,
            String excludePattern,
            ScriptObjectMirror sorts,
            Boolean enableTrackScores,
            Boolean allow,
            Boolean ignore,
            Boolean refresh) throws IOException {
        return search(scriptObjectToStrArray(indexNames), scriptObjectToStrArray(indexTypes), query, includePattern, excludePattern, scriptObjectToMaps(sorts), enableTrackScores, allow, ignore, refresh);
    }

    /**
     * 搜索查询
     *
     * @param indexNames     索引名称集合
     * @param indexTypes     文档类型集合
     * @param query          query
     * @param includePattern includePattern
     * @param excludePattern excludePattern
     * @param sorts          sorts --> [ {field: 'fieldName', order: 'ASC/DESC'}, ...]
     */
    public SearchResult search(
            ScriptObjectMirror indexNames,
            ScriptObjectMirror indexTypes,
            String query,
            String includePattern,
            String excludePattern,
            ScriptObjectMirror sorts) throws IOException {
        return search(scriptObjectToStrArray(indexNames), scriptObjectToStrArray(indexTypes), query, includePattern, excludePattern, scriptObjectToMaps(sorts), null, null, null, null);
    }

    /**
     * 搜索查询
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param query      query
     * @param sorts      sorts --> [ {field: 'fieldName', order: 'ASC/DESC'}, ...]
     */
    public SearchResult search(
            ScriptObjectMirror indexNames,
            ScriptObjectMirror indexTypes,
            String query,
            ScriptObjectMirror sorts) throws IOException {
        return search(scriptObjectToStrArray(indexNames), scriptObjectToStrArray(indexTypes), query, null, null, scriptObjectToMaps(sorts), null, null, null, null);
    }

    /**
     * 搜索查询
     *
     * @param indexNames        索引名称
     * @param indexTypes        文档类型
     * @param query             query
     * @param includePattern    includePattern
     * @param excludePattern    excludePattern
     * @param sorts             sorts --> [ {field: 'fieldName', order: 'ASC/DESC'}, ...]
     * @param enableTrackScores enableTrackScores
     * @param allow             是否允许不定义索引名称
     * @param ignore            忽略不可用的索引，这包括不存在或已关闭的索引
     * @param refresh           refresh
     */
    public SearchResult search(
            String indexNames,
            String indexTypes,
            String query,
            String includePattern,
            String excludePattern,
            ScriptObjectMirror sorts,
            Boolean enableTrackScores,
            Boolean allow,
            Boolean ignore,
            Boolean refresh) throws IOException {
        return search(Collections.singletonList(indexNames), Collections.singletonList(indexTypes), query, includePattern, excludePattern, scriptObjectToMaps(sorts), enableTrackScores, allow, ignore, refresh);
    }

    /**
     * 搜索查询
     *
     * @param indexNames     索引名称
     * @param indexTypes     文档类型
     * @param query          query
     * @param includePattern includePattern
     * @param excludePattern excludePattern
     * @param sorts          sorts --> [ {field: 'fieldName', order: 'ASC/DESC'}, ...]
     */
    public SearchResult search(
            String indexNames,
            String indexTypes,
            String query,
            String includePattern,
            String excludePattern,
            ScriptObjectMirror sorts) throws IOException {
        return search(Collections.singletonList(indexNames), Collections.singletonList(indexTypes), query, includePattern, excludePattern, scriptObjectToMaps(sorts), null, null, null, null);
    }

    /**
     * 搜索查询
     *
     * @param indexNames 索引名称
     * @param indexTypes 文档类型
     * @param query      query
     * @param sorts      sorts --> [ {field: 'fieldName', order: 'ASC/DESC'}, ...]
     */
    public SearchResult search(
            String indexNames,
            String indexTypes,
            String query,
            ScriptObjectMirror sorts) throws IOException {
        return search(Collections.singletonList(indexNames), Collections.singletonList(indexTypes), query, null, null, scriptObjectToMaps(sorts), null, null, null, null);
    }

    /**
     * 根据ID获取数据
     *
     * @param index   索引名称
     * @param id      数据ID
     * @param refresh refresh
     */
    public DocumentResult get(String index, String id, Boolean refresh) throws IOException {
        Get.Builder builder = new Get.Builder(index, id);
        if (refresh != null) {
            builder.refresh(refresh);
        }
        return jestClient.execute(builder.build());
    }

    /**
     * 根据ID获取数据
     *
     * @param index 索引名称
     * @param id    数据ID
     */
    public DocumentResult get(String index, String id) throws IOException {
        return get(index, id, null);
    }

    /**
     * 根据ID集合获取数据
     *
     * @param index   索引名称
     * @param type    文档类型
     * @param ids     数据ID集合
     * @param refresh refresh
     */
    public JestResult multiGet(String index, String type, Collection<String> ids, Boolean refresh) throws IOException {
        MultiGet.Builder.ById builder = new MultiGet.Builder.ById(index, type);
        builder.addId(ids);
        if (refresh != null) {
            builder.refresh(refresh);
        }
        return jestClient.execute(builder.build());
    }

    /**
     * 根据ID集合获取数据
     *
     * @param index 索引名称
     * @param type  文档类型
     * @param ids   数据ID集合
     */
    public JestResult multiGet(String index, String type, Collection<String> ids) throws IOException {
        return multiGet(index, type, ids, null);
    }

    /**
     * 根据ID集合获取数据
     *
     * @param index   索引名称
     * @param type    文档类型
     * @param ids     数据ID集合
     * @param refresh refresh
     */
    public JestResult multiGet(String index, String type, ScriptObjectMirror ids, Boolean refresh) throws IOException {
        return multiGet(index, type, scriptObjectToStrArray(ids), refresh);
    }

    /**
     * 根据ID集合获取数据
     *
     * @param index 索引名称
     * @param type  文档类型
     * @param ids   数据ID集合
     */
    public JestResult multiGet(String index, String type, ScriptObjectMirror ids) throws IOException {
        return multiGet(index, type, scriptObjectToStrArray(ids), null);
    }

    // --------------------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> scriptObjectToMaps(ScriptObjectMirror sorts) {
        if (sorts == null) {
            return null;
        }
        if (!sorts.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (sorts.size() <= 0) {
            return Collections.emptyList();
        }
        List<Map<String, String>> result = new ArrayList<>(sorts.size());
        sorts.forEach((index, map) -> {
            if (!(map instanceof Map)) {
                throw new IllegalArgumentException("数组元素必须是一个对象，例如: {field: 'fieldName', order: 'ASC/DESC'}");
            }
            result.add((Map<String, String>) map);
        });
        return result;
    }

    private List<Sort> mapsToSorts(Collection<Map<String, String>> sorts) {
        if (sorts == null) {
            return null;
        }
        return sorts.stream().map(this::mapToSort).collect(Collectors.toList());
    }

    private Sort mapToSort(Map<String, String> sort) {
        if (sort == null) {
            return null;
        }
        String field = sort.get("field");
        String order = sort.get("order");
        Sort.Sorting sorting = null;
        if (order.equalsIgnoreCase("ASC")) {
            sorting = Sort.Sorting.ASC;
        } else if (order.equalsIgnoreCase("DESC")) {
            sorting = Sort.Sorting.DESC;
        }
        Sort result;
        if (sorting == null) {
            result = new Sort(field);
        } else {
            result = new Sort(field, sorting);
        }
        return result;
    }

    private List<String> scriptObjectToStrArray(ScriptObjectMirror scriptObjectMirror) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return Collections.emptyList();
        }
        List<String> array = new ArrayList<>(scriptObjectMirror.size());
        scriptObjectMirror.values().forEach(str -> {
            if (!(str instanceof String)) {
                throw new IllegalArgumentException("数组元素必须是字符串");
            }
            array.add((String) str);
        });
        return array;
    }
}
