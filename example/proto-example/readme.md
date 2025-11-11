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
java -jar target/proto-example.jar
```

## 使用方法

启动服务后，运行 test 中的示例脚本

