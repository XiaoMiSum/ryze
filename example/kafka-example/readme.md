# 🚀 Kafka 示例指南

## 📋 简介

本示例演示了如何使用 Ryze 框架进行 Kafka 消息测试。示例包含 Docker 环境搭建、Kafka Java 消费者和对应的 Ryze 测试用例。

## 🚀 环境准备

### Docker Kafka 环境搭建

1. 拉取 Kafka 镜像
   ```bash
   docker pull apache/kafka
   ```

2. 启动 Kafka 容器
   ```bash
   docker run -d -p 9092:9092 --name broker apache/kafka:latest
   ```

3. 在 Kafka 容器中打开一个 shell
   ```bash
   docker exec --workdir /opt/kafka/bin/ -it broker sh
   ```

4. 创建一个测试 Topic
   ```bash
   ./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic test-topic
   ```

5. 使用 Kafka 附带的控制台生产者将消息写入主题
   ```bash
   ./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test-topic
   ```

   在生产者控制台中输入：
   ```
   Hello world
   ```

   然后按 `Ctrl+C` 退出控制台。

6. 读取 Topic 中的消息进行验证
   ```bash
   ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --from-beginning
   ```

### 启动 Kafka Java 消费者

1. 在 IDE 中运行 `kafka-example` 模块中的 `Consumer` 类
2. 或通过命令行启动：
   ```bash
   cd example/kafka-example
   mvn exec:java -Dexec.mainClass="io.github.xiaomisum.example.kafka.Consumer"
   ```

### 验证环境

通过以下命令验证 Kafka 服务是否正常运行：

```bash
docker ps | grep kafka
```

## 🧪 执行 Ryze Kafka 测试

### 运行测试

1. 在 IDE 中运行 `kafka-example` 模块中的测试类
2. 或通过命令行运行：
   ```bash
   cd example/kafka-example
   mvn test
   ```

### 测试内容

测试用例包含以下场景：

- Kafka 生产者消息发送测试
- 消息内容验证测试
- 消息分区测试
- 消息序列化测试
- 结果断言验证

## 📊 预期结果

### 执行结果验证

Kafka 消费者控制台应该打印 Kafka Sampler 发送的 message：
![Kafka 消费者](images/kafka_consumer.png)

### 测试报告

测试执行完成后，会在 `target/allure-results` 目录生成 Allure 测试报告数据，可通过以下命令查看报告：

```bash
allure serve target/allure-results
```

## 📁 项目结构

```
kafka-example/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── io/github/xiaomisum/ryze/kafka/example/
│   │           ├── Consumer.java         # Kafka 消费者
│   │           └── TestObj.java          # 测试对象类
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/ryze/kafka/example/
│       │       ├── code/                 # Java代码测试用例
│       │       │   ├── CodeTestCase.java      # Java代码测试类
│       │       │   └── GroovyCodeTestCase.groovy  # Groovy代码测试类
│       │       └── yaml/                 # YAML配置测试用例
│       │           └── YamlTestCase.java      # YAML测试类
│       └── resources/
│           ├── 测试用例/                # 测试用例配置
│           ├── 取样器/                  # Kafka 取样器模板
│           ├── 处理器/                  # 前置/后置处理器模板
│           └── 配置元件/                # Kafka 配置元件模板
├── images/                             # 文档图片
│   └── kafka_consumer.png              # Kafka 消费者截图
└── pom.xml                             # Maven 配置文件
```