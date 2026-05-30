# 📡 MQTT 协议

## 📖 概述

MQTT（Message Queuing Telemetry Transport）是一种轻量级的发布/订阅消息传输协议，专为资源受限的设备和低带宽、高延迟的网络环境设计。Ryze 测试框架的 MQTT 协议支持为物联网（IoT）场景提供了完整的消息发布与订阅测试能力。

**适用场景：**
- IoT 物联网设备通信测试
- 传感器数据上报测试
- 设备远程控制指令测试
- 消息中间件集成测试
- 实时消息推送测试

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

### MQTT 消息配置

| 配置项        | 类型     | 默认值 | 必需 | 描述              |
|------------|--------|-----|----|------------------|
| datasource | String | -   | ✅  | 数据源引用名          |
| topic      | String | -   | ✅  | 发布主题            |
| qos        | int    | 1   | ❌  | QoS 等级 (0/1/2)  |
| payload    | Object | -   | ❌  | 消息负载            |

### Last Will（遗愿消息）配置

| 配置项              | 类型     | 默认值 | 必需 | 描述       |
|------------------|--------|-----|----|----------|
| last_will_topic  | String | -   | ❌  | 遗愿消息主题   |
| last_will_payload| String | -   | ❌  | 遗愿消息内容   |
| last_will_qos    | int    | 0   | ❌  | 遗愿消息 QoS |

> **配置优先级**: 取样器配置 > MQTT 默认配置

## 🚀 依赖引入

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-mqtt</artifactId>
    <version>${version}</version>
</dependency>
```

## ⚙️ 配置元件

### MQTT 默认配置

MQTT 默认配置：使用该组件配置 MQTT 数据源连接信息，用于 MQTT 处理器\取样器引用。

当测试集合描述文件中存在此配置时，下级测试集合中包含的 MQTT 取样器\处理器从此配置中获取相关配置。

#### YAML 配置方式

```yaml
# MQTT 数据源配置
testclass: mqtt  # 配置元件类型
ref_name: mqtt_source  # 数据源名称
config:
  broker: '192.168.1.100'  # Broker地址
  port: 1883  # 端口号
  client_id: 'ryze-test-client'  # 客户端ID
  username: 'admin'  # 用户名
  password: 'password123'  # 密码
  clean_session: true  # 清除会话
  keep_alive: 60  # 心跳间隔(秒)
  tls_enabled: false  # 是否启用TLS
```

#### JSON 配置方式

```json
{
  "testclass": "mqtt",
  "ref_name": "mqtt_source",
  "config": {
    "broker": "192.168.1.100",
    "port": 1883,
    "client_id": "ryze-test-client",
    "username": "admin",
    "password": "password123",
    "clean_session": true,
    "keep_alive": 60,
    "tls_enabled": false
  }
}
```

## 🔧 处理器

### 前置处理器

前置处理器在主要测试之前执行，常用于预先发布消息或准备测试数据。

```yaml
testclass: mqtt  # mqtt 前置处理器类型
config:
  datasource: mqtt_source  # 数据源引用
  topic: 'device/init'  # 发布主题
  qos: 1  # QoS等级
  payload: '{"action":"init","deviceId":"sensor-001"}'
```

### 后置处理器

后置处理器在主要测试之后执行，常用于清理设备状态或发送断开通知。

```yaml
testclass: mqtt  # mqtt 后置处理器类型
config:
  datasource: mqtt_source  # 数据源引用
  topic: 'device/cleanup'  # 发布主题
  qos: 1
  payload: '{"action":"disconnect","deviceId":"sensor-001"}'
```

## 📊 取样器

### Publish（发布消息）取样器

## 📘 QoS 等级说明

| QoS 等级 | 名称         | 描述                     | 适用场景         |
|---------|------------|------------------------|--------------|
| 0       | At most once  | 最多一次，不保证消息到达         | 环境监测等允许丢失的场景 |
| 1       | At least once | 至少一次，确保消息到达但可能重复     | 大多数IoT场景     |
| 2       | Exactly once  | 恰好一次，确保消息不丢失且不重复     | 计费、指令控制等关键场景 |

## 🔒 TLS/SSL 连接配置

```yaml
testclass: mqtt
ref_name: mqtt_tls_source
config:
  broker: 'mqtt.example.com'
  port: 8883  # TLS默认端口
  client_id: 'ryze-secure-client'
  username: 'admin'
  password: 'secure_password'
  tls_enabled: true  # 启用TLS
  clean_session: true
  keep_alive: 60
```

## 📝 Last Will 遗愿消息配置

遗愿消息在客户端异常断开时由 Broker 自动发布，用于通知其他订阅者设备离线。

```yaml
testclass: mqtt
ref_name: mqtt_with_will
config:
  broker: '192.168.1.100'
  port: 1883
  client_id: 'device-001'
  clean_session: true
  last_will_topic: 'device/status/device-001'
  last_will_payload: '{"status":"offline","timestamp":"${__now()}"}'
  last_will_qos: 1
```

## 💻 Java API 示例

### 基础 MQTT 操作

```java
import io.github.xiaomisum.ryze.protocol.mqtt.Mqtt;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

public class MqttApiExample {

    @Test
    @RyzeTest
    public void testPublish() {
        // 发布消息
        Result result = Mqtt.publish("发布温度数据", builder -> builder
                .broker("192.168.1.100")
                .port(1883)
                .clientId("ryze-publisher")
                .topic("sensor/temperature")
                .qos(1)
                .payload("{\"deviceId\":\"sensor-001\",\"temperature\":25.6}")
        );
    }
}
```

### 完整测试套件

```java
import io.github.xiaomisum.ryze.protocol.mqtt.Mqtt;
import io.github.xiaomisum.ryze.protocol.mqtt.builder.MqttConfigureElementsBuilder;
import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

public class MqttSuiteExample {

    @Test
    @RyzeTest
    public void mqttIoTTestSuite() {
        Ryze.suite("IoT设备通信测试", suite -> {
            // 配置MQTT默认设置
            suite.configureElements(MqttConfigureElementsBuilder.class, ele ->
                    ele.mqtt(mqtt -> mqtt.config(config -> config
                            .broker("192.168.1.100")
                            .port(1883)
                            .clientId("ryze-iot-test")
                            .cleanSession(true)
                            .keepAlive(60)
                    ))
            );

            suite.children(child -> {
                // 发布传感器数据
                child.mqttPublish("发布温度数据", publish -> publish
                        .datasource("mqtt_source")
                        .topic("sensor/temperature")
                        .qos(1)
                        .payload("{\"deviceId\":\"sensor-001\",\"temperature\":25.6}")
                );
            });
        });
    }
}
```

## 🐦 Groovy API 示例

### 基础脚本

```groovy
import io.github.xiaomisum.ryze.protocol.mqtt.Mqtt
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class MqttGroovyExample {

    @Test
    @RyzeTest
    void testMqttPublish() {
        Mqtt.publish("发布设备数据") {
            broker "192.168.1.100"
            port 1883
            clientId "ryze-groovy-client"
            topic "sensor/humidity"
            qos 1
            payload '{"deviceId":"sensor-002","humidity":65.3}'
        }
    }
}
```

## 📋 完整 YAML 配置示例

```yaml
# IoT设备通信完整测试
title: IoT设备MQTT通信测试
configure_elements:
  - testclass: mqtt
    ref_name: mqtt_source
    config:
      broker: '192.168.1.100'
      port: 1883
      client_id: 'ryze-iot-test'
      username: 'admin'
      password: 'password123'
      clean_session: true
      keep_alive: 60

children:
  - title: 发布设备注册消息
    testclass: mqtt_publish
    config:
      datasource: mqtt_source
      topic: 'device/register'
      qos: 1
      payload: '{"deviceId":"sensor-001","type":"temperature","location":"room-101"}'

  - title: 发布温度数据
    testclass: mqtt_publish
    config:
      datasource: mqtt_source
      topic: 'sensor/temperature/sensor-001'
      qos: 0
      payload: '{"temperature":25.6,"unit":"celsius","timestamp":"${__now()}"}'

  - title: 发布设备离线通知
    testclass: mqtt_publish
    config:
      datasource: mqtt_source
      topic: 'device/status/sensor-001'
      qos: 1
      payload: '{"status":"offline","timestamp":"${__now()}"}'
```

## ❓ 常见问题

1. **连接被拒绝**：检查 Broker 地址和端口是否正确，用户名密码是否匹配
2. **消息丢失**：提高 QoS 等级，使用 QoS 1 或 QoS 2
3. **客户端 ID 冲突**：确保每个测试使用唯一的 `client_id`，避免被 Broker 踢下线
4. **TLS 连接失败**：确认端口为 8883，并设置 `tls_enabled: true`

## 📚 相关文档

- [MQTT 协议规范](https://mqtt.org/mqtt-specification/)
- [HiveMQ MQTT Client](https://github.com/hivemq/hivemq-mqtt-client)

---

**💡 提示**:
MQTT 主题支持通配符：`+` 匹配单层，`#` 匹配多层。例如 `sensor/+/temperature` 匹配所有传感器的温度主题。
