package org.clever.nashorn.websocket.looklogs;

import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.dto.request.ListenerLogsReq;
import org.clever.nashorn.dto.response.ConsoleLogRes;
import org.clever.nashorn.websocket.Handler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/28 15:12 <br/>
 */
@Component
@Slf4j
public class ListenerLogsHandler extends Handler<ListenerLogsReq, ListenerLogsTask> {

    @Override
    public synchronized ListenerLogsTask creatTask(WebSocketSession session, ListenerLogsReq message, boolean verify) {
        if (!verify) {
            sendErrorMessage(session, ConsoleLogRes.newError("请求参数校验失败", null), true);
        }
        return ListenerLogsTask.getListenerLogsTask(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        super.afterConnectionClosed(session, closeStatus);
        ListenerLogsTask.stopClosedTask();
    }
}
