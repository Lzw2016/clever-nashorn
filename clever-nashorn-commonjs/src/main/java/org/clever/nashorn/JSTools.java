package org.clever.nashorn;

import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.PropertyAccess;
import org.clever.nashorn.folder.ResourceFolder;
import org.clever.nashorn.internal.LogConsole;
import org.clever.nashorn.module.cache.MemoryModuleCache;
import org.clever.nashorn.utils.ScriptEngineUtils;

import javax.script.Bindings;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/26 12:01 <br/>
 */
public class JSTools {

    /**
     * 系统自带的默认实例
     */
    public static final ScriptModuleInstance Instance;

    public static final ScriptObjectMirror JSToolsCode;

    static {
        Instance = new ScriptModuleInstance(
                ResourceFolder.create(ScriptEngineUtils.class.getClassLoader(), "javascript", "UTF-8"),
                new MemoryModuleCache(1000 * 60),
                new LogConsole("/resources")
        );
        JSToolsCode = Instance.useJs("./JSToolsCode.js");
    }

    /**
     * 使用Json序列化 JS 对象(解除了循环依赖问题)
     *
     * @param object JS 对象
     */
    public static String inspect(JSObject object) {
        Object res = JSToolsCode.callMember("inspect", object);
        return String.valueOf(res);
    }

    /**
     * 使用Json序列化 JS 对象(解除了循环依赖问题)
     *
     * @param object JS 对象
     */
    public static String inspect(Bindings object) {
        Object res = JSToolsCode.callMember("inspect", object);
        return String.valueOf(res);
    }

    /**
     * 使用Json序列化 JS 对象(解除了循环依赖问题)
     *
     * @param object JS 对象
     */
    public static String inspect(PropertyAccess object) {
        Object res = JSToolsCode.callMember("inspect", object);
        return String.valueOf(res);
    }

    /**
     * 使用Json序列化 JS 对象(存在循环依赖问题)
     *
     * @param object JS 对象
     */
    public static String stringify(JSObject object) {
        Object res = JSToolsCode.callMember("stringify", object);
        return String.valueOf(res);
    }

    /**
     * 使用Json序列化 JS 对象(存在循环依赖问题)
     *
     * @param object JS 对象
     */
    public static String stringify(Bindings object) {
        Object res = JSToolsCode.callMember("stringify", object);
        return String.valueOf(res);
    }

    /**
     * 使用Json序列化 JS 对象(存在循环依赖问题)
     *
     * @param object JS 对象
     */
    public static String stringify(PropertyAccess object) {
        Object res = JSToolsCode.callMember("stringify", object);
        return String.valueOf(res);
    }
}
