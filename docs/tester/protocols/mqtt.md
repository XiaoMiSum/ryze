# 📡 MQTT 协议测试指南

## 📖 概述

MQTT 协议支持为 Ryze 测试框架提供了 MQTT 消息发布与订阅的测试能力，适用于 IoT 物联网设备通信场景的自动化测试。

## 📊 配置项参考表

### MQTT 数据源配置

| 配置项           | 类型      | 默认值       | 必需 | 描述                    |
|---------------|---------|-----------|----|-----------------------|
| broker        | String  | localhost | ✅  | MQTT Broker 地址        |
| port          | int     | 1883      | ❌  | 端口号 (TLS 默认: 8883)   |
| client_id     | String  | -         | ❌  | 客户端标识                 |
| username      | String  | -         | ❌  | 用户名                   |
| password      | String  | -         | ❌  | 密码                    |
| clean_session | boolean | true      | ❌  | 清除会话                  |
| keep_alive    | int     | 60        | ❌  | 心跳间隔(秒)              |
| tls_enabled   | boolean | false     | ❌  | 启用 TLS/SSL            |
| mqtt_version  | String  | "5.0"     | ❌  | MQTT 协议版本             |

### MQTT Publish（发布）配置

| 配置项        | 类型     | 默认值 | 必需 | 描述                 |
|------------|--------|-----|----|---------------------|
| datasource | String | -   | ✅  | 数据源引用名              |
| topic      | String | -   | ✅  | 发布主题               |
| qos        | int    | 1   | ❌  | QoS 等级 (0/1/2)     |
| payload    | Object | -   | ✅  | 消息负载               |

### Last Will（遗愿消息）配置

| 配置项               | 类型     | 默认值 | 必需 | 描述       |
|-------------------|--------|-----|----|----------|
| last_will_topic   | String | -   | ❌  | 遗愿消息主题   |
| last_will_payload | String | -   | ❌  | 遗愿消息内容   |
| last_will_qos     | int    | 0   | ❌  | 遗愿消息 QoS |

> **配置优先级**: 取样器配置 > MQTT 默认配置

## 🚀 依赖引入

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-mqtt</artifactId>
    <version>${version}</version>
</dependency>
```

## 🏃 快速开始

### 1. 配置 MQTT 数据源

```yaml
testclass: mqtt
ref_name: mqtt_source
config:
  broker: '192.168.1.100'
  port: 1883
  client_id: 'ryze-test-client'
  username: 'admin'
  password: 'password123'
```

### 2. 发布消息

## ⚙️ 配置元件

### MQTT 数据源配置

```yaml
# MQTT 数据源
testclass: mqtt
ref_name: mqtt_source
config:
  broker: '192.168.1.100'
  port: 1883
  client_id: 'ryze-test-client'
  username: 'admin'
  password: 'password123'
  clean_session: true
  keep_alive: 60
  tls_enabled: false
```

### TLS 安全连接配置

```yaml
testclass: mqtt
ref_name: mqtt_tls_source
config:
  broker: 'mqtt.example.com'
  port: 8883
  client_id: 'ryze-secure-client'
  username: 'admin'
  password: 'secure_password'
  tls_enabled: true
  clean_session: true
```

## 📊 取样器配置

### Publish 取样器

发布消息到指定主题。

## 📘 QoS 等级对照

| QoS | 名称             | 说明            | 推荐场景       |
|-----|----------------|---------------|------------|
| 0   | At most once   | 最多一次，可能丢失    | 环境数据上报    |
| 1   | At least once  | 至少一次，可能重复    | 普通IoT通信   |
| 2   | Exactly once   | 恰好一次，不丢不重    | 计费/控制指令   |

## 📋 完整 YAML 配置示例

```yaml
# IoT设备MQTT通信完整测试
title: IoT设备MQTT通信测试套件
configure_elements:
  - testclass: mqtt
    ref_name: mqtt_source
    config:
      broker: '192.168.1.100'
      port: 1883
      client_id: 'ryze-iot-tester'
      username: 'admin'
      password: 'password123'
      clean_session: true
      keep_alive: 60

preprocessors:
  - testclass: mqtt
    config:
      datasource: mqtt_source
      topic: 'device/init'
      qos: 1
      payload: '{"action":"init","deviceId":"sensor-001"}'

children:
  - title: 发布设备注册消息
    testclass: mqtt_publish
    config:
      datasource: mqtt_source
      topic: 'device/register'
      qos: 1
      payload: '{"deviceId":"sensor-001","type":"temperature","location":"room-101"}'

  - title: 发布温度数据(QoS 0)
    testclass: mqtt_publish
    config:
      datasource: mqtt_source
      topic: 'sensor/temperature/sensor-001'
      qos: 0
      payload: '{"value":25.6,"unit":"celsius","timestamp":"${__now()}"}'

  - title: 发布控制指令(QoS 2)
    testclass: mqtt_publish
    config:
      datasource: mqtt_source
      topic: 'device/control/sensor-001'
      qos: 2
      payload: '{"action":"setInterval","value":30}'

postprocessors:
  - testclass: mqtt
    config:
      datasource: mqtt_source
      topic: 'device/cleanup'
      qos: 1
      payload: '{"action":"disconnect","deviceId":"sensor-001"}'
```

## ❓ 常见问题

1. **连接被拒绝**：检查 Broker 地址、端口、用户名密码是否正确
2. **消息丢失**：提高 QoS 等级至 1 或 2
3. **客户端 ID 冲突**：使用唯一的 `client_id`，避免多个客户端使用相同 ID
4. **TLS 连接失败**：确认 `port: 8883` 且 `tls_enabled: true`

## 📚 相关文档

- [MQTT 协议规范](https://mqtt.org/mqtt-specification/)

---

**💡 提示**:
MQTT 主题通配符：`+` 匹配单层（如 `sensor/+/temperature`），`#` 匹配多层且只能在主题末尾使用（如 `sensor/#`）。
