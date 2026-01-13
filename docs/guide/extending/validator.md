# 自定义断言

Ryze框架允许开发者创建自定义断言来满足特定的验证需求。

## 创建自定义断言

实现`Assertion`接口：

```java

@KW({"CustomAssertion", "custom"})
public class CustomAssertion implements Assertion {

    @Override
    public void assertThat(ContextWrapper context) {
        var target = result.getResponse().bytesAsString();
        return target.contains(expectedValue);
    }
}
```

继承`AbstractAssertion`抽象类：

```java

@KW({"CustomExtractor", "custom"})
public class CustomExtractor extends AbstractAssertion {

    @Override
    protected Object extractActualValue(SampleResult result) {
        var target = result.getResponse().bytesAsString();
        return JSONPath.extract(field, field);
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

```groovy

http.assertions(assertion -> assertion.custom("自定义断言名称", '${actualValue}', "expectedValue"))
```