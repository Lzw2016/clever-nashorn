package org.clever.nashorn.internal;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.utils.ObjectConvertUtils;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.util.*;

/**
 * JDBC执行SQL支持
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/28 13:13 <br/>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@Slf4j
public class JdbcExecutor {
    // TODO 事务控制 TransactionTemplate DataSourceTransactionManager
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * 新建一个JDBC数据库脚本执行器
     */
    public JdbcExecutor(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        // 设置游标读取数据时，单批次的数据读取量(值不能太大也不能太小)
        jdbcTemplate.getJdbcTemplate().setFetchSize(500);
    }

    /**
     * 查询一条数据，返回一个Map
     *
     * @param sql      sql脚本，参数格式[:param]
     * @param paramMap 参数(可选)，参数格式[:param]
     */
    public Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) {
        paramMap = jsToJavaMap(paramMap);
        return jdbcTemplate.queryForObject(sql, paramMap, new ColumnMapRowMapper());
    }

    /**
     * 查询一条数据，返回一个Map
     *
     * @param sql sql脚本，参数格式[:param]
     */
    public Map<String, Object> queryForMap(String sql) {
        return jdbcTemplate.queryForObject(sql, Collections.emptyMap(), new ColumnMapRowMapper());
    }

    /**
     * 查询多条数据，返回一个Map数组
     *
     * @param sql      sql脚本，参数格式[:param]
     * @param paramMap 参数(可选)，参数格式[:param]
     */
    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) {
        paramMap = jsToJavaMap(paramMap);
        return jdbcTemplate.queryForList(sql, paramMap);
    }

    /**
     * 查询多条数据，返回一个Map数组
     *
     * @param sql sql脚本，参数格式[:param]
     */
    public List<Map<String, Object>> queryForList(String sql) {
        return jdbcTemplate.queryForList(sql, Collections.emptyMap());
    }

    /**
     * 查询多条数据(大量数据)，使用游标读取
     *
     * @param sql                sql脚本，参数格式[:param]
     * @param paramMap           参数(可选)，参数格式[:param]
     * @param scriptObjectMirror 回调函数
     */
    public void query(String sql, Map<String, ?> paramMap, ScriptObjectMirror scriptObjectMirror) {
        if (scriptObjectMirror == null) {
            throw new RuntimeException("没有回调函数");
        }
        ScriptObjectMirror callback = null;
        if (scriptObjectMirror.isFunction()) {
            callback = scriptObjectMirror;
        } else {
            Object tmp = scriptObjectMirror.get("callback");
            if (tmp instanceof ScriptObjectMirror && ((ScriptObjectMirror) tmp).isFunction()) {
                callback = (ScriptObjectMirror) tmp;
            }
        }
        if (callback == null) {
            throw new RuntimeException("没有回调函数");
        }
        RowCallbackHandler rowCallbackHandler = new RowCallbackHandlerJsCallback(callback);
        if (paramMap == null) {
            jdbcTemplate.query(sql, rowCallbackHandler);
        } else {
            paramMap = jsToJavaMap(paramMap);
            jdbcTemplate.query(sql, paramMap, rowCallbackHandler);
        }
    }

    /**
     * 查询多条数据(大量数据)，使用游标读取
     *
     * @param sql                sql脚本，参数格式[:param]
     * @param scriptObjectMirror 回调函数
     */
    public void query(String sql, ScriptObjectMirror scriptObjectMirror) {
        query(sql, null, scriptObjectMirror);
    }

    /**
     * 执行更新SQL，返回更新影响数据量
     *
     * @param sql      sql脚本，参数格式[:param]
     * @param paramMap 参数(可选)，参数格式[:param]
     */
    public int update(String sql, Map<String, ?> paramMap) {
        paramMap = jsToJavaMap(paramMap);
        return jdbcTemplate.update(sql, paramMap);
    }

    /**
     * 执行更新SQL，返回更新影响数据量
     *
     * @param sql sql脚本，参数格式[:param]
     */
    public int update(String sql) {
        return jdbcTemplate.update(sql, Collections.emptyMap());
    }

    /**
     * 执行insert SQL，返回数据库自增主键值和新增数据量
     *
     * @param sql      sql脚本，参数格式[:param]
     * @param paramMap 参数(可选)，参数格式[:param]
     */
    public Map<String, Object> insert(String sql, Map<String, ?> paramMap) {
        SqlParameterSource sqlParameterSource;
        if (paramMap != null && paramMap.size() > 0) {
            paramMap = jsToJavaMap(paramMap);
            sqlParameterSource = new MapSqlParameterSource(paramMap);
        } else {
            sqlParameterSource = new EmptySqlParameterSource();
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int insertCount = jdbcTemplate.update(sql, sqlParameterSource, keyHolder);
        Map<String, Object> result = new HashMap<>(3);
        Map<String, Object> keyHolderMap = new HashMap<>(3);
        List<Map<String, Object>> keysList = keyHolder.getKeyList();
        keyHolderMap.put("keysList", keysList);
        if (keysList.size() == 1) {
            Map<String, Object> keys = keysList.get(0);
            keyHolderMap.put("keys", keys);
            if (keys.size() == 1) {
                Iterator<Object> keyIter = keys.values().iterator();
                if (keyIter.hasNext()) {
                    Object key = keyIter.next();
                    keyHolderMap.put("key", key);
                    result.put("keyHolderValue", key);
                }
            }
        }
        result.put("insertCount", insertCount);
        result.put("keyHolder", keyHolderMap);
        return result;
    }

    /**
     * 执行insert SQL，返回数据库自增主键值和新增数据量
     *
     * @param sql sql脚本，参数格式[:param]
     */
    public Map<String, Object> insert(String sql) {
        return insert(sql, null);
    }

    /**
     * 批量执行更新SQL，返回更新影响数据量
     *
     * @param sql           sql脚本，参数格式[:param]
     * @param arrayParamMap 参数数组，参数格式[:param]
     */
    public int[] batchUpdate(String sql, ScriptObjectMirror arrayParamMap) {
        if (arrayParamMap == null) {
            throw new RuntimeException("参数不能为空");
        }
        if (!arrayParamMap.isArray()) {
            throw new RuntimeException("参数必须是一个数组");
        }
        List<SqlParameterSource> paramMapList = new ArrayList<>(arrayParamMap.size());
        arrayParamMap.forEach((index, map) -> {
            if (!(map instanceof Map)) {
                throw new RuntimeException("数组项必须是一个对象，不能是基本类型变量");
            }
            // noinspection unchecked
            paramMapList.add(new MapSqlParameterSource(jsToJavaMap((Map<String, ?>) map)));
        });
        return jdbcTemplate.batchUpdate(sql, paramMapList.toArray(new SqlParameterSource[0]));
    }

    // TODO 查询的智能分页、排序

    /**
     * 把Js对象转换成Java Map(Sql 参数处理)
     */
    private Map<String, Object> jsToJavaMap(Map<String, ?> paramMap) {
        if (paramMap == null || paramMap.size() <= 0) {
            return Collections.emptyMap();
        }
        Map<String, Object> javaMap = new HashMap<>(paramMap.size());
        paramMap.forEach((key, value) -> javaMap.put(key, ObjectConvertUtils.Instance.jsBaseToJava(value)));
        return javaMap;
    }
}
