# 🌐 新协议开发指南

## 概述

Ryze 框架支持开发自定义协议模块来扩展测试能力。每个协议模块都遵循相同的架构模式和开发规范，确保与框架核心的良好集成。

## 开发流程

### 1. 创建协议模块

```bash
# 创建新模块目录
mkdir ryze-myprotocol
cd ryze-myprotocol

# 创建 Maven 结构
mkdir -p src/main/java/io/github/xiaomisum/ryze/protocol/myprotocol
mkdir -p src/main/resources/META-INF/services
mkdir -p src/test/java
```

### 2. 创建 POM 文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>io.github.xiaomisum</groupId>
        <artifactId>ryze-parent</artifactId>
        <version>6.0.1</version>
    </parent>

    <artifactId>ryze-myprotocol</artifactId>
    <name>Ryze MyProtocol Support</name>
    <description>MyProtocol support for Ryze testing framework</description>

    <dependencies>
        <dependency>
            <groupId>io.github.xiaomisum</groupId>
            <artifactId>ryze</artifactId>
        </dependency>

        <!-- 协议特定依赖 -->
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>myprotocol-client</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</project>
```

### 3. 实现核心组件

#### 取样器实现

```java

@KW("myprotocol")
public class MyProtocolSampler extends AbstractSampler<MyProtocolSampler, MyProtocolConfigureItem, DefaultSampleResult>
        implements Sampler<DefaultSampleResult> {

    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.id, runtime.title);
    }

    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        // 实现协议特定的逻辑
        try {
            // 1. 获取配置
            MyProtocolConfigureItem config = getConfiguration(context);

            // 2. 建立连接
            MyProtocolClient client = createClient(config);

            // 3. 执行请求
            MyProtocolResponse response = client.execute(createRequest(config));

            // 4. 处理响应
            result.setResponse(new MyProtocolResultResponse(response));
            result.setStatus(TestStatus.passed);

        } catch (Exception e) {
            result.setStatus(TestStatus.failed);
            result.setMessage("协议执行失败: " + e.getMessage());
            result.setThrowable(e);
        }
    }

    private MyProtocolClient createClient(MyProtocolConfigureItem config) {
        return MyProtocolClient.builder()
                .host(config.getHost())
                .port(config.getPort())
                .timeout(config.getTimeout())
                .build();
    }

    private MyProtocolRequest createRequest(MyProtocolConfigureItem config) {
        return MyProtocolRequest.builder()
                .command(config.getCommand())
                .parameters(config.getParameters())
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractSampler.Builder<Builder, MyProtocolSampler, MyProtocolConfigureItem, DefaultSampleResult> {

        public Builder() {
            super(new MyProtocolSampler());
        }

        @Override
        public MyProtocolSampler build() {
            return new MyProtocolSampler(this);
        }

        // 协议特定的构建方法
        public Builder host(String host) {
            getOrCreateConfig().setHost(host);
            return self;
        }

        public Builder port(int port) {
            getOrCreateConfig().setPort(port);
            return self;
        }

        public Builder command(String command) {
            getOrCreateConfig().setCommand(command);
            return self;
        }

        public Builder timeout(int timeout) {
            getOrCreateConfig().setTimeout(timeout);
            return self;
        }

        private MyProtocolConfigureItem getOrCreateConfig() {
            if (config == null) {
                config = new MyProtocolConfigureItem();
            }
            return config;
        }
    }
}
```

#### 配置类实现

```java
public class MyProtocolConfigureItem implements ConfigureItem<MyProtocolConfigureItem> {
    private String host;
    private int port = 8080;
    private String username;
    private String password;
    private int timeout = 30000;
    private String command;
    private Map<String, Object> parameters = new HashMap<>();

    @Override
    public ValidateResult validate() {
        ValidateResult result = new ValidateResult();

        if (StringUtils.isBlank(host)) {
            result.append("Host 不能为空");
        }

        if (port <= 0 || port > 65535) {
            result.append("端口号必须在 1-65535 之间");
        }

        if (timeout <= 0) {
            result.append("超时时间必须大于 0");
        }

        if (StringUtils.isBlank(command)) {
            result.append("命令不能为空");
        }

        return result;
    }

    @Override
    public MyProtocolConfigureItem merge(MyProtocolConfigureItem other) {
        if (other == null) return this;

        MyProtocolConfigureItem merged = this.copy();

        if (StringUtils.isNotBlank(other.host)) {
            merged.host = other.host;
        }
        if (other.port > 0) {
            merged.port = other.port;
        }
        if (StringUtils.isNotBlank(other.username)) {
            merged.username = other.username;
        }
        if (StringUtils.isNotBlank(other.password)) {
            merged.password = other.password;
        }
        if (other.timeout > 0) {
            merged.timeout = other.timeout;
        }
        if (StringUtils.isNotBlank(other.command)) {
            merged.command = other.command;
        }
        if (other.parameters != null && !other.parameters.isEmpty()) {
            merged.parameters.putAll(other.parameters);
        }

        return merged;
    }

    @Override
    public MyProtocolConfigureItem copy() {
        MyProtocolConfigureItem copy = new MyProtocolConfigureItem();
        copy.host = this.host;
        copy.port = this.port;
        copy.username = this.username;
        copy.password = this.password;
        copy.timeout = this.timeout;
        copy.command = this.command;
        copy.parameters = new HashMap<>(this.parameters);
        return copy;
    }

    @Override
    public MyProtocolConfigureItem evaluate(ContextWrapper context) {
        MyProtocolConfigureItem evaluated = this.copy();
        evaluated.host = context.eval(this.host);
        evaluated.username = context.eval(this.username);
        evaluated.password = context.eval(this.password);
        evaluated.command = context.eval(this.command);

        // 评估参数中的表达式
        Map<String, Object> evaluatedParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : this.parameters.entrySet()) {
            evaluatedParams.put(entry.getKey(), context.eval(entry.getValue()));
        }
        evaluated.parameters = evaluatedParams;

        return evaluated;
    }

    // Getter/Setter 方法
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
```

#### 响应类实现

```java
public class MyProtocolResultResponse implements ResultResponse {
    private final MyProtocolResponse originalResponse;
    private final byte[] responseBytes;
    private final Map<String, String> headers;

    public MyProtocolResultResponse(MyProtocolResponse response) {
        this.originalResponse = response;
        this.responseBytes = response.getData();
        this.headers = new HashMap<>();

        // 构建响应头信息
        headers.put("status", String.valueOf(response.getStatusCode()));
        headers.put("message", response.getStatusMessage());
        headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public byte[] bytes() {
        return responseBytes;
    }

    @Override
    public String bytesAsString() {
        return new String(responseBytes, StandardCharsets.UTF_8);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("MyProtocol Response:\n");
        sb.append("Status: ").append(headers.get("status")).append("\n");
        sb.append("Message: ").append(headers.get("message")).append("\n");
        sb.append("Data: ").append(bytesAsString()).append("\n");
        return sb.toString();
    }

    public MyProtocolResponse getOriginalResponse() {
        return originalResponse;
    }
}
```

### 4. 注册 SPI 服务

创建文件 `src/main/resources/META-INF/services/io.github.xiaomisum.ryze.testelement.TestElement`：

```
io.github.xiaomisum.ryze.protocol.myprotocol.sampler.MyProtocolSampler
```

### 5. 添加到父 POM

在根目录的 `pom.xml` 中添加新模块：

```xml

<modules>
    <module>ryze</module>
    <module>ryze-dubbo</module>
    <!-- 其他模块 -->
    <module>ryze-myprotocol</module>
</modules>
```

### 6. 创建 MagicBox 扩展

```java
public class MyProtocolMagicBox {

    /**
     * 创建 MyProtocol 测试
     */
    public static void myprotocol(String title, Consumer<MyProtocolSampler.Builder> customizer) {
        var builder = MyProtocolSampler.builder().title(title);
        customizer.accept(builder);
        MagicBox.addTestElement(builder.build());
    }

    /**
     * 创建 MyProtocol 测试（无标题）
     */
    public static void myprotocol(Consumer<MyProtocolSampler.Builder> customizer) {
        myprotocol("MyProtocol 测试", customizer);
    }

    /**
     * 创建 MyProtocol 前置处理器
     */
    public static void myprotocolPreprocessor(String title, Consumer<MyProtocolSampler.Builder> customizer) {
        var builder = MyProtocolSampler.builder().title(title);
        customizer.accept(builder);
        MagicBox.addPreprocessor(builder.build());
    }

    /**
     * 创建 MyProtocol 后置处理器
     */
    public static void myprotocolPostprocessor(String title, Consumer<MyProtocolSampler.Builder> customizer) {
        var builder = MyProtocolSampler.builder().title(title);
        customizer.accept(builder);
        MagicBox.addPostprocessor(builder.build());
    }
}
```

### 7. 编写测试

```java
import org.testng.annotations.Test;
import static org.testng.Assert.*;

class MyProtocolSamplerTest {

    @Test
    void shouldExecuteCommand() {
        // Given
        MyProtocolSampler sampler = MyProtocolSampler.builder()
                .host("localhost")
                .port(8080)
                .command("test")
                .timeout(5000)
                .build();

        // When
        DefaultSampleResult result = sampler.run(SessionRunner.getSessionIfNoneCreateNew());

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getResponse());
    }

    @Test
    void shouldHandleConnectionFailure() {
        // Given
        MyProtocolSampler sampler = MyProtocolSampler.builder()
                .host("invalid-host")
                .port(9999)
                .command("test")
                .timeout(1000)
                .build();

        // When
        DefaultSampleResult result = sampler.run(SessionRunner.getSessionIfNoneCreateNew());

        // Then
        assertFalse(result.isSuccess());
        assertNotNull(result.getThrowable());
    }
}
```

## 协议模块结构

每个协议模块都应遵循以下标准结构：

```
ryze-protocol/
├── src/main/java/io/github/xiaomisum/ryze/protocol/name/
│   ├── sampler/                    # 取样器实现
│   │   └── ProtocolSampler.java
│   ├── processor/                  # 处理器实现（可选）
│   │   ├── ProtocolPreprocessor.java
│   │   └── ProtocolPostprocessor.java
│   ├── config/                     # 配置类
│   │   └── ProtocolConfigureItem.java
│   ├── response/                   # 响应类
│   │   └── ProtocolResultResponse.java
│   ├── builder/                    # 构建器类
│   │   └── ProtocolBuilder.java
│   ├── client/                     # 客户端类（可选）
│   │   └── ProtocolClient.java
│   └── ProtocolMagicBox.java       # MagicBox 扩展
├── src/main/resources/
│   └── META-INF/services/          # SPI 配置
└── src/test/java/                  # 测试代码
```

## 开发规范

### 1. 命名规范

- **取样器**: `ProtocolNameSampler`
- **配置类**: `ProtocolNameConfigureItem`
- **响应类**: `ProtocolNameResultResponse`
- **MagicBox**: `ProtocolNameMagicBox`

### 2. 注解规范

- 使用 `@KW` 注解定义关键字
- 关键字应简洁明了，避免冲突

### 3. 异常处理

- 统一使用 `RuntimeException` 包装协议异常
- 提供清晰的错误信息
- 设置适当的测试状态

### 4. 配置验证

- 实现完整的配置验证逻辑
- 提供有意义的验证错误信息
- 支持配置合并和表达式评估

### 5. 测试覆盖

- 编写完整的单元测试
- 包含正常和异常场景
- 测试配置验证逻辑

## 协议集成

### 在主模块中使用

```java

@Test
@RyzeTest
public void testMyProtocol() {
    MagicBox.myprotocol("测试自定义协议", protocol -> {
        protocol.host("localhost")
                .port(8080)
                .command("ping")
                .timeout(5000);
    });
}
```

### 在测试套件中使用

```java

@Test
@RyzeTest
public void testProtocolSuite() {
    MagicBox.suite("协议测试套件", suite -> {
        suite.children(children -> {
            children.myprotocolPreprocessor("初始化连接", init -> init
                    .host("localhost")
                    .command("init")
            );

            children.myprotocol("执行测试命令", test -> test
                    .command("test_data")
            );

            children.myprotocolPostprocessor("清理资源", cleanup -> cleanup
                    .command("cleanup")
            );
        });
    });
}
```

## 文档要求

每个协议模块都应该包含：

1. **README.md** - 模块概述和快速开始
2. **配置文档** - 详细的配置选项说明
3. **API 文档** - 完整的 Javadoc 注释
4. **示例代码** - 实际使用示例
5. **常见问题** - FAQ 和故障排除

## 最佳实践

1. **遵循框架约定**：保持与现有协议模块的一致性
2. **完善错误处理**：提供详细的错误信息和恢复建议
3. **性能优化**：合理使用连接池和资源管理
4. **安全考虑**：妥善处理敏感信息如密码
5. **扩展性设计**：考虑未来功能扩展的需要
6. **文档完整**：提供清晰的使用文档和示例

通过遵循这些指导原则，您可以开发出高质量、易用且与 Ryze 框架良好集成的协议模块。