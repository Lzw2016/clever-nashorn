package org.clever.nashorn.modules;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FilesystemFolder extends AbstractFolder {
    private File root;
    private String encoding = "UTF-8";

    private FilesystemFolder(File root, Folder parent, String path, String encoding) {
        super(parent, path);
        this.root = root;
        if (encoding != null) {
            this.encoding = encoding;
        }
    }

    public static FilesystemFolder create(File root) {
        return create(root, null);
    }

    public static FilesystemFolder create(File root, String encoding) {
        File absolute = root.getAbsoluteFile();
        return new FilesystemFolder(absolute, null, absolute.getPath() + File.separator, encoding);
    }

    @Override
    public String getFileContent(String name) {
        File file = new File(root, name);
        try (FileInputStream stream = new FileInputStream(file)) {
            return IOUtils.toString(stream, encoding);
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            throw new RuntimeException("open file failed", ex);
        }
    }

    @Override
    public Folder getFolder(String name) {
        File folder = new File(root, name);
        if (!folder.exists()) {
            return null;
        }
        return new FilesystemFolder(folder, this, getPath() + name + File.separator, encoding);
    }
}
