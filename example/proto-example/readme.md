# Protocol示例项目

本项目是 Ryze 框架的 Protocol 示例，演示了如何使用 Spring Boot 创建一个 HTTP 服务，用于接收 Protocol 数据并将其打印到控制台。

## 功能特点

- 使用 Spring Boot 创建 HTTP 服务
- 接收 Protocol 格式的数据
- 将接收到的数据打印到控制台
- 无需持久化存储

## 环境准备

1. 安装 JDK 1.8 或更高版本
2. 安装 Maven 3.6 或更高版本

## 运行示例

可以通过以下方式运行示例：

```bash
# 方式1：使用Maven运行
mvn spring-boot:run

# 方式2：先构建，再运行
mvn clean package
java -jar target/protocol-example.jar
```

## 使用方法

启动服务后，可以通过 HTTP POST 请求发送 Protocol 数据：

```bash
curl -X POST http://localhost:8080/protocol/receive \
  -H "Content-Type: application/json" \
  -d '{"protocolName": "test-protocol", "data": {"key1": "value1", "key2": "value2"}}'
```

服务会将接收到的数据打印到控制台，并返回成功响应。

## API 接口

- **POST /protocol/receive**：接收 Protocol 数据
  - 请求体：JSON 格式的 Protocol 数据
  - 响应：成功或失败信息

## 注意事项

- 本示例仅演示了接收和打印 Protocol 数据的功能，不包含持久化存储
- 可以根据需要扩展处理逻辑，如添加验证、转换等