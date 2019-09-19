package org.clever.nashorn.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Paths {
    public static String[] splitPath(String path) {
        if (StringUtils.isBlank(path)) {
            return new String[]{};
        }
        String[] parts = path.split("[\\\\/]");
        List<String> result = new ArrayList<>(parts.length);
        int index = 0;
        for (String part : parts) {
            index++;
            if (index == 1 && StringUtils.isBlank(part)) {
                continue;
            }
            result.add(part);
        }
        return result.toArray(new String[]{});
    }
}
