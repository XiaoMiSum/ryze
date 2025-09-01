# JSON 验证器

JSON 验证器用于验证 JSON 格式响应体中的特定字段值，通过 JSONPath 表达式定位字段。

## 配置项

```yaml
testclass: json # json 验证器，用于验证json内容
field: '$.status'  # json path
expected: 200  # 期望值
rule: ==   # 验证规则
strict: false # 是否严格验证，默认否：忽略大小写验证
```

## 参数说明

| 参数 | 必填 | 说明 |
|------|------|------|
| testclass | 是 | 验证器类型，固定值为 `json` |
| field | 是 | JSONPath 表达式，用于定位要验证的字段 |
| expected | 是 | 期望值 |
| rule | 是 | 验证规则，支持多种比较操作 |
| strict | 否 | 是否严格验证，默认为 `false`（忽略大小写） |

## 使用示例

### 验证基本字段

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
      "testclass": "json",
      "field": "$.status",
      "expected": 200,
      "rule": "=="
    },
    {
      "testclass": "json",
      "field": "$.data.user.name",
      "expected": "testuser",
      "rule": "=="
    },
    {
      "testclass": "json",
      "field": "$.data.token",
      "expected": "",
      "rule": "isNotEmpty"
    }
  ]
}
```

### 验证数组元素

```json
{
  "testclass": "http",
  "title": "获取用户列表",
  "config": {
    "method": "GET",
    "url": "https://api.example.com/users"
  },
  "assertions": [
    {
      "testclass": "json",
      "field": "$.data.length()",
      "expected": 10,
      "rule": ">="
    },
    {
      "testclass": "json",
      "field": "$.data[0].id",
      "expected": 0,
      "rule": ">"
    },
    {
      "testclass": "json",
      "field": "$.data[0].name",
      "expected": "",
      "rule": "isNotEmpty"
    }
  ]
}
```

### 复杂验证场景

```json
{
  "testclass": "http",
  "title": "创建订单",
  "config": {
    "method": "POST",
    "url": "https://api.example.com/orders",
    "body": {
      "product_id": 123,
      "quantity": 2
    }
  },
  "assertions": [
    {
      "testclass": "json",
      "field": "$.status",
      "expected": 201,
      "rule": "=="
    },
    {
      "testclass": "json",
      "field": "$.data.order_id",
      "expected": "",
      "rule": "isNotEmpty"
    },
    {
      "testclass": "json",
      "field": "$.data.total_amount",
      "expected": 100.0,
      "rule": ">="
    },
    {
      "testclass": "json",
      "field": "$.data.items.length()",
      "expected": 1,
      "rule": ">="
    }
  ]
}
```

## 支持的验证规则

JSON 验证器支持以下验证规则：

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

## JSONPath 语法说明

常用 JSONPath 表达式：

| 表达式 | 说明 | 示例 |
|--------|------|------|
| `$` | 根节点 | `$.status` |
| `.` | 子节点 | `$.data.user` |
| `..` | 递归下降 | `$..name` |
| `*` | 通配符 | `$.data.*` |
| `[]` | 数组索引 | `$.data[0]` |
| `[start:end]` | 数组切片 | `$.data[1:3]` |
| `[?(expression)]` | 过滤表达式 | `$.data[?(@.price > 100)]` |
| `()` | 脚本表达式 | `$.data[(@.length-1)]` |
| `@` | 当前节点 | `$.data[?(@.name == 'test')]` |