package io.github.xiaomisum.ryze.websocket.example;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ServerEndpoint("/ws/body/string")
public class WebSocketBodyStringServer {

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        session.getAsyncRemote().sendText("连接成功, Session Id: " + session.getId());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        System.out.println("连接断开, Session Id: " + session.getId());
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
        System.out.println("收到 bytes 消息: " + message);
        // 转发回自己
        session.getAsyncRemote().sendText(message);
    }

    /**
     * 发生异常调用方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("连接异常, Session Id: " + session.getId());
    }
}