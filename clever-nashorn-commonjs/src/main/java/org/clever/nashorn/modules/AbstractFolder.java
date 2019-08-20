package org.clever.nashorn.modules;

public abstract class AbstractFolder implements Folder {
    private Folder parent;
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
