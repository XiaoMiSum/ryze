# 🌐 HTTP 示例指南

## 📋 简介

本示例演示了如何使用 Ryze 框架进行 HTTP 协议测试。示例包含一个简单的 Spring Boot 应用程序和对应的 Ryze 测试用例。

## 🚀 环境准备

### 启动 Spring Boot 应用

1. 在 IDE 中运行 `http-example` 模块中的 `Application` 类
2. 或通过命令行启动：
   ```bash
   cd example/http-example
   mvn spring-boot:run
   ```

### 验证服务启动

访问以下 URL 验证服务是否正常运行：

- 用户接口: http://localhost:8080/user
- 健康检查: http://localhost:8080/actuator/health

## 🧪 执行 Ryze HTTP 测试

### 运行测试

1. 在 IDE 中运行 `http-example` 模块中的测试类
2. 或通过命令行运行：
   ```bash
   cd example/http-example
   mvn test
   ```

### 测试内容

测试用例包含以下场景：

- GET 请求测试
- POST 请求测试
- 请求参数验证
- 响应数据提取
- 结果断言验证

## 📊 预期结果

测试成功执行后，您应该看到类似以下的输出：

```
测试执行状态: SUCCESS
测试时长: 125ms
子测试数量: 2
```

### 测试报告

测试执行完成后，会在 `target/allure-results` 目录生成 Allure 测试报告数据，可通过以下命令查看报告：

```bash
allure serve target/allure-results
```

## 📁 项目结构

```
http-example/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── io/github/xiaomisum/example/http/
│   │           ├── Application.java        # Spring Boot 应用主类
│   │           └── controller/
│   │               └── UserController.java # 用户控制器
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/example/http/
│       │       └── HttpExampleTest.java   # Ryze 测试类
│       └── resources/
│           ├── 测试集合/                  # 测试集合配置
│           ├── 取样器/                    # HTTP 取样器模板
│           ├── 处理器/                    # 前置/后置处理器模板
│           └── 配置元件/                  # HTTP 配置元件模板
└── pom.xml                               # Maven 配置文件
```