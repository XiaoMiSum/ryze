# 🐰 RabbitMQ 示例指南

## 📋 简介

本示例演示了如何使用 Ryze 框架进行 RabbitMQ 消息测试。示例包含 Docker 环境搭建、RabbitMQ Java 消费者和对应的 Ryze 测试用例。

## 🚀 环境准备

### Docker RabbitMQ 环境搭建

1. 拉取 RabbitMQ 镜像（包含管理插件）
   ```bash
   docker pull rabbitmq:management
   ```

2. 启动 RabbitMQ 容器
   ```bash
   docker run -d -p 5672:5672 -p 15672:15672 --hostname my-rabbit --name rabbit rabbitmq:management
   ```

3. 访问 RabbitMQ Web 控制台
   ```
   URL: http://localhost:15672
   用户名: guest
   密码: guest
   ```

### 启动 RabbitMQ Java 消费者

1. 在 IDE 中运行 `rabbit-example` 模块中的 `Consumer` 类
2. 或通过命令行启动：
   ```bash
   cd example/rabbit-example
   mvn exec:java -Dexec.mainClass="io.github.xiaomisum.example.rabbit.Consumer"
   ```

### 验证环境

通过以下命令验证 RabbitMQ 服务是否正常运行：

```bash
docker ps | grep rabbit
```

访问 http://localhost:15672 确认管理界面可以正常访问。

## 🧪 执行 Ryze RabbitMQ 测试

### 运行测试

1. 在 IDE 中运行 `rabbit-example` 模块中的测试类
2. 或通过命令行运行：
   ```bash
   cd example/rabbit-example
   mvn test
   ```

### 测试内容

测试用例包含以下场景：

- RabbitMQ 消息发送测试
- 队列消息验证测试
- 消息路由测试
- 消息确认机制测试
- 结果断言验证

## 📊 预期结果

测试成功执行后，您应该看到类似以下的输出：

```
测试执行状态: SUCCESS
测试时长: 178ms
子测试数量: 2
```

### 执行结果验证

RabbitMQ 消费者控制台应该打印 Rabbit MQ Sampler 发送的 message：
![RabbitMQ 消费者](images/rabbit_consumer.png)

### 测试报告

测试执行完成后，会在 `target/allure-results` 目录生成 Allure 测试报告数据，可通过以下命令查看报告：

```bash
allure serve target/allure-results
```

## 📁 项目结构

```
rabbit-example/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── io/github/xiaomisum/example/rabbit/
│   │           ├── Consumer.java           # RabbitMQ 消费者
│   │           └── RabbitApplication.java  # RabbitMQ 应用主类
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/example/rabbit/
│       │       └── RabbitExampleTest.java  # Ryze 测试类
│       └── resources/
│           ├── 测试集合/                  # 测试集合配置
│           ├── 取样器/                    # RabbitMQ 取样器模板
│           ├── 处理器/                    # 前置/后置处理器模板
│           └── 配置元件/                  # RabbitMQ 配置元件模板
├── images/                               # 文档图片
│   └── rabbit_consumer.png               # RabbitMQ 消费者截图
└── pom.xml                               # Maven 配置文件
```
