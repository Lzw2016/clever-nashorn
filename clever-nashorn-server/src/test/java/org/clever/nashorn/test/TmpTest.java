package org.clever.nashorn.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：lizw <br/>
 * 创建时间：2020/06/13 11:56 <br/>
 */
@Slf4j
public class TmpTest {

    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://192.168.31.40:3306/test?useSSL=false&serverTimezone=UTC";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "test";
    static final String PASS = "lizhiwei";

    @Test
    public void t01() throws SQLException, ClassNotFoundException {
        Connection conn = null;
        Statement stmt = null;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
            // 打开链接
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "select * from ta where id=11";
            stmt = conn.prepareStatement(sql);

            int count = 1000;
            Map<String, Object> row = new HashMap<>(4);

            long start = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                ResultSet rs = stmt.executeQuery(sql);
                // 展开结果集数据库
                if (rs.next()) {
                    // 通过字段检索
                    row.put("ID", rs.getLong("id"));
                    row.put("Text", rs.getString("text"));
                    row.put("CreateAt", rs.getDate("create_at"));
                    row.put("UpdateAt", rs.getDate("update_at"));
                }
                // 完成后关闭
                rs.close();
            }
            long end = System.currentTimeMillis();

            log.info("耗时: {}ms | --> {}", (end - start) * 1.0 / count, row);
        } finally {
            assert stmt != null;
            stmt.close();
            conn.close();
        }
    }
}
