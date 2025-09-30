# HTTP 验证器

HTTP 验证器用于验证 HTTP 响应的特定部分，如状态码、响应头或响应体内容。

## 配置项

```yaml
testclass: http # http 验证器，用于验证http响应（状态码、header、响应消息内容）
field: status  # http响应的哪个部位 status、header[0].xxx、body
expected: 200  # 期望值
rule: ==   # 验证规则
strict: false # 是否严格验证，默认否：忽略大小写验证
```

## 参数说明

| 参数        | 必填 | 说明                                                                   |
|-----------|----|----------------------------------------------------------------------|
| testclass | 是  | 验证器类型，固定值为 `http`                                                    |
| field     | 是  | 要验证的 HTTP 响应部分，可选值包括：`status`（状态码）、`header[索引].字段名`（响应头）、`body`（响应体） |
| expected  | 是  | 期望值                                                                  |
| rule      | 是  | 验证规则，支持多种比较操作                                                        |
| strict    | 否  | 是否严格验证，默认为 `false`（忽略大小写）                                            |

## 使用示例

### 验证状态码

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
  "validators": [
    {
      "testclass": "http",
      "field": "status",
      "expected": 200,
      "rule": "=="
    }
  ]
}
```

### 验证响应头

```json
{
  "testclass": "http",
  "title": "下载文件",
  "config": {
    "method": "GET",
    "url": "https://api.example.com/files/document.pdf"
  },
  "validators": [
    {
      "testclass": "http",
      "field": "header[0].Content-Type",
      "expected": "application/pdf",
      "rule": "=="
    }
  ]
}
```

### 验证响应体内容

```json
{
  "testclass": "http",
  "title": "获取用户信息",
  "config": {
    "method": "GET",
    "url": "https://api.example.com/users/123"
  },
  "validators": [
    {
      "testclass": "http",
      "field": "body",
      "expected": "User not found",
      "rule": "contains"
    }
  ]
}
```

## 支持的验证规则

HTTP 验证器支持以下验证规则：

| 规则             | 说明     | 示例                                                                              |
|----------------|--------|---------------------------------------------------------------------------------|
| `==`           | 相等     | `field: status, expected: 200, rule: "=="`                                      |
| `eq_any`       | 任意一个相等 | `field: status, expected: [200, 300], rule: "eq_any"`                           |
| `!=`           | 不相等    | `field: status, expected: 404, rule: "!="`                                      |
| `>`            | 大于     | `field: status, expected: 0, rule: ">"`                                         |
| `<`            | 小于     | `field: status, expected: 10000, rule: "<"`                                     |
| `>=`           | 大于等于   | `field: status, expected: 0, rule: ">="`                                        |
| `<=`           | 小于等于   | `field: status, expected: 100, rule: "<="`                                      |
| `contains`     | 包含     | `field: header.Content-Type, expected: "success", rule: "contains"`             |
| `any_contains` | 包含任意一个 | `field: header.Content-Type, expected: [success, false], rule: "any_contains"`  |
| `not_contains` | 不包含    | `field: header.Content-Type, expected: "error", rule: "not_contains"`           |
| `regex`        | 正则匹配   | `field: header.Content-Type, expected: "^\\d{4}-\\d{2}-\\d{2}$", rule: "regex"` |
| `is_not_empty` | 非空     | `field: header.Content-Type, rule: "is_not_empty"`                              |
| `is_empty`     | 为空     | `field: header.Content-Type,  rule: "is_empty"`                                 |
| `same_object`  | 对象匹配   | `field: body, expected: {}, rule: "same_object"`                                |