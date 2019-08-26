package org.clever.nashorn.utils;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.clever.common.utils.exception.ExceptionUtils;

import javax.script.Bindings;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/21 09:26 <br/>
 */
public class ScriptEngineUtils {

    // 默认的 NashornScriptEngine
    private static final NashornScriptEngine Default_Engine = creatEngine();
    // 用于构造Js空对象 {}
    private static final ScriptObjectMirror Object_Constructor;
    // 用户构造Js Error 对象
    private static final ScriptObjectMirror Error_Constructor;
    // 用于解析JSON
    private static final ScriptObjectMirror Json_Constructor;

    static {
        try {
            Object_Constructor = (ScriptObjectMirror) Default_Engine.eval("Object");
            Error_Constructor = (ScriptObjectMirror) Default_Engine.eval("Error");
            Json_Constructor = (ScriptObjectMirror) Default_Engine.eval("JSON");
        } catch (ScriptException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 创建一个新的 NashornScriptEngine
     */
    public static NashornScriptEngine creatEngine() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        return (NashornScriptEngine) scriptEngineManager.getEngineByName("nashorn");
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
     * 解析Json成为 ScriptObjectMirror 对象
     */
    public static ScriptObjectMirror parseJson(String json) {
        return (ScriptObjectMirror) Json_Constructor.callMember("parse", json);
    }
}
