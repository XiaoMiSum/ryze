# ActiveMQ 协议

## 概述

ActiveMQ 协议支持为 Ryze 测试框架提供了与 Apache ActiveMQ 消息中间件进行交互的能力。支持队列和主题两种消息模式，可以进行消息发送、接收和处理的完整测试。

## 依赖引入

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-active</artifactId>
    <version>${version}</version>
</dependency>
```

## ActiveMQ 配置元件

ActiveMQ 默认配置：使用该组件，可配置 ActiveMQ协议的默认配置，降低测试集合的配置复杂度。

当测试集合描述文件中存在此配置时，下级测试集合中包含的 ActiveMQ 取样器\处理器从此配置中获取相关配置。

### YAML 配置方式

```yaml
# activemq 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: active  # 取样器类型
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  username: admin # activemq 用户名
  password: admin # activemq 密码
  queue: ryze.queue # 订阅模式 优先级高于 topic，根据实际选择使用
  topic: test  # 广播模式
  broker.url: 'tcp://192.168.1.7:61616'
```

### JSON 配置方式

```json
{
  "testclass": "active",
  "config": {
    "username": "admin",
    "password": "admin",
    "queue": "ryze.queue",
    "topic": "test",
    "broker.url": "tcp://192.168.1.7:61616"
  }
}
```

## ActiveMQ 处理器

### 前置处理器

前置处理器在主要测试之前执行，常用于准备测试消息或初始化队列。

```yaml
testclass: active  # activemq 前置处理器类型
config: # 处理器配置
  username: admin # activemq 用户名
  password: admin # activemq 密码
  queue: setup.queue # 队列名称
  broker.url: 'tcp://localhost:61616'
  message: # 发送的消息，可以是任意json对象、字符串、数字等
    type: "setup"
    data: "初始化数据"
```

### 后置处理器

后置处理器在主要测试之后执行，常用于清理测试数据或发送完成通知。

```yaml
testclass: active  # activemq 后置处理器类型
config: # 处理器配置
  username: admin # activemq 用户名
  password: admin # activemq 密码
  queue: cleanup.queue # 队列名称
  broker.url: 'tcp://localhost:61616'
  message: # 发送的消息
    type: "cleanup"
    status: "completed"
```

## ActiveMQ 取样器

### YAML 配置方式

```yaml
title: 标准activemq取样器
testclass: active  # 取样器类型
config: # 取样器配置
  username: admin # activemq 用户名
  password: admin # activemq 密码
  queue: ryze.queue # 订阅模式 优先级高于 topic，根据实际选择使用
  topic: test  # 广播模式
  broker.url: 'tcp://192.168.1.7:61616'
  message: # 发送的消息，可以是任意json对象、字符串、数字等
    name: test
    age: 18
```

### JSON 配置方式

```json
{
  "title": "用户通知消息",
  "testclass": "active",
  "config": {
    "username": "admin",
    "password": "admin",
    "queue": "user.notifications",
    "broker.url": "tcp://localhost:61616",
    "message": {
      "userId": 12345,
      "type": "welcome",
      "content": "欢迎使用我们的服务",
      "timestamp": "2024-01-01T10:00:00Z"
    }
  }
}
```

## 常见问题

1. **连接失败**：检查 ActiveMQ 服务是否正常运行，端口是否正确
2. **认证失败**：确认用户名和密码是否正确
3. **队列不存在**：ActiveMQ 会自动创建不存在的队列和主题
4. **消息发送失败**：检查网络连接和消息格式是否正确
5. **权限不足**：确保用户有相应队列或主题的发送权限

## 配置优先级

配置项的优先级为：**取样器配置 > ActiveMQ 默认配置**

## Java API 示例

### 基础消息发送

```java
import static io.github.xiaomisum.ryze.protocol.activemq.ActiveMQMagicBox.*;

public class ActiveMQApiExample {

    public void testQueueMessage() {
        // 发送队列消息
        Result result = activemq("发送用户通知", builder -> builder
                .brokerUrl("tcp://localhost:61616")
                .username("admin")
                .password("admin")
                .queue("user.notifications")
                .message(Map.of(
                        "userId", 12345,
                        "type", "welcome",
                        "content", "欢迎使用我们的服务",
                        "timestamp", System.currentTimeMillis()
                ))
        );

        assertThat(result.isSuccess()).isTrue();
    }

    public void testTopicMessage() {
        // 发送主题消息
        activemq("发布系统公告", builder -> builder
                .brokerUrl("tcp://localhost:61616")
                .username("admin")
                .password("admin")
                .topic("system.announcements")
                .message("系统将在2024年1月1日进行维护")
        );
    }

    public void testComplexMessage() {
        // 发送复杂对象消息
        Map<String, Object> orderInfo = Map.of(
                "orderId", "ORDER-001",
                "customerId", 12345,
                "items", List.of(
                        Map.of("productId", "P001", "quantity", 2, "price", 99.99),
                        Map.of("productId", "P002", "quantity", 1, "price", 199.99)
                ),
                "totalAmount", 399.97,
                "status", "pending"
        );

        activemq("发送订单消息", builder -> builder
                .brokerUrl("tcp://localhost:61616")
                .username("admin")
                .password("admin")
                .queue("order.processing")
                .message(orderInfo)
        );
    }
}
```

### 完整 ActiveMQ 测试套件

```java
import static io.github.xiaomisum.ryze.protocol.activemq.ActiveMQMagicBox.*;

public class ActiveMQTestSuite {

    public void messagingTestSuite() {
        suite("ActiveMQ消息测试套件", builder -> {
            // 配置 ActiveMQ 默认设置
            builder.configure(configure -> configure
                    .activemq(activemq -> activemq
                            .brokerUrl("tcp://localhost:61616")
                            .username("admin")
                            .password("admin")
                    )
            );

            builder.children(children -> {
                // 前置处理：发送初始化消息
                children.activemqPreprocessor("发送初始化消息", preprocessor -> preprocessor
                        .queue("test.init")
                        .message(Map.of(
                                "action", "initialize",
                                "testId", "SUITE-001",
                                "startTime", System.currentTimeMillis()
                        ))
                );

                // 测试1：发送用户注册消息
                children.activemq("发送用户注册消息", activemq -> activemq
                        .queue("user.registration")
                        .message(Map.of(
                                "userId", "USER-001",
                                "email", "test@example.com",
                                "name", "测试用户",
                                "registrationTime", System.currentTimeMillis()
                        ))
                );

                // 测试2：发送订单消息
                children.activemq("发送订单处理消息", activemq -> activemq
                        .queue("order.processing")
                        .message(Map.of(
                                "orderId", "ORDER-001",
                                "userId", "USER-001",
                                "amount", 299.99,
                                "status", "pending"
                        ))
                );

                // 测试3：发送系统广播
                children.activemq("发送系统广播", activemq -> activemq
                        .topic("system.broadcast")
                        .message(Map.of(
                                "type", "maintenance",
                                "message", "系统维护通知",
                                "scheduledTime", "2024-01-01T02:00:00Z"
                        ))
                );

                // 后置处理：发送完成消息
                children.activemqPostprocessor("发送完成消息", postprocessor -> postprocessor
                        .queue("test.complete")
                        .message(Map.of(
                                "action", "complete",
                                "testId", "SUITE-001",
                                "endTime", System.currentTimeMillis()
                        ))
                );
            });
        });
    }
}
```

## Groovy API 示例

### 基础消息发送

```groovy
import static io.github.xiaomisum.ryze.protocol.activemq.ActiveMQMagicBox.*

// 简单消息发送
def sendNotification() {
    activemq("发送通知消息") { builder ->
        builder.brokerUrl("tcp://localhost:61616")
                .username("admin")
                .password("admin")
                .queue("notifications")
                .message([
                        id       : UUID.randomUUID().toString(),
                        title    : "新消息通知",
                        content  : "您有一条新的消息",
                        priority : "high",
                        createdAt: new Date().toString()
                ])
    }
}

// 发送不同类型的消息
def sendVariousMessages() {
    // 字符串消息
    activemq("发送文本消息") { builder ->
        builder.brokerUrl("tcp://localhost:61616")
                .queue("text.messages")
                .message("这是一条简单的文本消息")
    }

    // 数字消息
    activemq("发送数字消息") { builder ->
        builder.brokerUrl("tcp://localhost:61616")
                .queue("number.messages")
                .message(12345)
    }

    // 列表消息
    activemq("发送列表消息") { builder ->
        builder.brokerUrl("tcp://localhost:61616")
                .queue("list.messages")
                .message(["项目1", "项目2", "项目3"])
    }
}

// 主题消息发送
def publishToTopic() {
    def announcement = [
            id         : UUID.randomUUID().toString(),
            type       : "system_update",
            title      : "系统更新通知",
            content    : "系统将在今晚进行更新，预计耗时2小时",
            publishTime: new Date(),
            severity   : "info"
    ]

    activemq("发布系统公告") { builder ->
        builder.brokerUrl("tcp://localhost:61616")
                .username("admin")
                .password("admin")
                .topic("system.announcements")
                .message(announcement)
    }
}
```

### 完整 ActiveMQ 测试脚本

```groovy
import static io.github.xiaomisum.ryze.protocol.activemq.ActiveMQMagicBox.*

// ActiveMQ 消息完整测试流程
suite("ActiveMQ消息功能测试") { builder ->
    // 配置 ActiveMQ 默认设置
    builder.configure { configure ->
        configure.activemq { activemq ->
            activemq.brokerUrl("tcp://localhost:61616")
                    .username("admin")
                    .password("admin")
        }
    }

    builder.children { children ->
        // 1. 前置处理：发送测试开始通知
        children.activemqPreprocessor("测试开始通知") { preprocessor ->
            preprocessor.topic("test.lifecycle")
                    .message([
                            event      : "test_started",
                            suiteId    : "groovy-test-suite",
                            startTime  : new Date(),
                            environment: "testing"
                    ])
        }

        // 2. 用户相关消息测试
        children.activemq("发送用户注册消息") { activemq ->
            activemq.queue("user.registration")
                    .message([
                            userId          : "GROOVY-USER-001",
                            email           : "groovy@test.com",
                            name            : "Groovy测试用户",
                            profile         : [
                                    age      : 30,
                                    location : "北京",
                                    interests: ["编程", "测试", "自动化"]
                            ],
                            registrationTime: new Date()
                    ])
        }

        // 3. 订单处理消息测试
        children.activemq("发送订单创建消息") { activemq ->
            activemq.queue("order.created")
                    .message([
                            orderId    : "ORDER-GROOVY-001",
                            customerId : "GROOVY-USER-001",
                            items      : [
                                    [productId: "PROD-001", name: "商品1", quantity: 2, price: 99.99],
                                    [productId: "PROD-002", name: "商品2", quantity: 1, price: 199.99]
                            ],
                            totalAmount: 399.97,
                            currency   : "CNY",
                            orderTime  : new Date(),
                            status     : "pending"
                    ])
        }

        // 4. 库存更新消息测试
        children.activemq("发送库存更新消息") { activemq ->
            activemq.queue("inventory.update")
                    .message([
                            productId     : "PROD-001",
                            operation     : "decrease",
                            quantity      : 2,
                            remainingStock: 98,
                            updateTime    : new Date(),
                            operator      : "system"
                    ])
        }

        // 5. 支付通知消息测试
        children.activemq("发送支付成功通知") { activemq ->
            activemq.queue("payment.notifications")
                    .message([
                            paymentId      : "PAY-GROOVY-001",
                            orderId        : "ORDER-GROOVY-001",
                            amount         : 399.97,
                            currency       : "CNY",
                            method         : "alipay",
                            status         : "success",
                            transactionTime: new Date()
                    ])
        }

        // 6. 系统监控消息测试
        children.activemq("发送系统监控数据") { activemq ->
            activemq.topic("system.monitoring")
                    .message([
                            timestamp: new Date(),
                            metrics  : [
                                    cpu   : [usage: 75.5, cores: 8],
                                    memory: [used: "4.2GB", total: "8GB", percentage: 52.5],
                                    disk  : [used: "120GB", total: "500GB", percentage: 24.0]
                            ],
                            alerts   : [],
                            status   : "healthy"
                    ])
        }

        // 7. 日志消息测试
        ["INFO", "WARN", "ERROR"].each { level ->
            children.activemq("发送${level}级别日志") { activemq ->
                activemq.queue("logs.${level.toLowerCase()}")
                        .message([
                                level    : level,
                                timestamp: new Date(),
                                source   : "groovy-test",
                                message  : "这是一条${level}级别的日志消息",
                                context  : [
                                        thread    : Thread.currentThread().getName(),
                                        method    : "testActiveMQSuite",
                                        lineNumber: 42
                                ]
                        ])
            }
        }

        // 8. 批量消息测试
        (1..5).each { i ->
            children.activemq("批量消息${i}") { activemq ->
                activemq.queue("batch.processing")
                        .message([
                                batchId  : "BATCH-001",
                                itemId   : "ITEM-${String.format('%03d', i)}",
                                data     : "批量处理数据${i}",
                                sequence : i,
                                timestamp: new Date()
                        ])
            }
        }

        // 9. 错误处理消息测试
        children.activemq("发送错误处理消息") { activemq ->
            activemq.queue("error.handling")
                    .message([
                            errorId    : UUID.randomUUID().toString(),
                            type       : "business_error",
                            description: "业务逻辑错误示例",
                            stackTrace : "模拟的错误堆栈信息",
                            context    : [
                                    userId   : "GROOVY-USER-001",
                                    action   : "create_order",
                                    timestamp: new Date()
                            ],
                            severity   : "medium"
                    ])
        }

        // 10. 后置处理：发送测试完成通知
        children.activemqPostprocessor("测试完成通知") { postprocessor ->
            postprocessor.topic("test.lifecycle")
                    .message([
                            event        : "test_completed",
                            suiteId      : "groovy-test-suite",
                            endTime      : new Date(),
                            status       : "success",
                            totalMessages: 15
                    ])
        }
    }
}

// 性能测试示例
def performanceTest() {
    def messageCount = 100

    suite("ActiveMQ性能测试") { builder ->
        builder.children { children ->
            // 大量消息发送测试
            (1..messageCount).each { i ->
                children.activemq("性能测试消息${i}") { activemq ->
                    activemq.queue("performance.test")
                            .message([
                                    messageId: i,
                                    timestamp: new Date(),
                                    payload  : "性能测试数据" * 10 // 增加消息大小
                            ])
                }
            }
        }
    }
}
```

## 相关文档

- [ActiveMQ 官方文档](https://activemq.apache.org/)

- [示例项目](https://github.com/XiaoMiSum/ryze/tree/master/example/active-example)

---

**💡 提示**:
更多详细示例请参考 [example/active-example](https://github.com/XiaoMiSum/ryze/tree/master/example/active-example)
目录下的完整示例代码。