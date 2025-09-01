# 模板语法与动态求值

Ryze框架内置了FreeMarker模板引擎，支持强大的模板语法和动态求值功能。

## 变量引用

使用`${variableName}`语法引用变量：

```yaml
variables:
  username: john
  userId: 123

config:
  url: "https://api.example.com/users/${userId}"
  body:
    name: "${username}"
```

## 函数调用

调用内置函数：

```yaml
variables:
  uuid: "${uuid()}"
  timestamp: "${now()}"
  randomNum: "${random(1000)}"
```

## 表达式求值

使用FreeMarker表达式：

```yaml
variables:
  score: 85
  passed: "${(score >= 60)?then('PASS', 'FAIL')}"

config:
  headers:
    X-Priority: "${score > 90? 'HIGH' : 'NORMAL'}"
```

## 条件处理

```yaml
variables:
  userType: "admin"

config:
  headers:
    Authorization: >
      ${(userType == 'admin')?then(
        'Bearer ' + admin_token,
        'Basic ' + user_token
      )}
```

## 循环处理

```yaml
variables:
  items:
    - name: "item1"
      value: "value1"
    - name: "item2"
      value: "value2"

body:
  items:
    <#list items as item>
    - ${item.name}: ${item.value}
    </#list>
```

## 字符串操作

```yaml
variables:
  fullName: "John Doe"
  firstName: "${fullName?substring(0, fullName?index_of(' '))}"
  lastName: "${fullName?substring(fullName?index_of(' ') + 1)}"
  upperName: "${fullName?upper_case}"
```