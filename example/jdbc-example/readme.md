# 🗄️ JDBC MySQL 示例指南

## 📋 简介

本示例演示了如何使用 Ryze 框架进行 JDBC MySQL 数据库测试。示例包含 Docker 环境搭建和对应的 Ryze 测试用例。

## 🚀 环境准备

### Docker MySQL 环境搭建

1. 拉取 MySQL 镜像
   ```bash
   docker pull mysql
   ```

2. 启动 MySQL 容器
   ```bash
   docker run -d --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql
   ```

3. 使用数据库客户端连接 MySQL
   ```
   host: localhost
   port: 3306
   username: root
   password: 123456
   ```

4. 创建测试数据库
   ```sql
   CREATE DATABASE `ryze-test`;
   USE `ryze-test`;
   ```

5. 创建测试表并插入数据
   ```sql
   -- 创建测试表
   DROP TABLE IF EXISTS `t_001`;
   CREATE TABLE `t_001` (
     `id` int NOT NULL AUTO_INCREMENT,
     `tick` varchar(255) DEFAULT NULL,
     `name` varchar(255) DEFAULT NULL,
     PRIMARY KEY (`id`)
   ) ENGINE=InnoDB AUTO_INCREMENT=1;
   
   -- 插入测试数据
   INSERT INTO `t_001` VALUES (1, 100, 'A1');
   ```

### 验证环境

通过以下命令验证 MySQL 服务是否正常运行：
```bash
docker ps | grep mysql
```

## 🧪 执行 Ryze MySQL 测试

### 运行测试

1. 在 IDE 中运行 `jdbc-example` 模块中的测试类
2. 或通过命令行运行：
   ```bash
   cd example/jdbc-example
   mvn test
   ```

### 注意事项

- 如果修改了数据库密码，请相应更新测试用例中的配置
- 确保 Docker MySQL 容器正在运行

### 测试内容

测试用例包含以下场景：
- 数据库连接测试
- SQL 查询测试
- SQL 更新测试
- 事务处理测试
- 结果断言验证

## 📊 预期结果

测试成功执行后，您应该看到类似以下的输出：

```
测试执行状态: SUCCESS
测试时长: 89ms
子测试数量: 3
```

### 测试报告

测试执行完成后，会在 `target/allure-results` 目录生成 Allure 测试报告数据，可通过以下命令查看报告：

```bash
allure serve target/allure-results
```

## 📁 项目结构

```
jdbc-example/
├── src/
│   └── test/
│       ├── java/
│       │   └── io/github/xiaomisum/example/jdbc/
│       │       └── JdbcExampleTest.java   # Ryze 测试类
│       └── resources/
│           ├── 测试集合/                  # 测试集合配置
│           ├── 取样器/                    # JDBC 取样器模板
│           ├── 处理器/                    # 前置/后置处理器模板
│           └── 配置元件/                  # JDBC 配置元件模板
└── pom.xml                               # Maven 配置文件
```

## 🤝 相关文档

- [JDBC 协议文档](../../docs/protocols/JDBC.md)
- [测试集合管理](../../docs/help/测试集合.md)
- [变量与函数](../../docs/help/变量与函数.md)