# 核心 API

核心API提供了Ryze框架的基础功能，包括测试套件的创建、变量管理、配置元素等。

## MagicBox 类

MagicBox是Ryze框架的核心入口类，提供了创建各种测试元素的方法。

### 方法列表

#### suite(String title, Consumer&lt;SuiteBuilder&gt; consumer)
创建一个测试套件。

```java
MagicBox.suite("测试套件名称", suite -> {
    // 配置测试套件
});
```

#### http(Consumer&lt;HttpSamplerBuilder&gt; consumer)
创建一个HTTP取样器。

```java
MagicBox.http(http -> {
    // 配置HTTP取样器
});
```

#### dubbo(Consumer&lt;DubboSamplerBuilder&gt; consumer)
创建一个Dubbo取样器。

```java
MagicBox.dubbo(dubbo -> {
    // 配置Dubbo取样器
});
```

#### jdbc(Consumer&lt;JdbcSamplerBuilder&gt; consumer)
创建一个JDBC取样器。

```java
MagicBox.jdbc(jdbc -> {
    // 配置JDBC取样器
});
```

#### redis(Consumer&lt;RedisSamplerBuilder&gt; consumer)
创建一个Redis取样器。

```java
MagicBox.redis(redis -> {
    // 配置Redis取样器
});
```

#### kafka(Consumer&lt;KafkaSamplerBuilder&gt; consumer)
创建一个Kafka取样器。

```java
MagicBox.kafka(kafka -> {
    // 配置Kafka取样器
});
```

#### rabbitmq(Consumer&lt;RabbitMQSamplerBuilder&gt; consumer)
创建一个RabbitMQ取样器。

```java
MagicBox.rabbitmq(rabbitmq -> {
    // 配置RabbitMQ取样器
});
```

#### activemq(Consumer&lt;ActiveMQSamplerBuilder&gt; consumer)
创建一个ActiveMQ取样器。

```java
MagicBox.activemq(activemq -> {
    // 配置ActiveMQ取样器
});
```

#### mongo(Consumer&lt;MongoSamplerBuilder&gt; consumer)
创建一个MongoDB取样器。

```java
MagicBox.mongo(mongo -> {
    // 配置MongoDB取样器
});
```