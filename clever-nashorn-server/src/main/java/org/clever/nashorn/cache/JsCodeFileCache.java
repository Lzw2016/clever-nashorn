package org.clever.nashorn.cache;

import org.clever.nashorn.entity.JsCodeFile;

/**
 * Js代码缓存
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 11:11 <br/>
 */
public interface JsCodeFileCache {

    /**
     * 获取 文件夹类型 JsCodeFile
     *
     * @param bizType   业务类型
     * @param groupName 代码分组
     * @param filePath  上级路径
     * @param name      文件或文件夹名称
     */
    JsCodeFile getFolder(String bizType, String groupName, String filePath, String name);

    /**
     * 获取 文件类型 JsCodeFile
     *
     * @param bizType   业务类型
     * @param groupName 代码分组
     * @param filePath  上级路径
     * @param name      文件或文件夹名称
     */
    JsCodeFile getFile(String bizType, String groupName, String filePath, String name);

    /**
     * 加入缓存
     */
    void put(JsCodeFile jsCodeFile);

    /**
     * 从缓存中移除
     */
    void remove(JsCodeFile jsCodeFile);

    /**
     * 重新加载缓存
     */
    void reload();

    /**
     * 清除缓存
     */
    void clear();
}
