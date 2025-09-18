# ✅ 验证器开发指南

## 概述

验证器用于验证测试结果是否符合预期。Ryze 框架提供了断言（Assertion）机制来实现测试结果验证。通过开发自定义验证器，您可以实现特定的验证逻辑。

## 断言（Assertion）架构

### 核心接口和类

1. **Assertion接口**：所有断言的根接口，定义了断言的基本规范
2. **AbstractAssertion抽象类**：实现了Assertion接口的抽象基类，提供了断言的通用实现
3. **Matchers工具类**：匹配器工厂类，用于创建各种匹配器实例
4. **ProxyMatcher抽象类**：所有自定义Matcher的基类，继承自Hamcrest的BaseMatcher
5. **内置匹配器**：框架提供的一系列常用匹配器实现

### AbstractAssertion 抽象类

AbstractAssertion是所有断言的基类，提供了断言的通用实现：

```java
public abstract class AbstractAssertion implements Assertion, AssertionConstantsInterface {
    protected Object actualValue;     // 实际值，用于与期望值进行比较
    protected Object expected;        // 期望值，断言的目标值
    protected String rule;            // 验证规则，如"==", "!=", "contains"等
    protected String field;           // 字段名，用于标识要验证的数据字段
    protected boolean strict;         // 严格匹配标志
    protected Matcher<Object> matcher; // 指定匹配方式，通过 org.hamcrest.Matcher 接口实现

    /**
     * 执行断言验证逻辑
     */
    @Override
    public void assertThat(ContextWrapper context) {
        // 实现细节...
    }

    /**
     * 提取期望值对象值，由子类实现
     */
    protected abstract Object extractActualValue(SampleResult result);
}
```

### 内置匹配器

框架提供了一系列内置匹配器，通过Matchers工厂类创建：

1. **EqualsMatcher**：相等匹配器，支持多种数据类型的深度比较
2. **NotEqualsMatcher**：不相等匹配器，与EqualsMatcher相反
3. **ContainsMatcher**：包含匹配器，检查实际值是否包含期望值
4. **NotContainsMatcher**：不包含匹配器，与ContainsMatcher相反
5. **AnyEqualsMatcher**：任意相等匹配器，检查实际值是否与期望值集合中的任意一个元素相等
6. **AnyContainsMatcher**：任意包含匹配器，检查实际值是否包含期望值集合中的任意一个元素
7. **IsEmpty**：空值匹配器，检查实际值是否为空
8. **IsNotEmpty**：非空匹配器，检查实际值是否不为空
9. **RegexMatcher**：正则表达式匹配器，检查实际值是否匹配指定的正则表达式
10. **SameObjectMatcher**：相同对象匹配器，检查实际值是否与期望值为相同的对象
11. **数字比较匹配器**：包括GreaterMatcher（大于）、LessMatcher（小于）、GreaterOrEqualsMatcher（大于等于）、LessOrEqualsMatcher（小于等于）

## 断言开发实例

### JSON 断言

```java

@KW({"JSONAssertion", "json_assertion", "json"})
public class JSONAssertion extends AbstractAssertion {


    /**
     * 创建JSON断言构建器
     *
     * @return JSON断言构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 初始化断言期望值，从JSON响应中提取指定字段的值
     *
     * <p>该方法使用field属性作为JSONPath表达式从响应中提取实际值。</p>
     *
     * @param result 取样结果对象
     * @return 断言期望值对象
     */
    @Override
    protected Object extractActualValue(SampleResult result) {
        var target = result.getResponse().bytesAsString();
        return JSONPath.extract(target, field);
    }

    /**
     * 验证断言配置的有效性
     *
     * <p>该方法检查field字段是否为空，如果为空则返回验证失败结果。</p>
     *
     * @return 验证结果
     */
    @Override
    public ValidateResult validate() {
        ValidateResult result = new ValidateResult();
        if (StringUtils.isBlank(field)) {
            result.append("\n提取表达式 %s 字段值缺失或为空，当前值：%s", field, toString());
        }
        return result;
    }

    /**
     * JSON断言构建器类
     */
    public static class Builder extends AbstractAssertion.Builder<Builder, JSONAssertion> {

        /**
         * 构造函数，创建JSON断言构建器
         */
        public Builder() {
            super(new JSONAssertion());
        }
    }
}
```

## 验证规则开发

> ⚠️ **注意**：Rule 接口已被标记为废弃（@Deprecated），从版本 6.0.2 开始不再推荐使用。请使用 Hamcrest Matcher 作为验证规则的核心实现。

### 使用 Hamcrest Matcher 开发验证规则

Ryze 框架使用 Hamcrest 匹配器作为验证规则的核心实现。每个匹配器都通过 @KW 注解定义了支持的关键字。

#### ProxyMatcher 抽象基类

所有自定义匹配器都应继承 ProxyMatcher 抽象类，该类提供了通用的属性和构造方法：

```java
public abstract class ProxyMatcher extends BaseMatcher<Object> {
    protected boolean strict;        // 严格匹配标志
    protected Object expectedValue;  // 期望值

    public ProxyMatcher(Object expectedValue, boolean strict) {
        this.expectedValue = expectedValue;
        this.strict = strict;
    }
}
```

#### 开发自定义匹配器

以下是一个自定义匹配器的开发示例：

```java

@KW({"email", "isEmail", "validEmail"})
public class EmailMatcher extends ProxyMatcher {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    public EmailMatcher(Object expectedValue) {
        this(expectedValue, false);
    }

    public EmailMatcher(Object expectedValue, boolean strict) {
        super(expectedValue, strict);
    }

    @Override
    public boolean matches(Object actualValue) {
        if (actualValue == null) return false;
        return EMAIL_PATTERN.matcher(actualValue.toString()).matches();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a valid email address");
    }
}
```

#### 内置匹配器示例

##### EqualsMatcher（相等匹配器）

支持的关键字：equals, equal, qe, is, =, ==, ===, 等于, 相等

```java

@KW({"equals", "equal", "qe", "is", "=", "==", "===", "等于", "相等"})
public class EqualsMatcher extends ProxyMatcher {
    @Override
    public boolean matches(Object actualValue) {
        return Comparator.areEqual(actualValue, expectedValue, !strict);
    }

    @Override
    public void describeTo(Description description) {
        // 实现细节...
    }
}
```

##### ContainsMatcher（包含匹配器）

支持的关键字：contains, ct, 包含, ⊆, contain

```java

@KW({"contains", "ct", "包含", "⊆", "contain"})
public class ContainsMatcher extends ProxyMatcher {
    @Override
    public boolean matches(Object actualValue) {
        return Comparator.contains(actualValue, expectedValue, !strict);
    }

    @Override
    public void describeTo(Description description) {
        // 实现细节...
    }
}
```

##### RegexMatcher（正则表达式匹配器）

支持的关键字：regex, rx, 正则, 正则表达式

```java

@KW({"regex", "rx", "正则", "正则表达式"})
public class RegexMatcher extends ProxyMatcher {
    private final Pattern pattern;

    public RegexMatcher(Object expected, boolean strict) {
        super(strict);
        this.pattern = Pattern.compile(expected.toString(), !strict ? Pattern.CASE_INSENSITIVE : 0);
    }

    @Override
    public boolean matches(Object actualValue) {
        return pattern.matcher((String) actualValue).matches();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches regex: ").appendValue(pattern.pattern())
                .appendText(strict ? "" : " (ignore case)");
    }
}
```

## 验证器注册

### SPI 注册

#### 断言注册

断言通过 SPI 机制注册，需要在 `src/main/resources/META-INF/services/io.github.xiaomisum.ryze.assertion.Assertion`
文件中添加断言类的全限定名：

```
io.github.xiaomisum.ryze.assertion.builtin.JSONAssertion
io.github.xiaomisum.ryze.assertion.builtin.ResultAssertion
```

#### 匹配器注册

匹配器同样通过 SPI 机制注册，需要在 `src/main/resources/META-INF/services/org.hamcrest.Matcher` 文件中添加匹配器类的全限定名：

```
io.github.xiaomisum.ryze.assertion.builtin.matcher.AnyContainsMatcher
io.github.xiaomisum.ryze.assertion.builtin.matcher.AnyEqualsMatcher
io.github.xiaomisum.ryze.assertion.builtin.matcher.ContainsMatcher
io.github.xiaomisum.ryze.assertion.builtin.matcher.EqualsMatcher
io.github.xiaomisum.ryze.assertion.builtin.matcher.GreaterMatcher
io.github.xiaomisum.ryze.assertion.builtin.matcher.GreaterOrEqualsMatcher
io.github.xiaomisum.ryze.assertion.builtin.matcher.LessMatcher
io.github.xiaomisum.ryze.assertion.builtin.matcher.LessOrEqualsMatcher
io.github.xiaomisum.ryze.assertion.builtin.matcher.NotContainsMatcher
io.github.xiaomisum.ryze.assertion.builtin.matcher.NotEqualsMatcher
io.github.xiaomisum.ryze.assertion.builtin.matcher.RegexMatcher
io.github.xiaomisum.ryze.assertion.builtin.matcher.SameObjectMatcher
```

#### 关键字映射

匹配器的关键字映射通过 @KW 注解定义。框架在启动时会自动扫描所有注册的匹配器类，并提取其 @KW 注解中的关键字，建立关键字与匹配器类的映射关系。

例如，EqualsMatcher 类使用了以下注解：

```java

@KW({"equals", "equal", "qe", "is", "=", "==", "===", "等于", "相等"})
public class EqualsMatcher extends ProxyMatcher {
    // ...
}
```

这表示在断言中使用 "equals"、"equal"、"qe"、"is"、"="、"=="、"==="、"等于" 或 "相等" 作为规则时，都会使用 EqualsMatcher 进行验证。

匹配器映射配置在 `ApplicationConfig` 中定义，通过规则名称查找对应的匹配器类型：

```java
public static Map<String, Class<? extends Matcher>> getMatcherKeyMap() {
    return getDataMap(MATCHER_KEY_MAP_LOCK,
            () -> ApplicationConfig.MATCHER_KEY_MAP,
            () -> {
                ApplicationConfig.MATCHER_KEY_MAP = RyzeServiceLoader.loadAsMapBySPI(Matcher.class);
                return ApplicationConfig.MATCHER_KEY_MAP;
            }
    );
}
```

当创建匹配器实例时，Matchers 工厂类会根据规则名称从映射表中查找对应的匹配器类型，并通过 JSON 反序列化创建实例：

```java
public static Matcher<Object> createMatcher(String rule, Object expected, boolean strict) {
    Class<? extends Matcher> type = ApplicationConfig.getMatcherKeyMap().get(rule.toLowerCase());
    String string = JSONObject.of("expectedValue", expected, "strict", strict).toString();
    return JSON.parseObject(string, type);
}
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
                assertions.json("$.user.id", 123, "equals")
                        .json("$.user.name", "John", "contains")
                        .json("$.user.email", "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$", "regex")
                        .json("$.user.age", 18, ">")
                        .json("$.user.tags", new String[]{"vip", "premium"}, "any_contains")
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
  - testclass: json
    jsonpath: "$.user.id"
    expected: 123
    rule: "equals"
  - testclass: json
    jsonpath: "$.user.name"
    expected: "John"
    rule: "contains"
  - testclass: json
    jsonpath: "$.user.email"
    expected: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    rule: "regex"
  - testclass: json
    jsonpath: "$.user.age"
    expected: 18
    rule: ">"
  - testclass: json
    jsonpath: "$.user.tags"
    expected: [ "vip", "premium" ]
    rule: "any_contains"
```

## 开发规范

1. **使用注解**：用 `@KW` 注解定义关键字
2. **继承抽象类**：自定义断言应继承 `AbstractAssertion` 类，自定义匹配器应继承 `ProxyMatcher` 类
3. **实现抽象方法**：断言必须实现 `extractActualValue` 方法，匹配器必须实现 `matches` 和 `describeTo` 方法
4. **使用Builder模式**：为断言提供链式调用的Builder
5. **异常处理**：妥善处理验证过程中的异常
6. **类型转换**：合理处理数据类型转换
7. **性能考虑**：避免耗时验证操作
8. **文档完整**：提供清晰的使用文档

## 最佳实践

1. **规则简单**：保持验证规则的简单性
2. **错误信息**：提供有意义的错误信息
3. **向后兼容**：保持 API 的向后兼容性
4. **测试覆盖**：编写完整的单元测试
5. **性能优化**：缓存编译结果（如正则表达式）
6. **合理使用strict模式**：根据需要选择是否严格匹配大小写