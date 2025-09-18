# 执行上下文与变量管理

执行上下文是 Ryze 框架中用于存储和管理测试执行过程中各种信息的核心组件。它负责维护变量、配置、测试结果等数据，并提供这些数据在不同测试组件间的共享机制。

## 概述

执行上下文在整个测试执行过程中起着至关重要的作用，它确保了测试数据在不同组件间的正确传递和共享。通过执行上下文，您可以：

- 管理测试变量
- 访问配置信息
- 存储测试结果
- 处理错误信息
- 控制测试流程

## 上下文结构

执行上下文包含以下主要组成部分：

### 变量管理

存储测试过程中使用的各种变量，包括用户定义变量、系统变量和提取变量。

### 配置信息

存储测试配置相关信息，如全局配置、协议配置等。

### 测试结果

存储测试执行过程中的结果数据。

### 错误信息

存储测试执行过程中产生的错误信息。

### 执行状态

跟踪测试执行的当前状态。

## 变量管理

### 变量类型

1. **用户变量**: 用户在测试配置中定义的变量
2. **系统变量**: 框架提供的内置变量
3. **提取变量**: 通过提取器从响应中提取的变量
4. **环境变量**: 从系统环境中获取的变量

### 变量作用域

- **全局作用域**: 在整个测试套件中可用
- **测试集合作用域**: 在当前测试集合中可用
- **测试元素作用域**: 在当前测试元素中可用

### 变量引用

在配置中使用变量时，使用 `${variable_name}` 语法：

```yaml
variables:
  base_url: "https://api.example.com"
  user_id: "${random(1000, 9999)}"

config:
  url: "${base_url}/users/${user_id}"
```

## 内置变量

Ryze 提供了丰富的内置变量：

### 系统变量

```yaml
# 当前时间戳
timestamp: "${timestamp()}"

# 随机UUID
uuid: "${uuid()}"

# 随机数字
random_number: "${random(1, 100)}"

```

## 变量函数

Ryze 内置了多种变量函数，用于生成动态数据：

### 时间函数

```yaml
# 当前时间戳
current_timestamp: "${timestamp()}"

# 格式化时间
formatted_time: "${timestamp('yyyy-MM-dd HH:mm:ss')}"

# 时间偏移
future_time: "${time_shift('yyyy-MM-dd', 'P7D')}"  # 7天后

past_time: "${time_shift('yyyy-MM-dd', '-P7D')}"   # 7天前
```

### 随机数据函数

```yaml
# 随机UUID
random_uuid: "${uuid()}"

# 随机数字
random_number: "${random(1000)}"

# 指定范围随机数字
random_range: "${random(1, 100)}"

# 随机字符串
random_string: "${random_string(10)}"

```

### 加密函数

```yaml
# MD5加密
md5_hash: "${digest('md5', 'password')}"

# SHA256加密
sha256_hash: "${digest('sha256', 'password', 'salt')}"

# Base64编码
base64_encoded: "${base64_encode('hello world')}"

# Base64解码
base64_decoded: "${base64_decode('aGVsbG8gd29ybGQ=')}"
```

### 模拟数据函数

```yaml
# 随机姓名
fake_name: "${faker('name.fullName')}"

# 随机邮箱
fake_email: "${faker('internet.emailAddress')}"

# 随机地址
fake_address: "${faker('address.fullAddress')}"

# 随机公司名
fake_company: "${faker('company.name')}"
```

## 变量作用域示例

```yaml
title: "变量作用域示例"
variables:
  global_var: "global_value"  # 全局变量

configelements:
  - testclass: http
    name: "HTTP配置"
    config:
      headers:
        X-Global-Var: "${global_var}"  # 可以访问全局变量

preprocessors:
  - testclass: http
    title: "前置处理"
    variables:
      local_var: "local_value"  # 局部变量
    config:
      url: "https://api.example.com/setup"
    extractors:
      - testclass: json
        field: "$.token"
        ref_name: "extracted_token"  # 提取变量

children:
  - testclass: http
    title: "测试请求"
    config:
      url: "https://api.example.com/test"
      headers:
        Authorization: "Bearer ${extracted_token}"  # 使用提取变量
        X-Local-Var: "${local_var}"  # 可以访问父级变量
        X-Global-Var: "${global_var}"  # 可以访问全局变量

postprocessors:
  - testclass: http
    title: "后置处理"
    config:
      url: "https://api.example.com/cleanup"
      headers:
        X-Global-Var: "${global_var}"  # 可以访问全局变量
```

## 变量处理顺序

变量处理遵循以下顺序：

1. **系统变量解析**: 解析内置系统变量
2. **用户变量解析**: 解析用户定义的变量
3. **函数执行**: 执行变量函数生成动态值
4. **引用解析**: 解析变量引用

## 最佳实践

### 1. 变量命名规范

```yaml
# 推荐：清晰的命名
variables:
  base_api_url: "https://api.example.com"
  user_auth_token: "${extracted_token}"
  request_timeout_ms: 5000

# 不推荐：含义不清的命名
variables:
  url: "https://api.example.com"
  token: "${extracted_token}"
  timeout: 5000
```

### 2. 合理使用作用域

```yaml
# 全局配置放在测试集合级别
title: "API测试套件"
variables:
  base_url: "https://api.example.com"  # 全局使用
  api_version: "v1"                    # 全局使用

children:
  - testclass: testsuite
    title: "用户管理测试"
    variables:
      user_service_path: "/users"      # 用户管理模块使用
```

### 3. 错误处理

```yaml
# 为可能失败的提取设置默认值
extractors:
  - testclass: json
    field: "$.optional_field"
    ref_name: "optional_value"
    defaultValue: "default_value"
```

### 4. 动态数据生成

```yaml
# 使用函数生成动态测试数据
variables:
  test_user_id: "${random()}"
  test_user_name: "${faker('name.fullName')}"
  request_id: "${uuid()}"
  timestamp: "${timestamp('yyyyMMddHHmmss')}"
```

## 常见问题

### 1. 变量未解析

检查变量名是否正确，确保变量在使用前已定义。

### 2. 变量作用域问题

确认变量在当前作用域内是否可用。

### 3. 函数执行错误

检查函数参数是否正确，函数名是否拼写正确。

### 4. 循环引用

避免变量间的循环引用，这会导致解析错误。

通过合理使用执行上下文和变量管理功能，您可以创建更加灵活和强大的测试用例，实现复杂的测试场景和数据驱动测试。