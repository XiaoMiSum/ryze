# 协议模块概览

Ryze 测试框架支持多种协议和中间件，为不同的测试场景提供全面的解决方案。本文档提供所有支持协议的概览和快速索引。

## 🌐 支持的协议

### Web 服务协议

#### [HTTP(S) 协议](protocols/HTTP.md)
- **适用场景**: RESTful API 测试、Web 服务测试、微服务接口测试
- **核心特性**: 支持 HTTP/HTTPS、HTTP/2、各种请求方法、自定义请求头
- **配置简易度**: ⭐⭐⭐⭐⭐
- **常用功能**: GET/POST/PUT/DELETE 请求、JSON/XML 数据处理、认证授权

### 数据库协议

#### [JDBC 数据库](protocols/JDBC.md)
- **适用场景**: 关系型数据库测试、数据验证、事务测试
- **支持数据库**: MySQL、PostgreSQL、Oracle、SQL Server、H2 等
- **配置简易度**: ⭐⭐⭐⭐
- **常用功能**: SQL 查询、数据插入更新、存储过程调用、事务管理

#### [Redis 协议](protocols/Redis.md)
- **适用场景**: 缓存测试、会话管理测试、分布式锁测试
- **支持数据类型**: String、Hash、List、Set、ZSet
- **配置简易度**: ⭐⭐⭐⭐⭐
- **常用功能**: 键值操作、过期时间设置、集群模式支持

#### [MongoDB 协议](protocols/MongoDB.md)
- **适用场景**: NoSQL 数据库测试、文档数据验证、聚合查询测试
- **核心特性**: 文档CRUD操作、聚合管道、索引管理
- **配置简易度**: ⭐⭐⭐⭐
- **常用功能**: 文档查询、复杂聚合、地理位置查询

### RPC 协议

#### [Dubbo 协议](protocols/Dubbo.md)
- **适用场景**: 微服务测试、分布式系统测试、服务治理验证
- **注册中心**: Zookeeper、Nacos、Consul
- **配置简易度**: ⭐⭐⭐
- **常用功能**: 服务调用、负载均衡、容错机制、服务发现

### 消息队列协议

#### [ActiveMQ 协议](protocols/ActiveMQ.md)
- **适用场景**: 异步消息测试、队列和主题模式验证
- **消息模式**: 点对点（Queue）、发布订阅（Topic）
- **配置简易度**: ⭐⭐⭐⭐
- **常用功能**: 消息发送、持久化消息、事务消息

#### [Kafka 协议](protocols/Kafka.md)
- **适用场景**: 大数据流处理测试、事件驱动架构测试、高吞吐量消息处理
- **核心特性**: 高吞吐量、分区机制、消费者组
- **配置简易度**: ⭐⭐⭐
- **常用功能**: 消息发布、批量处理、流式数据处理

#### [RabbitMQ 协议](protocols/RabbitMQ.md)
- **适用场景**: 复杂消息路由测试、AMQP 协议验证、企业集成模式
- **交换机类型**: Direct、Topic、Fanout、Headers
- **配置简易度**: ⭐⭐⭐
- **常用功能**: 消息路由、死信队列、延迟消息、优先级队列

## 🎯 选择指南

### 根据测试场景选择

| 测试场景 | 推荐协议 | 说明 |
|---------|----------|------|
| API 接口测试 | [HTTP(S)](protocols/HTTP.md) | 最常用的 Web API 测试 |
| 数据库验证 | [JDBC](protocols/JDBC.md) | 关系型数据库数据验证 |
| 缓存测试 | [Redis](protocols/Redis.md) | 缓存功能和性能测试 |
| 微服务测试 | [Dubbo](protocols/Dubbo.md) | 分布式服务调用测试 |
| 异步处理测试 | [ActiveMQ](protocols/ActiveMQ.md)/[Kafka](protocols/Kafka.md)/[RabbitMQ](protocols/RabbitMQ.md) | 根据具体MQ选择 |
| NoSQL 测试 | [MongoDB](protocols/MongoDB.md) | 文档数据库测试 |
| 大数据处理 | [Kafka](protocols/Kafka.md) | 流式数据处理测试 |
| 企业集成 | [RabbitMQ](protocols/RabbitMQ.md) | 复杂消息路由场景 |

### 根据复杂度选择

| 复杂度 | 协议 | 特点 |
|--------|------|------|
| 简单 ⭐⭐⭐⭐⭐ | HTTP、Redis | 配置简单，上手容易 |
| 中等 ⭐⭐⭐⭐ | JDBC、MongoDB、ActiveMQ | 需要基础配置知识 |
| 复杂 ⭐⭐⭐ | Dubbo、Kafka、RabbitMQ | 需要深入理解相关技术 |

## 🚀 快速开始

### 1. HTTP API 测试（推荐新手）

最简单的开始方式是 HTTP 测试：

```java
@Test
@RyzeTest
public void testHttpApi() {
    MagicBox.http(http -> {
        http.title("用户登录接口测试");
        http.config(config -> config
            .protocol("https")
            .host("api.example.com")
            .method("POST")
            .path("/auth/login")
            .body(body -> {
                body.put("username", "test@example.com");
                body.put("password", "password123");
            })
        );
        http.assertions(assertions -> 
            assertions.httpStatus(200)
                     .json("$.token", "", "isNotEmpty")
        );
    });
}
```

👉 **详细教程**: [HTTP 协议指南](protocols/HTTP.md)

### 2. 数据库测试

验证数据库操作结果：

```java
@Test
@RyzeTest
public void testDatabase() {
    MagicBox.jdbc("查询用户数量", jdbc -> {
        jdbc.configureElements(elements -> elements
            .jdbc(datasource -> datasource
                .ref("mysqlDefault")
                .driver("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/testdb")
                .username("testuser")
                .password("testpass")
            )
        );
        jdbc.config(config -> config
            .datasource("mysqlDefault")
            .sql("SELECT COUNT(*) as total FROM users WHERE status = 'active'")
        );
        jdbc.assertions(assertions ->
            assertions.json("$[0].total", 0, ">")
        );
    });
}
```

👉 **详细教程**: [JDBC 数据库指南](protocols/JDBC.md)

### 3. 消息队列测试

测试异步消息处理：

```java
@Test
@RyzeTest
public void testMessageQueue() {
    MagicBox.activemq("发送通知消息", mq -> {
        mq.config(config -> config
            .brokerUrl("tcp://localhost:61616")
            .username("admin")
            .password("admin")
            .queue("user.notifications")
            .message(Map.of(
                "userId", 12345,
                "type", "welcome",
                "content", "欢迎使用我们的服务"
            ))
        );
    });
}
```

👉 **详细教程**: [ActiveMQ 协议指南](protocols/ActiveMQ.md)

## 📋 配置模式对比

| 协议 | YAML配置 | JSON配置 | Java API | Groovy API |
|------|:--------:|:--------:|:--------:|:----------:|
| HTTP | ✅ | ✅ | ✅ | ✅ |
| JDBC | ✅ | ✅ | ✅ | ✅ |
| Redis | ✅ | ✅ | ✅ | ✅ |
| Dubbo | ✅ | ✅ | ✅ | ✅ |
| ActiveMQ | ✅ | ✅ | ✅ | ✅ |
| Kafka | ✅ | ✅ | ✅ | ✅ |
| RabbitMQ | ✅ | ✅ | ✅ | ✅ |
| MongoDB | ✅ | ✅ | ✅ | ✅ |

## 🔧 高级特性

### 通用特性

所有协议都支持以下特性：

- ✅ **变量替换**: 支持 `${variable}` 语法的动态变量
- ✅ **数据提取**: 从响应中提取数据用于后续测试
- ✅ **结果验证**: 丰富的断言和验证规则
- ✅ **前后置处理**: 测试前后的数据准备和清理
- ✅ **测试套件**: 多个测试用例的组织和管理
- ✅ **错误处理**: 完善的异常处理和错误报告

### 协议特有特性

| 协议 | 特有特性 |
|------|----------|
| HTTP | HTTP/2、自定义请求头、Cookie 管理、文件上传 |
| JDBC | 事务支持、批处理、存储过程、连接池管理 |
| Redis | 集群支持、管道操作、发布订阅、Lua 脚本 |
| Dubbo | 负载均衡、容错机制、服务发现、注册中心集成 |
| ActiveMQ | 消息持久化、事务消息、消息选择器 |
| Kafka | 分区支持、消费者组、流式处理、批量操作 |
| RabbitMQ | 多种交换机、消息路由、死信队列、延迟消息 |
| MongoDB | 聚合管道、地理位置查询、文本搜索、副本集 |

## 📚 学习路径

### 初学者路径

1. **开始**: [HTTP 协议](protocols/HTTP.md) - 最容易上手
2. **进阶**: [JDBC 数据库](protocols/JDBC.md) - 学习数据验证
3. **扩展**: [Redis 协议](protocols/Redis.md) - 了解缓存测试

### 进阶用户路径

1. **微服务**: [Dubbo 协议](protocols/Dubbo.md) - 分布式服务测试
2. **消息队列**: 选择 [ActiveMQ](protocols/ActiveMQ.md)、[Kafka](protocols/Kafka.md) 或 [RabbitMQ](protocols/RabbitMQ.md)
3. **NoSQL**: [MongoDB 协议](protocols/MongoDB.md) - 文档数据库测试

### 专家级路径

1. **性能测试**: 使用 [Kafka](protocols/Kafka.md) 进行高并发测试
2. **复杂集成**: 使用 [RabbitMQ](protocols/RabbitMQ.md) 进行企业级集成测试
3. **全栈测试**: 组合使用多种协议进行端到端测试

## 🆘 获取帮助

### 常见问题

| 问题类型 | 参考文档 |
|----------|----------|
| 连接配置问题 | 各协议文档的"常见问题"部分 |
| API 使用问题 | [API 文档](API.md) |
| 架构理解问题 | [架构设计](Architecture.md) |
| 开发贡献问题 | [开发指南](Development.md) |

### 技术支持

- 📖 **文档首选**: 查看对应协议的详细文档
- 🔍 **搜索问题**: [GitHub Issues](https://github.com/XiaoMiSum/ryze/issues)
- 💬 **社区讨论**: [GitHub Discussions](https://github.com/XiaoMiSum/ryze/discussions)
- 🐛 **报告Bug**: [提交Issue](https://github.com/XiaoMiSum/ryze/issues/new)

---

**💡 提示**: 每个协议文档都包含完整的配置说明、API 示例和常见问题解答。建议根据实际需求选择对应的协议进行深入学习。