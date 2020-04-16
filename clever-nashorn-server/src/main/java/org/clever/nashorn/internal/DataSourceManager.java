package org.clever.nashorn.internal;

import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.spring.SpringContextHolder;
import org.clever.nashorn.config.GlobalConfig;
import org.clever.nashorn.config.MultipleDataSourceConfig;
import org.clever.nashorn.dto.response.HikariConfigRes;
import org.clever.nashorn.utils.MergeDataSourceConfig;
import org.springframework.beans.BeanUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DataSourceManager {

    public static final DataSourceManager Instance = new DataSourceManager();
    public static final String INSTANCE_NAME = "DataSourceManager";

    private MultipleDataSourceConfig defaultJdbcDataSourceConfig;

    private DataSourceManager(){

    }

    private void initDefaultJdbcDataSourceConfig(){
        GlobalConfig globalConfig = SpringContextHolder.getBean("globalConfig");
        if(globalConfig == null || globalConfig.getMultipleDataSource() == null){
            defaultJdbcDataSourceConfig = new MultipleDataSourceConfig();
        }else{
            defaultJdbcDataSourceConfig = globalConfig.getMultipleDataSource();
        }
    }

    public String addJdbcDataSource(Map<String,String> jdbcConfig) throws SQLException,ClassNotFoundException{

        if(defaultJdbcDataSourceConfig == null){
            initDefaultJdbcDataSourceConfig();
        }

        Map<String, DataSource> multipleJdbcDataSource = SpringContextHolder.getBean("MultipleDataSource");
        final String propNameStr = "name";
        String name;
        if(jdbcConfig.containsKey(propNameStr)){
            name = jdbcConfig.get(propNameStr);
        }else{
            throw new NullArgumentException("jdbc config name not set");
        }

        if(multipleJdbcDataSource.containsKey(name)){
            return "datasource " + name + "have already exist";
        }

        HikariConfig hikariConfig = createWithMap(name,jdbcConfig);
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        testConnection(hikariConfig);
        multipleJdbcDataSource.put(name,hikariDataSource);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> hikariDataSource.close()));

        return "success";
    }

    public Map<String, HikariConfigRes> listJdbcDataSourceConfig(){
        Map<String, DataSource> multipleJdbcDataSource = SpringContextHolder.getBean("MultipleDataSource");
        Map<String,HikariConfigRes> result = Maps.newHashMap();
        multipleJdbcDataSource.forEach((name, dataSource) -> {
            if (dataSource instanceof HikariDataSource) {
                HikariDataSource tmp = (HikariDataSource) dataSource;
                HikariConfigRes item =  new HikariConfigRes();
                BeanUtils.copyProperties(tmp,item);
                result.put(name,item);
            }
        });
        return result;
    }

    private HikariConfig createWithMap(String name,Map<String,String> jdbcConfig){

        String driverClassNamePropName = "driverClassName";
        String jdbcUrlPropName = "jdbcUrl";
        String usernamePropName = "username";
        String passwordPropName = "password";

        if(!jdbcConfig.containsKey(jdbcUrlPropName)
                || !jdbcConfig.containsKey(usernamePropName)
                || !jdbcConfig.containsKey(passwordPropName)){
            throw new NullArgumentException("jdbc config jdbc-url or username or password is not set,need all set");
        }

        HikariConfig hikariConfig = new HikariConfig();
        if(jdbcConfig.containsKey(driverClassNamePropName)){
            hikariConfig.setDriverClassName(jdbcConfig.get(driverClassNamePropName));
        }
        hikariConfig.setJdbcUrl(jdbcConfig.get(jdbcUrlPropName));
        hikariConfig.setUsername(jdbcConfig.get(usernamePropName));
        hikariConfig.setPassword(jdbcConfig.get(passwordPropName));

        final HikariConfig dataSourceGlobalConfig = defaultJdbcDataSourceConfig.getDataSourceGlobalConfig();
        hikariConfig.setPoolName(name);
        hikariConfig = MergeDataSourceConfig.mergeConfig(dataSourceGlobalConfig, hikariConfig);
        return hikariConfig;
    }

    /**
     * 测试连接Jdbc数据源
     */
    private void testConnection(HikariConfig hikariConfig) throws SQLException ,ClassNotFoundException{
        //注册驱动  告诉程序在用哪个数据库
        Class.forName(hikariConfig.getDriverClassName());
        //建立连接
        try (Connection connection = DriverManager.getConnection(hikariConfig.getJdbcUrl(), hikariConfig.getUsername(), hikariConfig.getPassword())) {
            if (!connection.isValid(5)) {
                throw new SQLException(String.format("连接数据库失败: [%s] %s", hikariConfig.getUsername(), hikariConfig.getPassword()));
            }
        }
    }
}
