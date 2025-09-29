# 自定义协议

Ryze框架支持通过SPI机制扩展新的协议支持。

## 核心接口

### Sampler

取样器接口，定义协议的具体执行逻辑：

```java
public class CustomSampler implements Sampler {
    @Override
    public Result sample(Context context) {
        // 实现协议执行逻辑
        return new Result();
    }
}
```

### Preprocessor

前置处理器接口：

```java
public class CustomPreprocessor implements Preprocessor {
    @Override
    public void process(ContextWrapper context) {
        // 实现前置处理逻辑
    }
}
```

### Postprocessor

后置处理器接口：

```java
public class CustomPostprocessor implements Postprocessor {
    @Override
    public void process(ContextWrapper context) {
        // 实现后置处理逻辑
    }
}
```

### ConfigureElement

配置元件接口：

```java
public class CustomConfigureElement implements ConfigureElement<TestSuiteResult> {
    @Override
    public TestSuiteResult process(ContextWrapper context) {
        // 实现配置应用逻辑
    }
}
```

## 注册自定义协议

在相应的`META-INF/services/`目录下注册实现类：

```
# META-INF/services/io.github.xiaomisum.ryze.testelement.sampler.Sampler
com.example.CustomSampler

# META-INF/services/io.github.xiaomisum.ryze.testelement.processor.Preprocessor
com.example.CustomPreprocessor

# META-INF/services/io.github.xiaomisum.ryze.testelement.processor.Postprocessor
com.example.CustomPostprocessor

# META-INF/services/io.github.xiaomisum.ryze.testelement.configelement.ConfigureElement
com.example.CustomConfigureElement
```

## 使用自定义协议

在测试配置中使用自定义协议：

```yaml
testclass: custom
config:
  customProperty: value
```