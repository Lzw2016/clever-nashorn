package org.clever.nashorn.internal;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.objects.*;
import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.utils.ScriptEngineUtils;

import java.util.*;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/29 14:01 <br/>
 */
@Slf4j
public class TestInternal {
    // ------------------------------------------------------------------------------------------------------------------ test

    public static void test(Object param) {
        // jdk.nashorn.internal.objects.NativeDate
        log.info("----------> {} | {}", param == null ? "null" : param.getClass(), param);
    }

    // ------------------------------------------------------------------------------------------------------------------ java类型

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

    public static Date getDate() {
        return new Date();
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

    // ------------------------------------------------------------------------------------------------------------------ js Native 对象

    public static Object getNativeNumber() {
        Double val = 123.456D;
        return NativeNumber.constructor(true, val, val);
    }

    @SuppressWarnings("ConstantConditions")
    public static Object getNativeBoolean() {
        Boolean val = Boolean.FALSE;
        return NativeBoolean.constructor(true, val, val);
    }

    public static Object getNativeString() {
        String val = "A".toLowerCase();
        return NativeString.constructor(true, val, val);
    }

    public static Object getNativeDate() {
        double val = (double) new Date().getTime();
        return NativeDate.construct(true, val, val);
    }

    public static Object getNativeArray() {
        Object[] val = new String[]{"aaa", "bbb", "ccc"};
        return NativeArray.construct(true, val, val);
    }

    public static ScriptObjectMirror getNativeObject() {
        ScriptObjectMirror scriptObjectMirror = ScriptEngineUtils.newObject();
        scriptObjectMirror.put("int", 1);
        scriptObjectMirror.put("float", 1.1F);
        scriptObjectMirror.put("double", 1.3D);
        scriptObjectMirror.put("long", 123L);
        scriptObjectMirror.put("char", 'A');
        scriptObjectMirror.put("string", "aaa");
        scriptObjectMirror.put("boolean", false);
        return scriptObjectMirror;
    }
}
