# 📝 测试用例模板

## 🔍 基本概念

测试用例是执行具体测试逻辑的基本单元，是测试集合体系中的叶子节点。每个测试用例包含具体的测试步骤（取样器）、前置/后置处理器等组件。

### 特点

- **独立执行**：可以单独运行，也可以作为上级集合的一部分运行
- **继承特性**：继承父集合的变量、配置元件、处理器等
- **完整执行**：包含从准备→执行→验证→清理的完整生命周期
- **明确目标**：每个用例都有明确的测试目标和验证点

## 📋 配置项详解

```yaml
title: 测试用例 # 包含配置原件、前置处理器、后置处理器、多个取样器（执行步骤）
configelements:
  - testclass: http  # 该用例下所有http取样器将使用该配置
    config: # 可简化填写，无需config关键字，直接将配置内容至于上层
      protocol: http
      host: localhost
preprocessors: # 前置处理器
  - testclass: jdbc
    config: # 可简化填写，无需config关键字，直接将配置内容至于上层
      datasource: JDBCDataSource_var
      sql: 'select * from sys_user;'
    extractors:
      - { testclass: json, field: '$.user_name', ref_name: user_name }
      - { testclass: result, ref_name: result }
      - { testclass: regex, field: '"id":"([0-9]+)","create_', ref_name: r_total, match_num: 0 }
postprocessors: # 后置处理器
  - testclass: jdbc
    config: # 可简化填写，无需config关键字，直接将配置内容至于上层
      datasource: JDBCDataSource_var
      sql: 'select * from sys_user;'
children: # 执行步骤
  - title: 步骤1
    testclass: http
    variables: # 变量
      username: ryze
    config:
      method: post
      path: /api/login
      body: { userName: '${username}', password: '123456qq', sign: '__digest(${username}123456qq)' }
    extractors: ## 提取器
      - { testclass: json, field: '$.status', ref_name: status }
      - { testclass: json, field: '$.data', ref_name: message }
    validators: ## 验证器
      - { testclass: http, field: status, expected: 200, rule: == }
      - { testclass: json, field: '$.status', expected: 200, rule: '==' }
  - title: 步骤2
    testclass: http
    variables: # 变量
      username: ryze
    config:
      method: post
      path: /api/login
      body: { userName: '${username}', password: '123456qq', sign: '__digest(${username}123456qq)' }
    extractors: ## 提取器
      - { testclass: json, field: '$.status', ref_name: status }
      - { testclass: json, field: '$.data', ref_name: message }
    validators: ## 验证器
      - { testclass: http, field: status, expected: 200, rule: == }
      - { testclass: json, field: '$.status', expected: 200, rule: '==' }
```

## 参数说明

| 参数             | 类型     | 必填 | 描述                  |
|----------------|--------|----|---------------------|
| title          | String | 是  | 测试用例的标题             |
| variables      | Object | 否  | 用例管理的变量，支持嵌套对象和动态函数 |
| configelements | Array  | 否  | 配置元件列表（数据库、HTTP配置等） |
| preprocessors  | Array  | 否  | 前置处理器列表，在执行测试步骤之前运行 |
| postprocessors | Array  | 否  | 后置处理器列表，在执行测试步骤之后运行 |
| children       | Array  | 是  | 测试步骤列表，包含具体的取样器     |

### 子项配置：取样器 (child)

取样器是测试用例中执行具体测试逻辑的最小子单元。更详细的取样器配置流程请参考 [📋 取样器配置详解](./sampler) 文档。

| 参数         | 类型     | 必填 | 描述                        |
|------------|--------|----|---------------------------|
| title      | String | 是  | 该测试步骤的描述                  |
| testclass  | String | 是  | 取样器类型 (http/jdbc/dubbo 等) |
| variables  | Object | 否  | 该步骤特定的变量                  |
| config     | Object | 是  | 取样器配置                     |
| extractors | Array  | 否  | 提取器，从响应中提取数据              |
| validators | Array  | 否  | 验证器，验证响应结果                |

> **配置继承**：测试用例的配置会扩展父集合的配置，可以覆盖或新增配置

## 📚 最佳实践

### 用例设计原则

- **单一职责**：每个用例专注于一个测试场景
- **独立完整**：用例包含完整的准备、执行、验证、清理流程
- **清晰命名**：title 应该清晰描述测试目标
- **结果验证**：必须包含 validators 进行结果验证

### 步骤组织建议

```
children:
  # 步骤1：准备阶段（如果不用前置处理器）
  - title: 创建测试数据
    testclass: http
    # ...

  # 步骤2：执行阶段
  - title: 执行业务操作
    testclass: http
    # ...

  # 步骤3：验证阶段
  - title: 验证结果
    testclass: jdbc
    # ...

  # 步骤4：清理阶段（如果不用后置处理器）
  - title: 清理测试数据
    testclass: http
    # ...
```

### 变量管理建议

- **全局变量**：在项目或模块级定义
- **模块变量**：在模块级集合定义
- **用例变量**：在用例级定义，用于该用例及其子步骤
- **步骤变量**：在特定步骤中定义，优先级最高

## 使用示例

```
title: 用户登录并获取信息
configelements:
  - testclass: http
    config:
      protocol: https
      host: api.example.com
children:
  - title: 用户登录
    testclass: http
    config:
      method: POST
      path: /login
      body:
        username: testuser
        password: testpass
    extractors:
      - testclass: json
        field: $.data.token
        ref_name: auth_token
    validators:
      - testclass: http
        field: status
        expected: 200
        rule: '=='
  - title: 获取用户信息
    testclass: http
    config:
      method: GET
      path: /user/profile
      headers:
        Authorization: Bearer ${auth_token}
    validators:
      - testclass: http
        field: status
        expected: 200
        rule: '=='
```

在上述示例中，我们定义了一个包含两个步骤的测试用例：

1. 用户登录并提取认证令牌
2. 使用认证令牌获取用户信息

测试用例还包含了配置元件（为HTTP取样器提供默认配置）和前后置处理器（用于日志记录）。

---

## 📄 相关文档

- [📊 测试集合（模块级）](./test-suite-module) - 学习模块级测试集合的定义
- [🌋 测试集合（项目级）](./test-suite-project) - 学习项目级测试集合的定义
- [📈 测试集合体系概念](../concepts/test-suite) - 深入了解测试集合体系架构

> 📌 **提示**：测试用例是测试集合体系中的最小执行单元，设计良好的用例结构是测试成功的基础！