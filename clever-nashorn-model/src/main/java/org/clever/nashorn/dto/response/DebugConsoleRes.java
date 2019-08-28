package org.clever.nashorn.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/22 10:55 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DebugConsoleRes extends ConsoleLogRes {

    /**
     * 文件路径
     */
    private final String filePath;
    /**
     * 文件名称
     */
    private final String fileName;

    public DebugConsoleRes(String filePath, String fileName, String level, String log, List<Object> logs) {
        super(level, log, logs == null ? new ArrayList<Object>() {{
            add(log);
        }} : logs);
        this.filePath = filePath;
        this.fileName = fileName;
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
