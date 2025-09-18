# JSON 提取器

JSON 提取器用于从 JSON 格式的响应体中提取指定字段的值，并将其存储为变量以供后续使用。

## 配置项

```yaml
testclass: json  # json 提取器类型
field: '$.status'  # 提取的 json path
ref_name: status # 变量名称
```

## 参数说明

| 参数        | 必填 | 说明                      |
|-----------|----|-------------------------|
| testclass | 是  | 提取器类型，固定值为 `json`       |
| field     | 是  | JSONPath 表达式，用于定位要提取的字段 |
| ref_name  | 是  | 提取值存储的变量名称              |

## 使用示例

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
  "extractors": [
    {
      "testclass": "json",
      "field": "$.data.token",
      "ref_name": "auth_token"
    },
    {
      "testclass": "json",
      "field": "$.data.user.id",
      "ref_name": "user_id"
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
}
```

在上述示例中，我们从 JSON 响应体中提取了 token 和用户 ID，并将它们分别存储在变量 `auth_token` 和 `user_id`
中，可以在后续的测试步骤中使用这些变量。