package org.clever.nashorn.config;

import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.websocket.debug.DebugHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 作者： lzw<br/>
 * 创建时间：2017-12-05 21:37 <br/>
 */
@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private DebugHandler debugHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String[] allowsOrigins = {"*"};
        //WebSocket通道 withSockJS()表示开启 SockJs, SockJS 所处理的 URL 是 “http://“ 或 “https://“ 模式，而不是 “ws://“ or “wss://“
        registry.addHandler(debugHandler, "/ws/debug")
                .setAllowedOrigins(allowsOrigins);
        // .withSockJS();
    }
}
