---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

title: Ryze - 多协议测试框架
titleTemplate: 强大而优雅的测试解决方案

hero:
  name: "Ryze"
  text: "多协议测试框架"
  tagline: 强大而优雅的测试解决方案，让测试变得简单
  image:
    src: /logo.png
    alt: Ryze Logo
  actions:
    - theme: brand
      text: 快速开始
      link: /guide/quick-start
    - theme: alt
      text: 了解框架
      link: /guide/introduction
    - theme: alt
      text: 查看示例
      link: /examples/

features:
  - icon: 🚀
    title: 多协议支持
    details: 支持 HTTP(S)、Dubbo、JDBC、Redis、MongoDB、Kafka、ActiveMQ、RabbitMQ 等多种协议测试
  - icon: 🧩
    title: 模块化架构
    details: 采用模块化设计，核心模块提供基础能力，协议模块提供扩展支持
  - icon: 📝
    title: 配置分离
    details: 测试用例与代码分离，通过 JSON/YAML 描述测试场景，提升可维护性
  - icon: 🔧
    title: 灵活验证
    details: 提供丰富的验证机制和动态参数支持，满足各种测试需求
  - icon: 📊
    title: 详细报告
    details: 集成 Allure 和 ExtentReports，生成简洁美观的测试报告
  - icon: 🔌
    title: 易于扩展
    details: 支持自定义插件和组件，满足个性化需求
---

# Ryze - 多协议测试框架

Ryze 是一个基于 Java 的测试框架，旨在支持多种协议的测试，提供统一的测试用例描述方式和丰富的测试功能，提升测试的可维护性和扩展性。

## 核心特性

### 🌐 多协议支持

Ryze 支持多种协议测试，包括：

- **Web 测试**：HTTP/HTTPS 协议
- **微服务测试**：Dubbo RPC 框架
- **数据库测试**：JDBC 关系型数据库
- **缓存测试**：Redis 内存数据库
- **消息队列测试**：Kafka、RabbitMQ、ActiveMQ
- **文档数据库测试**：MongoDB

### 🏗️ 模块化架构

Ryze 采用模块化架构设计：

- **核心模块**：提供基础组件、断言、执行引擎
- **协议模块**：各协议独立模块，便于维护和扩展
- **集成模块**：TestNG 集成支持

### 📋 配置驱动

通过 JSON/YAML 描述测试场景：

- 测试用例与代码分离，提升可维护性
- 统一的测试流程，降低学习成本
- 支持动态参数和变量替换

### 🧪 灵活验证

提供丰富的验证机制：

- 多种断言规则和比较方式
- 动态参数支持和数据提取
- 可扩展的验证器和提取器

## 快速开始

### 环境要求

- JDK 21 或更高版本
- Maven 3.8+ 或 Gradle 7+

### Maven 依赖

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze</artifactId>
    <version>6.0.1</version>
</dependency>
```

### 第一个测试用例

```json
{
  "title": "用户API测试套件",
  "variables": {
    "baseUrl": "https://jsonplaceholder.typicode.com",
    "userId": "1"
  },
  "children": [
    {
      "testclass": "http",
      "title": "获取用户信息",
      "method": "GET",
      "url": "${baseUrl}/users/${userId}",
      "headers": {
        "Accept": "application/json"
      },
      "assertions": [
        {
          "testclass": "json",
          "field": "$.id",
          "expected": 1,
          "rule": "=="
        }
      ]
    }
  ]
}
```

## 文档导航

### 📚 用户文档

- [入门指南](/guide/introduction) - 框架介绍和核心概念
- [快速开始](/guide/quick-start) - 5分钟快速上手教程
- [安装配置](/guide/installation) - 环境搭建和配置说明

### 🔧 技术文档

- [核心概念](/guide/concepts/) - 测试集合、取样器、处理器等核心组件
- [配置语法](/guide/configuration/) - JSON/YAML 配置文件结构和语法
- [协议支持](/guide/protocols/) - 各协议模块的详细使用说明
- [高级功能](/guide/advanced/) - 动态变量、配置元件、拦截器等高级特性

### 👨‍💻 开发者文档

- [开发者指南](/developer/) - 架构设计、编码规范和扩展机制
- [API 参考](/developer/api/) - 核心 API 和组件接口文档

### 📋 使用示例

- [示例代码](/examples/) - 各协议的完整使用示例
- [模板文档](/tester/template/) - 测试配置模板和最佳实践

## 📞 获取帮助

- [常见问题](/faq/) - 常见问题解答和故障排除
- [GitHub Issues](https://github.com/XiaoMiSum/ryze/issues) - 问题反馈和功能建议

---

**📢 提示**: Ryze 文档持续更新中，欢迎贡献内容和反馈问题！