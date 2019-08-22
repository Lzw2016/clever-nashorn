package org.clever.nashorn.internal;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.mapper.JacksonMapper;
import org.clever.nashorn.module.Module;
import org.clever.nashorn.utils.ScriptEngineUtils;
import org.clever.nashorn.utils.StrFormatter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/22 09:30 <br/>
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractConsole implements Console {
    /**
     * 输出最大长度
     */
    public static final int Max_Len = 1024 * 8;
    /**
     * 日志溢出时的后缀
     */
    public static final String Overflow_Suffix = "...";

    @Getter
    private final String filePath;
    @Getter
    private final String fileName;

    /**
     * @param filePath 文件路径
     * @param fileName 文件名称
     */
    public AbstractConsole(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    /**
     * 创建 root Console
     * @param filePath root文件路径
     */
    public AbstractConsole(String filePath) {
        this(filePath, Module.Root_Filename);
    }

    /**
     * 根据日志输出参数得到日志字符串
     */
    protected String logString(Object... args) {
        if (args == null) {
            return "";
        }
        String format;
        if (args.length >= 1 && args[0] instanceof String) {
            format = (String) args[0];
        } else {
            format = "{}";
        }
        List<String> list = null;

        for (int index = 0; index < args.length; index++) {
            if (index <= 0) {
                continue;
            }
            if (list == null) {
                list = new ArrayList<>(args.length - 1);
            }
            String str = toString(args[index]);
            list.add(str);
        }
        if (list == null) {
            return "";
        } else {
            return overflow(format(format, list.toArray()));
        }
    }

    /**
     * 单个对象转成字符串
     */
    protected String toString(Object object) {
        if (object == null) {
            return null;
        }
        String str;
        if (object instanceof Byte
                || object instanceof Short
                || object instanceof Integer
                || object instanceof Long
                || object instanceof Float
                || object instanceof Double
                || object instanceof BigInteger
                || object instanceof BigDecimal
                || object instanceof Boolean
                || object instanceof String) {
            str = String.valueOf(object);
        } else if (object instanceof ScriptObjectMirror) {
            ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) object;
            if (scriptObjectMirror.isFunction() || scriptObjectMirror.isStrictFunction()) {
                str = scriptObjectMirror.toString();
            } else {
                str = ScriptEngineUtils.stringify(scriptObjectMirror);
            }
        } else {
            str = JacksonMapper.nonEmptyMapper().toJson(object);
        }
        return overflow(str);
    }

    /**
     * 字符串溢出处理
     */
    @SuppressWarnings("ConstantConditions")
    protected String overflow(String str) {
        if (StringUtils.isNotBlank(str) && Max_Len < str.length()) {
            int end = Max_Len - Overflow_Suffix.length();
            if (end < 0) {
                end = 0;
            }
            str = str.substring(0, end) + Overflow_Suffix;
        }
        return str;
    }

    /**
     * 日志格式化处理
     */
    protected String format(final String format, final Object... argArray) {
        return StrFormatter.format(format, argArray);
    }
}
