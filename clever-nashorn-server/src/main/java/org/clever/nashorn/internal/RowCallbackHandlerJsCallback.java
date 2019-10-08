package org.clever.nashorn.internal;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/30 17:56 <br/>
 */
@SuppressWarnings("WeakerAccess")
public class RowCallbackHandlerJsCallback extends RowCountCallbackHandler {
    /**
     * JS 回调函数
     */
    private final ScriptObjectMirror callback;

    public RowCallbackHandlerJsCallback(ScriptObjectMirror callback) {
        if (callback == null || !callback.isFunction()) {
            throw new RuntimeException("callback 必须是一个Js函数对象");
        }
        this.callback = callback;
    }

    @SuppressWarnings("ConstantConditions")
    protected void processRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Object> metaData = new HashMap<>(4);
        metaData.put("rowNum", rowNum);
        metaData.put("columnNames", getColumnNames());
        metaData.put("columnTypes", getColumnTypes());
        metaData.put("columnCount", getColumnCount());
        Map<String, Object> rowData = new LinkedCaseInsensitiveMap<>(getColumnCount());
        for (int i = 0; i < getColumnCount(); i++) {
            String column = getColumnNames()[i];
            rowData.putIfAbsent(column, getColumnValue(rs, i + 1));
        }
        callback.call(metaData, rowData, metaData);
    }

    private Object getColumnValue(ResultSet rs, int index) throws SQLException {
        return org.springframework.jdbc.support.JdbcUtils.getResultSetValue(rs, index);
    }
}
