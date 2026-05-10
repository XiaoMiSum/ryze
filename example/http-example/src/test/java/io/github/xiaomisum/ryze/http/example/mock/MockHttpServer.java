package io.github.xiaomisum.ryze.http.example.mock;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

/**
 * Mock HTTP 服务器工具类
 * <p>
 * 使用 MockServer 提供 HTTP 服务，用于测试 HTTP 协议
 * </p>
 *
 * @author xiaomi
 */
public class MockHttpServer {

    private static final int PORT = 58081;
    private static ClientAndServer mockServer;
    private static final Map<String, Map<String, Object>> users = new ConcurrentHashMap<>();

    /**
     * 启动 Mock HTTP 服务器
     */
    public static void start() {
        if (mockServer != null && mockServer.hasStarted()) {
            return;
        }

        mockServer = ClientAndServer.startClientAndServer(PORT);
        initUsers();
        setupExpectations();
        System.out.println("Mock HTTP Server started on port " + PORT);
    }

    /**
     * 停止 Mock HTTP 服务器
     */
    public static void stop() {
        if (mockServer != null) {
            mockServer.stop();
            System.out.println("Mock HTTP Server stopped");
        }
    }

    /**
     * 初始化用户数据
     */
    private static void initUsers() {
        users.clear();
        for (int i = 0; i < 10; i++) {
            String id = String.valueOf(i);
            users.put(id, Map.of(
                    "id", i,
                    "name", "name_" + i,
                    "age", 18
            ));
        }
    }

    /**
     * 设置 Mock 期望
     */
    private static void setupExpectations() {
        // GET /user - 获取所有用户
        mockServer.when(
                        request()
                                .withPath("/user")
                                .withMethod("GET")
                )
                .respond(httpRequest -> {
                    var result = Map.of(
                            "code", 0,
                            "data", users.values().stream().toList(),
                            "msg", "success"
                    );
                    return response()
                            .withStatusCode(200)
                            .withContentType(MediaType.APPLICATION_JSON)
                            .withBody(json(result));
                });

        // GET /user/{id} - 获取单个用户
        mockServer.when(
                        request()
                                .withPath("/user/.*")
                                .withMethod("GET")
                )
                .respond(httpRequest -> {
                    String path = httpRequest.getPath().getValue();
                    String id = path.substring(path.lastIndexOf('/') + 1);
                    var user = users.get(id);

                    var result = Map.of(
                            "code", 0,
                            "data", user != null ? user : Map.of(),
                            "msg", "success"
                    );
                    return response()
                            .withStatusCode(200)
                            .withContentType(MediaType.APPLICATION_JSON)
                            .withBody(json(result));
                });

        // POST /user - 添加用户
        mockServer.when(
                        request()
                                .withPath("/user")
                                .withMethod("POST")
                )
                .respond(httpRequest -> {
                    String body = httpRequest.getBodyAsString();
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> user = com.alibaba.fastjson2.JSON.parseObject(body, Map.class);
                        String userId = user.get("id").toString();
                        users.put(userId, user);

                        var result = Map.of(
                                "code", 0,
                                "data", user,
                                "msg", "success"
                        );
                        return response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(json(result));
                    } catch (Exception e) {
                        return response()
                                .withStatusCode(500)
                                .withBody("Error: " + e.getMessage());
                    }
                });

        // PUT /user - 更新用户
        mockServer.when(
                        request()
                                .withPath("/user")
                                .withMethod("PUT")
                )
                .respond(httpRequest -> {
                    String body = httpRequest.getBodyAsString();
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> user = com.alibaba.fastjson2.JSON.parseObject(body, Map.class);
                        String userId = user.get("id").toString();
                        users.put(userId, user);

                        var result = Map.of(
                                "code", 0,
                                "data", user,
                                "msg", "success"
                        );
                        return response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(json(result));
                    } catch (Exception e) {
                        return response()
                                .withStatusCode(500)
                                .withBody("Error: " + e.getMessage());
                    }
                });
    }
}
