# Kafka 协议

## 概述

Kafka 协议支持为 Ryze 测试框架提供了与 Apache Kafka 消息流平台进行交互的能力。支持高吞吐量的消息发布，适用于大规模数据处理、实时流计算和微服务架构中的事件驱动测试。

## 依赖引入

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-kafka</artifactId>
    <version>${version}</version>
</dependency>
```

## Kafka 配置元件

Kafka 默认配置：使用该组件，可配置 Kafka协议的默认配置，降低测试集合的配置复杂度。

当测试集合描述文件中存在此配置时，下级测试集合中包含的 Kafka 取样器\处理器从此配置中获取相关配置。

### YAML 配置方式

```yaml
# kafka 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: kafka # 配置元件类型
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  key.serializer: org.apache.kafka.common.serialization.StringSerializer # key 序列化 可空
  value.serializer: org.apache.kafka.common.serialization.StringSerializer # value 序列化 可空
  acks: 1 # 可空
  retries: 5  # 可空
  linger.ms: 20   # 可空
  bootstrap.servers: 192.168.1.7:9092 # 服务器地址
  topic: xxxx  # ProducerRecord 中的 topic
  key: xxxx # ProducerRecord 中的 key
```

### JSON 配置方式

```json
{
  "testclass": "kafka",
  "config": {
    "key.serializer": "org.apache.kafka.common.serialization.StringSerializer",
    "value.serializer": "org.apache.kafka.common.serialization.StringSerializer",
    "acks": 1,
    "retries": 5,
    "linger.ms": 20,
    "bootstrap.servers": "localhost:9092",
    "topic": "test.topic",
    "key": "test.key"
  }
}
```

## Kafka 处理器

### 前置处理器

前置处理器在主要测试之前执行，常用于准备测试数据或初始化主题。

```yaml
testclass: kafka # kafka 前置处理器类型
config: # 处理器配置
  topic: ryze.topic # ProducerRecord 中的 topic
  key: test # ProducerRecord 中的 key
  bootstrap.servers: 192.168.1.7:9092 # 服务器地址
  key.serializer: org.apache.kafka.common.serialization.StringSerializer # key 序列化 可空
  value.serializer: org.apache.kafka.common.serialization.StringSerializer # value 序列化
  acks: 1
  retries: 5
  linger.ms: 20
  message: # 发送的消息，ProducerRecord中的 value, 可以是任意json对象、字符串、数字等
    name: test
    age: 18
```

### 后置处理器

后置处理器在主要测试之后执行，常用于发送完成通知或清理数据。

```yaml
testclass: kafka # kafka 后置处理器类型
config: # 处理器配置
  topic: ryze.topic # ProducerRecord 中的 topic
  key: test # ProducerRecord 中的 key
  bootstrap.servers: 192.168.1.7:9092 # 服务器地址
  key.serializer: org.apache.kafka.common.serialization.StringSerializer # key 序列化 可空
  value.serializer: org.apache.kafka.common.serialization.StringSerializer # value 序列化
  acks: 1
  retries: 5
  linger.ms: 20
  message: # 发送的消息，ProducerRecord中的 value, 可以是任意json对象、字符串、数字等
    name: test
    age: 18
```

## Kafka 取样器

### YAML 配置方式

```yaml
title: 标准kafka取样器
testclass: kafka # 取样器类型
config: # 取样器配置
  topic: ryze.topic # ProducerRecord 中的 topic
  key: test # ProducerRecord 中的 key
  bootstrap.servers: 192.168.1.7:9092 # 服务器地址
  key.serializer: org.apache.kafka.common.serialization.StringSerializer # key 序列化 可空
  value.serializer: org.apache.kafka.common.serialization.StringSerializer # value 序列化
  acks: 1
  retries: 5
  linger.ms: 20
  message: # 发送的消息，ProducerRecord中的 value, 可以是任意json对象、字符串、数字等
    name: test
    age: 18
```

### JSON 配置方式

```json
{
  "title": "用户事件发送",
  "testclass": "kafka",
  "config": {
    "topic": "user.events",
    "key": "user-12345",
    "bootstrap.servers": "localhost:9092",
    "key.serializer": "org.apache.kafka.common.serialization.StringSerializer",
    "value.serializer": "org.apache.kafka.common.serialization.StringSerializer",
    "acks": 1,
    "retries": 3,
    "message": {
      "eventType": "user_login",
      "userId": 12345,
      "timestamp": "2024-01-01T10:00:00Z",
      "metadata": {
        "ip": "192.168.1.100",
        "userAgent": "Chrome/95.0"
      }
    }
  }
}
```

## 常见问题

1. **连接失败**：检查 Kafka 集群是否正常运行，bootstrap.servers 配置是否正确
2. **序列化错误**：确保 key.serializer 和 value.serializer 配置正确
3. **主题不存在**：检查主题是否存在，或启用自动创建主题
4. **消息发送超时**：调整 request.timeout.ms 和 delivery.timeout.ms 配置
5. **分区错误**：检查主题分区配置和分区键设置

## 配置优先级

配置项的优先级为：**取样器配置 > Kafka 默认配置**

## Java API 示例

### 基础消息发送

```java
import static io.github.xiaomisum.ryze.protocol.kafka.KafkaMagicBox.*;

public class KafkaApiExample {

    public void testProduceMessage() {
        // 发送 Kafka 消息
        Result result = kafka("发送用户事件", builder -> builder
                .bootstrapServers("localhost:9092")
                .topic("user.events")
                .key("user-12345")
                .message(Map.of(
                        "eventType", "user_login",
                        "userId", 12345,
                        "timestamp", System.currentTimeMillis(),
                        "metadata", Map.of(
                                "ip", "192.168.1.100",
                                "userAgent", "Chrome/95.0"
                        )
                ))
                .acks(1)
                .retries(3)
        );

        assertThat(result.isSuccess()).isTrue();
    }

    public void testBatchMessages() {
        // 批量发送消息
        for (int i = 1; i <= 10; i++) {
            kafka("发送批量消息 " + i, builder -> builder
                    .bootstrapServers("localhost:9092")
                    .topic("batch.test")
                    .key("batch-" + i)
                    .message("批量消息内容: " + i)
                    .lingerMs(20)
            );
        }
    }

    public void testTransactionMessage() {
        // 发送事务相关消息
        kafka("发送交易事件", builder -> builder
                .bootstrapServers("localhost:9092")
                .topic("transaction.events")
                .key("txn-001")
                .message(Map.of(
                        "transactionId", "TXN-001",
                        "accountId", "ACC-12345",
                        "amount", 1000.00,
                        "type", "credit",
                        "timestamp", System.currentTimeMillis()
                ))
                .acks("all") // 确保所有副本都收到
                .retries(5)
        );
    }
}
```

### 完整 Kafka 测试套件

```java
import static io.github.xiaomisum.ryze.protocol.kafka.KafkaMagicBox.*;

public class KafkaTestSuite {

    public void eventStreamingTestSuite() {
        suite("Kafka事件流测试套件", builder -> {
            // 配置 Kafka 默认设置
            builder.configure(configure -> configure
                    .kafka(kafka -> kafka
                            .bootstrapServers("localhost:9092")
                            .keySerializer("org.apache.kafka.common.serialization.StringSerializer")
                            .valueSerializer("org.apache.kafka.common.serialization.StringSerializer")
                            .acks(1)
                            .retries(5)
                            .lingerMs(20)
                    )
            );

            builder.children(children -> {
                // 前置处理：发送测试开始事件
                children.kafkaPreprocessor("发送测试开始事件", preprocessor -> preprocessor
                        .topic("test.lifecycle")
                        .key("test-start")
                        .message(Map.of(
                                "event", "test_started",
                                "suiteId", "kafka-test-suite",
                                "timestamp", System.currentTimeMillis()
                        ))
                );

                // 测试1：用户行为事件
                children.kafka("发送用户注册事件", kafka -> kafka
                        .topic("user.lifecycle")
                        .key("user-001")
                        .message(Map.of(
                                "eventType", "user_registered",
                                "userId", "USER-001",
                                "email", "test@example.com",
                                "registrationTime", System.currentTimeMillis(),
                                "source", "web"
                        ))
                );

                // 测试2：订单事件流
                children.kafka("发送订单创建事件", kafka -> kafka
                        .topic("order.events")
                        .key("order-001")
                        .message(Map.of(
                                "eventType", "order_created",
                                "orderId", "ORDER-001",
                                "customerId", "USER-001",
                                "totalAmount", 299.99,
                                "items", List.of(
                                        Map.of("productId", "P001", "quantity", 2),
                                        Map.of("productId", "P002", "quantity", 1)
                                ),
                                "timestamp", System.currentTimeMillis()
                        ))
                );

                // 测试3：库存变更事件
                children.kafka("发送库存变更事件", kafka -> kafka
                        .topic("inventory.changes")
                        .key("product-P001")
                        .message(Map.of(
                                "eventType", "stock_decreased",
                                "productId", "P001",
                                "changeAmount", -2,
                                "remainingStock", 98,
                                "reason", "order_fulfillment",
                                "timestamp", System.currentTimeMillis()
                        ))
                );

                // 测试4：支付事件
                children.kafka("发送支付完成事件", kafka -> kafka
                        .topic("payment.events")
                        .key("payment-001")
                        .message(Map.of(
                                "eventType", "payment_completed",
                                "paymentId", "PAY-001",
                                "orderId", "ORDER-001",
                                "amount", 299.99,
                                "method", "credit_card",
                                "timestamp", System.currentTimeMillis()
                        ))
                );

                // 后置处理：发送测试完成事件
                children.kafkaPostprocessor("发送测试完成事件", postprocessor -> postprocessor
                        .topic("test.lifecycle")
                        .key("test-end")
                        .message(Map.of(
                                "event", "test_completed",
                                "suiteId", "kafka-test-suite",
                                "timestamp", System.currentTimeMillis(),
                                "status", "success"
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
import static io.github.xiaomisum.ryze.protocol.kafka.KafkaMagicBox.*

// 简单消息发送
def sendUserEvent() {
    kafka("用户登录事件") { builder ->
        builder.bootstrapServers("localhost:9092")
                .topic("user.events")
                .key("user-001")
                .message([
                        eventType: "login",
                        userId   : "001",
                        loginTime: new Date(),
                        source   : "web"
                ])
                .acks(1)
                .retries(3)
    }
}

// 发送不同类型的事件
def sendVariousEvents() {
    def events = [
            [type: "click", data: [button: "submit", page: "/login"]],
            [type: "page_view", data: [url: "/dashboard", duration: 30]],
            [type: "api_call", data: [endpoint: "/api/users", method: "GET"]]
    ]

    events.each { event ->
        kafka("发送${event.type}事件") { builder ->
            builder.bootstrapServers("localhost:9092")
                    .topic("user.behavior")
                    .key("user-001")
                    .message(event)
        }
    }
}

// 高吞吐量消息发送
def sendHighVolumeMessages() {
    def batchSize = 1000

    (1..batchSize).each { i ->
        kafka("批量消息${i}") { builder ->
            builder.bootstrapServers("localhost:9092")
                    .topic("high.volume")
                    .key("batch-${i % 10}") // 分布到10个分区
                    .message([
                            id       : i,
                            timestamp: new Date(),
                            data     : "批量数据${i}"
                    ])
                    .lingerMs(50) // 批量发送优化
                    .batchSize(16384)
        }
    }
}
```

### 完整 Kafka 测试脚本

```groovy
import static io.github.xiaomisum.ryze.protocol.kafka.KafkaMagicBox.*

// Kafka 事件流完整测试
suite("Kafka事件流测试") { builder ->
    // 配置 Kafka 默认设置
    builder.configure { configure ->
        configure.kafka { kafka ->
            kafka.bootstrapServers("localhost:9092")
                    .acks(1)
                    .retries(5)
                    .lingerMs(20)
        }
    }

    builder.children { children ->
        // 1. 前置处理：清理并初始化
        children.kafkaPreprocessor("初始化测试环境") { preprocessor ->
            preprocessor.topic("test.control")
                    .key("init")
                    .message([
                            action     : "initialize",
                            testSuite  : "groovy-kafka-test",
                            startTime  : new Date(),
                            environment: "testing"
                    ])
        }

        // 2. 用户生命周期事件测试
        children.kafka("用户注册事件") { kafka ->
            kafka.topic("user.lifecycle")
                    .key("user-groovy-001")
                    .message([
                            eventType       : "user_registered",
                            userId          : "GROOVY-USER-001",
                            email           : "groovy@test.com",
                            profile         : [
                                    name     : "Groovy测试用户",
                                    age      : 30,
                                    city     : "北京",
                                    interests: ["编程", "测试", "Kafka"]
                            ],
                            registrationTime: new Date(),
                            source          : "mobile_app",
                            metadata        : [
                                    platform: "Android",
                                    version : "1.2.3",
                                    deviceId: "DEV-001"
                            ]
                    ])
        }

        // 3. 商品浏览事件
        def products = ["手机", "笔记本电脑", "耳机", "鼠标", "键盘"]
        products.each { product ->
            children.kafka("浏览${product}事件") { kafka ->
                kafka.topic("user.behavior")
                        .key("user-groovy-001")
                        .message([
                                eventType  : "product_viewed",
                                userId     : "GROOVY-USER-001",
                                productName: product,
                                productId  : "PROD-${product.hashCode()}",
                                viewTime   : new Date(),
                                duration   : (Math.random() * 60).intValue(),
                                source     : "product_list"
                        ])
            }
        }

        // 4. 购物车事件
        children.kafka("添加购物车事件") { kafka ->
            kafka.topic("cart.events")
                    .key("user-groovy-001")
                    .message([
                            eventType: "item_added_to_cart",
                            userId   : "GROOVY-USER-001",
                            cartId   : "CART-GROOVY-001",
                            item     : [
                                    productId  : "PROD-${products[0].hashCode()}",
                                    productName: products[0],
                                    quantity   : 1,
                                    price      : 2999.99
                            ],
                            cartTotal: 2999.99,
                            timestamp: new Date()
                    ])
        }

        // 5. 订单事件流
        children.kafka("订单创建事件") { kafka ->
            kafka.topic("order.events")
                    .key("order-groovy-001")
                    .message([
                            eventType      : "order_created",
                            orderId        : "ORDER-GROOVY-001",
                            customerId     : "GROOVY-USER-001",
                            items          : [
                                    [
                                            productId  : "PROD-${products[0].hashCode()}",
                                            productName: products[0],
                                            quantity   : 1,
                                            unitPrice  : 2999.99,
                                            totalPrice : 2999.99
                                    ]
                            ],
                            orderTotal     : 2999.99,
                            currency       : "CNY",
                            shippingAddress: [
                                    street : "测试街道123号",
                                    city   : "北京",
                                    zipCode: "100000"
                            ],
                            paymentMethod  : "alipay",
                            orderTime      : new Date(),
                            status         : "pending"
                    ])
        }

        // 6. 库存变更事件
        children.kafka("库存减少事件") { kafka ->
            kafka.topic("inventory.changes")
                    .key("product-${products[0].hashCode()}")
                    .message([
                            eventType     : "stock_decreased",
                            productId     : "PROD-${products[0].hashCode()}",
                            productName   : products[0],
                            changeQuantity: -1,
                            previousStock : 100,
                            currentStock  : 99,
                            reason        : "order_placement",
                            orderId       : "ORDER-GROOVY-001",
                            timestamp     : new Date()
                    ])
        }

        // 7. 支付事件
        children.kafka("支付处理事件") { kafka ->
            kafka.topic("payment.events")
                    .key("payment-groovy-001")
                    .message([
                            eventType    : "payment_initiated",
                            paymentId    : "PAY-GROOVY-001",
                            orderId      : "ORDER-GROOVY-001",
                            customerId   : "GROOVY-USER-001",
                            amount       : 2999.99,
                            currency     : "CNY",
                            paymentMethod: "alipay",
                            gateway      : "alipay_gateway",
                            timestamp    : new Date(),
                            status       : "processing"
                    ])
        }

        // 8. 支付成功事件
        children.kafka("支付成功事件") { kafka ->
            kafka.topic("payment.events")
                    .key("payment-groovy-001")
                    .message([
                            eventType    : "payment_completed",
                            paymentId    : "PAY-GROOVY-001",
                            orderId      : "ORDER-GROOVY-001",
                            transactionId: "TXN-${UUID.randomUUID()}",
                            amount       : 2999.99,
                            currency     : "CNY",
                            completedTime: new Date(),
                            status       : "success"
                    ])
        }

        // 9. 订单状态更新事件
        ["confirmed", "shipped", "delivered"].each { status ->
            children.kafka("订单${status}事件") { kafka ->
                kafka.topic("order.status")
                        .key("order-groovy-001")
                        .message([
                                eventType     : "order_status_changed",
                                orderId       : "ORDER-GROOVY-001",
                                previousStatus: status == "confirmed" ? "pending" :
                                        status == "shipped" ? "confirmed" : "shipped",
                                currentStatus : status,
                                updateTime    : new Date(),
                                operator      : "system"
                        ])
            }
        }

        // 10. 系统监控事件
        children.kafka("系统监控数据") { kafka ->
            kafka.topic("system.monitoring")
                    .key("app-server-01")
                    .message([
                            timestamp: new Date(),
                            serverId : "app-server-01",
                            metrics  : [
                                    cpu              : [usage: 75.5, loadAverage: 2.1],
                                    memory           : [used: 4200, total: 8192, percentage: 51.3],
                                    network          : [inbound: 1500, outbound: 800], // KB/s
                                    activeConnections: 150,
                                    requestRate      : 200 // requests/minute
                            ],
                            alerts   : [],
                            status   : "healthy"
                    ])
        }

        // 11. 后置处理：测试完成事件
        children.kafkaPostprocessor("测试完成通知") { postprocessor ->
            postprocessor.topic("test.control")
                    .key("complete")
                    .message([
                            action     : "complete",
                            testSuite  : "groovy-kafka-test",
                            endTime    : new Date(),
                            status     : "success",
                            totalEvents: 15,
                            summary    : [
                                    userEvents     : 2,
                                    orderEvents    : 4,
                                    paymentEvents  : 2,
                                    inventoryEvents: 1,
                                    systemEvents   : 1
                            ]
                    ])
        }
    }
}

// 性能测试示例
def performanceTest() {
    def messageCount = 10000
    def partitionCount = 10

    suite("Kafka性能测试") { builder ->
        builder.children { children ->
            // 高并发消息发送
            (1..messageCount).each { i ->
                children.kafka("性能测试${i}") { kafka ->
                    kafka.topic("performance.test")
                            .key("perf-${i % partitionCount}")
                            .message([
                                    messageId: i,
                                    timestamp: new Date(),
                                    payload  : "性能测试数据" * 5,
                                    batch    : Math.floor(i / 100)
                            ])
                            .lingerMs(10)
                            .batchSize(65536)
                }
            }
        }
    }
}
```

## 相关文档

- [Kafka 官方文档](https://kafka.apache.org/documentation/)

- [示例项目](https://github.com/XiaoMiSum/ryze/tree/master/example/kafka-example)

---

**💡 提示**:
更多详细示例请参考 [example/kafka-example](https://github.com/XiaoMiSum/ryze/tree/master/example/kafka-example)
目录下的完整示例代码。