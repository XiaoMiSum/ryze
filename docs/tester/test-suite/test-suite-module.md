# 测试集合（模块级）

模块级测试集合用于组织和管理属于同一功能模块的多个测试用例或子测试集合。

## 配置项

```yaml
title: 测试用例集合
configelements:
  - testclass: http  # 该用例集合下所有http取样器将使用该配置
    config: # 可简化填写，无需config关键字，直接将配置内容至于上层
      protocol: http
      host: localhost
      method: post
      path: /api/login
preprocessors: # 前置处理器
  - testclass: jdbc
    config: # 可简化填写，无需config关键字，直接将配置内容至于上
      datasource: JDBCDataSource_var
      query_type: select
      sql: 'select * from sys_user;'
    extractors:
      - { testclass: json, field: '$.user_name', variable_name: user_name }
      - { testclass: result, variable_name: result }
      - { testclass: regex, field: '"id":"([0-9]+)","create_', variable_name: r_total, match_num: 0 }
postprocessors: # 后置处理器
  - testclass: jdbc
    config: # 可简化填写，无需config关键字，直接将配置内容至于上层
      datasource: JDBCDataSource_var
      query_type: select
      sql: 'select * from sys_user;'
children: # 测试集合 或 测试用例列表，为了方便查看，可通过 @F(filepath) 将外部文件引入
  - '测试用例/测试用例.yaml'
  - '测试用例/测试用例.yaml'
```

## 参数说明

| 参数 | 必填 | 说明 |
|------|------|------|
| title | 是 | 测试集合的标题 |
| configelements | 否 | 配置元件列表，为该测试集合中的所有取样器提供默认配置 |
| preprocessors | 否 | 前置处理器列表，在执行子测试之前运行 |
| postprocessors | 否 | 后置处理器列表，在执行子测试之后运行 |
| children | 是 | 子测试集合或测试用例列表 |

## 使用示例

```json
{
  "title": "用户管理模块测试",
  "configelements": [
    {
      "testclass": "http",
      "config": {
        "protocol": "https",
        "host": "api.example.com",
        "headers": {
          "Content-Type": "application/json"
        }
      }
    }
  ],
  "preprocessors": [
    {
      "testclass": "log",
      "message": "开始执行用户管理模块测试"
    }
  ],
  "children": [
    "user/create_user.json",
    "user/get_user.json",
    "user/update_user.json",
    "user/delete_user.json"
  ],
  "postprocessors": [
    {
      "testclass": "log",
      "message": "用户管理模块测试执行完成"
    }
  ]
}
```

在上述示例中，我们定义了一个用户管理模块的测试集合，它包含了创建用户、获取用户、更新用户和删除用户四个测试用例。通过引用外部文件的方式组织测试用例，使测试结构更加清晰。

模块级测试集合可以包含：
- 共享的配置元件（如HTTP配置、数据库连接等）
- 模块级别的前置和后置处理器
- 多个子测试用例或子测试集合