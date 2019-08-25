package org.clever.nashorn.utils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.Undefined;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.DateTimeUtils;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.common.utils.mapper.JacksonMapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 字符串格式化工具
 */
@SuppressWarnings("WeakerAccess")
public class StrFormatter {
    public static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;
    public static final String EMPTY_JSON = "{}";
    public static final char C_BACKSLASH = '\\';
    public static final char C_DELIM_START = '{';

    /**
     * 格式化字符串<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
     *
     * @param strPattern 字符串模板
     * @param argArray   参数列表
     * @return 结果
     */
    public static List<Object> formatToList(final String strPattern, final Object... argArray) {
        List<Object> result = new ArrayList<>();
        if (StringUtils.isBlank(strPattern) || argArray == null || argArray.length <= 0) {
            result.add(strPattern);
            return result;
        }
        final int strPatternLength = strPattern.length();
        int handledPosition = 0;// 记录已经处理到的位置
        int delimIndex;// 占位符所在位置
        for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
            delimIndex = strPattern.indexOf(EMPTY_JSON, handledPosition);
            if (delimIndex == -1) {// 剩余部分无占位符
                if (handledPosition == 0) { // 不带占位符的模板直接返回
                    result.add(strPattern);
                    return result;
                }
                // 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
                result.add(strPattern.substring(handledPosition, strPatternLength));
                return result;
            }
            // 转义符
            if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == C_BACKSLASH) {// 转义符
                if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == C_BACKSLASH) {// 双转义符
                    // 转义符之前还有一个转义符，占位符依旧有效
                    result.add(strPattern.substring(handledPosition, delimIndex - 1));
                    result.add(argArray[argIndex]);
                    handledPosition = delimIndex + 2;
                } else {
                    // 占位符被转义
                    argIndex--;
                    result.add(strPattern.substring(handledPosition, delimIndex - 1));
                    result.add(C_DELIM_START);
                    handledPosition = delimIndex + 1;
                }
            } else {// 正常占位符
                result.add(strPattern.substring(handledPosition, delimIndex));
                result.add(argArray[argIndex]);
                handledPosition = delimIndex + 2;
            }
        }
        // append the characters following the last {} pair.
        // 加入最后一个占位符后所有的字符
        result.add(strPattern.substring(handledPosition));
        return result;
    }

    /**
     * 格式化字符串<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
     *
     * @param strPattern 字符串模板
     * @param argArray   参数列表
     * @return 结果
     */
    public static String format(final String strPattern, final Object... argArray) {
        if (StringUtils.isBlank(strPattern) || argArray == null || argArray.length <= 0) {
            return strPattern;
        }
        final int strPatternLength = strPattern.length();
        // 初始化定义好的长度以获得更好的性能
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50);
        int handledPosition = 0;// 记录已经处理到的位置
        int delimIndex;// 占位符所在位置
        for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
            delimIndex = strPattern.indexOf(EMPTY_JSON, handledPosition);
            if (delimIndex == -1) {// 剩余部分无占位符
                if (handledPosition == 0) { // 不带占位符的模板直接返回
                    return strPattern;
                }
                // 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
                sbuf.append(strPattern, handledPosition, strPatternLength);
                return sbuf.toString();
            }
            // 转义符
            if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == C_BACKSLASH) {// 转义符
                if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == C_BACKSLASH) {// 双转义符
                    // 转义符之前还有一个转义符，占位符依旧有效
                    sbuf.append(strPattern, handledPosition, delimIndex - 1);
                    sbuf.append(utf8Str(argArray[argIndex]));
                    handledPosition = delimIndex + 2;
                } else {
                    // 占位符被转义
                    argIndex--;
                    sbuf.append(strPattern, handledPosition, delimIndex - 1);
                    sbuf.append(C_DELIM_START);
                    handledPosition = delimIndex + 1;
                }
            } else {// 正常占位符
                sbuf.append(strPattern, handledPosition, delimIndex);
                sbuf.append(utf8Str(argArray[argIndex]));
                handledPosition = delimIndex + 2;
            }
        }
        // append the characters following the last {} pair.
        // 加入最后一个占位符后所有的字符
        sbuf.append(strPattern, handledPosition, strPattern.length());
        return sbuf.toString();
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String utf8Str(Object obj) {
        return str(obj, CHARSET_UTF_8);
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj     对象
     * @param charset 字符集
     * @return 字符串
     */
    @SuppressWarnings("unused")
    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[]) {
            return str(obj, charset);
        } else if (obj instanceof Byte[]) {
            return str(obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return str(obj, charset);
        } else if (isArray(obj)) {
            return toString(obj);
        }
        return toString(obj);
    }

    /**
     * 是否空白符<br>
     * 空白符包括空格、制表符、全角空格和不间断空格<br>
     *
     * @param c 字符
     * @return 是否空白符
     * @see Character#isWhitespace(int)
     * @see Character#isSpaceChar(int)
     * @since 4.0.10
     */
    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == '\ufeff' || c == '\u202a';
    }

    /**
     * 是否空白符<br>
     * 空白符包括空格、制表符、全角空格和不间断空格<br>
     *
     * @param c 字符
     * @return 是否空白符
     * @see Character#isWhitespace(int)
     * @see Character#isSpaceChar(int)
     * @since 4.0.10
     */
    public static boolean isBlankChar(char c) {
        return isBlankChar((int) c);
    }

    /**
     * 字符串是否为空白 空白的定义如下： <br>
     * 1、为null <br>
     * 2、为不可见字符（如空格）<br>
     * 3、""<br>
     *
     * @param str 被检测的字符串
     * @return 是否为空
     */
    public static boolean isBlank(CharSequence str) {
        int length;
        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            // 只要有一个非空字符即为非空字符串
            if (!isBlankChar(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将byte数组转为字符串
     *
     * @param bytes   byte数组
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(byte[] bytes, String charset) {
        return str(bytes, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 解码字节码
     *
     * @param data    字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String str(byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        if (null == charset) {
            return new String(data);
        }
        return new String(data, charset);
    }

    /**
     * 将Byte数组转为字符串
     *
     * @param bytes   byte数组
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(Byte[] bytes, String charset) {
        return str(bytes, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 解码字节码
     *
     * @param data    字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String str(Byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        byte[] bytes = new byte[data.length];
        Byte dataByte;
        for (int i = 0; i < data.length; i++) {
            dataByte = data[i];
            bytes[i] = (null == dataByte) ? -1 : dataByte;
        }

        return str(bytes, charset);
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data    数据
     * @param charset 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String str(ByteBuffer data, String charset) {
        if (data == null) {
            return null;
        }

        return str(data, Charset.forName(charset));
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data    数据
     * @param charset 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String str(ByteBuffer data, Charset charset) {
        if (null == charset) {
            charset = Charset.defaultCharset();
        }
        return charset.decode(data).toString();
    }

    /**
     * {@link CharSequence} 转为字符串，null安全
     *
     * @param cs {@link CharSequence}
     * @return 字符串
     */
    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }

    /**
     * 数组或集合转String
     *
     * @param obj 集合或数组对象
     * @return 数组字符串，与集合转字符串格式相同
     */
    @SuppressWarnings("ConstantConditions")
    public static String toString(Object obj) {
        if (null == obj) {
            return null;
        }
        if (isArray(obj)) {
            try {
                return Arrays.deepToString((Object[]) obj);
            } catch (Exception e) {
                final String className = obj.getClass().getComponentType().getName();
                switch (className) {
                    case "long":
                        return Arrays.toString((long[]) obj);
                    case "int":
                        return Arrays.toString((int[]) obj);
                    case "short":
                        return Arrays.toString((short[]) obj);
                    case "char":
                        return Arrays.toString((char[]) obj);
                    case "byte":
                        return Arrays.toString((byte[]) obj);
                    case "boolean":
                        return Arrays.toString((boolean[]) obj);
                    case "float":
                        return Arrays.toString((float[]) obj);
                    case "double":
                        return Arrays.toString((double[]) obj);
                    default:
                        throw ExceptionUtils.unchecked(e);
                }
            }
        }
        String str;
        if (obj instanceof Byte
                || obj instanceof Short
                || obj instanceof Integer
                || obj instanceof Long
                || obj instanceof Float
                || obj instanceof Double
                || obj instanceof BigInteger
                || obj instanceof BigDecimal
                || obj instanceof Boolean
                || obj instanceof String) {
            str = String.valueOf(obj);
        } else if (obj instanceof Date) {
            str = DateTimeUtils.formatToString((Date) obj);
        } else if (obj instanceof Undefined) {
            str = obj.toString();
        } else if (obj instanceof ScriptObjectMirror) {
            ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) obj;
            // jdk.nashorn.internal.objects.NativeDate
            // scriptObjectMirror.getScriptObject();
            if (scriptObjectMirror.isFunction() || scriptObjectMirror.isStrictFunction()) {
                str = scriptObjectMirror.toString();
            } else {
                // TODO 处理JS function
                str = ScriptEngineUtils.stringify(scriptObjectMirror);
            }
        } else {
            str = JacksonMapper.getInstance().toJson(obj);
        }
        return str;
    }

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        if (null == obj) {
            // throw new NullPointerException("Object check for isArray is null");
            return false;
        }
        return obj.getClass().isArray();
    }
}
