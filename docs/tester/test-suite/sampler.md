# 📌 取样器配置详解

## 🔍 基本概念

取样器（Sampler）是测试用例中执行具体测试逻辑的最小执行单元。每个取样器代表一个测试步骤，包含该步骤的配置、前后置处理器、提取器和验证器等。

### 特点

- **原子性**：每个取样器都是一个独立的可执行单元
- **灵活配置**：支持多种协议（HTTP、JDBC、Dubbo 等）
- **完整生命周期**：包含前置→执行→提取→验证→后置的完整流程
- **数据流转**：支持从前一个取样器提取数据，传递给后续取样器

## 📋 配置项详解

```yaml
# 取样器的完整配置示例
- title: 步骤1        # 取样器的描述
  testclass: http     # 取样器类型
  variables: # 步骤级变量
    username: ryze
    timeout: 5000
  config: # 取样器配置
    method: post
    path: /api/login
    body:
      userName: '${username}'
      password: '123456qq'
  extractors: # 提取器
    - testclass: json
      field: '$.status'
      ref_name: status
    - testclass: json
      field: '$.data.token'
      ref_name: auth_token
  validators: # 验证器
    - testclass: http
      field: status
      expected: 200
      rule: '=='
    - testclass: json
      field: '$.code'
      expected: 0
      rule: '=='
```

## 参数说明

### 顶层参数

| 参数             | 类型     | 必填 | 描述                                    |
|----------------|--------|----|---------------------------------------|
| title          | String | 是  | 该测试步骤的描述                              |
| testclass      | String | 是  | 取样器类型 (http/jdbc/dubbo/kafka/redis 等) |
| variables      | Object | 否  | 该步骤特定的变量，优先级最高                        |
| config         | Object | 是  | 取样器的具体配置，根据 testclass 类型而不同           |
| extractors     | Array  | 否  | 提取器列表，从响应中提取数据                        |
| validators     | Array  | 否  | 验证器列表，验证响应结果                          |
| preprocessors  | Array  | 否  | 该取样器的前置处理器                            |
| postprocessors | Array  | 否  | 该取样器的后置处理器                            |

### HTTP 取样器配置

```yaml
config:
  method: GET              # HTTP 方法 (GET/POST/PUT/DELETE/PATCH)
  protocol: https          # 协议 (http/https)
  host: api.example.com    # 主机地址
  port: 8080              # 端口（可选）
  path: /api/users        # 请求路径
  headers: # 请求头
    Content-Type: application/json
    Authorization: Bearer ${token}
  query: # 查询参数
    page: 1
    size: 10
  body: # 请求体
    name: test
    email: test@example.com
```

### JDBC 取样器配置

```yaml
config:
  datasource: main_db     # 数据源引用名称
  sql: 'SELECT * FROM users WHERE id = ?'  # SQL 语句
  args: # SQL 参数
    - 1
  timeout: 30000          # 超时时间（毫秒）
```

### Dubbo 取样器配置

```yaml
config:
  interface: com.example.UserService      # 服务接口
  method: getUser                         # 方法名
  parameter_types: # 参数类型
    - java.lang.Long
  parameters: # 参数值
    - 1
  timeout: 5000                           # 超时时间
```

### Kafka 取样器配置

```yaml
config:
  topic: user-events              # 主题
  key: user-001                   # 消息 key
  message: '{"id": 1, "name": "test"}'  # 消息内容
  partition: 0                    # 分区（可选）
```

## 提取器配置

### JSON 提取器

```yaml
extractors:
  - testclass: json
    field: '$.data.token'          # JSONPath 表达式
    ref_name: auth_token           # 变量名
```

### HTTP 响应头提取器

```yaml
extractors:
  - testclass: http_header
    field: 'Set-Cookie'            # 响应头名称
    ref_name: cookie
```

### 正则表达式提取器

```yaml
extractors:
  - testclass: regex
    field: 'token=([a-zA-Z0-9]+)'  # 正则表达式
    ref_name: token
    match_num: 0                    # 匹配组索引
```

### 响应结果提取器

```yaml
extractors:
  - testclass: result              # 提取整个响应
    ref_name: response_body
```

## 验证器配置

### HTTP 验证器

```yaml
validators:
  - testclass: http
    field: status                  # HTTP 状态码
    expected: 200
    rule: '=='                     # 比较规则
```

### JSON 验证器

```yaml
validators:
  - testclass: json
    field: '$.code'                # JSONPath 表达式
    expected: 0
    rule: '=='
```

### 响应结果验证器

```yaml
validators:
  - testclass: result
    field: response_time           # 响应时间
    expected: 1000
    rule: '<'                      # 小于 1000ms
```

## 📚 最佳实践

### 取样器设计原则

- **单一职责**：每个取样器只做一件事，若需要使用前后置处理器，则应当创建一个多步骤测试用力
- **清晰命名**：title 应该准确描述该步骤的目的
- **合理组织**：按照准备→执行→验证→清理的顺序组织
- **充分验证**：每个关键取样器都应该包含验证器

### 变量使用建议

```yaml
# 变量优先级（从低到高）
# 1. 全局变量（项目级 variables）
# 2. 模块变量（模块级 variables）
# 3. 用例变量（用例级 variables）
# 4. 步骤变量（步骤级 variables）  <- 优先级最高

# 示例：
children:
  - title: 登录
    testclass: http
    variables:
      username: admin      # 覆盖更高层的变量
    config:
      method: POST
      path: /login
      body:
        username: '${username}'  # 使用步骤级变量
```

### 提取与传递数据

```yaml
children:
  # 步骤 1：登录并提取 token
  - title: 用户登录
    testclass: http
    config:
      method: POST
      path: /login
      body:
        username: admin
        password: admin123
    extractors:
      - testclass: json
        field: '$.data.token'
        ref_name: user_token    # 保存 token

  # 步骤 2：使用 token 获取用户信息
  - title: 获取用户信息
    testclass: http
    config:
      method: GET
      path: /user/profile
      headers:
        Authorization: Bearer ${user_token}  # 使用提取的 token
    validators:
      - testclass: http
        field: status
        expected: 200
        rule: '=='
```

### 前后置处理

```yaml
children:
  - title: 删除用户
    testclass: http
    config:
      method: DELETE
      path: /users/1
    preprocessors:
      # 删除前的验证
      - testclass: jdbc
        config:
          datasource: main_db
          sql: 'SELECT COUNT(*) FROM users WHERE id = 1'
    postprocessors:
      # 删除后的清理
      - testclass: jdbc
        config:
          datasource: main_db
          sql: 'TRUNCATE TABLE user_audit_log'
```

## 常见取样器类型

| 类型        | 协议/场景         | 典型用途        |
|-----------|---------------|-------------|
| http      | HTTP/HTTPS    | REST API 测试 |
| jdbc      | 数据库           | 数据验证、数据准备   |
| dubbo     | Dubbo RPC     | 微服务接口测试     |
| kafka     | Kafka 消息队列    | 消息生产消费测试    |
| redis     | Redis         | 缓存操作测试      |
| rabbitmq  | RabbitMQ 消息队列 | 消息测试        |
| websocket | WebSocket     | 实时通信测试      |
| mongodb   | MongoDB       | NoSQL 数据库测试 |

---

## 📄 相关文档

- [📝 测试用例模板](./test-case) - 了解完整的用例结构
- [📊 测试集合（模块级）](./test-suite-module) - 学习模块级测试组织
- [🌋 测试集合（项目级）](./test-suite-project) - 学习项目级测试结构
- [📈 测试集合体系概念](../../guide/concepts/test-suite) - 深入了解测试集合体系架构

> 📌 **提示**：合理设计取样器的组织和配置，能够大大提升测试的可读性和可维护性！
