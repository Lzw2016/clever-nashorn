//package org.clever.nashorn.internal;
//
//import java.util.List;
//import java.util.Map;
//
//public interface DbOperate {
//
//    Map<String, Object> queryOne(String sql, Object... params);
//
//    <T> T queryOne(Class<T> clazz, String sql, Object... params);
//
//    <T> List<Map<String, Object>> query(String sql, Object... params);
//
//    <T> List<T> query(Class<T> clazz, String sql, Object... params);
//
//    int execute(String sql, Object... params);
//
//    Object executeScalar(String sql, Object... params);
//
//    int[] executeBatch(DbStmt dbStmtArray);
//}
