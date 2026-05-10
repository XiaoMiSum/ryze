# 生成规则参考

## 接口方法映射

| HTTP方法 | 测试策略 | 典型断言 |
|---------|---------|---------|
| GET | 查询测试 | 状态码200 + 数据结构验证 |
| POST | 创建测试 | 状态码201/200 + 响应数据验证 + 提取ID |
| PUT | 更新测试 | 状态码200 + 完整数据验证 |
| DELETE | 删除测试 | 状态码204/200 + 验证删除结果 |

## 参数处理规则

### Path参数

直接替换为变量：
```yaml
path: /users/${user_id}
```

### Query参数

使用query字段：
```yaml
config:
  query:
    page: 1
    size: 10
```

### Body参数

根据schema生成示例数据：
```yaml
config:
  body:
    name: "test_user"
    email: "test@example.com"
    age: 25
```

### Header参数

添加到headers：
```yaml
config:
  headers:
    Authorization: Bearer ${auth_token}
    X-Request-ID: ${uuid()}
```

## 认证处理

### Bearer Token

```yaml
config:
  headers:
    Authorization: Bearer ${auth_token}
```

### API Key

```yaml
config:
  headers:
    X-API-Key: ${api_key}
```

### Basic Auth

```yaml
config:
  headers:
    Authorization: Basic ${base64_credentials}
```

### Cookie

```yaml
config:
  headers:
    Cookie: session_id=${session_id}
```

## 特殊场景处理

### 分页接口

```yaml
variables:
  page: 1
  size: 10
config:
  query:
    page: ${page}
    size: ${size}
validators:
  - { testclass: json_assertion, field: '$.data.items', expected: '', rule: isNotEmpty }
  - { testclass: json_assertion, field: '$.data.total', expected: 0, rule: '>=' }
  - { testclass: json_assertion, field: '$.data.page', expected: '${page}', rule: == }
  - { testclass: json_assertion, field: '$.data.size', expected: '${size}', rule: == }
```

### 文件上传

```yaml
config:
  method: post
  path: /upload
  headers:
    Content-Type: multipart/form-data
  body:
    file: "@path/to/test-file.jpg"
    description: "测试文件"
validators:
  - { testclass: http_assertion, field: status, expected: 200, rule: == }
  - { testclass: json_assertion, field: '$.data.file_url', expected: '', rule: isNotEmpty }
```

### 批量操作

```yaml
config:
  body:
    - id: 1
      status: active
    - id: 2
      status: inactive
validators:
  - { testclass: http_assertion, field: status, expected: 200, rule: == }
  - { testclass: json_assertion, field: '$.data.success_count', expected: 2, rule: == }
```

### 导出接口

```yaml
config:
  method: get
  path: /export/users
  query:
    format: excel
validators:
  - { testclass: http_assertion, field: status, expected: 200, rule: == }
  - { testclass: http_assertion, field: content-type, expected: 'application/vnd.ms-excel', rule: contains }
```

### 异步任务

```yaml
# 提交任务
config:
  method: post
  path: /tasks/process
  body:
    data: ${test_data}
extractors:
  - { testclass: json, field: '$.data.task_id', ref_name: task_id }
validators:
  - { testclass: http_assertion, field: status, expected: 202, rule: == }

# 查询任务状态
config:
  method: get
  path: /tasks/${task_id}/status
validators:
  - { testclass: json_assertion, field: '$.data.status', expected: 'completed', rule: == }
```

## 变量引用规则

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

## 断言策略

### HTTP状态码断言

```yaml
validators:
  - { testclass: http_assertion, field: status, expected: 200, rule: == }
```

### JSONPath断言

```yaml
validators:
  - { testclass: json_assertion, field: '$.code', expected: 200, rule: == }
  - { testclass: json_assertion, field: '$.data.id', expected: 123, rule: == }
  - { testclass: json_assertion, field: '$.message', expected: 'success', rule: == }
```

### 非空验证

```yaml
validators:
  - { testclass: json_assertion, field: '$.data.token', expected: '', rule: isNotEmpty }
  - { testclass: json_assertion, field: '$.data.items', expected: '', rule: isNotEmpty }
```

### 范围验证

```yaml
validators:
  - { testclass: json_assertion, field: '$.data.age', expected: 0, rule: '>=' }
  - { testclass: json_assertion, field: '$.data.total', expected: 100, rule: '<=' }
  - { testclass: json_assertion, field: '$.data.status', expected: 'active,inactive', rule: contains }
```

### 正则匹配

```yaml
validators:
  - { testclass: json_assertion, field: '$.data.email', expected: '^[\\w.-]+@[\\w.-]+\\.\\w+$', rule: matches }
  - { testclass: json_assertion, field: '$.data.phone', expected: '^1[3-9]\\d{9}$', rule: matches }
```

## 提取器规则

### JSON提取

```yaml
extractors:
  - { testclass: json, field: '$.data.token', ref_name: auth_token }
  - { testclass: json, field: '$.data.user_id', ref_name: user_id }
  - { testclass: json, field: '$.data.items[*].id', ref_name: item_ids }
```

### 正则提取

```yaml
extractors:
  - { testclass: regex, field: '"id":"([0-9]+)"', ref_name: extracted_id, match_num: 1 }
```

### 结果提取

```yaml
extractors:
  - { testclass: result, ref_name: response_result }
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
