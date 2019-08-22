package org.clever.nashorn.modules;

import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.internal.LogConsole;
import org.junit.Test;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/22 09:52 <br/>
 */
@Slf4j
public class LogConsoleTest {

    private LogConsole logConsole = new LogConsole("/", "test");

    @Test
    public void t1() {
        logConsole.log("{}", "1234567890");
        logConsole.log("");
    }
}
