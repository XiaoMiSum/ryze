# Proto 协议

## 概述

Proto 协议支持为 Ryze 测试框架提供了与 Protocol Buffer (protobuf) 协议进行交互的能力。支持 HTTP/HTTPS 和 WebSocket/WSS
协议传输 protobuf 数据，自动处理 JSON 与 protobuf 二进制格式之间的序列化和反序列化。

## 依赖引入

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-proto</artifactId>
    <version>${version}</version>
</dependency>
```

## Proto 配置元件

Proto 默认配置：使用该组件，可配置 Proto 协议的默认配置，降低测试集合的配置复杂度。

当测试集合描述文件中存在此配置时，下级测试集合中包含的 Proto 取样器\处理器从此配置中获取相关配置。

### YAML 配置方式

```yaml
# Proto 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: proto  # 配置元件类型
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  protocol: http  # 请求协议，http、https、ws、wss，可空，默认 http
  host: localhost
  port: 8080 # 端口，默认 80
  path: /test # 接口路径，可空
  method: POST  # 请求方法，默认POST
  headers: # 请求头，可空
    Content-Type: application/x-protobuf
  proto: # Proto 配置
    desc_path: /path/to/demo.desc # .desc 文件路径
    request_message_name: demo.User # 请求消息类型全限定名
    response_message_name: demo.User # 响应消息类型全限定名
```

### JSON 配置方式

```json
{
  "testclass": "proto",
  "config": {
    "protocol": "http",
    "host": "localhost",
    "port": 8080,
    "path": "/test",
    "method": "POST",
    "headers": {
      "Content-Type": "application/x-protobuf"
    },
    "proto": {
      "desc_path": "/path/to/demo.desc",
      "request_message_name": "demo.User",
      "response_message_name": "demo.User"
    }
  }
}
```

### 基于 websocket 协议

```yaml
# Proto 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: proto  # 配置元件类型
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  protocol: ws  # 请求协议，http、https、ws、wss，可空，默认 http
  host: localhost
  port: 8080 # 端口，默认 80
  path: /test # 接口路径，可空
  headers: # 请求头，可空
    Content-Type: application/x-protobuf
  proto: # Proto 配置
    desc_path: /path/to/demo.desc # .desc 文件路径
    request_message_name: demo.User # 请求消息类型全限定名
    response_message_name: demo.User # 响应消息类型全限定名
  response_pattern: "^\\[\\d+\\]" # 响应消息匹配正则
```

## Proto 处理器

### 前置处理器

前置处理器在主要测试之前执行，常用于准备测试数据。

#### 基于 http/https 协议

```yaml
testclass: proto  # proto前置处理器 类型
config: # 处理器配置
  protocol: http   # 请求协议，默认 http
  port: 8080   # 请求端口，默认 80
  host: localhost  # 服务器地址
  method: POST  # 请求方法
  path: /user   # 接口path
  headers: # 请求头，可空
    Content-Type: application/x-protobuf
  proto: # Proto 配置
    desc_path: /path/to/demo.desc # .desc 文件路径
    request_message_name: demo.User # 请求消息类型全限定名
    response_message_name: demo.User # 响应消息类型全限定名
  body: { id: 1, name: 'test' } # 请求体(JSON格式)
```

#### 基于 websocket 协议

```yaml
# Proto 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: proto  # 配置元件类型
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  protocol: ws  # 请求协议，http、https、ws、wss，可空，默认 http
  host: localhost
  port: 8080 # 端口，默认 80
  path: /test # 接口路径，可空
  headers: # 请求头，可空
    Content-Type: application/x-protobuf
  proto: # Proto 配置
    desc_path: /path/to/demo.desc # .desc 文件路径
    request_message_name: demo.User # 请求消息类型全限定名
    response_message_name: demo.User # 响应消息类型全限定名
  response_pattern: "^\\[\\d+\\]" # 响应消息匹配正则
```

### 后置处理器

后置处理器在主要测试之后执行，常用于清理测试数据。

#### 基于 http/https 协议

```yaml
testclass: proto  # proto后置处理器 类型
config: # 处理器配置
  protocol: http   # 请求协议，默认 http
  port: 8080   # 请求端口，默认 80
  host: localhost  # 服务器地址
  method: POST  # 请求方法
  path: /user   # 接口path
  headers: # 请求头，可空
    Content-Type: application/x-protobuf
  proto: # Proto 配置
    desc_path: /path/to/demo.desc # .desc 文件路径
    request_message_name: demo.User # 请求消息类型全限定名
    response_message_name: demo.User # 响应消息类型全限定名
  body: { id: 1, name: 'test' } # 请求体(JSON格式)
```

#### 基于 websocket 协议

```yaml
# Proto 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: proto  # 配置元件类型
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  protocol: ws  # 请求协议，http、https、ws、wss，可空，默认 http
  host: localhost
  port: 8080 # 端口，默认 80
  path: /test # 接口路径，可空
  headers: # 请求头，可空
    Content-Type: application/x-protobuf
  proto: # Proto 配置
    desc_path: /path/to/demo.desc # .desc 文件路径
    request_message_name: demo.User # 请求消息类型全限定名
    response_message_name: demo.User # 响应消息类型全限定名
  response_pattern: "^\\[\\d+\\]" # 响应消息匹配正则
```

## Proto 取样器

### YAML 配置方式

```yaml
title: 标准Proto取样器
testclass: proto # 取样器类型
config: # 取样器配置
  protocol: http   # 请求协议，默认 http
  port: 8080   # 请求端口，默认 80
  host: localhost  # 服务器地址
  method: POST  # 请求方法
  path: /user   # 接口path
  headers: # 请求头，可空
    Content-Type: application/x-protobuf
  proto: # Proto 配置
    desc_path: /path/to/demo.desc # .desc 文件路径
    request_message_name: demo.User # 请求消息类型全限定名
    response_message_name: demo.User # 响应消息类型全限定名
  body: { id: 1, name: 'test' } # 请求体(JSON格式)
```

### JSON 配置方式

```json
{
  "title": "用户服务调用",
  "testclass": "proto",
  "config": {
    "protocol": "https",
    "host": "api.example.com",
    "port": 443,
    "path": "/user",
    "method": "POST",
    "headers": {
      "Content-Type": "application/x-protobuf"
    },
    "proto": {
      "desc_path": "/path/to/demo.desc",
      "request_message_name": "demo.User",
      "response_message_name": "demo.User"
    },
    "body": {
      "id": 1,
      "name": "test"
    }
  }
}
```

#### 基于 websocket 协议

```yaml
# Proto 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: proto  # 配置元件类型
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  protocol: ws  # 请求协议，http、https、ws、wss，可空，默认 http
  host: localhost
  port: 8080 # 端口，默认 80
  path: /test # 接口路径，可空
  headers: # 请求头，可空
    Content-Type: application/x-protobuf
  proto: # Proto 配置
    desc_path: /path/to/demo.desc # .desc 文件路径
    request_message_name: demo.User # 请求消息类型全限定名
    response_message_name: demo.User # 响应消息类型全限定名
  response_pattern: "^\\[\\d+\\]" # 响应消息匹配正则
```

## 常见问题

1. **如何生成 .desc 文件？**
   使用 protoc 编译器生成：
   ```bash
   protoc --include_imports --descriptor_set_out=demo.desc demo.proto
   ```

2. **消息类型全限定名如何确定？**
   消息类型的全限定名由包名和消息名组成，例如在 .proto 文件中：
   ```protobuf
   syntax = "proto3";
   
   package demo;
   
   message User {
     int32 id = 1;
     string name = 2;
   }
   ```
   全限定名为 `demo.User`。

3. **WebSocket 连接何时关闭？**
   对于 WebSocket 协议，可以通过设置 `response_pattern` 参数指定连接关闭条件，当收到的消息内容匹配该正则表达式时连接关闭。

4. **请求体格式要求？**
   请求体必须是有效的 JSON 格式字符串或对象。

## 配置优先级

配置项的优先级为：**取样器配置 > Proto 默认配置**

## Java API 示例

### 基础 Proto 请求

```java
import io.github.xiaomisum.ryze.protocol.proto.ProtoConfigureItem;

public class ProtoApiExample {

    public void testProtoHttp() {
        // HTTP Proto 请求
        ProtoConfigureItem config = ProtoConfigureItem.builder()
                .http()                                    // 使用HTTP协议
                .host("localhost")                         // 设置主机
                .port("8080")                              // 设置端口
                .path("/api/user")                         // 设置路径
                .post()                                    // 使用POST方法
                .protoDesc(pb -> pb                        // Proto配置
                        .descPath("/path/to/demo.desc")        // .desc文件路径
                        .requestMessageName("demo.User")       // 请求消息类型
                        .responseMessageName("demo.User"))     // 响应消息类型
                .body("{\"id\": 1, \"name\": \"张三\"}")   // 请求体(JSON格式)
                .build();

        // 使用 config 进行测试
    }

    public void testProtoWebSocket() {
        // WebSocket Proto 请求
        ProtoConfigureItem config = ProtoConfigureItem.builder()
                .ws()                                      // 使用WebSocket协议
                .host("localhost")                         // 设置主机
                .port("8080")                              // 设置端口
                .path("/ws/user")                          // 设置路径
                .protoDesc(pb -> pb                        // Proto配置
                        .descPath("/path/to/demo.desc")        // .desc文件路径
                        .requestMessageName("demo.Message")    // 请求消息类型
                        .responseMessageName("demo.Message"))  // 响应消息类型
                .responsePattern("close")                  // 连接关闭条件
                .build();

        // 使用 config 进行测试
    }
}
```

## Groovy API 示例

### 基础 Proto 请求

```groovy
import static io.github.xiaomisum.ryze.protocol.proto.ProtoMagicBox.*

class ProtoGroovyExample {

    def testProtoHttp() {
        // HTTP Proto 请求
        def config = ProtoConfigureItem.builder()
                .http()                                    // 使用HTTP协议
                .host("localhost")                         // 设置主机
                .port("8080")                              // 设置端口
                .path("/api/user")                         // 设置路径
                .post()                                    // 使用POST方法
                .protoDesc { pb ->                         // Proto配置
                    pb.descPath("/path/to/demo.desc")        // .desc文件路径
                            .requestMessageName("demo.User")       // 请求消息类型
                            .responseMessageName("demo.User")      // 响应消息类型
                }
                .body('{"id": 1, "name": "张三"}')         // 请求体(JSON格式)
                .build()

        // 使用 config 进行测试
    }

    def testProtoWebSocket() {
        // WebSocket Proto 请求
        def config = ProtoConfigureItem.builder()
                .ws()                                      // 使用WebSocket协议
                .host("localhost")                         // 设置主机
                .port("8080")                              // 设置端口
                .path("/ws/user")                          // 设置路径
                .protoDesc { pb ->                         // Proto配置
                    pb.descPath("/path/to/demo.desc")        // .desc文件路径
                            .requestMessageName("demo.Message")    // 请求消息类型
                            .responseMessageName("demo.Message")   // 响应消息类型
                }
                .responsePattern("close")                  // 连接关闭条件
                .build()

        // 使用 config 进行测试
    }
}
```

## 相关文档

- [Protocol Buffers 官方文档](https://developers.google.com/protocol-buffers)

- [示例项目](https://github.com/XiaoMiSum/ryze/tree/master/example/proto-example)

---

**💡 提示**:
更多详细示例请参考 [example/proto-example](https://github.com/XiaoMiSum/ryze/tree/master/example/proto-example)
目录下的完整示例代码。