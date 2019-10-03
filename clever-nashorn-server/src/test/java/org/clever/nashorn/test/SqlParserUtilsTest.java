package org.clever.nashorn.test;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-10-03 09:04 <br/>
 */
@Slf4j
public class SqlParserUtilsTest {

    // count 查询
    @Test
    public void test1() {
        String originalSql = "select * from merchandise where app_id=:app_id and cost_price>=:cost_price and create_at<:create_at order by update_at desc";
        SqlInfo sqlInfo = SqlParserUtils.getOptimizeCountSql(true, null, originalSql);
        log.info("-------> {}", sqlInfo.getSql());

        originalSql = "select * from merchandise where app_id=? and cost_price>=? and create_at<? order by update_at desc";
        sqlInfo = SqlParserUtils.getOptimizeCountSql(true, null, originalSql);
        log.info("-------> {}", sqlInfo.getSql());
    }

    @Test
    public void test2() {
        // 通过CacheBuilder构建一个缓存实例
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(100) // 设置缓存的最大容量
                .expireAfterWrite(1, TimeUnit.MINUTES) // 设置缓存在写入一分钟后失效
                .concurrencyLevel(10) // 设置并发级别为10
                .build();
        // 放入缓存
        cache.put("key", "value");
        // 获取缓存
        String value = cache.getIfPresent("key");
        for (int i = 0; i < 10000; i++) {
            cache.put("key" + i, "value" + i);
        }
        log.info("-------> {}", cache.size());
    }

    // page 查询
    @Test
    public void test3() {
        String originalSql = "select * from merchandise where app_id=:app_id and cost_price>=:cost_price and create_at<:create_at order by update_at desc";
        IPage<Map<String, Object>> page = new Page<>(1, 10);
        DialectModel model = DialectFactory.buildPaginationSql(page, originalSql, DbType.MYSQL, null);
        log.info("-------> {}", model.getDialectSql());

        String sql = org.clever.nashorn.internal.DialectFactory.buildPaginationSql(page, originalSql, new HashMap<String, Object>() {{
            put("first_mark", "12");
        }}, DbType.MYSQL, null);
        log.info("-------> {}", sql);
    }
}
