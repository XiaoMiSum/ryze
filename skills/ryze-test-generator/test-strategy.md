# 测试用例生成策略

## 核心原则

**每个接口必须生成多个测试用例YAML文件**，覆盖以下测试场景：

| 测试类型 | 用例数量 | 文件命名模式 | 说明 |
|---------|---------|------------|------|
| 正常场景 | 1-3个 | `<api>-happy-path.yaml` | 标准业务流程 |
| 异常场景 | 3-5个 | `<api>-error-*.yaml` | 参数错误、认证失败、业务异常 |
| 边界值 | 2-4个 | `<api>-boundary-*.yaml` | 最大值、最小值、空值 |
| 等价类 | 2-4个 | `<api>-equivalence-*.yaml` | 有效/无效等价类 |

## 正常场景测试 (happy-path)

### 测试要点

- 使用合法参数值
- 验证HTTP 200状态码
- 验证响应结构完整性
- 验证业务逻辑正确性
- 提取后续接口需要的变量

### 示例

```yaml
# user/login-happy-path.yaml
title: 用户登录_正常场景
variables:
  username: admin
  password: admin123
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
children:
  - title: 用户登录_正常场景
    testclass: http_sampler
    config:
      method: post
      path: /auth/login
      body:
        username: ${username}
        password: ${password}
    extractors:
      - { testclass: json, field: '$.data.token', ref_name: auth_token }
      - { testclass: json, field: '$.data.user_id', ref_name: user_id }
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.code', expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.token', expected: '', rule: isNotEmpty }
      - { testclass: json_assertion, field: '$.data.user_id', expected: '', rule: isNotEmpty }
```

## 异常场景测试 (error-scenarios)

### 测试要点

- **参数缺失**: 移除必填字段
- **参数类型错误**: 字符串传数字、数字传字符串
- **参数格式错误**: 邮箱、手机号、日期格式错误
- **参数范围错误**: 超出允许范围的值
- **认证失败**: 无token、token过期、token无效
- **权限不足**: 普通用户访问管理员接口
- **资源不存在**: 查询不存在的ID
- **重复操作**: 重复创建、重复提交

### 示例

```yaml
# user/login-error-no-password.yaml
title: 用户登录_缺少密码
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
children:
  - title: 用户登录_缺少密码
    testclass: http_sampler
    config:
      method: post
      path: /auth/login
      body:
        username: admin
    validators:
      - { testclass: http_assertion, field: status, expected: 400, rule: == }
      - { testclass: json_assertion, field: '$.code', expected: 400, rule: == }
```

## 边界值测试 (boundary-values)

### 测试要点

- **字符串**: 空字符串、最大长度、超最大长度
- **数值**: 最小值、最大值、0、负数、小数
- **数组**: 空数组、最大长度数组、超最大长度
- **分页**: 第0页、第1页、超大页码、size=0、size=超大值
- **日期**: 过去日期、未来日期、今天、非法日期

### 示例

```yaml
# user/login-boundary-max-username-length.yaml
title: 用户登录_用户名最大长度
variables:
  max_username: "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
children:
  - title: 用户登录_用户名最大长度
    testclass: http_sampler
    config:
      method: post
      path: /auth/login
      body:
        username: ${max_username}
        password: admin123
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
```

## 等价类测试 (equivalence-classes)

### 测试要点

- **有效等价类**: 每类选一个典型值
- **无效等价类**: 每类选一个典型值
- **组合法**: 多个有效等价类组合

### 示例

```yaml
# user/login-equivalence-valid-formats.yaml
title: 用户登录_有效邮箱格式
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
children:
  - title: 用户登录_有效邮箱格式
    testclass: http_sampler
    config:
      method: post
      path: /auth/login
      body:
        username: "user@example.com"
        password: admin123
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
```

## 文件组织结构

```
test-resources/
├── 测试用例/
│   ├── user/
│   │   ├── login-happy-path.yaml
│   │   ├── login-error-no-password.yaml
│   │   ├── login-error-wrong-password.yaml
│   │   ├── login-error-account-locked.yaml
│   │   ├── login-boundary-max-username-length.yaml
│   │   ├── login-boundary-empty-username.yaml
│   │   ├── login-equivalence-valid-formats.yaml
│   │   └── login-equivalence-invalid-formats.yaml
│   ├── create-user-happy-path.yaml
│   ├── create-user-error-duplicate-email.yaml
│   └── get-user-happy-path.yaml
└── order/
    ├── create-order-happy-path.yaml
    ├── create-order-error-insufficient-stock.yaml
    └── ...
```

## 命名规范

- 格式: `<接口功能>_<测试场景>.yaml`
- 使用小写字母和连字符
- 测试场景标识:
  - `happy-path`: 正常场景
  - `error-*`: 异常场景（如 `error-no-password`）
  - `boundary-*`: 边界值（如 `boundary-max-length`）
  - `equivalence-*`: 等价类（如 `equivalence-valid`）
