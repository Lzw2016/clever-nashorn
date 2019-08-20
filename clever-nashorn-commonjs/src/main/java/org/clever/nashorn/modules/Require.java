package org.clever.nashorn.modules;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;

public class Require {

    // 在引擎范围内全局注册require函数
    public static Module enable(NashornScriptEngine engine, Folder folder) throws ScriptException {
        Bindings global = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        return enable(engine, folder, global);
    }

    // 在特定Bindings中注册require函数,它在重用时非常有用
    // 跨多个线程的相同脚本引擎(每个线程应该通过作为参数传递的Bindings定义自己的全局作用域)
    public static Module enable(NashornScriptEngine engine, Folder folder, Bindings bindings) throws ScriptException {
        Bindings module = engine.createBindings();
        ScriptObjectMirror exports = (ScriptObjectMirror) engine.eval("Object");

        Module created = new Module(engine, new MemoryModuleCache(), folder, module, exports);
        created.setLoaded();

        bindings.put("require", created);
        bindings.put("module", module);
        bindings.put("exports", exports);

        return created;
    }
}
