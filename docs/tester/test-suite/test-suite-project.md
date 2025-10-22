# 测试集合（项目级）

项目级测试集合是整个测试项目的根集合，用于组织和管理所有模块级测试集合，形成完整的测试体系结构。

## 配置项

```yaml
title: 测试用例集合
configelements:
  - testclass: HttpDefaults  # 该用例集合下所有http取样器将使用该配置
    config: # 可简化填写，无需config关键字，直接将配置内容至于上层
      protocol: http
      host: localhost
      method: post
      path: /api/login
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
children: # 测试集合 或 测试用例列表，为了方便查看，可通过 @F(filepath) 将外部文件引入
  - !include '测试用例/测试集合（模块）.yaml'
  - !include '测试用例/测试集合（模块）.yaml'
```

## 参数说明

| 参数             | 必填 | 说明                        |
|----------------|----|---------------------------|
| title          | 是  | 测试集合的标题                   |
| configelements | 否  | 配置元件列表，为整个项目中的所有取样器提供默认配置 |
| preprocessors  | 否  | 前置处理器列表，在执行项目测试之前运行       |
| postprocessors | 否  | 后置处理器列表，在执行项目测试之后运行       |
| children       | 是  | 模块级测试集合列表                 |

## 使用示例

```json
{
  "title": "电商平台测试项目",
  "configelements": [
    {
      "testclass": "http",
      "config": {
        "protocol": "https",
        "host": "api.example.com",
        "headers": {
          "Content-Type": "application/json",
          "User-Agent": "Ryze-Test-Framework/1.0"
        }
      }
    }
  ],
  "preprocessors": [
    {
      "testclass": "jdbc",
      "config": {
        "url": "jdbc:mysql://localhost:3306/testdb",
        "username": "testuser",
        "password": "testpass"
      },
      "sql": "TRUNCATE TABLE test_data;"
    }
  ],
  "children": [
    "user_management/module.json",
    "order_management/module.json",
    "payment/module.json",
    "inventory/module.json"
  ],
  "postprocessors": [
    {
      "testclass": "jdbc",
      "config": {
        "url": "jdbc:mysql://localhost:3306/testdb",
        "username": "testuser",
        "password": "testpass"
      },
      "sql": "TRUNCATE TABLE test_data;"
    }
  ]
}
```

在上述示例中，我们定义了一个电商平台的项目级测试集合，它包含了用户管理、订单管理、支付和库存四个模块级测试集合。项目级测试集合通常包含：

- 全局共享的配置元件（如基础URL、通用请求头等）
- 项目级别的前置处理器（如测试数据初始化）
- 项目级别的后置处理器（如测试数据清理）
- 所有模块级测试集合的引用

通过这种层次化的组织方式，可以更好地管理大型项目的测试用例，提高测试的可维护性和可扩展性。