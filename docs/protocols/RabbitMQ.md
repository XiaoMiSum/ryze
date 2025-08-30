# RabbitMQ 协议

## 概述

RabbitMQ 协议支持为 Ryze 测试框架提供了与 RabbitMQ 消息代理进行交互的能力。支持多种交换机类型（direct、topic、fanout、headers）、队列管理、路由规则等 AMQP 协议特性。

## 依赖引入

```xml
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-rabbit</artifactId>
    <version>6.0.1</version>
</dependency>
```

## RabbitMQ 配置元件

### YAML 配置方式

```yaml
# rabbitmq 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: rabbitmq_defaults  # 配置元件类型
config:
  host: 'localhost' # rabbitmq服务器地址
  port: 5672 # rabbitmq服务器端口，可空，默认值：5672
  username: guest # rabbitmq 用户名，可空，默认值：guest
  password: guest # rabbitmq 密码，可空，默认值：guest
  timeout: 5000 # 连接超时时间，毫秒，默认值：60000
  queue: # 队列配置
    name: ryze  # 消息队列名称
    durable:  # 是否持久化队列，可空，默认值：false
    exclusive:  # 是否独占队列，可空，默认值：false
    auto_delete:  # 是否自动删除，可空，默认值：false
    arguments:  # 消息队列其他属性，keyword对象，可空
  exchange: # exchange 配置，可空，简单模式（不经过交换机）时，此配置为空
    name: ryze # exchange 名称
    type: topic # 交换机类型，参考值：fanout、direct、topic，可空，默认值：topic
    routing_key:  # 路由key，当 type为 fanout 时可空
```

## RabbitMQ 取样器

### YAML 配置方式

```yaml
title: 标准rabbitmq取样器
testclass: rabbitmqSampler  # 取样器类型
config: # 取样器配置
  host: 'localhost' # rabbitmq服务器地址
  port: 5672 # rabbitmq服务器端口
  username: guest # rabbitmq 用户名
  password: guest # rabbitmq 密码
  timeout: 5000 # 连接超时时间，毫秒
  queue: # 队列配置
    name: ryze  # 消息队列名称
    durable:  # 是否持久化队列
    exclusive:  # 是否独占队列
    auto_delete:  # 是否自动删除
    arguments:  # 消息队列其他属性
  exchange: # exchange 配置
    name: ryze # exchange 名称
    type: topic # 交换机类型：fanout、direct、topic
    routing_key:  # 路由key
  message: # 发送的消息
    name: test
    age: 18
```

## 常见问题

1. **连接失败**：检查 RabbitMQ 服务是否正常运行
2. **认证失败**：确认用户名和密码是否正确
3. **队列创建失败**：检查用户是否有创建队列的权限
4. **交换机不存在**：确保交换机已创建或有创建权限
5. **路由失败**：检查路由键和绑定规则是否正确

## Java API 示例

```java
import static io.github.xiaomisum.ryze.protocol.rabbitmq.RabbitMQMagicBox.*;

public class RabbitMQApiExample {
    
    public void testDirectExchange() {
        // 直接交换机消息
        Result result = rabbitmq("发送直接消息", builder -> builder
            .host("localhost")
            .port(5672)
            .username("guest")
            .password("guest")
            .queue(queue -> queue
                .name("user.notifications")
                .durable(true)
                .autoDelete(false)
            )
            .exchange(exchange -> exchange
                .name("user.direct")
                .type("direct")
                .routingKey("user.notification")
            )
            .message(Map.of(
                "userId", 12345,
                "title", "系统通知",
                "content", "您的账户已成功注册"
            ))
        );
        
        assertThat(result.isSuccess()).isTrue();
    }
    
    public void testTopicExchange() {
        // 主题交换机消息
        rabbitmq("发送主题消息", builder -> builder
            .host("localhost")
            .exchange(exchange -> exchange
                .name("logs.topic")
                .type("topic")
                .routingKey("application.error.database")
            )
            .message("数据库连接失败: Connection timeout")
        );
    }
}
```

## Groovy API 示例

```groovy
import static io.github.xiaomisum.ryze.protocol.rabbitmq.RabbitMQMagicBox.*

// 简单队列消息
def sendQueueMessage() {
    rabbitmq("发送队列消息") { builder ->
        builder.host("localhost")
               .port(5672)
               .username("guest")
               .password("guest")
               .queue { queue ->
                   queue.name("task.queue")
                        .durable(true)
               }
               .message([
                   taskId: UUID.randomUUID().toString(),
                   type: "email",
                   recipient: "user@example.com",
                   subject: "欢迎使用我们的服务"
               ])
    }
}

// 完整测试套件
suite("RabbitMQ消息测试") { builder ->
    builder.configure { configure ->
        configure.rabbitmq { rabbitmq ->
            rabbitmq.host("localhost")
                    .port(5672)
                    .username("guest")
                    .password("guest")
                    .timeout(5000)
        }
    }
    
    builder.children { children ->
        // 1. 发送直接消息
        children.rabbitmq("发送直接消息") { rabbitmq ->
            rabbitmq.queue { queue ->
                        queue.name("direct.queue")
                             .durable(true)
                    }
                    .message("直接消息内容")
        }
        
        // 2. 发送交换机消息
        children.rabbitmq("发送交换机消息") { rabbitmq ->
            rabbitmq.exchange { exchange ->
                        exchange.name("user.fanout")
                                .type("fanout")
                    }
                    .message([
                        eventType: "user_updated",
                        userId: "12345",
                        changes: ["email", "profile"],
                        timestamp: new Date()
                    ])
        }
        
        // 3. 发送主题消息
        ["info", "warning", "error"].each { level ->
            children.rabbitmq("发送${level}日志") { rabbitmq ->
                rabbitmq.exchange { exchange ->
                            exchange.name("logs.topic")
                                    .type("topic")
                                    .routingKey("application.${level}.service")
                        }
                        .message("日志内容: ${level} level message")
            }
        }
    }
}
```

## 相关文档

- [配置元件示例](../template/配置元件/rabbit_defaults.yaml)
- [取样器示例](../template/取样器/rabbit_sampler.yaml)
- [RabbitMQ 官方文档](https://www.rabbitmq.com/documentation.html)