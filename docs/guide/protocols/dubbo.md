# Dubbo 协议

## 概述

Dubbo 协议支持为 Ryze 测试框架提供了与 Dubbo 微服务进行交互的能力。支持服务发现、负载均衡、容错机制等 Dubbo
核心特性，可以完成分布式服务的端到端测试。

> 注意
>
> 泛化调用适用于老版本 dubbo 通信协议，如果您使用的是 3.3 及之后版本的 triple 协议，请直接使用 triple 自带的 http
> application/json 能力直接发起服务调用，相关示例可参考 网关接入说明。

## 依赖引入

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-dubbo</artifactId>
    <version>${version}</version>
</dependency>
```

## Dubbo 配置元件

Dubbo 默认配置：使用该组件，可配置 Dubbo协议的默认配置，降低测试集合的配置复杂度。

当测试集合描述文件中存在此配置时，下级测试集合中包含的 Dubbo 取样器\处理器从此配置中获取相关配置。

### YAML 配置方式

```yaml
# Dubbo 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: dubbo # 取样器类型
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  registry: # 注册中心配置
    protocol: zookeeper # zookeeper、nacos
    address: localhost:2181
    username:
    password:
    version: 1.0.0
  reference: # reference配置
    version: 1.0.0
    retries: 1
    timeout: 5000
    async: false
    load_balance: random
```

### JSON 配置方式

```json
{
  "testclass": "dubbo",
  "config": {
    "registry": {
      "protocol": "zookeeper",
      "address": "localhost:2181",
      "version": "1.0.0"
    },
    "reference": {
      "version": "1.0.0",
      "retries": 1,
      "timeout": 5000,
      "async": false,
      "load_balance": "random"
    }
  }
}
```

## Dubbo 处理器

### 前置处理器

前置处理器在主要测试之前执行，常用于准备测试数据或初始化服务状态。

```yaml
testclass: dubbo # dubbo前置处理器 类型
config: # 取样器配置
  registry: # 注册中心配置
    protocol: zookeeper
    address: localhost:2181
    username:
    password:
    version: 1.0.0
  reference: # reference配置
    version: 1.0.0
    retries: 1
    timeout: 5000
    async: false
    load_balance: random
  interface: protocol.xyz.ryze.dubbo.dubboserver.service.DemoService  # 接口类名全称
  method: sayHello  # 接口方法
  parameter_types: # 方法参数类型，根据接口定义
    - java.lang.String
  parameters: # 接口参数名称
    - name: test
  attachment_args: # 附加参数 keyword形式，可空
```

### 后置处理器

后置处理器在主要测试之后执行，常用于清理测试数据或恢复服务状态。

```yaml
testclass: dubbo # dubbo后置处理器 类型
config: # 取样器配置
  registry: # 注册中心配置
    protocol: zookeeper
    address: localhost:2181
    username:
    password:
    version: 1.0.0
  reference: # reference配置
    version: 1.0.0
    retries: 1
    timeout: 5000
    async: false
    load_balance: random
  interface: io.github.xiaomisum.ryze.dubbo.example.DemoService  # 接口类名全称
  method: sayHello  # 接口方法
  parameter_types: # 方法参数类型，根据接口定义
    - java.lang.String
  parameters: # 接口参数名称
    - name: test
  attachment_args: # 附加参数 keyword形式，可空
```

## Dubbo 取样器

### YAML 配置方式

```yaml
title: 标准dubbo取样器
testclass: dubbo # 取样器类型
config: # 取样器配置
  registry: # 注册中心配置
    protocol: zookeeper
    address: localhost:2181
    username:
    password:
    version: 1.0.0
  reference: # reference配置
    version: 1.0.0
    retries: 1
    timeout: 5000
    group: test
    async: false
    load_balance: random
  interface: io.github.xiaomisum.ryze.dubbo.example.DemoService  # 接口类名全称
  method: sayHello  # 接口方法
  parameter_types: # 方法参数类型，根据接口定义
    - java.lang.String
  parameters: # 接口参数名称
    - name: test
  attachment_args: # 附加参数 keyword形式，可空
```

### JSON 配置方式

```json
{
  "title": "用户服务调用",
  "testclass": "dubbo",
  "config": {
    "registry": {
      "protocol": "zookeeper",
      "address": "localhost:2181",
      "version": "1.0.0"
    },
    "reference": {
      "version": "1.0.0",
      "timeout": 10000,
      "retries": 2,
      "load_balance": "roundrobin"
    },
    "interface": "com.example.service.UserService",
    "method": "getUserById",
    "parameter_types": [
      "java.lang.Long"
    ],
    "parameters": [
      12345
    ]
  }
}
```

## 常见问题

1. **注册中心连接失败**：检查 ZooKeeper 或 Nacos 服务是否正常运行
2. **服务未找到**：确认服务提供者已注册且接口名称正确
3. **方法调用失败**：检查方法名、参数类型和参数值是否匹配
4. **超时异常**：适当调整 timeout 配置，检查网络连接
5. **版本不匹配**：确保消费者和提供者的版本号一致

## 配置优先级

配置项的优先级为：**取样器配置 > Dubbo 默认配置**

## Java API 示例

### 基础 Dubbo 服务调用

```java
import static io.github.xiaomisum.ryze.protocol.dubbo.DubboMagicBox.*;

public class DubboApiExample {

    public void setupDubboRegistry() {
        // 配置 Dubbo 注册中心
        dubboRegistry("dubboDefault", registry -> registry
                .protocol("zookeeper")
                .address("localhost:2181")
                .version("1.0.0")
                .timeout(30000)
                .retries(1)
                .loadBalance("random")
        );
    }

    public void testSimpleServiceCall() {
        // 简单服务调用
        Result result = dubbo("调用问候服务", builder -> builder
                .registry("dubboDefault")
                .interfaceName("com.example.service.GreetingService")
                .methodName("sayHello")
                .parameterTypes("java.lang.String")
                .parameters("世界")
        );

        // 验证结果
        assertThat(result.getResponseBody()).contains("你好");
    }

    public void testUserServiceCall() {
        // 用户服务调用
        Result result = dubbo("获取用户信息", builder -> builder
                .registry("dubboDefault")
                .interfaceName("com.example.service.UserService")
                .methodName("getUserById")
                .parameterTypes("java.lang.Long")
                .parameters(1L)
        );

        // 验证结果
        assertThat(result.extract("$.id")).isEqualTo(1);
        assertThat(result.extract("$.name")).isNotEmpty();
    }
}
```

### 完整 Dubbo 测试套件

```java
import static io.github.xiaomisum.ryze.protocol.dubbo.DubboMagicBox.*;

public class DubboTestSuite {

    public void userServiceTestSuite() {
        suite("用户服务测试套件", builder -> {
            // 配置 Dubbo 默认设置
            builder.configure(configure -> configure
                    .dubbo(dubbo -> dubbo
                            .ref("dubboDefault")
                            .registry(registry -> registry
                                    .protocol("zookeeper")
                                    .address("localhost:2181")
                                    .version("1.0.0")
                            )
                            .reference(reference -> reference
                                    .version("1.0.0")
                                    .timeout(5000)
                                    .retries(1)
                                    .loadBalance("random")
                            )
                    )
            );

            builder.children(children -> {
                // 前置处理：创建测试用户
                children.dubboPreprocessor("创建测试用户", preprocessor -> preprocessor
                        .interfaceName("com.example.service.UserService")
                        .methodName("createUser")
                        .parameterTypes("com.example.model.User")
                        .parameters(Map.of(
                                "name", "测试用户",
                                "email", "test@example.com",
                                "age", 25
                        ))
                        .extractor(extractor -> extractor
                                .json("$.id", "testUserId")
                        )
                );

                // 主要测试1：查询用户
                children.dubbo("查询新创建用户", dubbo -> dubbo
                        .interfaceName("com.example.service.UserService")
                        .methodName("getUserById")
                        .parameterTypes("java.lang.Long")
                        .parameters("${testUserId}")
                        .assertion(assertion -> assertion
                                .json("$.id", "${testUserId}", "==")
                                .json("$.name", "测试用户", "==")
                                .json("$.email", "test@example.com", "==")
                        )
                );

                // 主要测试2：更新用户
                children.dubbo("更新用户信息", dubbo -> dubbo
                        .interfaceName("com.example.service.UserService")
                        .methodName("updateUser")
                        .parameterTypes("java.lang.Long", "com.example.model.User")
                        .parameters("${testUserId}", Map.of(
                                "name", "更新后的用户",
                                "age", 30
                        ))
                        .assertion(assertion -> assertion
                                .json("$.success", true, "==")
                        )
                );

                // 后置处理：清理测试数据
                children.dubboPostprocessor("删除测试用户", postprocessor -> postprocessor
                        .interfaceName("com.example.service.UserService")
                        .methodName("deleteUser")
                        .parameterTypes("java.lang.Long")
                        .parameters("${testUserId}")
                );
            });
        });
    }
}
```

## Groovy API 示例

### 基础 Dubbo 服务调用

```groovy
import static io.github.xiaomisum.ryze.protocol.dubbo.DubboMagicBox.*

// 配置 Dubbo 注册中心
def setupDubbo() {
    dubboRegistry("dubboDefault") { registry ->
        registry.protocol("zookeeper")
                .address("localhost:2181")
                .version("1.0.0")
                .timeout(30000)
                .retries(1)
                .loadBalance("random")
    }
}

// 简单服务调用
def callGreetingService() {
    def result = dubbo("问候服务调用") { builder ->
        builder.registry("dubboDefault")
                .interfaceName("com.example.service.GreetingService")
                .methodName("sayHello")
                .parameterTypes("java.lang.String")
                .parameters("世界")
    }

    assert result.responseBody.contains("你好")
    return result.responseBody
}

// 复杂参数调用
def callUserService() {
    def userInfo = [
            name      : "张三",
            email     : "zhangsan@example.com",
            age       : 28,
            department: "技术部"
    ]

    def result = dubbo("创建用户") { builder ->
        builder.registry("dubboDefault")
                .interfaceName("com.example.service.UserService")
                .methodName("createUser")
                .parameterTypes("com.example.model.User")
                .parameters(userInfo)
    }

    // 验证创建结果
    assert result.extract("$.id") != null
    assert result.extract("$.name") == "张三"

    return result.extract("$.id")
}

// 异步调用示例
def asyncServiceCall() {
    def result = dubbo("异步服务调用") { builder ->
        builder.registry("dubboDefault")
                .interfaceName("com.example.service.AsyncService")
                .methodName("processDataAsync")
                .parameterTypes("java.util.Map")
                .parameters([
                        data       : "大量数据",
                        processType: "background",
                        callback   : "email"
                ])
                .reference { reference ->
                    reference.async(true)
                            .timeout(10000)
                }
    }

    return result
}
```

### 完整 Dubbo 测试脚本

```groovy
import static io.github.xiaomisum.ryze.protocol.dubbo.DubboMagicBox.*

// 用户服务完整测试流程
suite("用户服务Dubbo测试套件") { builder ->
    // 配置 Dubbo 默认设置
    builder.configure { configure ->
        configure.dubbo { dubbo ->
            dubbo.ref("dubboDefault")
                    .registry { registry ->
                        registry.protocol("zookeeper")
                                .address("localhost:2181")
                                .version("1.0.0")
                                .timeout(30000)
                    }
                    .reference { reference ->
                        reference.version("1.0.0")
                                .timeout(5000)
                                .retries(1)
                                .loadBalance("random")
                                .async(false)
                    }
        }
    }

    builder.children { children ->
        // 1. 准备数据：创建测试用户
        children.dubboPreprocessor("创建测试用户") { preprocessor ->
            preprocessor.interfaceName("com.example.service.UserService")
                    .methodName("createUser")
                    .parameterTypes("com.example.model.User")
                    .parameters([
                            name      : "Groovy测试用户",
                            email     : "groovy@test.com",
                            age       : 30,
                            department: "测试部门",
                            role      : "developer",
                            status    : "active"
                    ])
                    .extractor { extractor ->
                        extractor.json("$.id", "testUserId")
                                .json("$.email", "testUserEmail")
                    }
        }

        // 2. 查询用户详情
        children.dubbo("查询用户详情") { dubbo ->
            dubbo.interfaceName("com.example.service.UserService")
                    .methodName("getUserById")
                    .parameterTypes("java.lang.Long")
                    .parameters("${testUserId}")
                    .assertion { assertion ->
                        assertion.json("$.id", "${testUserId}", "==")
                                .json("$.name", "Groovy测试用户", "==")
                                .json("$.email", "groovy@test.com", "==")
                                .json("$.department", "测试部门", "==")
                                .json("$.status", "active", "==")
                    }
        }

        // 3. 获取用户列表
        children.dubbo("获取部门用户列表") { dubbo ->
            dubbo.interfaceName("com.example.service.UserService")
                    .methodName("getUsersByDepartment")
                    .parameterTypes("java.lang.String")
                    .parameters("测试部门")
                    .assertion { assertion ->
                        assertion.json("$.size()", 1, ">=")
                                .json("$[?(@.id == '${testUserId}')].name", "Groovy测试用户", "==")
                    }
        }

        // 4. 更新用户信息
        children.dubbo("更新用户信息") { dubbo ->
            dubbo.interfaceName("com.example.service.UserService")
                    .methodName("updateUser")
                    .parameterTypes("java.lang.Long", "com.example.model.User")
                    .parameters("${testUserId}", [
                            name        : "更新后的Groovy用户",
                            age         : 35,
                            department  : "升级后部门",
                            role        : "senior_developer",
                            lastModified: new Date().toString()
                    ])
                    .assertion { assertion ->
                        assertion.json("$.success", true, "==")
                                .json("$.message", "更新成功", "==")
                    }
        }

        // 5. 验证更新结果
        children.dubbo("验证更新结果") { dubbo ->
            dubbo.interfaceName("com.example.service.UserService")
                    .methodName("getUserById")
                    .parameterTypes("java.lang.Long")
                    .parameters("${testUserId}")
                    .assertion { assertion ->
                        assertion.json("$.name", "更新后的Groovy用户", "==")
                                .json("$.age", 35, "==")
                                .json("$.department", "升级后部门", "==")
                                .json("$.role", "senior_developer", "==")
                    }
        }

        // 6. 测试业务逻辑
        children.dubbo("测试用户激活状态") { dubbo ->
            dubbo.interfaceName("com.example.service.UserService")
                    .methodName("isUserActive")
                    .parameterTypes("java.lang.Long")
                    .parameters("${testUserId}")
                    .assertion { assertion ->
                        assertion.json("$.active", true, "==")
                                .json("$.status", "active", "==")
                    }
        }

        // 7. 测试权限检查
        children.dubbo("检查用户权限") { dubbo ->
            dubbo.interfaceName("com.example.service.AuthService")
                    .methodName("checkUserPermissions")
                    .parameterTypes("java.lang.Long", "java.lang.String")
                    .parameters("${testUserId}", "admin")
                    .assertion { assertion ->
                        assertion.json("$.hasPermission", true, "||false")
                    }
        }

        // 8. 清理：删除测试用户
        children.dubboPostprocessor("删除测试用户") { postprocessor ->
            postprocessor.interfaceName("com.example.service.UserService")
                    .methodName("deleteUser")
                    .parameterTypes("java.lang.Long")
                    .parameters("${testUserId}")
                    .assertion { assertion ->
                        assertion.json("$.deleted", true, "==")
                    }
        }
    }
}

// 分布式服务测试示例
def distributedServiceTest() {
    suite("分布式服务测试") { builder ->
        builder.children { children ->
            // 测试服务发现
            children.dubbo("服务发现测试") { dubbo ->
                dubbo.interfaceName("com.example.service.DiscoveryService")
                        .methodName("listAvailableServices")
                        .parameterTypes()
                        .parameters()
                        .assertion { assertion ->
                            assertion.json("$.size()", 0, ">")
                                    .json("$[?(@.name == 'UserService')].status", "UP", "==")
                        }
            }

            // 测试负载均衡
            (1..5).each { i ->
                children.dubbo("负载均衡测试: ${i}") { dubbo ->
                    dubbo.interfaceName("com.example.service.LoadBalanceService")
                            .methodName("getServerInfo")
                            .parameterTypes()
                            .parameters()
                            .reference { reference ->
                                reference.loadBalance("roundrobin")
                            }
                            .assertion { assertion ->
                                assertion.json("$.serverId", "", "isNotEmpty")
                                        .json("$.serverName", "", "isNotEmpty")
                            }
                }
            }
        }
    }
}
```

## 相关文档

- [Dubbo 官方文档](https://dubbo.apache.org/zh/docs/)

- [示例项目](https://github.com/XiaoMiSum/ryze/tree/master/example/dubbo-example)

---

**💡 提示**:
更多详细示例请参考 [example/dubbo-example](https://github.com/XiaoMiSum/ryze/tree/master/example/dubbo-example)
目录下的完整示例代码。