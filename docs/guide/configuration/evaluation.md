# 动态求值

Ryze框架支持在测试执行过程中动态求值，提供灵活的配置选项。

## 运行时变量

在测试执行过程中动态生成的变量：

```java
// 在前置处理器中设置运行时变量
preprocessor.extractors(extractors ->extractors
        .

json("$.data.token","authToken")
);

// 在后续请求中使用
        http.

config(config ->config
        .

headers(Map.of("Authorization", "Bearer ${authToken}"))
        );
```

## 表达式求值

支持复杂的表达式求值：

```yaml
variables:
  # 数学运算
  total: "${price * quantity}"
  discount: "${total * 0.1}"
  finalPrice: "${total - discount}"

  # 字符串操作
  fullName: "${firstName + ' ' + lastName}"
  email: "${username + '@example.com'}"

  # 条件表达式
  status: "${(score >= 60)?then('PASS', 'FAIL')}"
```

## 函数式求值

使用Java 8+的函数式特性：

```java
suite.variables("currentTime",() ->System.

currentTimeMillis());
        suite.

variables("randomId",() ->UUID.

randomUUID().

toString());
```

## 延迟求值

某些表达式在实际使用时才求值：

```yaml
variables:
  # 延迟求值，每次使用时都会重新生成
  timestamp: "${now()}"

  # 立即求值，只在初始化时生成一次
  initTime: "${.now()}"
```

## 错误处理

处理求值过程中的错误：

```yaml
variables:
  # 提供默认值
  userName: "${user.name!'Anonymous'}"

  # 条件检查
  safeValue: "${(user.age > 0)?then(user.age, 18)}"
```