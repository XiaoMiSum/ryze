package io.github.xiaomisum.ryze.coap.example;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * CoAP 消费者/服务器示例
 * 用于接收 CoAP 请求，配合 Ryze 测试框架的 CoAP 生产者使用
 */
public class CoapConsumer {

    private static final int COAP_PORT = 5683;
    private CoapServer server;

    /**
     * 启动 CoAP 服务器
     */
    public void start() {
        server = new CoapServer(COAP_PORT);

        // 添加资源处理器
        server.add(new CoapResource("sensor") {
            @Override
            public void handleGET(CoapExchange exchange) {
                System.out.println("[CoAP Consumer] 收到 GET 请求: " + exchange.getRequestText());
                exchange.respond(CoAP.ResponseCode.CONTENT, "Hello from CoAP Consumer!");
            }

            @Override
            public void handlePOST(CoapExchange exchange) {
                String payload = exchange.getRequestText();
                System.out.println("[CoAP Consumer] 收到 POST 请求 - Payload: " + payload);
                exchange.respond(CoAP.ResponseCode.CREATED, "Resource created");
            }

            @Override
            public void handlePUT(CoapExchange exchange) {
                String payload = exchange.getRequestText();
                System.out.println("[CoAP Consumer] 收到 PUT 请求 - Payload: " + payload);
                exchange.respond(CoAP.ResponseCode.CHANGED, "Resource updated");
            }

            @Override
            public void handleDELETE(CoapExchange exchange) {
                System.out.println("[CoAP Consumer] 收到 DELETE 请求");
                exchange.respond(CoAP.ResponseCode.DELETED, "Resource deleted");
            }
        });

        // 添加更多资源路径
        server.add(new CoapResource("temperature") {
            @Override
            public void handleGET(CoapExchange exchange) {
                System.out.println("[CoAP Consumer] 收到 GET /temperature 请求");
                exchange.respond(CoAP.ResponseCode.CONTENT, "{\"temperature\": 25.6, \"unit\": \"celsius\"}");
            }

            @Override
            public void handlePOST(CoapExchange exchange) {
                String payload = exchange.getRequestText();
                System.out.println("[CoAP Consumer] 收到 POST /temperature - Payload: " + payload);
                exchange.respond(CoAP.ResponseCode.CREATED, "Temperature data received");
            }
        });

        server.add(new CoapResource("device") {
            @Override
            public void handlePOST(CoapExchange exchange) {
                String payload = exchange.getRequestText();
                System.out.println("[CoAP Consumer] 收到 POST /device - Payload: " + payload);
                exchange.respond(CoAP.ResponseCode.CREATED, "Device registered");
            }
        });
        
        // 添加测试资源路径
        server.add(new CoapResource("test") {
            @Override
            public void handleGET(CoapExchange exchange) {
                System.out.println("[CoAP Consumer] 收到 GET /test 请求");
                exchange.respond(CoAP.ResponseCode.CONTENT, "{\"status\": \"ok\"}");
            }

            @Override
            public void handlePOST(CoapExchange exchange) {
                String payload = exchange.getRequestText();
                System.out.println("[CoAP Consumer] 收到 POST /test 请求 - Payload: " + payload);
                exchange.respond(CoAP.ResponseCode.CREATED, "Resource created: " + payload);
            }
        });

        server.start();
        System.out.println("[CoAP Consumer] 已启动，监听端口: " + COAP_PORT);
        System.out.println("[CoAP Consumer] 可用资源路径:");
        System.out.println("  - coap://localhost:" + COAP_PORT + "/sensor");
        System.out.println("  - coap://localhost:" + COAP_PORT + "/temperature");
        System.out.println("  - coap://localhost:" + COAP_PORT + "/device");
        System.out.println("  - coap://localhost:" + COAP_PORT + "/test");
    }

    /**
     * 停止 CoAP 服务器
     */
    public void stop() {
        if (server != null) {
            server.destroy();
            System.out.println("[CoAP Consumer] 已停止");
        }
    }

    /**
     *  standalone 模式启动（用于独立运行）
     */
    public static void main(String[] args) throws Exception {
        CoapConsumer consumer = new CoapConsumer();
        consumer.start();
        System.out.println("[CoAP Consumer] 等待接收请求... (按 Ctrl+C 退出)");

        // 保持服务器运行
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[CoAP Consumer] 正在关闭...");
            consumer.stop();
        }));

        Thread.currentThread().join();
    }
}
