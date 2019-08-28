package org.clever.nashorn.websocket.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 作者： lzw<br/>
 * 创建时间：2017-12-12 10:31 <br/>
 */
@SuppressWarnings("WeakerAccess")
@Slf4j
public class WebSocketCloseSessionUtils {

    /**
     * 所有需要关闭的Session
     */
    private static final CopyOnWriteArraySet<WebSocketSession> CLOSE_SESSION_SET = new CopyOnWriteArraySet<>();

    /**
     * 关闭需要关闭的连接
     */
    public static void closeSession(WebSocketSession session) {
        if (session != null && session.isOpen()) {
            CLOSE_SESSION_SET.add(session);
        }
        Set<WebSocketSession> rmList = new HashSet<>();
        for (WebSocketSession closeSession : CLOSE_SESSION_SET) {
            if (closeSession.isOpen()) {
                try {
                    closeSession.close();
                } catch (Throwable e) {
                    log.error("关闭WebSocketSession连接异常", e);
                    continue;
                }
            }
            rmList.add(closeSession);
        }
        CLOSE_SESSION_SET.removeAll(rmList);
    }

    /**
     * 返回需要关闭的WebSocket连接数量
     */
    public static int getCloseSessionCount() {
        return CLOSE_SESSION_SET.size();
    }
}
