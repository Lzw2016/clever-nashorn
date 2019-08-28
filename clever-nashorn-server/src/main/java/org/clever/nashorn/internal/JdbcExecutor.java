package org.clever.nashorn.internal;

import jdk.nashorn.internal.objects.NativeNumber;
import jdk.nashorn.internal.objects.NativeString;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * JDBC执行SQL支持
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/28 13:13 <br/>
 */
@Slf4j
public class JdbcExecutor {

    public static void test(Object param) {
        // jdk.nashorn.internal.objects.NativeDate
        log.info("----------> {} | {}", param == null ? "null" : param.getClass(), param);
    }

    public static byte getByte() {
        return 1;
    }

    public static short getShort() {
        return 2;
    }

    public static int getInt() {
        return 123;
    }

    public static long getLong() {
        return 456;
    }

    public static float getFloat() {
        return 123.456F;
    }

    public static double getDouble() {
        return 123.456D;
    }

    public static boolean getBoolean() {
        return true;
    }

    public static char getChar() {
        return 'A';
    }

    public static String getString() {
        return "aaa";
    }

    public static String[] getArray() {
        return new String[]{"aaa", "bbb", "ccc"};
    }

    public static List<String> getList() {
        return new ArrayList<String>() {{
            add("aaa");
            add("bbb");
            add("ccc");
        }};
    }

    public static Set<String> getSet() {
        return new HashSet<String>() {{
            add("aaa");
            add("bbb");
            add("ccc");
        }};
    }

    public static Map<String, Object> getMap() {
        return new HashMap<String, Object>() {{
            put("int", 1);
            put("float", 1.1F);
            put("double", 1.3D);
            put("long", 123L);
            put("char", 'A');
            put("string", "aaa");
            put("boolean", false);
        }};
    }

    // --------------------------------------------------------- js

    public static Object getNativeNumber() {
        double val = 123.456D;
        return NativeNumber.constructor(true, val, val);
    }

    public static Object getNativeString() {
        String val = "A".toLowerCase();
        return NativeString.constructor(true, val, val);
    }
}
