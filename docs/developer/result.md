# 📊 结果数据结构参考

## 📖 概述

结果树（Result）是测试执行的完整留档，包含测试套件、取样器、处理器、验证器、提取器、变量等所有运行时数据。本文档列出 Result
树中各模型的完整属性，供报告集成、Allure 渲染、外部系统消费时查阅。

> 配套设计文档：[架构设计](architecture.md)｜源码：`io.github.xiaomisum.ryze.result` 包 + `SampleResult.java`

---

## 🌳 结果树结构

```
Result（抽象基类）
├── TestSuiteResult（suite）
│   └── children[] : Result            ← 子套件 / 取样器结果
└── SampleResult（sampler / processor）
    ├── assertions[] : AssertionResult
    ├── extractors[] : ExtractorResult
    ├── variables[]  : VariableRecord
    ├── request      : RealRequest   （按协议具体化）
    └── response     : RealResponse  （按协议具体化）
```

`preprocessors[]` / `postprocessors[]` 挂在任意 `Result` 上，元素是处理器自己的 `Result`（取样器类处理器为 `SampleResult`
），不进入 `children`。

---

## 🧱 核心模型属性

### Result（基类）

所有结果类的公共字段，`TestSuiteResult` 和 `SampleResult` 均继承。

| 属性             | 类型                  | 说明                                              |
|------------------|-----------------------|---------------------------------------------------|
| `id`             | String                | 测试元素 ID                                       |
| `title`          | String                | 测试元素标题                                      |
| `status`         | TestStatus            | 执行状态，默认 `passed`                           |
| `startTime`      | LocalDateTime         | 开始时间                                          |
| `endTime`        | LocalDateTime         | 结束时间                                          |
| `throwable`      | Throwable             | 执行异常                                          |
| `rejectBy`       | String                | 拦截器标识（被拦截时非 null，如 `XXInterceptor`） |
| `metadata`       | Map\<String,Object>   | 元件元数据，浅拷贝留档，不做求值                  |
| `variables`      | List\<VariableRecord> | 本次执行产生/变更的变量增量                       |
| `preprocessors`  | List\<Result>         | 前置处理器结果                                    |
| `postprocessors` | List\<Result>         | 后置处理器结果                                    |

### TestSuiteResult（suite）

继承 `Result` 全部属性，另加：

| 属性       | 类型          | 说明                          |
|------------|---------------|-------------------------------|
| `children` | List\<Result> | 子测试元素结果（套件/取样器） |

### SampleResult（sampler）

继承 `Result` 全部属性，另加：

| 属性              | 类型                   | 说明                                                                              |
|-------------------|------------------------|-----------------------------------------------------------------------------------|
| `sampleStartTime` | LocalDateTime          | 取样开始时间                                                                      |
| `sampleEndTime`   | LocalDateTime          | 取样结束时间                                                                      |
| `duration`        | String                 | 由上述两时间计算耗时；任一为空时输出空串                                          |
| `request`         | RealRequest            | 实际请求数据（按协议具体化，见 [各协议字段](#各协议-request--response-完整属性)） |
| `response`        | RealResponse           | 实际响应数据（按协议具体化，见 [各协议字段](#各协议-request--response-完整属性)） |
| `assertions`      | List\<AssertionResult> | 验证器执行记录                                                                    |
| `extractors`      | List\<ExtractorResult> | 提取器执行记录                                                                    |

> 无个性化子类，`DefaultSampleResult` 即 `SampleResult`。

### AssertionResult（断言/验证器记录）

| 属性       | 类型       | 说明                                                        |
|------------|------------|-------------------------------------------------------------|
| `field`    | String     | 验证字段                                                    |
| `rule`     | String     | 验证规则（`==`、`contains` …）                              |
| `expected` | Object     | 期望值（已求值）                                            |
| `actual`   | Object     | 实际值                                                      |
| `status`   | TestStatus | 执行状态（passed / failed / skipped），未执行默认 `skipped` |
| `message`  | String     | 失败/异常消息                                               |

### ExtractorResult（提取器记录）

| 属性           | 类型    | 说明                            |
|----------------|---------|---------------------------------|
| `refName`      | String  | 提取后保存的变量名              |
| `field`        | String  | 提取表达式（JsonPath / 正则等） |
| `value`        | Object  | 提取到的值                      |
| `defaultValue` | boolean | 是否走了默认值分支              |
| `message`      | String  | 提取失败信息                    |

### VariableRecord（变量增量记录）

| 属性    | 类型   | 说明                                                           |
|---------|--------|----------------------------------------------------------------|
| `name`  | String | 变量名                                                         |
| `raw`   | Object | 原始值（定义形态，求值前的模板/定义值；提取器新增变量为 null） |
| `value` | Object | 本元件执行完后的实际值                                         |

### TestStatus（状态枚举）

| 枚举值     | Allure 映射 | 语义                           |
|------------|-------------|--------------------------------|
| `disabled` | SKIPPED     | 禁用，不执行                   |
| `passed`   | PASSED      | 执行成功，所有验证通过（默认） |
| `failed`   | FAILED      | 断言失败                       |
| `broken`   | BROKEN      | 系统错误/异常                  |
| `skipped`  | SKIPPED     | 被跳过（含断言未执行）         |

---

## 📡 Request / Response 契约基类

### RealRequest（`SampleResult.RealRequest`）

| 属性     | 类型   | 说明                                                   |
|----------|--------|--------------------------------------------------------|
| `format` | String | 契约字段。可读请求文本，由各协议子类的 `format()` 实现 |

抽象方法：`format()`、`bytes()`（请求字节，供 `bytesAsString` 等外部读取）。

### RealResponse（`SampleResult.RealResponse`）

| 属性     | 类型   | 说明                                                   |
|----------|--------|--------------------------------------------------------|
| `status` | int    | 契约字段，响应状态码（各实现构造时写入）               |
| `format` | String | 契约字段，可读响应文本，由各协议子类的 `format()` 实现 |

抽象方法：`bytes()`、`format()`。另有 `bytesAsString()`。

### 默认实现（协议未自定义时使用）

| 类                    | 属性                             | 说明                           |
|-----------------------|----------------------------------|--------------------------------|
| `DefaultRealRequest`  | `format`/`bytes`                 | `format()` 输出字节串或 `"ok"` |
| `DefaultRealResponse` | `status` = 200、`format`/`bytes` | `format()` 输出字节串或 `"ok"` |

---

## 📋 各协议 Request / Response 完整属性

> 以下字段均为该协议 Real 类的 getter 暴露属性（序列化即按此输出）。`byte[]` 类型字段序列化为 JSON
> 整数数组。密码类字段原样暴露（信息来自外部系统）。

### HTTP（模块 `ryze`）

**Request** `RealHTTPRequest`

| 属性      | 类型          | 说明                                        |
|-----------|---------------|---------------------------------------------|
| `format`  | String        | 请求行 + 请求头 + Query + Body 的格式化文本 |
| `url`     | String        | 请求 URL                                    |
| `method`  | String        | HTTP 方法                                   |
| `query`   | String        | 查询参数字符串                              |
| `body`    | byte[]        | 请求体字节                                  |
| `version` | String        | 协议版本，如 `HTTP/1.1`                     |
| `headers` | List\<Header> | 请求头（`org.apache.hc.core5.http.Header`） |

**Response** `RealHTTPResponse`

| 属性      | 类型          | 说明                                        |
|-----------|---------------|---------------------------------------------|
| `status`  | int           | 状态码                                      |
| `format`  | String        | 响应行 + 响应头 + Body 的格式化文本         |
| `headers` | List\<Header> | 响应头（`org.apache.hc.core5.http.Header`） |
| `version` | String        | 协议版本                                    |
| `message` | String        | 状态消息                                    |
| `body`    | byte[]        | 响应体字节                                  |

### Redis（模块 `ryze`）

**Request** `RealRedisRequest`（实现 `RedisConstantsInterface`）

| 属性      | 类型          | 说明                |
|-----------|---------------|---------------------|
| `format`  | String        | `url` + 命令 + 参数 |
| `url`     | String        | Redis 连接 URL      |
| `command` | String        | 命令                |
| `args`    | List\<String> | 命令参数            |

**Response** 默认 `DefaultRealResponse`（状态 200，文本为返回结果字符串）。

### JDBC（模块 `ryze`）

**Request** `RealJDBCRequest`（实现 `JDBCConstantsInterface`）

| 属性       | 类型          | 说明                  |
|------------|---------------|-----------------------|
| `format`   | String        | 连接信息 + SQL + 参数 |
| `url`      | String        | 数据库连接 URL        |
| `username` | String        | 用户名                |
| `password` | String        | 密码                  |
| `sql`      | String        | SQL 语句              |
| `args`     | List\<Object> | SQL 参数              |

**Response** 默认 `DefaultRealResponse`。

### E-Mail（模块 `ryze`）

**Request** `RealEMailRequest`

| 属性          | 类型    | 说明                |
|---------------|---------|---------------------|
| `format`      | String  | `To / Title / 正文` |
| `host`        | String  | SMTP 主机           |
| `port`        | String  | 端口                |
| `useSSL`      | Boolean | 是否 SSL            |
| `useStarttls` | Boolean | 是否 STARTTLS       |
| `username`    | String  | 用户名              |
| `password`    | String  | 密码                |
| `to`          | String  | 收件人              |
| `title`       | String  | 邮件标题            |
| `content`     | String  | 邮件内容            |

**Response** 默认 `DefaultRealResponse`。

### RabbitMQ（模块 `ryze-rabbit`）

**Request** `RealRabbitRequest`

| 属性          | 类型                         | 说明                                         |
|---------------|------------------------------|----------------------------------------------|
| `format`      | String                       | 地址/认证/虚拟主机 + 队列/交换机 JSON + 消息 |
| `address`     | String                       | 服务器地址（`host:port`）                    |
| `username`    | String                       | 用户名（默认 `guest`）                       |
| `password`    | String                       | 密码（默认 `guest`）                         |
| `virtualHost` | String                       | 虚拟主机（默认 `/`）                         |
| `queue`       | RabbitConfigureItem.Queue    | 队列配置                                     |
| `exchange`    | RabbitConfigureItem.Exchange | 交换机配置                                   |
| `message`     | String                       | 消息内容                                     |

**Queue**（嵌套对象）

| 属性         | 类型                |
|--------------|---------------------|
| `name`       | String              |
| `durable`    | Boolean             |
| `exclusive`  | Boolean             |
| `autoDelete` | Boolean             |
| `arguments`  | Map\<String,Object> |

**Exchange**（嵌套对象）

| 属性         | 类型   |
|--------------|--------|
| `name`       | String |
| `type`       | String |
| `routingKey` | String |

**Response** 默认 `DefaultRealResponse`。

### ActiveMQ（模块 `ryze-active`）

**Request** `RealActiveRequest`

| 属性       | 类型   | 说明                           |
|------------|--------|--------------------------------|
| `format`   | String | 地址/认证 + topic/queue + 消息 |
| `address`  | String | Broker URL                     |
| `topic`    | String | 目标 Topic                     |
| `queue`    | String | 目标 Queue                     |
| `username` | String | 用户名                         |
| `password` | String | 密码                           |
| `message`  | String | 消息内容                       |

**Response** 默认 `DefaultRealResponse`。

### Kafka（模块 `ryze-kafka`）

**Request** `RealKafkaRequest`

| 属性      | 类型   | 说明                      |
|-----------|--------|---------------------------|
| `format`  | String | 地址 + topic + key + 消息 |
| `address` | String | Bootstrap Servers         |
| `topic`   | String | 主题                      |
| `key`     | String | 消息键                    |
| `message` | String | 消息内容                  |

**Response** 默认 `DefaultRealResponse`。

### MQTT（模块 `ryze-mqtt`）

**Request** `RealMqttRequest`

| 属性      | 类型   | 说明                  |
|-----------|--------|-----------------------|
| `format`  | String | Topic + QoS + Payload |
| `topic`   | String | 主题                  |
| `qos`     | int    | QoS 等级              |
| `payload` | String | 负载                  |

**Response** `RealMqttResponse`

| 属性     | 类型   | 说明                      |
|----------|--------|---------------------------|
| `status` | int    | 收到消息 `0`，无消息 `-1` |
| `format` | String | status + Body             |
| `body`   | String | 接收到的消息内容          |

### CoAP（模块 `ryze-coap`）

**Request** `RealCoapRequest`

| 属性            | 类型    | 说明                                                  |
|-----------------|---------|-------------------------------------------------------|
| `format`        | String  | 方法 + URI + Type(CON/NON) + Content-Format + Payload |
| `method`        | String  | 请求方法                                              |
| `uri`           | String  | 请求 URI                                              |
| `payload`       | String  | 请求体                                                |
| `contentFormat` | String  | 内容格式                                              |
| `confirmable`   | boolean | 是否 CON 消息（getter `isConfirmable()`）             |

**Response** `RealCoapResponse`

| 属性           | 类型   | 说明                                                  |
|----------------|--------|-------------------------------------------------------|
| `status`       | int    | 响应码 value（无响应 `-1`）                           |
| `format`       | String | 响应码 + Payload                                      |
| `body`         | String | 响应体                                                |
| `responseCode` | String | 响应码描述（如 `2.05 Content`；无响应 `No Response`） |

### WebSocket（模块 `ryze-websocket`）

**Request** `RealWebsocketRequest`

| 属性      | 类型                | 说明                                              |
|-----------|---------------------|---------------------------------------------------|
| `format`  | String              | URL + 头 + Query + Body                           |
| `url`     | String              | 连接 URL                                          |
| `query`   | String              | 查询参数                                          |
| `body`    | String              | 请求体（优先字节转字符串，回退 `request.body()`） |
| `headers` | Map\<String,String> | 请求头                                            |

**Response** `RealWebsocketResponse`

| 属性     | 类型   | 说明                              |
|----------|--------|-----------------------------------|
| `status` | int    | 关闭状态码（`response.status()`） |
| `format` | String | status + Body                     |
| `body`   | String | 响应文本                          |

### Proto（模块 `ryze-proto`）

**Request** `RealProtoRequest`

| 属性      | 类型                | 说明                                  |
|-----------|---------------------|---------------------------------------|
| `format`  | String              | 方法 + URL + 版本 + 头 + Query + Body |
| `url`     | String              | 请求 URL                              |
| `method`  | String              | 方法                                  |
| `query`   | String              | 查询参数                              |
| `body`    | String              | 请求体                                |
| `version` | String              | 协议版本                              |
| `headers` | Map\<String,Object> | 请求头                                |

**Response** `RealProtoResponse`

| 属性      | 类型                | 说明                                      |
|-----------|---------------------|-------------------------------------------|
| `status`  | int                 | 状态码                                    |
| `format`  | String              | 版本 + 状态码 + 消息 + 头 + Body          |
| `message` | String              | 状态消息                                  |
| `body`    | String              | 经 `ProtoClient.convert` 转换后的响应文本 |
| `version` | String              | 协议版本                                  |
| `headers` | Map\<String,String> | 响应头                                    |

### Dubbo（模块 `ryze-dubbo`）

**Request** `RealDubboRequest`

| 属性             | 类型                | 说明                                              |
|------------------|---------------------|---------------------------------------------------|
| `format`         | String              | 注册中心 + 接口/方法 + 参数类型 + 参数 + 附加参数 |
| `address`        | String              | 注册中心地址（如 `zookeeper://127.0.0.1:2181`）   |
| `interfaceName`  | String              | 接口全限定类名                                    |
| `method`         | String              | 方法名                                            |
| `parameterTypes` | List\<String>       | 参数类型列表                                      |
| `attachmentArgs` | Map\<String,String> | 附加参数                                          |
| `parameters`     | List\<Object>       | 调用参数值                                        |

**Response** 默认 `DefaultRealResponse`。

### MongoDB（模块 `ryze-mongo`）

**Request** `MongoRealRequest`

| 属性         | 类型                | 说明                                                        |
|--------------|---------------------|-------------------------------------------------------------|
| `format`     | String              | 地址 + database + collection + action + Condition/Data JSON |
| `url`        | String              | 连接地址                                                    |
| `database`   | String              | 数据库名                                                    |
| `collection` | String              | 集合名                                                      |
| `action`     | String              | 操作类型（find、insert、update、delete）                    |
| `condition`  | Map\<String,Object> | 操作条件                                                    |
| `data`       | Object              | 操作数据（运行时为 Map / List）                             |

**Response** 默认 `DefaultRealResponse`。

### 无自定义 Response 的协议

Redis / JDBC / EMail / RabbitMQ / ActiveMQ / Kafka / Dubbo / Mongo 共 8 个协议响应使用 `DefaultRealResponse`（`status` 固定
200）；HTTP、WebSocket、MQTT、CoAP、Proto 共 5 个协议有自己的自定义 Response。

---

## 💡 读取示例

```java
SampleResult sr = (SampleResult) result;

// 基础信息
sr.

getTitle();                        // 用例标题
sr.

getStatus();                       // TestStatus.passed
sr.

getMetadata();                     // 用例 metadata（Map）
sr.

getVariables();                    // 变量增量记录列表

// 请求 / 响应
RealRequest req = sr.getRequest();     // 按协议类型化（强转为对应 Real 类）
RealResponse resp = sr.getResponse();  // 同上
resp.

getStatus();                     // 响应状态码（契约字段）
((RealHTTPRequest)req).

getMethod();  // 协议特有字段取值

// 验证器
for(
AssertionResult a :sr.

getAssertions()){
        a.

getField();    
        a.

getRule();     
        a.

getExpected();
        a.

getActual();   
        a.

getStatus();   // passed / failed / skipped
}

// 提取器
        for(
ExtractorResult e :sr.

getExtractors()){
        e.

getRefName();  
        e.

getValue();  
        e.

isDefaultValue();
}
```

---

## 🔄 序列化说明

- **仅序列化，不做反序列化还原**：`Real*` 子类无参构造不齐全，结果树为只读快照。
- **序列化 = getter 驱动**：request/response 按 JavaBean getter 输出，字段即上表所列。