package org.clever.nashorn.folder;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class ResourceFolder extends AbstractFolder {
    private ClassLoader loader;
    private String resourcePath;
    private String encoding;

    private ResourceFolder(ClassLoader loader, String resourcePath, Folder parent, String displayPath, String encoding) {
        super(parent, displayPath);
        this.loader = loader;
        this.resourcePath = resourcePath;
        this.encoding = encoding;
    }

    public static ResourceFolder create(ClassLoader loader, String path, String encoding) {
        return new ResourceFolder(loader, path, null, "/", encoding);
    }

    @Override
    public String getFileContent(String name) {
        try (InputStream stream = loader.getResourceAsStream(resourcePath + "/" + name)) {
            if (stream == null) {
                return null;
            }
            return IOUtils.toString(stream, encoding);
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public Folder getFolder(String name) {
        return new ResourceFolder(loader, resourcePath + "/" + name, this, getPath() + name + "/", encoding);
    }
}
