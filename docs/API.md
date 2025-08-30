# 📝 Ryze API 文档

## 📖 概述

本文档详细介绍了 Ryze 测试框架的核心 API，包括主要类、接口、方法以及使用示例。

## 🎯 核心 API

### 1. Ryze - 框架入口类

`Ryze` 类是框架的主要入口点，提供启动测试执行的方法。

#### 类定义

```java
public class Ryze {
    public Ryze(JsonTree testcase);
    public static Result start(String filePath);
    public static Result start(Map<String, Object> testcase);
    public static Result start(JsonTree testcase);
}
```

#### 方法详解

##### start(String filePath)

**描述**: 通过文件路径启动测试执行

**参数**:
- `filePath` (String): 测试用例文件路径，支持 JSON/YAML 格式

**返回值**: `Result` - 测试执行结果

**示例**:
```java
// 执行 JSON 测试文件
Result result = Ryze.start("test-cases/api-test.json");

// 执行 YAML 测试文件  
Result result = Ryze.start("test-cases/api-test.yaml");
```

##### start(Map<String, Object> testcase)

**描述**: 通过 Map 数据启动测试执行

**参数**:
- `testcase` (Map<String, Object>): Map 格式的测试用例数据

**返回值**: `Result` - 测试执行结果

**示例**:
```java
Map<String, Object> testCase = Map.of(
    "title", "用户API测试",
    "children", List.of(
        Map.of(
            "testclass", "http",
            "method", "GET",
            "url", "https://api.example.com/users"
        )
    )
);
Result result = Ryze.start(testCase);
```

##### start(JsonTree testcase)

**描述**: 通过 JsonTree 对象启动测试执行

**参数**:
- `testcase` (JsonTree): 标准化后的测试用例对象

**返回值**: `Result` - 测试执行结果

**示例**:
```java
JSONObject config = JSON.parseObject(jsonString);
JsonTree testCase = new JsonTree(config);
Result result = Ryze.start(testCase);
```

### 2. MagicBox - 函数式编程入口

`MagicBox` 提供函数式编程风格的测试用例构建和执行。

#### 类定义

```java
public class MagicBox {
    // 测试套件方法
    public static TestSuiteResult suite(Closure<?> closure);
    public static TestSuiteResult suite(String title, Closure<?> closure);
    public static TestSuiteResult suite(Customizer<TestSuite.Builder> customizer);
    public static TestSuiteResult suite(String title, Customizer<TestSuite.Builder> customizer);
    
    // HTTP 测试方法
    public static Result http(Closure<?> closure);
    public static Result http(String title, Closure<?> closure);
    public static Result http(Customizer<HTTPSampler.Builder> customizer);
    public static Result http(String title, Customizer<HTTPSampler.Builder> customizer);
    
    // 其他协议方法...
}
```

#### 方法详解

##### suite() 方法组

**描述**: 创建和执行测试套件

**Groovy 闭包方式**:
```groovy
import static io.github.xiaomisum.ryze.MagicBox.*

def result = suite("API测试套件") {
    variables([
        baseUrl: "https://api.example.com"
    ])
    
    children {
        http {
            title "获取用户列表"
            method "GET"
            url '${baseUrl}/users'
            assertion {
                json '$.length', 10, ">="
            }
        }
    }
}
```

**Java 函数式方式**:
```java
import static io.github.xiaomisum.ryze.MagicBox.*;

TestSuiteResult result = suite("API测试套件", builder -> {
    builder.variables(Map.of("baseUrl", "https://api.example.com"));
    builder.children(children -> {
        children.http(http -> http
            .title("获取用户列表")
            .method("GET")
            .url("${baseUrl}/users")
            .assertion(assertion -> assertion
                .json("$.length", 10, ">=")
            )
        );
    });
});
```

##### http() 方法组

**描述**: 创建和执行单个 HTTP 测试

**示例**:
```java
// 简单 HTTP 请求
Result result = http(builder -> builder
    .method("GET")
    .url("https://api.example.com/users/1")
    .assertion(assertion -> assertion
        .json("$.id", 1, "==")
    )
);

// 带标题的 HTTP 请求
Result result = http("获取用户信息", builder -> builder
    .method("GET")
    .url("https://api.example.com/users/1")
    .header("Authorization", "Bearer token")
    .assertion(assertion -> assertion
        .json("$.name", "", "isNotEmpty")
    )
);
```

### 3. TestElement - 测试元件基类

所有测试组件的基础接口。

#### 接口定义

```java
public interface TestElement<T extends Result> extends Validatable, Cloneable<TestElement<T>> {
    default T run(SessionRunner session);
    default TestElement<T> copy();
    default ValidateResult validate();
}
```

#### 核心方法

##### run(SessionRunner session)

**描述**: 执行测试元件

**参数**:
- `session` (SessionRunner): 会话运行器，提供执行上下文

**返回值**: `T extends Result` - 测试执行结果

##### copy()

**描述**: 创建测试元件的副本，用于解决线程安全问题

**返回值**: `TestElement<T>` - 元件副本

##### validate()

**描述**: 验证测试元件配置的有效性

**返回值**: `ValidateResult` - 验证结果

### 4. Sampler - 取样器接口

执行具体测试操作的组件。

#### 接口定义

```java
public interface Sampler<T extends Result> extends TestElement<T> {
    // 继承 TestElement 的所有方法
}
```

#### 实现类

##### HTTPSampler

**描述**: HTTP 协议取样器

**构建方式**:
```java
HTTPSampler sampler = HTTPSampler.builder()
    .title("用户登录")
    .method("POST")
    .url("https://api.example.com/login")
    .header("Content-Type", "application/json")
    .bodyAsJson(Map.of(
        "username", "testuser",
        "password", "password123"
    ))
    .assertion(assertion -> assertion
        .json("$.code", 200, "==")
        .json("$.token", "", "isNotEmpty")
    )
    .extractor(extractor -> extractor
        .json("$.token", "authToken")
    )
    .build();
```

**主要方法**:
- `method(String method)`: 设置 HTTP 方法
- `url(String url)`: 设置请求 URL
- `header(String name, String value)`: 添加请求头
- `headers(Map<String, String> headers)`: 批量设置请求头
- `bodyAsJson(Object body)`: 设置 JSON 请求体
- `bodyAsForm(Map<String, Object> form)`: 设置表单请求体
- `query(String name, String value)`: 添加查询参数

##### DubboSampler

**描述**: Dubbo RPC 协议取样器

**构建方式**:
```java
DubboSampler sampler = DubboSampler.builder()
    .title("用户服务调用")
    .interfaceName("com.example.service.UserService")
    .methodName("getUserById")
    .parameterTypes(new String[]{"java.lang.Long"})
    .parameters(new Object[]{1L})
    .assertion(assertion -> assertion
        .json("$.id", 1, "==")
    )
    .build();
```

### 5. Assertion - 断言验证器

验证测试结果是否符合预期。

#### 核心接口

```java
public interface Assertion extends TestElement<AssertionResult> {
    AssertionResult run(SessionRunner session);
}
```

#### 实现类

##### JSONAssertion

**描述**: JSON 数据断言验证器

**构建方式**:
```java
JSONAssertion assertion = JSONAssertion.builder()
    .field("$.data.id")        // JSONPath 表达式
    .expected(123)             // 期望值
    .rule("==")                // 验证规则
    .build();
```

**支持的验证规则**:
- `==`, `equals`: 相等性验证
- `!=`, `not`: 不相等验证  
- `contains`, `ct`: 包含性验证
- `>`, `>=`, `<`, `<=`: 数值比较
- `isEmpty`, `isNotEmpty`: 空值验证
- `regex`: 正则表达式验证

##### HTTPAssertion

**描述**: HTTP 响应断言验证器

**构建方式**:
```java
HTTPAssertion assertion = HTTPAssertion.builder()
    .field("statusCode")       // HTTP 字段
    .expected(200)             // 期望值
    .rule("==")                // 验证规则
    .build();
```

**支持的字段**:
- `statusCode`: HTTP 状态码
- `responseTime`: 响应时间
- `responseSize`: 响应大小
- `headers.HeaderName`: 响应头

### 6. Extractor - 数据提取器

从测试结果中提取数据供后续使用。

#### 核心接口

```java
public interface Extractor extends TestElement<ExtractResult> {
    ExtractResult run(SessionRunner session);
}
```

#### 实现类

##### JSONExtractor

**描述**: JSON 数据提取器

**构建方式**:
```java
JSONExtractor extractor = JSONExtractor.builder()
    .field("$.data.token")     // JSONPath 表达式
    .refName("authToken")      // 变量名
    .defaultValue("")          // 默认值
    .build();
```

##### RegexExtractor

**描述**: 正则表达式数据提取器

**构建方式**:
```java
RegexExtractor extractor = RegexExtractor.builder()
    .field("token\":\"([^\"]+)\"")  // 正则表达式
    .refName("authToken")           // 变量名
    .matchNumber(1)                 // 匹配组号
    .build();
```

##### ResultExtractor

**描述**: 完整结果提取器

**构建方式**:
```java
ResultExtractor extractor = ResultExtractor.builder()
    .refName("fullResponse")   // 变量名
    .build();
```

### 7. Processor - 处理器

在测试执行前后进行数据处理。

#### 核心接口

```java
public interface Preprocessor extends TestElement<Result> {
    // 前置处理器，在主测试前执行
}

public interface Postprocessor extends TestElement<Result> {
    // 后置处理器，在主测试后执行
}
```

#### 使用示例

```java
HTTPSampler sampler = HTTPSampler.builder()
    .title("需要认证的接口")
    .method("GET")
    .url("https://api.example.com/protected")
    // 前置处理器：获取认证令牌
    .preprocessor(pre -> pre
        .http(auth -> auth
            .title("获取访问令牌")
            .method("POST")
            .url("https://api.example.com/auth")
            .extractor(ext -> ext
                .json("$.token", "accessToken")
            )
        )
    )
    // 使用提取的令牌
    .header("Authorization", "Bearer ${accessToken}")
    // 后置处理器：清理数据
    .postprocessor(post -> post
        .http(cleanup -> cleanup
            .method("DELETE")
            .url("https://api.example.com/cleanup")
        )
    )
    .build();
```

### 8. SessionRunner - 会话执行器

管理测试执行的上下文和生命周期。

#### 类定义

```java
public class SessionRunner {
    public static SessionRunner getSessionIfNoneCreateNew();
    public static void newSession(Configure configure);
    public static void removeSession();
    
    public ContextWrapper getContext();
    public Map<String, Object> getStorage();
    public Configure getConfigure();
}
```

#### 核心方法

##### getSessionIfNoneCreateNew()

**描述**: 获取当前线程的会话，如果不存在则创建新会话

**返回值**: `SessionRunner` - 会话执行器实例

##### getContext()

**描述**: 获取当前执行上下文

**返回值**: `ContextWrapper` - 上下文包装器

##### getStorage()

**描述**: 获取会话级别的数据存储

**返回值**: `Map<String, Object>` - 存储映射

**使用示例**:
```java
SessionRunner session = SessionRunner.getSessionIfNoneCreateNew();

// 存储数据
session.getStorage().put("testData", someData);

// 获取数据
Object data = session.getStorage().get("testData");

// 获取变量
String token = session.getContext().getLocalVariablesWrapper().get("authToken");
```

### 9. Configure - 配置管理

管理框架的全局配置。

#### 类定义

```java
public class Configure {
    public static Configure defaultConfigure();
    
    public TemplateEngine getTemplateEngine();
    public void setTemplateEngine(TemplateEngine templateEngine);
    
    public List<RyzeInterceptor> getBuiltinInterceptors();
    public GlobalContext getGlobalContext();
}
```

#### 使用示例

```java
// 创建自定义配置
Configure configure = Configure.defaultConfigure();

// 禁用 Allure 报告
Configure configure = new Configure(false);

// 自定义模板引擎
configure.setTemplateEngine(new CustomTemplateEngine());

// 添加自定义拦截器
configure.getBuiltinInterceptors().add(new CustomInterceptor());
```

## 🔧 高级用法

### 1. 变量和函数

#### 内置函数

```java
// 时间戳函数
"timestamp": "${__timestamp()}"

// 随机数函数  
"randomId": "${__random(1000, 9999)}"

// 随机字符串函数
"randomString": "${__randomString(10, 'alphanumeric')}"

// UUID 函数
"uuid": "${__uuid()}"

// Base64 编码
"encoded": "${__base64encode('text')}"

// MD5 哈希
"hash": "${__md5('text')}"
```

#### 自定义函数

```java
@KW("customFunction")
public class CustomFunction implements Function {
    @Override
    public String apply(Args args, ContextWrapper context) {
        // 自定义函数逻辑
        return "custom result";
    }
}
```

### 2. 自定义断言规则

```java
@KW("customRule")
public class CustomRule implements Rule {
    @Override
    public boolean assertThat(Object actual, Object expected) {
        // 自定义验证逻辑
        return customValidation(actual, expected);
    }
}
```

### 3. 拦截器使用

```java
public class CustomInterceptor implements RyzeInterceptor {
    @Override
    public void beforeTest(TestElement element) {
        // 测试前处理
    }
    
    @Override
    public void afterTest(TestElement element, Result result) {
        // 测试后处理
    }
}
```

## ⚠️ 注意事项

### 1. 线程安全

- `TestElement` 实例非线程安全，多线程使用时需要调用 `copy()` 方法
- `SessionRunner` 使用 ThreadLocal 管理，每个线程有独立实例
- 共享配置和缓存使用读写锁保护

### 2. 内存管理

- 及时调用 `SessionRunner.removeSession()` 清理线程本地数据
- 大量数据测试时注意上下文数据的清理
- 合理设置对象生命周期

### 3. 错误处理

- 所有 API 方法都可能抛出 `RuntimeException`
- 使用 `ValidateResult` 检查配置有效性
- 测试结果中包含详细的错误信息

## 📚 参考资源

- [快速开始指南](./QuickStart.md)
- [架构设计文档](./Architecture.md)
- [协议模块指南](./Protocols.md)
- [GitHub Issues](https://github.com/XiaoMiSum/ryze/issues)

---

本文档持续更新中，如有疑问或建议，欢迎提交 Issue 或 Pull Request。