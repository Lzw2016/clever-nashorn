package org.clever.nashorn.module;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.nashorn.folder.Folder;
import org.clever.nashorn.internal.Console;
import org.clever.nashorn.module.cache.ModuleCache;
import org.clever.nashorn.tuples.Tuple3;
import org.clever.nashorn.utils.ScriptEngineUtils;

import javax.script.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * js 代码模块实现，实现模块之间的加载和依赖
 */
@Slf4j
public class Module extends SimpleBindings implements RequireFunction, CompileModule {

    public static final String Root_Filename = "<main>";

    // ---------------------------------------------------------------------------------------------- 自定义注入对象名(module、this)
    private static final String Module_Main = "main";
    private static final String Module_Exports = "exports";
    private static final String Module_Children = "children";
    private static final String Module_Filepath = "filepath";
    private static final String Module_Filename = "filename";
    private static final String Module_Id = "id";
    private static final String Module_Loaded = "loaded";
    private static final String Module_Parent = "parent";
    private static final String Module_Console = "console";
    // ----------------------------------------------------------------------------------------------JS加载初始化生命周期
    private static final String Module_Fuc_Init = "init";
    // ----------------------------------------------------------------------------------------------

    // 当前线程缓存(fileFullPath --> ScriptObjectMirror缓存)
    private final static ThreadLocal<Map<String, ScriptObjectMirror>> refCache = new ThreadLocal<>();

    // 当前Module使用的JS引擎
    @Getter
    private final NashornScriptEngine engine;
    // 当前Module使用的Console
    private final Console console;
    // 当前模块使用的Module缓存(fileFullPath ---> Module缓存)
    @Getter
    private final ModuleCache moduleCache;
    // 最顶层的Module对象(root Module)
    @Getter
    private final Module main;
    // 当前Module所在文件夹
    @Getter
    private final Folder folder;
    // 加载Module时定义的 module 对象
    @Getter
    private final Bindings module;
    // 当前Module依赖(require)的 module 对象
    @Getter
    private final List<Bindings> children = new ArrayList<>(1);
    @Getter
    private final RequireLib requireLib;
    // JS Module 定义的导出对象
    @Getter
    private ScriptObjectMirror exports;

    /**
     * 新建一个 root Module
     *
     * @param engine      Script Engine
     * @param console     Console
     * @param moduleCache Module Cache
     * @param rootFolder  Root Folder
     * @param module      module
     * @param exports     exports
     */
    public Module(NashornScriptEngine engine, Console console, ModuleCache moduleCache, Folder rootFolder, Bindings module, ScriptObjectMirror exports) {
        this.engine = engine;
        this.console = console;
        this.moduleCache = moduleCache;
        this.folder = rootFolder;
        this.module = module;
        this.requireLib = new RequireLib(rootFolder, moduleCache);
        this.exports = exports;
        // 设置根Module
        this.main = this;
        inject(Root_Filename, null);
        setLoaded();
    }

    /**
     * 新建一个 child Module
     *
     * @param folder   当前Module所在文件夹
     * @param filename 当前Module文件名
     * @param parent   当前Module的父Module
     */
    protected Module(Folder folder, String filename, Module parent) {
        this.engine = parent.engine;
        this.console = parent.console.creat(folder.getPath(), filename);
        this.moduleCache = parent.moduleCache;
        this.folder = folder;
        // 初始化 module
        this.module = ScriptEngineUtils.newObject();
        this.requireLib = new RequireLib(folder, moduleCache);
        // 初始化 exports
        this.exports = refCache.get() != null ? refCache.get().get(this.folder.getFilePath(filename)) : null;
        if (this.exports == null) {
            this.exports = ScriptEngineUtils.newObject();
        }
        // 设置根Module
        this.main = parent.main;
        inject(filename, parent);
    }

    /**
     * 创建一个第三方模块的Module
     *
     * @param exports 导出对象
     */
    private Module(ScriptObjectMirror exports) {
        engine = null;
        console = null;
        moduleCache = null;
        main = null;
        folder = null;
        module = null;
        requireLib = null;
        this.exports = exports;
    }

    /**
     * 创建一个第三方模块的Module
     *
     * @param exports 导出对象
     */
    static Module creatLibModule(ScriptObjectMirror exports) {
        return new Module(exports);
    }

    /**
     * 自定义注入对象名(module、this)
     *
     * @param filename 当前Module文件名
     * @param parent   当前Module的父Module
     */
    private void inject(String filename, Module parent) {
        // 当前模块module对象内容
        module.putAll(engine.getBindings(ScriptContext.ENGINE_SCOPE));
        module.put(Module_Exports, exports);
        module.put(Module_Children, children);
        module.put(Module_Filepath, folder.getPath());
        module.put(Module_Filename, filename);
        module.put(Module_Id, folder.getFilePath(filename));
        module.put(Module_Loaded, false);
        module.put(Module_Parent, parent == null ? null : parent.module);
        module.put(Module_Console, console);
        // 当前模块内容
        putAll(engine.getBindings(ScriptContext.ENGINE_SCOPE));
        put(Module_Main, this.main.module);
        put(Module_Exports, exports);
        put(Module_Children, children);
        put(Module_Filepath, folder.getPath());
        put(Module_Filename, filename);
        put(Module_Id, folder.getFilePath(filename));
        put(Module_Loaded, false);
        put(Module_Parent, parent == null ? null : parent.module);
        put(Module_Console, console);
    }

    /**
     * 设置加载成功
     */
    private void setLoaded() {
        // 修改加载状态
        module.put(Module_Loaded, true);
        put(Module_Loaded, true);
        // JS加载初始化生命周期
        if (!exports.hasMember(Module_Fuc_Init)) {
            return;
        }
        Object init = exports.getMember(Module_Fuc_Init);
        if (init instanceof ScriptObjectMirror) {
            ScriptObjectMirror initFuc = (ScriptObjectMirror) init;
            Object res = initFuc.call(this);
            log.debug("[{}] [{}] -> {}", Module_Fuc_Init, this.module.get(Module_Id), res);
        }
    }

    @Override
    public ScriptObjectMirror require(String module) throws ScriptException, NashornException {
        boolean useRoot = false;
        if (StringUtils.isNotBlank(module) && module.startsWith("@/")) {
            useRoot = true;
            module = module.substring(1);
        }
        Tuple3<String[], String, Folder> tuple3;
        if (useRoot && main != null) {
            tuple3 = InnerUtils.resolvedFolder(module, main.folder);
        } else {
            tuple3 = InnerUtils.resolvedFolder(module, folder);
        }
        String[] folderParts = tuple3.getValue1();
        String filename = tuple3.getValue2();
        Folder resolvedFolder = tuple3.getValue3();

        // 让我们确保每个线程都有自己的refCache
        boolean needRemove = false;
        if (refCache.get() == null) {
            needRemove = true;
            refCache.set(new HashMap<>());
        }
        String requestedFullPath;
        if (resolvedFolder != null) {
            requestedFullPath = resolvedFolder.getFilePath(filename);
            ScriptObjectMirror cachedExports = refCache.get().get(requestedFullPath);
            if (cachedExports != null) {
                log.debug("# RefCache 命中缓存 -> {}", requestedFullPath);
                return cachedExports;
            } else {
                // 我们必须存储对当前加载模块的引用，以避免循环require
                log.debug("# RefCache 加入缓存 -> {}", requestedFullPath);
                refCache.get().put(requestedFullPath, ScriptEngineUtils.newObject());
            }
        }

        // 加载 Module
        Module found;
        try {
            // 寻找并加载 Module
            found = InnerUtils.loadModule(module, folderParts, filename, resolvedFolder, folder, moduleCache, this);
            children.add(found.module);
            return found.exports;
        } finally {
            //  需要删除防止内存泄漏
            if (needRemove && refCache.get() != null) {
                refCache.remove();
            }
        }
    }

    @Override
    public Module compileJavaScriptModule(Folder path, String filename, String scriptCode) throws ScriptException {
        String fullPath = path.getFilePath(filename);
        // 创建 Module
        Module created = new Module(path, filename, this);
        // 初始化 Module
        String dirname = path.getPath();
        String previousFilename = (String) engine.get(ScriptEngine.FILENAME);
        // 设置文件名
        engine.put(ScriptEngine.FILENAME, fullPath);
        try {
            scriptCode = String.format("(function (exports, require, requireLib, module, __filename, __dirname) {\n %s \n})", scriptCode);
            ScriptObjectMirror function = (ScriptObjectMirror) engine.eval(scriptCode, created.module);
            // this         --> created
            // exports      --> created.exports
            // require      --> created (Module 对象实现了RequireFunction接口)
            // requireLib   --> created.requireLib (加载第三方依赖实现了RequireLibFunction接口)
            // module       --> created.module
            // __filename   --> filename
            // __dirname    --> dirname
            function.call(created, created.exports, created, created.requireLib, created.module, filename, dirname);
        } finally {
            engine.put(ScriptEngine.FILENAME, previousFilename);
        }
        // 获得js导出的 exports
        created.exports = (ScriptObjectMirror) created.module.get("exports");
        // 设置加载成功
        created.setLoaded();
        return created;
    }

    @Override
    public Module compileJsonModule(Folder path, String filename, String scriptCode) {
        Module created = new Module(path, filename, this);
        created.exports = ScriptEngineUtils.parseJson(scriptCode);
        created.setLoaded();
        return created;
    }

    /**
     * 使用 require 得到 JS 对象
     */
    public ScriptObjectMirror useJs(String name) {
        return useJs(name, false);
    }

    /**
     * 使用 require 得到 JS 对象
     */
    public ScriptObjectMirror useLibJs(String name) {
        return useJs(name, true);
    }

    /**
     * 使用 require 得到 JS 对象
     *
     * @param name  文件全路径
     * @param isLib 是否使用第三方依赖库加载模式
     */
    private ScriptObjectMirror useJs(String name, boolean isLib) {
        try {
            return isLib ? requireLib.requireLib(name) : require(name);
        } catch (ScriptException e) {
            throw new RuntimeException("require js failure", e);
        }
    }
}
