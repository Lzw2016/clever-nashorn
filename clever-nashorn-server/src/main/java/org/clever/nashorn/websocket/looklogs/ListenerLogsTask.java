package org.clever.nashorn.websocket.looklogs;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.dto.request.ListenerLogsReq;
import org.clever.nashorn.internal.AbstractConsole;
import org.clever.nashorn.utils.JsCodeFilePathUtils;
import org.clever.nashorn.websocket.Task;
import org.clever.nashorn.websocket.TaskType;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ObjectMapper
 * 作者：lizw <br/>
 * 创建时间：2019/09/28 15:11 <br/>
 */
@SuppressWarnings("WeakerAccess")
@Slf4j
public class ListenerLogsTask extends Task<ListenerLogsReq> {
    private static final String Task_Id = "Listener-Logs-Task";
    /**
     * 监听的js文件名 -> ListenerLogsTask
     */
    public static final ConcurrentHashMap<String, ListenerLogsTask> ListenerLogsTask_Map = new ConcurrentHashMap<>();

    /**
     * 获取控制台名称
     */
    private static String getListenerForJsFileId(ListenerLogsReq message) {
        return String.format("%s.%s#%s", message.getBizType(), message.getGroupName(), message.getFileFullPath());
    }

    /**
     * 获取控制台名称
     */
    private static String getListenerForJsFileId(AbstractConsole console) {
        return String.format("%s.%s#%s", console.getBizType(), console.getGroupName(), JsCodeFilePathUtils.concat(console.getFilePath(), console.getFileName()));
    }

    /**
     * 获取ListenerLogsTask实例
     */
    public static ListenerLogsTask getListenerLogsTask(ListenerLogsReq message) {
        final String listenerForJsFileId = ListenerLogsTask.getListenerForJsFileId(message);
        ListenerLogsTask listenerLogsTask = ListenerLogsTask.ListenerLogsTask_Map.get(listenerForJsFileId);
        if (listenerLogsTask == null) {
            listenerLogsTask = new ListenerLogsTask(message);
            ListenerLogsTask_Map.put(listenerForJsFileId, listenerLogsTask);
        }
        return listenerLogsTask;
    }

    /**
     * 停止所有websocket连接都关闭了的任务
     */
    public static void stopClosedTask() {
        List<String> rmList = new ArrayList<>();
        ListenerLogsTask_Map.forEach((listenerForJsFileId, listenerLogsTask) -> {
            listenerLogsTask.removeCloseSession();
            if (listenerLogsTask.getWebSocketSessionSize() <= 0) {
                rmList.add(listenerForJsFileId);
            }
        });
        rmList.forEach(listenerForJsFileId -> {
            ListenerLogsTask listenerLogsTask = ListenerLogsTask_Map.get(listenerForJsFileId);
            if (listenerLogsTask == null || listenerLogsTask.getWebSocketSessionSize() > 0) {
                return;
            }
            listenerLogsTask.stop();
            ListenerLogsTask_Map.remove(listenerForJsFileId);
        });
    }

    /**
     * 发送消息到所有的客户端
     *
     * @param object 消息对象
     */
    public static void sendMessage(AbstractConsole target, Object object) {
        final String listenerForJsFileId = ListenerLogsTask.getListenerForJsFileId(target);
        ListenerLogsTask listenerLogsTask = ListenerLogsTask.ListenerLogsTask_Map.get(listenerForJsFileId);
        if (listenerLogsTask == null) {
            return;
        }
        listenerLogsTask.sendMessage(object);
    }

    /**
     * 监听的js文件名
     */
    @Getter
    private final String listenerForJsFileId;

    private ListenerLogsTask(ListenerLogsReq message) {
        super(Task_Id, TaskType.ListenerLogs);
        this.runTimeOut = -1;
        this.listenerForJsFileId = getListenerForJsFileId(message);
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
