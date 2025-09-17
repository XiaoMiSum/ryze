# 结果验证器

结果验证器用于验证整个测试步骤的执行结果是否符合预期。

## 配置项

```yaml
testclass: result # result 验证器，用于验证整个取样结果是否符合预取
expected: 200  # 期望值
rule: ==   # 验证规则
strict: false # 是否严格验证，默认否：忽略大小写验证
```

## 参数说明

| 参数        | 必填 | 说明                        |
|-----------|----|---------------------------|
| testclass | 是  | 验证器类型，固定值为 `result`       |
| expected  | 是  | 期望值                       |
| rule      | 是  | 验证规则，支持多种比较操作             |
| strict    | 否  | 是否严格验证，默认为 `false`（忽略大小写） |

## 使用示例

### 验证HTTP请求成功

```json
{
  "testclass": "http",
  "title": "用户登录",
  "config": {
    "method": "POST",
    "url": "https://api.example.com/login",
    "body": {
      "username": "testuser",
      "password": "testpass"
    }
  },
  "assertions": [
    {
      "testclass": "result",
      "expected": true,
      "rule": "=="
    }
  ]
}
```

### 验证数据库操作影响行数

```json
{
  "testclass": "jdbc",
  "title": "更新用户信息",
  "config": {
    "url": "jdbc:mysql://localhost:3306/testdb",
    "username": "testuser",
    "password": "testpass",
    "sql": "UPDATE users SET name = 'updated_user' WHERE id = 123"
  },
  "assertions": [
    {
      "testclass": "result",
      "expected": 1,
      "rule": "=="
    }
  ]
}
```

### 验证消息队列发送成功

```json
{
  "testclass": "kafka",
  "title": "发送订单创建消息",
  "config": {
    "bootstrap_servers": "localhost:9092",
    "topic": "order-events",
    "key": "order-123",
    "value": "{\"orderId\": 123, \"status\": \"created\"}"
  },
  "assertions": [
    {
      "testclass": "result",
      "expected": true,
      "rule": "=="
    }
  ]
}
```

## 支持的验证规则

结果验证器支持以下验证规则：

| 规则             | 说明     | 示例                                                  |
|----------------|--------|-----------------------------------------------------|
| `==`           | 相等     | `expected: 200, rule: "=="`                         |
| `eq_any`       | 任意一个相等 | `expected: [200, 300], rule: "eq_any"`              |
| `!=`           | 不相等    | `expected: 404, rule: "!="`                         |
| `>`            | 大于     | `expected: 0, rule: ">"`                            |
| `<`            | 小于     | `expected: 10000, rule: "<"`                        |
| `>=`           | 大于等于   | `expected: 0, rule: ">="`                           |
| `<=`           | 小于等于   | `expected: 100, rule: "<="`                         |
| `contains`     | 包含     | `expected: "success", rule: "contains"`             |
| `any_contains` | 包含任意一个 | `expected: [success, false], rule: "any_contains"`  |
| `not_contains` | 不包含    | `expected: "error", rule: "not_contains"`           |
| `regex`        | 正则匹配   | `expected: "^\\d{4}-\\d{2}-\\d{2}$", rule: "regex"` |
| `isNotEmpty`   | 非空     | `rule: "isNotEmpty"`                                |
| `isEmpty`      | 为空     | ` rule: "isEmpty"`                                  |
| `same_object`  | 对象匹配   | `expected: {}, rule: "same_object"`                 |

## 验证内容说明

结果验证器验证的内容根据不同的测试类型而有所不同：

### HTTP测试

验证HTTP请求是否成功执行，返回布尔值：

- `true`：请求成功发送并收到响应
- `false`：请求失败（如网络错误、超时等）

### 数据库测试

验证SQL执行影响的行数：

- 对于SELECT语句，返回查询结果集的大小
- 对于INSERT/UPDATE/DELETE语句，返回受影响的行数

### 消息队列测试

验证消息是否成功发送：

- `true`：消息成功发送到队列
- `false`：消息发送失败

### Redis测试

验证操作是否成功执行：

- 对于读取操作，返回获取到的值或null
- 对于写入操作，返回操作结果（如"OK"）

通过结果验证器，可以确保整个测试步骤按预期执行，而不仅仅是验证响应内容的特定部分。