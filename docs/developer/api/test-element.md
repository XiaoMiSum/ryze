# 测试元素 API

测试元素API涵盖了所有测试组件的接口和实现，包括取样器、处理器、配置元件等。

## 测试元素基础类

### TestElement 类
所有测试元素的基类，提供了通用的属性和方法。

#### 主要属性
- `title`: 元素标题
- `variables`: 变量映射
- `context`: 执行上下文

#### 主要方法
- `execute(Context context)`: 执行测试元素
- `getTitle()`: 获取标题
- `getVariables()`: 获取变量映射

## 取样器 API

### Sampler 接口
取样器接口，定义了执行测试操作的方法。

#### 主要方法
- `sample(Context context)`: 执行取样操作

### HttpSampler 类
HTTP取样器实现，用于发送HTTP请求。

### DubboSampler 类
Dubbo取样器实现，用于调用Dubbo服务。

### JdbcSampler 类
JDBC取样器实现，用于执行数据库查询。

### RedisSampler 类
Redis取样器实现，用于执行Redis命令。

### KafkaSampler 类
Kafka取样器实现，用于发送Kafka消息。

### RabbitMQSampler 类
RabbitMQ取样器实现，用于发送RabbitMQ消息。

### ActiveMQSampler 类
ActiveMQ取样器实现，用于发送ActiveMQ消息。

### MongoSampler 类
MongoDB取样器实现，用于执行MongoDB操作。

## 处理器 API

### Processor 接口
处理器接口，定义了在测试执行前后处理数据的方法。

#### 主要方法
- `process(Context context)`: 执行处理逻辑

### Preprocessor 类
前置处理器，用于在测试执行前准备数据。

### Postprocessor 类
后置处理器，用于在测试执行后清理数据。

## 配置元件 API

### ConfigureElement 接口
配置元件接口，用于为测试元素提供默认配置。

#### 主要方法
- `apply(TestElement element)`: 应用配置到测试元素

### HttpDefaults 类
HTTP默认配置元件。

### DubboDefaults 类
Dubbo默认配置元件。

### JdbcDefaults 类
JDBC默认配置元件。

### RedisDefaults 类
Redis默认配置元件。

### KafkaDefaults 类
Kafka默认配置元件。

### RabbitDefaults 类
RabbitMQ默认配置元件。

### ActiveDefaults 类
ActiveMQ默认配置元件。

### MongoDefaults 类
MongoDB默认配置元件。