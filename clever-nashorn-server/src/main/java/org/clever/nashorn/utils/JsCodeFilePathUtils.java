package org.clever.nashorn.utils;

import org.clever.common.utils.tuples.TupleTow;
import org.clever.nashorn.entity.EnumConstant;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 17:33 <br/>
 */
public class JsCodeFilePathUtils {

    public static TupleTow<String, String> getParentPath(String filePath) {
        int index = filePath.lastIndexOf(EnumConstant.File_Path_Separator);
        if (index < 0) {
            return TupleTow.creat(filePath, "");
        }
        index = index + 1;
        return TupleTow.creat(filePath.substring(0, index), filePath.substring(index));
    }

    public static String concat(String filePath, String name) {
        if (filePath.endsWith(EnumConstant.File_Path_Separator)) {
            return filePath + name;
        } else {
            return filePath + EnumConstant.File_Path_Separator + name;
        }
    }

    public static String getFilePath(String filePath) {
        if (filePath.endsWith(EnumConstant.File_Path_Separator)) {
            return filePath;
        } else {
            return filePath + EnumConstant.File_Path_Separator;
        }
    }
}
