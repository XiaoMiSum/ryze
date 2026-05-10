# 🌐 WebSocket 示例指南

## 📋 简介

本示例演示了如何使用 Ryze 框架进行 WebSocket 协议测试。示例使用 Tyrus 提供 WebSocket 服务，无需额外的 Spring Boot 应用程序。

## 🚀 环境准备

### Mock WebSocket 服务

本示例使用 Tyrus 作为 WebSocket 服务提供者，测试运行时会自动启动 Mock 服务，无需手动启动额外的应用程序。

Mock 服务提供以下端点：

- **/ws/body/bytes** - 二进制消息回显端点
- **/ws/body/string** - 文本消息回显端点
- **/ws/path/{userId}** - 支持路径参数的端点
- **/ws/query** - 支持查询参数的端点

服务默认运行在 `ws://127.0.0.1:8080`

## 🧪 执行 Ryze WebSocket 测试

### 运行测试

1. 在 IDE 中运行 `websocket-example` 模块中的测试类
2. 或通过命令行运行：
   ```bash
   cd example/websocket-example
   mvn test
   ```

### 测试内容

测试用例包含以下场景：

- WebSocket 连接测试
- 二进制消息发送与接收
- 文本消息发送与接收
- 路径参数测试
- 查询参数测试
- 请求参数验证
- 响应数据提取
- 结果断言验证

## 📊 预期结果

### 测试报告

测试执行完成后，会在 `target/allure-results` 目录生成 Allure 测试报告数据，可通过以下命令查看报告：

```bash
allure serve target/allure-results
```

## 📁 项目结构

```
websocket-example/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── io/github/xiaomisum/ryze/websocket/example/
│   │           └── (已移除 Spring Boot 相关文件)
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/ryze/websocket/example/
│       │       ├── mock/                      # Mock 服务
│       │       │   └── MockWebSocketServer.java # WebSocket Mock 服务器
│       │       ├── code/                      # Java代码测试用例
│       │       │   ├── CodeExTestCase.java        # Java异常测试类
│       │       │   ├── CodeTestCase.java          # Java代码测试类
│       │       │   └── GroovyCodeTestCase.groovy  # Groovy代码测试类
│       │       └── yaml/                      # YAML配置测试用例
│       │           └── YamlTestCase.java           # YAML测试类
│       └── resources/
│           ├── 测试用例/                     # 测试用例配置
│           ├── 取样器/                       # WebSocket 取样器模板
│           ├── 处理器/                       # 前置/后置处理器模板
│           └── 配置元件/                     # WebSocket 配置元件模板
└── pom.xml                                  # Maven 配置文件
```