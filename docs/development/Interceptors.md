# 🎯 拦截器开发指南

## 概述

拦截器是 Ryze 框架中的重要组件，用于在测试执行过程中进行横切关注点处理，如日志记录、性能监控、数据处理、安全控制等。通过实现 `RyzeInterceptor` 接口，您可以创建自定义拦截器来扩展框架功能。

## 拦截器接口

### 基本接口定义

```java
public interface RyzeInterceptor<T extends TestElement> {
    
    /**
     * 获取执行顺序，数字越小优先级越高
     * @return 执行顺序值
     */
    int getOrder();
    
    /**
     * 判断是否支持当前测试元素
     * @param context 测试上下文
     * @return 如果支持当前测试元素返回 true，否则返回 false
     */
    boolean supports(ContextWrapper context);
    
    /**
     * 前置处理，在测试执行前调用
     * @param context 测试上下文
     * @param runtime TestElement 运行时数据
     * @return 如果返回 false，将中断后续处理
     */
    default boolean preHandle(ContextWrapper context, T runtime) {
        return true;
    }
    
    /**
     * 后置处理，在测试执行后调用
     * @param context 测试上下文
     * @param runtime TestElement 运行时数据
     */
    default void postHandle(ContextWrapper context, T runtime) {
    }
    
    /**
     * 最终处理，无论测试成功失败都会调用
     * @param context 测试上下文
     */
    default void afterCompletion(ContextWrapper context) {
    }
}
```

### 执行生命周期

拦截器按照以下顺序执行：

```
preHandle (按 order 升序) → 测试业务逻辑 → postHandle (按 order 降序) → afterCompletion (按 order 降序)
```

如果某个拦截器的 `preHandle` 返回 `false`，则：
1. 不会执行后续拦截器的 `preHandle`
2. 不会执行测试业务逻辑
3. 会调用已执行拦截器的 `afterCompletion`

## 拦截器开发实例

### 1. 基础拦截器示例

#### 日志记录拦截器

```java
/**
 * 通用日志记录拦截器
 */
@KW("logger")
public class LoggingInterceptor implements RyzeInterceptor<TestElement> {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    
    @Override
    public int getOrder() {
        return 100; // 较高优先级
    }
    
    @Override
    public boolean supports(ContextWrapper context) {
        // 支持所有类型的测试元素
        return true;
    }
    
    @Override
    public boolean preHandle(ContextWrapper context, TestElement runtime) {
        String title = StringUtils.isNotBlank(context.getTestResult().getTitle()) 
            ? context.getTestResult().getTitle() 
            : "未命名测试";
            
        logger.info("开始执行测试: {} [{}]", title, runtime.getClass().getSimpleName());
        
        // 记录开始时间
        context.getTestResult().getMetadata().put("start_time", System.currentTimeMillis());
        
        return true;
    }
    
    @Override
    public void postHandle(ContextWrapper context, TestElement runtime) {
        Long startTime = (Long) context.getTestResult().getMetadata().get("start_time");
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            context.getTestResult().getMetadata().put("duration", duration);
            
            logger.info("测试执行完成: {} - 耗时: {}ms - 状态: {}", 
                context.getTestResult().getTitle(),
                duration,
                context.getTestResult().getStatus());
        }
    }
    
    @Override
    public void afterCompletion(ContextWrapper context) {
        // 记录异常信息
        Throwable throwable = context.getTestResult().getThrowable();
        if (throwable != null) {
            logger.error("测试执行异常: {}", context.getTestResult().getTitle(), throwable);
        }
    }
}
```

#### 性能监控拦截器

```java
/**
 * 性能监控拦截器
 */
@KW("performance_monitor")
public class PerformanceMonitorInterceptor implements RyzeInterceptor<TestElement> {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitorInterceptor.class);
    private static final String METRICS_KEY = "performance.metrics";
    
    @Override
    public int getOrder() {
        return 1000; // 较低优先级，在其他拦截器之后执行
    }
    
    @Override
    public boolean supports(ContextWrapper context) {
        return true;
    }
    
    @Override
    public boolean preHandle(ContextWrapper context, TestElement runtime) {
        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.startTime = System.nanoTime();
        metrics.startMemory = getUsedMemory();
        metrics.threadCount = Thread.activeCount();
        
        context.getTestResult().getMetadata().put(METRICS_KEY, metrics);
        
        logger.debug("性能监控开始 - 测试: {}", context.getTestResult().getTitle());
        return true;
    }
    
    @Override
    public void postHandle(ContextWrapper context, TestElement runtime) {
        PerformanceMetrics metrics = (PerformanceMetrics) 
            context.getTestResult().getMetadata().get(METRICS_KEY);
            
        if (metrics != null) {
            metrics.endTime = System.nanoTime();
            metrics.endMemory = getUsedMemory();
            metrics.duration = (metrics.endTime - metrics.startTime) / 1_000_000; // 转换为毫秒
            metrics.memoryUsed = metrics.endMemory - metrics.startMemory;
            
            // 记录性能指标
            logger.info("性能指标 - 测试: {} | 耗时: {}ms | 内存使用: {}KB | 线程数: {}", 
                context.getTestResult().getTitle(),
                metrics.duration,
                metrics.memoryUsed / 1024,
                metrics.threadCount);
        }
    }
    
    @Override
    public void afterCompletion(ContextWrapper context) {
        PerformanceMetrics metrics = (PerformanceMetrics) 
            context.getTestResult().getMetadata().get(METRICS_KEY);
            
        if (metrics != null) {
            // 性能预警
            if (metrics.duration > 10000) { // 超过10秒
                logger.warn("性能预警 - 测试执行时间过长: {} ({}ms)", 
                    context.getTestResult().getTitle(), metrics.duration);
            }
            
            if (metrics.memoryUsed > 100 * 1024 * 1024) { // 超过100MB
                logger.warn("性能预警 - 内存使用过多: {} ({}MB)", 
                    context.getTestResult().getTitle(), metrics.memoryUsed / 1024 / 1024);
            }
        }
    }
    
    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
    private static class PerformanceMetrics {
        long startTime;
        long endTime;
        long startMemory;
        long endMemory;
        long duration;
        long memoryUsed;
        int threadCount;
    }
}
```

### 2. 协议特定拦截器

#### HTTP 拦截器

```java
/**
 * HTTP 请求/响应拦截器
 */
@KW("http_interceptor")
public class HttpInterceptor implements RyzeInterceptor<HTTPSampler> {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpInterceptor.class);
    
    @Override
    public int getOrder() {
        return 200;
    }
    
    @Override
    public boolean supports(ContextWrapper context) {
        return context.getTestElement() instanceof HTTPSampler;
    }
    
    @Override
    public boolean preHandle(ContextWrapper context, HTTPSampler runtime) {
        // 记录请求信息
        logger.info("HTTP请求 - {} {} - Headers: {} - Body: {}", 
            runtime.getMethod(), 
            runtime.getUrl(),
            runtime.getHeaders(),
            runtime.getBody());
        
        // 添加通用请求头
        if (runtime.getHeaders() == null) {
            runtime.setHeaders(new HashMap<>());
        }
        
        // 添加请求ID用于跟踪
        String requestId = UUID.randomUUID().toString();
        runtime.getHeaders().put("X-Request-ID", requestId);
        runtime.getHeaders().put("X-Client", "Ryze-Framework");
        
        context.getTestResult().getMetadata().put("request_id", requestId);
        
        return true;
    }
    
    @Override
    public void postHandle(ContextWrapper context, HTTPSampler runtime) {
        if (context.getTestResult() instanceof SampleResult result) {
            String requestId = (String) context.getTestResult().getMetadata().get("request_id");
            
            logger.info("HTTP响应 - RequestID: {} - Status: {} - ContentType: {} - Size: {}bytes", 
                requestId,
                result.getResponse().getHeaders().get("status"),
                result.getResponse().getHeaders().get("content-type"),
                result.getResponse().bytes().length);
                
            // 检查响应状态
            String status = result.getResponse().getHeaders().get("status");
            if (status != null) {
                int statusCode = Integer.parseInt(status);
                if (statusCode >= 400) {
                    logger.warn("HTTP响应异常 - RequestID: {} - Status: {} - Body: {}", 
                        requestId, statusCode, result.getResponse().bytesAsString());
                }
            }
        }
    }
    
    @Override
    public void afterCompletion(ContextWrapper context) {
        String requestId = (String) context.getTestResult().getMetadata().get("request_id");
        if (context.getTestResult().getThrowable() != null) {
            logger.error("HTTP请求异常 - RequestID: {} - Error: {}", 
                requestId, context.getTestResult().getThrowable().getMessage());
        }
    }
}
```

#### 数据库拦截器

```java
/**
 * 数据库操作拦截器
 */
@KW("database_interceptor") 
public class DatabaseInterceptor implements RyzeInterceptor<JDBCSampler> {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInterceptor.class);
    
    @Override
    public int getOrder() {
        return 300;
    }
    
    @Override
    public boolean supports(ContextWrapper context) {
        return context.getTestElement() instanceof JDBCSampler;
    }
    
    @Override
    public boolean preHandle(ContextWrapper context, JDBCSampler runtime) {
        // 记录SQL执行信息
        logger.info("SQL执行 - DataSource: {} - SQL: {}", 
            runtime.getDataSource(), 
            runtime.getSql());
        
        // 记录执行开始时间
        context.getTestResult().getMetadata().put("sql_start_time", System.currentTimeMillis());
        
        // SQL安全检查
        if (containsDangerousKeywords(runtime.getSql())) {
            logger.warn("SQL安全警告 - 检测到潜在危险操作: {}", runtime.getSql());
        }
        
        return true;
    }
    
    @Override
    public void postHandle(ContextWrapper context, JDBCSampler runtime) {
        Long startTime = (Long) context.getTestResult().getMetadata().get("sql_start_time");
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            
            if (context.getTestResult() instanceof SampleResult result) {
                String response = result.getResponse().bytesAsString();
                
                logger.info("SQL执行完成 - 耗时: {}ms - 结果行数: {}", 
                    duration, countResultRows(response));
                
                // 慢查询警告
                if (duration > 5000) { // 超过5秒
                    logger.warn("慢查询警告 - SQL: {} - 耗时: {}ms", runtime.getSql(), duration);
                }
            }
        }
    }
    
    private boolean containsDangerousKeywords(String sql) {
        String sqlUpper = sql.toUpperCase();
        String[] dangerousKeywords = {"DROP", "DELETE", "TRUNCATE", "ALTER", "CREATE"};
        
        return Arrays.stream(dangerousKeywords)
                .anyMatch(keyword -> sqlUpper.contains(keyword));
    }
    
    private int countResultRows(String response) {
        try {
            if (response.startsWith("[")) {
                return JSON.parseArray(response).size();
            } else if (response.startsWith("{")) {
                return 1;
            }
        } catch (Exception e) {
            // 忽略解析错误
        }
        return 0;
    }
}
```

### 3. 功能性拦截器

#### 数据脱敏拦截器

```java
/**
 * 敏感数据脱敏拦截器
 */
@KW("data_masking")
public class DataMaskingInterceptor implements RyzeInterceptor<TestElement> {
    
    private static final Logger logger = LoggerFactory.getLogger(DataMaskingInterceptor.class);
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\d{3})\\d{4}(\\d{4})");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("(\\w{2})\\w+(@\\w+\\.\\w+)");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(\\d{6})\\d{8}(\\d{4})");
    
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 100; // 很低的优先级，最后执行
    }
    
    @Override
    public boolean supports(ContextWrapper context) {
        // 只在需要脱敏的环境中启用
        return "production".equals(System.getProperty("environment")) ||
               Boolean.parseBoolean(System.getProperty("data.masking.enabled", "false"));
    }
    
    @Override
    public void postHandle(ContextWrapper context, TestElement runtime) {
        if (context.getTestResult() instanceof SampleResult result) {
            String originalResponse = result.getResponse().bytesAsString();
            String maskedResponse = maskSensitiveData(originalResponse);
            
            if (!originalResponse.equals(maskedResponse)) {
                // 替换响应内容
                result.setResponse(new MaskedResultResponse(maskedResponse.getBytes()));
                logger.debug("敏感数据已脱敏 - 测试: {}", context.getTestResult().getTitle());
            }
        }
    }
    
    private String maskSensitiveData(String data) {
        if (StringUtils.isBlank(data)) {
            return data;
        }
        
        String masked = data;
        
        // 脱敏手机号
        masked = PHONE_PATTERN.matcher(masked).replaceAll("$1****$2");
        
        // 脱敏邮箱
        masked = EMAIL_PATTERN.matcher(masked).replaceAll("$1****$2");
        
        // 脱敏身份证
        masked = ID_CARD_PATTERN.matcher(masked).replaceAll("$1********$2");
        
        return masked;
    }
    
    private static class MaskedResultResponse implements ResultResponse {
        private final byte[] data;
        
        public MaskedResultResponse(byte[] data) {
            this.data = data;
        }
        
        @Override
        public byte[] bytes() {
            return data;
        }
        
        @Override
        public String bytesAsString() {
            return new String(data, StandardCharsets.UTF_8);
        }
        
        @Override
        public Map<String, String> getHeaders() {
            return Map.of("data-masked", "true");
        }
        
        @Override
        public String format() {
            return bytesAsString();
        }
    }
}
```

#### 重试机制拦截器

```java
/**
 * 重试机制拦截器
 */
@KW("retry")
public class RetryInterceptor implements RyzeInterceptor<TestElement> {
    
    private static final Logger logger = LoggerFactory.getLogger(RetryInterceptor.class);
    private static final String RETRY_COUNT_KEY = "retry.count";
    private static final String MAX_RETRIES_KEY = "retry.max";
    
    private final int maxRetries;
    private final long retryDelay;
    
    public RetryInterceptor() {
        this(3, 1000); // 默认重试3次，间隔1秒
    }
    
    public RetryInterceptor(int maxRetries, long retryDelay) {
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
    }
    
    @Override
    public int getOrder() {
        return 50; // 高优先级，早期介入
    }
    
    @Override
    public boolean supports(ContextWrapper context) {
        // 检查是否启用重试
        return Boolean.parseBoolean(
            context.getLocalVariable("retry.enabled", "false")
        );
    }
    
    @Override
    public boolean preHandle(ContextWrapper context, TestElement runtime) {
        // 初始化重试计数
        if (!context.getTestResult().getMetadata().containsKey(RETRY_COUNT_KEY)) {
            context.getTestResult().getMetadata().put(RETRY_COUNT_KEY, 0);
            context.getTestResult().getMetadata().put(MAX_RETRIES_KEY, maxRetries);
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(ContextWrapper context) {
        // 检查是否需要重试
        if (shouldRetry(context)) {
            int currentRetry = (Integer) context.getTestResult().getMetadata().get(RETRY_COUNT_KEY);
            currentRetry++;
            
            logger.warn("测试失败，开始第{}次重试 - 测试: {}", 
                currentRetry, context.getTestResult().getTitle());
            
            // 更新重试计数
            context.getTestResult().getMetadata().put(RETRY_COUNT_KEY, currentRetry);
            
            // 等待重试间隔
            try {
                Thread.sleep(retryDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            
            // 重置测试状态
            context.getTestResult().setStatus(TestStatus.pending);
            context.getTestResult().setThrowable(null);
            context.getTestResult().setMessage(null);
            
            // 重新执行测试
            try {
                ((TestElement) context.getTestElement()).execute(context);
            } catch (Exception e) {
                context.getTestResult().setThrowable(e);
                context.getTestResult().setStatus(TestStatus.failed);
                
                // 递归调用，继续重试检查
                afterCompletion(context);
            }
        } else if (context.getTestResult().getMetadata().containsKey(RETRY_COUNT_KEY)) {
            int retryCount = (Integer) context.getTestResult().getMetadata().get(RETRY_COUNT_KEY);
            if (retryCount > 0) {
                logger.info("重试完成 - 测试: {} - 重试次数: {} - 最终状态: {}", 
                    context.getTestResult().getTitle(), 
                    retryCount,
                    context.getTestResult().getStatus());
            }
        }
    }
    
    private boolean shouldRetry(ContextWrapper context) {
        // 只有失败的测试才重试
        if (context.getTestResult().getStatus() != TestStatus.failed) {
            return false;
        }
        
        Integer currentRetry = (Integer) context.getTestResult().getMetadata().get(RETRY_COUNT_KEY);
        Integer maxRetries = (Integer) context.getTestResult().getMetadata().get(MAX_RETRIES_KEY);
        
        return currentRetry != null && maxRetries != null && currentRetry < maxRetries;
    }
}
```

## 拦截器注册

### SPI 注册

创建文件 `src/main/resources/META-INF/services/io.github.xiaomisum.ryze.core.interceptor.RyzeInterceptor`：

```
com.example.LoggingInterceptor
com.example.PerformanceMonitorInterceptor
com.example.HttpInterceptor
com.example.DatabaseInterceptor
com.example.DataMaskingInterceptor
com.example.RetryInterceptor
```

### 程序化注册

```java
public class InterceptorConfiguration {
    
    public static void configureInterceptors() {
        // 创建拦截器配置
        InterceptorConfigureItem interceptors = new InterceptorConfigureItem();
        
        // 添加拦截器
        interceptors.add(new LoggingInterceptor());
        interceptors.add(new PerformanceMonitorInterceptor());
        interceptors.add(new HttpInterceptor());
        interceptors.add(new DataMaskingInterceptor());
        interceptors.add(new RetryInterceptor(5, 2000)); // 5次重试，间隔2秒
        
        // 应用到全局配置
        Configure configure = Configure.newDefaultConfigure();
        configure.getGlobalContext().getConfigGroup().put("interceptors", interceptors);
    }
}
```

## 拦截器使用

### 在测试中使用

```java
@Test
@RyzeTest
public void testWithInterceptors() {
    MagicBox.http("带拦截器的测试", http -> {
        http.config(config -> config
            .method("GET")
            .url("https://api.example.com/users")
        );
        
        // 添加拦截器
        http.interceptors(interceptors -> 
            interceptors.add(new LoggingInterceptor())
                        .add(new PerformanceMonitorInterceptor())
                        .add(new HttpInterceptor())
        );
    });
}
```

### 在测试套件中使用

```java
@Test
@RyzeTest
public void testSuiteWithInterceptors() {
    MagicBox.suite("拦截器测试套件", suite -> {
        // 套件级拦截器
        suite.interceptors(interceptors -> 
            interceptors.add(new LoggingInterceptor())
                        .add(new PerformanceMonitorInterceptor())
        );
        
        suite.children(children -> {
            children.http("HTTP测试1", http -> {
                http.config(config -> config.method("GET").url("https://api1.example.com"));
                // 测试级拦截器
                http.interceptors(interceptors -> interceptors.add(new HttpInterceptor()));
            });
            
            children.http("HTTP测试2", http -> {
                http.config(config -> config.method("POST").url("https://api2.example.com"));
            });
        });
    });
}
```

## 开发规范

### 1. 优先级设计

- **0-99**: 系统核心拦截器（框架内部使用）
- **100-199**: 高优先级业务拦截器（日志、安全等）
- **200-799**: 一般业务拦截器（协议特定、功能性等）
- **800-999**: 低优先级拦截器（性能监控、数据处理等）
- **1000+**: 最低优先级拦截器（清理、收尾工作等）

### 2. 异常处理

```java
@Override
public boolean preHandle(ContextWrapper context, TestElement runtime) {
    try {
        // 拦截器逻辑
        return true;
    } catch (Exception e) {
        logger.error("拦截器执行异常", e);
        // 决定是否继续执行
        return true; // 或者 false
    }
}
```

### 3. 资源管理

```java
public class ResourceManagedInterceptor implements RyzeInterceptor<TestElement> {
    
    private final Map<String, AutoCloseable> resources = new ConcurrentHashMap<>();
    
    @Override
    public boolean preHandle(ContextWrapper context, TestElement runtime) {
        try {
            // 创建资源
            AutoCloseable resource = createResource();
            resources.put(context.getTestResult().getId(), resource);
            return true;
        } catch (Exception e) {
            logger.error("资源创建失败", e);
            return false;
        }
    }
    
    @Override
    public void afterCompletion(ContextWrapper context) {
        // 清理资源
        AutoCloseable resource = resources.remove(context.getTestResult().getId());
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                logger.warn("资源清理异常", e);
            }
        }
    }
}
```

### 4. 线程安全

```java
public class ThreadSafeInterceptor implements RyzeInterceptor<TestElement> {
    
    // 使用线程安全的集合
    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
    
    // 使用 ThreadLocal 存储线程特定数据
    private final ThreadLocal<Context> threadContext = new ThreadLocal<>();
    
    @Override
    public boolean preHandle(ContextWrapper context, TestElement runtime) {
        // 线程安全的操作
        Context ctx = new Context();
        threadContext.set(ctx);
        
        return true;
    }
    
    @Override
    public void afterCompletion(ContextWrapper context) {
        // 清理 ThreadLocal
        threadContext.remove();
    }
}
```

## 测试拦截器

### 单元测试示例

```java
class LoggingInterceptorTest {
    
    private LoggingInterceptor interceptor;
    private ContextWrapper context;
    private TestElement testElement;
    
    @BeforeEach
    void setUp() {
        interceptor = new LoggingInterceptor();
        context = mock(ContextWrapper.class);
        testElement = mock(TestElement.class);
        
        TestResult testResult = new TestResult("test-id", "测试标题");
        when(context.getTestResult()).thenReturn(testResult);
    }
    
    @Test
    void shouldSupportAllTestElements() {
        // When & Then
        assertTrue(interceptor.supports(context));
    }
    
    @Test
    void shouldRecordStartTime() {
        // When
        boolean result = interceptor.preHandle(context, testElement);
        
        // Then
        assertTrue(result);
        assertNotNull(context.getTestResult().getMetadata().get("start_time"));
    }
    
    @Test
    void shouldCalculateDuration() {
        // Given
        context.getTestResult().getMetadata().put("start_time", System.currentTimeMillis() - 1000);
        
        // When
        interceptor.postHandle(context, testElement);
        
        // Then
        Long duration = (Long) context.getTestResult().getMetadata().get("duration");
        assertNotNull(duration);
        assertTrue(duration >= 1000);
    }
}
```

## 最佳实践

1. **单一职责**：每个拦截器只负责一个特定功能
2. **最小侵入**：避免修改原始测试逻辑
3. **异常隔离**：拦截器异常不应影响测试执行
4. **性能考虑**：避免在拦截器中执行耗时操作
5. **资源管理**：及时释放占用的资源
6. **日志规范**：使用适当的日志级别
7. **配置灵活**：支持动态开启/关闭
8. **测试覆盖**：编写完整的单元测试

通过遵循这些指导原则，您可以开发出高质量、可靠且高性能的拦截器组件。