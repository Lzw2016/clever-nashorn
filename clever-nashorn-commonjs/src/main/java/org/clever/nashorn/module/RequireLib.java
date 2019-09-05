package org.clever.nashorn.module;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.clever.nashorn.folder.Folder;
import org.clever.nashorn.module.cache.ModuleCache;
import org.clever.nashorn.module.tuples.Tuple3;

import javax.script.ScriptException;

/**
 * 用于加载第三方依赖库(如：lodash、Underscore、等等)
 * <p>
 * 作者： lzw<br/>
 * 创建时间：2019-09-04 21:22 <br/>
 */
public class RequireLib implements RequireLibFunction, CompileModule {

    private final Folder folder;
    private final ModuleCache moduleCache;

    public RequireLib(Folder folder, ModuleCache moduleCache) {
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
        Module found = InnerUtils.requireModule(module, folderParts, filename, resolvedFolder, folder, moduleCache, this);
        return found.getExports();
    }

    @Override
    public Module compileJsonModule(Folder path, String filename, String scriptCode) throws ScriptException {
        return null;
    }

    @Override
    public Module compileJavaScriptModule(Folder path, String filename, String scriptCode) throws ScriptException {
        return null;
    }
}
