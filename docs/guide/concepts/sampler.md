# 📊 取样器

取样器是 Ryze 框架中执行具体测试操作的核心组件，用于向目标系统发送请求并收集响应结果。

## 🔍 基本概念

### 作用原理

取样器是测试执行的基本单元，负责向目标系统发送请求并收集响应结果。每个取样器代表一个具体的测试操作。

### 配置位置

取样器通常配置在：

- **测试集合中**：作为测试步骤执行
- **用例级集合**：执行具体的测试操作
- **测试套件**：组织多个相关的测试操作

## 🎯 设计理念

### 协议抽象化

取样器的设计理念是实现协议的抽象化：

- **统一接口**：不同协议的取样器具有相似的配置接口
- **协议封装**：隐藏协议特定的复杂性
- **可扩展性**：支持新协议的快速集成
- **互操作性**：不同协议的取样器可以在同一测试中协同工作

### 配置标准化

取样器采用标准化的配置结构：

```yaml
# 通用取样器结构
- title: 取样器描述
  testclass: 协议类型
  config:
  # 协议特定配置项
  variables:
  # 取样器级变量
  extractors:
  # 数据提取器
  validators:
  # 结果断言
  interceptors:
  # 拦截器
  preprocessors:
  # 前置处理器
  postprocessors:
  # 后置处理器
```

## ⚙️ 执行机制

### 执行流程

取样器的执行遵循以下流程：

1. **变量解析**：解析配置中的变量引用
2. **前置处理**：执行取样器级前置处理器
3. **请求发送**：根据配置发送协议请求
4. **响应接收**：接收并解析响应结果
5. **数据提取**：执行配置的数据提取器
6. **结果验证**：执行配置的断言验证
7. **后置处理**：执行取样器级后置处理器
8. **结果返回**：返回执行结果

### 执行上下文

取样器在执行时具有以下特点：

- **变量继承**：可以访问父级定义的变量
- **配置继承**：可以继承配置元件的配置
- **结果传递**：可以将执行结果通过提取器传递给后续组件

```yaml
title: 用户管理测试套件
variables:
  base_url: https://api.example.com
  user_id: 12345

configelements:
  # HTTP配置元件
  - testclass: http
    config:
      headers:
        Content-Type: application/json
        User-Agent: RyzeTestFramework

children:
  # 创建用户取样器
  - title: "创建用户"
    testclass: http
    config:
      method: POST
      path: /users
      body:
        name: Test User
        email: test@example.com
    extractors:
      # 提取新创建的用户ID
      - { testclass: json, field: '$.data.id', ref_name: new_user_id }
    validators:
      # 验证创建成功
      - { testclass: http, field: 'status', expected: 201, rule: '==' }

  # 查询用户取样器
  - title: "查询用户"
    testclass: http
    config:
      method: GET
      path: /users/${new_user_id}  # 使用上一步提取的ID
    validators:
      # 验证用户信息正确
      - { testclass: json, field: '$.data.name', expected: 'Test User', rule: '==' }

  # 更新用户取样器
  - title: "更新用户"
    testclass: http
    config:
      method: PUT
      path: /users/${new_user_id}
      body:
        name: Updated User
    validators:
      # 验证更新成功
      - { testclass: http, field: 'status', expected: 200, rule: '==' }

  # 删除用户取样器
  - title: "删除用户"
    testclass: http
    config:
      method: DELETE
      path: /users/${new_user_id}
    validators:
      # 验证删除成功
      - { testclass: http, field: 'status', expected: 204, rule: '==' }
```

## 🌐 各协议取样器

### 🌐 HTTP 取样器

用于执行 HTTP/HTTPS 请求。

**配置模板**

```yaml
title: 标准HTTP取样器
testclass: http
config:
  method: post
  base_url: http://localhost:8080
  http/2: false
  headers:
    h1: 1
  path: /user
  query: { }
  data: { }
  body: { userName: 'ryze', password: '123456qq' }
```

**相关文档**：[HTTP 协议文档](/guide/protocols/http)

### 🗄️ JDBC 取样器

用于执行数据库 SQL 操作。

**配置模板**

```yaml
title: 数据库查询取样器
testclass: jdbc
config:
  sql: "SELECT * FROM users WHERE id = ?"
  args:
    - 12345
```

**相关文档**：[JDBC 协议文档](/guide/protocols/jdbc)

### 🗃️ Redis 取样器

用于执行 Redis 命令。

**配置模板**

```yaml
title: Redis操作取样器
testclass: redis
config:
  command: GET
  key: test_key
```

**相关文档**：[Redis 协议文档](/guide/protocols/redis)

### 🔌 Dubbo 取样器

用于执行 Dubbo 服务调用。

**配置模板**

```yaml
title: Dubbo服务调用取样器
testclass: dubbo
config:
  interface: com.example.UserService
  method: getUser
  parameters:
    - 12345
```

**相关文档**：[Dubbo 协议文档](/guide/protocols/dubbo)

### 🚀 Kafka 取样器

用于发送 Kafka 消息。

**配置模板**

```yaml
title: Kafka消息发送取样器
testclass: kafka
config:
  topic: user-events
  key: user-created
  value: '{"userId": 12345, "action": "create"}'
```

**相关文档**：[Kafka 协议文档](/guide/protocols/kafka)

### 🐰 RabbitMQ 取样器

用于发送 RabbitMQ 消息。

**配置模板**

```yaml
title: RabbitMQ消息发送取样器
testclass: rabbit
config:
  exchange: user-exchange
  routing_key: user.created
  message: '{"userId": 12345, "timestamp": "${timestamp()}"}'
```

**相关文档**：[RabbitMQ 协议文档](/guide/protocols/rabbitmq)

### 🎯 ActiveMQ 取样器

用于发送 ActiveMQ 消息。

**配置模板**

```yaml
title: ActiveMQ消息发送取样器
testclass: active
config:
  destination: user-queue
  message: '{"userId": 12345, "action": "create"}'
```

**相关文档**：[ActiveMQ 协议文档](/guide/protocols/activemq)

### 🍃 MongoDB 取样器

用于执行 MongoDB 操作。

**配置模板**

```yaml
title: MongoDB操作取样器
testclass: mongo
config:
  operation: find
  collection: users
  filter: '{"_id": 12345}'
```

**相关文档**：[MongoDB 协议文档](/guide/protocols/mongodb)

## 🔧 使用场景

### API 接口测试

使用 HTTP 取样器测试 RESTful API：

```yaml
title: 用户管理API测试
children:
  # 创建用户
  - title: "创建用户"
    testclass: http
    config:
      method: POST
      path: /api/v1/users
      body:
        name: ${user_name}
        email: ${user_email}
        password: ${user_password}
    extractors:
      - { testclass: json, field: '$.data.id', ref_name: user_id }
      - { testclass: json, field: '$.data.token', ref_name: auth_token }
    validators:
      - { testclass: http, field: 'status', expected: 201, rule: '==' }
      - { testclass: json, field: '$.data.name', expected: '${user_name}', rule: '==' }

  # 查询用户
  - title: "查询用户"
    testclass: http
    config:
      method: GET
      path: /api/v1/users/${user_id}
      headers:
        Authorization: Bearer ${auth_token}
    validators:
      - { testclass: http, field: 'status', expected: 200, rule: '==' }
      - { testclass: json, field: '$.data.email', expected: '${user_email}', rule: '==' }

  # 更新用户
  - title: "更新用户"
    testclass: http
    config:
      method: PUT
      path: /api/v1/users/${user_id}
      headers:
        Authorization: Bearer ${auth_token}
      body:
        name: Updated ${user_name}
    validators:
      - { testclass: http, field: 'status', expected: 200, rule: '==' }

  # 删除用户
  - title: "删除用户"
    testclass: http
    config:
      method: DELETE
      path: /api/v1/users/${user_id}
      headers:
        Authorization: Bearer ${auth_token}
    validators:
      - { testclass: http, field: 'status', expected: 204, rule: '==' }
```

### 数据库操作测试

使用 JDBC 取样器测试数据库操作：

```yaml
title: 用户数据管理测试
children:
  # 插入用户数据
  - title: "插入用户数据"
    testclass: jdbc
    config:
      sql: "INSERT INTO users (name, email, created_at) VALUES (?, ?, ?)"
      args:
        - ${user_name}
        - ${user_email}
        - ${timestamp()}
    extractors:
      - { testclass: result, ref_name: insert_result }
    validators:
      - { testclass: result, expected: 1, rule: '==' }  # 影响行数为1

  # 查询用户数据
  - title: "查询用户数据"
    testclass: jdbc
    config:
      sql: "SELECT id, name, email FROM users WHERE email = ?"
      args:
        - ${user_email}
    extractors:
      - { testclass: json, field: '$[0].id', ref_name: user_id }
    validators:
      - { testclass: json, field: '$[0].name', expected: '${user_name}', rule: '==' }

  # 更新用户数据
  - title: "更新用户数据"
    testclass: jdbc
    config:
      sql: "UPDATE users SET name = ? WHERE id = ?"
      args:
        - "Updated ${user_name}"
        - ${user_id}
    validators:
      - { testclass: result, expected: 1, rule: '==' }  # 影响行数为1

  # 删除用户数据
  - title: "删除用户数据"
    testclass: jdbc
    config:
      sql: "DELETE FROM users WHERE id = ?"
      args:
        - ${user_id}
    validators:
      - { testclass: result, expected: 1, rule: '==' }  # 影响行数为1
```

### 消息队列测试

使用 Kafka 取样器测试消息队列：

```yaml
title: 订单处理消息测试
variables:
  order_id: ${uuid()}
  order_data: '{"orderId": "${order_id}", "productId": "PRODUCT_001", "quantity": 2}'

children:
  # 发送订单创建消息
  - title: "发送订单创建消息"
    testclass: kafka
    config:
      topic: order-events
      key: order-created-${order_id}
      value: ${order_data}
    validators:
      - { testclass: result, expected: true, rule: '==' }  # 发送成功

  # 验证订单处理结果
  - title: "验证订单处理"
    testclass: http
    config:
      method: GET
      path: /api/v1/orders/${order_id}
      # 等待订单处理完成
      interceptors:
        - RetryInterceptor:
            max_attempts: 10
            retry_delay: 1000
    validators:
      - { testclass: http, field: 'status', expected: 200, rule: '==' }
      - { testclass: json, field: '$.data.status', expected: 'processed', rule: '==' }
```

### 缓存操作测试

使用 Redis 取样器测试缓存操作：

```yaml
title: 用户会话缓存测试
variables:
  session_id: ${uuid()}
  user_data: '{"userId": 12345, "username": "testuser", "loginTime": "${timestamp()}"}'

children:
  # 设置用户会话
  - title: "设置用户会话"
    testclass: redis
    config:
      command: SET
      key: session:${session_id}
      value: ${user_data}
      expire: 3600  # 1小时过期
    validators:
      - { testclass: result, expected: 'OK', rule: '==' }

  # 获取用户会话
  - title: "获取用户会话"
    testclass: redis
    config:
      command: GET
      key: session:${session_id}
    extractors:
      - { testclass: result, ref_name: retrieved_session }
    validators:
      - { testclass: result, expected: '${user_data}', rule: '==' }

  # 验证会话过期
  - title: "验证会话过期"
    testclass: redis
    config:
      command: EXPIRE
      key: session:${session_id}
      seconds: 1  # 1秒后过期
    # 等待1秒
    variables:
      wait_time: ${time_shift('PT1S')}
    # 尝试获取已过期的会话
    children:
      - testclass: redis
        config:
          command: GET
          key: session:${session_id}
        validators:
          - { testclass: result, expected: null, rule: '==' }  # 应该返回null
```

## 📚 最佳实践

### 取样器设计原则

#### 单一职责

每个取样器应该只负责一个具体的测试操作：

```yaml
# 好的做法：每个取样器职责单一
- title: "创建用户"
  testclass: http
  config:
    method: POST
    path: /users
    # ... 配置

- title: "查询用户"
  testclass: http
  config:
    method: GET
    path: /users/${user_id}
    # ... 配置

# 避免：一个取样器做太多事情
- title: "用户CRUD操作"
  testclass: http
  # 这样的取样器难以维护和调试
```

#### 配置复用

合理使用配置元件和变量减少重复配置：

```yaml
# 使用配置元件
configelements:
  - testclass: http
    config:
      headers:
        Content-Type: application/json
        User-Agent: TestFramework/1.0

children:
  # 所有取样器继承公共配置
  - testclass: http
    config:
      method: POST
      path: /users
      # headers 自动继承
      body:
        name: Test User
```

### 性能优化

#### 并行执行

对于相互独立的取样器，可以考虑并行执行：

```yaml
# 这些取样器可以并行执行
children:
  - title: "查询用户信息"
    testclass: http
    config:
      method: GET
      path: /users/${user_id}

  - title: "查询订单信息"
    testclass: http
    config:
      method: GET
      path: /orders/${order_id}

  - title: "查询产品信息"
    testclass: http
    config:
      method: GET
      path: /products/${product_id}
```

#### 连接复用

合理配置连接池参数以提高性能：

```yaml
configelements:
  - testclass: http
    config:
      # 启用连接复用
      keep_alive: true
      max_connections: 10
      connection_timeout: 5000
      socket_timeout: 10000
```

### 可维护性

#### 命名规范

使用清晰的命名提高可读性：

```yaml
# 好的命名
- title: "创建新用户账户"
  testclass: http
  config:
    method: POST
    path: /api/v1/users

# 避免模糊的命名
- title: "测试1"
  testclass: http
  config:
    method: POST
    path: /users
```

#### 配置分离

将复杂的取样器配置分离到独立的配置文件中：

```yaml
# 在单独的配置文件中定义复杂的取样器
children:
  - !include 'samplers/user_management.yaml'
  - !include 'samplers/order_processing.yaml'
  - !include 'samplers/payment_processing.yaml'
```

#### 模块化设计

将相关的取样器组织成模块：

```yaml
children:
  # 用户管理模块
  - title: "用户管理测试"
    children:
      - !include 'samplers/user/create_user.yaml'
      - !include 'samplers/user/query_user.yaml'
      - !include 'samplers/user/update_user.yaml'
      - !include 'samplers/user/delete_user.yaml'

  # 订单管理模块
  - title: "订单管理测试"
    children:
      - !include 'samplers/order/create_order.yaml'
      - !include 'samplers/order/query_order.yaml'
      - !include 'samplers/order/update_order.yaml'
      - !include 'samplers/order/delete_order.yaml'
```

**💡 提示**：取样器是测试执行的核心组件，合理设计和使用取样器可以构建高效、可靠的测试体系！