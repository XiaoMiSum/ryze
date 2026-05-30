# 📡 MQTT 示例指南

## 📋 简介

本示例演示了如何使用 Ryze 框架进行 MQTT 协议测试。示例包含 MQTT Broker 环境搭建和对应的 Ryze 测试用例。

## 🚀 环境准备

### Docker MQTT Broker 环境搭建

1. 拉取 Eclipse Mosquitto 镜像
   ```bash
   docker pull eclipse-mosquitto
   ```

2. 启动 Mosquitto 容器，将容器 1883 端口映射到宿主机 1883 端口
   ```bash
   docker run -d -p 1883:1883 --name mosquitto eclipse-mosquitto:latest
   ```

3. 验证 MQTT Broker 是否正常运行
   ```bash
   docker ps | grep mosquitto
   ```

### 验证环境

可使用 MQTT 客户端工具（如 mosquitto_pub/mosquitto_sub）验证连接：

```bash
# 订阅主题
mosquitto_sub -h 127.0.0.1 -t test/topic

# 发布消息
mosquitto_pub -h 127.0.0.1 -t test/topic -m "hello"
```

## 📥 MQTT 消费者示例

为了方便测试，我们提供了一个 MQTT 消费者示例程序，用于接收 Ryze 发布的消息。

### 启动消费者

```bash
# 编译项目
mvn clean compile

# 运行消费者
mvn exec:java -Dexec.mainClass="io.github.xiaomisum.ryze.mqtt.example.MqttConsumer"
```

或者在 IDE 中直接运行 `MqttConsumer.java` 的 main 方法。

### 消费者功能

- 自动连接到本地 MQTT Broker (tcp://127.0.0.1:1883)
- 订阅多个主题：
  - `sensor/temperature` - 传感器温度数据
  - `device/status/#` - 设备状态（支持通配符）
  - `ryze/test/#` - Ryze 测试主题
- 实时打印接收到的消息

### 配合测试使用

1. 先启动 MQTT 消费者
2. 然后运行 Ryze MQTT 测试用例
3. 在消费者控制台查看接收到的消息

## 🧪 执行 Ryze MQTT 测试

### 运行测试

1. 在 IDE 中运行 `mqtt-example` 模块中的测试类
2. 或通过命令行运行：
   ```bash
   cd example/mqtt-example
   mvn test
   ```

### 测试内容

测试用例包含以下场景：

- MQTT Broker 连接测试
- 消息发布（Publish）测试
- 消息订阅（Subscribe）测试
- 结果断言验证

## 📊 预期结果

### 测试报告

测试执行完成后，会在 `target/allure-results` 目录生成 Allure 测试报告数据，可通过以下命令查看报告：

```bash
allure serve target/allure-results
```

## 📁 项目结构

```
mqtt-example/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── io/github/xiaomisum/ryze/mqtt/example/
│   │           └── MqttConsumer.java      # MQTT 消费者示例
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/ryze/mqtt/example/
│       │       ├── code/                 # Java代码测试用例
│       │       │   └── CodeTestCase.java  # MQTT Java代码测试类
│       │       └── yaml/                 # YAML配置测试用例
│       │           └── YamlTestCase.java  # MQTT YAML测试类
│       └── resources/
│           ├── 取样器/                   # MQTT 取样器模板
│           ├── 处理器/                   # 前置/后置处理器模板
│           ├── 测试用例/                 # 测试用例配置
│           └── 配置元件/                 # MQTT 配置元件模板
└── pom.xml                              # Maven 配置文件
```
