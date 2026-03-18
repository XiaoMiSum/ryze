# 🔗 拦截器

拦截器是 Ryze 框架中的高级功能，允许您在测试执行的不同阶段进行自定义处理，如参数加密、响应解密、日志记录等。

## 🔍 基本概念

### 作用范围

拦截器可以对以下组件进行拦截：

- **测试集合**：在整个测试集合执行前后进行处理
- **取样器**：在单个测试请求执行前后进行处理
- **处理器**：在前置/后置处理器执行时进行拦截

### 执行生命周期

拦截器遵循以下执行顺序：

1. **preHandle**：在目标组件执行前调用
2. **目标组件执行**
3. **postHandle**：在目标组件执行后调用
4. **afterCompletion**：在整个请求完成后调用（无论成功或失败）

## ⚙️ 配置语法

### 基本配置格式

在测试集合或取样器中通过 `interceptors` 字段配置拦截器：

```yaml
testclass: http  # 目标组件类型
interceptors:
  # 方式1：简单引用（无参数）
  - 'LogInterceptor'

  # 方式2：带参数引用
  - EncryptInterceptor:
      algorithm: 'AES'
      key: 'mySecretKey'

  # 方式3：完整配置形式
  - testclass: 'PerformanceInterceptor'
    timeout_threshold: 5000
    log_slow_requests: true
```

### 应用场景示例

#### 1. HTTP 请求日志记录

```yaml
testclass: http
interceptors:
  - HttpLogInterceptor:
      log_headers: true
      log_body: true
      mask_sensitive: true
config:
  method: POST
  base_url: https://api.example.com
  path: /users
  headers:
    Authorization: Bearer ${token}
  body:
    username: testuser
    password: secret123
```

#### 2. 动态参数加密

```yaml
testclass: http
interceptors:
  - RequestEncryptInterceptor:
      fields: [ 'password', 'email' ]
      algorithm: 'md5'
config:
  method: POST
  base_url: https://api.example.com
  path: /register
  body:
    username: newuser
    password: plaintext  # 将被自动加密
    email: user@example.com  # 将被自动加密
```

#### 3. 响应数据解密

```yaml
testclass: http
interceptors:
  - ResponseDecryptInterceptor:
      encrypted_fields: [ 'data.user_info' ]
      decryption_key: ${decrypt_key}
config:
  method: GET
  base_url: https://api.example.com
  path: /profile
```

#### 4. 性能监控

```yaml
testclass: http
interceptors:
  - PerformanceMonitorInterceptor:
      alert_threshold: 3000  # 超过3秒报警
      collect_metrics: true
config:
  method: GET
  base_url: https://api.example.com
  path: /heavy-operation
```

## 🛠️ 内置拦截器

### 日志记录拦截器

**功能**：记录请求和响应的详细信息

```yaml
interceptors:
  - LoggingInterceptor:
      log_level: 'INFO'  # DEBUG, INFO, WARN, ERROR
      include_headers: true
      include_body: false
      max_body_length: 1000
```

### 缓存拦截器

**功能**：缓存响应结果，提高测试性能

```yaml
interceptors:
  - CacheInterceptor:
      cache_key_fields: [ 'url', 'method' ]
      cache_duration: 300  # 5分钟
      enable_cache: true
```

### 重试拦截器

**功能**：在请求失败时自动重试

```yaml
interceptors:
  - RetryInterceptor:
      max_attempts: 3
      retry_delay: 1000  # 毫秒
      retry_on_status: [ 500, 502, 503, 504 ]
```

## 💡 扩展功能

当内置拦截器无法满足特定需求时，Ryze 框架支持自定义拦截器扩展。详细的开发指南请参考：

- **开发文档**：[拦截器](/developer/interceptor) - 完整的开发指南和最佳实践
- **代码示例**
  ：查看框架源码中的[内置拦截器实现](https://github.com/XiaoMiSum/ryze/tree/master/ryze/src/main/java/io/github/xiaomisum/ryze/interceptor)

**💡 提示**：拦截器是实现复杂测试逻辑的强大工具，合理使用可以大大提高测试的灵活性和扩展性！