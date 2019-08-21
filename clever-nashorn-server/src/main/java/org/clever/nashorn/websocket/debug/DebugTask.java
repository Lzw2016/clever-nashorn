package org.clever.nashorn.websocket.debug;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.IDCreateUtils;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.dto.request.DebugReq;
import org.clever.nashorn.websocket.Task;
import org.clever.nashorn.websocket.TaskType;
import org.springframework.web.socket.WebSocketSession;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/21 17:39 <br/>
 */
@Slf4j
public class DebugTask extends Task<DebugReq> {

    private final ScriptModuleInstance scriptModuleInstance;

    public DebugTask(DebugReq debugReq) {
        super(String.format("%s-%s/%s", IDCreateUtils.shortUuid(), debugReq.getFilePath(), debugReq.getFileName()));
        scriptModuleInstance = ScriptModuleInstance.creatDefault(debugReq.getFilePath());
    }

    @Override
    protected TaskType getTaskType() {
        return TaskType.DebugJs;
    }

    @Override
    protected void doStart(DebugReq message, boolean verify) {
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs(message.getFileName());
        scriptObjectMirror.callMember(message.getFucName(), "DebugTask");
        stop();
    }

    @Override
    protected void doDestroy() {
    }

    @Override
    protected void handleMessage(WebSocketSession session, DebugReq message, boolean verify) {
    }
}
