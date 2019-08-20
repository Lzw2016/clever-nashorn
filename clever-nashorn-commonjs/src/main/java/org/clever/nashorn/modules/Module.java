package org.clever.nashorn.modules;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.ECMAException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.nashorn.modules.utils.Paths;

import javax.script.*;
import java.util.*;

@Slf4j
public class Module extends SimpleBindings implements RequireFunction {
    private static final String Module_Main = "main";
    private static final String Module_Exports = "exports";
    private static final String Module_Children = "children";
    private static final String Module_Filepath = "filepath";
    private static final String Module_Filename = "filename";
    private static final String Module_Id = "id";
    private static final String Module_Loaded = "loaded";
    private static final String Module_Parent = "parent";
    private static final String Module_Console = "console";

    // 当前线程缓存(fileFullPath --> ScriptObjectMirror缓存)
    private final static ThreadLocal<Map<String, ScriptObjectMirror>> refCache = new ThreadLocal<>();

    // 当前Module使用的JS引擎
    @Getter
    private final NashornScriptEngine engine;
    // 当前模块使用的Module缓存(fileFullPath ---> Module缓存)
    @Getter
    private final ModuleCache moduleCache;
    // 最顶层的Module对象(根Module)
    @Getter
    private final Module main;
    // 用于构造Js空对象 {}
    private final ScriptObjectMirror objectConstructor;
    // 用户构造Js Error 对象
    private final ScriptObjectMirror errorConstructor;
    // 用于解析JSON
    private final ScriptObjectMirror jsonConstructor;

    // 当前Module所在文件夹
    @Getter
    private final Folder folder;
    // 加载Module时定义的 module 对象 TODO 删除此属性
    @Getter
    private final Bindings module;
    // 当前Module依赖(require)的 module 对象
    @Getter
    private final List<Bindings> children = new ArrayList<>();
    // JS Module 定义的导出对象
    @Getter
    private ScriptObjectMirror exports;

    /**
     * @param engine      Script Engine
     * @param moduleCache Module Cache
     * @param rootFolder  Root Folder
     * @param module      module
     * @param exports     exports
     */
    public Module(NashornScriptEngine engine, ModuleCache moduleCache, Folder rootFolder, Bindings module, ScriptObjectMirror exports) throws ScriptException {
        this.folder = rootFolder;
        this.engine = engine;
        this.moduleCache = moduleCache;
        this.objectConstructor = (ScriptObjectMirror) engine.eval("Object");
        this.jsonConstructor = (ScriptObjectMirror) engine.eval("JSON");
        this.errorConstructor = (ScriptObjectMirror) engine.eval("Error");
        // 设置根Module
        this.main = this;
        this.module = module;
        this.exports = exports;
        String filename = "<main>";
        put(Module_Main, this.main.module);
        this.module.put(Module_Exports, exports);
        this.module.put(Module_Children, children);
        this.module.put(Module_Filepath, folder.getPath());
        this.module.put(Module_Filename, filename);
        this.module.put(Module_Id, folder.getFilePath(filename));
        this.module.put(Module_Loaded, false);
        this.module.put(Module_Parent, null);
        this.module.put(Module_Console, new Console(folder.getFilePath(filename)));
        // TODO 注入其他自定义的对象
    }

    /**
     * @param folder   当前Module所在文件夹
     * @param filename 当前Module文件名
     * @param parent   当前Module的父Module
     */
    protected Module(Folder folder, String filename, Module parent) {
        this.folder = folder;
        this.engine = parent.engine;
        this.moduleCache = parent.moduleCache;
        this.objectConstructor = parent.objectConstructor;
        this.errorConstructor = parent.errorConstructor;
        this.jsonConstructor = parent.jsonConstructor;
        // 设置根Module
        this.main = parent.main;
        // 初始化 exports
        this.exports = refCache.get() != null ? refCache.get().get(this.folder.getFilePath(filename)) : null;
        if (this.exports == null) {
            this.exports = createSafeBindings();
        }
        // 初始化 module
        this.module = createSafeBindings();
        module.putAll(parent.engine.getBindings(ScriptContext.ENGINE_SCOPE));
        put(Module_Main, this.main.module);
        this.module.put(Module_Exports, exports);
        this.module.put(Module_Children, children);
        this.module.put(Module_Filepath, folder.getPath());
        this.module.put(Module_Filename, filename);
        this.module.put(Module_Id, folder.getFilePath(filename));
        this.module.put(Module_Loaded, false);
        this.module.put(Module_Parent, parent.module);
        this.module.put(Module_Console, new Console(folder.getFilePath(filename)));
        // TODO 注入其他自定义的对象
    }

    public ScriptObjectMirror useJs(String name) {
        try {
            return require(name);
        } catch (ScriptException e) {
            throw new RuntimeException("require js failure", e);
        }
    }

    @Override
    public ScriptObjectMirror require(String module) throws ScriptException, NashornException {
        if (module == null) {
            throwModuleNotFoundException("<null>");
        }
        assert module != null;

        // 解析module得到“文件名称”和“文件所在文件夹”
        String[] parts = Paths.splitPath(module);
        if (parts.length == 0) {
            throwModuleNotFoundException(module);
        }
        String[] folderParts = Arrays.copyOfRange(parts, 0, parts.length - 1);
        String filename = parts[parts.length - 1];
        Folder resolvedFolder = resolveFolder(folder, folderParts);
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
                refCache.get().put(requestedFullPath, createSafeBindings());
            }
        }

        // 加载 Module
        Module found = null;
        try {
            // 寻找并加载 Module
            if (isPrefixedModuleName(module)) {
                found = attemptToLoadFromThisFolder(resolvedFolder, filename);
            }
            // 未加载成功则从 node_modules 中搜索加载 Module
            if (found == null) {
                found = searchForModuleInNodeModules(folder, folderParts, filename);
            }
            // 还未加载成功则抛出异常
            if (found == null) {
                throwModuleNotFoundException(module);
            }
            assert found != null;
            children.add(found.module);
            return found.exports;
        } finally {
            //  需要删除防止内存泄漏
            if (needRemove && refCache.get() != null) {
                refCache.remove();
            }
        }
    }

    // 抛出Module找不到异常
    private void throwModuleNotFoundException(String module) {
        Bindings error = (Bindings) errorConstructor.newObject("Module not found: " + module);
        error.put("code", "MODULE_NOT_FOUND");
        throw new ECMAException(error, null);
    }

    // 定位得到对应文件夹对象
    private Folder resolveFolder(Folder from, String[] folders) {
        Folder current = from;
        for (String name : folders) {
            switch (name) {
                case "":
                    throw new IllegalArgumentException();
                case ".":
                    continue;
                case "..":
                    current = current.getParent();
                    break;
                default:
                    current = current.getFolder(name);
                    break;
            }
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    // 创建一个安全的Bindings对象
    private ScriptObjectMirror createSafeBindings() {
        return (ScriptObjectMirror) objectConstructor.newObject();
    }

    // 前缀是固定的 “/” 或 “../” 或 “./”
    private static boolean isPrefixedModuleName(String module) {
        return module.startsWith("/") || module.startsWith("../") || module.startsWith("./");
    }

    // 尝试根据filename从文件夹中加载Module
    private Module attemptToLoadFromThisFolder(Folder resolvedFolder, String filename) throws ScriptException {
        if (resolvedFolder == null) {
            return null;
        }
        // 从文件加载Module - 尝试各种文件后缀变化
        Module module = loadModuleAsFileAndPutInCache(resolvedFolder, filename);
        // 从文件夹加载 (寻找对应的 package.json | index.js | index.json)
        if (module == null) {
            module = loadModuleAsFolder(resolvedFolder, filename);
        }
        return module;
    }

    // 从文件加载Module
    private Module loadModuleAsFileAndPutInCache(Folder path, String filename) throws ScriptException {
        // 获取可能的文件名
        String[] filenamesToAttempt = getFilenamesToAttempt(filename);
        for (String tentativeFilename : filenamesToAttempt) {
            String requestedFullPath = path.getFilePath(tentativeFilename);
            Module found = moduleCache.get(requestedFullPath);
            if (found != null) {
                log.debug("# ModuleCache 命中缓存 -> {}", requestedFullPath);
                return found;
            }
            String scriptCode = path.getFileContent(tentativeFilename);
            if (scriptCode != null) {
                Module module = compileModule(path, tentativeFilename, scriptCode);
                if (module != null) {
                    // 缓存当前加载的 Module
                    log.debug("# ModuleCache 加入缓存 -> {}", requestedFullPath);
                    moduleCache.put(requestedFullPath, module);
                    return module;
                }
            }
        }
        return null;
    }

    // 获取需要尝试的文件名
    private static String[] getFilenamesToAttempt(String filename) {
        if (StringUtils.isBlank(filename)) {
            return new String[]{};
        }
        List<String> filenameList = new ArrayList<String>() {{
            add(filename);
        }};
        String[] suffixArray = new String[]{".js", ".json"};
        for (String suffix : suffixArray) {
            if (!filename.toLowerCase().endsWith(suffix)) {
                filenameList.add(filename + suffix);
            }
        }
        return filenameList.toArray(new String[]{});
    }

    // 编译 Module (.js 和 .json)
    private Module compileModule(Folder path, String filename, String scriptCode) throws ScriptException {
        Module created;
        // 编译 Module
        if (filename.endsWith(".js")) {
            // 编译 js
            created = compileJavaScriptModule(path, filename, scriptCode);
        } else if (filename.endsWith(".json")) {
            // 编译 json
            created = compileJsonModule(path, filename, scriptCode);
        } else {
            // 不支持的 module 类型 TODO 抛异常
            return null;
        }
        return created;
    }

    // 编译 JavaScript Module
    private Module compileJavaScriptModule(Folder path, String filename, String scriptCode) throws ScriptException {
        String fullPath = path.getFilePath(filename);
        // 创建 Module
        Module created = new Module(path, filename, this);
        // 初始化 Module
        String dirname = path.getPath();
        String previousFilename = (String) engine.get(ScriptEngine.FILENAME);
        // 设置文件名
        engine.put(ScriptEngine.FILENAME, fullPath);
        try {
            scriptCode = String.format("(function (exports, require, module, __filename, __dirname) {\n %s \n})", scriptCode);
            ScriptObjectMirror function = (ScriptObjectMirror) engine.eval(scriptCode, created.module);
            // this         --> created
            // exports      --> created.exports
            // require      --> created (Module 对象实现了RequireFunction接口)
            // module       --> created.module
            // __filename   --> filename
            // __dirname    --> dirname
            function.call(created, created.exports, created, created.module, filename, dirname);
        } finally {
            engine.put(ScriptEngine.FILENAME, previousFilename);
        }
        // 获得js导出的 exports
        created.exports = (ScriptObjectMirror) created.module.get("exports");
        // 设置加载成功
        created.setLoaded();
        return created;
    }

    // 编译 Json Module
    private Module compileJsonModule(Folder path, String filename, String scriptCode) {
        Module created = new Module(path, filename, this);
        created.exports = parseJson(scriptCode);
        created.setLoaded();
        return created;
    }

    // 设置加载成功
    void setLoaded() {
        // 修改加载状态
        module.put("loaded", true);
        // TODO JS加载初始化生命周期
        if (!exports.hasMember("init")) {
            return;
        }
        Object init = exports.getMember("init");
        if (init instanceof ScriptObjectMirror) {
            ScriptObjectMirror initFuc = (ScriptObjectMirror) init;
            Object res = initFuc.call(this);
            log.debug("[init] [{}] -> {}", this.module.get(Module_Id), res);
        }
    }

    // 解析Json成为 ScriptObjectMirror 对象
    private ScriptObjectMirror parseJson(String json) {
        return (ScriptObjectMirror) jsonConstructor.callMember("parse", json);
    }

    // 从文件夹加载 Module
    private Module loadModuleAsFolder(Folder path, String filename) throws ScriptException {
        Folder fileAsFolder = path.getFolder(filename);
        if (fileAsFolder == null) {
            return null;
        }
        Module found = loadModuleThroughPackageJson(fileAsFolder);
        if (found == null) {
            found = loadModuleThroughIndexJs(fileAsFolder);
        }
        if (found == null) {
            found = loadModuleThroughIndexJson(fileAsFolder);
        }
        return found;
    }

    // 通过 package.json 文件加载 Module
    private Module loadModuleThroughPackageJson(Folder parent) throws ScriptException {
        String packageJson = parent.getFileContent("package.json");
        if (packageJson == null) {
            return null;
        }
        String mainFile = getMainFileFromPackageJson(packageJson);
        if (mainFile == null) {
            return null;
        }
        String[] parts = Paths.splitPath(mainFile);
        String[] folders = Arrays.copyOfRange(parts, 0, parts.length - 1);
        String filename = parts[parts.length - 1];
        Folder folder = resolveFolder(parent, folders);
        if (folder == null) {
            return null;
        }
        Module module = loadModuleAsFileAndPutInCache(folder, filename);
        if (module == null) {
            folder = resolveFolder(parent, parts);
            if (folder != null) {
                module = loadModuleThroughIndexJs(folder);
            }
        }
        return module;
    }

    private String getMainFileFromPackageJson(String packageJson) {
        Bindings parsed = parseJson(packageJson);
        return (String) parsed.get("main");
    }

    // 从 index.js 文件加载 Module
    private Module loadModuleThroughIndexJs(Folder parent) throws ScriptException {
        String code = parent.getFileContent("index.js");
        if (code == null) {
            return null;
        }
        return compileModule(parent, parent.getPath() + "index.js", code);
    }

    // 从 index.json 文件加载 Module
    private Module loadModuleThroughIndexJson(Folder parent) throws ScriptException {
        String code = parent.getFileContent("index.json");
        if (code == null) {
            return null;
        }
        return compileModule(parent, parent.getPath() + "index.json", code);
    }

    // 寻找 node_modules 文件夹，从node_modules中加载 Module
    private Module searchForModuleInNodeModules(Folder resolvedFolder, String[] folderParts, String filename) throws ScriptException {
        Folder current = resolvedFolder;
        while (current != null) {
            Folder nodeModules = current.getFolder("node_modules");
            if (nodeModules != null) {
                Module found = attemptToLoadFromThisFolder(resolveFolder(nodeModules, folderParts), filename);
                if (found != null) {
                    return found;
                }
            }
            current = current.getParent();
        }
        return null;
    }
}
