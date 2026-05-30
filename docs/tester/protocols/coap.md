# 🌐 CoAP 协议测试指南

## 📖 概述

CoAP 协议支持为 Ryze 测试框架提供了 CoAP 资源请求的测试能力，适用于 IoT 受限设备通信场景的自动化测试。支持 GET/POST/PUT/DELETE 四种方法，以及 Observe 资源观察功能。

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

> **默认端口**: CoAP 端口 5683，DTLS 安全端口 5684

## 🚀 依赖引入

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-coap</artifactId>
    <version>${version}</version>
</dependency>
```

## 🏃 快速开始

### 1. 配置 CoAP 默认参数

```yaml
testclass: coap
ref_name: coap_default
config:
  uri: 'coap://192.168.1.100:5683'
  confirmable: true
  timeout: 10000
  content_format: 'application/json'
```

### 2. 发送 GET 请求

```yaml
title: 获取设备温度
testclass: coap
config:
  uri: 'coap://192.168.1.100:5683/sensor/temperature'
  method: GET
  accept: 'application/json'
```

### 3. 发送 POST 请求

```yaml
title: 注册新设备
testclass: coap
config:
  uri: 'coap://192.168.1.100:5683/devices'
  method: POST
  payload: '{"deviceId":"sensor-001","type":"temperature"}'
```

## ⚙️ 配置元件

### CoAP 默认配置

```yaml
testclass: coap
ref_name: coap_default
config:
  uri: 'coap://192.168.1.100:5683'
  confirmable: true
  timeout: 10000
  content_format: 'application/json'
```

### DTLS 安全配置

```yaml
testclass: coap
ref_name: coap_secure
config:
  uri: 'coaps://192.168.1.100:5684'
  dtls_enabled: true
  confirmable: true
  timeout: 15000
```

## 📊 各方法 YAML 配置示例

### GET - 获取资源

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

### POST - 创建资源

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

### PUT - 更新资源

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

### DELETE - 删除资源

```yaml
title: 删除设备注册
testclass: coap
config:
  uri: 'coap://192.168.1.100:5683/devices/sensor-003'
  method: DELETE
  confirmable: true
```

### Observe - 观察资源变化

```yaml
title: 观察温度变化
testclass: coap
config:
  uri: 'coap://192.168.1.100:5683/sensor/temperature'
  method: GET
  observe: true
  timeout: 30000
  accept: 'application/json'
```

### Non-confirmable 消息

```yaml
title: 上报环境数据(NON消息)
testclass: coap
config:
  uri: 'coap://192.168.1.100:5683/sensor/environment'
  method: POST
  payload: '{"temperature":25.6,"humidity":65}'
  confirmable: false  # 不需要确认，适合周期性数据
  timeout: 5000
```

## 📘 消息类型对照

| 类型              | 标识  | 可靠性 | 适用场景         |
|-----------------|-----|-----|--------------|
| Confirmable     | CON | 可靠  | 设备控制、配置更新   |
| Non-confirmable | NON | 不可靠 | 周期性数据上报     |

## 📘 CoAP 响应码对照表

| 响应码  | 含义                   | 对应 HTTP | 说明         |
|------|----------------------|---------|------------|
| 2.01 | Created              | 201     | 资源创建成功     |
| 2.02 | Deleted              | 200     | 资源删除成功     |
| 2.03 | Valid                 | 304     | 资源未修改      |
| 2.04 | Changed              | 200     | 资源更新成功     |
| 2.05 | Content              | 200     | 返回资源内容     |
| 4.00 | Bad Request          | 400     | 请求格式错误     |
| 4.01 | Unauthorized         | 401     | 未授权        |
| 4.03 | Forbidden            | 403     | 禁止访问       |
| 4.04 | Not Found            | 404     | 资源不存在      |
| 4.05 | Method Not Allowed   | 405     | 方法不允许      |
| 4.15 | Unsupported Format   | 415     | 不支持的内容格式   |
| 5.00 | Internal Server Error| 500     | 服务器内部错误    |
| 5.03 | Service Unavailable  | 503     | 服务不可用      |

## 📋 完整 YAML 配置示例

```yaml
# CoAP设备管理完整测试
title: CoAP智能设备管理测试套件
configure_elements:
  - testclass: coap
    ref_name: coap_default
    config:
      uri: 'coap://192.168.1.100:5683'
      confirmable: true
      timeout: 10000
      content_format: 'application/json'

preprocessors:
  - testclass: coap
    config:
      uri: 'coap://192.168.1.100:5683/device/init'
      method: POST
      payload: '{"action":"reset"}'
      confirmable: true

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

  - title: 获取传感器数据
    testclass: coap
    config:
      uri: 'coap://192.168.1.100:5683/sensor/temp-001/value'
      method: GET
      accept: 'application/json'
      confirmable: true

  - title: 更新传感器配置
    testclass: coap
    config:
      uri: 'coap://192.168.1.100:5683/device/temp-001/config'
      method: PUT
      content_format: 'application/json'
      payload: '{"reportInterval":15,"unit":"seconds"}'

  - title: 观察传感器数据变化
    testclass: coap
    config:
      uri: 'coap://192.168.1.100:5683/sensor/temp-001/value'
      method: GET
      observe: true
      timeout: 30000

  - title: 删除传感器
    testclass: coap
    config:
      uri: 'coap://192.168.1.100:5683/devices/temp-001'
      method: DELETE
      confirmable: true

postprocessors:
  - testclass: coap
    config:
      uri: 'coap://192.168.1.100:5683/device/cleanup'
      method: DELETE
      confirmable: true
```

## ❓ 常见问题

1. **连接超时**：检查设备是否在线，网络是否连通，可增大 `timeout` 值
2. **资源未找到(4.04)**：确认 URI 路径是否正确拼写
3. **方法不允许(4.05)**：该资源不支持当前请求方法，换用其他方法
4. **Observe 无通知**：确认服务端支持 Observe 扩展，且资源确实有变化
5. **DTLS 握手失败**：确认使用 `coaps://` 协议和端口 5684
6. **内容格式不匹配**：检查 `content_format` 和 `accept` 是否与服务端一致

## 📚 相关文档

- [CoAP 协议规范 (RFC 7252)](https://tools.ietf.org/html/rfc7252)

---

**💡 提示**:
CoAP URI 格式为 `coap://host:port/path`，安全连接使用 `coaps://host:port/path`。默认端口 5683 可省略。
