package org.clever.nashorn.websocket.debug;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.IDCreateUtils;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.cache.JsCodeFileCache;
import org.clever.nashorn.cache.MemoryJsCodeFileCache;
import org.clever.nashorn.dto.request.DebugReq;
import org.clever.nashorn.entity.EnumConstant;
import org.clever.nashorn.folder.DatabaseFolder;
import org.clever.nashorn.folder.Folder;
import org.clever.nashorn.internal.CommonUtils;
import org.clever.nashorn.internal.Console;
import org.clever.nashorn.module.cache.MemoryModuleCache;
import org.clever.nashorn.websocket.Task;
import org.clever.nashorn.websocket.TaskType;
import org.clever.nashorn.websocket.WebSocketConsole;
import org.springframework.web.socket.WebSocketSession;

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
        // (uuid)path
        super(String.format("(%s)%s", IDCreateUtils.uuid(), debugReq.getFileFullPath()), TaskType.DebugJs);
        // Folder rootFolder = FileSystemFolder.create(new File(debugReq.getFilePath()));
        JsCodeFileCache jsCodeFileCache = MemoryJsCodeFileCache.getInstance();
        Folder rootFolder = new DatabaseFolder(EnumConstant.DefaultBizType, EnumConstant.DefaultGroupName, jsCodeFileCache);
        MemoryModuleCache cache = new MemoryModuleCache();
        Console console = new WebSocketConsole(debugReq.getFileFullPath(), this);
        Map<String, Object> context = new HashMap<>(1);
        context.put("CommonUtils", CommonUtils.Instance);
        scriptModuleInstance = new ScriptModuleInstance(rootFolder, cache, console, context);
        this.runTimeOut = 5;
    }

    @Override
    protected void doStart(DebugReq message, boolean verify) {
        execTask(() -> {
            ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs(message.getFileFullPath());
            scriptObjectMirror.callMember(message.getFucName());
            // Thread.sleep(50);
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
