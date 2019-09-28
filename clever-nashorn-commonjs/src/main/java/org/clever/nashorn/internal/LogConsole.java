package org.clever.nashorn.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Console 内容输出到日志
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/21 17:38 <br/>
 */
public class LogConsole extends AbstractConsole {

    private final Logger log;

    public LogConsole(String bizType, String groupName, String filePath, String fileName) {
        super(bizType, groupName, filePath, fileName);
        this.log = LoggerFactory.getLogger(getConsoleName());
    }

    /**
     * 创建 root Console
     *
     * @param filePath root文件路径
     */
    public LogConsole(String bizType, String groupName, String filePath) {
        super(bizType, groupName, filePath);
        this.log = LoggerFactory.getLogger(getConsoleName());
    }

    @Override
    protected void doLog(String logsText, List<Object> args) {
        if (log.isInfoEnabled()) {
            log.info(logsText);
        }
    }

    @Override
    public void doTrace(String logsText, List<Object> args) {
        if (log.isTraceEnabled()) {
            log.trace(logsText);
        }
    }

    @Override
    public void doDebug(String logsText, List<Object> args) {
        if (log.isDebugEnabled()) {
            log.debug(logsText);
        }
    }

    @Override
    public void doInfo(String logsText, List<Object> args) {
        if (log.isInfoEnabled()) {
            log.info(logsText);
        }
    }

    @Override
    public void doWarn(String logsText, List<Object> args) {
        if (log.isWarnEnabled()) {
            log.warn(logsText);
        }
    }

    @Override
    public void doError(String logsText, List<Object> args) {
        if (log.isErrorEnabled()) {
            log.error(logsText);
        }
    }

    @Override
    public Console creat(String filePath, String fileName) {
        return new LogConsole(this.getBizType(), this.getGroupName(), filePath, fileName);
    }
}
