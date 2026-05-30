# 🌐 CoAP 示例指南

## 📋 简介

本示例演示了如何使用 Ryze 框架进行 CoAP（Constrained Application Protocol）协议测试。示例包含 CoAP 服务端环境搭建和对应的 Ryze 测试用例。

## 🚀 环境准备

### CoAP 服务环境

Ryze CoAP 测试可以直接使用本示例提供的 CoAP 消费者程序，无需额外搭建 Docker 环境。

> **注意**：如果你需要测试外部 CoAP 服务，可以使用 Docker 或其他方式部署。

## 📥 CoAP 消费者示例

为了方便测试，我们提供了一个 CoAP 消费者（服务器）示例程序，用于接收 Ryze 发送的 CoAP 请求。

### 启动消费者

```bash
# 编译项目
mvn clean compile

# 运行消费者
mvn exec:java -Dexec.mainClass="io.github.xiaomisum.ryze.coap.example.CoapConsumer"
```

或者在 IDE 中直接运行 `CoapConsumer.java` 的 main 方法。

### 消费者功能

- 启动本地 CoAP 服务器，监听端口 5683
- 提供多个资源路径：
  - `/sensor` - 传感器资源（支持 GET/POST/PUT/DELETE）
  - `/temperature` - 温度数据（支持 GET/POST）
  - `/device` - 设备注册（支持 POST）
- 实时打印接收到的请求
- 自动返回响应

### 配合测试使用

1. 先启动 CoAP 消费者
2. 然后运行 Ryze CoAP 测试用例
3. 在消费者控制台查看接收到的请求

## 🧪 执行 Ryze CoAP 测试

### 运行测试

1. 在 IDE 中运行 `coap-example` 模块中的测试类
2. 或通过命令行运行：
   ```bash
   cd example/coap-example
   mvn test
   ```

### 测试内容

测试用例包含以下场景：

- CoAP GET 请求测试
- CoAP POST 请求测试
- CoAP PUT 请求测试
- CoAP DELETE 请求测试
- CON/NON 消息模式测试
- 结果断言验证

## 📊 预期结果

### 测试报告

测试执行完成后，会在 `target/allure-results` 目录生成 Allure 测试报告数据，可通过以下命令查看报告：

```bash
allure serve target/allure-results
```

## 📁 项目结构

```
coap-example/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── io/github/xiaomisum/ryze/coap/example/
│   │           └── CoapConsumer.java      # CoAP 消费者示例
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/ryze/coap/example/
│       │       ├── code/                 # Java代码测试用例
│       │       │   └── CodeTestCase.java  # CoAP Java代码测试类
│       │       └── yaml/                 # YAML配置测试用例
│       │           └── YamlTestCase.java  # CoAP YAML测试类
│       └── resources/
│           ├── 取样器/                   # CoAP 取样器模板
│           ├── 处理器/                   # 前置/后置处理器模板
│           ├── 测试用例/                 # 测试用例配置
│           └── 配置元件/                 # CoAP 配置元件模板
└── pom.xml                              # Maven 配置文件
```
