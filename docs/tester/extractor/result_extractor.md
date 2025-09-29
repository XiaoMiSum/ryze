# 结果提取器

结果提取器用于从整个测试步骤的执行结果中提取信息，并将其存储为变量以供后续使用。

## 配置项

```yaml
testclass: result  # result 提取器类型
ref_name: status # 变量名称
```

## 参数说明

| 参数        | 必填 | 说明                  |
|-----------|----|---------------------|
| testclass | 是  | 提取器类型，固定值为 `result` |
| ref_name  | 是  | 提取值存储的变量名称          |

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
      "testclass": "result",
      "ref_name": "login_result"
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

在上述示例中，我们将整个 HTTP 请求的执行结果存储在变量 `login_result` 中，可以在后续的测试步骤中使用该变量。

## 提取的内容

结果提取器会提取整个测试步骤的执行结果，包括：

- 响应状态
- 响应头
- 响应体
- 执行时间
- 其他元数据

这使得您可以在后续步骤中访问完整的执行结果信息。