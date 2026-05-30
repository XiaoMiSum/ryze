# CoAP 自动启动/关闭功能说明

## ✅ 已完成的功能

### 1. 改造 CoapConsumer 类
- ✅ 将 `CoapConsumer` 改造为可实例化的类
- ✅ 添加 `start()` 方法：启动 CoAP 服务器
- ✅ 添加 `stop()` 方法：停止 CoAP 服务器
- ✅ 添加 `/test` 资源路径（用于测试用例）
- ✅ 保持 standalone 模式兼容（`main` 方法）

### 2. 创建测试监听器
- ✅ 创建 `CoapTestListener` 类
- ✅ 实现 `ITestListener.onStart()` - 测试套件启动时自动启动 CoAP 服务器
- ✅ 实现 `ITestListener.onFinish()` - 测试套件结束时自动关闭 CoAP 服务器

### 3. 集成到测试类
- ✅ 在 `CodeTestCase` 上添加 `@Listeners(CoapTestListener.class)` 注解
- ✅ 在 `YamlTestCase` 上添加 `@Listeners(CoapTestListener.class)` 注解

## 🎯 使用方式

### 自动模式（推荐）
直接运行测试，CoAP 服务器会自动启动和关闭：

```bash
cd example/coap-example
mvn test
```

**执行流程：**
1. TestNG 触发 `onStart()` 事件
2. `CoapTestListener` 启动 CoAP 服务器（端口 5683）
3. 执行所有测试用例
4. TestNG 触发 `onFinish()` 事件
5. `CoapTestListener` 关闭 CoAP 服务器

### 手动模式
如果需要独立运行 CoAP 服务器进行测试：

```bash
# 终端1：手动启动 CoAP 服务器
cd example/coap-example
mvn exec:java -Dexec.mainClass="io.github.xiaomisum.ryze.coap.example.CoapConsumer"

# 终端2：运行测试
mvn test
```

## 📝 控制台输出示例

```
========== [CoAP Test Listener] 开始启动 CoAP 服务器 ==========
[CoAP Consumer] 已启动，监听端口: 5683
[CoAP Consumer] 可用资源路径:
  - coap://localhost:5683/sensor
  - coap://localhost:5683/temperature
  - coap://localhost:5683/device
  - coap://localhost:5683/test
========== [CoAP Test Listener] CoAP 服务器启动成功 ==========

...（测试执行过程）...

[CoAP Consumer] 收到 POST /test 请求 - Payload: {"key": "value"}
[CoAP Consumer] 收到 GET /test 请求

========== [CoAP Test Listener] 开始停止 CoAP 服务器 ==========
[CoAP Consumer] 已停止
========== [CoAP Test Listener] CoAP 服务器已停止 ==========
```

## ⚠️ 当前问题

测试仍然失败，错误信息：`Configuration contains no definitions!`

这是 Eclipse Californium CoAP 客户端的配置问题，与服务器启动/关闭功能无关。

**可能的原因：**
1. Californium 需要在 classpath 中提供配置文件
2. 测试环境缺少必要的 CoAP 配置
3. Maven 依赖版本问题

**下一步建议：**
1. 检查 `ryze-coap` 模块的 Californium 配置
2. 参考 `ryze-coap` 模块的测试用例，看是否有特殊配置要求
3. 可能需要在 `coap-example` 的 `pom.xml` 中添加额外的依赖或资源配置

## 📂 修改的文件

1. `src/main/java/io/github/xiaomisum/ryze/coap/example/CoapConsumer.java`
   - 重构为可实例化类
   - 添加 start/stop 方法
   - 添加 /test 资源路径

2. `src/test/java/io/github/xiaomisum/ryze/coap/example/CoapTestListener.java` (新增)
   - TestNG 监听器
   - 管理 CoAP 服务器生命周期

3. `src/test/java/io/github/xiaomisum/ryze/coap/example/code/CodeTestCase.java`
   - 添加 @Listeners 注解

4. `src/test/java/io/github/xiaomisum/ryze/coap/example/yaml/YamlTestCase.java`
   - 添加 @Listeners 注解
