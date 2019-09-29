package org.clever.nashorn.internal;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.spring.SpringContextHolder;
import org.clever.nashorn.utils.ObjectConvertUtils;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JDBC执行SQL支持
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/28 13:13 <br/>
 */
@Slf4j
public class JdbcExecutor {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcExecutor(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * @param sql
     * @param paramMap
     */
    public Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) {
        paramMap = jsToJavaMap(paramMap);
        return jdbcTemplate.queryForObject(sql, paramMap, new ColumnMapRowMapper());
    }

    /**
     * @param sql
     * @param paramMap
     */
    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) {
        paramMap = jsToJavaMap(paramMap);
        return jdbcTemplate.queryForList(sql, paramMap);
    }

    /**
     * TODO RowCallbackHandler 还需要封装
     *
     * @param sql
     * @param paramMap
     */
    public void query(String sql, Map<String, ?> paramMap, RowCallbackHandler rch) {
        paramMap = jsToJavaMap(paramMap);
        jdbcTemplate.query(sql, paramMap, rch);
    }

    // TODO 分页相关

    /**
     * 把Js对象转换成Java Map
     */
    private Map<String, Object> jsToJavaMap(Map<String, ?> paramMap) {
        if (paramMap == null || paramMap.size() <= 0) {
            return Collections.emptyMap();
        }
        Map<String, Object> javaMap = new HashMap<>(paramMap.size());
        paramMap.forEach((key, value) -> {
//            打印JS变量 | undefined jdk.nashorn.internal.runtime.Undefined | 行尾
//            打印JS变量 | null null | 行尾
//            打印JS变量 | int java.lang.Integer | 行尾
//            打印JS变量 | float java.lang.Double | 行尾
//            打印JS变量 | boolean java.lang.Boolean | 行尾
//            打印JS变量 | string java.lang.String | 行尾
//            打印JS变量 | date jdk.nashorn.api.scripting.ScriptObjectMirror | 行尾
//            打印JS变量 | array jdk.nashorn.api.scripting.ScriptObjectMirror | 行尾
//            打印JS变量 | object jdk.nashorn.api.scripting.ScriptObjectMirror | 行尾
//            打印JS变量 | function jdk.nashorn.api.scripting.ScriptObjectMirror | 行尾
            // if(value instanceof )
            // noinspection StreamToLoop,Convert2MethodRef
            javaMap.put(key, value);
        });
        return javaMap;
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * DataSource管理; Map<name, DataSource>
     */
    private static final ConcurrentHashMap<String, DataSource> DataSource_Map = new ConcurrentHashMap<>();

//    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public void t() {
//        jdbcTemplate.
    }

    private static final JdbcTemplate JDBC_TEMPLATE;

    static {
        JDBC_TEMPLATE = SpringContextHolder.getBean(JdbcTemplate.class);
    }

    public static Object query(String sql) {
//        return JDBC_TEMPLATE.queryForList(sql);
        List<Map<String, Object>> list = JDBC_TEMPLATE.queryForList(sql);
        return ObjectConvertUtils.Instance.javaToJSObject(list);
    }

    public static String getClass(Object object) {
        if (object == null) {
            return null;
        }
        return object.getClass().getName();
    }
}
