package org.clever.nashorn.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/22 10:55 <br/>
 */
@Data
public class DebugConsoleRes implements Serializable {

    public static final String Level_Trace = "trace";
    public static final String Level_Debug = "debug";
    public static final String Level_Info = "info";
    public static final String Level_Warn = "warn";
    public static final String Level_Error = "error";

    /**
     * 文件路径
     */
    private final String filePath;
    /**
     * 文件名称
     */
    private final String fileName;
    /**
     * 日志时间
     */
    public final Date logTime = new Date();
    /**
     * 日志级别 trace debug info warn error
     */
    private String level;
    /**
     * 日志内容
     */
    private String log;


    public DebugConsoleRes(String filePath, String fileName, String level, String log) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.level = level;
        this.log = log;
    }

    public static DebugConsoleRes newTrace(String filePath, String fileName, String log) {
        return new DebugConsoleRes(filePath, fileName, Level_Trace, log);
    }

    public static DebugConsoleRes newDebug(String filePath, String fileName, String log) {
        return new DebugConsoleRes(filePath, fileName, Level_Debug, log);
    }

    public static DebugConsoleRes newInfo(String filePath, String fileName, String log) {
        return new DebugConsoleRes(filePath, fileName, Level_Info, log);
    }

    public static DebugConsoleRes newWarn(String filePath, String fileName, String log) {
        return new DebugConsoleRes(filePath, fileName, Level_Warn, log);
    }

    public static DebugConsoleRes newError(String filePath, String fileName, String log) {
        return new DebugConsoleRes(filePath, fileName, Level_Error, log);
    }
}
