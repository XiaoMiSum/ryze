package io.github.xiaomisum.ryze.websocket.example;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@ServerEndpoint("/ws/query")
public class WebSocketQueryServer {

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    private String queryId;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        Map<String, List<String>> query = session.getRequestParameterMap();
        queryId = query.getOrDefault("queryId", List.of()).stream().findFirst().orElse(null);
        session.getAsyncRemote().sendText("连接成功, Session Id: " + session.getId() + ", Query Id: " + queryId);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        System.out.println("连接断开, Session Id: " + session.getId() + ", Query Id: " + queryId);
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "连接断开"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     **/
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("收到 Query Id [ " + queryId + "] 消息: " + message);
        // 转发回自己
        session.getAsyncRemote().sendText(message);
    }

    /**
     * 发生异常调用方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("连接异常, Session Id: " + session.getId() + ", Query Id: " + queryId);
    }
}