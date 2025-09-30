# ✅ 验证器

验证器是 Ryze 框架中用于验证测试结果是否符合预期的关键组件，支持多种数据格式和验证规则。

## 🔍 基本概念

### 作用原理

验证器在取样器或处理器执行完成后自动运行，从响应结果中获取实际值，并与预期值进行比较，判断测试是否通过。

### 配置位置

验证器可以配置在：

- **取样器中**：直接验证取样器的响应结果
- **后置处理器中**：作为后置处理的一部分
- **测试集合中**：在集合级别进行统一验证

### 验证流程

1. **获取实际值**：从响应数据中提取指定字段的值
2. **数据转换**：必要时进行数据类型转换
3. **规则匹配**：根据指定的验证规则进行比较
4. **结果返回**：返回验证通过或失败的结果

## 🛠️ 内置验证器

```yaml
testclass: json # json 验证器，用于验证json内容
field: '$.status'  # json path
expected: 200  # 期望值
rule: ==
```

或

```yaml
{ testclass: json, field: '$.status', expected: 200, rule: == }
```

testclass：定义验证器类型，可选值：json、JSONAssertion 、json_assertion

field：定义 验证值的 JSON PATH

expected：期望值，可以多个值

rule：验证规则，默认 ==

### HTTP 验证器

HTTP取样器专属验证器，通常用于验证 http状态码、http response header

```yaml
testclass: http_assertion # http 验证器，用于验证http响应（状态码、header、响应消息内容）
field: status  # http响应的哪个部位 status、header[0].xxx、body
expected: 200  # 期望值
rule: ==
```

或

```yaml
{ testclass: http_assertion, field: status, expected: 2000, rule: == }
```

testclass：定义取样器类型，可选值：http、https、HTTPAssertion 、http_assertion

field：定义 验证值，HTTP状态码：status，响应Body：body，响应头：header[0].Content-Type

expected：期望值，可以多个值

rule：验证规则，默认 ==

### Result 验证器

验证取样器响应结果是否符合预期

```yaml
testclass: result_assertion  # result 验证器类型
expected: 200 # 变量名称
rule: ==
```

或

```yaml
{ testclass: result_assertion, expected: 200, rule: == }
```

testclass：定义取样器类型，可选值：RegexAssertion 、regex_assertion

expected：期望值，可以多个值（仅部分验证规则生效）

rule：验证规则，默认 ==

## 验证规则 rule

验证规则默认为 ==，验证 实际值是否与期望值一致，详细规则如下：

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

## 💡 扩展功能

当内置验证器无法满足特定需求时，Ryze 框架支持自定义验证器和验证规则扩展。详细的开发指南请参考：

- **开发文档**：[开发者文档](/developer/validator) - 完整的开发指南和最佳实践
- **代码示例**
  ：查看框架源码中的[内置验证器实现](https://github.com/XiaoMiSum/ryze/tree/master/ryze/src/main/resources/META-INF/services/io.github.xiaomisum.ryze.assertion.Assertion)

**💡 提示**：验证器是确保测试质量的关键组件，合理使用各种验证规则可以构建强健的测试体系！