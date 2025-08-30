# ✅ 验证器开发指南

## 概述

验证器用于验证测试结果是否符合预期。Ryze 框架提供了两种验证器类型：断言（Assertion）和验证规则（Rule）。通过开发自定义验证器，您可以实现特定的验证逻辑。

## 验证器类型

### 1. 断言（Assertion）

断言用于验证整个测试结果的特定字段，继承 `AbstractAssertion` 类：

```java
public abstract class AbstractAssertion implements Assertion {
    protected String field;      // 字段表达式
    protected Object expected;   // 期望值
    protected String rule;       // 验证规则
    protected Object actualValue; // 实际值
    
    /**
     * 初始化断言结果，由子类实现
     */
    protected abstract AssertionResult initialized(SampleResult result);
}
```

### 2. 验证规则（Rule）

验证规则定义具体的比较逻辑，实现 `Rule` 接口：

```java
public interface Rule {
    /**
     * 执行断言规则验证
     * @param actual 实际值
     * @param expected 期望值
     * @return 验证结果
     */
    boolean assertThat(Object actual, Object expected);
}
```

## 断言开发实例

### XML 断言

```java
@KW({"xml_assertion", "xml"})
public class XMLAssertion extends AbstractAssertion {
    
    private String xpath;
    
    @Override
    protected AssertionResult initialized(SampleResult result) {
        var assertionResult = new AssertionResult("XML 断言: " + field);
        
        try {
            String xmlContent = result.getResponse().bytesAsString();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));
            
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expression = xpath.compile(this.xpath);
            
            actualValue = expression.evaluate(document);
            
        } catch (Exception e) {
            assertionResult.setStatus(TestStatus.failed);
            assertionResult.setMessage("XML 解析失败: " + e.getMessage());
        }
        
        return assertionResult;
    }
    
    @Override
    public ValidateResult validate() {
        ValidateResult result = new ValidateResult();
        if (StringUtils.isBlank(xpath)) {
            result.append("XPath 表达式不能为空");
        }
        result.append(super.validate());
        return result;
    }
    
    public static class Builder extends AbstractAssertion.Builder<Builder, XMLAssertion> {
        public Builder() {
            super(new XMLAssertion());
        }
        
        public Builder xpath(String xpath) {
            assertion.xpath = xpath;
            return self;
        }
    }
}
```

## 验证规则开发实例

### 邮箱验证规则

```java
@KW({"email", "isEmail", "validEmail"})
public class EmailValidationRule implements Rule {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    @Override
    public boolean assertThat(Object actual, Object expected) {
        if (actual == null) return false;
        return EMAIL_PATTERN.matcher(actual.toString()).matches();
    }
}
```

### 范围验证规则

```java
@KW({"inRange", "between", "range"})
public class RangeValidationRule extends BaseRule implements Rule {
    
    @Override
    public boolean assertThat(Object actual, Object expected) {
        try {
            double actualValue = Double.parseDouble(objectToString(actual));
            String[] range = objectToString(expected).split(",");
            
            if (range.length != 2) {
                throw new IllegalArgumentException("范围格式错误，应为 'min,max'");
            }
            
            double min = Double.parseDouble(range[0].trim());
            double max = Double.parseDouble(range[1].trim());
            
            return actualValue >= min && actualValue <= max;
            
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
```

### JSON Schema 验证规则

```java
@KW({"jsonSchema", "schema"})
public class JsonSchemaValidationRule implements Rule {
    
    @Override
    public boolean assertThat(Object actual, Object expected) {
        try {
            // 将期望值作为 JSON Schema
            String schemaJson = objectToString(expected);
            String dataJson = objectToString(actual);
            
            // 使用 JSON Schema 验证库
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema schema = factory.getSchema(schemaJson);
            
            JsonNode dataNode = new ObjectMapper().readTree(dataJson);
            Set<ValidationMessage> errors = schema.validate(dataNode);
            
            return errors.isEmpty();
            
        } catch (Exception e) {
            return false;
        }
    }
}
```

## 验证器注册

### SPI 注册

断言注册文件 `src/main/resources/META-INF/services/io.github.xiaomisum.ryze.core.assertion.Assertion`：

```
com.example.XMLAssertion
```

规则注册文件 `src/main/resources/META-INF/services/io.github.xiaomisum.ryze.core.assertion.Rule`：

```
com.example.EmailValidationRule
com.example.RangeValidationRule
com.example.JsonSchemaValidationRule
```

## 验证器使用

### 在 Java API 中使用

```java
@Test
@RyzeTest
public void testWithValidators() {
    MagicBox.http("验证器测试", http -> {
        http.config(config -> config
            .method("POST")
            .url("https://api.example.com/users")
        );
        
        http.assertions(assertions -> 
            assertions.xml("//user/id", "", "isNotEmpty")
                      .xml("//user/email", "", "email")
                      .json("$.age", "18,65", "inRange")
                      .json("$", schemaJson, "jsonSchema")
        );
    });
}
```

### 在 YAML 中使用

```yaml
title: 验证器测试
testclass: http
config:
  method: POST
  url: "https://api.example.com/users"
    
assertions:
  - testclass: xml
    xpath: "//user/id"
    expected: ""
    rule: "isNotEmpty"
  - testclass: xml
    xpath: "//user/email"
    expected: ""
    rule: "email"
  - testclass: json
    field: "$.age"
    expected: "18,65"
    rule: "inRange"
```

## 开发规范

1. **使用注解**：用 `@KW` 注解定义关键字
2. **异常处理**：处理验证过程中的异常
3. **类型转换**：合理处理数据类型转换
4. **性能考虑**：避免耗时验证操作
5. **文档完整**：提供清晰的使用文档

## 最佳实践

1. **规则简单**：保持验证规则的简单性
2. **错误信息**：提供有意义的错误信息
3. **向后兼容**：保持 API 的向后兼容性
4. **测试覆盖**：编写完整的单元测试
5. **性能优化**：缓存编译结果（如正则表达式）

通过遵循这些指导原则，您可以开发出高效、可靠的验证器组件。