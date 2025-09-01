# 断言与提取器 API

断言与提取器API提供了验证和数据提取功能，是测试结果处理的核心组件。

## 断言 API

### Assertion 接口
断言接口，定义了验证测试结果的方法。

#### 主要方法
- `validate(Object actual, Object expected, String rule)`: 验证实际值是否符合预期
- `getDescription()`: 获取断言描述

### AbstractAssertion 抽象类
断言基类，提供了通用的断言实现。

### JsonAssertion 类
JSON断言实现，用于验证JSON格式的响应数据。

#### 主要方法
- `validateJson(String jsonPath, Object expected, String rule)`: 验证JSON路径表达式的值

### HttpResponseAssertion 类
HTTP响应断言实现，用于验证HTTP响应状态码和头部信息。

#### 主要方法
- `validateStatus(int expectedStatus)`: 验证HTTP状态码
- `validateHeader(String headerName, Object expectedValue, String rule)`: 验证HTTP头部

### 内置断言规则

#### 相等性断言
- `==`: 等于
- `!=`: 不等于

#### 比较断言
- `&gt;`: 大于
- `&gt;=`: 大于等于
- `&lt;`: 小于
- `&lt;=`: 小于等于

#### 包含性断言
- `contains`: 包含
- `notContains`: 不包含

#### 正则断言
- `regex`: 正则匹配

## 提取器 API

### Extractor 接口
提取器接口，定义了从测试结果中提取数据的方法。

#### 主要方法
- `extract(Object source, String expression)`: 从源数据中提取数据
- `getName()`: 获取提取器名称

### JsonExtractor 类
JSON提取器实现，用于从JSON格式的响应中提取数据。

#### 主要方法
- `extractJson(String json, String jsonPath)`: 使用JSON路径表达式提取数据

### RegexExtractor 类
正则提取器实现，用于使用正则表达式从文本中提取数据。

#### 主要方法
- `extractRegex(String text, String regex, int group)`: 使用正则表达式提取数据

### HeaderExtractor 类
HTTP头部提取器实现，用于从HTTP响应头部提取数据。

#### 主要方法
- `extractHeader(Map&lt;String, String&gt; headers, String headerName)`: 提取HTTP头部值

## 使用示例

### JSON断言示例
```java
// 验证JSON响应中的字段值
assertions.json("$.data.id", 123, "==");
assertions.json("$.data.name", "John", "contains");
```

### JSON提取器示例
```java
// 从JSON响应中提取数据并存储到变量
extractor.json("$.data.id", "userId");
extractor.json("$.data.token", "authToken");
```

### HTTP响应断言示例
```java
// 验证HTTP状态码和头部信息
assertions.httpStatus(200);
assertions.httpHeader("Content-Type", "application/json", "contains");
```