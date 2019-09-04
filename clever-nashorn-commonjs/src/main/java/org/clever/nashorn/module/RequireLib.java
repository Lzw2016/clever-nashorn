package org.clever.nashorn.module;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.clever.nashorn.folder.Folder;

import javax.script.ScriptException;
import java.util.Arrays;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-09-04 21:22 <br/>
 */
public class RequireLib implements RequireLibFunction {

    private final Folder FOLDER;

    @Override
    public ScriptObjectMirror requireLib(String module) throws ScriptException, NashornException {
        InnerUtils.checkNullModuleName(module);
        // 解析module得到“文件名称”和“文件所在文件夹”
        String[] parts = InnerUtils.splitPath(module);
        String[] folderParts = Arrays.copyOfRange(parts, 0, parts.length - 1);
        String filename = parts[parts.length - 1];
        Folder resolvedFolder = InnerUtils.resolveFolder(FOLDER, folderParts);
        String requestedFullPath;
        if (resolvedFolder != null) {
            requestedFullPath = resolvedFolder.getFilePath(filename);
        }
        // 加载 Module
        Module found = null;
        try {
            // 寻找并加载 Module
            if (InnerUtils.isPrefixedModuleName(module)) {
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


}
