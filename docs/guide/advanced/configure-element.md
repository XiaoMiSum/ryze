# ⚙️ 配置元件

配置元件是 Ryze 框架中用于定义和管理测试组件基础配置的核心组件，提供了统一的配置管理和复用机制。

## 📋 目录

## 🔍 基本概念

### 作用原理

配置元件用于定义测试组件的基础配置，当测试集合中包含配置元件时，其子级测试组件会自动继承这些配置。配置元件可以显著降低测试配置的复杂度，提高配置的复用性。

### 配置位置

配置元件可以配置在：

- **测试集合中**：为整个测试集合及其子级提供基础配置
- **模块级集合**：为特定模块提供配置
- **项目级集合**：为整个项目提供全局配置

### 配置优先级

当存在多层配置时，配置优先级遵循以下规则：

1. **取样器/处理器配置** > **配置元件配置** > **默认配置**
2. **子级配置** > **父级配置**
3. **同级配置**按声明顺序，后者覆盖前者

## 🎯 设计理念

### 统一配置管理

配置元件的设计理念是实现配置的统一管理和复用：

- **降低复杂度**：通过继承机制减少重复配置
- **提高一致性**：确保相同类型的组件使用一致的基础配置
- **便于维护**：集中管理配置，修改一处影响所有继承组件
- **灵活覆盖**：允许子级组件根据需要覆盖父级配置

### 协议无关性

配置元件设计为协议无关的组件，不同协议的配置元件具有相似的结构和使用方式：

```yaml
# 通用配置元件结构
- testclass: 协议类型
  config:
  # 协议特定配置项
```

## 🔗 配置继承

### 继承机制

配置元件通过测试集合的层级结构实现继承：

```yaml
# 项目级集合
title: 项目测试集合
configelements:
  - testclass: http
    config:
      base_url: https://api.example.com
      headers:
        User-Agent: RyzeTestFramework

children:
  # 模块级集合自动继承项目级配置
  - title: 用户模块测试
    configelements:
      # 可以添加或覆盖配置
      - testclass: http
        config:
          headers:
            User-Agent: RyzeTestFramework
            X-Module: User
    children:
      # 用例级集合继承所有上级配置
      - title: 用户登录测试
        # 取样器将继承所有上级HTTP配置
        children:
          - testclass: http
            config:
              # 可以覆盖继承的配置
              path: /auth/login
```

### 配置合并

配置元件支持配置合并机制：

```yaml
# 父级配置
configelements:
  - testclass: http
    config:
      headers:
        User-Agent: RyzeTestFramework
        Content-Type: application/json

# 子级配置（合并而非替换）
children:
  - testclass: http
    config:
      headers:
        X-Request-ID: ${uuid()}  # 新增
        Content-Type: application/xml  # 覆盖
      # 最终headers:
      #   User-Agent: RyzeTestFramework
      #   Content-Type: application/xml
      #   X-Request-ID: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

## 🌐 各协议配置元件

### 🌐 HTTP 配置元件

用于定义 HTTP/HTTPS 请求的基础配置。

```yaml
- testclass: http
  config:
    method: GET
    base_url: http://localhost:8080
    path: /test
    http/2: false
    headers:
      h1: 1
```

**相关文档**：[HTTP 协议文档](/guide/protocols/http)

### 🗄️ JDBC 配置元件

用于定义数据库连接的基础配置。

```yaml
- testclass: jdbc
  config:
    driver: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/testdb?user=testuser&password=testpass
    max_active: 10
```

**相关文档**：[JDBC 协议文档](/guide/protocols/jdbc)

### 🗃️ Redis 配置元件

用于定义 Redis 连接的基础配置。

```yaml
- testclass: redis
  config:
    url: redis://localhost:6379/0
    timeout: 2000
```

**相关文档**：[Redis 协议文档](/guide/protocols/redis)

### 🔌 Dubbo 配置元件

用于定义 Dubbo 服务调用的基础配置。

```yaml
- testclass: dubbo
  config:
    registry:
      address: zookeeper://localhost:2181
```

**相关文档**：[Dubbo 协议文档](/guide/protocols/dubbo)

### 🚀 Kafka 配置元件

用于定义 Kafka 消息发送的基础配置。

```yaml
- testclass: kafka
  config:
    bootstrap_servers: localhost:9092
    topic: test-topic
    key_serializer: org.apache.kafka.common.serialization.StringSerializer
    value_serializer: org.apache.kafka.common.serialization.StringSerializer
```

**相关文档**：[Kafka 协议文档](/guide/protocols/kafka)

### 🐰 RabbitMQ 配置元件

用于定义 RabbitMQ 消息发送的基础配置。

```yaml
- testclass: rabbit
  config:
    # 推荐使用 url 格式：amqp://[用户名:密码@]主机地址[:端口]/[虚拟主机]
    url: amqp://guest:guest@localhost:5672/
    exchange: test-exchange
```

**相关文档**：[RabbitMQ 协议文档](/guide/protocols/rabbitmq)

### 🎯 ActiveMQ 配置元件

用于定义 ActiveMQ 消息发送的基础配置。

```yaml
- testclass: active
  config:
    broker_url: tcp://localhost:61616
    username: admin
    password: admin
    queue_name: test-queue
```

**相关文档**：[ActiveMQ 协议文档](/guide/protocols/activemq)

### 🍃 MongoDB 配置元件

用于定义 MongoDB 连接的基础配置。

```yaml
- testclass: mongo
  config:
    connection_string: mongodb://localhost:27017
    database: test
    collection: test_collection
```

**相关文档**：[MongoDB 协议文档](/guide/protocols/mongodb)

## 🔧 使用场景

### 全局配置管理

在项目级测试集合中定义全局配置：

```yaml
title: 电商系统测试项目
configelements:
  # HTTP 全局配置
  - testclass: http
    config:
      base_url: https://api.ecommerce.com
      headers:
        User-Agent: EcommerceTestFramework/1.0
        Accept: application/json

  # 数据库全局配置
  - testclass: jdbc
    config:
      driver: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/testdb?user=testuser&password=testpass

  # 缓存全局配置
  - testclass: redis
    config:
      url: redis://cache.ecommerce.com:6379/0

children:
  - title: 用户服务模块
    # 继承所有全局配置
```

### 模块特定配置

在模块级测试集合中定义特定配置：

```yaml
title: 订单服务模块
configelements:
  # 订单服务特定的HTTP配置
  - testclass: http
    config:
      path: /api/v1/orders
      headers:
        X-Service: OrderService

  # 订单数据库配置
  - testclass: jdbc
    config:
      url: jdbc:mysql://localhost:3306/testdb?user=testuser&password=testpass

children:
  - title: 创建订单测试
    # 继承模块和项目级配置
    children:
      - testclass: http
        config:
          method: POST
          # 继承 path: /api/v1/orders
          # 继承 headers 中的通用和模块特定配置
          body:
            product_id: 12345
            quantity: 2
```

### 环境适配配置

根据不同环境定义不同的配置：

```yaml
title: 用户服务测试
variables:
  # 环境配置
  env_config:
    dev:
      api_base_url: https://api-dev.example.com
      db_url: jdbc:mysql://db-dev.example.com:3306/user_dev?user=test&password=test
    test:
      api_base_url: https://api-test.example.com
      db_url: jdbc:mysql://db-test.example.com:3306/user_test

configelements:
  # 根据环境动态配置
  - testclass: http
    config:
      base_url: ${env_config.${environment}.api_base_url}

  - testclass: jdbc
    config:
      url: ${env_config.${environment}.db_url}
```

## 📚 最佳实践

### 配置分层原则

#### 项目级配置

- **全局基础配置**：API 基础 URL、通用请求头
- **公共资源配置**：数据库连接池、缓存连接
- **安全配置**：认证信息、加密密钥

#### 模块级配置

- **模块特定配置**：API 路径前缀、模块标识头
- **业务配置**：业务相关的默认参数
- **环境适配**：模块特定的环境配置

#### 用例级配置

- **用例特定配置**：当前用例独有的配置项
- **动态配置**：基于变量生成的配置
- **覆盖配置**：临时覆盖上级配置

### 命名规范

```yaml
configelements:
  # 使用清晰的 testclass 名称
  - testclass: http
    # 配置项使用标准命名
    config:
      base_url: https://api.example.com
      # 布尔值使用明确的 true/false
      http/2: false
      # 复杂配置使用嵌套结构
      headers:
        Content-Type: application/json
        User-Agent: TestFramework/1.0
```

### 配置复用策略

#### 1. 模板引用

```yaml
# 定义标准配置模板
variables:
  standard_http_config: &standard_http_config
    base_url: https://api.example.com
    headers:
      Content-Type: application/json
      User-Agent: StandardTestClient

# 引用模板
configelements:
  - testclass: http
    config:
      <<: *standard_http_config
```

#### 2. 变量配置

```yaml
# 集中定义配置变量
variables:
  http_config:
    base_url: ${api_base_url}
    headers:
      User-Agent: ${user_agent}

# 在配置元件中使用变量
configelements:
  - testclass: http
    config: ${http_config}
```

### 性能优化

- **连接池配置**：合理配置数据库和消息队列的连接池参数
- **超时设置**：根据实际网络环境设置合适的超时时间
- **缓存利用**：对于不变的配置，考虑使用缓存机制
- **懒加载**：仅在需要时才初始化配置元件

**💡 提示**：合理使用配置元件可以显著提高测试配置的可维护性和复用性，建议在项目初期就设计好配置元件的分层结构！