package org.clever.nashorn.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.common.model.response.AjaxMessage;
import org.clever.nashorn.cache.JsCodeFileCache;
import org.clever.nashorn.config.LettuceClientBuilder;
import org.clever.nashorn.module.cache.ModuleCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/18 13:14 <br/>
 */
@Api("管理接口")
@RequestMapping("/api/code_run_log")
@RestController
public class AdminController {
    @Autowired
    @Qualifier("HttpRequestJsHandler-ModuleCache")
    private ModuleCache moduleCache;
    @Autowired
    @Qualifier("HttpRequestJsHandler-JsCodeFileCache")
    private JsCodeFileCache jsCodeFileCache;
    @Autowired
    @Qualifier("MultipleDataSource")
    private Map<String, DataSource> multipleDataSource;
    @Autowired
    @Qualifier("MultipleRedis")
    private Map<String, LettuceClientBuilder> multipleRedis;

    @ApiOperation("清空HttpRequestJsHandler-ModuleCache缓存")
    @GetMapping("/http_js_handler_module_cache_clear")
    public AjaxMessage httpJsHandlerModuleCacheClear() {
        moduleCache.clear();
        return new AjaxMessage(true, "操作成功", "");
    }

    @ApiOperation("清空HttpRequestJsHandler-JsCodeFileCache缓存")
    @GetMapping("/http_js_handler_js_code_file_cache_reload")
    public AjaxMessage httpJsHandlerJsCodeFileCacheReload() {
        jsCodeFileCache.reload();
        return new AjaxMessage(true, "操作成功", "");
    }

    @ApiOperation("获取所有DataSource名称")
    @GetMapping("/multiple_data_source_name")
    public Set<String> getMultipleDataSourceName() {
        return multipleDataSource.keySet();
    }

    @ApiOperation("获取所有Redis数据源名称")
    @GetMapping("/multiple_redis_name")
    public Set<String> getMultipleRedisName() {
        return multipleRedis.keySet();
    }
}
