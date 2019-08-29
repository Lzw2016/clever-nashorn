package org.clever.nashorn.internal;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.spring.SpringContextHolder;
import org.clever.nashorn.utils.ObjectConvertUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
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
}
