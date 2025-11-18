# 🔌 Dubbo 示例指南

## 📋 简介

本示例演示了如何使用 Ryze 框架进行 Dubbo 服务测试。示例包含 Docker Zookeeper 环境搭建、Dubbo Java 服务端和对应的 Ryze
测试用例。

## 🚀 环境准备

### Docker Zookeeper 环境搭建

1. 拉取 Zookeeper 镜像
   ```bash
   docker pull zookeeper:latest
   ```

2. 启动 Zookeeper 容器
   ```bash
   docker run --name some-zookeeper -p 42181:2181 -p 42888:2888 -p 43888:3888 -p 48080:8080 --restart always -d zookeeper
   ```

### 启动 Dubbo Java 服务端

1. 在 IDE 中运行 `dubbo-example` 模块中的 `DubboApplication` 类
2. 或通过命令行启动：
   ```bash
   cd example/dubbo-example
   mvn spring-boot:run
   ```

### 验证环境

通过以下命令验证 Zookeeper 和 Dubbo 服务是否正常运行：

```bash
docker ps | grep zookeeper
```

## 🧪 执行 Ryze Dubbo 测试

### 运行测试

1. 在 IDE 中运行 `dubbo-example` 模块中的测试类
2. 或通过命令行运行：
   ```bash
   cd example/dubbo-example
   mvn test
   ```

### 测试内容

测试用例包含以下场景：

- Dubbo 服务调用测试
- 参数传递测试
- 返回值验证测试
- 异常处理测试
- 结果断言验证

## 📊 预期结果

### 执行结果验证

Dubbo 服务端控制台应该打印 Dubbo Sampler 的请求信息：
![Dubbo 服务端](images/dubbo_service.png)

### 测试报告

测试执行完成后，会在 `target/allure-results` 目录生成 Allure 测试报告数据，可通过以下命令查看报告：

```bash
allure serve target/allure-results
```

## 📁 项目结构

```
dubbo-example/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── io/github/xiaomisum/ryze/dubbo/example/
│   │           ├── DemoService.java       # Dubbo 服务接口
│   │           ├── DemoServiceImpl.java   # Dubbo 服务实现
│   │           └── DubboApplication.java  # Dubbo 应用主类
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/ryze/dubbo/example/
│       │       ├── code/                  # Java代码测试用例
│       │       │   ├── CodeTestCase.java      # Java代码测试类
│       │       │   └── GroovyCodeTestCase.groovy  # Groovy代码测试类
│       │       └── yaml/                  # YAML配置测试用例
│       │           └── YamlTestCase.java       # YAML测试类
│       └── resources/
│           ├── 测试用例/                 # 测试用例配置
│           ├── 取样器/                   # Dubbo 取样器模板
│           ├── 处理器/                   # 前置/后置处理器模板
│           └── 配置元件/                 # Dubbo 配置元件模板
├── images/                              # 文档图片
│   └── dubbo_service.png                # Dubbo 服务端截图
└── pom.xml                              # Maven 配置文件
```