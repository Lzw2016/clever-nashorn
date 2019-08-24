package org.clever.nashorn.websocket.debug;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.IDCreateUtils;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.dto.request.DebugReq;
import org.clever.nashorn.folder.FilesystemFolder;
import org.clever.nashorn.folder.Folder;
import org.clever.nashorn.internal.CommonUtils;
import org.clever.nashorn.internal.Console;
import org.clever.nashorn.module.cache.MemoryModuleCache;
import org.clever.nashorn.websocket.Task;
import org.clever.nashorn.websocket.TaskType;
import org.clever.nashorn.websocket.WebSocketConsole;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/21 17:39 <br/>
 */
@Slf4j
public class DebugTask extends Task<DebugReq> {

    private final ScriptModuleInstance scriptModuleInstance;

    public DebugTask(DebugReq debugReq) {
        super(String.format("%s-%s/%s", IDCreateUtils.shortUuid(), debugReq.getFilePath(), debugReq.getFileName()), TaskType.DebugJs);
        Folder rootFolder = FilesystemFolder.create(new File(debugReq.getFilePath()));
        MemoryModuleCache cache = new MemoryModuleCache();
        Console console = new WebSocketConsole(debugReq.getFilePath(), this);
        Map<String, Object> context = new HashMap<>(1);
        context.put("CommonUtils", CommonUtils.Instance);
        scriptModuleInstance = new ScriptModuleInstance(rootFolder, cache, console, context);
    }

    @Override
    protected void doStart(DebugReq message, boolean verify) {
        execTask(() -> {
            ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs(message.getFileName());
            scriptObjectMirror.callMember(message.getFucName(), "DebugTask");
            stop();
        });
    }

    @Override
    protected void doStop() {
    }

    @Override
    protected void handleMessage(WebSocketSession session, DebugReq message, boolean verify) {
    }
}
