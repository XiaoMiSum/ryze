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

| 参数 | 必填 | 说明 |
|------|------|------|
| testclass | 是 | 验证器类型，固定值为 `http` |
| field | 是 | 要验证的 HTTP 响应部分，可选值包括：`status`（状态码）、`header[索引].字段名`（响应头）、`body`（响应体） |
| expected | 是 | 期望值 |
| rule | 是 | 验证规则，支持多种比较操作 |
| strict | 否 | 是否严格验证，默认为 `false`（忽略大小写） |

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
  "assertions": [
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
  "assertions": [
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
  "assertions": [
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

| 规则 | 说明 | 示例 |
|------|------|------|
| `==` | 相等 | `expected: 200, rule: "=="` |
| `!=` | 不相等 | `expected: 404, rule: "!="` |
| `>` | 大于 | `expected: 0, rule: ">"` |
| `<` | 小于 | `expected: 10000, rule: "<"` |
| `>=` | 大于等于 | `expected: 0, rule: ">="` |
| `<=` | 小于等于 | `expected: 100, rule: "<="` |
| `contains` | 包含 | `expected: "success", rule: "contains"` |
| `notContains` | 不包含 | `expected: "error", rule: "notContains"` |
| `startsWith` | 以...开始 | `expected: "http", rule: "startsWith"` |
| `endsWith` | 以...结束 | `expected: ".com", rule: "endsWith"` |
| `matches` | 正则匹配 | `expected: "^\\d{4}-\\d{2}-\\d{2}$", rule: "matches"` |
| `isNotEmpty` | 非空 | `expected: "", rule: "isNotEmpty"` |
| `isEmpty` | 为空 | `expected: "", rule: "isEmpty"` |