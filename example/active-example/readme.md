# 🎯 ActiveMQ 示例指南

## 📋 简介

本示例演示了如何使用 Ryze 框架进行 ActiveMQ 消息测试。示例包含 Docker 环境搭建、ActiveMQ Java 消费者和对应的 Ryze 测试用例。

## 🚀 环境准备

### Docker ActiveMQ 环境搭建

1. 拉取 ActiveMQ Artemis 镜像
   ```bash
   docker pull apache/activemq-artemis:latest
   ```

2. 启动 ActiveMQ 容器
   ```bash
   docker run --detach --name same-active -p 61616:61616 -p 8161:8161 apache/activemq-artemis:latest
   ```

3. 访问 ActiveMQ Web 控制台
   ```
   URL: http://localhost:8161
   用户名: artemis
   密码: artemis
   ```

### 启动 ActiveMQ Java 消费者

1. 在 IDE 中运行 `active-example` 模块中的 `Consumer` 类
2. 或通过命令行启动：
   ```bash
   cd example/active-example
   mvn exec:java -Dexec.mainClass="io.github.xiaomisum.example.active.Consumer"
   ```

### 验证环境

通过以下命令验证 ActiveMQ 服务是否正常运行：
```bash
docker ps | grep activemq
```

访问 http://localhost:8161 确认管理界面可以正常访问。

## 🧪 执行 Ryze ActiveMQ 测试

### 运行测试

1. 在 IDE 中运行 `active-example` 模块中的测试类
2. 或通过命令行运行：
   ```bash
   cd example/active-example
   mvn test
   ```

### 测试内容

测试用例包含以下场景：
- ActiveMQ 消息发送测试
- 队列消息验证测试
- 消息持久化测试
- 消息确认机制测试
- 结果断言验证

## 📊 预期结果

测试成功执行后，您应该看到类似以下的输出：

```
测试执行状态: SUCCESS
测试时长: 145ms
子测试数量: 2
```

### 执行结果验证

ActiveMQ 消费者控制台应该打印 Active MQ Sampler 发送的 message：
![ActiveMQ 消费者](images/active_consumer.png)

### 测试报告

测试执行完成后，会在 `target/allure-results` 目录生成 Allure 测试报告数据，可通过以下命令查看报告：

```bash
allure serve target/allure-results
```

## 📁 项目结构

```
active-example/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── io/github/xiaomisum/example/active/
│   │           ├── Consumer.java            # ActiveMQ 消费者
│   │           └── ActiveApplication.java  # ActiveMQ 应用主类
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/example/active/
│       │       └── ActiveExampleTest.java   # Ryze 测试类
│       └── resources/
│           ├── 测试集合/                  # 测试集合配置
│           ├── 取样器/                    # ActiveMQ 取样器模板
│           ├── 处理器/                    # 前置/后置处理器模板
│           └── 配置元件/                  # ActiveMQ 配置元件模板
├── images/                               # 文档图片
│   └── active_consumer.png               # ActiveMQ 消费者截图
└── pom.xml                               # Maven 配置文件
```

## 🤝 相关文档

- [ActiveMQ 协议文档](../../docs/protocols/ActiveMQ.md)
- [测试集合管理](../../docs/help/测试集合.md)
- [变量与函数](../../docs/help/变量与函数.md)