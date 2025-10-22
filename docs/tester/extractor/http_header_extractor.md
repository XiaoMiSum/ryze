# HTTP Header 提取器

HTTP Header 提取器用于从 HTTP 响应头中提取指定字段的值，并将其存储为变量以供后续使用。

## 配置项

```yaml
testclass: http  # http header 提取器类型
field: 'Content-Type'  # 提取的 response header name 区分大小写
ref_name: content_type # 引用变量名称
```

## 参数说明

| 参数        | 必填 | 说明                |
|-----------|----|-------------------|
| testclass | 是  | 提取器类型，固定值为 `http` |
| field     | 是  | 要提取的响应头字段名，区分大小写  |
| ref_name  | 是  | 提取值存储的变量名称        |

## 使用示例

```yaml
testclass: http
title: 获取用户信息
config:
  method: GET
  protocol: http
  host: api.example.com
  path: /users/123
extractors:
  - testclass: http
    field: Content-Type
    ref_name: response_content_type
validators:
  - testclass: json
    field: $.id
    expected: 123
    rule: '=='
```

在上述示例中，我们从 HTTP 响应头中提取了 `Content-Type` 字段的值，并将其存储在变量 `response_content_type`
中，可以在后续的测试步骤中使用该变量。