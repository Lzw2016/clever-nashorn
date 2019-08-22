package org.clever.nashorn.websocket;

import org.clever.nashorn.dto.response.DebugConsoleRes;
import org.clever.nashorn.internal.AbstractConsole;
import org.clever.nashorn.internal.Console;

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

    @Override
    public void log(Object... args) {
        task.sendMessage(DebugConsoleRes.newInfo(this.getFilePath(), this.getFileName(), logString(args)));
    }

    @Override
    public void trace(Object... args) {
        task.sendMessage(DebugConsoleRes.newTrace(this.getFilePath(), this.getFileName(), logString(args)));
    }

    @Override
    public void debug(Object... args) {
        task.sendMessage(DebugConsoleRes.newDebug(this.getFilePath(), this.getFileName(), logString(args)));
    }

    @Override
    public void info(Object... args) {
        task.sendMessage(DebugConsoleRes.newInfo(this.getFilePath(), this.getFileName(), logString(args)));
    }

    @Override
    public void warn(Object... args) {
        task.sendMessage(DebugConsoleRes.newWarn(this.getFilePath(), this.getFileName(), logString(args)));
    }

    @Override
    public void error(Object... args) {
        task.sendMessage(DebugConsoleRes.newError(this.getFilePath(), this.getFileName(), logString(args)));
    }

    @Override
    public Console creat(String filePath, String fileName) {
        return new WebSocketConsole(filePath, fileName, task);
    }
}
