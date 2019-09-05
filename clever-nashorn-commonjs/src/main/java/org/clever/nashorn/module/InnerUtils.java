package org.clever.nashorn.module;

import jdk.nashorn.internal.runtime.ECMAException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.nashorn.folder.Folder;
import org.clever.nashorn.module.cache.ModuleCache;
import org.clever.nashorn.module.tuples.Tuple3;
import org.clever.nashorn.utils.Paths;
import org.clever.nashorn.utils.ScriptEngineUtils;

import javax.script.Bindings;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-09-04 21:31 <br/>
 */
@Slf4j
class InnerUtils {
    /**
     * 解析module得到“文件名称”和“文件所在文件夹”<br />
     * <pre>
     *     Tuple3 - String[] - folderParts      - 文件夹路径数组
     *     Tuple3 - String   - filename         - 文件名称
     *     Tuple3 - Folder   - resolvedFolder   - 文件所在文件夹对象
     * </pre>
     */
    static Tuple3<String[], String, Folder> resolvedFolder(String module, Folder folder) {
        if (module == null) {
            InnerUtils.throwModuleNotFoundException("<null>");
        }
        // 解析module得到“文件名称”和“文件所在文件夹”
        String[] parts = InnerUtils.splitPath(module);
        String[] folderParts = Arrays.copyOfRange(parts, 0, parts.length - 1);
        String filename = parts[parts.length - 1];
        Folder resolvedFolder = resolveFolder(folder, folderParts);
        return Tuple3.creat(folderParts, filename, resolvedFolder);
    }

    /**
     * 寻找并加载 Module
     *
     * @param module         模块全路径
     * @param folderParts    模块文件夹路径数组
     * @param filename       模块文件名称
     * @param resolvedFolder 模块文件所在文件夹对象
     * @param folder         当前Module所在文件夹对象
     * @param moduleCache    Module缓存
     * @param compileModule  编译Js对象接口
     */
    static Module requireModule(String module, String[] folderParts, String filename, Folder resolvedFolder, Folder folder, ModuleCache moduleCache, CompileModule compileModule) throws ScriptException {
        Module found = null;
        if (InnerUtils.isPrefixedModuleName(module)) {
            found = attemptToLoadFromThisFolder(resolvedFolder, filename, moduleCache, compileModule);
        }
        // 未加载成功则从 node_modules 中搜索加载 Module
        if (found == null) {
            found = searchForModuleInNodeModules(folder, folderParts, filename, moduleCache, compileModule);
        }
        // 还未加载成功则抛出异常
        if (found == null) {
            InnerUtils.throwModuleNotFoundException(module);
        }
        return found;
    }

    /**
     * 抛出Module找不到异常
     */
    private static void throwModuleNotFoundException(String module) {
        Bindings error = ScriptEngineUtils.newError("Module not found: " + module);
        error.put("code", "MODULE_NOT_FOUND");
        throw new ECMAException(error, null);
    }

    /**
     * 前缀是固定的 “/” 或 “../” 或 “./”
     */
    private static boolean isPrefixedModuleName(String module) {
        return module.startsWith("/") || module.startsWith("../") || module.startsWith("./");
    }

    /**
     * 尝试根据filename从文件夹中加载Module
     */
    private static Module attemptToLoadFromThisFolder(Folder resolvedFolder, String filename, ModuleCache moduleCache, CompileModule compileModule) throws ScriptException {
        if (resolvedFolder == null) {
            return null;
        }
        // 从文件加载Module - 尝试各种文件后缀变化
        Module module = loadModuleAsFileAndPutInCache(resolvedFolder, filename, moduleCache, compileModule);
        // 从文件夹加载 (寻找对应的 package.json | index.js | index.json)
        if (module == null) {
            module = loadModuleAsFolder(resolvedFolder, filename, moduleCache, compileModule);
        }
        return module;
    }

    /**
     * 寻找 node_modules 文件夹，从node_modules中加载 Module
     */
    private static Module searchForModuleInNodeModules(Folder resolvedFolder, String[] folderParts, String filename, ModuleCache moduleCache, CompileModule compileModule) throws ScriptException {
        Folder current = resolvedFolder;
        while (current != null) {
            Folder nodeModules = current.getFolder("node_modules");
            if (nodeModules != null) {
                Module found = attemptToLoadFromThisFolder(resolveFolder(nodeModules, folderParts), filename, moduleCache, compileModule);
                if (found != null) {
                    return found;
                }
            }
            current = current.getParent();
        }
        return null;
    }

    /**
     * 解析module得到“文件名称”和“文件所在文件夹”
     */
    private static String[] splitPath(String path) {
        String[] parts = Paths.splitPath(path);
        if (parts.length == 0) {
            throwModuleNotFoundException(path);
        }
        return parts;
    }

    /**
     * 定位得到对应文件夹对象
     */
    private static Folder resolveFolder(Folder from, String[] folders) {
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

    // 从文件加载Module
    private static Module loadModuleAsFileAndPutInCache(Folder path, String filename, ModuleCache moduleCache, CompileModule compileModule) throws ScriptException {
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
                Module module = compileModule(path, tentativeFilename, scriptCode, compileModule);
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
    private static String[] getFilenamesToAttempt(final String filename) {
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
    private static Module compileModule(Folder path, String filename, String scriptCode, CompileModule compileModule) throws ScriptException {
        Module created;
        // 编译 Module
        if (filename.endsWith(".js")) {
            // 编译 js
            created = compileModule.compileJavaScriptModule(path, filename, scriptCode);
        } else if (filename.endsWith(".json")) {
            // 编译 json
            created = compileModule.compileJsonModule(path, filename, scriptCode);
        } else {
            // 不支持的 module 类型
            Bindings error = ScriptEngineUtils.newError("not support file type:" + filename);
            error.put("code", "MODULE_NOT_SUPPORT");
            throw new ECMAException(error, null);
        }
        return created;
    }

    // 从文件夹加载 Module
    private static Module loadModuleAsFolder(Folder path, String filename, ModuleCache moduleCache, CompileModule compileModule) throws ScriptException {
        Folder fileAsFolder = path.getFolder(filename);
        if (fileAsFolder == null) {
            return null;
        }
        Module found = loadModuleThroughPackageJson(fileAsFolder, moduleCache, compileModule);
        if (found == null) {
            found = loadModuleThroughIndexJs(fileAsFolder, compileModule);
        }
        if (found == null) {
            found = loadModuleThroughIndexJson(fileAsFolder, compileModule);
        }
        return found;
    }

    // 通过 package.json 文件加载 Module
    private static Module loadModuleThroughPackageJson(Folder parent, ModuleCache moduleCache, CompileModule compileModule) throws ScriptException {
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
        Module module = loadModuleAsFileAndPutInCache(folder, filename, moduleCache, compileModule);
        if (module == null) {
            folder = resolveFolder(parent, parts);
            if (folder != null) {
                module = loadModuleThroughIndexJs(folder, compileModule);
            }
        }
        return module;
    }

    private static String getMainFileFromPackageJson(String packageJson) {
        Bindings parsed = ScriptEngineUtils.parseJson(packageJson);
        return (String) parsed.get("main");
    }

    // 从 index.js 文件加载 Module
    private static Module loadModuleThroughIndexJs(Folder parent, CompileModule compileModule) throws ScriptException {
        String code = parent.getFileContent("index.js");
        if (code == null) {
            return null;
        }
        return compileModule(parent, parent.getPath() + "index.js", code, compileModule);
    }

    // 从 index.json 文件加载 Module
    private static Module loadModuleThroughIndexJson(Folder parent, CompileModule compileModule) throws ScriptException {
        String code = parent.getFileContent("index.json");
        if (code == null) {
            return null;
        }
        return compileModule(parent, parent.getPath() + "index.json", code, compileModule);
    }
}
