package io.github.xiaomisum.ryze.websocket.example.mock;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.glassfish.tyrus.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock WebSocket 服务器工具类
 * <p>
 * 使用 Tyrus 提供 WebSocket 服务，用于测试 WebSocket 协议
 * </p>
 *
 * @author xiaomi
 */
public class MockWebSocketServer {

    private static final int PORT = 8080;
    private static Server server;

    /**
     * 启动 Mock WebSocket 服务器
     */
    public static void start() {
        if (server != null) {
            return;
        }

        try {
            server = new Server("localhost", PORT, "/", null,
                    WebSocketBodyBytesServer.class,
                    WebSocketBodyStringServer.class,
                    WebSocketPathServer.class,
                    WebSocketQueryServer.class);
            server.start();
            System.out.println("Mock WebSocket Server started on port " + PORT);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start Mock WebSocket Server", e);
        }
    }

    /**
     * 停止 Mock WebSocket 服务器
     */
    public static void stop() {
        if (server != null) {
            server.stop();
            System.out.println("Mock WebSocket Server stopped");
        }
    }

    /**
     * WebSocket 端点：/ws/body/bytes
     * 处理二进制消息，回显给客户端
     */
    @ServerEndpoint("/ws/body/bytes")
    public static class WebSocketBodyBytesServer {

        private Session session;

        @OnOpen
        public void onOpen(Session session) {
            this.session = session;
            session.getAsyncRemote().sendBinary(ByteBuffer.wrap(("连接成功, Session Id: " + session.getId()).getBytes()));
        }

        @OnClose
        public void onClose() {
            System.out.println("连接断开, Session Id: " + session.getId());
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "连接断开"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @OnMessage
        public void onMessage(byte[] message, Session session) {
            System.out.println("收到 bytes 消息: " + new String(message));
            session.getAsyncRemote().sendBinary(ByteBuffer.wrap(message));
        }

        @OnError
        public void onError(Session session, Throwable error) {
            System.out.println("连接异常, Session Id: " + session.getId());
        }
    }

    /**
     * WebSocket 端点：/ws/body/string
     * 处理文本消息，回显给客户端
     */
    @ServerEndpoint("/ws/body/string")
    public static class WebSocketBodyStringServer {

        private Session session;

        @OnOpen
        public void onOpen(Session session) {
            this.session = session;
            session.getAsyncRemote().sendText("连接成功, Session Id: " + session.getId());
        }

        @OnClose
        public void onClose() {
            System.out.println("连接断开, Session Id: " + session.getId());
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "连接断开"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @OnMessage
        public void onMessage(String message, Session session) {
            System.out.println("收到 string 消息: " + message);
            session.getAsyncRemote().sendText(message);
        }

        @OnError
        public void onError(Session session, Throwable error) {
            System.out.println("连接异常, Session Id: " + session.getId());
        }
    }

    /**
     * WebSocket 端点：/ws/path/{userId}
     * 支持路径参数，回显消息给客户端
     */
    @ServerEndpoint("/ws/path/{userId}")
    public static class WebSocketPathServer {

        private Session session;
        private String userId;

        @OnOpen
        public void onOpen(Session session, @PathParam("userId") String userId) {
            this.session = session;
            this.userId = userId;
            session.getAsyncRemote().sendText("连接成功, Session Id: " + session.getId() + ", User Id: " + userId);
        }

        @OnClose
        public void onClose() {
            System.out.println("连接断开, Session Id: " + session.getId() + ", User Id: " + userId);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "连接断开"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @OnMessage
        public void onMessage(String message, Session session) {
            System.out.println("收到 User Id [ " + userId + "] 消息: " + message);
            session.getAsyncRemote().sendText(message);
        }

        @OnError
        public void onError(Session session, Throwable error) {
            System.out.println("连接异常, Session Id: " + session.getId() + ", User Id: " + userId);
        }
    }

    /**
     * WebSocket 端点：/ws/query
     * 支持查询参数，回显消息给客户端
     */
    @ServerEndpoint("/ws/query")
    public static class WebSocketQueryServer {

        private Session session;
        private String queryId;

        @OnOpen
        public void onOpen(Session session) {
            this.session = session;
            Map<String, List<String>> query = session.getRequestParameterMap();
            queryId = query.getOrDefault("queryId", List.of()).stream().findFirst().orElse(null);
            session.getAsyncRemote().sendText("连接成功, Session Id: " + session.getId() + ", Query Id: " + queryId);
        }

        @OnClose
        public void onClose() {
            System.out.println("连接断开, Session Id: " + session.getId() + ", Query Id: " + queryId);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "连接断开"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @OnMessage
        public void onMessage(String message, Session session) {
            System.out.println("收到 Query Id [ " + queryId + "] 消息: " + message);
            session.getAsyncRemote().sendText(message);
        }

        @OnError
        public void onError(Session session, Throwable error) {
            System.out.println("连接异常, Session Id: " + session.getId() + ", Query Id: " + queryId);
        }
    }
}
