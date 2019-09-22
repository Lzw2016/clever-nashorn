package org.clever.nashorn.utils;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.clever.common.utils.exception.ExceptionUtils;

import javax.script.Bindings;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Collection;
import java.util.Date;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/21 09:26 <br/>
 */
public class ScriptEngineUtils {

    // 默认的 NashornScriptEngine
    private static final NashornScriptEngine Default_Engine = creatEngine();
    // 用于构造Js空对象 {}
    private static final ScriptObjectMirror Object_Constructor;
    // 用于构造Js数组
    private static final ScriptObjectMirror Array_Constructor;
    // 用于构造Js Date对象
    private static final ScriptObjectMirror Date_Constructor;
    // 用户构造Js Error 对象
    private static final ScriptObjectMirror Error_Constructor;
    // 用于解析JSON
    private static final ScriptObjectMirror Json_Constructor;

    static {
        try {
            Object_Constructor = (ScriptObjectMirror) Default_Engine.eval("Object");
            Array_Constructor = (ScriptObjectMirror) Default_Engine.eval("Array");
            Date_Constructor = (ScriptObjectMirror) Default_Engine.eval("Date");
            Error_Constructor = (ScriptObjectMirror) Default_Engine.eval("Error");
            Json_Constructor = (ScriptObjectMirror) Default_Engine.eval("JSON");
        } catch (ScriptException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static NashornScriptEngine getDefaultEngine() {
        return Default_Engine;
    }

    /**
     * 创建一个新的 NashornScriptEngine
     */
    public static NashornScriptEngine creatEngine() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        return (NashornScriptEngine) scriptEngineManager.getEngineByName("nashorn");
        // 支持ES6语法的 NashornScriptEngine
        // ScriptEngineManager sm = new ScriptEngineManager();
        // NashornScriptEngineFactory factory = null;
        // for (ScriptEngineFactory f : sm.getEngineFactories()) {
        //     if (f.getEngineName().equalsIgnoreCase("Oracle Nashorn")) {
        //         factory = (NashornScriptEngineFactory) f;
        //         break;
        //     }
        // }
        // String[] stringArray = new String[]{"-doe", "--language=es6"};
        // return factory.getScriptEngine(stringArray);
    }

    /**
     * 新建一个js Error对象
     */
    public static Bindings newError(Object... args) {
        return (Bindings) Error_Constructor.newObject(args);
    }

    /**
     * 新建一个js 普通对象
     */
    public static ScriptObjectMirror newObject(Object... args) {
        return (ScriptObjectMirror) Object_Constructor.newObject(args);
    }

    /**
     * 新建一个js 数组对象
     */
    public static ScriptObjectMirror newArray(Object... args) {
        return (ScriptObjectMirror) Array_Constructor.newObject(args);
    }

    /**
     * 新建一个js 数组对象
     */
    public static ScriptObjectMirror newArray(Collection args) {
        return (ScriptObjectMirror) Array_Constructor.newObject(args.toArray());
    }

    /**
     * 新建一个js Date对象
     */
    public static ScriptObjectMirror newDate(double timeStamp) {
        return (ScriptObjectMirror) Date_Constructor.newObject(timeStamp);
    }

    /**
     * 新建一个js Date对象
     */
    public static ScriptObjectMirror newDate(Date date) {
        return (ScriptObjectMirror) Date_Constructor.newObject((double) date.getTime());
    }

    /**
     * 解析Json成为 ScriptObjectMirror 对象
     */
    public static ScriptObjectMirror parseJson(String json) {
        return (ScriptObjectMirror) Json_Constructor.callMember("parse", json);
    }
}
