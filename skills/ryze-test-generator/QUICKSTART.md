# 快速使用指南

## 开始使用

当你需要生成Ryze测试脚本时，只需提供接口文档，AI会自动应用这个Skill。

### 最简单的用法

```
请根据这个OpenAPI文档生成Ryze测试脚本：
https://petstore.swagger.io/v2/swagger.json
```

### 提供本地文件

```
@swagger.json
请根据这个文件生成测试脚本
```

### 提供Markdown文档

```
接口文档如下，请生成Ryze测试：

# 用户API
## POST /api/login
登录接口...
```

## 生成流程

1. **解析文档** - 自动识别文档格式（OpenAPI URL/文件/Markdown）
2. **提取接口** - 提取所有接口的路径、方法、参数、响应
3. **分组组织** - 按tag或模块分组生成测试套件
4. **生成断言** - 根据响应schema自动生成验证器
5. **识别依赖** - 建立接口间的变量传递关系
6. **输出YAML** - 生成符合Ryze规范的测试脚本

## 自定义选项

在请求时可以附加以下选项：

### 指定基础URL

```
生成测试脚本，base_url使用：https://test-api.example.com
```

### 指定认证信息

```
登录接口使用以下测试数据：
- 用户名: admin
- 密码: test123
```

### 指定测试范围

```
只生成"订单管理"相关的测试用例
```

### 指定输出位置

```
生成的文件保存到：src/test/resources/api-tests/
```

## 示例对话

**用户**: 
```
请帮我根据这个Swagger文档生成测试脚本：
https://api.example.com/v3/api-docs

要求：
1. base_url使用环境变量
2. 登录接口使用 admin/admin123
3. 为所有POST接口添加数据验证
```

**AI** (应用Skill后):
```yaml
title: API测试套件
variables:
  base_url: ${Env('API_BASE_URL', 'http://localhost:8080')}
  auth_token: ""
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
      headers:
        Content-Type: application/json
        Authorization: Bearer ${auth_token}
children:
  - title: 管理员登录
    testclass: http_sampler
    config:
      method: post
      path: /auth/login
      body:
        username: admin
        password: admin123
    extractors:
      - { testclass: json, field: '$.data.token', ref_name: auth_token }
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.code', expected: 200, rule: == }
  
  # ... 更多测试用例
```

## 提示

- 提供完整的接口文档会生成更准确的测试脚本
- 如果有具体的测试数据需求，请提前说明
- 可以要求只生成特定模块的测试用例
- 生成后可以手动调整测试数据和断言

## 下一步

生成测试脚本后：

1. 将YAML文件放入项目的 `src/test/resources/` 目录
2. 根据需要修改测试数据和base_url
3. 创建Java测试类执行：

```java
@Test
@RyzeTest("测试套件/api-test.yaml")
public void apiTest(TestElement<?> element) {
    // 自动执行
}
```

4. 运行测试并查看Allure报告
