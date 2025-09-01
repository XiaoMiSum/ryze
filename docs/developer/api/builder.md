# 构建器 API

构建器API提供了流畅的API来构建各种测试元素，使用建造者模式让配置更加清晰和易于理解。

## SuiteBuilder 类

测试套件构建器，用于构建完整的测试套件。

### 主要方法

#### variables(String name, Object value)
添加变量到测试套件上下文中。

#### configure(Consumer&lt;ConfigureElementsBuilder&gt; consumer)
配置测试元素的默认设置。

#### preprocessors(Consumer&lt;PreprocessorsBuilder&gt; consumer)
添加前置处理器。

#### children(Consumer&lt;ChildrenBuilder&gt; consumer)
添加子测试元素。

#### postprocessors(Consumer&lt;PostprocessorsBuilder&gt; consumer)
添加后置处理器。

## HttpSamplerBuilder 类

HTTP取样器构建器，用于构建HTTP请求测试。

### 主要方法

#### title(String title)
设置取样器标题。

#### config(Consumer&lt;HttpConfigBuilder&gt; consumer)
配置HTTP请求参数。

#### variables(String name, Object value)
添加变量。

#### extractors(Consumer&lt;ExtractorsBuilder&gt; consumer)
配置数据提取器。

#### assertions(Consumer&lt;AssertionsBuilder&gt; consumer)
配置断言。

## DubboSamplerBuilder 类

Dubbo取样器构建器，用于构建Dubbo服务调用测试。

### 主要方法

#### title(String title)
设置取样器标题。

#### registry(String ref)
设置注册中心引用。

#### interfaceName(String interfaceName)
设置接口名称。

#### methodName(String methodName)
设置方法名称。

#### parameterTypes(String... types)
设置参数类型。

#### parameters(Object... params)
设置参数值。

#### extractor(Consumer&lt;ExtractorBuilder&gt; consumer)
配置数据提取器。

#### assertion(Consumer&lt;AssertionBuilder&gt; consumer)
配置断言。

## JdbcSamplerBuilder 类

JDBC取样器构建器，用于构建数据库查询测试。

### 主要方法

#### title(String title)
设置取样器标题。

#### datasource(String ref)
设置数据源引用。

#### sql(String sql)
设置SQL语句。

#### parameters(Object... params)
设置SQL参数。

#### extractor(Consumer&lt;ExtractorBuilder&gt; consumer)
配置数据提取器。

#### assertions(Consumer&lt;AssertionsBuilder&gt; consumer)
配置断言。

## RedisSamplerBuilder 类

Redis取样器构建器，用于构建Redis操作测试。

### 主要方法

#### title(String title)
设置取样器标题。

#### datasource(String ref)
设置数据源引用。

#### command(String command)
设置Redis命令。

#### args(Object... args)
设置命令参数。

#### extractor(Consumer&lt;ExtractorBuilder&gt; consumer)
配置数据提取器。

#### assertions(Consumer&lt;AssertionsBuilder&gt; consumer)
配置断言。