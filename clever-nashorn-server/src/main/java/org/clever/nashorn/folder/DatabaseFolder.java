package org.clever.nashorn.folder;

import org.clever.nashorn.cache.JsCodeFileCache;
import org.clever.nashorn.entity.EnumConstant;
import org.clever.nashorn.entity.JsCodeFile;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 11:01 <br/>
 */
public class DatabaseFolder extends AbstractFolder {
    /**
     * Js代码缓存
     */
    private final JsCodeFileCache cache;
    /**
     * 业务类型
     */
    private final String bizType;
    /**
     * 代码分组
     */
    private final String groupName;

    /**
     * 创建根文件夹
     *
     * @param bizType   业务类型
     * @param groupName 代码分组
     * @param cache     Js代码缓存
     */
    public DatabaseFolder(String bizType, String groupName, JsCodeFileCache cache) {
        super(null, EnumConstant.File_Path_Separator);
        this.bizType = bizType;
        this.groupName = groupName;
        this.cache = cache;
    }

    /**
     * 创建子文件夹
     *
     * @param parent 父文件夹
     * @param path   子文件夹全路径
     */
    private DatabaseFolder(DatabaseFolder parent, String path) {
        super(parent, path);
        this.bizType = parent.bizType;
        this.groupName = parent.groupName;
        this.cache = parent.cache;
    }

    @Override
    public Folder getFolder(String name) {
        String folderPath = this.getPath() + name + EnumConstant.File_Path_Separator;
        JsCodeFile jsCodeFile = cache.getFolder(bizType, groupName, folderPath);
        if (jsCodeFile == null) {
            return null;
        }
        return new DatabaseFolder(this, folderPath);
    }

    @Override
    public String getFileContent(String name) {
        String filePath = this.getPath() + name;
        JsCodeFile jsCodeFile = cache.getFile(bizType, groupName, filePath);
        if (jsCodeFile == null) {
            return null;
        }
        return jsCodeFile.getJsCode();
    }
}
