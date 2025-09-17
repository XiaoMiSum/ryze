# 🌐 HTTP(S) 协议测试指南

## 📖 概述

HTTP(S) 协议是 Ryze 框架内置支持的核心协议，提供完整的 HTTP/HTTPS 接口测试能力，支持 GET、POST、PUT、DELETE 等各种 HTTP
方法，以及丰富的断言和数据提取功能。

## 🚀 依赖引入

HTTP 协议支持内置在核心模块中，无需额外依赖：

```xml
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze</artifactId>
    <version>${version}</version>
</dependency>
```

## ⚙️ 配置元件

### HTTP 默认配置

HTTP 默认配置：使用该组件，可配置 HTTP协议的默认配置，降低测试集合的配置复杂度。

当测试集合描述文件中存在此配置时，下级测试集合中包含的 HTTP 取样器\处理器从此配置中获取相关配置。

#### YAML 配置方式

```yaml
# Http 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: http  # 配置元件类型
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  method: GET  # 请求方法，默认Get
  protocol: http  # 请求协议，http、https，可空，默认 http
  host: localhost
  port: 8080 # 端口，默认 80
  path: /test # 接口路径，可空
  http/2: false # 是否 http2， ture、false，可空，默认 false
  headers: # 请求头，可空
    h1: 1
```

#### JSON 配置方式

```json
{
  "testclass": "http",
  "config": {
    "method": "GET",
    "protocol": "http",
    "host": "localhost",
    "port": 8080,
    "path": "/test",
    "http/2": false,
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
testclass: http  # http前置处理器 类型
config: # 处理器配置
  method: post   # 请求方法
  protocol: http   # 请求协议，默认 http
  http/2: false # 是否 http2， ture、false，可空，默认 false
  port: 8080   # 请求端口，默认 80
  host: localhost  # 服务器地址
  path: /user   # 接口path
  headers: # 请求头，可空
    h1: 1
  query: { } # url中的参数，如: ?id=1&name=t {id: 1, name: t}
  data: { } # 请求data
  body: { userName: 'ryze', password: '123456qq' } # 请求body 优先级高于 data
```

### 后置处理器

后置处理器在主要测试之后执行，常用于清理测试数据或发送通知。

```yaml
testclass: http  # http后置处理器 类型
config: # 处理器配置
  method: post   # 请求方法
  protocol: http   # 请求协议，默认 http
  http/2: false # 是否 http2， ture、false，可空，默认 false
  port: 8080   # 请求端口，默认 80
  host: localhost  # 服务器地址
  path: /user   # 接口path
  headers: # 请求头，可空
    h1: 1
  query: { } # url中的参数，如: ?id=1&name=t {id: 1, name: t}
  data: { } # 请求data
  body: { userName: 'ryze', password: '123456qq' } # 请求body 优先级高于 data
```

## 📊 取样器

### HTTP 取样器

#### YAML 配置方式

```yaml
title: 标准HTTP取样器
testclass: http # 取样器类型
config: # 取样器配置
  method: post   # 请求方法
  protocol: http   # 请求协议，默认 http
  http/2: false # 是否 http2， ture、false，可空，默认 false
  port: 8080   # 请求端口，默认 80
  host: localhost  # 服务器地址
  headers: # 请求头，可空
    h1: 1
  path: /user   # 接口path
  query: { } # url中的参数，如: ?id=1&name=t {id: 1, name: t}
  data: { } # 请求data
  body: { userName: 'ryze', password: '123456qq' } # 请求body 优先级高于 data
```

#### JSON 配置方式

```json
{
  "title": "用户登录接口",
  "testclass": "http",
  "config": {
    "method": "POST",
    "protocol": "https",
    "host": "api.example.com",
    "port": 443,
    "path": "/auth/login",
    "http/2": false,
    "headers": {
      "Content-Type": "application/json"
    },
    "body": {
      "username": "testuser",
      "password": "password123"
    },
    "query": {},
    "data": {}
  }
}
```

## 💻 Java API 示例

### 基础 HTTP 请求

```java
import io.github.xiaomisum.ryze.MagicBox;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class HttpApiExample {

    @Test
    @RyzeTest
    public void testHttpGet() {
        // 单个 HTTP GET 请求
        MagicBox.http(http -> {
            http.variables("id", 1);
            http.title("获取用户信息：id = ${id}");
            http.config(config -> config
                    .protocol("http")
                    .host("127.0.0.1")
                    .port("58081")
                    .method("GET")
                    .path("/user/${id}")
            );
            http.assertions(assertions -> assertions.json("$.data.id", "${id}"));
        });
    }

    @Test
    @RyzeTest
    public void testHttpPost() {
        // HTTP POST 请求
        MagicBox.http(http -> {
            http.title("创建用户");
            http.config(config -> config
                    .protocol("http")
                    .host("127.0.0.1")
                    .port("58081")
                    .method("POST")
                    .path("/user")
                    .body(body -> {
                        body.put("id", "ryze");
                        body.put("name", "ryze_http_sampler");
                        body.put("age", 1);
                    })
            );
            http.assertions(assertions -> assertions.httpStatus(200));
        });
    }
}
```

### 完整测试套件

```java
import io.github.xiaomisum.ryze.MagicBox;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class HttpSuiteExample {

    @Test
    @RyzeTest
    public void userApiTestSuite() {
        MagicBox.suite("测试用例", suite -> {
            suite.variables("id", 1);
            suite.variables("t_body", Collections.of("id", "ryze", "name", "ryze_http_preprocessor", "age", 0));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.variables(var -> var.put("c", 3).put("d", 4));

            // 配置默认HTTP设置
            suite.configureElements(ele ->
                    ele.http(http -> http.config(config -> config
                            .protocol("http")
                            .host("127.0.0.1")
                            .port("58081")
                    ))
            );

            // 前置处理器：新增用户
            suite.preprocessors(pre ->
                    pre.http(http -> {
                        http.title("前置处理器新增用户");
                        http.config(config -> config
                                .method("PUT")
                                .path("/user")
                                .body("${t_body}")
                        );
                        http.extractors(extract -> extract.json("t_id", "$.data.id"));
                    })
            );

            suite.children(child -> {
                child.http(http -> http
                        .title("步骤1——获取用户：id = ${id}")
                        .config(config -> config
                                .method("GET")
                                .path("/user/${id}")
                        )
                        .validators(validator -> validator.json("$.data.id", "${id}"))
                );

                child.http(http -> http
                        .title("步骤2——修改用户：id = ${t_id}")
                        .config(config -> config
                                .method("POST")
                                .path("/user")
                                .body(body -> {
                                    body.put("id", "ryze");
                                    body.put("name", "ryze_http_sampler");
                                    body.put("age", 1);
                                })
                        )
                        .validators(validator -> validator.httpStatus(200))
                );

                child.http(http -> http
                        .title("步骤3——获取用户：id = ${t_body.id}")
                        .config(config -> config
                                .method("GET")
                                .path("/user/${t_body.id}")
                        )
                        .validators(validator -> validator.json("$.data.name", "ryze_http_sampler"))
                );
            });
        });
    }
}
```

## 🐦 Groovy API 示例

### 基础脚本

```groovy
import io.github.xiaomisum.ryze.MagicBox
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyHttpExample {

    @Test
    @RyzeTest
    void testHttpGet() {
        // 简单的 GET 请求
        MagicBox.http {
            variables {
                put "id", "1"
            }
            title "获取用户信息：id = \${id}"
            config {
                protocol "http"
                host "127.0.0.1"
                port "58081"
                method "GET"
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
    }

    @Test
    @RyzeTest
    void testHttpPost() {
        // POST 请求创建数据
        MagicBox.http {
            title "创建用户"
            config {
                protocol "http"
                host "127.0.0.1"
                port "58081"
                method "POST"
                path '/user'
                body { body -> body.putAll([id: "ryze", name: "ryze_http_sampler", age: 0]) }
            }
            validators {
                http {
                    field 'statusCode'
                    rule "=="
                    expected 200
                }
            }
        }
    }
}
```

### 完整测试套件脚本

```groovy
import io.github.xiaomisum.ryze.MagicBox
import io.github.xiaomisum.ryze.support.Collections
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyHttpSuiteExample {

    @Test
    @RyzeTest
    void userApiTestSuite() {
        // 用户API完整测试流程
        MagicBox.suite {
            title "测试用例"
            variables("id", 1)
            variables("t_body", [id: "ryze", name: "ryze_http_preprocessor", age: 0])
            variables {
                // 函数式写法
                put([a: 1, b: 2])
            }
            variables Collections.newHashMap([c: 3, d: 4])

            // HTTP默认配置
            configureElements {
                http {
                    config {
                        protocol "http"
                        method "get"
                        host "127.0.0.1"
                        port "58081"
                    }
                }
            }

            // 前置处理器：新增用户
            preprocessors {
                http {
                    title "前置处理器新增用户"
                    config {
                        method "PUT"
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
            }

            children {
                http {
                    title "步骤1——获取用户：id = \${id}"
                    config {
                        method "GET"
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
            }

            children {
                http {
                    title "步骤2——修改用户：id=\${t_id}"
                    config {
                        method "POST"
                        path '/user'
                        body { body -> body.putAll([id: "ryze", name: "ryze_http_sampler", age: 0]) }
                    }
                    validators {
                        http {
                            field 'statusCode'
                            rule "=="
                            expected 400
                        }
                    }
                }
            }

            children {
                http {
                    title "步骤3——获取用户：id =\${t_body.id}"
                    config {
                        method "GET"
                        path '/user/${t_body.id}'
                    }
                    validators {
                        json {
                            field '$.data.name'
                            rule '=='
                            expected 'ryze_http_sampler'
                        }
                    }
                }
            }
        }
    }
}
```

## ❓ 常见问题

### 配置优先级

配置项的优先级为：**取样器配置 > HTTP 默认配置**

### FAQ

1. **当一个测试集合内存在多个 HTTP默认配置时怎么办？**
    - 多个 HTTP默认配置中的配置会合并，相同配置项以后定义的 HTTP默认配置的值为准。

2. **取样器配置与默认配置冲突时如何处理？**
    - 当取样器中的 HTTP配置项 与 HTTP默认配置中的配置项重复时，以取样器中的配置项的值为准。

3. **如何处理 HTTPS 证书问题？**
    - 可以在配置中设置 `protocol: "https"` 并根据需要配置证书验证选项。

4. **支持哪些 HTTP 方法？**
    - 支持所有标准 HTTP 方法：GET、POST、PUT、DELETE、PATCH、HEAD、OPTIONS 等。

## 📚 相关文档

- [快速开始指南](../QuickStart.md)
- [变量与函数](../help/变量与函数.md)
- [验证器](../help/验证器.md)
- [提取器](../help/提取器.md)
- [示例项目](../../example/http-example/)

---

**💡 提示**: 更多详细示例请参考 [example/http-example](../../example/http-example/) 目录下的完整示例代码。