package org.clever.nashorn.internal;

import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.DateTimeUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基本工具类
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/24 12:32 <br/>
 */
public class CommonUtils {

    public static final CommonUtils Instance = new CommonUtils();

    /**
     * 休眠一段时间
     *
     * @param millis 毫秒
     */
    public static void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    /**
     * 获取对象16进制的 hashcode
     */
    public static String hexHashCode(Object object) {
        if (object == null) {
            return null;
        }
        return Integer.toHexString(object.hashCode());
    }

    /**
     * 获取对象的 hashcode
     */
    public static Integer hashCode(Object object) {
        if (object == null) {
            return null;
        }
        return object.hashCode();
    }

    /**
     * 两个对象 equals
     */
    public static boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    private static final Pattern Date_Pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z");

    /**
     * 把时间格式化成标准的格式(只支持格式 2019-08-26T08:35:24.566Z)
     */
    public static String formatDate(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        if (str.length() != 24) {
            return str;
        }
        Matcher matcher = Date_Pattern.matcher(str);
        if (!matcher.matches()) {
            return str;
        }
        try {
            Date date = DateTimeUtils.parseDate(str, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            return DateTimeUtils.formatToString(date);
        } catch (ParseException e) {
            return str;
        }
    }
}
