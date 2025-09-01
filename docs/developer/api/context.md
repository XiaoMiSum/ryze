# 上下文与变量 API

上下文与变量API管理测试执行过程中的变量和上下文信息，提供了动态数据管理和作用域控制功能。

## Context 接口

执行上下文接口，用于在测试执行过程中存储和传递数据。

### 主要方法

#### put(String name, Object value)
将变量存储到上下文中。

#### get(String name)
从上下文中获取变量值。

#### remove(String name)
从上下文中移除变量。

#### containsKey(String name)
检查上下文中是否包含指定变量。

#### getVariables()
获取所有变量的映射。

#### clone()
克隆上下文对象。

## VariableManager 类

变量管理器，负责处理变量的解析和替换。

### 主要方法

#### resolve(String expression, Context context)
解析表达式中的变量引用并替换为实际值。

#### evaluate(String expression, Context context)
计算表达式的值。

#### registerFunction(String name, Function function)
注册自定义函数。

## 内置变量

### 测试相关变量
- `${test.id}`: 测试ID
- `${test.title}`: 测试标题
- `${test.timestamp}`: 测试时间戳

### 系统变量
- `${system.timestamp}`: 系统时间戳
- `${system.date}`: 系统日期
- `${system.uuid}`: UUID生成器

### 环境变量
- `${env.VARIABLE_NAME}`: 环境变量值

## 内置函数

### 字符串函数
- `upper(value)`: 转换为大写
- `lower(value)`: 转换为小写
- `trim(value)`: 去除首尾空格
- `substring(value, start, end)`: 截取子字符串

### 数学函数
- `randomInt(min, max)`: 生成随机整数
- `randomFloat(min, max)`: 生成随机浮点数
- `round(value, decimals)`: 四舍五入

### 日期函数
- `now()`: 当前时间
- `formatDate(date, pattern)`: 格式化日期
- `addDays(date, days)`: 增加天数

### JSON函数
- `toJson(object)`: 转换为JSON字符串
- `parseJson(json)`: 解析JSON字符串

## 使用示例

### 变量定义和使用
```java
// 在测试套件中定义变量
suite.variables("userId", 123);
suite.variables("userName", "John");

// 在取样器中使用变量
http.config(config -> config
    .path("/users/${userId}")
    .body(Map.of("name", "${userName}"))
);
```

### 动态表达式求值
```java
// 使用函数和表达式
http.config(config -> config
    .path("/orders")
    .body(Map.of(
        "orderId", "${randomInt(1000, 9999)}",
        "timestamp", "${now()}",
        "customerName", "${upper(${userName})}"
    ))
);
```

### 变量提取和传递
```java
// 从前一个请求中提取数据
http.extractors(extract -> extract
    .json("$.data.id", "orderId")
    .json("$.data.token", "authToken")
);

// 在后续请求中使用提取的数据
http.config(config -> config
    .headers(Map.of("Authorization", "Bearer ${authToken}"))
    .path("/orders/${orderId}")
);
```