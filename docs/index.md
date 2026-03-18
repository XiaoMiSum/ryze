---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

title: RYZE - 多协议测试框架
titleTemplate: 强大而优雅的测试解决方案

hero:
  name: "RYZE"
  text: "多协议测试框架"
  tagline: 🚀 强大的多协议测试框架，让测试变得简单而优雅
  actions:
    - theme: brand
      text: 快速开始
      link: /guide/quick-start
    - theme: alt
      text: 了解框架
      link: /guide/introduction

features:
  - icon: 🎯
    title: 测试用例与代码分离
    details: 采用 JSON/YAML 描述测试场景，保障测试用例的统一性和可维护性
  - icon: 🔧
    title: 多协议支持
    details: 原生支持 HTTP(S)、Dubbo、JDBC、Redis、MongoDB、Kafka、ActiveMQ、RabbitMQ 等多种协议测试
  - icon: ⚡
    title: 丰富的测试组件
    details: 内置前置/后置处理器、断言验证器、数据提取器等，轻松实现复杂测试场景
  - icon: 🎨
    title: 灵活的校验机制
    details: 提供丰富的验证机制和动态参数支持，满足各种测试需求
  - icon: 🚀
    title: 极强的可扩展性
    details: 基于 SPI 机制，支持自定义协议、处理器、断言规则等组件
  - icon: 📊
    title: 美观的测试报告
    details: 集成 Allure，生成简洁美观的测试报告
  - icon: 🎪
    title: 函数式编程支持
    details: 提供 MagicBox 魔法盒子，支持 Groovy 闭包和 Java 函数式接口
  - icon: 🌐
    title: 模板引擎支持
    details: 基于 FreeMarker 模板引擎，支持动态参数和变量替换
---

# Ryze - 多协议测试框架

Ryze 是一个基于 Java 21 开发的现代化测试框架，专为多协议测试而设计。它通过 JSON
描述测试场景，实现了测试用例与代码的完全分离，提供了统一的测试执行方式和丰富的扩展能力。

## 快速开始

### 环境要求

- JDK 21 或更高版本
- Maven 3.8+ 或 Gradle 7+

### Maven 依赖

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze</artifactId>
    <version>${version}</version>
</dependency>
```

### 第一个测试用例

```json
{
  "title": "用户API测试套件",
  "variables": {
    "host": "jsonplaceholder.typicode.com",
    "userId": "1"
  },
  "children": [
    {
      "testclass": "http",
      "title": "获取用户信息",
      "config": {
        "method": "GET",
        "base_url": "https://${host}",
        "api": "/users/${userId}",
        "headers": {
          "Accept": "application/json"
        }
      },
      "validators": [
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

- [核心概念](/guide/concepts/test-suite) - 测试集合、取样器、处理器等核心组件
- [配置语法](/guide/configuration/structure) - JSON/YAML 配置文件结构和语法
- [协议支持](/guide/protocols/http) - 各协议模块的详细使用说明
- [高级功能](/guide/advanced/variables-and-functions) - 动态变量、配置元件、拦截器等高级特性

### 👨‍💻 开发者文档

- [开发者指南](/developer) - 架构设计、编码规范和扩展机制
- [API 参考](/developer/api) - 核心 API 和组件接口文档

### 📋 测试工程师文档

- [测试套件](/tester/test-suite/test-suite-project) - 测试套件使用指南
- [协议指南](/tester/protocols/http) - 协议配置指南

## 📞 获取帮助

- [常见问题](/faq) - 常见问题解答和故障排除
- [GitHub Issues](https://github.com/XiaoMiSum/ryze/issues) - 问题反馈和功能建议

---

**📢 提示**: Ryze 文档持续更新中，欢迎贡献内容和反馈问题！