# 自定义断言

Ryze框架允许开发者创建自定义断言来满足特定的验证需求。

## 创建自定义断言

继承`AbstractAssertion`类并实现验证逻辑：

```java

@KW({"CustomAssertion", "custom"})
public class CustomAssertion extends AbstractAssertion {
    @Override
    protected Object extractActualValue(SampleResult result) {
        // 自定义实现提取期望值
        return result.getResponse().bytesAsString();
    }
}
```

## 注册自定义断言

在`META-INF/services/io.github.xiaomisum.ryze.assertion.Assertion`文件中注册：

```
com.example.CustomAssertion
```

## 使用自定义断言

在测试中使用自定义断言：

```java
http.assertions(assertions ->assertions
        .

custom("自定义断言名称","${actualValue}","expectedValue")
);
```