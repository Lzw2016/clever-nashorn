package org.clever.nashorn.utils;

import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.tuples.TupleTow;
import org.clever.nashorn.entity.EnumConstant;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 17:33 <br/>
 */
public class JsCodeFilePathUtils {

    /**
     * 拆分路径<br />
     * <pre>
     *     ""                -> "", ""
     *     "/"               -> "/", ""
     *     "/public"         -> "/", "public"
     *     "/public/tmp.js"  -> "/public/", "tmp.js"
     * </pre>
     *
     * @param filePath 文件路径
     */
    public static TupleTow<String, String> getParentPath(String filePath) {
        int index = filePath.lastIndexOf(EnumConstant.File_Path_Separator);
        if (index < 0) {
            return TupleTow.creat(filePath, "");
        }
        index = index + 1;
        return TupleTow.creat(filePath.substring(0, index), filePath.substring(index));
    }

    /**
     * 连接路径与文件
     *
     * @param filePath 上级路径
     * @param name     当前文件(夹)
     */
    public static String concat(String filePath, String name) {
        if (StringUtils.isBlank(filePath)) {
            filePath = EnumConstant.File_Path_Separator;
        }
        if (StringUtils.isBlank(name)) {
            name = "";
        }
        if (name.startsWith(EnumConstant.File_Path_Separator)) {
            name = name.substring(1);
        }
        if (name.endsWith(EnumConstant.File_Path_Separator)) {
            name = name.substring(0, name.length() - 1);
        }
        if (filePath.endsWith(EnumConstant.File_Path_Separator)) {
            return filePath + name;
        } else {
            return filePath + EnumConstant.File_Path_Separator + name;
        }
    }

    /**
     * 上级路径，以“/”号结尾
     *
     * @param filePath 上级路径
     */
    public static String getFilePath(String filePath) {
        if (filePath.endsWith(EnumConstant.File_Path_Separator)) {
            return filePath;
        } else {
            return filePath + EnumConstant.File_Path_Separator;
        }
    }
}
