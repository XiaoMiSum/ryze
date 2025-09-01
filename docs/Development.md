# 🛠️ Ryze 开发者指南

## 👋 欢迎

感谢您对 Ryze 项目的关注！这份指南将帮助您快速上手项目开发，无论您是想要修复 bug、添加新功能，还是改进文档。

## 🎯 贡献方式

我们欢迎各种形式的贡献：

- 🐛 **Bug 修复** - 修复已知问题
- ✨ **新功能** - 添加新的协议支持、测试组件等
- 📚 **文档改进** - 完善使用文档、API 文档、示例代码
- 🧪 **测试增强** - 增加单元测试、集成测试
- 🎨 **代码优化** - 性能优化、代码重构
- 💡 **想法分享** - 在 Issues 或 Discussions 中分享创意

## 🚀 快速开始

### 环境准备

#### 必需软件

- **JDK 21+** - [下载地址](https://adoptium.net/)
- **Maven 3.8+** - [下载地址](https://maven.apache.org/download.cgi)
- **Git** - [下载地址](https://git-scm.com/)

#### 推荐工具

- **IDE**: IntelliJ IDEA (推荐) 或 Eclipse
- **Git 客户端**: SourceTree、GitKraken 或命令行
- **Docker** (可选) - 用于运行测试依赖服务

#### 环境验证

```bash
# 检查 Java 版本
java -version
# 输出应包含 "21" 或更高版本

# 检查 Maven 版本
mvn -version
# 输出应包含 "3.8" 或更高版本

# 检查 Git 版本
git --version
```

### 项目设置

#### 1. Fork 项目

1. 访问 [Ryze GitHub 仓库](https://github.com/XiaoMiSum/ryze)
2. 点击右上角的 "Fork" 按钮
3. 选择您的 GitHub 账户

#### 2. 克隆代码

```bash
# 克隆您的 fork
git clone https://github.com/YOUR_USERNAME/ryze.git
cd ryze

# 添加上游仓库
git remote add upstream https://github.com/XiaoMiSum/ryze.git

# 验证远程仓库
git remote -v
```

#### 3. 构建项目

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 完整构建
mvn clean install
```

#### 4. IDE 配置

**IntelliJ IDEA 配置**：

1. 打开 IntelliJ IDEA
2. 选择 "Open" 并选择项目根目录
3. 等待 Maven 导入完成
4. 配置项目 SDK：
    - File → Project Structure → Project
    - 设置 Project SDK 为 Java 21
    - 设置 Language Level 为 21

**代码风格配置**：

1. 导入代码风格配置（如果有）
2. 配置格式化规则：
    - Settings → Editor → Code Style → Java
    - 设置缩进为 4 个空格
    - 设置行长度为 120 字符

## 📁 项目结构

```
ryze/
├── ryze/                          # 核心模块
│   ├── src/main/java/io/github/xiaomisum/ryze/
│   │   ├── assertion/             # 断言框架
│   │   │   ├── builtin/           # 内置断言实现
│   │   │   └── ...                # 断言接口和抽象类
│   │   ├── builder/               # 构建器模式实现
│   │   ├── config/                # 配置管理
│   │   ├── context/               # 上下文管理
│   │   │   └── variables/         # 变量管理
│   │   ├── extractor/             # 数据提取框架
│   │   │   ├── builtin/           # 内置提取器实现
│   │   │   └── ...                # 提取器接口和抽象类
│   │   ├── function/              # 函数框架
│   │   │   ├── builtin/           # 内置函数实现
│   │   │   └── ...                # 函数接口
│   │   ├── interceptor/           # 拦截器框架
│   │   │   └── report/            # 报告拦截器
│   │   ├── protocol/              # 内置协议实现
│   │   │   ├── debug/             # 调试协议
│   │   │   ├── http/              # HTTP协议
│   │   │   ├── jdbc/              # JDBC协议
│   │   │   └── redis/             # Redis协议
│   │   ├── support/               # 支持工具类
│   │   │   ├── fastjson/          # FastJSON支持
│   │   │   ├── groovy/            # Groovy支持
│   │   │   └── yaml/              # YAML支持
│   │   ├── template/              # 模板引擎
│   │   │   └── freemarker/        # FreeMarker实现
│   │   ├── testelement/           # 测试元件框架
│   │   │   ├── configure/         # 配置元件
│   │   │   ├── processor/         # 处理器
│   │   │   └── sampler/           # 取样器
│   │   └── ...                    # 核心类（ApplicationConfig、Ryze等）
│   └── src/test/java/io/github/xiaomisum/ryze/
│       ├── assertion/             # 断言测试
│       ├── function/              # 函数测试
│       └── ...                    # 其他测试
├── ryze-dubbo/                   # Dubbo 协议模块
├── ryze-kafka/                   # Kafka 协议模块
├── ryze-mongo/                   # MongoDB 协议模块
├── ryze-rabbit/                  # RabbitMQ 协议模块
├── ryze-active/                  # ActiveMQ 协议模块
├── ryze-testng/                  # TestNG 集成模块
├── example/                      # 示例模块
│   ├── http-example/
│   ├── dubbo-example/
│   └── ...
├── docs/                         # 文档目录
│   ├── QuickStart.md
│   ├── API.md
│   └── ...
└── pom.xml                       # 根 POM 文件
```

### 核心模块说明

#### ryze (核心模块)

- **`assertion/`**: 断言框架
    - `builtin/`: 内置断言实现（JSON、HTTP、Result 等）
    - `AbstractAssertion`: 断言抽象类
    - `Assertion`: 断言接口
    - `AssertionResult`: 断言结果
    - `Matchers`: 匹配器
    - `ProxyMatcher`: 代理匹配器
    - `Rule`: 规则接口

- **`builder/`**: 构建器模式实现
    - `DefaultChildrenBuilder`: 默认子元素构建器
    - `ExtensibleChildrenBuilder`: 可扩展子元素构建器
    - 各种默认和可扩展的构建器实现

- **`config/`**: 配置管理
    - `ConfigureGroup`: 配置组
    - `ConfigureItem`: 配置项接口
    - `GlobalConfigure`: 全局配置
    - `RyzeVariables`: 变量管理

- **`context/`**: 上下文管理
    - `variables/`: 变量包装器
    - `Context`: 上下文接口
    - `ContextWrapper`: 上下文包装器
    - `GlobalContext`: 全局上下文
    - `TestSuiteContext`: 测试套件上下文

- **`extractor/`**: 数据提取框架
    - `builtin/`: 内置提取器实现
    - `AbstractExtractor`: 提取器抽象类
    - `Extractor`: 提取器接口
    - `ExtractResult`: 提取结果

- **`function/`**: 函数框架
    - `builtin/`: 内置函数实现
    - `Function`: 函数接口
    - `Args`: 函数参数

- **`interceptor/`**: 拦截器框架
    - `report/`: 报告拦截器
    - `RyzeInterceptor`: 拦截器接口
    - `HandlerExecutionChain`: 拦截器执行链

- **`protocol/`**: 内置协议实现
    - `debug/`: 调试协议支持
    - `http/`: HTTP 协议支持
    - `jdbc/`: 数据库支持
    - `redis/`: Redis 支持

- **`support/`**: 支持工具类
    - `fastjson/`: FastJSON支持
    - `groovy/`: Groovy支持
    - `yaml/`: YAML支持
    - 工具类（集合操作、克隆、比较等）

- **`template/`**: 模板引擎
    - `freemarker/`: FreeMarker实现
    - `TemplateEngine`: 模板引擎接口

- **`testelement/`**: 测试元件框架
    - `configure/`: 配置元件
    - `processor/`: 处理器（前置/后置）
    - `sampler/`: 取样器
    - `AbstractTestElement`: 测试元件抽象类
    - `TestElement`: 测试元件接口
    - `TestSuite`: 测试套件

- **核心类**:
    - `ApplicationConfig`: 应用配置管理
    - `Ryze`: 框架入口类
    - `SessionRunner`: 测试执行引擎
    - `JsonTree`: JSON测试用例解析
    - `MagicBox`: 函数式API入口

#### 协议模块

每个协议模块都遵循相同的结构：

```
ryze-protocol/
├── src/main/java/io/github/xiaomisum/ryze/protocol/name/
│   ├── sampler/          # 取样器实现
│   ├── processor/        # 处理器实现
│   ├── config/          # 配置类
│   ├── builder/         # 构建器类
│   └── ProtocolMagicBox.java  # MagicBox 扩展
└── src/main/resources/META-INF/services/  # SPI 配置
```

## 🔧 开发指南

### 开发流程

#### 1. 创建功能分支

```bash
# 确保在最新的 main 分支上
git checkout main
git pull upstream main

# 创建功能分支
git checkout -b feature/your-feature-name
```

#### 2. 开发过程

1. **编写代码**
2. **编写测试**
3. **运行测试**
4. **提交代码**

```bash
# 添加文件
git add .

# 提交 (使用有意义的提交信息)
git commit -m "feat: add support for XXX protocol"

# 推送到您的 fork
git push origin feature/your-feature-name
```

#### 3. 创建 Pull Request

1. 访问您的 GitHub fork 页面
2. 点击 "Compare & pull request"
3. 填写 PR 描述
4. 等待代码审查

### 代码规范

#### Java 代码风格

1. **命名规范**：

```java
// 类名：PascalCase
public class HTTPSampler {
}

// 方法名：camelCase
public void executeRequest() {
}

// 常量：UPPER_SNAKE_CASE
public static final String DEFAULT_TIMEOUT = "30000";

// 变量：camelCase
private String baseUrl;
```

2. **注释规范**：

```java
/**
 * HTTP 取样器实现类
 *
 * <p>该类用于执行 HTTP 请求并收集响应结果。支持所有标准 HTTP 方法，
 * 包括 GET、POST、PUT、DELETE 等。</p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>发送 HTTP 请求</li>
 *   <li>处理响应数据</li>
 *   <li>支持各种认证方式</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @see Sampler
 * @since 6.0.0
 */
public class HTTPSampler implements Sampler<DefaultSampleResult> {

    /**
     * 执行 HTTP 请求
     *
     * @param session 会话运行器，提供执行上下文
     * @return 请求执行结果
     * @throws RuntimeException 当请求执行失败时抛出
     */
    @Override
    public DefaultSampleResult run(SessionRunner session) {
        // 实现逻辑...
    }
}
```

3. **错误处理**：

```java
// 使用运行时异常
public void executeRequest() {
    try {
        // 执行请求
    } catch (IOException e) {
        throw new RuntimeException("HTTP 请求执行失败", e);
    }
}
```

#### 测试编写

1. **单元测试**：

```java
class HTTPSamplerTest {

    @Test
    void shouldExecuteGetRequest() {
        // Given
        HTTPSampler sampler = HTTPSampler.builder()
                .method("GET")
                .url("https://httpbin.org/get")
                .build();

        // When
        DefaultSampleResult result = sampler.run(SessionRunner.getSessionIfNoneCreateNew());

        // Then
        assertTrue(result.isSuccess());
        assertThat(result.getResponse().bytesAsString()).contains("httpbin.org");
    }

    @Test
    void shouldHandleTimeout() {
        // Given
        HTTPSampler sampler = HTTPSampler.builder()
                .method("GET")
                .url("https://httpbin.org/delay/10")
                .timeout(1000)  // 1 秒超时
                .build();

        // When & Then
        Assert.assertThrows(RuntimeException.class, () -> {
            sampler.run(SessionRunner.getSessionIfNoneCreateNew());
        });
    }
}
```

2. **集成测试**：

```java

@Test(singleThreaded = true)
class HTTPIntegrationTest {

    @Test(dependsOnMethods = {}, priority = 1)
    void shouldCreateUser() {
        // 测试创建用户
    }

    @Test(dependsOnMethods = {"shouldCreateUser"}, priority = 2)
    void shouldGetUser() {
        // 测试获取用户
    }

    @Test(dependsOnMethods = {"shouldGetUser"}, priority = 3)
    void shouldDeleteUser() {
        // 测试删除用户
    }
}
```

### 新协议开发

#### 1. 创建协议模块

```bash
# 创建新模块目录
mkdir ryze-myprotocol
cd ryze-myprotocol

# 创建 Maven 结构
mkdir -p src/main/java/io/github/xiaomisum/ryze/protocol/myprotocol
mkdir -p src/main/resources/META-INF/services
mkdir -p src/test/java
```

#### 2. 创建 POM 文件

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

#### 3. 实现核心组件

**取样器实现**：

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
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractSampler.Builder<...>

    {
        @Override
        public MyProtocolSampler build () {
        return new MyProtocolSampler(this);
    }

        // 协议特定的构建方法
        public Builder host(String host){
        // 实现逻辑
        return self;
    }
    }
}
```

**配置类实现**：

```java
public class MyProtocolConfigureItem implements ConfigureItem<MyProtocolConfigureItem> {
    private String host;
    private int port;
    private String username;
    private String password;

    // getter/setter 方法

    @Override
    public ValidateResult validate() {
        ValidateResult result = new ValidateResult();
        if (StringUtils.isBlank(host)) {
            result.append("Host 不能为空");
        }
        if (port <= 0 || port > 65535) {
            result.append("端口号必须在 1-65535 之间");
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
        // 其他字段合并逻辑
        return merged;
    }

    @Override
    public MyProtocolConfigureItem evaluate(ContextWrapper context) {
        MyProtocolConfigureItem evaluated = this.copy();
        evaluated.host = context.eval(this.host);
        evaluated.username = context.eval(this.username);
        evaluated.password = context.eval(this.password);
        return evaluated;
    }
}
```

#### 4. 注册 SPI 服务

创建文件 `src/main/resources/META-INF/services/io.github.xiaomisum.ryze.testelement.TestElement`：

```
io.github.xiaomisum.ryze.protocol.myprotocol.sampler.MyProtocolSampler
```

#### 5. 添加到父 POM

在根目录的 `pom.xml` 中添加新模块：

```xml

<modules>
    <module>ryze</module>
    <module>ryze-dubbo</module>
    <!-- 其他模块 -->
    <module>ryze-myprotocol</module>
</modules>
```

## 🔌 扩展组件开发

Ryze 框架支持开发多种类型的扩展组件来增强测试能力。所有扩展组件都通过 SPI（Service Provider Interface）机制进行注册和加载。

### 组件类型概览

| 组件类型     | 用途                 | 开发难度  | 文档链接                                   |
|----------|--------------------|-------|----------------------------------------|
| **函数**   | 生成动态数据（时间、随机数、计算等） | ⭐⭐    | [函数开发指南](development/Functions.md)     |
| **验证器**  | 验证测试结果（断言和验证规则）    | ⭐⭐⭐   | [验证器开发指南](development/Validators.md)   |
| **拦截器**  | 横切关注点处理（日志、监控、安全等） | ⭐⭐⭐⭐  | [拦截器开发指南](development/Interceptors.md) |
| **提取器**  | 从结果中提取数据到变量        | ⭐⭐⭐   | [提取器开发指南](development/Extractors.md)   |
| **协议模块** | 支持新的测试协议           | ⭐⭐⭐⭐⭐ | [新协议开发指南](development/NewProtocol.md)  |

### 快速开始

#### 选择组件类型

根据您的需求选择合适的组件类型：

- **需要生成动态数据？** → 开发[函数](development/Functions.md)（如自定义随机数生成、数据库查询、API调用等）
- **需要自定义验证逻辑？** → 开发[验证器](development/Validators.md)（如邮箱格式验证、业务规则验证等）
- **需要监控或日志功能？** → 开发[拦截器](development/Interceptors.md)（如性能监控、请求日志、数据脱敏等）
- **需要提取特殊格式数据？** → 开发[提取器](development/Extractors.md)（如 CSV、XML、自定义格式解析等）
- **需要支持新协议？** → 开发[协议模块](development/NewProtocol.md)（如 GraphQL、gRPC、自定义 TCP 协议等）

### 文档规范

#### 1. API 文档

- 所有公共类和方法必须有 Javadoc
- 包含使用示例
- 说明参数和返回值
- 标注异常情况

#### 2. README 文档

每个协议模块都应该有 README.md：

```markdown
# Ryze MyProtocol Support

## 概述

简要描述协议和模块功能。

## 安装

```xml
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-myprotocol</artifactId>
    <version>6.0.1</version>
</dependency>
```

## 快速开始

提供简单的使用示例。

## 配置选项

详细说明所有配置参数。

## 示例

提供完整的使用示例。

```markdown

## 🧪 测试

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定模块测试
mvn test -pl ryze-http

# 运行特定测试类
mvn test -Dtest=HTTPSamplerTest

# 跳过测试
mvn install -DskipTests
```

### 测试分类

1. **单元测试**：测试单个类或方法
2. **集成测试**：测试多个组件协作
3. **端到端测试**：测试完整流程

### 测试最佳实践

1. **测试命名**：

```java
// 格式：should + 预期行为 + when + 条件
@Test
void shouldReturnSuccessResult_whenValidRequestIsExecuted() {
}

@Test
void shouldThrowException_whenTimeoutOccurs() {
}
```

2. **测试结构**：

```java

@Test
void testMethod() {
    // Given - 准备测试数据

    // When - 执行被测试的方法

    // Then - 验证结果
}
```

3. **使用断言库**：

```java
// 推荐使用 AssertJ

import static org.assertj.api.Assertions.*;

assertThat(result.isSuccess()).

isTrue();

assertThat(result.getResponse().

bytesAsString())
        .

contains("expected text")
    .

doesNotContain("error");
```

## 🚀 发布流程

### 版本号规范

采用 [语义化版本](https://semver.org/lang/zh-CN/)：

- **主版本号**：不兼容的 API 修改
- **次版本号**：向下兼容的功能性新增
- **修订号**：向下兼容的问题修正

### 发布检查清单

发布前请确保：

- [ ] 所有测试通过
- [ ] 代码审查完成
- [ ] 文档更新完整
- [ ] 版本号正确
- [ ] 变更日志更新
- [ ] 示例代码验证

## 🤝 社区

### 行为准则

我们致力于为所有参与者创造友好、包容的环境：

1. **友善和耐心**：对所有社区成员保持友善和耐心
2. **尊重差异**：尊重不同的观点和经验
3. **建设性反馈**：提供和接受建设性的批评
4. **专注社区利益**：以社区和项目的最佳利益为导向

### 沟通渠道

- **GitHub Issues**：Bug 报告和功能请求
- **GitHub Discussions**：一般讨论和问题求助
- **Pull Requests**：代码贡献和审查

### 成为维护者

积极贡献的社区成员可能被邀请成为项目维护者。维护者的职责包括：

- 审查和合并 Pull Request
- 参与技术决策
- 帮助新贡献者
- 维护项目质量

## 📋 贡献检查清单

提交 Pull Request 前请确认：

- [ ] 代码遵循项目规范
- [ ] 添加了必要的测试
- [ ] 测试全部通过
- [ ] 更新了相关文档
- [ ] 提交信息清晰明了
- [ ] PR 描述详细完整

## 🎉 致谢

感谢所有为 Ryze 项目做出贡献的开发者！每一个贡献，无论大小，都让项目变得更好。

---

**有问题？** 随时在 [Issues](https://github.com/XiaoMiSum/ryze/issues) 中提问，我们很乐意帮助您！