---
name: ryze-test-generator
description: Generate Ryze YAML test scripts from API documentation. Extracts endpoints, methods, parameters to create test suites with assertions and extractors.
---

# Ryze Test Script Generator

根据接口文档自动生成Ryze YAML测试脚本。

## 核心工作流

### 1. 解析接口

提取：path、method、参数、请求体、响应、认证、tags

### 2. 生成测试用例（一个接口多个场景）

**每个接口必须覆盖**：
- 正常场景 (1-3个): `happy-path.yaml`
- 异常场景 (3-5个): `error-*.yaml`
- 边界值 (2-4个): `boundary-*.yaml`
- 等价类 (2-4个): `equivalence-*.yaml`

### 3. 测试用例结构

```yaml
title: 接口功能_测试场景（TC-XXX-001）
variables:
  biz_var:
    field1: value1
configelements: !include 'common/env/env.yaml'
preprocessors:
  - !include 'common/processor/login.yaml'
children:
  - title: Step 1 - 操作描述
    testclass: http_sampler
    config:
      path: /api/endpoint
      method: post
      headers:
        Authorization: Bearer ${sso_token}
      body: ${biz_var}
    validators:
      - { testclass: http, field: 'status', expected: 200 }
      - { testclass: json, field: '$.success', expected: true }
```

### 4. 输出结构

**一个接口一个目录，一个接口一个测试类**

```
src/test/resources/
├── common/env/env.yaml
├── common/processor/login.yaml
└── 接口名/biz/
    ├── happy-path.yaml
    ├── error-xxx.yaml
    └── boundary-xxx.yaml
```

**Java测试类**：
```java
package io.github.ryze.demo.test;

public class LoginTest {
    @Test(description = "用户登录_正常场景（TC-LOGIN-001）")
    @RyzeTest("login/biz/happy-path.yaml")
    public void testLoginHappyPath(TestElement<?> el) {}
}
```

## 详细规则

- **测试策略**: [test-strategy.md](test-strategy.md)
- **生成规则**: [generation-rules.md](generation-rules.md)
- **配置参考**: [reference.md](reference.md)
- **完整示例**: [examples.md](examples.md)
- **项目搭建**: [project-setup.md](project-setup.md)

## 关键规范

### 命名规则

| 元素 | 规则 | 示例 |
|------|------|------|
| YAML目录 | 接口名称 | `login`, `getUserInfoById` |
| YAML文件 | 场景.yaml | `happy-path.yaml`, `error-no-password.yaml` |
| Java类 | 接口名Test.java | `LoginTest.java`, `GetUserInfoByIdTest.java` |
| 用例编号 | TC-接口-001 | TC-LOGIN-001 |

### 环境配置

- configelements: `!include 'common/env/env.yaml'`
- 引用环境: `ref: test_env`
- JDBC: `datasource: jdbc_test_env`

### 认证处理

- 登录: `!include 'common/processor/login.yaml'`
- Token: 提取 `sso_token`, `refresh_token`, `user_id`, `system_id`
- 使用: `Authorization: Bearer ${sso_token}`

### 断言规则

- HTTP: `{ testclass: http, field: 'status', expected: 200 }`
- JSON: `{ testclass: json, field: '$.success', expected: true }`

### 变量命名

- 业务变量: `biz_var` 嵌套对象
- Token: `sso_token`（不是 `auth_token`）
- 时间戳: `${timestamp('yyyyMMddHHmmss')}`
- 时间偏移: `${time_shift('yyyy-MM-dd','P1D')}`

### 处理器

- 通用: `common/processor/` (login, sleep)
- 专用: `接口名/processor/` (数据清理)
- 前置: preprocessors (登录、清理)
- 后置: postprocessors (sleep等待)
