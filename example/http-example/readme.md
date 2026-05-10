# 🌐 HTTP 示例指南

## 📋 简介

本示例演示了如何使用 Ryze 框架进行 HTTP 协议测试。示例使用 MockServer 提供 HTTP 服务，无需额外的 Spring Boot 应用程序。

## 🚀 环境准备

### Mock HTTP 服务

本示例使用 MockServer 作为 HTTP 服务提供者，测试运行时会自动启动 Mock 服务，无需手动启动额外的应用程序。

Mock 服务提供以下接口：

- **GET /user** - 获取所有用户列表
- **GET /user/{id}** - 根据 ID 获取单个用户
- **POST /user** - 添加新用户
- **PUT /user** - 更新用户信息

服务默认运行在 `http://127.0.0.1:58081`

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
│   │       └── io/github/xiaomisum/ryze/http/example/
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/ryze/
│       │       ├── http/example/
│       │       │   ├── mock/
│       │       │   │   └── MockHttpServer.java    # Mock HTTP 服务器
│       │       │   ├── ExampleInterceptor.java    # 示例拦截器
│       │       │   ├── code/                      # Java代码测试用例
│       │       │   │   ├── CodeExTestCase.java        # Java异常测试类
│       │       │   │   ├── CodeTestCase.java          # Java代码测试类
│       │       │   │   └── GroovyCodeTestCase.groovy  # Groovy代码测试类
│       │       │   └── yaml/                      # YAML配置测试用例
│       │       │       └── YamlTestCase.java           # YAML测试类
│       │       └── debug/
│       │           └── DebugScenariosTest.java    # Debug场景测试
│       └── resources/
│           ├── 测试用例/                     # 测试用例配置
│           ├── 取样器/                       # HTTP 取样器模板
│           ├── 处理器/                       # 前置/后置处理器模板
│           ├── 配置元件/                     # HTTP 配置元件模板
│           └── META-INF/
│               └── services/
│                   └── io.github.xiaomisum.ryze.interceptor.Interceptor # 拦截器服务配置
└── pom.xml                                  # Maven 配置文件
```