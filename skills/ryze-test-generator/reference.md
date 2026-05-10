# Ryze YAML配置项参考

## 测试套件结构

### 项目级测试集合

```yaml
title: 项目名称
variables:
  environment: test
  base_url: https://api.example.com
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
      headers:
        Content-Type: application/json
children:
  - !include modules/user-management.yaml
  - !include modules/order-management.yaml
```

### 模块级测试集合

```yaml
title: 模块名称
variables:
  module_path: /api/v1/users
configelements:
  - testclass: http
    config:
      path: ${module_path}
children:
  - !include use-cases/create-user.yaml
  - !include use-cases/get-user.yaml
```

### 测试用例

```yaml
title: 测试用例名称
variables:
  user_id: 1
configelements:
  - testclass: http
    config:
      base_url: http://localhost:8080
preprocessors:
  - testclass: http
    config:
      method: post
      path: /auth/login
      body:
        username: admin
        password: admin123
    extractors:
      - { testclass: json, field: '$.data.token', ref_name: auth_token }
postprocessors:
  - testclass: http
    config:
      method: delete
      path: /cleanup
children:
  - title: 步骤1
    testclass: http_sampler
    config:
      method: get
      path: /users/${user_id}
      headers:
        Authorization: Bearer ${auth_token}
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.id', expected: '${user_id}', rule: == }
```

## HTTP配置项

### 配置元件 (testclass: http)

| 配置项 | 类型 | 必需 | 描述 | 示例 |
|--------|------|------|------|------|
| method | String | 否 | HTTP方法 | GET, POST, PUT, DELETE |
| base_url | String | 是 | 基础URL | http://localhost:8080 |
| path | String | 否 | 接口路径 | /api/users |
| http/2 | Boolean | 否 | 是否HTTP2 | false |
| headers | Map | 否 | 请求头 | {Content-Type: application/json} |
| query | Map | 否 | URL参数 | {page: 1, size: 10} |
| data | Map | 否 | 表单数据 | {key: value} |
| body | Object | 否 | 请求体 | {name: "test"} |

### 取样器 (testclass: http_sampler)

继承配置元件的所有配置项，优先级更高。

## 验证器 (Validators)

### HTTP断言 (testclass: http_assertion)

| 字段 | 类型 | 描述 | 示例 |
|------|------|------|------|
| field | String | 验证字段 | status, statusCode |
| expected | Any | 期望值 | 200 |
| rule | String | 验证规则 | ==, !=, >, <, contains |

```yaml
validators:
  - { testclass: http_assertion, field: status, expected: 200, rule: == }
```

### JSON断言 (testclass: json_assertion)

| 字段 | 类型 | 描述 | 示例 |
|------|------|------|------|
| field | String | JSONPath表达式 | $.data.id |
| expected | Any | 期望值 | 123, "success" |
| rule | String | 验证规则 | ==, !=, isNotEmpty, contains |

```yaml
validators:
  - { testclass: json_assertion, field: '$.data.id', expected: 123, rule: == }
  - { testclass: json_assertion, field: '$.data.name', expected: '', rule: isNotEmpty }
  - { testclass: json_assertion, field: '$.message', expected: 'success', rule: contains }
```

### 结果断言 (testclass: result_assertion)

```yaml
validators:
  - { testclass: result_assertion, expected: 'expected_value' }
```

## 提取器 (Extractors)

### JSON提取器 (testclass: json)

| 字段 | 类型 | 描述 | 示例 |
|------|------|------|------|
| field | String | JSONPath表达式 | $.data.token |
| ref_name | String | 变量名 | auth_token |

```yaml
extractors:
  - { testclass: json, field: '$.data.token', ref_name: auth_token }
  - { testclass: json, field: '$.data.user_id', ref_name: user_id }
```

### 正则提取器 (testclass: regex)

| 字段 | 类型 | 描述 | 示例 |
|------|------|------|------|
| field | String | 正则表达式 | "id":"([0-9]+)" |
| ref_name | String | 变量名 | extracted_id |
| match_num | Integer | 匹配组号 | 0, 1, 2 |

```yaml
extractors:
  - { testclass: regex, field: '"id":"([0-9]+)"', ref_name: extracted_id, match_num: 1 }
```

### 结果提取器 (testclass: result)

```yaml
extractors:
  - { testclass: result, ref_name: response_result }
```

## 变量引用

### 基本引用

```yaml
variables:
  user_id: 123
  username: test_user

config:
  path: /users/${user_id}
  body:
    name: ${username}
```

### 嵌套对象引用

```yaml
variables:
  user_data:
    id: 123
    name: test_user
    email: test@example.com

config:
  body: ${user_data}
```

### 函数调用

```yaml
variables:
  timestamp: ${timestamp('yyyyMMddHHmmss')}
  uuid: ${uuid()}
  random_value: ${random()}
  fake_name: ${faker('name.fullName')}
  env_value: ${Env('API_KEY', 'default_key')}
```

## 文件引用

使用 `!include` 引用外部文件：

```yaml
configelements:
  - !include '配置元件/http_defaults.yaml'
preprocessors:
  - !include '处理器/login_preprocessor.yaml'
children:
  - !include '测试用例/create-user.yaml'
  - !include '测试用例/get-user.yaml'
```

## 拦截器 (Interceptors)

```yaml
interceptors:
  - ExampleInterceptor
  - CustomLoggingInterceptor
```

## 常用规则 (Rules)

| 规则 | 说明 | 示例 |
|------|------|------|
| == | 等于 | expected: 200, rule: == |
| != | 不等于 | expected: 400, rule: != |
| > | 大于 | expected: 0, rule: > |
| < | 小于 | expected: 100, rule: < |
| >= | 大于等于 | expected: 1, rule: >= |
| <= | 小于等于 | expected: 10, rule: <= |
| contains | 包含 | expected: 'success', rule: contains |
| isNotEmpty | 非空 | expected: '', rule: isNotEmpty |
| isEmpty | 为空 | expected: '', rule: isEmpty |
| matches | 正则匹配 | expected: '^[0-9]+$', rule: matches |
