# 🔧 函数开发指南

## 概述

函数是 Ryze 框架中用于生成动态数据的重要组件。通过实现 `Function` 接口，您可以创建自定义函数来满足特定的测试需求，如生成随机数据、处理字符串、执行计算等。

## 函数接口

### 基本接口定义

```java
public interface Function {
    /**
     * 获取函数的唯一标识符
     * @return 函数标识符，用于在表达式中调用
     */
    String key();
    
    /**
     * 执行函数逻辑
     * @param context 上下文对象
     * @param args 参数列表
     * @return 函数执行结果
     */
    Object execute(ContextWrapper context, Args args);
    
    /**
     * 检查函数参数数量是否符合要求
     * @param args 参数列表
     * @param minCnt 最小参数数量
     * @param maxCnt 最大参数数量
     */
    default void checkMethodArgCount(Args args, int minCnt, int maxCnt) {
        if (args.size() < minCnt || args.size() > maxCnt) {
            throw new RuntimeException(
                String.format("函数 %s 参数数量错误, 期望 %d ~ %d, 实际 %d", 
                    key(), minCnt, maxCnt, args.size())
            );
        }
    }
}
```

### 参数处理工具

```java
public class Args {
    // 获取字符串参数
    public String getString(int index);
    public String getFirstString();
    
    // 获取数值参数
    public int getIntValue(int index);
    public double getDoubleValue(int index);
    public boolean getBooleanValue(int index);
    
    // 获取对象参数
    public Object getFirst();
    public Object get(int index);
    
    // 参数数量和检查
    public int size();
    public boolean isEmpty();
}
```

## 函数开发实例

### 1. 基础函数示例

#### 随机字符串函数

```java
/**
 * 生成指定长度的随机字符串
 * 使用方式：${custom_random(10)} 或 ${custom_random(8, "abc123", true)}
 */
public class CustomRandomFunction implements Function {
    
    @Override
    public String key() {
        return "custom_random";
    }
    
    @Override
    public Object execute(ContextWrapper context, Args args) {
        // 参数验证：0-3个参数
        checkMethodArgCount(args, 0, 3);
        
        // 获取参数，设置默认值
        int length = args.size() > 0 ? args.getIntValue(0) : 8;
        String chars = args.size() > 1 ? args.getString(1) : null;
        boolean uppercase = args.size() > 2 ? args.getBooleanValue(2) : false;
        
        // 生成随机字符串
        String result;
        if (StringUtils.isBlank(chars)) {
            result = RandomStringUtils.secure().nextAlphabetic(length);
        } else {
            result = RandomStringUtils.secure().next(length, chars);
        }
        
        return uppercase ? result.toUpperCase() : result;
    }
}
```

#### 数学计算函数

```java
/**
 * 数学计算函数
 * 使用方式：${math("add", 10, 20)} 或 ${math("multiply", 5, 3)}
 */
public class MathFunction implements Function {
    
    @Override
    public String key() {
        return "math";
    }
    
    @Override
    public Object execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 3, 3);
        
        String operation = args.getString(0);
        double num1 = args.getDoubleValue(1);
        double num2 = args.getDoubleValue(2);
        
        return switch (operation.toLowerCase()) {
            case "add", "+" -> num1 + num2;
            case "subtract", "-" -> num1 - num2;
            case "multiply", "*" -> num1 * num2;
            case "divide", "/" -> {
                if (num2 == 0) {
                    throw new IllegalArgumentException("除数不能为零");
                }
                yield num1 / num2;
            }
            case "power", "pow" -> Math.pow(num1, num2);
            case "mod", "%" -> num1 % num2;
            default -> throw new IllegalArgumentException("不支持的运算: " + operation);
        };
    }
}
```

### 2. 高级函数示例

#### 数据库查询函数

```java
/**
 * 数据库查询函数
 * 使用方式：${db_query("SELECT name FROM users WHERE id=?", "123")}
 */
public class DatabaseQueryFunction implements Function {
    
    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    
    @Override
    public String key() {
        return "db_query";
    }
    
    @Override
    public Object execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 1, 10);
        
        String sql = args.getString(0);
        Object[] params = args.stream().skip(1).toArray();
        
        try {
            // 从上下文获取数据源名称，默认使用 "default"
            String dataSourceName = context.getLocalVariable("datasource", "default");
            DataSource dataSource = getDataSource(dataSourceName, context);
            
            return executeQuery(dataSource, sql, params);
            
        } catch (Exception e) {
            throw new RuntimeException("数据库查询失败: " + e.getMessage(), e);
        }
    }
    
    private DataSource getDataSource(String name, ContextWrapper context) {
        return dataSources.computeIfAbsent(name, key -> {
            // 从上下文配置中创建数据源
            String url = context.getGlobalVariable(key + ".url");
            String username = context.getGlobalVariable(key + ".username");
            String password = context.getGlobalVariable(key + ".password");
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(5);
            config.setConnectionTimeout(30000);
            
            return new HikariDataSource(config);
        });
    }
    
    private Object executeQuery(DataSource dataSource, String sql, Object[] params) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // 设置参数
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            // 执行查询
            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
                
                // 如果只有一行一列，直接返回值
                if (results.size() == 1 && results.get(0).size() == 1) {
                    return results.get(0).values().iterator().next();
                }
                
                // 如果只有一行，返回 Map
                if (results.size() == 1) {
                    return results.get(0);
                }
                
                // 否则返回 List
                return results;
            }
        }
    }
}
```

#### HTTP 请求函数

```java
/**
 * HTTP 请求函数
 * 使用方式：${http_get("https://api.example.com/users/123")}
 */
public class HttpRequestFunction implements Function {
    
    private final OkHttpClient httpClient;
    
    public HttpRequestFunction() {
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    }
    
    @Override
    public String key() {
        return "http_get";
    }
    
    @Override
    public Object execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 1, 2);
        
        String url = args.getString(0);
        String returnType = args.size() > 1 ? args.getString(1) : "body";
        
        try {
            Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                return switch (returnType.toLowerCase()) {
                    case "status", "code" -> response.code();
                    case "body", "content" -> {
                        ResponseBody body = response.body();
                        yield body != null ? body.string() : null;
                    }
                    case "json" -> {
                        ResponseBody body = response.body();
                        if (body != null) {
                            String content = body.string();
                            yield JSON.parseObject(content);
                        }
                        yield null;
                    }
                    case "headers" -> {
                        Map<String, String> headers = new HashMap<>();
                        response.headers().forEach(pair -> 
                            headers.put(pair.getFirst(), pair.getSecond())
                        );
                        yield headers;
                    }
                    default -> throw new IllegalArgumentException("不支持的返回类型: " + returnType);
                };
            }
        } catch (Exception e) {
            throw new RuntimeException("HTTP 请求失败: " + e.getMessage(), e);
        }
    }
}
```

### 3. 工具类函数

#### 文件操作函数

```java
/**
 * 文件读取函数
 * 使用方式：${file_read("data/test.txt")} 或 ${file_read("data/test.json", "json")}
 */
public class FileReadFunction implements Function {
    
    @Override
    public String key() {
        return "file_read";
    }
    
    @Override
    public Object execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 1, 2);
        
        String filePath = args.getString(0);
        String format = args.size() > 1 ? args.getString(1) : "text";
        
        try {
            // 支持相对路径和绝对路径
            Path path = Paths.get(filePath);
            if (!path.isAbsolute()) {
                // 相对于项目根目录
                String projectRoot = System.getProperty("user.dir");
                path = Paths.get(projectRoot, filePath);
            }
            
            if (!Files.exists(path)) {
                throw new FileNotFoundException("文件不存在: " + path);
            }
            
            String content = Files.readString(path, StandardCharsets.UTF_8);
            
            return switch (format.toLowerCase()) {
                case "text", "string" -> content;
                case "json" -> JSON.parseObject(content);
                case "yaml", "yml" -> {
                    Yaml yaml = new Yaml();
                    yield yaml.load(content);
                }
                case "lines" -> Arrays.asList(content.split("\\n"));
                case "csv" -> parseCsv(content);
                default -> throw new IllegalArgumentException("不支持的格式: " + format);
            };
            
        } catch (Exception e) {
            throw new RuntimeException("文件读取失败: " + e.getMessage(), e);
        }
    }
    
    private List<Map<String, String>> parseCsv(String content) {
        String[] lines = content.split("\\n");
        if (lines.length < 2) {
            return Collections.emptyList();
        }
        
        String[] headers = lines[0].split(",");
        List<Map<String, String>> results = new ArrayList<>();
        
        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            Map<String, String> row = new LinkedHashMap<>();
            
            for (int j = 0; j < Math.min(headers.length, values.length); j++) {
                row.put(headers[j].trim(), values[j].trim());
            }
            results.add(row);
        }
        
        return results;
    }
}
```

#### 环境变量函数

```java
/**
 * 环境变量获取函数
 * 使用方式：${env("JAVA_HOME")} 或 ${env("API_KEY", "default_key")}
 */
public class EnvironmentFunction implements Function {
    
    @Override
    public String key() {
        return "env";
    }
    
    @Override
    public Object execute(ContextWrapper context, Args args) {
        checkMethodArgCount(args, 1, 2);
        
        String varName = args.getString(0);
        String defaultValue = args.size() > 1 ? args.getString(1) : null;
        
        // 优先从系统环境变量获取
        String value = System.getenv(varName);
        
        // 如果环境变量不存在，尝试从系统属性获取
        if (value == null) {
            value = System.getProperty(varName);
        }
        
        // 如果都没有，使用默认值
        if (value == null) {
            value = defaultValue;
        }
        
        return value;
    }
}
```

## 函数注册

### SPI 注册

创建文件 `src/main/resources/META-INF/services/io.github.xiaomisum.ryze.core.function.Function`：

```
com.example.CustomRandomFunction
com.example.MathFunction
com.example.DatabaseQueryFunction
com.example.HttpRequestFunction
com.example.FileReadFunction
com.example.EnvironmentFunction
```

### 动态注册

```java
public class FunctionRegistry {
    
    public static void registerCustomFunctions() {
        // 在运行时动态注册函数
        ApplicationConfig.getFunctions().addAll(Arrays.asList(
            new CustomRandomFunction(),
            new MathFunction(),
            new DatabaseQueryFunction(),
            new HttpRequestFunction(),
            new FileReadFunction(),
            new EnvironmentFunction()
        ));
    }
}
```

## 函数使用

### 在测试配置中使用

```yaml
title: 用户注册测试
testclass: http
config:
  method: POST
  url: "https://api.example.com/users"
  body:
    name: "${faker(\"name.fullName\")}"
    email: "${custom_random(8)}@test.com"
    age: "${math(\"add\", 20, 10)}"
    id: "${db_query(\"SELECT MAX(id) + 1 FROM users\")}"
    config_value: "${env(\"API_CONFIG\", \"default_config\")}"
```

### 在 Java 代码中使用

```java
@Test
@RyzeTest
public void testWithFunctions() {
    MagicBox.http("函数测试", http -> {
        http.config(config -> config
            .method("POST")
            .url("${env(\"API_BASE_URL\")}/users")
            .body(body -> {
                body.put("name", "${faker(\"name.fullName\")}");
                body.put("email", "${custom_random(10)}@example.com");
                body.put("score", "${math(\"multiply\", 85, 1.2)}");
                body.put("data", "${file_read(\"testdata/user.json\", \"json\")}");
            })
        );
    });
}
```

## 开发规范

### 1. 命名规范

- 函数名使用小写字母和下划线
- 避免与内置函数冲突
- 名称应具有描述性

### 2. 参数处理

- 始终验证参数数量和类型
- 提供合理的默认值
- 处理参数转换异常

### 3. 异常处理

- 使用 `RuntimeException` 包装异常
- 提供清晰的错误信息
- 包含必要的上下文信息

### 4. 性能考虑

- 避免在函数中执行耗时操作
- 合理使用缓存机制
- 注意资源管理和释放

### 5. 线程安全

- 函数实例可能被多线程调用
- 避免使用实例变量存储状态
- 使用线程安全的工具类

## 测试函数

### 单元测试示例

```java
class CustomFunctionTest {
    
    private CustomRandomFunction function;
    private ContextWrapper context;
    
    @BeforeEach
    void setUp() {
        function = new CustomRandomFunction();
        context = mock(ContextWrapper.class);
    }
    
    @Test
    void shouldGenerateRandomString() {
        // Given
        Args args = new Args(Arrays.asList("10"));
        
        // When
        String result = (String) function.execute(context, args);
        
        // Then
        assertThat(result).hasSize(10);
        assertThat(result).matches("[a-zA-Z]+");
    }
    
    @Test
    void shouldUseDefaultLength() {
        // Given
        Args args = new Args(Collections.emptyList());
        
        // When
        String result = (String) function.execute(context, args);
        
        // Then
        assertThat(result).hasSize(8);
    }
    
    @Test
    void shouldThrowExceptionForInvalidParams() {
        // Given
        Args args = new Args(Arrays.asList("10", "chars", "upper", "extra"));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            function.execute(context, args);
        });
    }
}
```

## 最佳实践

1. **功能单一**：每个函数只负责一个特定功能
2. **参数清晰**：提供清晰的参数文档和示例
3. **错误处理**：完善的异常处理和错误提示
4. **性能优化**：避免重复计算和资源浪费
5. **兼容性**：保持向后兼容性
6. **文档完整**：提供详细的使用文档
7. **测试覆盖**：编写完整的单元测试

通过遵循这些指导原则，您可以开发出高质量、可靠且易用的自定义函数。