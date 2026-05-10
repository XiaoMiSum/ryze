# Ryze测试脚本生成器

根据接口文档自动生成Ryze测试框架的YAML测试脚本。

## 功能特性

✅ 支持OpenAPI/Swagger 2.0/3.0规范  
✅ 支持URL自动获取和解析  
✅ 支持Markdown接口文档  
✅ 自动生成测试断言  
✅ 智能识别接口依赖  
✅ 自动提取变量传递  
✅ 模块化测试套件组织  

## 使用方法

### 方式1: 提供OpenAPI URL

```
请帮我根据这个OpenAPI文档生成Ryze测试脚本：
https://api.example.com/v3/api-docs
```

### 方式2: 提供本地Swagger文件

```
请根据这个Swagger文件生成测试脚本：
@swagger.json
```

### 方式3: 提供Markdown文档

```
这是我的接口文档，请生成对应的Ryze测试：

# 用户管理API

## 1. 用户登录
- URL: POST /api/auth/login
- 请求体: {username, password}
- 响应: {code, data: {token, user_id}, message}

## 2. 获取用户信息
- URL: GET /api/users/{userId}
- 请求头: Authorization: Bearer {token}
- 响应: {code, data: {id, name, email}, message}
```

### 方式4: 指定测试场景

```
请为订单管理系统生成测试脚本，包含：
- 创建订单
- 查询订单
- 更新订单状态
- 删除订单

接口文档：https://api.example.com/orders/swagger.json
```

## 输出示例

为单个接口生成的多个测试用例：

```
测试用例/
└── user/
    ├── login-happy-path.yaml          # 正常场景
    ├── login-error-no-password.yaml   # 异常场景-缺少密码
    ├── login-error-wrong-password.yaml # 异常场景-密码错误
    ├── login-error-account-locked.yaml # 异常场景-账号锁定
    ├── login-boundary-max-length.yaml  # 边界值-最大长度
    ├── login-boundary-empty.yaml       # 边界值-空值
    ├── login-equivalence-valid.yaml    # 等价类-有效格式
    └── login-equivalence-invalid.yaml  # 等价类-无效格式
```

单个测试用例示例：

```yaml
# login-happy-path.yaml
title: 用户登录_正常场景
variables:
  username: admin
  password: admin123
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
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
```

## 高级选项

### 指定测试数据

```
生成测试脚本时使用以下测试数据：
- 用户名: testuser
- 密码: testpass123
- 基础URL: https://test-api.example.com
```

### 指定测试分组

```
请只为"用户管理"和"订单管理"这两个tag生成测试用例
```

### 自定义断言策略

```
生成测试脚本，断言策略为：
- 只验证HTTP状态码
- 验证响应中的code字段
- 不验证具体业务数据
```

## 文件组织

生成的测试脚本按接口和测试类型组织：

```
src/test/resources/
└── 测试用例/
    ├── user/
    │   ├── login-happy-path.yaml
    │   ├── login-error-*.yaml
    │   ├── login-boundary-*.yaml
    │   ├── login-equivalence-*.yaml
    │   ├── create-user-happy-path.yaml
    │   └── ...
    └── order/
        ├── create-order-happy-path.yaml
        ├── create-order-error-*.yaml
        └── ...
```

## 注意事项

1. **每个接口生成多个用例**: 覆盖正常、异常、边界值、等价类
2. **base_url配置**: 生成的脚本使用变量引用，需要根据实际环境配置
3. **认证处理**: 自动识别Bearer Token、API Key等认证方式
4. **依赖管理**: 自动识别接口间的依赖关系（如登录后获取token）
5. **断言策略**: 对必需字段使用精确匹配，对可选字段使用非空验证
6. **变量提取**: 为后续接口依赖的字段自动生成提取器
7. **命名规范**: `<接口功能>_<测试场景>.yaml`

## 辅助工具

### OpenAPI解析脚本

```bash
# 解析OpenAPI文档为结构化JSON
python scripts/parse_openapi.py swagger.json output.json

# 查看解析结果
cat output.json
```

## 相关文档

- [SKILL.md](SKILL.md) - 核心生成规则和工作流
- [reference.md](reference.md) - 完整配置项参考
- [examples.md](examples.md) - 更多生成示例

## 常见问题

**Q: 如何处理复杂的请求体？**  
A: 根据OpenAPI schema自动生成示例数据，嵌套对象会完整保留结构。

**Q: 能处理需要认证的接口吗？**  
A: 可以，自动识别security定义并生成相应的认证头配置。

**Q: 如何自定义测试数据？**  
A: 在请求时提供具体的测试数据，或生成后手动修改body部分。

**Q: 支持哪些断言类型？**  
A: 支持HTTP状态码断言、JSONPath断言、正则断言等。

**Q: 如何处理分页接口？**  
A: 自动识别分页参数（page、size等）并生成多页测试用例。
