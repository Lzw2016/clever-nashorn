package org.clever.nashorn.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.nashorn.model.WebSocketTaskRes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/22 10:55 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DebugConsoleRes extends WebSocketTaskRes {

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

    /**
     * 日志对象
     */
    private List<Object> logs;

    public DebugConsoleRes(String filePath, String fileName, String level, String log, List<Object> logs) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.level = level;
        this.log = log;
        this.logs = logs == null ? new ArrayList<Object>() {{
            add(log);
        }} : logs;
        this.setType(Type_Log);
    }

    public static DebugConsoleRes newTrace(String filePath, String fileName, String log, List<Object> logs) {
        return new DebugConsoleRes(filePath, fileName, Level_Trace, log, logs);
    }

    public static DebugConsoleRes newDebug(String filePath, String fileName, String log, List<Object> logs) {
        return new DebugConsoleRes(filePath, fileName, Level_Debug, log, logs);
    }

    public static DebugConsoleRes newInfo(String filePath, String fileName, String log, List<Object> logs) {
        return new DebugConsoleRes(filePath, fileName, Level_Info, log, logs);
    }

    public static DebugConsoleRes newWarn(String filePath, String fileName, String log, List<Object> logs) {
        return new DebugConsoleRes(filePath, fileName, Level_Warn, log, logs);
    }

    public static DebugConsoleRes newError(String filePath, String fileName, String log, List<Object> logs) {
        return new DebugConsoleRes(filePath, fileName, Level_Error, log, logs);
    }
}
