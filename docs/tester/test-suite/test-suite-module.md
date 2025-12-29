# 📊 测试集合（模块级）

## 🔍 基本概念

模块级测试集合是介于项目级和用例级之间的中层集合，代表某个功能模块或业务领域的测试集合。它继承项目级配置，可以针对模块特定需求进行配置的扩展或覆盖。

### 特点

- **模块隔离**：按业务模块或功能域划分，相对独立的测试单元
- **配置继承**：继承项目级配置，支持覆盖或扩展
- **多层组织**：可包含子模块或直接包含测试用例
- **复用性强**：同一模块的测试用例共用配置和变量

## 📋 配置项详解

```yaml
title: 用户管理模块  # 模块级集合标题

# 模块级变量定义
variables:
  base_api_path: /api/v1/users
  timeout: 30000
  default_user:
    username: testuser
    email: test@example.com

# 模块级配置元件
configelements:
  - testclass: http
    config:
      path: ${base_api_path}
      headers:
        X-Module: UserManagement

# 前置处理器（模块初始化）
preprocessors:
  - testclass: jdbc
    config:
      datasource: main_db
      sql: 'TRUNCATE TABLE test_users'

# 后置处理器（模块清理）
postprocessors:
  - testclass: jdbc
    config:
      datasource: main_db
      sql: 'DELETE FROM test_users WHERE created_by = "test"'

# 子集合（用例或子模块）
children:
  - !include use-cases/create-user.yaml
  - !include use-cases/update-user.yaml
```

## 参数说明

| 参数 | 类型 | 必填 | 描述 |
|---------|--------|------|-------------------------------------|
| title | String | 是 | 测试集合的标题 |
| variables | Object | 否 | 变量定义，支持嵌套对象和动态函数，继承父级变量 |
| configelements | Array | 否 | 配置元件列表，为该集合中的所有取样器提供默认配置 |
| preprocessors | Array | 否 | 前置处理器列表，在执行子测试之前运行 |
| postprocessors | Array | 否 | 后置处理器列表，在执行子测试之后运行 |
| children | Array | 是 | 子测试集合或测试用例列表 |

> **配置继承**：模块级集合继承项目级配置，可覆盖或扩展具体项
> 
> **文件引入**：通过 `!include` 或 `!import` 指令引入外部文件

## 📚 最佳实践

### 模块划分原则

- **业务驱动**：按功能模块或业务领域划分
- **独立完整**：每个模块是相对独立的测试单元
- **合理粒度**：不宜过大或过小，一般 10~20 个测试用例为宜
- **清晰命名**：module 名称应清晰反映功能

### 配置管理建议

```yaml
# 推荐做法：模块级配置针对模块特定需求
configelements:
  # 继承项目级 HTTP 配置，添加模块特定路径
  - testclass: http
    config:
      path: /api/v1/users  # 模块 API 前缀
      headers:
        X-Module: UserManagement  # 模块标识

  # 模块特定的数据库操作
  - testclass: jdbc
    ref_name: users_db
    config:
      datasource: main_db  # 引用项目级配置
      # 不重复定义连接信息
```

### 子集合组织建议

```yaml
children:
  # 方式1：包含测试用例
  - !include use-cases/create-user.yaml
  - !include use-cases/update-user.yaml
  - !include use-cases/delete-user.yaml
  
  # 方式2：包含子模块集合
  - !include sub-modules/user-profile.yaml
  - !include sub-modules/user-permissions.yaml
```

---

## 使用示例

### 例子 1：用户管理模块

```yaml
title: 用户管理模块

# 模块级变量
variables:
  base_api_path: /api/v1/users
  default_user:
    username: testuser
    email: test@example.com
    status: active

# 模块级配置元件
configelements:
  # HTTP 配置
  - testclass: http
    config:
      path: ${base_api_path}
      headers:
        X-Module: UserManagement
        Content-Type: application/json

  # JDBC 配置
  - testclass: jdbc
    ref_name: user_db
    config:
      datasource: main_db

# 模块级前置处理
preprocessors:
  - testclass: jdbc
    config:
      datasource: main_db
      sql: 'DELETE FROM users WHERE email LIKE "test%"'

# 模块级后置处理
postprocessors:
  - testclass: jdbc
    config:
      datasource: main_db
      sql: 'DELETE FROM users WHERE created_by = "test_framework"'

# 子测试用例
children:
  - !include use-cases/create-user.yaml
  - !include use-cases/get-user.yaml
  - !include use-cases/update-user.yaml
  - !include use-cases/delete-user.yaml
  - !include use-cases/list-users.yaml
```

### 例子 2：订单管理模块

```yaml
title: 订单管理模块

variables:
  base_api_path: /api/v1/orders
  order_status:
    pending: pending
    processing: processing
    completed: completed
    cancelled: cancelled

configelements:
  - testclass: http
    config:
      path: ${base_api_path}
      headers:
        X-Module: OrderManagement

preprocessors:
  # 清理旧测试订单
  - testclass: jdbc
    config:
      datasource: main_db
      sql: |
        DELETE FROM orders WHERE created_at < DATE_SUB(NOW(), INTERVAL 7 DAY);
        DELETE FROM order_items WHERE order_id NOT IN (SELECT id FROM orders);

children:
  - !include use-cases/create-order.yaml
  - !include use-cases/view-order.yaml
  - !include use-cases/update-order.yaml
  - !include use-cases/cancel-order.yaml
  - !include use-cases/list-orders.yaml
```

### 例子 3：带有子模块的业务模块

```yaml
title: 支付业务模块

variables:
  base_api_path: /api/v1/payment
  currencies:
    CNY: 人民币
    USD: 美元
    EUR: 欧元

configelements:
  - testclass: http
    config:
      path: ${base_api_path}
      headers:
        X-Module: Payment
        X-API-Version: '2.0'

preprocessors:
  - testclass: http
    config:
      method: POST
      path: /admin/init-payment-env
      body:
        environment: test

children:
  # 子模块 1: 支付渠道管理
  - !include sub-modules/payment-gateway.yaml
  
  # 子模块 2: 事务处理
  - !include sub-modules/transaction-processing.yaml
  
  # 子模块 3: 费用计算
  - !include sub-modules/fee-calculation.yaml

postprocessors:
  - testclass: http
    config:
      method: POST
      path: /admin/cleanup-payment-env
      body:
        environment: test
```

### 例子 4：简化的模块（仅包含用例）

```yaml
title: 报告管理模块

variables:
  report_types:
    - sales
    - inventory
    - user_behavior

children:
  - !include use-cases/generate-sales-report.yaml
  - !include use-cases/export-report.yaml
  - !include use-cases/schedule-report.yaml
```

### 例子 5：使用动态函数的模块

```yaml
title: 批量处理模块

variables:
  batch_size: 100
  current_date: ${Now()}
  batch_id: ${UUID()}
  test_users:
    - username: user_${RandomString(4)}
      email: test_${RandomString(6)}@example.com
      age: ${RandomInt(18, 60)}

configelements:
  - testclass: http
    config:
      path: /api/v1/batch
      headers:
        X-Batch-ID: ${batch_id}
        X-Request-Date: ${current_date}

preprocessors:
  - testclass: jdbc
    config:
      datasource: main_db
      sql: |
        INSERT INTO batch_logs 
        (batch_id, created_at, status) 
        VALUES('${batch_id}', '${current_date}', 'STARTED')

children:
  - !include use-cases/validate-batch-data.yaml
  - !include use-cases/process-batch.yaml
  - !include use-cases/generate-batch-report.yaml

postprocessors:
  - testclass: jdbc
    config:
      datasource: main_db
      sql: |
        UPDATE batch_logs 
        SET status = 'COMPLETED', completed_at = NOW() 
        WHERE batch_id = '${batch_id}'
```

---

## 📄 相关文档

- [📝 测试用例模板](./test-case) - 学习用例级测试的定义
- [🌋 测试集合（项目级）](./test-suite-project) - 学习项目级测试集合的定义
- [📈 测试集合体系概念](../../guide/concepts/test-suite) - 深入了解测试集合体系架构

> 📌 **提示**：模块级测试集合是项目和用例之间的桥梁，其良好设计会常成员配合！
