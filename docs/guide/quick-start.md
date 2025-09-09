# 🚀 Ryze 快速开始指南

本指南将帮助您在 5 分钟内快速上手 Ryze 测试框架。

## 📋 系统要求

- **Java**: 21 或更高版本
- **Maven**: 3.8+ (可选)
- **IDE**: IntelliJ IDEA、Eclipse 或 VS Code

## 🛠️ 项目初始化

### 1. 创建 Maven 项目

```bash
mvn archetype:generate \
  -DgroupId=com.example \
  -DartifactId=ryze-demo \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false
cd ryze-demo
```

### 2. 更新 pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>ryze-demo</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <ryze.version>${version}</ryze.version>
    </properties>

    <dependencies>
        <!-- Ryze 核心模块 -->
        <dependency>
            <groupId>io.github.xiaomisum</groupId>
            <artifactId>ryze</artifactId>
            <version>${ryze.version}</version>
        </dependency>

        <!-- 如果需要 Dubbo 支持 -->
        <dependency>
            <groupId>io.github.xiaomisum</groupId>
            <artifactId>ryze-dubbo</artifactId>
            <version>${ryze.version}</version>
        </dependency>

        <!-- 如果需要 TestNG 集成 -->
        <dependency>
            <groupId>io.github.xiaomisum</groupId>
            <artifactId>ryze-testng</artifactId>
            <version>${ryze.version}</version>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## 🎯 第一个测试案例

### 方式一：JSON 描述文件

1. **创建测试用例文件** `src/test/resources/api-test.json`：

```json
{
  "title": "用户API测试套件",
  "variables": {
    "host": "jsonplaceholder.typicode.com",
    "userId": "1"
  },
  "children": [
    {
      "testclass": "http",
      "title": "获取用户信息",
      "config": {
        "method": "GET",
        "protocol": "https",
        "host": "${host}",
        "api": "/users/${userId}",
        "headers": {
          "Accept": "application/json"
        }
      },
      "validators": [
        {
          "testclass": "json",
          "field": "$.id",
          "expected": 1,
          "rule": "=="
        },
        {
          "testclass": "json",
          "field": "$.name",
          "expected": "Leanne Graham",
          "rule": "=="
        },
        {
          "testclass": "json",
          "field": "$.email",
          "expected": "@",
          "rule": "contains"
        }
      ],
      "extractors": [
        {
          "testclass": "json",
          "field": "$.email",
          "ref_name": "userEmail"
        }
      ]
    },
    {
      "testclass": "http",
      "title": "获取用户文章",
      "config": {
        "method": "GET",
        "protocol": "https",
        "host": "${host}",
        "api": "/users/${userId}/posts",
        "headers": {
          "Accept": "application/json"
        }
      },
      "validators": [
        {
          "testclass": "json",
          "field": "$",
          "expected": "",
          "rule": "isNotEmpty"
        },
        {
          "testclass": "json",
          "field": "$[0].userId",
          "expected": 1,
          "rule": "=="
        }
      ]
    }
  ]
}
```

2. **创建 Java 测试类** `src/test/java/com/example/JsonTestDemo.java`：

```java
package com.example;

import io.github.xiaomisum.ryze.Ryze;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class JsonTestDemo {

    @Test
    public void testUserApi() {
        // 通过 JSON 文件执行测试
        var result = Ryze.start("api-test.json");

        // 验证测试结果
        assertTrue(result.isSuccess(), "API 测试应该成功");

        // 打印测试结果
        System.out.println("测试执行状态: " + result.getStatus());
        System.out.println("测试时长: " + result.getElapsedTime() + "ms");
        System.out.println("子测试数量: " + result.getSubResults().size());
    }
}
```

### 方式二：MagicBox 函数式编程

创建 `src/test/java/com/example/MagicBoxDemo.java`：

```java
package com.example;

import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import org.testng.annotations.Test;

import java.util.Map;

import static io.github.xiaomisum.ryze.MagicBox.*;
import static org.testng.Assert.assertTrue;

public class MagicBoxDemo {

    @Test
    public void testWithMagicBox() {
        // 使用 MagicBox 的函数式 API
        TestSuiteResult result = suite("用户API测试", builder -> {
            // 设置全局变量
            builder.variables(Map.of(
                    "host", "jsonplaceholder.typicode.com",
                    "userId", "1"
            ));

            // 添加测试用例
            builder.children(children -> {
                // HTTP 请求测试
                children.http(http -> http
                        .title("获取用户信息")
                        .method("GET")
                        .protocol("https")
                        .host("${host}")
                        .api("/users/${userId}")
                        .header("Accept", "application/json")
                        // 添加断言
                        .validators(assertion -> assertion
                                .json("$.id", 1, "==")
                                .json("$.name", "Leanne Graham", "==")
                                .json("$.email", "@", "contains")
                        )
                        // 添加提取器
                        .extractors(extractor -> extractor
                                .json("$.email", "userEmail")
                        )
                );

                // 第二个测试用例
                children.http(http -> http
                        .title("获取用户文章")
                        .method("GET")
                        .protocol("https")
                        .host("${host}")
                        .api("$/users/${userId}/posts")
                        .assertion(assertion -> assertion
                                .json("$", "", "isNotEmpty")
                                .json("$[0].userId", 1, "==")
                        )
                );
            });
        });

        // 验证结果
        assertTrue(result.isSuccess(), "API 测试应该成功");
        System.out.println("测试结果: " + result.getStatus());
    }
}
```

### 方式三：Groovy 脚本风格

创建 `src/test/groovy/ApiTestGroovy.groovy`（需要添加 Groovy 依赖）：

```groovy
import static io.github.xiaomisum.ryze.MagicBox.*

def result = suite("用户API测试") {
    variables([
            host  : "jsonplaceholder.typicode.com",
            userId: "1"
    ])

    children {
        http {
            title "获取用户信息"
            method "GET"
            protocol "https"
            api '/users/${userId}'
            header "Accept", "application/json"

            assertion {
                json '$.id', 1, "=="
                json '$.name', "Leanne Graham", "=="
                json '$.email', "@", "contains"
            }

            extractor {
                json '$.email', "userEmail"
            }
        }

        http {
            title "获取用户文章"
            method "GET"
            procotol "https"
            api '/users/${userId}/posts'

            assertion {
                json '$', "", "isNotEmpty"
                json '$[0].userId', 1, "=="
            }
        }
    }
}

println "测试结果: ${result.success}"
```

## 🏃 运行测试

### Maven 命令运行

```bash
# 编译项目
mvn compile

# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=JsonTestDemo

# 生成测试报告
mvn surefire-report:report
```

### IDE 中运行

1. 在 IntelliJ IDEA 中右键点击测试类
2. 选择 "Run JsonTestDemo"
3. 查看测试结果和控制台输出

## 📊 测试报告

Ryze 内置支持 Allure 测试报告。

### Allure 报告配置

在 `pom.xml` 中添加：

```xml

<plugin>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-maven</artifactId>
    <version>2.12.0</version>
    <configuration>
        <reportVersion>2.24.0</reportVersion>
    </configuration>
</plugin>
```

生成 Allure 报告：

```bash
mvn allure:report
mvn allure:serve
```

## 🎨 高级功能示例

### 使用变量和函数

```json
{
  "title": "动态数据测试",
  "variables": {
    "timestamp": "${__timestamp()}",
    "randomId": "${__random(1000, 9999)}",
    "randomString": "${__randomString(10)}"
  },
  "children": [
    {
      "testclass": "http",
      "title": "创建用户",
      "config": {
        "method": "POST",
        "protocol": "https",
        "host": "api.example.com",
        "api": "/users",
        "body": {
          "id": "${randomId}",
          "name": "testuser_${timestamp}",
          "email": "test_${randomString}@example.com"
        }
      }
    }
  ]
}
```

## 🔧 故障排除

### 常见问题

1. **编译错误**：确保 Java 版本为 21+
2. **依赖冲突**：检查 Maven 依赖树 (`mvn dependency:tree`)
3. **测试失败**：检查网络连接和 API 地址

## 🎉 下一步

恭喜！您已成功运行了第一个 Ryze 测试。接下来可以：

1. 📖 阅读[详细文档](../README.md#📚-详细文档)
2. 🔍 探索[各协议模块](../README.md#🧩-模块说明)
3. 🛠️ 学习[高级配置](./docs/help/)
4. 🤝 参与[社区贡献](../README.md#🤝-贡献指南)

有问题？欢迎在 [Issues](https://github.com/XiaoMiSum/ryze/issues) 中提问！