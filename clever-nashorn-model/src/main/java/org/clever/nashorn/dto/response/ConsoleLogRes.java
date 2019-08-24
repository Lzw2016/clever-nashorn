package org.clever.nashorn.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.nashorn.model.WebSocketTaskRes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/24 16:33 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ConsoleLogRes extends WebSocketTaskRes {

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

    public ConsoleLogRes(String level, String log, List<Object> logs) {
        this.level = level;
        this.log = log;
        this.logs = logs == null ? new ArrayList<Object>() {{
            add(log);
        }} : logs;
        this.setType(Type_Log);
    }

    public static ConsoleLogRes newTrace(String log, List<Object> logs) {
        return new ConsoleLogRes(Level_Trace, log, logs);
    }

    public static ConsoleLogRes newDebug(String log, List<Object> logs) {
        return new ConsoleLogRes(Level_Debug, log, logs);
    }

    public static ConsoleLogRes newInfo(String log, List<Object> logs) {
        return new ConsoleLogRes(Level_Info, log, logs);
    }

    public static ConsoleLogRes newWarn(String log, List<Object> logs) {
        return new ConsoleLogRes(Level_Warn, log, logs);
    }

    public static ConsoleLogRes newError(String log, List<Object> logs) {
        return new ConsoleLogRes(Level_Error, log, logs);
    }
}
