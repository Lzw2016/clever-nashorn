package org.clever.nashorn.utils;

import org.apache.commons.lang3.StringUtils;

public class Paths {
    public static String[] splitPath(String path) {
        if (StringUtils.isBlank(path)) {
            return new String[]{};
        }
        return path.split("[\\\\/]");
    }
}
