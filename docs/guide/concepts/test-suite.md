# 🗂️ 测试集合

测试集合是 Ryze 框架中组织和管理测试用例的核心结构，提供了分层管理、配置继承、资源共享等强大功能。

## 🔍 基本概念

### 分层管理

测试集合采用三层层级结构，实现了从宏观到微观的测试组织：

- **项目级**：整个测试项目的最高层级
- **模块级**：按功能模块或接口分组的中间层级
- **用例级**：具体的测试用例实现

### 配置继承

子级集合自动继承父级集合的配置：

- **变量继承**：子级可以使用父级定义的变量
- **配置元件继承**：共享数据库连接、HTTP 配置等
- **处理器继承**：继承父级的前置/后置处理逻辑

## 🏢 层级结构

### 项目级集合

位于整个测试体系的最顶层，定义全局配置和公共资源。

**特点**：

- 包含全局变量和配置
- 定义整个项目的数据库连接、API 基础 URL 等
- 为所有子模块提供公共的前置/后置处理逻辑

### 模块级集合

按功能模块或业务领域组织的中间层级。

**特点**：

- 针对特定模块的配置和变量
- 可以覆盖或扩展项目级配置
- 包含多个相关的测试用例

### 用例级集合

具体的测试用例实现，包含具体的测试步骤。

**特点**：

- 包含具体的测试步骤（取样器）
- 可以定义用例级的变量和处理逻辑
- 是实际执行测试的最小单元

## 📋 模板定义

### 通用模板结构

所有层级的测试集合都遵循相同的模板结构：

```yaml
# 集合基本信息
title: 测试集合描述

# 变量定义
variables:
  global_var: 全局变量值
  config:
    host: api.example.com
    port: 443

# 配置元件（数据库连接、HTTP 配置等）
configelements:
  - testclass: jdbc
    config:
      url: jdbc:mysql://localhost:3306/testdb
      username: ${db_user}
      password: ${db_password}

# 前置处理器
preprocessors:
  - testclass: jdbc
    config:
      sql: "DELETE FROM temp_table WHERE created_at < NOW() - INTERVAL 1 DAY"

# 后置处理器
postprocessors:
  - testclass: jdbc
    config:
      sql: "DELETE FROM temp_table WHERE created_at < NOW() - INTERVAL 1 DAY"

# 子集合或测试步骤
children:
  - title: 子集合或测试步骤
    # ...
```

### 字段详细说明

| 字段               | 类型     | 必填 | 说明                   |
|------------------|--------|----|----------------------|
| `title`          | String | 是  | 集合名称和描述              |
| `variables`      | Object | 否  | 变量定义，支持嵌套对象          |
| `configelements` | Array  | 否  | 配置元件列表（数据库、HTTP 配置等） |
| `preprocessors`  | Array  | 否  | 前置处理器列表              |
| `postprocessors` | Array  | 否  | 后置处理器列表              |
| `children`       | Array  | 否  | 子集合或测试步骤列表           |

## 📊 实际示例

### 🏢 项目级示例

```yaml
title: 用户管理系统测试项目

# 项目级全局配置
variables:
  # 环境配置
  environment: test
  base_url: https://test-api.example.com

  # 数据库配置
  database:
    host: test-db.example.com
    port: 3306
    name: user_system_test

  # 公共认证信息
  admin_credentials:
    username: admin
    password: admin123

# 全局配置元件
configelements:
  # 数据库连接配置
  - testclass: jdbc
    config:
      url: jdbc:mysql://${database.host}:${database.port}/${database.name}
      username: ${db_user}
      password: ${db_password}
      max_active: 10
      max_wait: 5000

  # HTTP 全局配置
  - testclass: http
    config:
      protocol: https
      host: ${base_url}
      headers:
        Content-Type: application/json
        User-Agent: RyzeTestFramework/1.0

# 全局前置处理
preprocessors:
  - testclass: jdbc
    config:
      sql: "DELETE FROM temp_table WHERE created_at < NOW() - INTERVAL 1 DAY"

# 全局后置处理
postprocessors:
  - testclass: jdbc
    config:
      sql: "DELETE FROM temp_table WHERE created_at < NOW() - INTERVAL 1 DAY"

# 子模块
children:
  - title: 用户模块测试
    # 模块级配置...

  - title: 角色模块测试
    # 模块级配置...
```

### 📦 模块级示例

```yaml
title: 用户模块测试

# 模块级变量（继承项目级变量）
variables:
  # 用户模块特定配置
  user_api_prefix: /api/v1/users
  default_user_data:
    username: testuser
    email: test@example.com
    department: IT

  # 测试数据
  test_users:
    - { id: 1001, name: "张三", role: "admin" }
    - { id: 1002, name: "李四", role: "user" }

# 模块级配置元件
configelements:
  # 用户模块特定 HTTP 配置
  - testclass: http
    config:
      path: ${user_api_prefix}
      headers:
        X-Module: UserManagement
        Authorization: Bearer ${admin_token}

# 模块级前置处理
preprocessors:
  - testclass: jdbc
    config:
      sql: "DELETE FROM temp_table WHERE created_at < NOW() - INTERVAL 1 DAY"

# 模块级后置处理
postprocessors:
  - testclass: jdbc
    config:
      sql: "DELETE FROM temp_table WHERE created_at < NOW() - INTERVAL 1 DAY"

# 子用例
children:
  - title: 用户创建测试
    # 用例级配置...

  - title: 用户查询测试
    # 用例级配置...

  - title: 用户更新测试
    # 用例级配置...
```

### 📝 用例级示例

```yaml
title: 用户创建测试用例

# 用例级变量（继承项目和模块级变量）
variables:
  # 当前用例特定数据
  new_user:
    username: ${RandomString(length=8)}
    email: ${new_user.username}@test.com
    password: ${RandomString(length=12)}
    real_name: ${Faker('name.fullName')}
    phone: ${Faker('phoneNumber.cellPhone')}

  expected_result:
    status: "success"
    code: 200

# 用例级前置处理
preprocessors:
  - testclass: jdbc
    config:
      sql: "DELETE FROM temp_table WHERE created_at < NOW() - INTERVAL 1 DAY"

# 用例级后置处理
postprocessors:
  - testclass: jdbc
    config:
      sql: "DELETE FROM temp_table WHERE created_at < NOW() - INTERVAL 1 DAY"

# 具体测试步骤
children:
  # 步骤1：发送用户创建请求
  - title: "创建新用户"
    testclass: http
    config:
      method: POST
      path: /create
      body: ${new_user}
    extractors:
      - { testclass: json, field: '$.data.user_id', ref_name: created_user_id }
      - { testclass: json, field: '$.data.token', ref_name: user_token }
    validators:
      - { testclass: json, field: '$.status', expected: ${ expected_result.status }, rule: '==' }
      - { testclass: http, field: 'status', expected: ${ expected_result.code }, rule: '==' }

  # 步骤2：验证用户信息
  - title: "验证用户信息"
    testclass: http
    config:
      method: GET
      path: /${created_user_id}
      headers:
        Authorization: Bearer ${user_token}
    validators:
      - { testclass: json, field: '$.data.username', expected: ${ new_user.username }, rule: '==' }
      - { testclass: json, field: '$.data.email', expected: ${ new_user.email }, rule: '==' }

  # 步骤3：数据库验证
  - title: "数据库验证用户信息"
    testclass: jdbc
    config:
      sql: "SELECT username, email, status FROM users WHERE id = ${created_user_id}"
    validators:
      - { testclass: result, expected: ${ new_user.username }, rule: 'contains' }
```

## 📚 最佳实践

### 层级划分原则

#### 项目级配置

- **全局变量**：环境配置、API 基础 URL、数据库连接信息
- **公共资源**：数据库连接池、HTTP 客户端配置
- **公共处理逻辑**：环境初始化、数据清理

#### 模块级配置

- **模块特定配置**：API 路径前缀、模块特定头信息
- **模块数据**：测试数据集、模拟数据
- **模块处理**：模块级的数据准备和清理

#### 用例级配置

- **用例变量**：当前用例特定的数据和参数
- **测试步骤**：具体的测试操作和验证
- **用例处理**：用例级的准备和清理工作

### 变量命名规范

```yaml
variables:
  # 环境配置 - 使用小写下划线
  environment: test
  base_url: https://test-api.example.com

  # 嵌套对象 - 使用小写下划线
  database_config:
    host: localhost
    port: 3306
    username: test_user

  # 数组数据 - 使用复数形式
  test_users:
    - { id: 1, name: "user1" }
    - { id: 2, name: "user2" }

  # 常量 - 使用大写下划线
  MAX_RETRY_COUNT: 3
  DEFAULT_TIMEOUT: 30000
```

### 配置复用策略

#### 1. 继承复用

```yaml
# 父级配置
variables:
  common_headers:
    Content-Type: application/json
    User-Agent: TestClient/1.0

children:
  # 子级自动继承父级配置
  - title: 子测试
    variables:
      # 可以扩展或覆盖父级配置
      common_headers:
        Content-Type: application/json
        User-Agent: TestClient/1.0
        Authorization: Bearer ${token}  # 新增
```

#### 2. 引用复用

```yaml
variables:
  # 定义公共配置
  standard_user: &standard_user
    department: IT
    role: user
    status: active

  # 引用公共配置
  test_user_1:
    <<: *standard_user
    username: testuser1
    email: user1@test.com

  test_user_2:
    <<: *standard_user
    username: testuser2
    email: user2@test.com
```

### 错误处理建议

- **参数验证**：在测试前验证必要参数的存在性
- **异常捕获**：合理处理测试执行中的异常情况
- **资源清理**：确保测试结束后及时清理临时数据
- **错误日志**：记录详细的错误信息以便问题定位

### 性能优化

- **合理分组**：按功能模块分组，避免过度嵌套
- **并发执行**：合理使用并发执行提高效率
- **数据缓存**：复用公共数据，减少重复操作
- **资源管理**：合理配置连接池和线程数

**💡 提示**：合理的测试集合结构设计是测试项目成功的关键，建议在项目初期就认真考虑和设计好整体结构！