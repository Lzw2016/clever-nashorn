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
@SuppressWarnings({"unused", "DuplicatedCode", "WeakerAccess", "deprecation"})
@Slf4j
public class JestExecutor {
    private final JestClient jestClient;

    public JestExecutor(JestClient jestClient) {
        if (jestClient == null) {
            throw new IllegalArgumentException("jestClient 不能为 null");
        }
        this.jestClient = jestClient;
    }

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
    public Map createIndex(
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
        JestResult jestResult = jestClient.execute(builder.build());
        return jestResult.getJsonMap();
    }

    /**
     * 创建索引
     *
     * @param index    索引名称
     * @param settings settings
     * @param mappings mappings
     * @param aliases  aliases
     */
    public Map createIndex(String index, Map<String, Object> settings, Map<String, Object> mappings, Map<String, Object> aliases) throws IOException {
        return createIndex(index, settings, mappings, aliases, null, null);
    }

    /**
     * 创建索引
     *
     * @param index    索引名称
     * @param settings settings
     * @param mappings mappings
     */
    public Map createIndex(String index, Map<String, Object> settings, Map<String, Object> mappings) throws IOException {
        return createIndex(index, settings, mappings, null, null, null);
    }

    /**
     * 创建索引
     *
     * @param index    索引名称
     * @param settings settings
     */
    public Map createIndex(String index, Map<String, Object> settings) throws IOException {
        return createIndex(index, settings, null, null, null, null);
    }

    /**
     * 删除索引
     *
     * @param index   索引名称
     * @param type    文档类型
     * @param refresh refresh
     */
    public Map deleteIndex(String index, String type, Boolean refresh) throws IOException {
        DeleteIndex.Builder builder = new DeleteIndex.Builder(index);
        if (StringUtils.isNotBlank(type)) {
            builder.type(type);
        }
        if (refresh != null) {
            builder.refresh(refresh);
        }
        JestResult jestResult = jestClient.execute(builder.build());
        return jestResult.getJsonMap();
    }

    /**
     * 删除索引
     *
     * @param index 索引名称
     * @param type  文档类型
     */
    public Map deleteIndex(String index, String type) throws IOException {
        return deleteIndex(index, type, null);
    }

    /**
     * 删除索引
     *
     * @param index 索引名称
     */
    public Map deleteIndex(String index) throws IOException {
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
    public Map saveOrUpdate(String index, String type, Object id, Map<String, Object> source, Boolean refresh) throws IOException {
        Index.Builder builder = new Index.Builder(source).index(index).type(type);
        if (id != null) {
            String idStr = String.valueOf(id);
            if (StringUtils.isNotBlank(idStr)) {
                builder.id(idStr);
            }
        }
        if (refresh != null) {
            builder.refresh(refresh);
        }
        DocumentResult documentResult = jestClient.execute(builder.build());
        return documentResult.getJsonMap();
    }

    /**
     * 新增或者更新数据
     *
     * @param index  索引名称
     * @param type   文档类型
     * @param id     文档ID
     * @param source 文档数据
     */
    public Map saveOrUpdate(String index, String type, Object id, Map<String, Object> source) throws IOException {
        return saveOrUpdate(index, type, id, source, null);
    }

    /**
     * 新增或者更新数据
     *
     * @param index  索引名称
     * @param type   文档类型
     * @param source 文档数据
     */
    public Map save(String index, String type, Map<String, Object> source) throws IOException {
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
    public Map update(String index, String type, Object id, Object payload, Boolean refresh) throws IOException {
        Update.Builder builder = new Update.Builder(payload);
        builder.index(index);
        builder.type(type);
        builder.id(String.valueOf(id));
        if (refresh != null) {
            builder.refresh(refresh);
        }
        DocumentResult documentResult = jestClient.execute(builder.build());
        return documentResult.getJsonMap();
    }

    /**
     * 使用update更新数据，文档：https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update.html
     *
     * @param index   索引名称
     * @param type    文档类型
     * @param id      数据id
     * @param payload payload
     */
    public Map update(String index, String type, Object id, Object payload) throws IOException {
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
    public Map updateByQuery(
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
        UpdateByQueryResult updateByQueryResult = jestClient.execute(builder.build());
        return updateByQueryResult.getJsonMap();
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param payload    payload
     * @param refresh    refresh
     */
    public Map updateByQuery(Collection<String> indexNames, Collection<String> indexTypes, Object payload, Boolean refresh) throws IOException {
        return updateByQuery(indexNames, indexTypes, payload, null, null, refresh);
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param payload    payload
     */
    public Map updateByQuery(Collection<String> indexNames, Collection<String> indexTypes, Object payload) throws IOException {
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
    public Map updateByQuery(
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
    public Map updateByQuery(ScriptObjectMirror indexNames, ScriptObjectMirror indexTypes, Object payload, Boolean refresh) throws IOException {
        return updateByQuery(scriptObjectToStrArray(indexNames), scriptObjectToStrArray(indexTypes), payload, null, null, refresh);
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param payload    payload
     */
    public Map updateByQuery(ScriptObjectMirror indexNames, ScriptObjectMirror indexTypes, Object payload) throws IOException {
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
    public Map updateByQuery(String index, String type, Object payload, Boolean allow, Boolean ignore, Boolean refresh) throws IOException {
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
        UpdateByQueryResult updateByQueryResult = jestClient.execute(builder.build());
        return updateByQueryResult.getJsonMap();
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param index   索引名称集
     * @param type    文档类型集
     * @param payload payload
     * @param refresh refresh
     */
    public Map updateByQuery(String index, String type, Object payload, Boolean refresh) throws IOException {
        return updateByQuery(index, type, payload, null, null, refresh);
    }

    /**
     * 根据查询更新数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update-by-query.html
     *
     * @param index   索引名称集
     * @param type    文档类型集
     * @param payload payload
     */
    public Map updateByQuery(String index, String type, Object payload) throws IOException {
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
    public Map deleteData(String index, String type, Object id, Boolean refresh) throws IOException {
        Delete.Builder builder = new Delete.Builder(String.valueOf(id));
        builder.index(index);
        builder.type(type);
        if (refresh != null) {
            builder.refresh(refresh);
        }
        DocumentResult documentResult = jestClient.execute(builder.build());
        return documentResult.getJsonMap();
    }

    /**
     * 根据ID删除数据
     *
     * @param index 索引名称集
     * @param type  文档类型集
     * @param id    数据ID
     */
    public Map deleteData(String index, String type, Object id) throws IOException {
        return deleteData(index, type, id, null);
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
    public Map deleteByQuery(
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
        JestResult jestResult = jestClient.execute(builder.build());
        return jestResult.getJsonMap();
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param query      query
     * @param refresh    refresh
     */
    public Map deleteByQuery(Collection<String> indexNames, Collection<String> indexTypes, String query, Boolean refresh) throws IOException {
        return deleteByQuery(indexNames, indexTypes, query, null, null, refresh);
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param query      query
     */
    public Map deleteByQuery(Collection<String> indexNames, Collection<String> indexTypes, String query) throws IOException {
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
    public Map deleteByQuery(
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
    public Map deleteByQuery(ScriptObjectMirror indexNames, ScriptObjectMirror indexTypes, String query, Boolean refresh) throws IOException {
        return deleteByQuery(scriptObjectToStrArray(indexNames), scriptObjectToStrArray(indexTypes), query, null, null, refresh);
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param indexNames 索引名称集合
     * @param indexTypes 文档类型集合
     * @param query      query
     */
    public Map deleteByQuery(ScriptObjectMirror indexNames, ScriptObjectMirror indexTypes, String query) throws IOException {
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
    public Map deleteByQuery(String index, String type, String query, Boolean allow, Boolean ignore, Boolean refresh) throws IOException {
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
        JestResult jestResult = jestClient.execute(builder.build());
        return jestResult.getJsonMap();
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param index   索引名称
     * @param type    文档类型
     * @param query   query
     * @param refresh refresh
     */
    public Map deleteByQuery(String index, String type, String query, Boolean refresh) throws IOException {
        return deleteByQuery(index, type, query, null, null, refresh);
    }

    /**
     * 根据查询删除数据 https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete-by-query.html
     *
     * @param index 索引名称
     * @param type  文档类型
     * @param query query
     */
    public Map deleteByQuery(String index, String type, String query) throws IOException {
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
    public Map search(
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
        SearchResult searchResult = jestClient.execute(builder.build());
        return searchResult.getJsonMap();
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
    public Map search(
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
    public Map search(
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
    public Map search(
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
    public Map search(
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
    public Map search(
            ScriptObjectMirror indexNames,
            ScriptObjectMirror indexTypes,
            String query,
            ScriptObjectMirror sorts) throws IOException {
        return search(scriptObjectToStrArray(indexNames), scriptObjectToStrArray(indexTypes), query, null, null, scriptObjectToMaps(sorts), null, null, null, null);
    }

    /**
     * 搜索查询
     *
     * @param index             索引名称
     * @param type              文档类型
     * @param query             query
     * @param includePattern    includePattern
     * @param excludePattern    excludePattern
     * @param sorts             sorts --> [ {field: 'fieldName', order: 'ASC/DESC'}, ...]
     * @param enableTrackScores enableTrackScores
     * @param allow             是否允许不定义索引名称
     * @param ignore            忽略不可用的索引，这包括不存在或已关闭的索引
     * @param refresh           refresh
     */
    public Map search(
            String index,
            String type,
            String query,
            String includePattern,
            String excludePattern,
            ScriptObjectMirror sorts,
            Boolean enableTrackScores,
            Boolean allow,
            Boolean ignore,
            Boolean refresh) throws IOException {
        return search(Collections.singletonList(index), Collections.singletonList(type), query, includePattern, excludePattern, scriptObjectToMaps(sorts), enableTrackScores, allow, ignore, refresh);
    }

    /**
     * 搜索查询
     *
     * @param index          索引名称
     * @param type           文档类型
     * @param query          query
     * @param includePattern includePattern
     * @param excludePattern excludePattern
     * @param sorts          sorts --> [ {field: 'fieldName', order: 'ASC/DESC'}, ...]
     */
    public Map search(
            String index,
            String type,
            String query,
            String includePattern,
            String excludePattern,
            ScriptObjectMirror sorts) throws IOException {
        return search(Collections.singletonList(index), Collections.singletonList(type), query, includePattern, excludePattern, scriptObjectToMaps(sorts), null, null, null, null);
    }

    /**
     * 搜索查询
     *
     * @param index 索引名称
     * @param type  文档类型
     * @param query query
     * @param sorts sorts --> [ {field: 'fieldName', order: 'ASC/DESC'}, ...]
     */
    public Map search(
            String index,
            String type,
            String query,
            ScriptObjectMirror sorts) throws IOException {
        return search(Collections.singletonList(index), Collections.singletonList(type), query, null, null, scriptObjectToMaps(sorts), null, null, null, null);
    }

    /**
     * 根据ID获取数据
     *
     * @param index   索引名称
     * @param id      数据ID
     * @param refresh refresh
     */
    public Map getData(String index, Object id, Boolean refresh) throws IOException {
        Get.Builder builder = new Get.Builder(index, String.valueOf(id));
        if (refresh != null) {
            builder.refresh(refresh);
        }
        DocumentResult documentResult = jestClient.execute(builder.build());
        return documentResult.getJsonMap();
    }

    /**
     * 根据ID获取数据
     *
     * @param index 索引名称
     * @param id    数据ID
     */
    public Map getData(String index, Object id) throws IOException {
        return getData(index, id, null);
    }

    /**
     * 根据ID集合获取数据
     *
     * @param index   索引名称
     * @param type    文档类型
     * @param ids     数据ID集合
     * @param refresh refresh
     */
    public Map multiGet(String index, String type, Collection<?> ids, Boolean refresh) throws IOException {
        MultiGet.Builder.ById builder = new MultiGet.Builder.ById(index, type);
        builder.addId(toStrList(ids));
        if (refresh != null) {
            builder.refresh(refresh);
        }
        JestResult jestResult = jestClient.execute(builder.build());
        return jestResult.getJsonMap();
    }

    /**
     * 根据ID集合获取数据
     *
     * @param index 索引名称
     * @param type  文档类型
     * @param ids   数据ID集合
     */
    public Map multiGet(String index, String type, Collection<Object> ids) throws IOException {
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
    public Map multiGet(String index, String type, ScriptObjectMirror ids, Boolean refresh) throws IOException {
        return multiGet(index, type, scriptObjectToStrArray(ids), refresh);
    }

    /**
     * 根据ID集合获取数据
     *
     * @param index 索引名称
     * @param type  文档类型
     * @param ids   数据ID集合
     */
    public Map multiGet(String index, String type, ScriptObjectMirror ids) throws IOException {
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

    public List<String> toStrList(Collection<?> ids) {
        if (ids == null) {
            return null;
        }
        return ids.stream().map(String::valueOf).collect(Collectors.toList());
    }
}
