package org.clever.nashorn.config;

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.baomidou.mybatisplus.extension.plugins.SqlExplainInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.server.config.CustomPaginationInterceptor;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.cache.JsCodeFileCacheService;
import org.clever.nashorn.cache.MemoryJsCodeFileCache;
import org.clever.nashorn.entity.EnumConstant;
import org.clever.nashorn.folder.DatabaseFolder;
import org.clever.nashorn.folder.Folder;
import org.clever.nashorn.intercept.HttpRequestJsHandler;
import org.clever.nashorn.internal.CommonUtils;
import org.clever.nashorn.internal.Console;
import org.clever.nashorn.internal.LogConsole;
import org.clever.nashorn.module.cache.MemoryModuleCache;
import org.clever.nashorn.module.cache.ModuleCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2017-12-04 10:37 <br/>
 */
@Configuration
@Slf4j
public class BeanConfiguration {

    /**
     * 分页插件
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    @Bean
    public CustomPaginationInterceptor paginationInterceptor() {
        CustomPaginationInterceptor paginationInterceptor = new CustomPaginationInterceptor();
//        paginationInterceptor.setSqlParser()
//        paginationInterceptor.setDialectClazz()
//        paginationInterceptor.setOverflow()
//        paginationInterceptor.setProperties();
        return paginationInterceptor;
    }

    /**
     * 乐观锁插件<br />
     * 取出记录时，获取当前version <br />
     * 更新时，带上这个version <br />
     * 执行更新时， set version = yourVersion+1 where version = yourVersion <br />
     * 如果version不对，就更新失败 <br />
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

//    /**
//     * 逻辑删除<br />
//     */
//    @Bean
//    public ISqlInjector sqlInjector() {
//        return new LogicSqlInjector();
//    }

    /**
     * SQL执行效率插件
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    @Bean
    @Profile({"dev", "test"})
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
//        performanceInterceptor.setFormat(true);
//        performanceInterceptor.setMaxTime();
//        performanceInterceptor.setWriteInLog();
        return performanceInterceptor;
    }

    /**
     * 执行分析插件<br />
     * SQL 执行分析拦截器【 目前只支持 MYSQL-5.6.3 以上版本 】
     * 作用是分析 处理 DELETE UPDATE 语句
     * 防止小白或者恶意 delete update 全表操作！
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    @Bean
    @Profile({"dev", "test"})
    public SqlExplainInterceptor sqlExplainInterceptor() {
        SqlExplainInterceptor sqlExplainInterceptor = new SqlExplainInterceptor();
//        sqlExplainInterceptor.stopProceed
        return sqlExplainInterceptor;
    }

    @Bean("ScriptGlobalContext")
    public Map<String, Object> scriptContext() {
        Map<String, Object> context = new HashMap<>(1);
        context.put("CommonUtils", CommonUtils.Instance);
        return Collections.unmodifiableMap(context);
    }

    @Bean("HttpRequestJsHandler-ModuleCache")
    public ModuleCache moduleCache() {
        return new MemoryModuleCache();
    }

    @Bean("HttpRequestJsHandler-JsCodeFileCache")
    public MemoryJsCodeFileCache jsCodeFileCache(@Autowired JsCodeFileCacheService jsCodeFileCacheService) {
        return new MemoryJsCodeFileCache(1000 * 3600 * 2, jsCodeFileCacheService);
    }

    @Bean
    public HttpRequestJsHandler httpRequestJsHandler(
            @Autowired ObjectMapper objectMapper,
            @Autowired @Qualifier("HttpRequestJsHandler-ModuleCache") ModuleCache moduleCache,
            @Autowired @Qualifier("HttpRequestJsHandler-JsCodeFileCache") MemoryJsCodeFileCache jsCodeFileCache,
            @Autowired @Qualifier("ScriptGlobalContext") Map<String, Object> context
    ) {
        final String bizType = EnumConstant.DefaultBizType;
        final String groupName = EnumConstant.DefaultGroupName;
        // 初始化ScriptModuleInstance
        Folder rootFolder = new DatabaseFolder(bizType, groupName, jsCodeFileCache);
        Console console = new LogConsole(bizType, groupName, "/");
        ScriptModuleInstance scriptModuleInstance = new ScriptModuleInstance(rootFolder, moduleCache, console, context);
        return new HttpRequestJsHandler(bizType, groupName, objectMapper, jsCodeFileCache, scriptModuleInstance);
    }

    // TODO 需要删除
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setDaemon(true);
        return scheduler;
    }
}
