package org.clever.nashorn.internal;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.model.request.QueryBySort;
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
import java.sql.Connection;
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
    private static final String ASC = "ASC";
    private static final String DESC = "DESC";
    private static final String COMMA = ",";
    private static final int Max_PageSize = 1000;

    /**
     * Count Sql 缓存(最大1W条数据)
     */
    private static final Cache<String, String> Count_Sql_Cache = CacheBuilder.newBuilder().maximumSize(3000).initialCapacity(500).build();

    // TODO 事务控制 TransactionTemplate DataSourceTransactionManager
    private final NamedParameterJdbcTemplate jdbcTemplate;
    /**
     * 数据库类型
     */
    private final DbType dbType;

    /**
     * 新建一个JDBC数据库脚本执行器
     */
    public JdbcExecutor(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        // 设置游标读取数据时，单批次的数据读取量(值不能太大也不能太小)
        jdbcTemplate.getJdbcTemplate().setFetchSize(500);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            dbType = JdbcUtils.getDbType(connection.getMetaData().getURL());
        } catch (Throwable e) {
            throw new RuntimeException("读取数据库类型失败", e);
        } finally {
            if (connection != null) {
                org.springframework.jdbc.support.JdbcUtils.closeConnection(connection);
            }
        }
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

    /**
     * 分页查询(支持排序)，返回分页对象
     *
     * @param sql        sql脚本，参数格式[:param]
     * @param paramMap   参数，参数格式[:param] | { orderFields: [], sorts: [], fieldsMapping: { orderField: "sqlField"}, pageSize: 10, pageNo: 1}
     * @param countQuery 是否要执行count查询(可选)
     */
    public IPage<Map<String, Object>> queryByPage(String sql, Map<String, Object> paramMap, boolean countQuery) {
        // 读取排序 和 分页数据
        final List<String> orderFields = toStringArray(paramMap.get("orderFields"), "orderFields 必须是字符串数组");
        final List<String> sorts = toStringArray(paramMap.get("sorts"), "sorts 必须是字符串数组");
        final int pageSize = toInt(paramMap.get("pageSize"), 10, "pageSize 必须是Number型");
        final int pageNo = toInt(paramMap.get("pageNo"), 1, "pageNo 必须是Number型");
        QueryBySort queryBySort = new QueryBySort();
        queryBySort.setOrderFields(orderFields);
        queryBySort.setSorts(sorts);
        // 读取排序字段映射
        Object fieldsMappingObject = paramMap.get("fieldsMapping");
        if (fieldsMappingObject instanceof Map) {
            Map<?, ?> map = (Map) fieldsMappingObject;
            map.forEach((orderField, sqlField) -> {
                if (!(orderField instanceof String) || !(sqlField instanceof String)) {
                    throw new RuntimeException("fieldsMapping 必须是字符串类型Map");
                }
                queryBySort.addOrderFieldMapping((String) orderField, (String) sqlField);
            });
        }
        Page<Map<String, Object>> page = new Page<>(pageNo, Math.min(pageSize, Max_PageSize));
        // 执行 count 查询
        if (countQuery) {
            String countSql = Count_Sql_Cache.getIfPresent(StringUtils.trim(sql));
            if (StringUtils.isBlank(countSql)) {
                SqlInfo sqlInfo = SqlParserUtils.getOptimizeCountSql(true, null, sql);
                countSql = sqlInfo.getSql();
                Count_Sql_Cache.put(sql, countSql);
            }
            log.info("countSql --> \n {}", countSql);
            Long total = jdbcTemplate.queryForObject(countSql, paramMap, Long.class);
            if (total == null) {
                total = 0L;
            }
            page.setTotal(total);
            // 溢出总页数，设置最后一页
            long pages = page.getPages();
            if (page.getCurrent() > pages) {
                page.setCurrent(pages);
            }
        } else {
            page.setSearchCount(false);
            page.setTotal(-1);
        }
        // 构造排序以及分页sql
        String sortSql = concatOrderBy(sql, queryBySort);
        String pageSql = DialectFactory.buildPaginationSql(page, sortSql, paramMap, dbType, null);
        // 执行 pageSql
        paramMap = jsToJavaMap(paramMap);
        log.info("pageSql --> \n {}", pageSql);
        List<Map<String, Object>> listData = jdbcTemplate.queryForList(pageSql, paramMap);
        // 设置返回数据
        page.setRecords(listData);
        // 排序信息
        List<String> orderFieldsTmp = queryBySort.getOrderFieldsSql();
        List<String> sortsTmp = queryBySort.getSortsSql();
        for (int i = 0; i < orderFieldsTmp.size(); i++) {
            String fieldSql = orderFieldsTmp.get(i);
            String sort = sortsTmp.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setColumn(fieldSql);
            orderItem.setAsc(ASC.equalsIgnoreCase(StringUtils.trim(sort)));
            page.addOrder(orderItem);
        }
        return page;
    }

    /**
     * 分页查询(支持排序)，返回分页对象
     *
     * @param sql      sql脚本，参数格式[:param]
     * @param paramMap 参数，参数格式[:param] | { orderFields: [], sorts: [], fieldsMapping: { orderField: "sqlField"}, pageSize: 10, pageNo: 1}
     */
    public IPage<Map<String, Object>> queryByPage(String sql, Map<String, Object> paramMap) {
        return queryByPage(sql, paramMap, true);
    }

    /**
     * 查询SQL拼接Order By
     *
     * @param originalSql 需要拼接的SQL
     * @param queryBySort 排序对象
     * @return ignore
     */
    private static String concatOrderBy(String originalSql, QueryBySort queryBySort) {
        if (null != queryBySort && queryBySort.getOrderFields() != null && queryBySort.getOrderFields().size() > 0) {
            List<String> orderFields = queryBySort.getOrderFieldsSql();
            List<String> sorts = queryBySort.getSortsSql();
            StringBuilder buildSql = new StringBuilder(originalSql);
            StringBuilder orderBySql = new StringBuilder();
            for (int index = 0; index < orderFields.size(); index++) {
                String orderField = orderFields.get(index);
                if (orderField != null) {
                    orderField = orderField.trim();
                }
                if (orderField == null || orderField.length() <= 0) {
                    continue;
                }
                String sort = ASC;
                if (sorts.size() > index) {
                    sort = sorts.get(index);
                    if (sort != null) {
                        sort = sort.trim();
                    }
                    if (!DESC.equalsIgnoreCase(sort) && !ASC.equalsIgnoreCase(sort)) {
                        sort = ASC;
                    }
                }
                String orderByStr = concatOrderBuilder(orderField, sort.toUpperCase());
                if (StringUtils.isNotBlank(orderByStr)) {
                    if (orderBySql.length() > 0) {
                        orderBySql.append(COMMA).append(' ');
                    }
                    orderBySql.append(orderByStr.trim());
                }
            }
            if (orderBySql.length() > 0) {
                buildSql.append(" ORDER BY ").append(orderBySql.toString());
            }
            return buildSql.toString();
        }
        return originalSql;
    }

    /**
     * 拼接多个排序方法
     *
     * @param column    ignore
     * @param orderWord ignore
     */
    private static String concatOrderBuilder(String column, String orderWord) {
        if (StringUtils.isNotBlank(column)) {
            return column + ' ' + orderWord;
        }
        return StringUtils.EMPTY;
    }

    /**
     * 参数转 int
     *
     * @param object           参数
     * @param defaultInt       装换失败的默认值
     * @param exceptionMessage 异常消息
     */
    private static int toInt(Object object, int defaultInt, String exceptionMessage) {
        int result = defaultInt;
        if (object != null) {
            object = ObjectConvertUtils.Instance.jsBaseToJava(object);
            if (!(object instanceof Number)) {
                throw new RuntimeException(exceptionMessage);
            }
            result = ((Number) object).intValue();
        }
        return result;
    }

    /**
     * 参数转字符串数组集合
     *
     * @param object           参数
     * @param exceptionMessage 异常消息
     */
    private static List<String> toStringArray(Object object, String exceptionMessage) {
        List<String> result;
        if (object == null) {
            result = Collections.emptyList();
        } else if (object instanceof ScriptObjectMirror) {
            ScriptObjectMirror tmp = (ScriptObjectMirror) object;
            result = new ArrayList<>(tmp.size());
            tmp.forEach((index, field) -> {
                if (!(field instanceof String)) {
                    throw new RuntimeException(exceptionMessage);
                }
                result.add((String) field);
            });
        } else if (object instanceof List) {
            List<?> list = (List) object;
            result = new ArrayList<>(list.size());
            list.forEach(field -> {
                if (!(field instanceof String)) {
                    throw new RuntimeException(exceptionMessage);
                }
                result.add((String) field);
            });
        } else if (object instanceof String[]) {
            String[] array = (String[]) object;
            result = new ArrayList<>(array.length);
            Collections.addAll(result, array);
        } else {
            throw new RuntimeException(exceptionMessage);
        }
        return result;
    }

    /**
     * 把Js对象转换成Java Map(Sql 参数处理)
     */
    private static Map<String, Object> jsToJavaMap(Map<String, ?> paramMap) {
        if (paramMap == null || paramMap.size() <= 0) {
            return Collections.emptyMap();
        }
        Map<String, Object> javaMap = new HashMap<>(paramMap.size());
        paramMap.forEach((key, value) -> javaMap.put(key, ObjectConvertUtils.Instance.jsBaseToJava(value)));
        return javaMap;
    }
}
