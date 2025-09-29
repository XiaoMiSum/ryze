# 🍃 MongoDB 示例指南

## 📋 简介

本示例演示了如何使用 Ryze 框架进行 MongoDB 数据库测试。示例包含 Docker 环境搭建和对应的 Ryze 测试用例。

## 🚀 环境准备

### Docker MongoDB 环境搭建

1. 拉取 MongoDB 镜像
   ```bash
   docker pull mongo
   ```

2. 启动 MongoDB 容器
   ```bash
   docker run -d --name same-mongo -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=123456 mongo
   ```

3. 使用数据库客户端连接 MongoDB
   ```
   host: localhost
   port: 27017
   authenticationDatabase: admin
   username: root
   password: 123456
   ```

4. 创建测试数据库
   ```javascript
   use demo
   ```

5. 创建测试集合
   ```javascript
   db.createCollection("book")
   ```

### 验证环境

通过以下命令验证 MongoDB 服务是否正常运行：

```bash
docker ps | grep mongo
```

## 🧪 执行 Ryze Mongo 测试

### 运行测试

1. 在 IDE 中运行 `mongo-example` 模块中的测试类
2. 或通过命令行运行：
   ```bash
   cd example/mongo-example
   mvn test
   ```

### 测试内容

测试用例包含以下场景：

- MongoDB 连接测试
- 文档插入测试
- 文档查询测试
- 文档更新测试
- 文档删除测试
- 结果断言验证

## 📊 预期结果

### 测试报告

测试执行完成后，会在 `target/allure-results` 目录生成 Allure 测试报告数据，可通过以下命令查看报告：

```bash
allure serve target/allure-results
```

## 📁 项目结构

```
mongo-example/
├── src/
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/example/mongo/
│       │       └── MongoExampleTest.java  # Ryze 测试类
│       └── resources/
│           ├── 测试集合/                  # 测试集合配置
│           ├── 取样器/                    # MongoDB 取样器模板
│           ├── 处理器/                    # 前置/后置处理器模板
│           └── 配置元件/                  # MongoDB 配置元件模板
└── pom.xml                               # Maven 配置文件
```