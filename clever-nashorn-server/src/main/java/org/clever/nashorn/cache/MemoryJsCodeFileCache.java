package org.clever.nashorn.cache;

import org.clever.common.utils.spring.SpringContextHolder;
import org.clever.nashorn.entity.EnumConstant;
import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.mapper.JsCodeFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 11:25 <br/>
 */
@Transactional(readOnly = true)
@Service
public class MemoryJsCodeFileCache implements JsCodeFileCache {

    /**
     * 获取实例
     */
    public static MemoryJsCodeFileCache getInstance() {
        return SpringContextHolder.getBean(MemoryJsCodeFileCache.class);
    }

    /**
     * 内存缓存 Map<bizType|groupName|nodeType|filePath|name, JsCodeFile>
     */
    private static final Map<String, JsCodeFile> Js_Code_File_Map = new HashMap<>();
    @Autowired
    private JsCodeFileMapper jsCodeFileMapper;
    /**
     * 定时清除缓存的时间间隔,毫秒(小于等于0表示不清除)
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final long clearTimeInterval = 1000 * 3600 * 3;
    /**
     * 最后一次清除缓存时间
     */
    private long lastClearTime = System.currentTimeMillis();

    private void intervalClear() {
        //noinspection ConstantConditions
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
    public static String getCacheKey(String bizType, String groupName, Integer nodeType, String filePath, String name) {
        return String.format("%s|%s|%s|%s|%s", bizType, groupName, nodeType, filePath, name);
    }

    /**
     * 得到缓存 key
     */
    public static String getCacheKey(JsCodeFile jsCodeFile) {
        return getCacheKey(jsCodeFile.getBizType(), jsCodeFile.getGroupName(), jsCodeFile.getNodeType(), jsCodeFile.getFilePath(), jsCodeFile.getName());
    }

    @Override
    public JsCodeFile getFolder(String bizType, String groupName, String filePath, String name) {
        intervalClear();
        String key = getCacheKey(bizType, groupName, EnumConstant.Node_Type_2, filePath, name);
        JsCodeFile jsCodeFile = Js_Code_File_Map.get(key);
        if (jsCodeFile == null) {
            jsCodeFile = jsCodeFileMapper.getJsCodeFile(bizType, groupName, EnumConstant.Node_Type_2, filePath, name);
        }
        return jsCodeFile;
    }

    @Override
    public JsCodeFile getFile(String bizType, String groupName, String filePath, String name) {
        intervalClear();
        String key = getCacheKey(bizType, groupName, EnumConstant.Node_Type_1, filePath, name);
        JsCodeFile jsCodeFile = Js_Code_File_Map.get(key);
        if (jsCodeFile == null) {
            jsCodeFile = jsCodeFileMapper.getJsCodeFile(bizType, groupName, EnumConstant.Node_Type_1, filePath, name);
        }
        return jsCodeFile;
    }

    @Override
    public void put(JsCodeFile jsCodeFile) {
        intervalClear();
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
        Set<JsCodeFile> set = jsCodeFileMapper.findAll();
        set.forEach(this::put);
    }

    @Override
    public void clear() {
        Js_Code_File_Map.clear();
    }
}
