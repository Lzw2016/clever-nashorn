package org.clever.nashorn.cache;

import org.clever.common.utils.spring.SpringContextHolder;
import org.clever.nashorn.entity.EnumConstant;
import org.clever.nashorn.entity.JsCodeFile;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 11:25 <br/>
 */
public class MemoryJsCodeFileCache implements JsCodeFileCache {

    /**
     * JsCodeFile 数据库查询工具
     */
    private final JsCodeFileCacheService cacheService;
    /**
     * 内存缓存 Map<bizType|groupName|nodeType|filePath|name, JsCodeFile>
     */
    private final Map<String, JsCodeFile> Js_Code_File_Map = new ConcurrentHashMap<>();
    /**
     * 定时清除缓存的时间间隔,毫秒(小于等于0表示不清除)
     */
    private final long clearTimeInterval;
    /**
     * 最后一次清除缓存时间
     */
    private long lastClearTime = System.currentTimeMillis();

    /**
     * @param clearTimeInterval 定时清除缓存的时间间隔,毫秒(小于等于0表示不清除)
     */
    public MemoryJsCodeFileCache(long clearTimeInterval) {
        this.clearTimeInterval = clearTimeInterval;
        this.cacheService = SpringContextHolder.getBean(JsCodeFileCacheService.class);
    }

    public MemoryJsCodeFileCache() {
        this(-1);
    }

    public MemoryJsCodeFileCache(long clearTimeInterval, JsCodeFileCacheService cacheService) {
        this.clearTimeInterval = clearTimeInterval;
        this.cacheService = cacheService;
    }

    /**
     * 清除缓存(定时策略)
     */
    private void intervalClear() {
        if (clearTimeInterval <= 0) {
            return;
        }
        if ((System.currentTimeMillis() - lastClearTime) >= clearTimeInterval) {
            clear();
        }
    }

    /**
     * 得到缓存 key
     *
     * @param bizType   业务类型
     * @param groupName 代码分组
     * @param nodeType  数据类型
     * @param filePath  上级路径
     * @param name      文件或文件夹名称
     */
    private static String getCacheKey(String bizType, String groupName, Integer nodeType, String filePath, String name) {
        return String.format("%s|%s|%s|%s|%s", bizType, groupName, nodeType, filePath, name);
    }

    /**
     * 得到缓存 key
     */
    private static String getCacheKey(JsCodeFile jsCodeFile) {
        return getCacheKey(jsCodeFile.getBizType(), jsCodeFile.getGroupName(), jsCodeFile.getNodeType(), jsCodeFile.getFilePath(), jsCodeFile.getName());
    }

    @Override
    public JsCodeFile getFolder(String bizType, String groupName, String filePath, String name) {
        intervalClear();
        String key = getCacheKey(bizType, groupName, EnumConstant.Node_Type_2, filePath, name);
        JsCodeFile jsCodeFile = Js_Code_File_Map.get(key);
        if (jsCodeFile == null) {
            jsCodeFile = cacheService.getJsCodeFile(bizType, groupName, EnumConstant.Node_Type_2, filePath, name);
            if (jsCodeFile != null) {
                put(jsCodeFile);
            }
        }
        return jsCodeFile;
    }

    @Override
    public JsCodeFile getFile(String bizType, String groupName, String filePath, String name) {
        intervalClear();
        String key = getCacheKey(bizType, groupName, EnumConstant.Node_Type_1, filePath, name);
        JsCodeFile jsCodeFile = Js_Code_File_Map.get(key);
        if (jsCodeFile == null) {
            jsCodeFile = cacheService.getJsCodeFile(bizType, groupName, EnumConstant.Node_Type_1, filePath, name);
            if (jsCodeFile != null) {
                put(jsCodeFile);
            }
        }
        return jsCodeFile;
    }

    @Override
    public void put(JsCodeFile jsCodeFile) {
        intervalClear();
        JsCodeFile exists = Js_Code_File_Map.values().stream().filter(file -> Objects.equals(file.getId(), jsCodeFile.getId())).findFirst().orElse(null);
        if (exists != null) {
            remove(exists);
        }
        String key = getCacheKey(jsCodeFile);
        Js_Code_File_Map.put(key, jsCodeFile);
    }

    @Override
    public void remove(JsCodeFile jsCodeFile) {
        intervalClear();
        String key = getCacheKey(jsCodeFile);
        Js_Code_File_Map.remove(key);
    }

    @Override
    public void reload() {
        clear();
        Set<JsCodeFile> set = cacheService.findAll();
        set.forEach(this::put);
    }

    @Override
    public void clear() {
        Js_Code_File_Map.clear();
    }
}
