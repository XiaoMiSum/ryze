# ❓ Ryze 常见问题解答 (FAQ)

## 🎯 安装与配置

### Q1: 支持哪些 Java 版本？

**A**: Ryze 6.0+ 要求 **Java 21 或更高版本**。早期版本支持情况：

- Ryze 6.0+: Java 21+
- Ryze 5.x: Java 17+
- Ryze 4.x: Java 11+

推荐使用最新的 LTS 版本（Java 21）以获得最佳性能和最新特性支持。

### Q2: Maven 依赖配置有什么注意事项？

**A**: 主要注意以下几点：

1. **核心模块包含 HTTP 支持**：

```xml
<!-- 只需引入核心模块即可使用 HTTP -->
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze</artifactId>
    <version>${version}</version>
</dependency>
```

2. **协议模块按需引入**：

```xml
<!-- 只有需要时才引入 -->
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-dubbo</artifactId>
    <version>${version}</version>
</dependency>
```

3. **数据库驱动需要额外添加**：

```xml
<!-- JDBC 需要数据库驱动 -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.3.0</version>
</dependency>
```

### Q3: 如何解决依赖冲突问题？

**A**: 常见解决方案：

1. **查看依赖树**：

```bash
mvn dependency:tree
```

2. **排除冲突依赖**：

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-dubbo</artifactId>
    <version>${version}</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

3. **强制指定版本**：

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.alibaba.fastjson2</groupId>
            <artifactId>fastjson2</artifactId>
            <version>2.0.53</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

---

## 🔧 使用问题

### Q4: JSON 配置文件不生效怎么办？

**A**: 检查以下几点：

1. **文件路径是否正确**：

```java
// 相对路径（相对于 classpath）
Result result = Ryze.start("test-cases/api-test.json");

// 绝对路径
Result result = Ryze.start("/absolute/path/to/test.json");
```

2. **JSON 格式是否正确**：

```json
{
  "title": "测试标题",
  "children": [
    {
      "testclass": "http",
      "method": "GET",
      "url": "https://api.example.com/test"
    }
  ]
}
```

3. **检查控制台错误信息**：

```java
try{
Result result = Ryze.start("test.json");
}catch(
Exception e){
        e.

printStackTrace();
}
```

### Q5: 变量替换不工作怎么解决？

**A**: 常见原因和解决方案：

1. **变量语法错误**：

```json
// ❌ 错误
"url": "{baseUrl}/users"

// ✅ 正确
"url": "${baseUrl}/users"
```

2. **变量未定义**：

```json
{
  "variables": {
    "baseUrl": "https://api.example.com",
    "apiKey": "your-api-key"
  },
  "children": [
    {
      "testclass": "http",
      "url": "${baseUrl}/users",
      "headers": {
        "Authorization": "Bearer ${apiKey}"
      }
    }
  ]
}
```

3. **函数调用格式**：

```json
{
  "variables": {
    "timestamp": "${__timestamp()}",
    "randomId": "${__random(1000, 9999)}",
    "uuid": "${__uuid()}"
  }
}
```

### Q6: 断言验证失败如何调试？

**A**: 调试步骤：

1. **打印实际响应**：

```java
Result result = http(builder -> builder
        .method("GET")
        .url("https://api.example.com/users/1")
);

System.out.

println("响应内容: "+result.getResponse().

bytesAsString());
```

2. **使用正确的 JSONPath**：

```json
// 检查 JSONPath 表达式是否正确
{
  "testclass": "json",
  "field": "$.data.users[0].name",
  // 确保路径正确
  "expected": "张三",
  "rule": "=="
}
```

3. **分步验证**：

```java
// 先验证基础结构
.assertion(assertion ->assertion
        .

json("$","","isNotEmpty")          // 响应非空
    .

json("$.code",200,"==")            // 状态码
    .

json("$.data","","isNotEmpty")     // 数据存在
    .

json("$.data.name","张三","==")    // 具体值
)
```

### Q7: 提取器提取数据失败怎么办？

**A**: 检查和解决：

1. **确认提取表达式**：

```json
{
  "testclass": "json",
  "field": "$.data.token",
  "refName": "authToken",
  "defaultValue": ""
  // 设置默认值
}
```

2. **验证变量是否可用**：

```java
// 在后续步骤中使用
.header("Authorization","Bearer ${authToken}")

// 调试输出
SessionRunner session = SessionRunner.getSessionIfNoneCreateNew();
Object token = session.getContext().getLocalVariablesWrapper().get("authToken");
System.out.

println("提取的 token: "+token);
```

3. **使用 Result 提取器作为后备**：

```json
{
  "testclass": "result",
  "refName": "fullResponse"
}
```

---

## 🌐 协议特定问题

### Q8: HTTP 请求超时如何处理？

**A**: 根据项目实际支持的配置：

1. **YAML 配置方式**：

```yaml
testclass: http
config:
  method: GET
  protocol: https
  host: api.example.com
  path: /slow
  # 注意：当前版本不支持独立的超时配置
```

2. **JSON 配置方式**：

```json
{
  "testclass": "http",
  "config": {
    "method": "GET",
    "protocol": "https",
    "host": "api.example.com",
    "path": "/slow"
  }
}
```

**注意**：当前版本的HTTP协议支持的主要配置参数包括：`method`、`protocol`、`host`、`port`、`path`、`headers`、`query`、`data`、
`body`、`http/2` 等。

### Q9: Dubbo 连接失败怎么解决？

**A**: 常见问题排查：

1. **检查注册中心连接**：

```bash
# 确认 Zookeeper 运行状态
telnet localhost 2181
```

2. **验证服务提供者**：

```bash
# 检查服务是否注册
zkCli.sh -server localhost:2181
ls /dubbo/com.example.service.UserService/providers
```

3. **配置检查**：

```yaml
# Dubbo 配置
testclass: dubbo
config:
  registry:
    protocol: zookeeper
    address: localhost:2181
    version: 1.0.0
  reference:
    version: 1.0.0
    timeout: 5000
    retries: 1
    load_balance: random
  interface: com.example.service.UserService
  method: getUserById
  parameter_types:
    - java.lang.Long
  parameters:
    - 123
```

```json
{
  "testclass": "dubbo",
  "config": {
    "registry": {
      "protocol": "zookeeper",
      "address": "localhost:2181",
      "version": "1.0.0"
    },
    "reference": {
      "version": "1.0.0",
      "timeout": 5000,
      "retries": 1,
      "load_balance": "random"
    },
    "interface": "com.example.service.UserService",
    "method": "getUserById",
    "parameter_types": [
      "java.lang.Long"
    ],
    "parameters": [
      123
    ]
  }
}
```

### Q10: 数据库连接池配置建议？

**A**: 根据项目实际支持的配置参数：

```yaml
# JDBC 数据源配置
testclass: jdbc
ref_name: mysqlDefault
config:
  driver: com.mysql.cj.jdbc.Driver
  url: 'jdbc:mysql://localhost:3306/testdb?useSSL=false&serverTimezone=UTC'
  username: testuser
  password: testpass
  max_active: '5'      # 最大连接数（测试环境不需要太多）
  max_wait: '10000'    # 获取连接最大等待时间
```

```json
{
  "testclass": "jdbc",
  "ref_name": "mysqlDefault",
  "config": {
    "driver": "com.mysql.cj.jdbc.Driver",
    "url": "jdbc:mysql://localhost:3306/testdb?useSSL=false&serverTimezone=UTC",
    "username": "testuser",
    "password": "testpass",
    "max_active": "5",
    "max_wait": "10000"
  }
}
```

**注意**：当前版本支持的主要配置参数包括：`driver`、`url`、`username`、`password`、`max_active`、`max_wait` 等。

---

## 🚀 性能与最佳实践

### Q11: 如何提高测试执行效率？

**A**: 性能优化建议：

1. **合理使用连接池**：

```yaml
# 复用数据库连接
testclass: jdbc
ref_name: sharedDB
config:
  max_active: '5'
```

2. **避免不必要的断言**：

```java
// ❌ 过多断言影响性能
.assertion(assertion ->assertion
        .

json("$.field1","value1","==")
    .

json("$.field2","value2","==")
    .

json("$.field3","value3","==")
// ... 10+ 个断言
)

// ✅ 关键断言即可
        .

assertion(assertion ->assertion
        .

json("$.code",200,"==")
    .

json("$.data","","isNotEmpty")
)
```

3. **优化测试结构**：

```java
// 使用测试套件组织相关测试
MagicBox.suite("用户测试套件",suite ->{
        // 配置共享资源
        suite.

configureElements(ele ->ele.

http(http ->http
        .

config(config ->config.

host("api.example.com"))
        ));

        // 多个相关测试
        suite.

children(child ->child.

http(...));
        suite.

children(child ->child.

http(...));
        });
```

### Q12: 内存使用过高怎么优化？

**A**: 内存优化方案：

1. **及时清理会话**：

```java
try{
Result result = Ryze.start("test.json");
// 处理结果...
}finally{
        SessionRunner.

removeSession();  // 清理 ThreadLocal
}
```

2. **控制测试数据量**：

```java
// 避免一次性加载大量数据
List<TestData> data = loadTestData();
data.

stream()
    .

limit(100)  // 限制数据量
    .

forEach(this::runTest);
```

3. **JVM 参数调整**：

```bash
java -Xmx2g -Xms1g -XX:+UseG1GC -jar test-app.jar
```

### Q13: 如何进行数据驱动测试？

**A**: 数据驱动最佳实践：

1. **使用变量配置**：

```java

@Test
public void testWithVariables() {
    MagicBox.suite("数据驱动测试", suite -> {
        suite.variables("name", "testuser");
        suite.variables("email", "test@example.com");
        suite.variables("age", 25);

        suite.children(child -> child.http(http -> http
                .config(config -> config
                        .method("POST")
                        .host("api.example.com")
                        .path("/users")
                        .body(body -> {
                            body.put("name", "${name}");
                            body.put("email", "${email}");
                            body.put("age", "${age}");
                        })
                )
        ));
    });
}
```

2. **外部JSON数据文件**：

```json
{
  "title": "用户测试套件",
  "variables": {
    "baseUrl": "https://api.example.com",
    "userData": {
      "name": "testuser",
      "email": "test@example.com"
    }
  },
  "children": [
    {
      "testclass": "http",
      "config": {
        "method": "POST",
        "host": "${baseUrl}",
        "path": "/users",
        "body": "${userData}"
      }
    }
  ]
}
```

---

## 🔍 故障排查

### Q14: 如何启用详细日志？

**A**: 日志配置方法：

1. **Logback 配置** (`logback.xml`)：

```xml

<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="io.github.xiaomisum.ryze" level="DEBUG"/>
    <logger name="org.apache.http" level="DEBUG"/>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

2. **代码中启用调试**：

```java
System.setProperty("ryze.debug","true");
System.

setProperty("org.slf4j.simpleLogger.defaultLogLevel","debug");
```

### Q15: 测试报告生成失败怎么办？

**A**: 报告问题解决：

1. **检查 Allure 配置**：

```xml

<plugin>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-maven</artifactId>
    <version>2.12.0</version>
</plugin>
```

2. **禁用报告（调试时）**：

```java
Configure configure = new Configure(false);  // 禁用 Allure
SessionRunner.

newSession(configure);
```

3. **清理报告目录**：

```bash
rm -rf allure-results/
mvn clean test
mvn allure:report
```

### Q16: 网络环境限制如何处理？

**A**: 基于项目实际支持的功能：

1. **基础HTTP配置**：

```json
{
  "testclass": "http",
  "config": {
    "method": "GET",
    "protocol": "https",
    "host": "api.example.com",
    "port": 443,
    "path": "/test",
    "headers": {
      "Host": "api.example.com",
      "User-Agent": "Ryze-Test-Client"
    }
  }
}
```

2. **HTTPS 支持**：

```yaml
testclass: http
config:
  protocol: https  # 支持 HTTPS
  host: api.example.com
  port: 443
  path: /test
```

**注意**：当前版本不支持代理配置、SSL证书配置和自定义Host解析。如需这些功能，建议通过系统级别配置或升级版本。

---

## 💡 高级用法

### Q17: 如何实现自定义断言规则？

**A**: 自定义断言实现：

1. **实现 Rule 接口**：

```java

@KW("customEquals")
public class CustomEqualsRule implements Rule {
    @Override
    public boolean assertThat(Object actual, Object expected) {
        // 自定义比较逻辑
        return customCompare(actual, expected);
    }

    private boolean customCompare(Object actual, Object expected) {
        // 实现自定义比较逻辑
        return Objects.equals(normalize(actual), normalize(expected));
    }

    private Object normalize(Object value) {
        // 标准化逻辑，如去除空格、转换大小写等
        return value;
    }
}
```

2. **注册 SPI 服务**：

```
# META-INF/services/io.github.xiaomisum.ryze.assertion.Rule
com.example.CustomEqualsRule
```

2. **在测试中使用**：

```json
{
  "testclass": "json",
  "field": "$.message",
  "expected": "SUCCESS",
  "rule": "customEquals"
}
```

### Q18: 如何扩展新的协议支持？

**A**: 协议扩展步骤：

1. **实现核心接口**：

```java

@KW("myprotocol")
public class MyProtocolSampler implements Sampler<DefaultSampleResult> {
    @Override
    public DefaultSampleResult run(SessionRunner session) {
        // 协议特定的实现逻辑
        return result;
    }
}
```

2. **实现配置类**：

```java
public class MyProtocolConfigureItem implements ConfigureItem<MyProtocolConfigureItem> {
    // 协议特定的配置参数
}
```

3. **实现构建器**：

```java
public static class Builder extends AbstractSampler.Builder<...>{
        // 构建器实现
        }
```

4. **注册服务**：

```
# META-INF/services/io.github.xiaomisum.ryze.testelement.TestElement
com.example.MyProtocolSampler
```

---

## ⚠️ 注意事项

### Q19: 有哪些常见的陷阱需要避免？

**A**: 避免这些常见问题：

1. **线程安全问题**：

```java
// ❌ 多线程共享 TestElement
TestElement element = HTTPSampler.builder().build();
executor.

submit(() ->element.

run(session1));
        executor.

submit(() ->element.

run(session2));

// ✅ 使用副本
TestElement template = HTTPSampler.builder().build();
executor.

submit(() ->template.

copy().

run(session1));
        executor.

submit(() ->template.

copy().

run(session2));
```

2. **资源泄露**：

```java
// ❌ 忘记清理会话
public void test() {
    Result result = Ryze.start("test.json");
    // 测试结束但没有清理
}

// ✅ 正确清理
public void test() {
    try {
        Result result = Ryze.start("test.json");
    } finally {
        SessionRunner.removeSession();
    }
}
```

3. **变量作用域混淆**：

```java
// 理解变量的作用域层次：
// 全局变量 -> 测试套件变量 -> 测试用例变量 -> 提取器变量
```

### Q20: 升级版本时需要注意什么？

**A**: 版本升级指南：

1. **检查兼容性**：
    - 查看 [Release Notes](https://github.com/XiaoMiSum/ryze/releases)
    - 注意破坏性变更

2. **渐进式升级**：

```xml
<!-- 先升级到中间版本测试 -->
<ryze.version>5.9.9</ryze.version>
        <!-- 确认无问题后再升级到目标版本 -->
<ryze.version>${version}</ryze.version>
```

3. **测试验证**：
    - 运行现有测试套件
    - 检查已知问题
    - 验证关键功能

---

## 🆘 获取帮助

### 还有其他问题？

1. **查看文档**：
    - [快速开始指南](./QuickStart.md)
    - [API 文档](./API.md)
    - [架构设计](./Architecture.md)

2. **社区支持**：
    - [GitHub Issues](https://github.com/XiaoMiSum/ryze/issues)

3. **报告问题**：
    - 提供完整的错误信息
    - 包含可重现的示例代码
    - 说明环境信息（Java 版本、操作系统等）

4. **贡献代码**：
    - Fork 项目并提交 Pull Request
    - 参考[贡献指南](../README.md#🤝-贡献指南)

---

**本 FAQ 会持续更新，如果您遇到了文档中未涵盖的问题，欢迎提交 Issue 帮助我们完善！**