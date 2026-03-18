# 🔧 前置处理器

前置处理器是 Ryze 框架中在测试组件执行前运行的特殊组件，用于执行预处理逻辑，如数据准备、环境初始化、认证获取等。

## 🔍 基本概念

### 作用原理

前置处理器在测试组件（取样器、测试集合等）执行前自动运行，可以执行各种预处理逻辑，为后续的测试执行准备必要的条件。

### 配置位置

前置处理器可以配置在：

- **测试集合中**：在测试集合执行前运行
- **取样器中**：在取样器执行前运行
- **模块级集合**：在模块测试前运行
- **项目级集合**：在整个项目测试前运行

## 🎯 设计理念

### 测试准备自动化

前置处理器的设计理念是实现测试准备的自动化：

- **数据准备**：自动创建测试所需的数据
- **环境初始化**：确保测试环境处于预期状态
- **认证获取**：自动获取测试所需的认证信息
- **资源预分配**：预先分配测试所需的资源

### 协议无关性

前置处理器设计为协议无关的组件，不同协议的前置处理器具有相似的结构和使用方式：

```yaml
# 通用前置处理器结构
- testclass: 协议类型
  async: true # 是否异步执行
  condition: '${1<2}' # 条件表达式 freemarker 模板
  config:
  # 协议特定配置项
```

## ⏱️ 执行时机

### 执行顺序

前置处理器遵循严格的执行顺序：

1. **项目级前置处理器**：最先执行
2. **模块级前置处理器**：其次执行
3. **集合级前置处理器**：然后执行
4. **取样器级前置处理器**：最后执行

### 执行上下文

前置处理器在执行时具有以下特点：

- **变量继承**：可以访问父级定义的变量
- **配置继承**：可以继承配置元件的配置
- **结果传递**：可以将处理结果通过变量传递给后续组件

```yaml
title: 测试集合
variables:
  user_id: 12345

# 项目级前置处理器
preprocessors:
  - testclass: http
    config:
      method: POST
      path: /auth/login
      body:
        username: admin
        password: password
    extractors:
      # 提取认证token供后续使用
      - { testclass: json, field: '$.data.token', ref_name: auth_token }

children:
  - title: 用户操作测试
    # 模块级前置处理器
    preprocessors:
      - testclass: http
        config:
          method: POST
          path: /users
          headers:
            Authorization: Bearer ${auth_token}  # 使用上级提取的token
          body:
            name: Test User
        extractors:
          # 提取新创建的用户ID
          - { testclass: json, field: '$.data.id', ref_name: new_user_id }

    children:
      - testclass: http
        # 取样器级前置处理器
        preprocessors:
          - testclass: http
            config:
              method: PUT
              path: /users/${new_user_id}/profile
              headers:
                Authorization: Bearer ${auth_token}
              body:
                status: active
        # 主要测试逻辑
        config:
          method: GET
          path: /users/${new_user_id}
          headers:
            Authorization: Bearer ${auth_token}
```

## 🌐 各协议前置处理器

### 🌐 HTTP 前置处理器

用于执行 HTTP 请求作为前置处理逻辑。

**配置模板**

```yaml
- testclass: http
  config:
    method: post
    base_url: http://localhost:8080
    http/2: false
    path: /user
    headers:
      h1: 1
    query: { }
    data: { }
    body: { userName: 'ryze', password: '123456qq' }
```

**相关文档**：[HTTP 协议文档](/guide/protocols/http)

### 🗄️ JDBC 前置处理器

用于执行数据库操作作为前置处理逻辑。

**配置模板**

```yaml
- testclass: jdbc
  config:
    sql: "INSERT INTO users (name, email) VALUES ('testuser', 'test@example.com')"
```

**相关文档**：[JDBC 协议文档](/guide/protocols/jdbc)

### 🗃️ Redis 前置处理器

用于执行 Redis 操作作为前置处理逻辑。

**配置模板**

```yaml
- testclass: redis
  config:
    command: SET
    key: test_key
    value: test_value
```

**相关文档**：[Redis 协议文档](/guide/protocols/redis)

### 🔌 Dubbo 前置处理器

用于执行 Dubbo 服务调用作为前置处理逻辑。

**配置模板**

```yaml
- testclass: dubbo
  config:
    interface: com.example.UserService
    method: createUser
    parameters:
      - "testuser"
      - "test@example.com"
```

**相关文档**：[Dubbo 协议文档](/guide/protocols/dubbo)

### 🚀 Kafka 前置处理器

用于发送 Kafka 消息作为前置处理逻辑。

**配置模板**

```yaml
- testclass: kafka
  config:
    topic: user-events
    key: user-created
    value: '{"userId": 12345, "action": "create"}'
```

**相关文档**：[Kafka 协议文档](/guide/protocols/kafka)

### 🐰 RabbitMQ 前置处理器

用于发送 RabbitMQ 消息作为前置处理逻辑。

**配置模板**

```yaml
- testclass: rabbit
  config:
    exchange: user-exchange
    routing_key: user.created
    message: '{"userId": 12345, "timestamp": "${timestamp()}"}'
```

**相关文档**：[RabbitMQ 协议文档](/guide/protocols/rabbitmq)

### 🎯 ActiveMQ 前置处理器

用于发送 ActiveMQ 消息作为前置处理逻辑。

**配置模板**

```yaml
- testclass: active
  config:
    destination: user-queue
    message: '{"userId": 12345, "action": "create"}'
```

**相关文档**：[ActiveMQ 协议文档](/guide/protocols/activemq)

### 🍃 MongoDB 前置处理器

用于执行 MongoDB 操作作为前置处理逻辑。

**配置模板**

```yaml
- testclass: mongo
  config:
    operation: insert
    collection: users
    document: '{"name": "testuser", "email": "test@example.com"}'
```

**相关文档**：[MongoDB 协议文档](/guide/protocols/mongodb)

## 🔧 使用场景

### 认证令牌获取

在测试前获取认证令牌：

```yaml
title: 需要认证的API测试
preprocessors:
  # 获取认证令牌
  - testclass: http
    config:
      method: POST
      path: /auth/login
      body:
        username: testuser
        password: password123
    extractors:
      - { testclass: json, field: '$.data.token', ref_name: auth_token }

children:
  - testclass: http
    config:
      method: GET
      path: /user/profile
      headers:
        Authorization: Bearer ${auth_token}  # 使用获取的令牌
```

### 测试数据准备

在测试前准备必要的测试数据：

```yaml
title: 用户订单测试
preprocessors:
  # 创建测试用户
  - testclass: http
    config:
      method: POST
      path: /users
      body:
        name: Test User
        email: test@example.com
    extractors:
      - { testclass: json, field: '$.data.id', ref_name: user_id }

  # 为用户创建初始订单
  - testclass: http
    config:
      method: POST
      path: /users/${user_id}/orders
      body:
        product_id: 12345
        quantity: 2
    extractors:
      - { testclass: json, field: '$.data.order_id', ref_name: order_id }

children:
  - testclass: http
    config:
      method: GET
      path: /orders/${order_id}
      # 测试订单详情
```

### 环境状态初始化

在测试前初始化环境状态：

```yaml
title: 库存管理系统测试
preprocessors:
  # 清理测试数据
  - testclass: jdbc
    config:
      sql: "DELETE FROM inventory WHERE product_id LIKE 'TEST_%'"

  # 初始化测试库存
  - testclass: jdbc
    config:
      sql: |
        INSERT INTO inventory (product_id, quantity, reserved) VALUES
        ('TEST_001', 100, 0),
        ('TEST_002', 50, 0),
        ('TEST_003', 200, 0)

children:
  - title: 库存扣减测试
    # 执行库存扣减操作测试
```

### 依赖服务准备

在测试前准备依赖服务：

```yaml
title: 订单支付测试
preprocessors:
  # 确保支付服务可用
  - testclass: http
    config:
      method: GET
      path: /health

  # 预先创建支付账户
  - testclass: http
    config:
      method: POST
      path: /accounts
      body:
        user_id: ${user_id}
        balance: 1000.00
    extractors:
      - { testclass: json, field: '$.data.account_id', ref_name: account_id }

children:
  - testclass: http
    config:
      method: POST
      path: /payments
      body:
        account_id: ${account_id}
        amount: 99.99
```

## 📚 最佳实践

### 处理器分层原则

#### 项目级处理器

- **全局初始化**：整个项目的通用初始化逻辑
- **认证获取**：获取项目级的认证信息
- **环境检查**：检查测试环境是否就绪

#### 模块级处理器

- **模块准备**：特定模块的准备工作
- **数据初始化**：模块特定的测试数据准备
- **服务检查**：检查模块依赖的服务状态

#### 用例级处理器

- **用例准备**：当前用例特定的准备工作
- **状态设置**：设置测试所需的特定状态
- **资源分配**：分配用例所需的资源

### 性能优化

#### 异步处理

对于不影响测试结果的前置处理，可以考虑异步执行：

```yaml
preprocessors:
  # 这些处理器可以异步执行
  - testclass: http
    async: true
    config:
      method: GET
      path: /service1/health

  - testclass: http
    async: true
    config:
      method: GET
      path: /service2/health
```

#### 缓存利用

对于重复的前置处理，可以考虑缓存结果：

```yaml
variables:
  # 缓存认证令牌（如果在有效期内）
  cached_token: ${get_cached_token()}

preprocessors:
  - testclass: http
    # 仅在没有缓存令牌时执行
    condition: ${cached_token == null}
    config:
      method: POST
      path: /auth/login
      body:
        username: testuser
        password: password123
    extractors:
      - { testclass: json, field: '$.data.token', ref_name: auth_token }
```

### 可维护性

#### 配置分离

将复杂的前置处理配置分离到独立的配置文件中：

```yaml
# 在单独的配置文件中定义复杂的处理器
preprocessors:
  - !include 'processors/setup_test_environment.yaml'
  - !include 'processors/create_test_data.yaml'
```

#### 模块化设计

将相关的前置处理逻辑组织成模块：

```yaml
preprocessors:
  # 用户模块初始化
  - !include 'processors/user_module_init.yaml'
  # 订单模块初始化
  - !include 'processors/order_module_init.yaml'
```

**💡 提示**：前置处理器是实现测试自动化的重要工具，合理使用可以显著提高测试效率和可靠性！