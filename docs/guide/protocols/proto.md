# 🌐 Protobuf 协议测试指南

## 📖 概述

Proto 协议支持为 Ryze 测试框架提供了与 Protocol Buffer (protobuf) 协议进行交互的能力。支持 HTTP/HTTPS 和 WebSocket/WSS
协议传输 protobuf 数据，自动处理 JSON 与 protobuf 二进制格式之间的序列化和反序列化。

## 📊 配置项参考表

### 传输协议配置

| 配置项      | 类型     | 默认值  | 必需 | 描述                               |
|----------|--------|------|----|----------------------------------|
| base_url | String | -    | ✅  | 基础路径，如：ws(http)://127.0.0.1:8080 |
| method   | String | POST | ❌  | HTTP 请求方法 (http/https 时候需要)      |
| path     | String | -    | ❌  | 接口路径                             |

### Protobuf 描述配置

| 配置项                               | 类型     | 默认值 | 必需 | 描述         |
|-----------------------------------|--------|-----|----|------------|
| proto_desc.desc_path              | String | -   | ✅  | .desc 文件路径 |
| proto_desc.request_message_class  | String | -   | ✅  | 请求消息类全限定名  |
| proto_desc.response_message_class | String | -   | ✅  | 响应消息类全限定名  |

### 请求配置

| 配置项              | 类型     | 默认值 | 必需 | 描述                      |
|------------------|--------|-----|----|-------------------------|
| headers          | Map    | -   | ❌  | 请求头                     |
| query            | Map    | -   | ❌  | URL 查询参数                |
| body             | Object | -   | ✅  | 请求体 (JSON 格式)           |
| response_pattern | String | -   | ❌  | 响应匹配正则 (WebSocket 时候需要) |

> **配置优先级**: 取样器配置 > Protobuf 默认配置

## 🚀 依赖引入

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-proto</artifactId>
    <version>${version}</version>
</dependency>
```

## ⚙️ 配置元件

### Protobuf 默认配置

Protobuf 默认配置：使用该组件，可配置 Protobuf协议的默认配置，降低测试集合的配置复杂度。

当测试集合描述文件中存在此配置时，下级测试集合中包含的 Protobuf 取样器\处理器从此配置中获取相关配置。

#### 基于 http 协议

##### YAML 配置方式

```yaml
# Protobuf 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: proto  # 配置元件类型
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  base_url: http://localhost:8080   # 基础路径，如：http://127.0.0.1:8080
  method: post
  host: localhost
  headers: # 请求头，可空
    h1: 1
  proto_desc: # protobuf描述配置，可空（默认配置为空时，处理器\取样器则不可为空）
    desc_path: /path
    request_message_class: com.example.User
    response_message_class: com.example.User
```

##### JSON 配置方式

```json
{
  "testclass": "proto",
  "config": {
    "base_url": "http://localhost:8080",
    "method": "post",
    "path": "/test",
    "headers": {
      "h1": 1
    },
    "proto_desc": {
      "desc_path": "/path",
      "request_message_class": "com.example.User",
      "response_message_class": "com.example.User"
    }
  }
}
```

#### 基于 websocket 协议

##### YAML 配置方式

```yaml
# Protobuf 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: proto  # 配置元件类型
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  base_url: ws://localhost:8080   # 基础路径，如：ws://127.0.0.1:8080
  path: /test # 接口路径，可空
  headers: # 请求头，可空
    h1: 1
  response_pattern: 'userName' # 捕获相应信息匹配模式，可空（）
  proto_desc: # protobuf描述配置，可空（默认配置为空时，处理器\取样器则不可为空）
    desc_path: /path
    request_message_class: com.example.User
    response_message_class: com.example.User
```

##### JSON 配置方式

```json
{
  "testclass": "proto",
  "config": {
    "base_url": "ws://localhost:8080",
    "path": "/test",
    "headers": {
      "h1": 1
    },
    "response_pattern": "userName",
    "proto_desc": {
      "desc_path": "/path",
      "request_message_class": "com.example.User",
      "response_message_class": "com.example.User"
    }
  }
}
```

## 🔧 处理器

### 前置处理器

前置处理器在主要测试之前执行，常用于获取认证 token 或准备测试数据。

#### 基于 http 协议

```yaml
testclass: proto  # ws前置处理器 类型
config: # 处理器配置
  base_url: http://localhost:8080   # 基础路径，如：http://127.0.0.1:8080
  method: post
  path: /user   # 接口path
  headers: # 请求头，可空
    h1: 1
  query: { } # url中的参数，如: ?id=1&name=t {id: 1, name: t}
  body: { userName: 'ryze', password: '123456qq' } # 请求body
  proto_desc: # protobuf描述配置，可空（默认配置为空时，处理器\取样器则不可为空）
    desc_path: /path
    request_message_class: com.example.User
    response_message_class: com.example.User
```

#### 基于 websocket 协议

```yaml
testclass: proto  # ws前置处理器 类型
config: # 处理器配置
  base_url: ws://localhost:8080   # 基础路径，如：ws://127.0.0.1:8080
  path: /user   # 接口path
  headers: # 请求头，可空
    h1: 1
  query: { } # url中的参数，如: ?id=1&name=t {id: 1, name: t}
  body: { userName: 'ryze', password: '123456qq' } # 请求body
  response_pattern: 'userName'
  proto_desc: # protobuf描述配置，可空（默认配置为空时，处理器\取样器则不可为空）
    desc_path: /path
    request_message_class: com.example.User
    response_message_class: com.example.User
```

### 后置处理器

后置处理器在主要测试之后执行，常用于清理测试数据或发送通知。

#### 基于 http 协议

```yaml
testclass: proto  # ws后置处理器 类型
config: # 处理器配置
  base_url: http://localhost:8080   # 基础路径，如：http://127.0.0.1:8080
  method: post
  path: /user   # 接口path
  headers: # 请求头，可空
    h1: 1
  query: { } # url中的参数，如: ?id=1&name=t {id: 1, name: t}
  body: { userName: 'ryze', password: '123456qq' } # 请求body
  response_pattern: 'userName'
```

#### 基于 websocket 协议

```yaml
testclass: proto  # ws后置处理器 类型
config: # 处理器配置
  base_url: ws://localhost:8080   # 基础路径，如：ws://127.0.0.1:8080
  path: /user   # 接口path
  headers: # 请求头，可空
    h1: 1
  query: { } # url中的参数，如: ?id=1&name=t {id: 1, name: t}
  body: { userName: 'ryze', password: '123456qq' } # 请求body
  response_pattern: 'userName'
```

## 📊 取样器

### 基于 http 协议

#### YAML 配置方式

```yaml
title: 标准WS取样器
testclass: proto # 取样器类型
config: # 取样器配置
  base_url: http://localhost:8080   # 基础路径，如：http://127.0.0.1:8080
  method: post
  headers: # 请求头，可空
    h1: 1
  path: /user   # 接口path
  query: { } # url中的参数，如: ?id=1&name=t {id: 1, name: t}
  body: { userName: 'ryze', password: '123456qq' } # 请求body
  response_pattern: 'userName'
```

#### JSON 配置方式

```json
{
  "title": "用户登录接口",
  "testclass": "proto",
  "config": {
    "protocol": "http",
    "method": "post",
    "host": "api.example.com",
    "port": 443,
    "path": "/auth/login",
    "headers": {
      "Content-Type": "application/json"
    },
    "body": {
      "username": "testuser",
      "password": "password123"
    },
    "query": {},
    "response_pattern": "userName"
  }
}
```

### 基于 websocket 协议

#### YAML 配置方式

```yaml
title: 标准WS取样器
testclass: proto # 取样器类型
config: # 取样器配置
  base_url: ws://localhost:8080   # 基础路径，如：ws://127.0.0.1:8080
  headers: # 请求头，可空
    h1: 1
  path: /user   # 接口path
  query: { } # url中的参数，如: ?id=1&name=t {id: 1, name: t}
  body: { userName: 'ryze', password: '123456qq' } # 请求body
  response_pattern: 'userName'
```

#### JSON 配置方式

```json
{
  "title": "用户登录接口",
  "testclass": "proto",
  "config": {
    "base_url": "wss://api.example.com",
    "path": "/auth/login",
    "headers": {
      "Content-Type": "application/json"
    },
    "body": {
      "username": "testuser",
      "password": "password123"
    },
    "query": {},
    "response_pattern": "userName"
  }
}
```

## 💻 Java API 示例

### 基础 WS 请求

```java
import io.github.xiaomisum.ryze.procotol.Protobuf.ProtobufMagicBox;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class ProtobufApiExample {

    @Test
    @RyzeTest
    public void testProtobuf() {
        // 单个 WS GET 请求
        ProtobufMagicBox.ws(ws -> {
            ws.variables("id", 1);
            ws.title("获取用户信息：id = ${id}");
            ws.config(config -> config
                    .baseUrl("ws://127.0.0.1:58081")
                    .path("/user/${id}")
                    .responsePattern("userName")
            );
            ws.assertions(assertions -> assertions.json("$.data.id", "${id}"));
        });
    }
}
```

### 完整测试套件

```java
import io.github.xiaomisum.ryze.protocol.Protobuf.builder.ProtobufConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.Protobuf.builder.ProtobufPreprocessorsBuilder;
import io.github.xiaomisum.ryze.MagicBox;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class ProtobufSuiteExample {

    @Test
    @RyzeTest
    public void userApiTestSuite() {
        MagicBox.suite("测试用例", suite -> {
            suite.variables("id", 1);
            suite.variables("t_body", Collections.of("id", "ryze", "name", "ryze_ws_preprocessor", "age", 0));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.variables(var -> var.put("c", 3).put("d", 4));

            // 配置默认WS设置
            suite.configureElements(ProtoConfigureElementsBuilder.class, ele ->
                    ele.proto(ws -> ws.config(config -> config
                            .baseUrl("ws://127.0.0.1:58081")
                            .responsePattern("userName")
                    ))
            );

            // 前置处理器：新增用户
            suite.preprocessors(ProtoPreprocessorsBuilder.class, pre ->
                    pre.proto(ws -> {
                        ws.title("前置处理器新增用户");
                        ws.config(config -> config
                                .path("/user")
                                .body("${t_body}")
                        );
                        ws.extractors(extract -> extract.json("t_id", "$.data.id"));
                    })
            );

            suite.children(ProtoSamplersBuilder.class, child -> {
                child.proto(ws -> ws
                        .title("步骤1——获取用户：id = ${id}")
                        .config(config -> config
                                .path("/user/${id}")
                        )
                        .validators(validator -> validator.json("$.data.id", "${id}"))
                );

                child.proto(ws -> ws
                        .title("步骤2——修改用户：id = ${t_id}")
                        .config(config -> config
                                .path("/user")
                                .body(body -> {
                                    body.put("id", "ryze");
                                    body.put("name", "ryze_ws_sampler");
                                    body.put("age", 1);
                                })
                        )
                        .validators(validator -> validator.wsStatus(200))
                );

                child.proto(ws -> ws
                        .title("步骤3——获取用户：id = ${t_body.id}")
                        .config(config -> config
                                .path("/user/${t_body.id}")
                        )
                        .validators(validator -> validator.json("$.data.name", "ryze_ws_sampler"))
                );
            });
        });
    }
}
```

## 🐦 Groovy API 示例

### 基础脚本

```groovy
import io.github.xiaomisum.ryze.protocol.Protobuf.ProtobufMagicBox
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyProtobufExample {

    @Test
    @RyzeTest
    void testProtobuf() {
        // 简单的 GET 请求
        ProtobufMagicBox.proto {
            variables {
                put "id", "1"
            }
            title "获取用户信息：id = \${id}"
            config {
                baseUrl "ws://127.0.0.1:58081"
                path '/user/${id}'
                responsePattern 'userName'
            }
            validators {
                json {
                    field '$.data.id'
                    rule '=='
                    expected '${id}'
                }
            }
        }
    }
}
```

### 完整测试套件脚本

```groovy
import io.github.xiaomisum.ryze.protocol.Protobuf.builder.ProtobufConfigureElementsBuilder
import io.github.xiaomisum.ryze.protocol.Protobuf.builder.ProtobufPreprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.Protobuf.ProtobufMagicBox
import io.github.xiaomisum.ryze.support.Collections
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyProtobufSuiteExample {

    @Test
    @RyzeTest
    void userApiTestSuite() {
        // 用户API完整测试流程
        ProtobufMagicBox.suite {
            title "测试用例"
            variables("id", 1)
            variables("t_body", [id: "ryze", name: "ryze_ws_preprocessor", age: 0])
            variables {
                // 函数式写法
                put([a: 1, b: 2])
            }
            variables Collections.newHashMap([c: 3, d: 4])

            // WS默认配置
            configureElements(ProtoConfigureElementsBuilder.class, {
                proto {
                    config {
                        baseUrl "ws://127.0.0.1:58081"
                        responsePattern 'userName'
                    }
                }
            })

            // 前置处理器：新增用户
            preprocessors(ProtoPreprocessorsBuilder.class, {
                proto {
                    title "前置处理器新增用户"
                    config {
                        path '/user'
                        body '${t_body}'
                    }
                    extractors {
                        json {
                            field '$.data.id'
                            refName "t_id"
                        }
                    }
                }
            })

            children(ProtoSamplersBuilder.class, {
                proto {
                    title "步骤1——获取用户：id = \${id}"
                    config {
                        path '/user/${id}'
                    }
                    validators {
                        json {
                            field '$.data.id'
                            rule '=='
                            expected '${id}'
                        }
                    }
                }
            })

            children(ProtoSamplersBuilder.class, {
                proto {
                    title "步骤2——修改用户：id=\${t_id}"
                    config {
                        path '/user'
                        body { body -> body.putAll([id: "ryze", name: "ryze_ws_sampler", age: 0]) }
                    }
                    validators {
                        ws {
                            field 'statusCode'
                            rule "=="
                            expected 400
                        }
                    }
                }
            })

            children(ProtoSamplersBuilder.class, {
                ws {
                    title "步骤3——获取用户：id =\${t_body.id}"
                    config {
                        path '/user/${t_body.id}'
                    }
                    validators {
                        json {
                            field '$.data.name'
                            rule '=='
                            expected 'ryze_ws_sampler'
                        }
                    }
                }
            })
        }
    }
}
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

## 📚 相关文档

- [Protocol Buffers 官方文档](https://developers.google.com/protocol-buffers)

- [示例项目](https://github.com/XiaoMiSum/ryze/tree/master/example/proto-example)

---

**💡 提示**:
更多详细示例请参考 [example/proto-example](https://github.com/XiaoMiSum/ryze/tree/master/example/proto-example)
目录下的完整示例代码。