package org.clever.nashorn.config;

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.baomidou.mybatisplus.extension.plugins.SqlExplainInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.searchbox.client.JestClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.server.config.CustomPaginationInterceptor;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.cache.JsCodeFileCache;
import org.clever.nashorn.cache.JsCodeFileCacheService;
import org.clever.nashorn.cache.MemoryJsCodeFileCache;
import org.clever.nashorn.canal.CanalMysqlSlave;
import org.clever.nashorn.entity.EnumConstant;
import org.clever.nashorn.folder.DatabaseFolder;
import org.clever.nashorn.folder.Folder;
import org.clever.nashorn.intercept.HttpRequestJsHandler;
import org.clever.nashorn.internal.*;
import org.clever.nashorn.module.cache.MemoryModuleCache;
import org.clever.nashorn.module.cache.ModuleCache;
import org.clever.nashorn.service.CodeRunLogService;
import org.clever.nashorn.utils.MergeDataSourceConfig;
import org.clever.nashorn.utils.MergeJestProperties;
import org.clever.nashorn.utils.MergeRedisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.elasticsearch.jest.HttpClientConfigBuilderCustomizer;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
        context.put("HttpUtils", HttpUtils.Instance);
        context.put("JdbcUtils", JdbcUtils.Instance);
        context.put("RedisUtils", RedisUtils.Instance);
        context.put("JestUtils", JestUtils.Instance);
        return Collections.unmodifiableMap(context);
    }

    @Bean("HttpRequestJsHandler-ModuleCache")
    public ModuleCache moduleCache() {
        return new MemoryModuleCache();
    }

    @Bean("HttpRequestJsHandler-JsCodeFileCache")
    public JsCodeFileCache jsCodeFileCache(@Autowired JsCodeFileCacheService jsCodeFileCacheService) {
        return new MemoryJsCodeFileCache(1000 * 3600 * 2, jsCodeFileCacheService);
    }

    @Bean("Global-ScriptModuleInstance")
    public ScriptModuleInstance scriptModuleInstance(
            @Autowired @Qualifier("HttpRequestJsHandler-ModuleCache") ModuleCache moduleCache,
            @Autowired @Qualifier("HttpRequestJsHandler-JsCodeFileCache") JsCodeFileCache jsCodeFileCache,
            @Autowired @Qualifier("ScriptGlobalContext") Map<String, Object> context
    ) {
        final String bizType = EnumConstant.DefaultBizType;
        final String groupName = EnumConstant.DefaultGroupName;
        // 初始化ScriptModuleInstance
        Folder rootFolder = new DatabaseFolder(bizType, groupName, jsCodeFileCache);
        Console console = new AllConsoleWrapper(bizType, groupName, "/");
        // 由于Spring bug导致需要这样获取 context
        if (context.get("ScriptGlobalContext") != null && context.get("ScriptGlobalContext") instanceof Map) {
            //noinspection unchecked
            context = (Map<String, Object>) context.get("ScriptGlobalContext");
        }
        return new ScriptModuleInstance(rootFolder, moduleCache, console, context);
    }

    @Bean
    public HttpRequestJsHandler httpRequestJsHandler(
            @Autowired ObjectMapper objectMapper,
            @Autowired @Qualifier("HttpRequestJsHandler-JsCodeFileCache") JsCodeFileCache jsCodeFileCache,
            @Autowired @Qualifier("Global-ScriptModuleInstance") ScriptModuleInstance scriptModuleInstance,
            @Autowired CodeRunLogService codeRunLogService
    ) {
        final String bizType = EnumConstant.DefaultBizType;
        final String groupName = EnumConstant.DefaultGroupName;
        return new HttpRequestJsHandler(bizType, groupName, objectMapper, jsCodeFileCache, scriptModuleInstance, codeRunLogService);
    }

    @Bean
    public CanalMysqlSlave canalMysqlSlave(
            @Autowired GlobalConfig globalConfig,
            @Autowired @Qualifier("Global-ScriptModuleInstance") ScriptModuleInstance scriptModuleInstance
    ) {
        MultipleCanalConfig multipleCanalConfig = globalConfig.getMultipleCanal();
        if (multipleCanalConfig == null) {
            multipleCanalConfig = new MultipleCanalConfig();
            globalConfig.setMultipleCanal(multipleCanalConfig);
        }
        // TODO Merge Config
        CanalMysqlSlave canalMysqlSlave = new CanalMysqlSlave(scriptModuleInstance, Collections.unmodifiableMap(multipleCanalConfig.getCanalConfigMap()));
        canalMysqlSlave.start();
        // 关闭 CanalMysqlSlave
        Runtime.getRuntime().addShutdownHook(new Thread(canalMysqlSlave::stop));
        return canalMysqlSlave;
    }

    @Bean("MultipleDataSource")
    public Map<String, DataSource> multipleDataSource(
            @Autowired GlobalConfig globalConfig,
            @Autowired(required = false) List<DataSource> dataSourceList) {
        MultipleDataSourceConfig multipleDataSource = globalConfig.getMultipleDataSource();
        if (multipleDataSource == null) {
            multipleDataSource = new MultipleDataSourceConfig();
            globalConfig.setMultipleDataSource(multipleDataSource);
        }
        int dataSourceCount = multipleDataSource.getDataSourceMap().size();
        if (dataSourceList != null) {
            dataSourceCount = dataSourceCount + dataSourceList.size();
        }
        final Map<String, DataSource> dataSourceMap = new HashMap<>(dataSourceCount);
        // 加入已存在的数据源
        if (dataSourceList != null) {
            for (DataSource dataSource : dataSourceList) {
                String name = null;
                if (dataSource instanceof HikariDataSource) {
                    HikariDataSource tmp = (HikariDataSource) dataSource;
                    name = tmp.getPoolName();
                }
                if (StringUtils.isBlank(name)) {
                    name = dataSource.toString();
                }
                if (dataSourceMap.containsKey(name)) {
                    throw new RuntimeException("DataSource 名称重复: " + name);
                }
                dataSourceMap.put(name, dataSource);
                if (StringUtils.isBlank(multipleDataSource.getDefaultDataSource())) {
                    multipleDataSource.setDefaultDataSource(name);
                }
            }
        }
        if (StringUtils.isBlank(multipleDataSource.getDefaultDataSource())) {
            throw new RuntimeException("默认的数据源名称 defaultDataSource 不能是空");
        }
        // 初始化配置的数据源
        final HikariConfig dataSourceGlobalConfig = multipleDataSource.getDataSourceGlobalConfig();
        multipleDataSource.getDataSourceMap().forEach((name, hikariConfig) -> {
            if (dataSourceMap.containsKey(name)) {
                throw new RuntimeException("DataSource 名称重复: " + name);
            }
            hikariConfig = MergeDataSourceConfig.mergeConfig(dataSourceGlobalConfig, hikariConfig);
            if (StringUtils.isBlank(hikariConfig.getPoolName())) {
                hikariConfig.setPoolName(name);
            }
            HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
            dataSourceMap.put(name, hikariDataSource);
        });
        final Map<String, DataSource> result = Collections.unmodifiableMap(dataSourceMap);
        // 关闭连接池
        Runtime.getRuntime().addShutdownHook(new Thread(() -> result.forEach((name, dataSource) -> {
            if (dataSource instanceof HikariDataSource) {
                HikariDataSource tmp = (HikariDataSource) dataSource;
                tmp.close();
            }
            // 其他类型的连接池也要关闭连接池
        })));
        return result;
    }

    @Bean("MultipleRedis")
    public Map<String, LettuceClientBuilder> multipleRedis(
            @Autowired ObjectMapper objectMapper,
            @Autowired GlobalConfig globalConfig,
            @Autowired(required = false) RedisConnectionFactory redisConnectionFactory) {
        MultipleRedisConfig multipleRedis = globalConfig.getMultipleRedis();
        if (multipleRedis == null) {
            multipleRedis = new MultipleRedisConfig();
            globalConfig.setMultipleRedis(multipleRedis);
        }
        int redisConfigCount = multipleRedis.getRedisConfigMap().size();
        if (redisConnectionFactory != null) {
            redisConfigCount = redisConfigCount + 1;
        }
        final Map<String, LettuceClientBuilder> redisConnectionFactoryMap = new HashMap<>(redisConfigCount);
        // 加入已存在的数据源
        if (redisConnectionFactory != null) {
            redisConnectionFactoryMap.put("spring-data-redis", new LettuceClientBuilder(redisConnectionFactory, objectMapper));
        }
        // 初始化配置的数据源
        final RedisProperties redisGlobalConfig = multipleRedis.getGlobalConfig();
        final Map<String, LettuceClientBuilder> destroyMap = new HashMap<>(multipleRedis.getRedisConfigMap().size());
        multipleRedis.getRedisConfigMap().forEach((name, redisConfig) -> {
            if (redisConnectionFactoryMap.containsKey(name)) {
                if ("spring-data-redis".equals(name)) {
                    throw new RuntimeException("redis-config-map 名称不能使用“spring-data-redis”");
                }
                throw new RuntimeException("redis-config-map 名称重复: " + name);
            }
            redisConfig = MergeRedisProperties.mergeConfig(redisGlobalConfig, redisConfig);
            LettuceClientBuilder lettuceClientBuilder = new LettuceClientBuilder(redisConfig, objectMapper);
            redisConnectionFactoryMap.put(name, lettuceClientBuilder);
            destroyMap.put(name, lettuceClientBuilder);
        });
        final Map<String, LettuceClientBuilder> result = Collections.unmodifiableMap(redisConnectionFactoryMap);
        // 关闭连接池
        Runtime.getRuntime().addShutdownHook(new Thread(() -> destroyMap.forEach((name, lettuceClientBuilder) -> {
            log.info("[" + name + "]Redis Connection Destroy start...");
            try {
                lettuceClientBuilder.destroy();
                log.info("[" + name + "]Redis Connection Destroy completed!");
            } catch (Throwable e) {
                log.info("[" + name + "]Redis Connection Destroy error", e);
            }
        })));
        return result;
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean("MultipleJest")
    public Map<String, JestClient> multipleJest(
            @Autowired GlobalConfig globalConfig,
            @Autowired(required = false) JestClient jestClient,
            @Autowired(required = false) Gson gson,
            @Autowired(required = false) List<HttpClientConfigBuilderCustomizer> builderCustomizers) {
        MultipleJestConfig multipleJest = globalConfig.getMultipleJest();
        if (multipleJest == null) {
            multipleJest = new MultipleJestConfig();
            globalConfig.setMultipleJest(multipleJest);
        }
        int jestConfigCount = multipleJest.getJestConfigMap().size();
        if (jestClient != null) {
            jestConfigCount = jestConfigCount + 1;
        }
        final Map<String, JestClient> jestClientMap = new HashMap<>(jestConfigCount);
        // 加入已存在的数据源
        if (jestClient != null) {
            jestClientMap.put("spring-data-jest", jestClient);
        }
        // 初始化配置的数据源
        final JestProperties jestGlobalConfig = multipleJest.getGlobalConfig();
        multipleJest.getJestConfigMap().forEach((name, jestConfig) -> {
            if (jestClientMap.containsKey(name)) {
                if ("spring-data-jest".equals(name)) {
                    throw new RuntimeException("jest-config-map 名称不能使用“spring-data-jest”");
                }
                throw new RuntimeException("jest-config-map 名称重复: " + name);
            }
            jestConfig = MergeJestProperties.mergeConfig(jestGlobalConfig, jestConfig);
            JestClientBuilder jestClientBuilder = new JestClientBuilder(jestConfig, gson, builderCustomizers);
            JestClient jestClientTmp = jestClientBuilder.builder();
            jestClientMap.put(name, jestClientTmp);
        });
        final Map<String, JestClient> result = Collections.unmodifiableMap(jestClientMap);
        // 关闭 JestClient
        Runtime.getRuntime().addShutdownHook(new Thread(() -> jestClientMap.forEach((name, jestClientTmp) -> {
            log.info("[" + name + "]JestClient Close start...");
            try {
                jestClientTmp.close();
                log.info("[" + name + "]JestClient Close completed!");
            } catch (Throwable e) {
                log.info("[" + name + "]JestClient Close error", e);
            }
        })));
        return result;
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
