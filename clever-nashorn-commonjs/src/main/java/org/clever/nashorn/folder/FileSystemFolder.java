package org.clever.nashorn.folder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileSystemFolder extends AbstractFolder {
    private final File root;
    private final String encoding;

    private FileSystemFolder(File root, Folder parent, String path, String encoding) {
        super(parent, path);
        this.root = root;
        if (StringUtils.isNotBlank(encoding)) {
            this.encoding = encoding;
        } else {
            this.encoding = "UTF-8";
        }
    }

    public static FileSystemFolder create(File root) {
        return create(root, null);
    }

    public static FileSystemFolder create(File root, String encoding) {
        File absolute = root.getAbsoluteFile();
        return new FileSystemFolder(absolute, null, absolute.getPath() + File.separator, encoding);
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
        return new FileSystemFolder(folder, this, getPath() + name + File.separator, encoding);
    }
}
