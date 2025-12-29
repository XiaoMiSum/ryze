# 🌐 WebSocket(S) 协议测试指南

## 📖 概述

WebSocket 协议支持为 Ryze 测试框架提供了 WebSocket 接口测试能力。

## 📊 配置项参考表

### WebSocket 连接配置

| 配置项 | 类型 | 默认值 | 必需 | 描述 |
|-------|------|--------|------|------|
| protocol | String | ws | ❌ | 协议类型 (ws/wss) |
| host | String | - | ✅ | 服务器地址 |
| port | Integer | 80 | ❌ | 服务器端口 |
| path | String | - | ❌ | 接口路径 |
| headers | Map | - | ❌ | 请求头 |
| query | Map | - | ❌ | URL 查询参数 |
| body | Object | - | ❌ | 请求体 |
| response_pattern | String | - | ❌ | 响应匹配正则表达式 |

> **配置优先级**: 取样器配置 > WebSocket 默认配置

## 🚀 依赖引入

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-websocket</artifactId>
    <version>${version}</version>
</dependency>
```

## ⚙️ 配置元件

### WS 默认配置

WS 默认配置：使用该组件，可配置 WS协议的默认配置，降低测试集合的配置复杂度。

当测试集合描述文件中存在此配置时，下级测试集合中包含的 WS 取样器\处理器从此配置中获取相关配置。

#### YAML 配置方式

```yaml
# websocket 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: ws  # 配置元件类型
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  protocol: ws  # 请求协议，ws、wss，可空，默认 ws
  host: localhost
  port: 8080 # 端口，默认 80
  path: /test # 接口路径，可空
  headers: # 请求头，可空
    h1: 1
```

#### JSON 配置方式

```json
{
  "testclass": "ws",
  "config": {
    "protocol": "ws",
    "host": "localhost",
    "port": 8080,
    "path": "/test",
    "headers": {
      "h1": 1
    }
  }
}
```

## 🔧 处理器

### 前置处理器

前置处理器在主要测试之前执行，常用于获取认证 token 或准备测试数据。

```yaml
testclass: ws  # ws前置处理器 类型
config: # 处理器配置
  protocol: ws   # 请求协议，默认 ws
  port: 8080   # 请求端口，默认 80
  host: localhost  # 服务器地址
  path: /user   # 接口path
  headers: # 请求头，可空
    h1: 1
  query: { } # url中的参数，如: ?id=1&name=t {id: 1, name: t}
  body: { userName: 'ryze', password: '123456qq' } # 请求body
  response_pattern: 'userName'
```

### 后置处理器

后置处理器在主要测试之后执行，常用于清理测试数据或发送通知。

```yaml
testclass: ws  # ws后置处理器 类型
config: # 处理器配置
  method: post   # 请求方法
  protocol: ws   # 请求协议，默认 ws
  port: 8080   # 请求端口，默认 80
  host: localhost  # 服务器地址
  path: /user   # 接口path
  headers: # 请求头，可空
    h1: 1
  query: { } # url中的参数，如: ?id=1&name=t {id: 1, name: t}
  body: { userName: 'ryze', password: '123456qq' } # 请求body
  response_pattern: 'userName'
```

## 📊 取样器

### WS 取样器

#### YAML 配置方式

```yaml
title: 标准WS取样器
testclass: ws # 取样器类型
config: # 取样器配置
  protocol: ws   # 请求协议，默认 ws
  port: 8080   # 请求端口，默认 80
  host: localhost  # 服务器地址
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
  "testclass": "ws",
  "config": {
    "protocol": "wss",
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

## 💻 Java API 示例

### 基础 WS 请求

```java
import io.github.xiaomisum.ryze.procotol.websocket.WebsocketMagicBox;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class websocketApiExample {

    @Test
    @RyzeTest
    public void testWebsocket() {
        // 单个 WS GET 请求
        WebsocketMagicBox.ws(ws -> {
            ws.variables("id", 1);
            ws.title("获取用户信息：id = ${id}");
            ws.config(config -> config
                    .protocol("ws")
                    .host("127.0.0.1")
                    .port("58081")
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
import io.github.xiaomisum.ryze.protocol.websocket.builder.WebsocketConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.websocket.builder.WebsocketPreprocessorsBuilder;
import io.github.xiaomisum.ryze.MagicBox;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class websocketSuiteExample {

    @Test
    @RyzeTest
    public void userApiTestSuite() {
        MagicBox.suite("测试用例", suite -> {
            suite.variables("id", 1);
            suite.variables("t_body", Collections.of("id", "ryze", "name", "ryze_ws_preprocessor", "age", 0));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.variables(var -> var.put("c", 3).put("d", 4));

            // 配置默认WS设置
            suite.configureElements(WebsocketConfigureElementsBuilder.class, ele ->
                    ele.ws(ws -> ws.config(config -> config
                            .protocol("ws")
                            .host("127.0.0.1")
                            .port("58081")
                            .responsePattern("userName")
                    ))
            );

            // 前置处理器：新增用户
            suite.preprocessors(WebsocketPreprocessorsBuilder.class, pre ->
                    pre.ws(ws -> {
                        ws.title("前置处理器新增用户");
                        ws.config(config -> config
                                .path("/user")
                                .body("${t_body}")
                        );
                        ws.extractors(extract -> extract.json("t_id", "$.data.id"));
                    })
            );

            suite.children(WebsocketSamplersBuilder.class, child -> {
                child.ws(ws -> ws
                        .title("步骤1——获取用户：id = ${id}")
                        .config(config -> config
                                .path("/user/${id}")
                        )
                        .validators(validator -> validator.json("$.data.id", "${id}"))
                );

                child.ws(ws -> ws
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

                child.ws(ws -> ws
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
import io.github.xiaomisum.ryze.protocol.websocket.WebsocketMagicBox
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyWebsocketExample {

    @Test
    @RyzeTest
    void testWebsocket() {
        // 简单的 GET 请求
        WebsocketMagicBox.ws {
            variables {
                put "id", "1"
            }
            title "获取用户信息：id = \${id}"
            config {
                protocol "ws"
                host "127.0.0.1"
                port "58081"
                method "GET"
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
import io.github.xiaomisum.ryze.protocol.websocket.builder.WebsocketConfigureElementsBuilder
import io.github.xiaomisum.ryze.protocol.websocket.builder.WebsocketPreprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.websocket.WebsocketMagicBox
import io.github.xiaomisum.ryze.support.Collections
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyWebsocketSuiteExample {

    @Test
    @RyzeTest
    void userApiTestSuite() {
        // 用户API完整测试流程
        WebsocketMagicBox.suite {
            title "测试用例"
            variables("id", 1)
            variables("t_body", [id: "ryze", name: "ryze_ws_preprocessor", age: 0])
            variables {
                // 函数式写法
                put([a: 1, b: 2])
            }
            variables Collections.newHashMap([c: 3, d: 4])

            // WS默认配置
            configureElements(WebsocketConfigureElementsBuilder.class, {
                ws {
                    config {
                        protocol "ws"
                        host "127.0.0.1"
                        port "58081"
                        responsePattern 'userName'
                    }
                }
            })

            // 前置处理器：新增用户
            preprocessors(WebsocketPreprocessorsBuilder.class, {
                ws {
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

            children(WebsocketSamplersBuilder.class, {
                ws {
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

            children(WebsocketSamplersBuilder.class, {
                ws {
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

            children(WebsocketSamplersBuilder.class, {
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

## ❓ 常见问题

### 配置优先级

配置项的优先级为：**取样器配置 > WS 默认配置**

### FAQ

1. **当一个测试集合内存在多个 WS默认配置时怎么办？**
    - 多个 WS默认配置中的配置会合并，相同配置项以后定义的 WS默认配置的值为准。

2. **取样器配置与默认配置冲突时如何处理？**
    - 当取样器中的 WS配置项 与 WS默认配置中的配置项重复时，以取样器中的配置项的值为准。

3. **如何处理 WSS 证书问题？**
    - 可以在配置中设置 `protocol: "wss"` 并根据需要配置证书验证选项。

4. **如何处理服务端主动推送的消息不是期望的消息？**
    - 在 websocket 中，服务器会主动推送消息到客户端，这些消息可能不是当前测试所期望的消息，可以使用
      `response_pattern: "正则表达式"` 进行匹配，
      如果匹配成功，则将消息写入到响应结果中，否则将持续等待直到匹配成功或超时

## 📚 相关文档

- [示例项目](https://github.com/XiaoMiSum/ryze/tree/master/example/websocket-example)

---

**💡 提示**:
更多详细示例请参考 [example/websocket-example](https://github.com/XiaoMiSum/ryze/tree/master/example/websocket-example)
目录下的完整示例代码。