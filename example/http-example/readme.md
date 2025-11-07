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

- 用户接口: http://localhost:58081/user

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
 苏│   │       └── io/github/xiaomisum/ryze/http/example/
│   │           └── springboot/
│   │               ├── Application.java        # Spring Boot 应用主类
│   │               ├── ErrorLogServiceImpl.java # 错误日志服务实现
│   │               └── TestController.java    # 测试控制器
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/ryze/http/example/
│       │       ├── ExampleInterceptor.java    # 示例拦截器
│       │       ├── code/                      # Java代码测试用例
│       │       │   ├── CodeExTestCase.java        # Java异常测试类
│       │       │   ├── CodeTestCase.java          # Java代码测试类
│       │       │   └── GroovyCodeTestCase.groovy  # Groovy代码测试类
│       │       └── yaml/                      # YAML配置测试用例
│       │           └── YamlTestCase.java           # YAML测试类
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