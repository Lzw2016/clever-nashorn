package org.clever.nashorn.websocket.looklogs;

import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.dto.request.ListenerLogsReq;
import org.clever.nashorn.websocket.Task;
import org.clever.nashorn.websocket.TaskType;
import org.springframework.web.socket.WebSocketSession;

/**
 * ObjectMapper
 * 作者：lizw <br/>
 * 创建时间：2019/09/28 15:11 <br/>
 */
@Slf4j
public class ListenerLogsTask extends Task<ListenerLogsReq> {
    private static final String Task_Id = "Listener-Logs-Task";

    public ListenerLogsTask() {
        super(Task_Id, TaskType.ListenerLogs);
        this.runTimeOut = -1;
    }

    @Override
    protected void doStart(ListenerLogsReq message, boolean verify) {
        log.info("ListenerLogsTask-doStart");
    }

    @Override
    protected void doStop() {
        log.info("ListenerLogsTask-doStop");
    }

    @Override
    protected void handleMessage(WebSocketSession session, ListenerLogsReq message, boolean verify) {
        log.info("ListenerLogsTask-handleMessage");
    }
}
