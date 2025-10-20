# 协议 API

协议API为每种支持的协议提供了专门的接口和实现，确保各种协议测试的一致性和可扩展性。

## HTTP协议API

### HttpSampler 类

HTTP取样器实现，支持完整的HTTP请求功能。

#### 主要配置项

- `method`: HTTP方法（GET、POST、PUT、DELETE等）
- `protocol`: 协议（http、https）
- `host`: 主机地址
- `port`: 端口号
- `path`: 请求路径
- `headers`: 请求头
- `query`: 查询参数
- `data`: 表单数据
- `body`: 请求体

#### 使用示例

```java
MagicBox.http(http ->{
        http.

config(config ->config
        .

method("POST")
        .

protocol("https")
        .

host("api.example.com")
        .

path("/users")
        .

headers(Map.of("Content-Type", "application/json"))
        .

body(Map.of("name", "John","email","john@example.com"))
        );
        });
```

## Dubbo协议API

### DubboSampler 类

Dubbo取样器实现，支持Dubbo服务调用。

#### 主要配置项

- `registry`: 注册中心配置
- `interfaceName`: 接口名称
- `methodName`: 方法名称
- `parameterTypes`: 参数类型列表
- `parameters`: 参数值列表

#### 使用示例

```java
MagicBox.dubbo(dubbo ->{
        dubbo.

registry("zookeeper://localhost:2181")
         .

interfaceName("com.example.UserService")
         .

methodName("getUserById")
         .

parameterTypes("java.lang.Long")
         .

parameters(123L);
});
```

## JDBC协议API

### JdbcSampler 类

JDBC取样器实现，支持数据库查询和更新操作。

#### 主要配置项

- `datasource`: 数据源引用
- `sql`: SQL语句
- `args`: SQL参数

#### 使用示例

```java
MagicBox.jdbc(jdbc ->{
        jdbc.

datasource("mysqlDataSource")
        .

sql("SELECT * FROM users WHERE id = ?")
        .

args(123);
});
```

## Redis协议API

### RedisSampler 类

Redis取样器实现，支持各种Redis命令操作。

#### 主要配置项

- `datasource`: 数据源引用
- `command`: Redis命令
- `args`: 命令参数

#### 使用示例

```java
MagicBox.redis(redis ->{
        redis.

datasource("redisDataSource")
         .

command("SET")
         .

args("user:123","John");
});
```

## Kafka协议API

### KafkaSampler 类

Kafka取样器实现，支持消息生产和消费。

#### 主要配置项

- `bootstrapServers`: Kafka服务器地址
- `topic`: 主题名称
- `key`: 消息键
- `message`: 消息内容
- `acks`: 确认机制

#### 使用示例

```java
MagicBox.kafka(kafka ->{
        kafka.

bootstrapServers("localhost:9092")
         .

topic("user-events")
         .

key("user-123")
         .

message(Map.of("action", "login","userId",123));
        });
```

## RabbitMQ协议API

### RabbitMQSampler 类

RabbitMQ取样器实现，支持消息队列操作。

#### 主要配置项

- `host`: 主机地址
- `port`: 端口号
- `queue`: 队列配置
- `exchange`: 交换机配置
- `message`: 消息内容

#### 使用示例

```java
MagicBox.rabbitmq(rabbitmq ->{
        rabbitmq.

host("localhost")
            .

port(5672)
            .

queue(queue ->queue.

name("user.notifications"))
        .

message("Hello, RabbitMQ!");
});
```

## ActiveMQ协议API

### ActiveMQSampler 类

ActiveMQ取样器实现，支持消息队列操作。

#### 主要配置项

- `brokerUrl`: Broker URL
- `queue`: 队列名称
- `topic`: 主题名称
- `message`: 消息内容

#### 使用示例

```java
MagicBox.activemq(activemq ->{
        activemq.

brokerUrl("tcp://localhost:61616")
            .

queue("user.notifications")
            .

message("Hello, ActiveMQ!");
});
```

## MongoDB协议API

### MongoSampler 类

MongoDB取样器实现，支持文档数据库操作。

#### 主要配置项

- `datasource`: 数据源引用
- `database`: 数据库名称
- `collection`: 集合名称
- `operation`: 操作类型（insert、find、update、delete）
- `document`: 文档内容
- `filter`: 查询过滤条件
- `update`: 更新操作

#### 使用示例

```java
MagicBox.mongo(mongo ->{
        mongo.

datasource("mongoDataSource")
         .

database("testdb")
         .

collection("users")
         .

operation("insert")
         .

document(Map.of("name", "John","email","john@example.com"));
        });
```