//package org.clever.nashorn.internal;
//
//import lombok.SneakyThrows;
//import org.apache.commons.dbutils.QueryRunner;
//import org.apache.commons.dbutils.handlers.BeanHandler;
//import org.apache.commons.dbutils.handlers.MapHandler;
//import org.apache.commons.dbutils.handlers.ScalarHandler;
//
//import java.sql.Connection;
//import java.util.List;
//import java.util.Map;
//
//public class DbOperateConn implements DbOperate, AutoCloseable {
//
//    private final Connection conn;
//
//    DbOperateConn(Connection connection) {
//        this.conn = connection;
//    }
//
//    @SneakyThrows
//    public Map<String, Object> queryOne(String sql, Object... params) {
//        QueryRunner run = new QueryRunner();
//        if (params == null) {
//            return run.query(conn, sql, new MapHandler());
//        } else {
//            JdbcUtils.fixParams(params);
//            return run.query(conn, sql, new MapHandler(), params);
//        }
//    }
//
//    @SneakyThrows
//    public <T> T queryOne(Class<T> clazz, String sql, Object... params) {
//        QueryRunner run = new QueryRunner();
//        if (params == null) {
//            return run.query(conn, sql, new BeanHandler<T>(clazz));
//        } else {
//            JdbcUtils.fixParams(params);
//            return run.query(conn, sql, new BeanHandler<T>(clazz), params);
//        }
//    }
//
//    @SneakyThrows
//    public <T> List<Map<String, Object>> query(String sql, Object... params) {
//        QueryRunner run = new QueryRunner();
//        if (params == null) {
//            return run.execute(conn, sql, new MapHandler());
//        } else {
//            JdbcUtils.fixParams(params);
//            return run.execute(conn, sql, new MapHandler(), params);
//        }
//    }
//
//    @SneakyThrows
//    public <T> List<T> query(Class<T> clazz, String sql, Object... params) {
//        QueryRunner run = new QueryRunner();
//        if (params == null) {
//            return run.execute(conn, sql, new BeanHandler<T>(clazz));
//        } else {
//            JdbcUtils.fixParams(params);
//            return run.execute(conn, sql, new BeanHandler<T>(clazz), params);
//        }
//    }
//
//    @SneakyThrows
//    public int execute(String sql, Object... params) {
//        QueryRunner run = new QueryRunner();
//        if (params == null) {
//            return run.update(conn, sql);
//        } else {
//            JdbcUtils.fixParams(params);
//            return run.update(conn, sql, params);
//        }
//    }
//
//    @SneakyThrows
//    public Object executeScalar(String sql, Object... params) {
//        QueryRunner run = new QueryRunner();
//        if (params == null) {
//            return run.query(conn, sql, new ScalarHandler<>());
//        } else {
//            JdbcUtils.fixParams(params);
//            return run.query(conn, sql, new ScalarHandler<>(), params);
//        }
//    }
//
//    @SneakyThrows
//    @Override
//    public int[] executeBatch(DbStmt dbStmt) {
//        QueryRunner run = new QueryRunner();
//        JdbcUtils.fixParams(dbStmt);
//        return run.batch(conn, dbStmt.getSql(), dbStmt.getParam());
//    }
//
//    @SneakyThrows
//    public void commit() {
//        conn.commit();
//    }
//
//    @SneakyThrows
//    public void rollback() {
//        conn.rollback();
//    }
//
//    @SneakyThrows
//    public void close() {
//        conn.close();
//    }
//}
