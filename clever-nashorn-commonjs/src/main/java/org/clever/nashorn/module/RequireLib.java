package org.clever.nashorn.module;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.lang3.StringUtils;
import org.clever.nashorn.folder.Folder;
import org.clever.nashorn.module.cache.ModuleCache;
import org.clever.nashorn.tuples.Tuple3;
import org.clever.nashorn.utils.ScriptEngineUtils;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;

/**
 * 用于加载第三方依赖库(如：lodash、Underscore、等等)
 * <p>
 * 作者： lzw<br/>
 * 创建时间：2019-09-04 21:22 <br/>
 */
public class RequireLib implements RequireLibFunction, CompileModule {

    private final Folder folder;
    private final ModuleCache moduleCache;

    RequireLib(Folder folder, ModuleCache moduleCache) {
        this.folder = folder;
        this.moduleCache = moduleCache;
    }

    @Override
    public ScriptObjectMirror requireLib(String module) throws ScriptException, NashornException {
        Tuple3<String[], String, Folder> tuple3 = InnerUtils.resolvedFolder(module, folder);
        String[] folderParts = tuple3.getValue1();
        String filename = tuple3.getValue2();
        Folder resolvedFolder = tuple3.getValue3();
        // 寻找并加载 Module
        Module found = InnerUtils.loadModule(module, folderParts, filename, resolvedFolder, folder, moduleCache, this);
        return found.getExports();
    }

    @Override
    public Module compileJsonModule(Folder path, String filename, String scriptCode) {
        ScriptObjectMirror exports = ScriptEngineUtils.parseJson(scriptCode);
        return Module.creatLibModule(exports);
    }

    @Override
    public Module compileJavaScriptModule(Folder path, String filename, String scriptCode) throws ScriptException {
        if (StringUtils.isBlank(scriptCode)) {
            return null;
        }
        final NashornScriptEngine engine = ScriptEngineUtils.creatEngine();
        String fullPath = path.getFilePath(filename);
        String previousFilename = (String) engine.get(ScriptEngine.FILENAME);
        // 设置文件名
        engine.put(ScriptEngine.FILENAME, fullPath);
        try {
            ScriptObjectMirror exports = (ScriptObjectMirror) engine.eval(scriptCode);
            if (exports == null) {
                exports = ScriptEngineUtils.newObject();
                Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
                if (bindings.size() <= 0) {
                    bindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
                }
                if (bindings.size() > 0) {
                    boolean flag = true;
                    if (bindings.size() == 1) {
                        for (Map.Entry<String, Object> entry : bindings.entrySet()) {
                            if (entry.getValue() instanceof ScriptObjectMirror) {
                                exports = (ScriptObjectMirror) entry.getValue();
                                flag = false;
                            }
                        }
                    }
                    if (flag) {
                        exports.putAll(bindings);
                    }
                }
            }
            return Module.creatLibModule(exports);
        } finally {
            engine.put(ScriptEngine.FILENAME, previousFilename);
        }
    }
}
