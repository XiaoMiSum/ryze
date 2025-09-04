# MongoDB 协议

## 概述

MongoDB 协议支持为 Ryze 测试框架提供了与 MongoDB 数据库进行交互的能力。支持文档的增删改查、聚合查询、索引操作等 MongoDB
核心功能，适用于 NoSQL 数据库的测试场景。

## 依赖引入

```xml

<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze-mongo</artifactId>
    <version>6.0.1</version>
</dependency>
```

## MongoDB 配置元件

### YAML 配置方式

```yaml
# MongoDB 默认配置，各配置项的优先级为：取样器 > 默认配置
testclass: mongo  # 取样器类型
ref_name: mongo_source # 引用名称，可空，空值时，使用默认Key作为引用名称
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  url: 'mongodb://root:123456@127.0.0.1:27017/?authSource=admin' # 连接URL
  database: 'demo' # 数据库名称
  collection: 'demo' # 集合名，可空
```

### JSON 配置方式

```json
{
  "testclass": "mongo",
  "ref_name": "mongo_source",
  "config": {
    "url": "mongodb://root:123456@127.0.0.1:27017/?authSource=admin",
    "database": "testdb",
    "collection": "users"
  }
}
```

## MongoDB 取样器

### YAML 配置方式

```yaml
testclass: mongo  # 取样器类型
config: # 取样器配置
  url: 'mongodb://root:123456@127.0.0.1:27017/?authSource=admin'
  database: 'demo' # 数据库名称
  collection: 'demo' # 集合名
  action: find  # 操作类型：find、insert、update、delete、aggregate
  condition: { } # 条件，非空
```

### JSON 配置方式

```json
{
  "testclass": "mongo",
  "config": {
    "url": "mongodb://localhost:27017",
    "database": "testdb",
    "collection": "users",
    "action": "find",
    "condition": {
      "age": {
        "$gte": 18
      },
      "status": "active"
    }
  }
}
```

## 常见问题

1. **连接失败**：检查 MongoDB 服务是否正常运行，连接字符串是否正确
2. **认证失败**：确认用户名、密码和认证数据库配置
3. **数据库不存在**：MongoDB 会自动创建不存在的数据库和集合
4. **查询语法错误**：检查查询条件的 JSON 格式是否正确
5. **权限不足**：确保用户有相应数据库和集合的操作权限

## Java API 示例

```java
import static io.github.xiaomisum.ryze.protocol.mongo.MongoMagicBox.*;

public class MongoApiExample {

    public void testInsertDocument() {
        // 插入文档
        Result result = mongo("插入用户文档", builder -> builder
                .url("mongodb://localhost:27017")
                .database("testdb")
                .collection("users")
                .action("insert")
                .document(Map.of(
                        "name", "张三",
                        "email", "zhangsan@example.com",
                        "age", 25,
                        "department", "技术部",
                        "skills", List.of("Java", "Python", "MongoDB"),
                        "createdAt", new Date()
                ))
        );

        assertThat(result.extract("$.insertedId")).isNotNull();
    }

    public void testFindDocuments() {
        // 查询文档
        Result result = mongo("查询用户文档", builder -> builder
                .url("mongodb://localhost:27017")
                .database("testdb")
                .collection("users")
                .action("find")
                .condition(Map.of(
                        "department", "技术部",
                        "age", Map.of("$gte", 20)
                ))
        );

        assertThat(result.extract("$.length()")).isGreaterThan(0);
    }

    public void testUpdateDocument() {
        // 更新文档
        mongo("更新用户信息", builder -> builder
                .url("mongodb://localhost:27017")
                .database("testdb")
                .collection("users")
                .action("update")
                .condition(Map.of("email", "zhangsan@example.com"))
                .document(Map.of(
                        "$set", Map.of(
                                "age", 26,
                                "lastModified", new Date()
                        ),
                        "$addToSet", Map.of(
                                "skills", "Docker"
                        )
                ))
        );
    }
}
```

## Groovy API 示例

```groovy
import static io.github.xiaomisum.ryze.protocol.mongo.MongoMagicBox.*

// 基本文档操作
def createUser() {
    def result = mongo("创建用户文档") { builder ->
        builder.url("mongodb://localhost:27017")
                .database("testdb")
                .collection("users")
                .action("insert")
                .document([
                        name      : "李四",
                        email     : "lisi@example.com",
                        age       : 30,
                        department: "产品部",
                        profile   : [
                                bio     : "产品经理",
                                location: "北京",
                                joinDate: new Date()
                        ],
                        tags      : ["product", "management", "strategy"]
                ])
    }

    def userId = result.extract("$.insertedId")
    return userId
}

// 完整数据库测试套件
suite("MongoDB数据库测试") { builder ->
    builder.configure { configure ->
        configure.mongo { mongo ->
            mongo.url("mongodb://localhost:27017")
                    .database("testdb")
        }
    }

    builder.children { children ->
        // 1. 清理测试数据
        children.mongoPreprocessor("清理测试数据") { preprocessor ->
            preprocessor.collection("users")
                    .action("delete")
                    .condition([email: [$regex: ".*@test.com"]])
        }

        // 2. 插入测试数据
        children.mongo("插入用户数据") { mongo ->
            mongo.collection("users")
                    .action("insert")
                    .document([
                            name      : "Groovy测试用户",
                            email     : "groovy@test.com",
                            age       : 28,
                            department: "测试部",
                            skills    : ["Groovy", "MongoDB", "Testing"],
                            metadata  : [
                                    createdBy  : "test-suite",
                                    version    : "1.0",
                                    environment: "testing"
                            ],
                            active    : true
                    ])
                    .assertion { assertion ->
                        assertion.json("$.insertedId", "", "isNotEmpty")
                    }
                    .extractor { extractor ->
                        extractor.json("$.insertedId", "testUserId")
                    }
        }

        // 3. 查询数据
        children.mongo("查询用户信息") { mongo ->
            mongo.collection("users")
                    .action("find")
                    .condition([email: "groovy@test.com"])
                    .assertion { assertion ->
                        assertion.json("$[0].name", "Groovy测试用户", "==")
                                .json("$[0].department", "测试部", "==")
                                .json("$[0].active", true, "==")
                                .json("$[0].skills.size()", 3, "==")
                    }
        }

        // 4. 更新数据
        children.mongo("更新用户信息") { mongo ->
            mongo.collection("users")
                    .action("update")
                    .condition([_id: "${testUserId}"])
                    .document([
                            "$set"     : [
                                    age                    : 30,
                                    department             : "高级测试部",
                                    "metadata.lastModified": new Date(),
                                    "metadata.version"     : "1.1"
                            ],
                            "$addToSet": [
                                    skills: "Automation"
                            ]
                    ])
                    .assertion { assertion ->
                        assertion.json("$.modifiedCount", 1, "==")
                    }
        }

        // 5. 验证更新结果
        children.mongo("验证更新结果") { mongo ->
            mongo.collection("users")
                    .action("find")
                    .condition([_id: "${testUserId}"])
                    .assertion { assertion ->
                        assertion.json("$[0].age", 30, "==")
                                .json("$[0].department", "高级测试部", "==")
                                .json("$[0].skills", "Automation", "contains")
                                .json("$[0].metadata.version", "1.1", "==")
                    }
        }

        // 6. 删除数据
        children.mongo("删除测试数据") { mongo ->
            mongo.collection("users")
                    .action("delete")
                    .condition([email: "groovy@test.com"])
                    .assertion { assertion ->
                        assertion.json("$.deletedCount", 1, "==")
                    }
        }
    }
}
```

## 相关文档

- [MongoDB 官方文档](https://docs.mongodb.com/)