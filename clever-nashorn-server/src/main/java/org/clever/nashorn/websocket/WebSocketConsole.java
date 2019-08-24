package org.clever.nashorn.websocket;

import org.clever.nashorn.dto.response.DebugConsoleRes;
import org.clever.nashorn.internal.AbstractConsole;
import org.clever.nashorn.internal.Console;
import org.clever.nashorn.utils.StrFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/22 10:49 <br/>
 */
public class WebSocketConsole extends AbstractConsole {

    /**
     * WebSocket任务
     */
    private final Task task;

    /**
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @param task     WebSocket任务
     */
    private WebSocketConsole(String filePath, String fileName, Task task) {
        super(filePath, fileName);
        this.task = task;
    }

    /**
     * 创建 root Console
     *
     * @param filePath root文件路径
     * @param task     WebSocket任务
     */
    public WebSocketConsole(String filePath, Task task) {
        super(filePath);
        this.task = task;
    }

    private List<Object> getLogs(final Object... args) {
        if (args == null) {
            return new ArrayList<>();
        }
        String format;
        if (args.length >= 1 && args[0] instanceof String) {
            format = (String) args[0];
            if (args.length <= 1) {
                return new ArrayList<Object>() {{
                    add(format);
                }};
            }
            Object[] argList = new Object[args.length - 1];
            for (int index = 0; index < args.length; index++) {
                if (index == 0) {
                    continue;
                }
                argList[index - 1] = args[index];
            }
            return StrFormatter.formatToList(format, argList);
        }
        format = "{}";
        return StrFormatter.formatToList(format, args);
    }

    @Override
    public void log(Object... args) {
        task.sendMessage(DebugConsoleRes.newInfo(this.getFilePath(), this.getFileName(), logString(args), getLogs(args)));
    }

    @Override
    public void trace(Object... args) {
        task.sendMessage(DebugConsoleRes.newTrace(this.getFilePath(), this.getFileName(), logString(args), getLogs(args)));
    }

    @Override
    public void debug(Object... args) {
        task.sendMessage(DebugConsoleRes.newDebug(this.getFilePath(), this.getFileName(), logString(args), getLogs(args)));
    }

    @Override
    public void info(Object... args) {
        task.sendMessage(DebugConsoleRes.newInfo(this.getFilePath(), this.getFileName(), logString(args), getLogs(args)));
    }

    @Override
    public void warn(Object... args) {
        task.sendMessage(DebugConsoleRes.newWarn(this.getFilePath(), this.getFileName(), logString(args), getLogs(args)));
    }

    @Override
    public void error(Object... args) {
        task.sendMessage(DebugConsoleRes.newError(this.getFilePath(), this.getFileName(), logString(args), getLogs(args)));
    }

    @Override
    public Console creat(String filePath, String fileName) {
        return new WebSocketConsole(filePath, fileName, task);
    }
}
