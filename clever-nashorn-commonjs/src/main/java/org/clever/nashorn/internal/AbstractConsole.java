package org.clever.nashorn.internal;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.tuples.TupleTow;
import org.clever.nashorn.module.Module;
import org.clever.nashorn.utils.StrFormatter;

import java.util.Arrays;
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
    private final String bizType;
    @Getter
    private final String groupName;
    @Getter
    private final String filePath;
    @Getter
    private final String fileName;

    /**
     * @param filePath 文件路径
     * @param fileName 文件名称
     */
    protected AbstractConsole(String bizType, String groupName, String filePath, String fileName) {
        this.bizType = bizType;
        this.groupName = groupName;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    /**
     * 创建 root Console
     *
     * @param filePath root文件路径
     */
    public AbstractConsole(String bizType, String groupName, String filePath) {
        this(bizType, groupName, filePath, Module.Root_Filename);
    }

    /**
     * 根据日志输出参数得到日志字符串
     */
    protected String logString(Object... args) {
        if (args == null || args.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(args.length * 32);
        for (Object arg : args) {
            String str = StrFormatter.toString(arg);
            TupleTow<String, Boolean> tupleTow = overflow(str);
            sb.append(tupleTow.getValue1());
            if (tupleTow.getValue2()) {
                break;
            }
        }
        return overflow(sb.toString()).getValue1();
    }

    /**
     * 字符串溢出处理
     */
    @SuppressWarnings("ConstantConditions")
    protected TupleTow<String, Boolean> overflow(String str) {
        boolean overflow = false;
        if (StringUtils.isNotBlank(str) && Max_Len < str.length()) {
            int end = Max_Len - Overflow_Suffix.length();
            if (end < 0) {
                end = 0;
            }
            str = str.substring(0, end) + Overflow_Suffix;
            overflow = true;
        }
        return TupleTow.creat(str, overflow);
    }

    /**
     * 日志格式化处理
     */
    protected String format(final String format, final Object... argArray) {
        return StrFormatter.format(format, argArray);
    }

    /**
     * 获取控制台名称
     */
    protected String getConsoleName() {
        return String.format("%s.%s#%s", bizType, groupName, FilenameUtils.concat(filePath, fileName));
    }

    @Override
    public void log(Object... args) {
        String logsText = logString(args);
        doLog(logsText, Arrays.asList(args));
    }

    @Override
    public void trace(Object... args) {
        String logsText = logString(args);
        doTrace(logsText, Arrays.asList(args));
    }

    @Override
    public void debug(Object... args) {
        String logsText = logString(args);
        doDebug(logsText, Arrays.asList(args));
    }

    @Override
    public void info(Object... args) {
        String logsText = logString(args);
        doInfo(logsText, Arrays.asList(args));
    }

    @Override
    public void warn(Object... args) {
        String logsText = logString(args);
        doWarn(logsText, Arrays.asList(args));
    }

    @Override
    public void error(Object... args) {
        String logsText = logString(args);
        doError(logsText, Arrays.asList(args));
    }

    protected abstract void doLog(String logsText, List<Object> args);

    protected abstract void doTrace(String logsText, List<Object> args);

    protected abstract void doDebug(String logsText, List<Object> args);

    protected abstract void doInfo(String logsText, List<Object> args);

    protected abstract void doWarn(String logsText, List<Object> args);

    protected abstract void doError(String logsText, List<Object> args);
}
