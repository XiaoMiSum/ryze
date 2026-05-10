package io.github.xiaomisum.ryze.proto.example.mock;

import com.google.protobuf.InvalidProtocolBufferException;
import io.github.xiaomisum.ryze.proto.example.springboot.UserOuterClass;
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
 * 使用 MockServer 提供 HTTP 服务，用于测试 Proto 协议
 * 支持 JSON 和 Protobuf 格式的请求/响应
 * </p>
 *
 * @author xiaomi
 */
public class MockProtoHttpServer {

    private static final int PORT = 8080;
    private static ClientAndServer mockServer;
    private static final Map<Integer, UserOuterClass.User> users = new ConcurrentHashMap<>();

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
        System.out.println("Mock Proto HTTP Server started on port " + PORT);
    }

    /**
     * 停止 Mock HTTP 服务器
     */
    public static void stop() {
        if (mockServer != null) {
            mockServer.stop();
            System.out.println("Mock Proto HTTP Server stopped");
        }
    }

    /**
     * 初始化用户数据
     */
    private static void initUsers() {
        users.clear();
        for (int i = 0; i < 10; i++) {
            users.put(i, UserOuterClass.User.newBuilder()
                    .setId(i)
                    .setName("name_" + i)
                    .setAge(18)
                    .build());
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
                    var userList = users.values().stream()
                            .map(user -> Map.of(
                                    "id", user.getId(),
                                    "name", user.getName(),
                                    "age", user.getAge()
                            ))
                            .toList();

                    var result = Map.of(
                            "code", 0,
                            "data", userList,
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
                    String idStr = path.substring(path.lastIndexOf('/') + 1);
                    Integer id = Integer.parseInt(idStr);
                    var user = users.get(id);

                    if (user != null) {
                        var result = Map.of(
                                "code", 0,
                                "data", Map.of(
                                        "id", user.getId(),
                                        "name", user.getName(),
                                        "age", user.getAge()
                                ),
                                "msg", "success"
                        );
                        return response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(json(result));
                    } else {
                        var result = Map.of(
                                "code", -1,
                                "data", Map.of(),
                                "msg", "user not found"
                        );
                        return response()
                                .withStatusCode(404)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(json(result));
                    }
                });

        // POST /user - 添加用户（支持 JSON 格式）
        mockServer.when(
                        request()
                                .withPath("/user")
                                .withMethod("POST")
                )
                .respond(httpRequest -> {
                    String body = httpRequest.getBodyAsString();
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> userData = com.alibaba.fastjson2.JSON.parseObject(body, Map.class);
                        
                        Integer id = Integer.parseInt(userData.get("id").toString());
                        String name = userData.get("name").toString();
                        Integer age = Integer.parseInt(userData.get("age").toString());

                        UserOuterClass.User user = UserOuterClass.User.newBuilder()
                                .setId(id)
                                .setName(name)
                                .setAge(age)
                                .build();
                        
                        users.put(id, user);

                        var result = Map.of(
                                "code", 0,
                                "data", Map.of(
                                        "id", user.getId(),
                                        "name", user.getName(),
                                        "age", user.getAge()
                                ),
                                "msg", "success"
                        );
                        return response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(json(result));
                    } catch (Exception e) {
                        var result = Map.of(
                                "code", -1,
                                "msg", "Error: " + e.getMessage()
                        );
                        return response()
                                .withStatusCode(500)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(json(result));
                    }
                });

        // PUT /user - 更新用户（支持 JSON 格式）
        mockServer.when(
                        request()
                                .withPath("/user")
                                .withMethod("PUT")
                )
                .respond(httpRequest -> {
                    String body = httpRequest.getBodyAsString();
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> userData = com.alibaba.fastjson2.JSON.parseObject(body, Map.class);
                        
                        Integer id = Integer.parseInt(userData.get("id").toString());
                        String name = userData.get("name").toString();
                        Integer age = Integer.parseInt(userData.get("age").toString());

                        UserOuterClass.User user = UserOuterClass.User.newBuilder()
                                .setId(id)
                                .setName(name)
                                .setAge(age)
                                .build();
                        
                        users.put(id, user);

                        var result = Map.of(
                                "code", 0,
                                "data", Map.of(
                                        "id", user.getId(),
                                        "name", user.getName(),
                                        "age", user.getAge()
                                ),
                                "msg", "success"
                        );
                        return response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(json(result));
                    } catch (Exception e) {
                        var result = Map.of(
                                "code", -1,
                                "msg", "Error: " + e.getMessage()
                        );
                        return response()
                                .withStatusCode(500)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(json(result));
                    }
                });
    }
}
