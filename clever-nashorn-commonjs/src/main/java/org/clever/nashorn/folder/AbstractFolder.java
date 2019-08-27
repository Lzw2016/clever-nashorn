package org.clever.nashorn.folder;

public abstract class AbstractFolder implements Folder {
    /**
     * 上级目录
     */
    private Folder parent;
    /**
     * 当前文件夹路径
     */
    private String path;

    protected AbstractFolder(Folder parent, String path) {
        this.parent = parent;
        this.path = path;
    }

    @Override
    public Folder getParent() {
        return parent;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getFilePath(String name) {
        return getPath() + name;
    }
}
