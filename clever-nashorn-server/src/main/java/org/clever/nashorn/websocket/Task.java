package org.clever.nashorn.websocket;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.mapper.JacksonMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket 任务抽象
 * <p>
 * 作者： lzw<br/>
 * 创建时间：2018-01-07 18:42 <br/>
 *
 * @param <T> 请求消息的类型
 */
@SuppressWarnings("WeakerAccess")
@Slf4j
public abstract class Task<T> {

    /**
     * 连接当前任务的Session集合
     */
    private final CopyOnWriteArraySet<WebSocketSession> Session_Set = new CopyOnWriteArraySet<>();

    /**
     * 当前任务ID(全局唯一)
     */
    @Getter
    private final String taskId;

    /**
     * 当前任务是否已经停止
     */
    @Getter
    private boolean stop = false;

    /**
     * 任务是否已经启动
     */
    @Getter
    private boolean start = false;

    /**
     * @param taskId 当前任务ID(全局唯一)
     */
    public Task(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 增加一个WebSocketSession到当前任务
     */
    public void addWebSocketSession(WebSocketSession session) {
        Session_Set.add(session);
    }

    /**
     * 从当前任务移除一个WebSocketSession
     */
    private boolean removeWebSocketSession(WebSocketSession session) {
        if (session == null) {
            return true;
        }
        WebSocketSession rm = Session_Set.stream().filter(s -> Objects.equals(s.getId(), session.getId())).findFirst().orElse(null);
        return rm != null && Session_Set.remove(rm);
    }

    /**
     * 判断Session是否已经存在
     */
    public boolean contains(WebSocketSession session) {
        return Session_Set.contains(session);
    }

    /**
     * 返回连接当前任务的Session数量
     */
    public int getWebSocketSessionSize() {
        return Session_Set.size();
    }

    /**
     * 发送消息到指定的客户端
     *
     * @param session WebSocket连接
     * @param object  消息对象
     */
    private void sendMessage(WebSocketSession session, Object object) {
        TextMessage textMessage = new TextMessage(JacksonMapper.nonEmptyMapper().toJson(object));
        try {
            session.sendMessage(textMessage);
        } catch (Throwable e) {
            log.error("[ContainerLogTask] 发送任务结束消息异常", e);
        }
    }

    /**
     * 等待所有的连接关闭(会阻塞当前线程)
     */
    protected void awaitAllSessionClose() {
        while (getWebSocketSessionSize() > 0) {
            try {
                Thread.sleep(1000);
                // 移除关闭了的Session
                removeCloseSession();
            } catch (InterruptedException e) {
                log.info("休眠失败", e);
            }
        }
    }

    /**
     * 发送消息到所有的客户端
     *
     * @param object 消息对象
     */
    protected void sendMessage(Object object) {
        Set<WebSocketSession> rmSet = new HashSet<>();
        for (WebSocketSession session : Session_Set) {
            if (!session.isOpen()) {
                rmSet.add(session);
                continue;
            }
            sendMessage(session, object);
        }
        // 移除关闭了的Session
        Session_Set.removeAll(rmSet);
    }

    /**
     * 关闭所有的 WebSocketSession
     */
    protected void closeAllSession() {
        for (WebSocketSession session : Session_Set) {
            WebSocketCloseSessionUtils.closeSession(session);
        }
        Session_Set.clear();
    }

    /**
     * 移除所有已经关闭了 WebSocketSession
     */
    protected void removeCloseSession() {
        // 移除关闭了的Session
        Set<WebSocketSession> rmSet = new HashSet<>();
        for (WebSocketSession session : Session_Set) {
            if (!session.isOpen()) {
                rmSet.add(session);
            }
        }
        Session_Set.removeAll(rmSet);
    }

    /**
     * 开始任务<br />
     *
     * @param message 请求消息
     * @param verify  消息数据校验结果
     */
    public void start(T message, boolean verify) {
        start = true;
        doStart(message, false);
    }

    /**
     * 关闭任务<br />
     * 释放资源
     */
    public void stop() {
        stop = true;
        closeAllSession();
        doStop();
    }

    // ----------------------------------------------------------------------------------------------------------- 需要子类实现的方法

    /**
     * 返回当前任务类型
     */
    protected abstract TaskType getTaskType();

    /**
     * 任务开始执行任务功能
     *
     * @param message 请求消息
     * @param verify  消息数据校验结果
     */
    protected abstract void doStart(T message, boolean verify);

    /**
     * 结束任务释放资源
     */
    protected abstract void doStop();

    /**
     * 处理请求消息
     *
     * @param session 会话
     * @param message 请求消息
     * @param verify  消息数据校验结果
     */
    protected abstract void handleMessage(WebSocketSession session, T message, boolean verify);
}
