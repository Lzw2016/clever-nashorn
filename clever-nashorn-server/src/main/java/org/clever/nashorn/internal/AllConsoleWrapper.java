package org.clever.nashorn.internal;

import org.clever.nashorn.dto.response.DebugConsoleRes;
import org.clever.nashorn.websocket.looklogs.ListenerLogsTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-09-28 21:14 <br/>
 */
public class AllConsoleWrapper extends AbstractConsole {

    private final Logger log;

    private AllConsoleWrapper(String bizType, String groupName, String filePath, String fileName) {
        super(bizType, groupName, filePath, fileName);
        this.log = LoggerFactory.getLogger(getConsoleName());
    }

    public AllConsoleWrapper(String bizType, String groupName, String filePath) {
        super(bizType, groupName, filePath);
        this.log = LoggerFactory.getLogger(getConsoleName());
    }

    @Override
    protected void doLog(String logsText, List<Object> args) {
        if (log.isInfoEnabled()) {
            log.info(logsText);
        }
        DebugConsoleRes res = DebugConsoleRes.newLog(this.getBizType(), this.getGroupName(), this.getFilePath(), this.getFileName(), logsText, args);
        ListenerLogsTask.sendMessage(this, res);
    }

    @Override
    public void doTrace(String logsText, List<Object> args) {
        if (log.isTraceEnabled()) {
            log.trace(logsText);
        }
        DebugConsoleRes res = DebugConsoleRes.newTrace(this.getBizType(), this.getGroupName(), this.getFilePath(), this.getFileName(), logsText, args);
        ListenerLogsTask.sendMessage(this, res);
    }

    @Override
    public void doDebug(String logsText, List<Object> args) {
        if (log.isDebugEnabled()) {
            log.debug(logsText);
        }
        DebugConsoleRes res = DebugConsoleRes.newDebug(this.getBizType(), this.getGroupName(), this.getFilePath(), this.getFileName(), logsText, args);
        ListenerLogsTask.sendMessage(this, res);
    }

    @Override
    public void doInfo(String logsText, List<Object> args) {
        if (log.isInfoEnabled()) {
            log.info(logsText);
        }
        DebugConsoleRes res = DebugConsoleRes.newInfo(this.getBizType(), this.getGroupName(), this.getFilePath(), this.getFileName(), logsText, args);
        ListenerLogsTask.sendMessage(this, res);
    }

    @Override
    public void doWarn(String logsText, List<Object> args) {
        if (log.isWarnEnabled()) {
            log.warn(logsText);
        }
        DebugConsoleRes res = DebugConsoleRes.newWarn(this.getBizType(), this.getGroupName(), this.getFilePath(), this.getFileName(), logsText, args);
        ListenerLogsTask.sendMessage(this, res);
    }

    @Override
    public void doError(String logsText, List<Object> args) {
        if (log.isErrorEnabled()) {
            log.error(logsText);
        }
        DebugConsoleRes res = DebugConsoleRes.newError(this.getBizType(), this.getGroupName(), this.getFilePath(), this.getFileName(), logsText, args);
        ListenerLogsTask.sendMessage(this, res);
    }

    @Override
    public Console creat(String filePath, String fileName) {
        return new AllConsoleWrapper(this.getBizType(), this.getGroupName(), filePath, fileName);
    }
}
