# 自定义提取器

Ryze框架允许开发者创建自定义提取器来从响应中提取特定格式的数据。

## 创建自定义提取器

实现`Extractor`接口：

```java

@KW({"CustomExtractor", "custom"})
public class CustomExtractor implements Extractor {
    @Override
    public Object extract(Object source, String expression) {
        // 实现自定义提取逻辑
        if (source instanceof String text) {
            // 自定义提取逻辑
            return text.substring(text.indexOf(expression));
        }
        return null;
    }
}
```

继承`AbstractExtractor`抽象类：

```java

@KW({"CustomExtractor", "custom"})
public class CustomExtractor extends AbstractExtractor {
    
    @Override
    protected Object extract(SampleResult result) {
        var source = result.getResponse().bytesAsString();
        // 实现自定义提取逻辑
        if (source instanceof String text) {
            // 自定义提取逻辑
            return text.substring(text.indexOf(expression));
        }
        return null;
    }
}
```

## 注册自定义提取器

在`META-INF/services/io.github.xiaomisum.ryze.extractor.Extractor`文件中注册：

```
com.example.CustomExtractor
```

## 使用自定义提取器

在测试中使用自定义提取器：

```groovy
http.extractors(extractors -> extractors.custom("customVariable", '$.data.customField'))
```