# Redis 协议

## 概述

Redis 协议支持为 Ryze 测试框架提供了与 Redis 数据库进行交互的能力。支持字符串、哈希、列表、集合等多种数据类型操作，以及缓存测试、分布式锁测试等常见
Redis 应用场景。

## 📊 配置项参考表

### Redis 数据源配置

| 配置项              | 类型           | 默认值        | 必需 | 描述                                              |
|------------------|--------------|------------|----|-------------------------------------------------|
| mode             | String       | standalone | ❌  | 部署模式: standalone/cluster/sentinel              |
| url              | String       | -          | ✅  | Redis 连接 URL (standalone 模式)                    |
| nodes            | List\<String\> | -          | ❌  | 集群/哨兵节点列表 (host:port)，cluster/sentinel 模式必填  |
| master_name       | String       | -          | ❌  | Sentinel 主节点名称，sentinel 模式必填                  |
| password         | String       | -          | ❌  | Redis 密码                                        |
| sentinel_password | String       | -          | ❌  | Sentinel 密码，sentinel 模式可选                      |
| timeout          | Integer      | 5000       | ❌  | 连接超时时间 (毫秒)                                     |
| max_total        | Integer      | 10         | ❌  | 最大池大小                                           |
| max_idle         | Integer      | 5          | ❌  | 最大空闲连接数                                         |
| min_idle         | Integer      | 1          | ❌  | 最小空闲连接数                                         |
| max_wait_millis    | long         | -1         | ❌  | 连接等待超时 (毫秒)，-1 表示无限等待                         |
| test_on_borrow     | boolean      | false      | ❌  | 借用连接时是否测试连接有效性                                 |
| test_on_return     | boolean      | false      | ❌  | 归还连接时是否测试连接有效性                                 |
| test_while_idle    | boolean      | false      | ❌  | 空闲时是否测试连接有效性                                   |

> **向后兼容**: 不指定 `mode` 时默认使用 `standalone` 模式，现有配置无需任何修改。

### Redis 命令配置

| 配置项        | 类型     | 默认值 | 必需 | 描述                              |
|------------|--------|-----|----|---------------------------------|
| datasource | String | -   | ✅  | 数据源引用名                          |
| command    | String | -   | ✅  | Redis 命令 (GET/SET/LPUSH/HSET 等) |
| args       | Array  | -   | ❌  | 命令参数数组                          |

> **配置优先级**: 取样器配置 > Redis 默认配置

## 依赖引入

Redis 协议支持内置在核心模块中，无需额外依赖：

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze</artifactId>
    <version>${version}</version>
</dependency>
```

## Redis 配置元件

Redis 数据源：使用该组件配置 Redis 数据源，用于 Redis 处理器\取样器引用。

### 部署模式说明

Redis 数据源支持三种部署模式：

| 模式         | 说明                       | 适用场景                  |
|------------|--------------------------|----------------------|
| standalone | 单机模式（默认），连接单个 Redis 实例  | 开发环境、小规模应用           |
| cluster    | 集群模式，连接 Redis Cluster    | 大规模数据分片、高吞吐场景       |
| sentinel   | 哨兵模式，通过 Sentinel 实现高可用   | 需要自动故障转移的生产环境       |

### Standalone 模式（默认）

最简配置方式，与之前版本完全兼容：

```yaml
# Standalone 模式 - 默认模式，无需指定 mode
testclass: redis
ref_name: redis_source
config:
  url: 'redis://127.0.0.1:6379/0'
  timeout: 5000
  max_total: 10
  max_idle: 5
  min_idle: 1
```

### Cluster 模式

适用于 Redis Cluster 集群环境，通过 `nodes` 指定集群节点列表：

```yaml
# Cluster 模式
testclass: redis
ref_name: redis_cluster
config:
  mode: cluster
  nodes:
    - 10.0.0.1:7000
    - 10.0.0.2:7001
    - 10.0.0.3:7002
  password: secret
  timeout: 5000
  max_total: 20
  max_idle: 10
  test_on_borrow: true
```

### Sentinel 模式

适用于 Redis Sentinel 高可用环境，通过 `master_name` 和 `nodes` 指定哨兵配置：

```yaml
# Sentinel 模式
testclass: redis
ref_name: redis_sentinel
config:
  mode: sentinel
  master_name: mymaster
  nodes:
    - 10.0.0.1:26379
    - 10.0.0.2:26379
    - 10.0.0.3:26379
  password: redis_password
  sentinel_password: sentinel_password
  timeout: 5000
  max_total: 15
  max_wait_millis: 5000
```

### JSON 配置方式

```json
{
  "testclass": "redis",
  "ref_name": "redis_source",
  "config": {
    "url": "redis://127.0.0.1:6379/0",
    "timeout": 5000,
    "max_total": 10,
    "max_idle": 5,
    "min_idle": 1
  }
}
```

Cluster 模式 JSON 示例：

```json
{
  "testclass": "redis",
  "ref_name": "redis_cluster",
  "config": {
    "mode": "cluster",
    "nodes": ["10.0.0.1:7000", "10.0.0.2:7001", "10.0.0.3:7002"],
    "password": "secret",
    "max_total": 20,
    "max_idle": 10,
    "test_on_borrow": true
  }
}
```

### 高级连接池配置

连接池参数用于优化 Redis 连接的管理和复用，合理配置可以提升系统稳定性和性能：

| 参数            | 推荐值   | 说明                                                    |
|---------------|-------|-------------------------------------------------------|
| max_total     | 10-50 | 根据并发量设置，过大会浪费资源，过小会导致等待                              |
| max_idle      | 5-20  | 建议为 max_total 的 50%-80%                               |
| min_idle      | 1-5   | 保持少量空闲连接，避免冷启动延迟                                     |
| max_wait_millis | 3000-10000 | 建议设置合理超时，避免无限等待。-1 为无限等待（默认）                    |
| test_on_borrow  | true  | 生产环境建议开启，确保获取的连接可用                                   |
| test_on_return  | false | 一般无需开启，会增加归还连接的耗时                                    |
| test_while_idle | true  | 建议开启，配合空闲检测剔除无效连接                                    |

**生产环境推荐配置示例**：

```yaml
testclass: redis
ref_name: redis_production
config:
  mode: sentinel
  master_name: mymaster
  nodes:
    - 10.0.0.1:26379
    - 10.0.0.2:26379
    - 10.0.0.3:26379
  password: your_password
  timeout: 3000
  max_total: 30
  max_idle: 20
  min_idle: 5
  max_wait_millis: 5000
  test_on_borrow: true
  test_while_idle: true
```

## Redis 处理器

### 前置处理器

前置处理器在主要测试之前执行，常用于准备测试数据或清理缓存。

```yaml
testclass: redis  # redis 前置处理器类型
config: # 处理器配置
  datasource: redis_source  # 数据源，必须先定义数据源
  command: SETEX  # Redis命令
  args: # 命令参数
    - test:user:1
    - 3600
    - '{"name":"测试用户","age":25}'
```

### 后置处理器

后置处理器在主要测试之后执行，常用于清理测试数据。

```yaml
testclass: redis  # redis 后置处理器类型
config: # 处理器配置
  datasource: redis_source  # 数据源，必须先定义数据源
  command: DEL  # Redis命令
  args: # 命令参数
    - test:user:1
    - test:list
```

## Redis 取样器

### YAML 配置方式

```yaml
title: 标准Redis取样器
testclass: redis  # 取样器类型
config: # 取样器配置
  datasource: redis_source  # 数据源，必须先定义数据源
  command: GET  # Redis命令
  args: # 命令参数
    - test:user:1
```

### JSON 配置方式

```json
{
  "title": "Redis字符串操作",
  "testclass": "redis",
  "config": {
    "datasource": "redis_source",
    "command": "HGETALL",
    "args": [
      "user:profile:123"
    ]
  }
}
```

## 常见问题

1. **连接超时问题**：检查 Redis 服务是否正常运行，网络连接是否畅通
2. **认证失败**：确保 URL 中包含正确的用户名和密码
3. **数据库选择**：URL 中的数字表示数据库编号（0-15）
4. **内存不足**：检查 Redis 内存使用情况，适当调整 maxmemory 配置

## 配置优先级

配置项的优先级为：**取样器配置 > Redis 默认配置**

## Java API 示例

### 基础 Redis 操作

```java
import static io.github.xiaomisum.ryze.Ryze.*;

public class RedisApiExample {

    public void setupRedisDataSource() {
        // 配置 Redis 数据源
        redisDatasource("redisDefault", datasource -> datasource
                .url("redis://localhost:6379/0")
                .timeout(5000)
                .maxTotal(10)
                .maxIdle(5)
                .minIdle(1)
        );
    }

    public void testStringOperations() {
        // 字符串操作
        Result setResult = redis("设置用户缓存", builder -> builder
                .datasource("redisDefault")
                .command("SETEX")
                .args("user:123", "3600", "张三")
        );

        Result getResult = redis("获取用户缓存", builder -> builder
                .datasource("redisDefault")
                .command("GET")
                .args("user:123")
        );

        assertThat(getResult.getResponseBody()).isEqualTo("张三");
    }

    public void testHashOperations() {
        // 哈希操作
        redis("设置用户信息", builder -> builder
                .datasource("redisDefault")
                .command("HMSET")
                .args("user:info:123",
                        "name", "李四",
                        "age", "30",
                        "city", "北京")
        );

        Result result = redis("获取用户信息", builder -> builder
                .datasource("redisDefault")
                .command("HGETALL")
                .args("user:info:123")
        );

        // 验证结果
        assertThat(result.extract("$.name")).isEqualTo("李四");
        assertThat(result.extract("$.age")).isEqualTo("30");
    }
}
```

### 完整 Redis 测试套件

```java
import static io.github.xiaomisum.ryze.Ryze.*;

public class RedisTestSuite {

    public void redisCacheTestSuite() {
        suite("Redis缓存测试套件", builder -> {
            // 配置 Redis 默认设置
            builder.configure(configure -> configure
                    .redis(redis -> redis
                            .ref("redisDefault")
                            .url("redis://localhost:6379/0")
                            .timeout(5000)
                            .maxTotal(10)
                            .maxIdle(5)
                            .minIdle(1)
                    )
            );

            builder.children(children -> {
                // 前置处理：清理测试数据
                children.redisPreprocessor("清理测试数据", preprocessor -> preprocessor
                        .datasource("redisDefault")
                        .command("FLUSHDB")
                );

                // 测试1：字符串缓存
                children.redis("设置用户缓存", redis -> redis
                        .datasource("redisDefault")
                        .command("SETEX")
                        .args("test:user:1", "3600", "{\"name\":\"测试用户\",\"age\":25}")
                        .assertion(assertion -> assertion
                                .responseBody("OK")
                        )
                );

                // 测试2：获取缓存
                children.redis("获取用户缓存", redis -> redis
                        .datasource("redisDefault")
                        .command("GET")
                        .args("test:user:1")
                        .assertion(assertion -> assertion
                                .json("$.name", "测试用户", "==")
                                .json("$.age", 25, "==")
                        )
                );

                // 测试3：列表操作
                children.redis("添加列表元素", redis -> redis
                        .datasource("redisDefault")
                        .command("LPUSH")
                        .args("test:list", "item1", "item2", "item3")
                        .assertion(assertion -> assertion
                                .responseBody("3")
                        )
                );

                // 后置处理：清理数据
                children.redisPostprocessor("清理测试数据", postprocessor -> postprocessor
                        .datasource("redisDefault")
                        .command("DEL")
                        .args("test:user:1", "test:list")
                );
            });
        });
    }
}
```

## Groovy API 示例

### 基础 Redis 操作

```groovy
import static io.github.xiaomisum.ryze.Ryze.*

// 配置 Redis 数据源
def setupRedis() {
    redisDatasource("redisDefault") { datasource ->
        datasource.url("redis://localhost:6379/0")
                .timeout(5000)
                .maxTotal(10)
                .maxIdle(5)
                .minIdle(1)
    }
}

// 基本字符串操作
def stringOperations() {
    // 设置值
    redis("设置用户名") { builder ->
        builder.datasource("redisDefault")
                .command("SET")
                .args("user:123:name", "张三")
    }

    // 获取值
    def result = redis("获取用户名") { builder ->
        builder.datasource("redisDefault")
                .command("GET")
                .args("user:123:name")
    }

    assert result.responseBody == "张三"
    return result.responseBody
}

// 哈希操作
def hashOperations() {
    // 设置哈希字段
    redis("设置用户信息") { builder ->
        builder.datasource("redisDefault")
                .command("HMSET")
                .args("user:info:123",
                        "name", "李四",
                        "age", "30",
                        "city", "北京",
                        "department", "技术部")
    }

    // 获取所有哈希字段
    def result = redis("获取用户信息") { builder ->
        builder.datasource("redisDefault")
                .command("HGETALL")
                .args("user:info:123")
    }

    // 返回结果为 Map 形式
    return result.responseBody
}

// 列表操作
def listOperations() {
    def listKey = "user:notifications:123"

    // 添加通知
    ["欢迎加入", "系统更新", "新消息提醒"].each { notification ->
        redis("添加通知: ${notification}") { builder ->
            builder.datasource("redisDefault")
                    .command("LPUSH")
                    .args(listKey, notification)
        }
    }

    // 获取所有通知
    def result = redis("获取通知列表") { builder ->
        builder.datasource("redisDefault")
                .command("LRANGE")
                .args(listKey, "0", "-1")
    }

    return result.responseBody
}
```

### 完整 Redis 测试脚本

```groovy
import static io.github.xiaomisum.ryze.Ryze.*

// Redis 缓存完整测试流程
suite("Redis缓存功能测试") { builder ->
    // 配置 Redis 数据源
    builder.configure { configure ->
        configure.redis { redis ->
            redis.ref("redisDefault")
                    .url("redis://localhost:6379/0")
                    .timeout(5000)
                    .maxTotal(10)
                    .maxIdle(5)
                    .minIdle(1)
        }
    }

    builder.children { children ->
        // 1. 数据准备：清理旧数据
        children.redisPreprocessor("清理测试数据") { preprocessor ->
            preprocessor.datasource("redisDefault")
                    .command("FLUSHDB")
        }

        // 2. 基本缓存操作
        children.redis("设置用户基本信息") { redis ->
            redis.datasource("redisDefault")
                    .command("SETEX")
                    .args("cache:user:001", "3600", """
                     {
                         "id": "001",
                         "name": "Groovy测试用户",
                         "email": "groovy@test.com",
                         "role": "developer",
                         "lastLogin": "2024-01-01T10:00:00Z"
                     }
                 """)
                    .assertion { assertion ->
                        assertion.responseBody("OK")
                    }
        }

        // 3. 验证缓存获取
        children.redis("获取用户信息") { redis ->
            redis.datasource("redisDefault")
                    .command("GET")
                    .args("cache:user:001")
                    .assertion { assertion ->
                        assertion.json("$.id", "001", "==")
                                .json("$.name", "Groovy测试用户", "==")
                                .json("$.role", "developer", "==")
                                .json("$.email", "groovy@test.com", "==")
                    }
        }

        // 4. 设置哈希类型数据
        children.redis("设置用户配置") { redis ->
            redis.datasource("redisDefault")
                    .command("HMSET")
                    .args("config:user:001",
                            "theme", "dark",
                            "language", "zh-CN",
                            "timezone", "Asia/Shanghai",
                            "notifications", "true")
                    .assertion { assertion ->
                        assertion.responseBody("OK")
                    }
        }

        // 5. 获取哈希数据
        children.redis("获取用户配置") { redis ->
            redis.datasource("redisDefault")
                    .command("HGETALL")
                    .args("config:user:001")
                    .assertion { assertion ->
                        assertion.json("$.theme", "dark", "==")
                                .json("$.language", "zh-CN", "==")
                                .json("$.timezone", "Asia/Shanghai", "==")
                                .json("$.notifications", "true", "==")
                    }
        }

        // 6. 列表操作：添加最近访问记录
        children.redis("添加访问记录") { redis ->
            redis.datasource("redisDefault")
                    .command("LPUSH")
                    .args("recent:user:001", "/dashboard", "/profile", "/settings")
                    .assertion { assertion ->
                        assertion.responseBody("3")
                    }
        }

        // 7. 获取最近访问记录
        children.redis("获取访问记录") { redis ->
            redis.datasource("redisDefault")
                    .command("LRANGE")
                    .args("recent:user:001", "0", "4")
                    .assertion { assertion ->
                        assertion.json("$.size()", 3, "==")
                                .json("$[0]", "/settings", "==")
                                .json("$[1]", "/profile", "==")
                                .json("$[2]", "/dashboard", "==")
                    }
        }

        // 8. 集合操作：添加权限
        children.redis("添加用户权限") { redis ->
            redis.datasource("redisDefault")
                    .command("SADD")
                    .args("permissions:user:001", "read", "write", "delete", "admin")
                    .assertion { assertion ->
                        assertion.responseBody("4")
                    }
        }

        // 9. 检查权限
        children.redis("检查管理员权限") { redis ->
            redis.datasource("redisDefault")
                    .command("SISMEMBER")
                    .args("permissions:user:001", "admin")
                    .assertion { assertion ->
                        assertion.responseBody("1")
                    }
        }

        // 10. 获取所有权限
        children.redis("获取所有权限") { redis ->
            redis.datasource("redisDefault")
                    .command("SMEMBERS")
                    .args("permissions:user:001")
                    .assertion { assertion ->
                        assertion.json("$.size()", 4, "==")
                    }
        }

        // 11. 清理：删除所有测试数据
        children.redisPostprocessor("清理所有测试数据") { postprocessor ->
            postprocessor.datasource("redisDefault")
                    .command("DEL")
                    .args("cache:user:001", "config:user:001",
                            "recent:user:001", "permissions:user:001")
        }
    }
}
```

## 相关文档

- [Redis 官方文档](https://redis.io/documentation)

- [示例项目](https://github.com/XiaoMiSum/ryze/tree/master/example/redis-example)

---

**💡 提示**:
更多详细示例请参考 [example/redis-example](https://github.com/XiaoMiSum/ryze/tree/master/example/redis-example)
目录下的完整示例代码。