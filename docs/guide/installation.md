# 安装指南

本指南将帮助您在项目中安装和配置Ryze测试框架。

## 系统要求

- Java 21或更高版本
- Maven 3.8+ 或 Gradle 7.0+

## Maven安装

在您的`pom.xml`中添加以下依赖：

### 核心模块

```xml
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze</artifactId>
    <version>6.0.1</version>
</dependency>
```

### 协议模块

根据需要添加相应的协议模块：

```xml
<!-- Dubbo协议支持 -->
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-dubbo</artifactId>
    <version>6.0.1</version>
</dependency>

<!-- Kafka协议支持 -->
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-kafka</artifactId>
    <version>6.0.1</version>
</dependency>

<!-- MongoDB协议支持 -->
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-mongo</artifactId>
    <version>6.0.1</version>
</dependency>

<!-- RabbitMQ协议支持 -->
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-rabbit</artifactId>
    <version>6.0.1</version>
</dependency>

<!-- ActiveMQ协议支持 -->
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-active</artifactId>
    <version>6.0.1</version>
</dependency>
```

## Gradle安装

在您的`build.gradle`中添加以下依赖：

### 核心模块

```gradle
implementation 'io.github.xiaomisum:ryze:6.0.1'
```

### 协议模块

```gradle
// Dubbo协议支持
implementation 'io.github.xiaomisum:ryze-dubbo:6.0.1'

// Kafka协议支持
implementation 'io.github.xiaomisum:ryze-kafka:6.0.1'

// MongoDB协议支持
implementation 'io.github.xiaomisum:ryze-mongo:6.0.1'

// RabbitMQ协议支持
implementation 'io.github.xiaomisum:ryze-rabbit:6.0.1'

// ActiveMQ协议支持
implementation 'io.github.xiaomisum:ryze-active:6.0.1'
```

## IDE配置

### IntelliJ IDEA

1. 确保已安装Java 21 SDK
2. 导入Maven或Gradle项目
3. 在Project Structure中设置Project SDK为Java 21

### Eclipse

1. 安装Eclipse JDT for Java 21
2. 导入Maven或Gradle项目
3. 配置项目的Java Build Path使用Java 21

## 验证安装

创建一个简单的测试类来验证安装是否成功：

```java
import static io.github.xiaomisum.ryze.MagicBox.*;

public class InstallationTest {
    public static void main(String[] args) {
        var result = http(builder -> {
            builder.title("安装验证测试")
                   .config(config -> config
                       .method("GET")
                       .url("https://httpbin.org/get")
                   )
                   .assertions(assertions -> assertions
                       .httpStatus(200)
                   );
        });
        
        System.out.println("安装验证" + (result.isSuccess() ? "成功" : "失败"));
    }
}
```

## 常见问题

### 1. 依赖冲突

如果遇到依赖冲突，可以排除冲突的依赖：

```xml
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze</artifactId>
    <version>6.0.1</version>
    <exclusions>
        <exclusion>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 2. Java版本问题

确保使用Java 21或更高版本：

```bash
java -version
# 应该显示Java 21或更高版本
```

### 3. 编译错误

如果遇到编译错误，请检查：

1. Java版本是否正确
2. Maven/Gradle版本是否符合要求
3. 依赖是否正确添加

## 下一步

- 阅读[快速开始指南](./quick-start.md)创建您的第一个测试
- 了解[核心概念](./concepts/test-suite.md)
- 探索[协议支持](./protocols/http.md)