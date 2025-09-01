# 自定义函数

Ryze框架允许开发者创建自定义函数来扩展模板引擎的功能。

## 创建自定义函数

实现`Function`接口：

```java
public class CustomFunction implements Function {
    @Override
    public Object execute(Object... args) {
        // 实现自定义函数逻辑
        if (args.length > 0) {
            return "Custom: " + args[0].toString();
        }
        return "Custom Function";
    }
    
    @Override
    public String getName() {
        return "custom";
    }
}
```

## 注册自定义函数

在`META-INF/services/io.github.xiaomisum.ryze.function.Function`文件中注册：

```
com.example.CustomFunction
```

## 使用自定义函数

在测试中使用自定义函数：

```java
suite.variables("customValue", "${custom('inputValue')}");
```