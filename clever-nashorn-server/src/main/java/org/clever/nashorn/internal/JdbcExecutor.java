package org.clever.nashorn.internal;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.utils.ObjectConvertUtils;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC执行SQL支持
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/28 13:13 <br/>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@Slf4j
public class JdbcExecutor {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * 新建一个JDBC数据库脚本执行器
     */
    public JdbcExecutor(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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
        paramMap = jsToJavaMap(paramMap);
        jdbcTemplate.query(sql, paramMap, new RowCallbackHandlerJsCallback(callback));
    }

    /**
     * 查询多条数据(大量数据)，使用游标读取
     *
     * @param sql                sql脚本，参数格式[:param]
     * @param scriptObjectMirror 回调函数
     */
    public void query(String sql, ScriptObjectMirror scriptObjectMirror) {
        query(sql, Collections.emptyMap(), scriptObjectMirror);
    }

    // TODO 分页相关

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

    // ---------------------------------------------------------------------------------------------------------------------------------------------

    public static String getClass(Object object) {
        if (object == null) {
            return null;
        }
        return object.getClass().getName();
    }
}
