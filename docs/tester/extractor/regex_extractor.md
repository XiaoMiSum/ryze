# 正则表达式提取器

正则表达式提取器用于从响应内容中通过正则表达式提取指定内容，并将其存储为变量以供后续使用。

## 配置项

```yaml
testclass: regex  # regex 提取器类型
field: '(\\d+)'  # 提取的 正则表达式
match_num: 0  # 匹配到多个时，取哪一个值
ref_name: status # 变量名称
```

## 参数说明

| 参数        | 必填 | 说明                               |
|-----------|----|----------------------------------|
| testclass | 是  | 提取器类型，固定值为 `regex`               |
| field     | 是  | 正则表达式模式                          |
| match_num | 否  | 当匹配到多个结果时，指定使用第几个匹配结果（从0开始），默认为0 |
| ref_name  | 是  | 提取值存储的变量名称                       |

## 使用示例

### 提取用户ID

```json
{
  "testclass": "http",
  "title": "创建用户",
  "config": {
    "method": "POST",
    "url": "https://api.example.com/users",
    "body": {
      "name": "testuser"
    }
  },
  "extractors": [
    {
      "testclass": "regex",
      "field": "User ID: (\\d+)",
      "match_num": 0,
      "ref_name": "user_id"
    }
  ],
  "validators": [
    {
      "testclass": "http",
      "field": "status",
      "expected": 201,
      "rule": "=="
    }
  ]
}
```

在上述示例中，我们使用正则表达式从响应内容中提取用户ID，并将其存储在变量 `user_id` 中。

### 提取多个匹配项

```json
{
  "testclass": "http",
  "title": "获取订单列表",
  "config": {
    "method": "GET",
    "url": "https://api.example.com/orders"
  },
  "extractors": [
    {
      "testclass": "regex",
      "field": "Order ID: (\\d+)",
      "match_num": 0,
      "ref_name": "first_order_id"
    },
    {
      "testclass": "regex",
      "field": "Order ID: (\\d+)",
      "match_num": 1,
      "ref_name": "second_order_id"
    }
  ]
}
```

在此示例中，我们从响应中提取多个订单ID，通过设置不同的 `match_num` 值来获取第一个和第二个匹配项。