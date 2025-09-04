# 🗃️ Redis 示例指南

## 📋 简介

本示例演示了如何使用 Ryze 框架进行 Redis 数据库测试。示例包含 Docker 环境搭建和对应的 Ryze 测试用例。

## 🚀 环境准备

### Docker Redis 环境搭建

1. 拉取 Redis 镜像
   ```bash
   docker pull redis
   ```

2. 启动 Redis 容器，将容器 6379 端口映射到宿主机 6379 端口
   ```bash
   docker run -d -p 6379:6379 --name redis redis:latest
   ```

3. 在 Redis 容器中打开一个 shell
   ```bash
   docker exec -it redis sh
   ```

4. 进入 Redis 客户端控制台
   ```bash
   redis-cli
   ```

5. 创建 key 并设置值
   ```
   set test redis_docker
   ```

6. 查看 key 值
   ```
   get test
   ```

### 验证环境

通过以下命令验证 Redis 服务是否正常运行：

```bash
docker ps | grep redis
```

您应该能看到类似以下的输出：
![Redis 控制台](images/redis.png)

## 🧪 执行 Ryze Redis 测试

### 运行测试

1. 在 IDE 中运行 `redis-example` 模块中的测试类
2. 或通过命令行运行：
   ```bash
   cd example/redis-example
   mvn test
   ```

### 测试内容

测试用例包含以下场景：

- Redis 连接测试
- Key-Value 操作测试
- 字符串操作测试
- 数据提取测试
- 结果断言验证

## 📊 预期结果

测试成功执行后，您应该看到类似以下的输出：

```
测试执行状态: SUCCESS
测试时长: 45ms
子测试数量: 2
```

### 执行结果验证

测试执行完成后，可以通过 Redis CLI 查看 keys：

```bash
redis-cli keys *
```

您应该能看到类似以下的输出：
![Redis CLI Keys](images/redis_cli_keys.png)

### 测试报告

测试执行完成后，会在 `target/allure-results` 目录生成 Allure 测试报告数据，可通过以下命令查看报告：

```bash
allure serve target/allure-results
```

## 📁 项目结构

```
redis-example/
├── src/
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/example/redis/
│       │       └── RedisExampleTest.java  # Ryze 测试类
│       └── resources/
│           ├── 测试集合/                  # 测试集合配置
│           ├── 取样器/                    # Redis 取样器模板
│           ├── 处理器/                    # 前置/后置处理器模板
│           └── 配置元件/                  # Redis 配置元件模板
├── images/                               # 文档图片
│   ├── redis.png                         # Redis 控制台截图
│   └── redis_cli_keys.png                # Redis CLI Keys 截图
└── pom.xml                               # Maven 配置文件
```