# Proto 示例项目

本项目是 Ryze 框架的 Proto (Protobuf) 协议示例，演示了如何使用 Ryze 进行 Protobuf 格式的 HTTP 和 WebSocket 协议测试。

## 功能特点

- 使用 MockServer 提供 HTTP 服务（无需额外启动 Spring Boot 应用）
- 支持 Protobuf 格式的 HTTP 请求/响应
- 支持 Protobuf 格式的 WebSocket 通信
- 演示 YAML 配置和 Java/Groovy 编码两种测试方式

## 环境准备

1. 安装 JDK 21 或更高版本
2. 安装 Maven 3.6 或更高版本

## 运行示例

本示例使用 MockServer 作为 HTTP 服务提供者，测试运行时会自动启动 Mock 服务，无需手动启动额外的应用程序。

Mock 服务提供以下接口（运行在 `http://127.0.0.1:8080`）：

- **GET /user** - 获取所有用户列表
- **GET /user/{id}** - 根据 ID 获取单个用户
- **POST /user** - 添加新用户
- **PUT /user** - 更新用户信息

### 执行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=YamlTestCase
mvn test -Dtest=CodeTestCase
mvn test -Dtest=GroovyCodeTestCase
```

## 测试内容

示例包含以下测试场景：

- HTTP + Protobuf 协议测试
- WebSocket + Protobuf 协议测试
- 前置处理器（Preprocessor）测试
- 后置处理器（Postprocessor）测试
- 数据提取器（Extractor）测试
- 验证器（Validator）测试

## 使用方法

直接在 IDE 中运行 test 中的示例测试类即可，MockServer 会在测试开始前自动启动，测试结束后自动停止。
