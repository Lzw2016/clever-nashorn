//package org.clever.nashorn.internal;
//
//import com.google.common.base.Joiner;
//import com.google.common.collect.Lists;
//import com.yvan.datax.script.ScriptUtil;
//import com.yvan.platform.Conv;
//
//import java.sql.Timestamp;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class JdbcUtils {
//
//    public static final DateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    private static final Pattern PATTERN = Pattern.compile("\\$\\{\\w+\\}");
//    private static final Pattern PATTERN2 = Pattern.compile("#\\{\\w+\\}");
//
//    public static SqlParam parseSqlParam(String sql, Object sqlSnippet, Object sqlParam) {
//
//        if (ScriptUtil.isCollection(sqlParam)) {
//            //参数是集合，不用变换sql语句
//            Collection<Object> listParams = ScriptUtil.toCollection(sqlParam);
//            Object[] params = new Object[0];
//            if (listParams.size() > 0) {
//                params = new Object[listParams.size()];
//                listParams.toArray(params);
//            }
//            return new SqlParam(sql, params);
//        }
//
//        //变换SQL语句
//        return renderSql(sql, ScriptUtil.toMap(sqlSnippet), ScriptUtil.toMap(sqlParam));
//    }
//
//    /**
//     * Sql解析引擎。
//     * 变量的形式有2种，一种是 ${sqlSnippet} 表达SQL语句片段，一种是 #{value} 表达SQL语句参数
//     * --
//     * ${sqlSnippet} 可以是任意SQL语句
//     * 示例：
//     * select * from table1 where ${where}
//     * 如果 ${where} 依然包含变量，会持续替换，一直替换到没有 ${var} 为止.
//     * --
//     * #{param} 是用来表达参数的
//     */
//    public static SqlParam renderSql(String template, Map<String, Object> sqlSnippet, Map<String, Object> sqlParamMap) {
//        //替换 ${var}
//        while (template.contains("${")) {
//            StringBuffer sb = new StringBuffer();
//            Matcher m = PATTERN.matcher(template);
//            while (m.find()) {
//                String param = m.group();
//                String keyName = param.substring(2, param.length() - 1);
//                String value = Conv.NS(sqlSnippet.get(keyName));
//                m.appendReplacement(sb, Matcher.quoteReplacement(value));
//            }
//            m.appendTail(sb);
//            template = sb.toString();
//        }
//
//        //替换 #{var}
//        StringBuffer sb = new StringBuffer();
//        Matcher m = PATTERN2.matcher(template);
//        List<Object> paramList = Lists.newArrayList();
//        while (m.find()) {
//            String keyNameRaw = m.group();
//            String keyName = keyNameRaw.substring(2, keyNameRaw.length() - 1);
//            Object param = sqlParamMap.get(keyName);
//
//            final String snip = parseSqlParam(param, paramList);
//            m.appendReplacement(sb, snip);
//        }
//        m.appendTail(sb);
//        Object[] paramArray = new Object[paramList.size()];
//        paramList.toArray(paramArray);
//        return new SqlParam(sb.toString(), paramArray);
//    }
//
//    private static String parseSqlParam(Object param, List<Object> paramList) {
//        if (param == null) {
//            paramList.add(null);
//            return "?";
//
//        } else if (param instanceof Collection) {
//            return repeatCollectionParam((Collection) param, paramList);
//
//        } else if (param.getClass().isArray()) {
//            return parseArrayParam((Object[]) param, paramList);
//
//        } else {
//            paramList.add(param);
//            return "?";
//        }
//    }
//
//    private static String parseArrayParam(Object[] source, List<Object> paramList) {
//        if (source.length <= 0) {
//            return "";
//        }
//
//        List<String> snipList = Lists.newArrayList();
//        for (Object param : source) {
//            snipList.add(parseSqlParam(param, paramList));
//        }
//        return "(" + Joiner.on(",").join(snipList) + ")";
//    }
//
//
//    private static String repeatCollectionParam(Collection source, List<Object> paramList) {
//        if (source.size() <= 0) {
//            return "";
//        }
//
//        List<String> snipList = Lists.newArrayList();
//        for (Object param : source) {
//            snipList.add(parseSqlParam(param, paramList));
//        }
//        return "(" + Joiner.on(",").join(snipList) + ")";
//    }
//
//    /**
//     * 修复 Timestamp 在 mysql-connector 中 setObjects 报空指针的异常
//     */
//    public static void fixParams(DbStmt dbStmt) {
//        if (dbStmt.getParam() != null && dbStmt.getParam().length > 0) {
//            for (Object[] params : dbStmt.getParam()) {
//                fixParams(params);
//            }
//        }
//    }
//
//    /**
//     * 修复 Timestamp 在 mysql-connector 中 setObjects 报空指针的异常
//     */
//    public static void fixParams(Object[] params) {
//        if (params != null && params.length > 0) {
//            for (int i = 0; i < params.length; i++) {
//                if (params[i] instanceof Timestamp) {
//                    Timestamp ts = (Timestamp) params[i];
//                    params[i] = SDF.format(ts);
//                }
//            }
//        }
//    }
//
//}
