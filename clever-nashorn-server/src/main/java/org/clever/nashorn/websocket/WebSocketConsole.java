package org.clever.nashorn.websocket;

import org.clever.common.utils.tuples.TupleTow;
import org.clever.nashorn.dto.response.DebugConsoleRes;
import org.clever.nashorn.internal.AbstractConsole;
import org.clever.nashorn.internal.Console;

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
    private WebSocketConsole(String bizType, String groupName, String filePath, String fileName, Task task) {
        super(bizType, groupName, filePath, fileName);
        this.task = task;
    }

    /**
     * 创建 root Console
     *
     * @param filePath root文件路径
     * @param task     WebSocket任务
     */
    public WebSocketConsole(String bizType, String groupName, String filePath, Task task) {
        super(bizType, groupName, filePath);
        this.task = task;
    }

    /**
     * debug不需要做溢出处理
     */
    @Override
    protected TupleTow<String, Boolean> overflow(String str) {
        return TupleTow.creat(str, false);
    }

    @Override
    protected void doLog(String logsText, List<Object> args) {
        task.sendMessage(DebugConsoleRes.newLog(this.getBizType(), this.getGroupName(), this.getFilePath(), this.getFileName(), logsText, args));
    }

    @Override
    protected void doTrace(String logsText, List<Object> args) {
        task.sendMessage(DebugConsoleRes.newTrace(this.getBizType(), this.getGroupName(), this.getFilePath(), this.getFileName(), logsText, args));
    }

    @Override
    protected void doDebug(String logsText, List<Object> args) {
        task.sendMessage(DebugConsoleRes.newDebug(this.getBizType(), this.getGroupName(), this.getFilePath(), this.getFileName(), logsText, args));
    }

    @Override
    protected void doInfo(String logsText, List<Object> args) {
        task.sendMessage(DebugConsoleRes.newInfo(this.getBizType(), this.getGroupName(), this.getFilePath(), this.getFileName(), logsText, args));
    }

    @Override
    protected void doWarn(String logsText, List<Object> args) {
        task.sendMessage(DebugConsoleRes.newWarn(this.getBizType(), this.getGroupName(), this.getFilePath(), this.getFileName(), logsText, args));
    }

    @Override
    protected void doError(String logsText, List<Object> args) {
        task.sendMessage(DebugConsoleRes.newError(this.getBizType(), this.getGroupName(), this.getFilePath(), this.getFileName(), logsText, args));
    }

    @Override
    public Console creat(String filePath, String fileName) {
        return new WebSocketConsole(this.getBizType(), this.getGroupName(), filePath, fileName, task);
    }
}
