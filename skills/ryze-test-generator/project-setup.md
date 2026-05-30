# Ryze测试项目搭建指南

## Maven项目配置

### pom.xml 核心依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>io.github.ryze</groupId>
    <artifactId>demo-api-test</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <ryze.version>6.1.0</ryze.version>
        <testng.version>7.11.0</testng.version>
        <allure.cmd.version>2.35.1</allure.cmd.version>
        <maven.allure.version>2.16.1</maven.allure.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.github.xiaomisum</groupId>
            <artifactId>ryze</artifactId>
            <version>${ryze.version}</version>
        </dependency>

        <dependency>
            <groupId>io.github.xiaomisum</groupId>
            <artifactId>ryze-testng</artifactId>
            <version>${ryze.version}</version>
        </dependency>

        <dependency>
            <groupId>org.testng</groupId>
            <artifactId>testng</artifactId>
            <version>${testng.version}</version>
        </dependency>

        <!-- MySQL驱动（可选） -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>9.3.0</version>
        </dependency>

        <!-- 日志（可选） -->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.5.32</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>io.qameta.allure</groupId>
                <artifactId>allure-maven</artifactId>
                <version>${maven.allure.version}</version>
                <configuration>
                    <reportVersion>${allure.cmd.version}</reportVersion>
                    <resultsDirectory>${project.build.directory}/allure-results</resultsDirectory>
                    <reportDirectory>${project.build.directory}/allure-report</reportDirectory>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## 项目目录结构

```
测试项目/
├── pom.xml
├── .gitignore
├── .gitlab-ci.yml                    # CI/CD配置（可选）
├── run-allure.ps1                    # Allure报告生成脚本
├── src/
│   ├── main/
│   │   └── java/                     # 自定义函数、拦截器等
│   │       └── io/github/ryze/demo/
│   │           ├── function/         # 自定义函数
│   │           └── interceptor/      # 自定义拦截器
│   └── test/
│       ├── java/                     # TestNG测试类
│       │   └── io/github/ryze/demo/test/
│       │       ├── LoginTest.java
│       │       ├── GetUserByIdTest.java
│       │       └── CreateUserTest.java
│       └── resources/                # YAML测试脚本
│           ├── common/
│           │   ├── env/
│           │   │   └── env.yaml      # 环境配置
│           │   └── processor/
│           │       ├── login.yaml    # 登录处理器
│           │       └── sleep.yaml    # 等待处理器
│           ├── login/
│           │   └── biz/
│           │       ├── happy-path.yaml
│           │       ├── error-no-password.yaml
│           │       └── boundary-max-username.yaml
│           ├── getUserById/
│           │   └── biz/
│           │       ├── happy-path.yaml
│           │       └── error-missing-id.yaml
│           └── logback-test.xml      # 日志配置
└── target/
    ├── allure-results/               # Allure测试结果
    └── allure-report/                # Allure报告
```

## 执行方式

### 运行测试

**运行单个测试类**：

```bash
mvn test -Dtest=LoginTest
```

**运行指定测试方法**：

```bash
mvn test -Dtest=LoginTest#testLoginHappyPath
```

**运行所有测试**：

```bash
mvn test
```

### 生成Allure报告

**生成并查看报告**：

```bash
mvn allure:serve
```

**仅生成报告**：

```bash
mvn allure:report
```

**查看报告**：

```bash
# 报告位于 target/allure-report/index.html
```

### PowerShell脚本（Windows）

**run-allure.ps1**：

```powershell
# 运行测试并生成Allure报告
mvn clean test
mvn allure:serve
```

## 环境配置

### common/env/env.yaml

```yaml
# HTTP默认配置
- testclass: http
  config:
    protocol: http
    host: 127.0.0.1
    port: 8080

# JDBC数据源
- testclass: jdbc
  ref_name: jdbc_test_env
  config:
    url: jdbc:mysql://localhost:3306/test_db?useUnicode=true&characterEncoding=utf8
    username: root
    password: root123

# 测试环境HTTP配置
- testclass: http
  ref_name: test_env
  config:
    protocol: http
    host: test.example.com
    port: 80
    headers:
      systemid: 500003
      x-app-id: 511610133850107907
      x-noauth: true
      x-tenant-id: 476501800058195973
      x-user-id: 476501801144520705
```

## 处理器示例

### common/processor/login.yaml

```yaml
title: 登录系统
testclass: http
config:
  ref: test_env
  path: /common/login
  method: post
  body:
    userCode: admin
    usePwd: admin123
    loginType: 1
    systemType: "1"
extractors:
  - { testclass: json, field: $.data.ssoToken, ref_name: sso_token }
  - { testclass: json, field: $.data.refreshToken, ref_name: refresh_token }
  - { testclass: json, field: $.data.userId, ref_name: user_id }
  - { testclass: json, field: $.data.systemId, ref_name: system_id }
```

### common/processor/sleep.yaml

```yaml
title: 等待3秒
testclass: sleep
config:
  duration: 3000
```

### 接口专用处理器（示例）

**login/processor/clean_test_data.yaml**：

```yaml
title: 清理登录测试数据
testclass: jdbc
config:
  datasource: jdbc_test_env
  sql: DELETE FROM login_log WHERE user_code = 'test_user'
```

## 日志配置

### logback-test.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/test.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>

    <logger name="io.github.xiaomisum.ryze" level="DEBUG"/>
</configuration>
```

## CI/CD配置（可选）

### .gitlab-ci.yml

```yaml
stages:
  - test

api-test:
  stage: test
  image: maven:3.9-eclipse-temurin-21
  script:
    - mvn clean test
    - mvn allure:report
  artifacts:
    when: always
    paths:
      - target/allure-results/
      - target/allure-report/
    reports:
      junit:
        - target/surefire-reports/TEST-*.xml
  only:
    - main
    - develop
```

## 快速开始

1. **创建Maven项目**：
   ```bash
   mvn archetype:generate -DgroupId=io.github.ryze.demo -DartifactId=demo-api-test -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
   ```

2. **修改pom.xml**：添加Ryze依赖（见上方配置）

3. **创建目录结构**：按照上方目录结构创建

4. **配置环境**：编辑 `common/env/env.yaml`

5. **生成测试用例**：使用ryze-test-generator skill

6. **运行测试**：
   ```bash
   mvn test
   ```

7. **查看报告**：
   ```bash
   mvn allure:serve
   ```
