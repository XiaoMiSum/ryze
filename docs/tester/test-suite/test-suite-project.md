# 🌋 测试集合（项目级）

## 🔍 基本概念

项目级测试集合是整个测试框架的根节点和最高层级，定义了项目范围内的全局配置、环境变量、资源管理等基础设施。所有模块级和用例级集合都继承项目级的配置。

### 特点

- **全局管理**：管理整个项目的全局配置和资源
- **环境隔离**：支持多环境配置（测试、预发、生产等）
- **配置共享**：为所有子集合提供统一的配置和变量
- **生命周期管理**：管理项目的初始化和清理流程

## 📋 配置项详解

```yaml
title: 测试项目  # 项目级集合标题

# 全局变量定义
variables:
  environment: test
  base_url: https://api.example.com
  database:
    host: localhost
    port: 3306
    name: test_db
  timeout: 30000

# 全局配置元件
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
      headers:
        Content-Type: application/json

  - testclass: jdbc
    ref_name: main_db
    config:
      driver: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://${database.host}:${database.port}/${database.name}

# 项目级前置处理
preprocessors:
  - testclass: jdbc
    config:
      datasource: main_db
      sql: 'TRUNCATE TABLE test_users'

# 项目级后置处理
postprocessors:
  - testclass: jdbc
    config:
      datasource: main_db
      sql: 'DELETE FROM test_users WHERE created_by = "test"'

# 子模块
children:
  - !include modules/user-management/module.yaml
  - !include modules/order-management/module.yaml
```

## 参数说明

| 参数             | 类型     | 必填 | 描述                           |
|----------------|--------|----|------------------------------|
| title          | String | 是  | 测试集合的标题                      |
| variables      | Object | 否  | 全局变量定义，支持嵌套对象和动态函数，所有子集合都可继承 |
| configelements | Array  | 否  | 全局配置元件列表，为整个项目中的所有取样器提供默认配置  |
| preprocessors  | Array  | 否  | 项目级前置处理器列表，在执行项目测试之前运行       |
| postprocessors | Array  | 否  | 项目级后置处理器列表，在执行项目测试之后运行       |
| children       | Array  | 是  | 模块级测试集合列表                    |

> **注意**：项目级配置被所有下级集合继承，修改需谨慎

## 📚 最佳实践

### 项目级配置原则

- **全局变量**：定义环境配置、API 基础 URL、数据库连接信息等
- **公共连接**：定义数据库、缓存等公共资源连接
- **认证信息**：定义项目共用的认证信息和 token
- **公共头信息**：定义所有请求都需要的公共请求头

### 全局变量最佳实践

```yaml
variables:
  # 环境配置（必需）
  environment: test  # test/staging/production
  base_url: https://test-api.example.com

  # 数据库配置
  database:
    host: test-db.example.com
    port: 3306
    name: test_db
    username: test_user
    password: test_pass

  # 缓存配置
  redis_url: redis://test-redis.example.com:6379

  # 认证信息
  admin_credentials:
    username: admin
    password: admin123

  # 常量配置
  DEFAULT_TIMEOUT: 30000
  MAX_RETRY_COUNT: 3
```

### 全局配置元件最佳实践

```yaml
configelements:
  # HTTP 全局配置
  - testclass: http
    config:
      base_url: ${base_url}
      headers:
        Content-Type: application/json
        User-Agent: RyzeTestFramework/1.0

  # JDBC 全局配置
  - testclass: jdbc
    ref_name: main_db
    config:
      driver: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://${database.host}:${database.port}/${database.name}?user=${database.username}&password=${database.password}
      max_active: 10
      max_idle: 5

  # Redis 全局配置（如需要）
  - testclass: redis
    ref_name: cache
    config:
      url: ${redis_url}
      timeout: 5000
```

### 项目生命周期管理

```yaml
# 前置处理：项目初始化
preprocessors:
  - testclass: jdbc
    config:
      datasource: main_db
      sql: |
        -- 清理测试数据
        TRUNCATE TABLE test_users;
        TRUNCATE TABLE test_orders;

  - testclass: http
    config:
      method: POST
      path: /admin/init-test-env
      body:
        environment: ${environment}

# 后置处理：项目清理
postprocessors:
  - testclass: http
    config:
      method: POST
      path: /admin/cleanup-test-env
      body:
        environment: ${environment}

  - testclass: jdbc
    config:
      datasource: main_db
      sql: |
        -- 最终清理
        DELETE FROM test_users WHERE created_by = 'test_framework';
        DELETE FROM test_orders WHERE created_by = 'test_framework';
```

### 模块引入建议

```yaml
children:
  # 按业务领域分组

  # 用户管理模块
  - !include modules/user-management/module.yaml

  # 订单管理模块
  - !include modules/order-management/module.yaml

  # 支付模块
  - !include modules/payment/module.yaml

  # 库存模块
  - !include modules/inventory/module.yaml

  # 报告和统计（可选）
  - !include modules/reporting/module.yaml
```

### 项目结构推荐

```
project/
├── test-suite.yaml              # 项目级集合文件
├── modules/
│   ├── user-management/
│   │   ├── module.yaml          # 模块级集合
│   │   ├── use-cases/
│   │   │   ├── create-user.yaml
│   │   │   └── update-user.yaml
│   │   └── data/
│   │       └── test-users.json
│   ├── order-management/
│   │   └── module.yaml
│   └── payment/
│       └── module.yaml
└── shared/
    ├── variables.yaml           # 共享变量
    └── functions.groovy         # 共享函数
```

---

## 使用示例

### 例子 1：电商平台项目级集合

```yaml
title: 电商平台综合测试项目

# 全局变量定义
variables:
  # 环境配置
  environment: test
  base_url: https://test-api.ecommerce.com

  # 数据库配置
  database:
    host: test-db.ecommerce.com
    port: 3306
    name: ecommerce_test
    username: test_user
    password: test_pass_123

  # 缓存配置
  redis:
    host: test-redis.ecommerce.com
    port: 6379
    db: 0

  # 认证信息
  admin_user:
    username: admin
    password: admin@123
    email: admin@test.com

  test_user:
    username: testuser
    password: testuser@123
    email: testuser@test.com

  # 常量
  DEFAULT_TIMEOUT: 30000
  MAX_RETRY: 3
  REQUEST_ID: ${UUID()}

# 全局配置元件
configelements:
  # HTTP 全局配置
  - testclass: http
    config:
      base_url: ${base_url}
      headers:
        Content-Type: application/json
        User-Agent: RyzeTestFramework/1.0
        X-Environment: ${environment}

  # JDBC 全局配置
  - testclass: jdbc
    ref_name: main_db
    config:
      driver: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://${database.host}:${database.port}/${database.name}?user=${database.username}&password=${database.password}
      max_active: 20
      max_idle: 10
      max_wait: 30000

  # Redis 全局配置
  - testclass: redis
    ref_name: cache
    config:
      url: redis://${redis.host}:${redis.port}/${redis.db}
      timeout: 5000

# 项目级前置处理
preprocessors:
  # 1. 初始化数据库
  - testclass: jdbc
    config:
      datasource: main_db
      sql: |
        -- 清空测试表
        TRUNCATE TABLE users;
        TRUNCATE TABLE products;
        TRUNCATE TABLE orders;
        TRUNCATE TABLE order_items;
        TRUNCATE TABLE payments;

        -- 插入初始数据
        INSERT INTO users (id, username, email, status) 
        VALUES 
          (1, 'admin', 'admin@test.com', 'active'),
          (2, 'testuser', 'testuser@test.com', 'active');

  # 2. 清空缓存
  - testclass: redis
    config:
      datasource: cache
      command: FLUSHDB

  # 3. 环境初始化通知
  - testclass: http
    config:
      method: POST
      path: /admin/init-test-env
      headers:
        Authorization: Bearer ${admin_token}
      body:
        environment: ${environment}
        request_id: ${REQUEST_ID}
        timestamp: ${Now()}

# 子模块
children:
  # 用户管理模块
  - !include modules/user-management/module.yaml

  # 商品管理模块
  - !include modules/product-management/module.yaml

  # 订单管理模块
  - !include modules/order-management/module.yaml

  # 支付模块
  - !include modules/payment/module.yaml

  # 库存管理模块
  - !include modules/inventory/module.yaml

# 项目级后置处理
postprocessors:
  # 1. 生成测试报告
  - testclass: http
    config:
      method: POST
      path: /admin/generate-test-report
      body:
        environment: ${environment}
        request_id: ${REQUEST_ID}
        status: completed

  # 2. 数据清理
  - testclass: jdbc
    config:
      datasource: main_db
      sql: |
        -- 删除测试数据
        DELETE FROM order_items WHERE order_id IN 
          (SELECT id FROM orders WHERE created_by = 'test_framework');
        DELETE FROM orders WHERE created_by = 'test_framework';
        DELETE FROM users WHERE email LIKE 'test%@test.com';

  # 3. 环境清理通知
  - testclass: http
    config:
      method: POST
      path: /admin/cleanup-test-env
      body:
        environment: ${environment}
        request_id: ${REQUEST_ID}
```

### 例子 2：SaaS 应用项目级集合

```yaml
title: SaaS 应用全面测试项目

variables:
  # 部署环境
  environment: ${Env('TEST_ENV', 'test')}
  base_url: ${Env('API_BASE_URL', 'https://api-test.saas.com')}

  # 多租户配置
  tenants:
    default: tenant_001
    secondary: tenant_002

  # 数据库主从配置
  database:
    master:
      host: ${Env('DB_MASTER_HOST', 'test-db-master.saas.com')}
      port: 3306
      username: ${Env('DB_USER', 'saas_user')}
      password: ${Env('DB_PASS', 'saas_pass')}
    slave:
      host: ${Env('DB_SLAVE_HOST', 'test-db-slave.saas.com')}
      port: 3306

  # 认证配置
  auth:
    oauth2_client_id: test_client_id
    oauth2_client_secret: test_client_secret
    jwt_secret: test_jwt_secret

  # 特性开关
  features:
    advanced_analytics: true
    custom_branding: true
    api_webhooks: true

configelements:
  # HTTP 配置
  - testclass: http
    config:
      base_url: ${base_url}
      headers:
        Content-Type: application/json
        User-Agent: SaasTestSuite/1.0
        X-Tenant-ID: ${tenants.default}
        X-Environment: ${environment}

  # 主数据库配置
  - testclass: jdbc
    ref_name: master_db
    config:
      driver: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://${database.master.host}:${database.master.port}/saas_db?user=${database.master.username}&password=${database.master.password}
      max_active: 30

preprocessors:
  # 初始化测试数据
  - testclass: jdbc
    config:
      datasource: master_db
      sql: |
        -- 初始化租户数据
        INSERT INTO tenants (id, name, status) VALUES 
          ('${tenants.default}', 'Default Tenant', 'active'),
          ('${tenants.secondary}', 'Secondary Tenant', 'active');

        -- 初始化用户
        INSERT INTO users (tenant_id, username, email, role) VALUES
          ('${tenants.default}', 'admin', 'admin@test.com', 'admin'),
          ('${tenants.default}', 'user', 'user@test.com', 'user');

children:
  - !include modules/authentication/module.yaml
  - !include modules/user-management/module.yaml
  - !include modules/workspace/module.yaml
  - !include modules/collaboration/module.yaml
  - !include modules/analytics/module.yaml
  - !include modules/api-integration/module.yaml

postprocessors:
  # 数据清理
  - testclass: jdbc
    config:
      datasource: master_db
      sql: |
        DELETE FROM user_audit_logs WHERE created_at < NOW() - INTERVAL 7 DAY;
        DELETE FROM api_logs WHERE created_at < NOW() - INTERVAL 7 DAY;
```

### 例子 3：微服务项目级集合

```yaml
title: 微服务架构综合测试项目

variables:
  environment: test

  # 服务端点配置
  services:
    user_service: https://user-service.test.local
    order_service: https://order-service.test.local
    payment_service: https://payment-service.test.local
    notification_service: https://notification-service.test.local

  # 消息队列配置
  kafka:
    bootstrap_servers: kafka-broker.test.local:9092
    topics:
      user_events: user-events
      order_events: order-events
      payment_events: payment-events

  # 数据库配置
  databases:
    user_db:
      host: user-db.test.local
      name: user_service_db
    order_db:
      host: order-db.test.local
      name: order_service_db
    payment_db:
      host: payment-db.test.local
      name: payment_service_db

configelements:
  # 用户服务 HTTP 配置
  - testclass: http
    config:
      base_url: ${services.user_service}
      headers:
        X-Service: user-service
        Content-Type: application/json

  # 订单服务 HTTP 配置
  - testclass: http
    config:
      base_url: ${services.order_service}
      headers:
        X-Service: order-service

  # 用户服务数据库配置
  - testclass: jdbc
    ref_name: user_db
    config:
      driver: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://sa:sa_pass@${databases.user_db.host}:3306/${databases.user_db.name}

  # Kafka 消息队列配置
  - testclass: kafka
    config:
      bootstrap.servers: ${kafka.bootstrap_servers}

preprocessors:
  # 初始化所有服务的数据
  - testclass: jdbc
    config:
      datasource: user_db
      sql: TRUNCATE TABLE users; TRUNCATE TABLE roles;

  # 清理消息队列
  - testclass: kafka
    config:
      bootstrap.servers: ${kafka.bootstrap_servers}
      topics:
        - ${kafka.topics.user_events}
        - ${kafka.topics.order_events}

children:
  - !include modules/user-service/module.yaml
  - !include modules/order-service/module.yaml
  - !include modules/payment-service/module.yaml
  - !include modules/integration-tests/module.yaml

postprocessors:
  # 最终清理
  - testclass: jdbc
    config:
      datasource: user_db
      sql: DELETE FROM users WHERE created_by = 'test_framework';

  # 生成性能报告
  - testclass: http
    config:
      method: POST
      path: /metrics/generate-report
      body:
        environment: ${environment}
```

### 例子 4：最小化项目级集合

```yaml
title: 简单应用测试项目

variables:
  environment: test
  base_url: https://api-test.example.com
  db_host: localhost
  db_name: test_db

configelements:
  - testclass: http
    config:
      base_url: ${base_url}

children:
  - !include modules/basic-tests/module.yaml
```

---

## 📄 相关文档

- [📝 测试用例模板](./test-case) - 学习用例级测试的定义
- [📊 测试集合（模块级）](./test-suite-module) - 学习模块级测试集合的定义
- [📈 测试集合体系概念](../../guide/concepts/test-suite) - 深入了解测试集合体系架构

> 📌 **提示**：项目级测试集合定义了整个测试框架的基础，合理设计项目结构会大大提升测试的可维护性和可扩展性！
