package org.clever.nashorn.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Console 内容输出到日志
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/21 17:38 <br/>
 */
public class LogConsole extends AbstractConsole {

    private final Logger log;

    public LogConsole(String filePath, String fileName) {
        super(filePath, fileName);
        this.log = LoggerFactory.getLogger(fileName);
    }

    /**
     * 创建 root Console
     *
     * @param filePath root文件路径
     */
    public LogConsole(String filePath) {
        super(filePath);
        this.log = LoggerFactory.getLogger(this.getFileName());
    }

    @Override
    public void log(Object... args) {
        if (log.isInfoEnabled()) {
            log.info(logString(args));
        }
    }

    @Override
    public void trace(Object... args) {
        if (log.isTraceEnabled()) {
            log.trace(logString(args));
        }
    }

    @Override
    public void debug(Object... args) {
        if (log.isDebugEnabled()) {
            log.debug(logString(args));
        }
    }

    @Override
    public void info(Object... args) {
        if (log.isInfoEnabled()) {
            log.info(logString(args));
        }
    }

    @Override
    public void warn(Object... args) {
        if (log.isWarnEnabled()) {
            log.warn(logString(args));
        }
    }

    @Override
    public void error(Object... args) {
        if (log.isErrorEnabled()) {
            log.error(logString(args));
        }
    }

    @Override
    public Console creat(String filePath, String fileName) {
        return new LogConsole(filePath, fileName);
    }
}
