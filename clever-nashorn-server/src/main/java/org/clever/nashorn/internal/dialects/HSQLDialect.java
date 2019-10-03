package org.clever.nashorn.internal.dialects;

import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-10-03 13:03 <br/>
 */
public class HSQLDialect extends AbstractDialect {
    @Override
    public String doBuildPaginationSql(String originalSql, long offset, long limit, Map<String, Object> paramMap, String firstMark, String secondMark) {
        return originalSql + " limit " + (COLON + firstMark) + COMMA + (COLON + secondMark);
    }
}
