package org.clever.nashorn.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.tuples.TupleTow;
import org.junit.Test;

import java.sql.*;
//import org.clever.common.utils.tuples.TupleTow;
//import org.clever.nashorn.internal.JdbcExecutor;
//import org.junit.Test;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/16 11:31 <br/>
 */
@Slf4j
public class JdbcExecutorTest {

//    @Test
//    public void t1() {
//        Map<String, Object> fields = new HashMap<>();
//        fields.put("storeProdNo", "12345");
//        fields.put("createAt", new Date());
//        Map<String, Object> whereMap = new HashMap<>();
//        whereMap.put("id", 1);
//        whereMap.put("erpNo", "666");
//        TupleTow<String, Map<String, Object>> tupleTow = JdbcExecutor.updateSql("tb_merchandise", fields, whereMap, true);
//        log.info("--> {}", tupleTow.getValue1());
//    }

//    @Test
//    public void t2() {
//        Map<String, Object> fields = new HashMap<>();
//        fields.put("storeProdNo", "12345");
//        fields.put("createAt", new Date());
//        TupleTow<String, Map<String, Object>> tupleTow = JdbcExecutor.insertSql("tb_merchandise", fields, true);
//        log.info("--> {}", tupleTow.getValue1());
//    }

    @Test
    public void t5() throws Exception {
        HikariConfig configuration = new HikariConfig();
        configuration.setPoolName("xxx");
        configuration.setDriverClassName("com.mysql.cj.jdbc.Driver");
        configuration.setJdbcUrl("jdbc:mysql://mysql.msvc.top:3306/clever-nashorn-demo?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convert_to_null&useSSL=false&useOldAliasMetadataBehavior=true");
        configuration.setUsername("clever-nashorn-demo");
        configuration.setPassword("demo");
        configuration.setMinimumIdle(3);
        configuration.setMaxLifetime(1000 * 60 * 30);
        configuration.setAutoCommit(false);
        configuration.setMaximumPoolSize(5);
        // useCursorFetch=true
        configuration.getDataSourceProperties().put("useCursorFetch", true);
        HikariDataSource dataSource = new HikariDataSource(configuration);
        Connection connection = dataSource.getConnection();

        String sql = "select * from merchandise limit 20000";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        preparedStatement.setFetchSize(1);
        long startTime = System.currentTimeMillis();
        TupleTow<Long, Long> endTime = new TupleTow<>(null, null);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            if (endTime.getValue1() == null) {
                endTime.setValue1(System.currentTimeMillis());
                log.info("### 耗时: --> {}ms", endTime.getValue1() - startTime); // 377ms
            } else {
                return;
            }
        }
        dataSource.close();
    }

    @Test
    public void testCursor2() throws SQLException {
        Connection connection = DriverManager
                .getConnection(
                        "jdbc:mysql://mysql.msvc.top:3306/clever-nashorn-demo?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&useOldAliasMetadataBehavior=true",
                        "clever-nashorn-demo",
                        "demo"
                );
        String sql = "select * from merchandise limit 20000";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        preparedStatement.setFetchSize(1);
        long startTime = System.currentTimeMillis();
        TupleTow<Long, Long> endTime = new TupleTow<>(null, null);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            if (endTime.getValue1() == null) {
                endTime.setValue1(System.currentTimeMillis());
                log.info("### 耗时: --> {}ms", endTime.getValue1() - startTime); // 88ms


            } else {
                return;
            }
        }
        connection.close();
    }

}
