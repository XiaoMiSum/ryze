# 🌐 CoAP 协议

## 📖 概述

CoAP（Constrained Application Protocol）是一种专为受限设备和受限网络设计的轻量级应用层协议。它采用类似 HTTP 的 RESTful 交互模型，但具有更小的开销，非常适合物联网中资源受限的嵌入式设备通信。Ryze 测试框架的 CoAP 协议支持提供了完整的 CoAP 请求测试能力。

**适用场景：**
- IoT 受限设备通信测试
- 智能家居设备控制测试
- 传感器数据采集测试
- 低功耗广域网（LPWAN）应用测试
- M2M（Machine-to-Machine）通信测试

## 📊 配置项参考表

### CoAP 请求配置

| 配置项            | 类型      | 默认值              | 必需 | 描述                          |
|----------------|---------|------------------|----|-----------------------------|
| uri            | String  | -                | ✅  | CoAP 资源 URI                 |
| method         | String  | GET              | ❌  | 请求方法 (GET/POST/PUT/DELETE)  |
| content_format | String  | application/json | ❌  | 内容格式                        |
| payload        | String  | -                | ❌  | 请求体                         |
| confirmable    | boolean | true             | ❌  | 是否使用 CON（确认）消息             |
| observe        | boolean | false            | ❌  | 是否启用观察模式                    |
| timeout        | int     | 10000            | ❌  | 超时时间(毫秒)                    |
| accept         | String  | -                | ❌  | 期望响应格式                      |
| block_size     | int     | -                | ❌  | 块传输大小                       |
| dtls_enabled   | boolean | false            | ❌  | 启用 DTLS 安全传输               |

> **配置优先级**: 取样器配置 > CoAP 默认配置

> **默认端口**: CoAP 默认端口为 5683，DTLS 安全端口为 5684

## 🚀 依赖引入

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-coap</artifactId>
    <version>${version}</version>
</dependency>
```

## ⚙️ 配置元件

### CoAP 默认配置

CoAP 默认配置：使用该组件配置 CoAP 协议的默认参数，降低测试集合的配置复杂度。

当测试集合描述文件中存在此配置时，下级测试集合中包含的 CoAP 取样器\处理器从此配置中获取相关配置。

#### YAML 配置方式

```yaml
# CoAP 默认配置
testclass: coap  # 配置元件类型
ref_name: coap_default  # 配置引用名称
config:
  uri: 'coap://192.168.1.100:5683'  # 基础URI
  confirmable: true  # 使用CON消息
  timeout: 10000  # 超时时间(毫秒)
  content_format: 'application/json'  # 内容格式
```

#### JSON 配置方式

```json
{
  "testclass": "coap",
  "ref_name": "coap_default",
  "config": {
    "uri": "coap://192.168.1.100:5683",
    "confirmable": true,
    "timeout": 10000,
    "content_format": "application/json"
  }
}
```

## 🔧 处理器

### 前置处理器

前置处理器在主要测试之前执行，常用于初始化设备资源或准备测试数据。

```yaml
testclass: coap  # coap 前置处理器类型
config:
  uri: 'coap://192.168.1.100:5683/device/init'
  method: POST
  content_format: 'application/json'
  payload: '{"action":"reset","deviceId":"light-001"}'
  confirmable: true
  timeout: 5000
```

### 后置处理器

后置处理器在主要测试之后执行，常用于清理资源或恢复设备状态。

```yaml
testclass: coap  # coap 后置处理器类型
config:
  uri: 'coap://192.168.1.100:5683/device/cleanup'
  method: DELETE
  confirmable: true
  timeout: 5000
```

## 📊 取样器

### 四种请求方法

#### GET - 获取资源

```yaml
title: 获取设备温度
testclass: coap
config:
  uri: 'coap://192.168.1.100:5683/sensor/temperature'
  method: GET
  accept: 'application/json'
  confirmable: true
  timeout: 10000
```

#### POST - 创建资源

```yaml
title: 注册新设备
testclass: coap
config:
  uri: 'coap://192.168.1.100:5683/devices'
  method: POST
  content_format: 'application/json'
  payload: '{"deviceId":"sensor-003","type":"humidity","location":"room-201"}'
  confirmable: true
```

#### PUT - 更新资源

```yaml
title: 更新设备配置
testclass: coap
config:
  uri: 'coap://192.168.1.100:5683/device/sensor-001/config'
  method: PUT
  content_format: 'application/json'
  payload: '{"reportInterval":30,"threshold":28.0}'
  confirmable: true
```

#### DELETE - 删除资源

```yaml
title: 删除设备注册
testclass: coap
config:
  uri: 'coap://192.168.1.100:5683/devices/sensor-003'
  method: DELETE
  confirmable: true
```

## 📘 Confirmable vs Non-confirmable 消息

| 消息类型            | 标识  | 描述              | 适用场景       |
|-----------------|-----|-----------------|------------|
| Confirmable (CON) | CON | 需要确认的可靠消息，会重传直到收到 ACK | 设备控制、配置更新 |
| Non-confirmable (NON) | NON | 不需要确认的消息，不保证到达 | 周期性数据上报   |

```yaml
# Confirmable 消息 (可靠传输)
title: 控制设备开关
testclass: coap
config:
  uri: 'coap://192.168.1.100:5683/device/light-001/switch'
  method: PUT
  payload: '{"state":"on"}'
  confirmable: true  # CON消息，确保命令到达

---

# Non-confirmable 消息 (不可靠传输)
title: 上报环境数据
testclass: coap
config:
  uri: 'coap://192.168.1.100:5683/sensor/environment'
  method: POST
  payload: '{"temperature":25.6,"humidity":65}'
  confirmable: false  # NON消息，适合周期性数据上报
```

## 👁️ Observe 资源观察

CoAP Observe 扩展允许客户端注册对资源的观察，当资源状态变化时服务端主动推送通知。

```yaml
title: 观察温度变化
testclass: coap
config:
  uri: 'coap://192.168.1.100:5683/sensor/temperature'
  method: GET
  observe: true  # 启用观察模式
  timeout: 30000  # 观察超时(毫秒)
  accept: 'application/json'
```

## 🔒 DTLS 安全连接配置

```yaml
testclass: coap
ref_name: coap_secure
config:
  uri: 'coaps://192.168.1.100:5684/secure/data'  # 使用coaps协议
  method: GET
  dtls_enabled: true  # 启用DTLS
  confirmable: true
  timeout: 15000
```

## 💻 Java API 示例

### 基础 CoAP 操作

```java
import io.github.xiaomisum.ryze.protocol.coap.Coap;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

public class CoapApiExample {

    @Test
    @RyzeTest
    public void testCoapGet() {
        // GET请求获取资源
        Result result = Coap.get("获取设备温度", builder -> builder
                .config(config -> config
                        .uri("coap://192.168.1.100:5683/sensor/temperature")
                        .accept("application/json")
                        .confirmable(true)
                        .timeout(10000)
                )
        );
    }

    @Test
    @RyzeTest
    public void testCoapPost() {
        // POST请求创建资源
        Result result = Coap.post("注册新设备", builder -> builder
                .config(config -> config
                        .uri("coap://192.168.1.100:5683/devices")
                        .contentFormat("application/json")
                        .payload("{\"deviceId\":\"sensor-003\",\"type\":\"humidity\"}")
                        .confirmable(true)
                )
        );
    }

    @Test
    @RyzeTest
    public void testCoapPut() {
        // PUT请求更新资源
        Result result = Coap.put("更新设备配置", builder -> builder
                .config(config -> config
                        .uri("coap://192.168.1.100:5683/device/sensor-001/config")
                        .contentFormat("application/json")
                        .payload("{\"reportInterval\":30}")
                        .confirmable(true)
                )
        );
    }

    @Test
    @RyzeTest
    public void testCoapDelete() {
        // DELETE请求删除资源
        Result result = Coap.delete("删除设备", builder -> builder
                .config(config -> config
                        .uri("coap://192.168.1.100:5683/devices/sensor-003")
                        .confirmable(true)
                )
        );
    }
}
```

### 完整测试套件

```java
import io.github.xiaomisum.ryze.protocol.coap.Coap;
import io.github.xiaomisum.ryze.protocol.coap.builder.CoapConfigureElementsBuilder;
import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

public class CoapSuiteExample {

    @Test
    @RyzeTest
    public void coapDeviceTestSuite() {
        Ryze.suite("CoAP设备管理测试", suite -> {
            // 配置CoAP默认设置
            suite.configureElements(CoapConfigureElementsBuilder.class, ele ->
                    ele.coap(coap -> coap.config(config -> config
                            .uri("coap://192.168.1.100:5683")
                            .confirmable(true)
                            .timeout(10000)
                            .contentFormat("application/json")
                    ))
            );

            suite.children(child -> {
                // 注册设备
                child.coap("注册新设备", coap -> coap
                        .config(config -> config
                                .uri("coap://192.168.1.100:5683/devices")
                                .post()
                                .payload("{\"deviceId\":\"sensor-001\",\"type\":\"temperature\"}")
                        )
                );

                // 获取设备信息
                child.coap("获取设备信息", coap -> coap
                        .config(config -> config
                                .uri("coap://192.168.1.100:5683/devices/sensor-001")
                                .get()
                        )
                );

                // 更新设备配置
                child.coap("更新设备配置", coap -> coap
                        .config(config -> config
                                .uri("coap://192.168.1.100:5683/devices/sensor-001/config")
                                .put()
                                .payload("{\"reportInterval\":60}")
                        )
                );

                // 删除设备
                child.coap("删除设备", coap -> coap
                        .config(config -> config
                                .uri("coap://192.168.1.100:5683/devices/sensor-001")
                                .delete()
                        )
                );
            });
        });
    }
}
```

## 🐦 Groovy API 示例

### 基础脚本

```groovy
import io.github.xiaomisum.ryze.protocol.coap.Coap
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class CoapGroovyExample {

    @Test
    @RyzeTest
    void testCoapGet() {
        Coap.coap("获取传感器数据") {
            config {
                uri "coap://192.168.1.100:5683/sensor/temperature"
                get()
                accept "application/json"
                confirmable true
                timeout 10000
            }
        }
    }

    @Test
    @RyzeTest
    void testCoapPost() {
        Coap.coap("创建设备资源") {
            config {
                uri "coap://192.168.1.100:5683/devices"
                post()
                contentFormat "application/json"
                payload '{"deviceId":"sensor-004","type":"pressure"}'
                confirmable true
            }
        }
    }
}
```

## 📋 完整 YAML 配置示例

```yaml
# CoAP设备管理完整测试
title: CoAP智能设备管理测试
configure_elements:
  - testclass: coap
    ref_name: coap_default
    config:
      uri: 'coap://192.168.1.100:5683'
      confirmable: true
      timeout: 10000
      content_format: 'application/json'

children:
  - title: 获取设备列表
    testclass: coap
    config:
      uri: 'coap://192.168.1.100:5683/devices'
      method: GET
      accept: 'application/json'

  - title: 注册温度传感器
    testclass: coap
    config:
      uri: 'coap://192.168.1.100:5683/devices'
      method: POST
      content_format: 'application/json'
      payload: '{"deviceId":"temp-001","name":"温度传感器","location":"room-101"}'

  - title: 获取温度数据
    testclass: coap
    config:
      uri: 'coap://192.168.1.100:5683/sensor/temp-001/value'
      method: GET
      accept: 'application/json'
      confirmable: true

  - title: 更新上报间隔
    testclass: coap
    config:
      uri: 'coap://192.168.1.100:5683/device/temp-001/config'
      method: PUT
      content_format: 'application/json'
      payload: '{"reportInterval":15,"unit":"seconds"}'

  - title: 观察温度变化
    testclass: coap
    config:
      uri: 'coap://192.168.1.100:5683/sensor/temp-001/value'
      method: GET
      observe: true
      timeout: 30000

  - title: 删除设备
    testclass: coap
    config:
      uri: 'coap://192.168.1.100:5683/devices/temp-001'
      method: DELETE
      confirmable: true
```

## 📘 CoAP 响应码对照表

| 响应码  | 含义              | 对应 HTTP |
|------|-----------------|---------|
| 2.01 | Created         | 201     |
| 2.02 | Deleted         | 200     |
| 2.03 | Valid           | 304     |
| 2.04 | Changed         | 200     |
| 2.05 | Content         | 200     |
| 4.00 | Bad Request     | 400     |
| 4.01 | Unauthorized    | 401     |
| 4.03 | Forbidden       | 403     |
| 4.04 | Not Found       | 404     |
| 4.05 | Method Not Allowed | 405  |
| 5.00 | Internal Server Error | 500 |

## ❓ 常见问题

1. **连接超时**：检查设备是否在线，网络是否连通，适当增大 `timeout` 值
2. **资源未找到(4.04)**：确认 URI 路径是否正确
3. **方法不允许(4.05)**：确认资源支持的请求方法
4. **Observe 无通知**：确认服务端是否支持 Observe 扩展，资源是否发生变化
5. **DTLS 握手失败**：确认安全端口(5684)和证书配置是否正确

## 📚 相关文档

- [CoAP 协议规范 (RFC 7252)](https://tools.ietf.org/html/rfc7252)
- [Eclipse Californium](https://github.com/eclipse-californium/californium)

---

**💡 提示**:
CoAP 使用 UDP 作为传输层协议，具有比 HTTP 更小的开销。对于需要可靠传输的场景，请使用 Confirmable (CON) 消息。
