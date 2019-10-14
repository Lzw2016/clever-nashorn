package org.clever.nashorn.internal;

import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.DateTimeUtils;
import org.clever.nashorn.utils.ObjectConvertUtils;

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
@SuppressWarnings("unused")
public class CommonUtils {
    /**
     * 时间格式
     */
    private static final Pattern Date_Pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z");

    public static final CommonUtils Instance = new CommonUtils();

    private CommonUtils() {
    }

    /**
     * 休眠一段时间
     *
     * @param millis 毫秒
     */
    public void sleep(Number millis) throws InterruptedException {
        Thread.sleep(millis.longValue());
    }

    /**
     * 获取对象16进制的 hashcode
     */
    public String hexHashCode(Object object) {
        if (object == null) {
            return null;
        }
        return Integer.toHexString(object.hashCode());
    }

    /**
     * 获取对象的 hashcode
     */
    public Integer hashCode(Object object) {
        if (object == null) {
            return null;
        }
        return object.hashCode();
    }

    /**
     * 两个对象 equals
     */
    public boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    /**
     * 判断两个对象是不是同一个对象(内存地址相同)
     */
    public boolean same(Object a, Object b) {
        return a == b;
    }

    /**
     * 把时间格式化成标准的格式(只支持格式 2019-08-26T08:35:24.566Z)
     */
    public String formatDate(String str) {
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
        //return str.replace('T', ' ').substring(0, str.length() - 5);
        try {
            Date date = DateTimeUtils.parseDate(str, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            // 8个小时的时差
            date = DateTimeUtils.addHours(date, 8);
            return DateTimeUtils.formatToString(date);
        } catch (ParseException e) {
            return str;
        }
    }

    /**
     * Java对象转换成JS对象(慎用: 性能较差)
     */
    public Object javaToJsObject(Object obj) {
        return ObjectConvertUtils.Instance.javaToJSObject(obj);
    }

    // TODO String处理等工具类
}
