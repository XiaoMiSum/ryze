# 测试用例模板

测试用例是执行具体测试逻辑的基本单元，包含配置元件、前置处理器、后置处理器、多个取样器（执行步骤）等组件。

## 配置项

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
      query_type: select
      sql: 'select * from sys_user;'
    extractors:
      - { testclass: json, field: '$.user_name', ref_name: user_name }
      - { testclass: result, ref_name: result }
      - { testclass: regex, field: '"id":"([0-9]+)","create_', ref_name: r_total, match_num: 0 }
postprocessors: # 后置处理器
  - testclass: jdbc
    config: # 可简化填写，无需config关键字，直接将配置内容至于上层
      datasource: JDBCDataSource_var
      query_type: select
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

| 参数             | 必填 | 说明                       |
|----------------|----|--------------------------|
| title          | 是  | 测试用例的标题                  |
| configelements | 否  | 配置元件列表，为该测试用例中的取样器提供默认配置 |
| preprocessors  | 否  | 前置处理器列表，在执行测试步骤之前运行      |
| postprocessors | 否  | 后置处理器列表，在执行测试步骤之后运行      |
| children       | 是  | 测试步骤列表，包含具体的取样器          |

## 使用示例

```json
{
  "title": "用户登录并获取信息",
  "configelements": [
    {
      "testclass": "http",
      "config": {
        "protocol": "https",
        "host": "api.example.com"
      }
    }
  ],
  "preprocessors": [
    {
      "testclass": "log",
      "message": "开始执行用户登录测试"
    }
  ],
  "children": [
    {
      "title": "用户登录",
      "testclass": "http",
      "config": {
        "method": "POST",
        "path": "/login",
        "body": {
          "username": "testuser",
          "password": "testpass"
        }
      },
      "extractors": [
        {
          "testclass": "json",
          "field": "$.data.token",
          "ref_name": "auth_token"
        }
      ],
      "validators": [
        {
          "testclass": "http",
          "field": "status",
          "expected": 200,
          "rule": "=="
        }
      ]
    },
    {
      "title": "获取用户信息",
      "testclass": "http",
      "config": {
        "method": "GET",
        "path": "/user/profile",
        "headers": {
          "Authorization": "Bearer ${auth_token}"
        }
      },
      "validators": [
        {
          "testclass": "http",
          "field": "status",
          "expected": 200,
          "rule": "=="
        }
      ]
    }
  ],
  "postprocessors": [
    {
      "testclass": "log",
      "message": "用户登录测试执行完成"
    }
  ]
}
```

在上述示例中，我们定义了一个包含两个步骤的测试用例：

1. 用户登录并提取认证令牌
2. 使用认证令牌获取用户信息

测试用例还包含了配置元件（为HTTP取样器提供默认配置）和前后置处理器（用于日志记录）。