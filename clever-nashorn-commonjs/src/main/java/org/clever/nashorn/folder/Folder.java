package org.clever.nashorn.folder;

/**
 * 文件夹路径抽象
 */
public interface Folder {

    /**
     * 得到父文件夹路径
     *
     * @return 如果当前文件夹已经是根文件夹则返回 null
     */
    Folder getParent();

    /**
     * 得到当前文件夹路径，以路径分隔符(“/”或者“\”)结尾
     */
    String getPath();

    /**
     * 得到当前文件夹下的文件路径
     *
     * @param name 文件名称
     * @return 文件不存在返回null
     */
    String getFilePath(String name);

    /**
     * 得到当前文件夹下的文件内容
     *
     * @param name 文件名称
     * @return 文件不存在返回null
     */
    String getFileContent(String name);

    /**
     * 得到当前文件夹下的文件夹对象
     *
     * @param name 文件夹名称
     * @return 文件夹不存在返回null
     */
    Folder getFolder(String name);
}
