package org.clever.nashorn.internal;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志写入数据库实现
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 11:12 <br/>
 */
public class DatabaseLogConsole extends AbstractConsole {

    private final Logger log;

    public DatabaseLogConsole(String filePath, String fileName) {
        super(filePath, fileName);
        this.log = LoggerFactory.getLogger(FilenameUtils.concat(filePath, fileName));
    }

    @Override
    public void log(Object... args) {

    }

    @Override
    public void trace(Object... args) {

    }

    @Override
    public void debug(Object... args) {

    }

    @Override
    public void info(Object... args) {

    }

    @Override
    public void warn(Object... args) {

    }

    @Override
    public void error(Object... args) {

    }

    @Override
    public Console creat(String filePath, String fileName) {
        return null;
    }
}
