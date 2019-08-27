//package org.clever.nashorn.internal;
//
//import java.util.Arrays;
//import java.util.Objects;
//
//public class SqlParam {
//
//    private String sql;
//    private Object[] param;
//
//    public SqlParam(String sql, Object[] param) {
//        this.sql = sql;
//        this.param = param;
//    }
//
//    @Override
//    public String toString() {
//        return "SqlParam{" +
//                "sql='" + sql + '\'' +
//                ", param=" + Arrays.toString(param) +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        SqlParam sqlParam = (SqlParam) o;
//        return Objects.equals(sql, sqlParam.sql) &&
//                Arrays.equals(param, sqlParam.param);
//    }
//
//    @Override
//    public int hashCode() {
//        int result = Objects.hash(sql);
//        result = 31 * result + Arrays.hashCode(param);
//        return result;
//    }
//
//    public String getSql() {
//        return sql;
//    }
//
//    public Object[] getParam() {
//        return param;
//    }
//
//    public void setSql(String sql) {
//        this.sql = sql;
//    }
//
//    public void setParam(Object[] param) {
//        this.param = param;
//    }
//}
