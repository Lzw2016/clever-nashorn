package org.clever.nashorn.test.websocket;

import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.websocket.debug.DebugHandler;
import org.junit.Test;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/21 20:45 <br/>
 */
@Slf4j
public class DebugHandlerTest {

    @Test
    public void t01() {
        DebugHandler debugHandler = new DebugHandler();
        log.info("### {}", debugHandler.getClazzByT());
    }
}
