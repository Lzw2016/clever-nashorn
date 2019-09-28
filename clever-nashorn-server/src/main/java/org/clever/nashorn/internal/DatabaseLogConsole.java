package org.clever.nashorn.internal;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 日志写入数据库实现
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 11:12 <br/>
 */
public class DatabaseLogConsole extends AbstractConsole {

    private final Logger log;

    public DatabaseLogConsole(String bizType, String groupName, String filePath, String fileName) {
        super(bizType, groupName, filePath, fileName);
        this.log = LoggerFactory.getLogger(FilenameUtils.concat(filePath, fileName));
    }

    @Override
    protected void doLog(String logsText, List<Object> args) {

    }

    @Override
    protected void doTrace(String logsText, List<Object> args) {

    }

    @Override
    protected void doDebug(String logsText, List<Object> args) {

    }

    @Override
    protected void doInfo(String logsText, List<Object> args) {

    }

    @Override
    protected void doWarn(String logsText, List<Object> args) {

    }

    @Override
    protected void doError(String logsText, List<Object> args) {

    }

    @Override
    public Console creat(String filePath, String fileName) {
        return null;
    }
}
