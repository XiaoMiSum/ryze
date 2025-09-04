# 🔧 变量

本文档详细介绍 Ryze 测试框架中变量定义和内置函数的使用方法。

## 🔗 变量定义

### 基本语法

在测试集合、测试用例、取样器中，可通过 `variables` 字段定义变量：

```yaml
variables:
  # 简单变量
  var1: value1
  # 对象变量
  var2:
    key1: 1
    key2: 2
  # 数组变量
  var3: [ "item1", "item2" ]
```

### 变量引用

**引用语法**：`${变量名}`

```yaml
testclass: http
variables:
  username: testuser
  config:
    host: 192.168.1.100
    port: 8080
config:
  body:
    name: ${username}          # 引用简单变量
    password: ${config.port}   # 引用对象变量的属性
```

### 作用域说明

变量具有继承特性，子级可以访问父级变量：

- **项目级变量**：在整个测试项目中有效
- **模块级变量**：在当前模块及其子模块中有效
- **用例级变量**：仅在当前测试用例中有效

**💡 提示**：变量和函数是 Ryze 框架中实现动态测试的核心功能，合理使用可以大大提高测试的灵活性和可维护性！