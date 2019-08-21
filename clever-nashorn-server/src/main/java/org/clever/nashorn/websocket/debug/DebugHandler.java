package org.clever.nashorn.websocket.debug;

import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.dto.request.DebugReq;
import org.clever.nashorn.websocket.Handler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/21 17:38 <br/>
 */
@Component
@Slf4j
public class DebugHandler extends Handler<DebugReq, DebugTask> {

    @Override
    public DebugTask handleFirstMessage(WebSocketSession session, DebugReq message, boolean verify) {
        return new DebugTask(message);
    }
}
