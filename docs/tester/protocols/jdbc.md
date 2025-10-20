# 🗃️ JDBC 数据库测试指南

## 📖 概述

JDBC 协议是 Ryze 框架内置支持的数据库测试协议，提供完整的数据库操作测试能力，支持各种 SQL 操作包括查询、插入、更新、删除等，同时支持事务处理和连接池管理。

## 🚀 依赖引入

JDBC 支持内置在核心模块中，需要添加对应的数据库驱动：

```xml
<!-- Ryze 核心依赖 -->
<dependency>
    <groupId>io.github.xiaomisum</groupId>
    <artifactId>ryze</artifactId>
    <version>${version}</version>
</dependency>

        <!-- MySQL 驱动 -->
<dependency>
<groupId>com.mysql</groupId>
<artifactId>mysql-connector-j</artifactId>
<version>9.3.0</version>
</dependency>
```

## ⚙️ 配置元件

### JDBC 数据源

JDBC 数据源：使用该组件配置 JDBC数据源，用于 JDBC处理器\取样器引用。

```yaml
# jdbc取样器、处理器必须引用一个jdbc数据源
testclass: jdbc # 配置元件类型
ref_name: JDBCDataSource_var  # 数据源名称
config: # 可简化填写，无需config关键字，直接将配置内容至于首层
  driver: com.mysql.cj.jdbc.Driver # jdbc驱动，jdbc版本没支持 SPI时必填
  url: 'jdbc:mysql://127.0.0.1:3306/db-template?characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2b8&failOverReadOnly=false'
  username: 'root'
  password: '123456'
  max_active: '10'
  max_wait: '60000'
```

## 🔧 处理器

### 前置处理器

```yaml
testclass: jdbc  # jdbc 前置处理器类型
config: # 处理器配置
  datasource: JDBCDataSource_var  # 数据源，必须先定义数据源
  sql: 'select * from sys_user where id = 1;'  # sql语句
```

### 后置处理器

```yaml
testclass: jdbc_postprocessor  # jdbc 后置处理器类型
config: # 处理器配置
  datasource: JDBCDataSource_var  # 数据源，必须先定义数据源
  sql: 'select * from sys_user where id = ?;'  # sql语句
  args: [ 1 ]
```

## 📊 取样器

### JDBC 取样器

```yaml
title: 标准jdbc取样器
testclass: jdbc  # 取样器类型
config: # 取样器配置
  datasource: JDBCDataSource_var  # 数据源，必须先定义数据源
  sql: 'select * from sys_user where id = 1;'  # sql语句
```

## 💻 Java API 示例

### 基础数据库操作

```java
import io.github.xiaomisum.ryze.MagicBox;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class JdbcApiExample {

    @Test
    @RyzeTest
    public void testJdbcInsert() {
        // 单个 JDBC 插入操作
        MagicBox.jdbc(jdbc -> {
            jdbc.title("插入用户：tick = jdbc_preprocessor");
            jdbc.configureElements(ele -> ele.jdbc(j -> j
                    .refName("jdbc_source")
                    .config(config -> config
                            .username("root")
                            .password("123456qq!")
                            .url("jdbc:mysql://127.0.0.1:3306/ryze-test?characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false")
                    )
            ));
            jdbc.config(config -> config
                    .datasource("jdbc_source")
                    .sql("insert into t_001 (tick, name) values (?, ?);")
                    .args("jdbc_preprocessor", "ryze_http_sampler")
            );
        });
    }

    @Test
    @RyzeTest
    public void testJdbcQuery() {
        // JDBC 查询操作
        MagicBox.jdbc(jdbc -> {
            jdbc.title("查找用户：tick = jdbc_preprocessor");
            jdbc.configureElements(ele -> ele.jdbc(j -> j
                    .refName("jdbc_source")
                    .config(config -> config
                            .username("root")
                            .password("123456qq!")
                            .url("jdbc:mysql://127.0.0.1:3306/ryze-test?characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false")
                    )
            ));
            jdbc.postprocessors(post -> post.jdbc(j -> j
                    .config(config -> config
                            .datasource("jdbc_source")
                            .sql("truncate table t_001;")
                    )
            ));
            jdbc.config(config -> config
                    .datasource("jdbc_source")
                    .sql("select * from t_001  where tick = \"jdbc_preprocessor\";")
            );
            jdbc.assertions(assertions -> assertions.json("$.name", "ryze_http_sampler"));
        });
    }
}
```

### 完整数据库测试套件

```java
import io.github.xiaomisum.ryze.MagicBox;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class JdbcSuiteExample {

    @Test
    @RyzeTest
    public void userDatabaseTestSuite() {
        MagicBox.suite("测试用例", suite -> {
            suite.variables("id", 1);
            suite.variables(var -> var.put("tick", "ryze"));
            suite.variables(Map.of("a", 1, "b", 2));

            // 配置 JDBC 数据源
            suite.configureElements(ele -> ele.jdbc(jdbc -> jdbc
                    .refName("jdbc_source")
                    .config(config -> config
                            .username("root")
                            .password("123456qq!")
                            .url("jdbc:mysql://127.0.0.1:3306/ryze-test?characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false")
                    )
            ));

            // 前置处理：插入测试数据
            suite.preprocessors(pre ->
                    pre.jdbc(jdbc -> jdbc.config(config -> config
                            .datasource("jdbc_source")
                            .sql("insert into t_001 (tick, name) values (\"jdbc_preprocessor\", \"jdbc_preprocessor\");")
                    ))
            );

            // 后置处理：清理数据
            suite.postprocessors(post -> post.jdbc(jdbc -> jdbc.config(config -> config
                    .datasource("jdbc_source")
                    .sql("truncate table t_001;")
            )));

            suite.children(child -> child.jdbc(jdbc -> jdbc
                    .title("步骤1")
                    .variables("username", "ryze")
                    .config(config -> config
                            .datasource("jdbc_source")
                            .sql("select * from t_001  where tick = \"jdbc_preprocessor\";")
                    )
                    .validators(validator -> validator.json(json -> json
                            .field("$.name")
                            .expected("jdbc_preprocessor")
                    ))
            ));

            suite.children(child -> child.jdbc(jdbc -> jdbc
                    .title("步骤2")
                    .config(config -> config
                            .datasource("jdbc_source")
                            .sql("update t_001  set name = \"步骤2:jdbcSampler\" where tick = \"jdbc_preprocessor\";")
                    )
            ));

            suite.children(child -> child.jdbc(jdbc -> jdbc
                    .title("步骤3")
                    .config(config -> config
                            .datasource("jdbc_source")
                            .sql("select * from t_001  where tick = \"jdbc_preprocessor\";")
                    )
                    .validators(validator -> validator.json(json -> json
                            .field("$.name")
                            .expected("步骤2:jdbcSampler")
                    ))
            ));
        });
    }
}
```

## 🐦 Groovy API 示例

### 基础数据库操作

```groovy
import io.github.xiaomisum.ryze.MagicBox
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyJdbcExample {

    @Test
    @RyzeTest
    void testJdbcInsert() {
        // 简单的插入操作
        MagicBox.jdbc({
            title "插入用户：tick = jdbc_preprocessor"
            configureElements {
                jdbc {
                    refName "jdbc_source"
                    config {
                        username "root"
                        password "123456qq!"
                        url "jdbc:mysql://127.0.0.1:3306/ryze-test?characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false"
                    }
                }
            }
            config {
                datasource "jdbc_source"
                sql "insert into t_001 (tick, name) values (\"http_sampler\", \"ryze_http_sampler\");"
            }
        })
    }

    @Test
    @RyzeTest
    void testJdbcQuery() {
        // 查询操作
        MagicBox.jdbc({
            title "查找用户：tick = ryze_http_sampler"
            configureElements {
                jdbc {
                    refName "jdbc_source"
                    config {
                        username "root"
                        password "123456qq!"
                        url "jdbc:mysql://127.0.0.1:3306/ryze-test?characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false"
                    }
                }
            }
            postprocessors {
                jdbc {
                    config {
                        datasource "jdbc_source"
                        sql "truncate table t_001;"
                    }
                }
            }
            config {
                datasource "jdbc_source"
                sql "select * from t_001  where tick = \"http_sampler\";"
            }
            assertions {
                json { field "\$.name" expected "ryze_http_sampler" }
            }
        })
    }
}
```

### 完整数据库测试脚本

```groovy
import io.github.xiaomisum.ryze.MagicBox
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyJdbcSuiteExample {

    @Test
    @RyzeTest
    void userDatabaseTestSuite() {
        MagicBox.suite("测试用例", {
            variables("id", 1)
            variables { put("tick", "ryze") }
            variables Map.of("a", 1, "b", 2)

            // 数据源配置
            configureElements {
                jdbc {
                    refName "jdbc_source"
                    config {
                        username "root"
                        password "123456qq!"
                        url "jdbc:mysql://127.0.0.1:3306/ryze-test?characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false"
                    }
                }
            }

            // 前置处理：插入测试数据
            preprocessors {
                jdbc {
                    config {
                        datasource "jdbc_source"
                        sql "insert into t_001 (tick, name) values (\"jdbc_preprocessor\", \"jdbc_preprocessor\");"
                    }
                }
            }

            // 后置处理：清理数据
            postprocessors {
                jdbc {
                    config {
                        datasource "jdbc_source"
                        sql "truncate table t_001;"
                    }
                }
            }

            children {
                jdbc {
                    title "步骤1"
                    variables("username", "ryze")
                    config {
                        datasource "jdbc_source"
                        sql "select * from t_001  where tick = \"jdbc_preprocessor\";"
                    }
                    validators {
                        json { field "\$.name" expected "jdbc_preprocessor" }
                    }
                }
            }

            children {
                jdbc {
                    title "步骤2"
                    config {
                        datasource "jdbc_source"
                        sql "update t_001  set name = \"步骤2:jdbcSampler\" where tick = \"jdbc_preprocessor\";"
                    }
                }
            }

            children {
                jdbc {
                    title "步骤3"
                    variables("username", "ryze")
                    config {
                        datasource "jdbc_source"
                        sql "select * from t_001  where tick = \"jdbc_preprocessor\";"
                    }
                    validators {
                        json { field "\$.name" expected "步骤2:jdbcSampler" }
                    }
                }
            }
        })
    }
}
```

## 💡 高级特性

### 事务处理

```java

@Test
@RyzeTest
public void transactionTest() {
    MagicBox.suite("数据库事务测试", suite -> {
        suite.children(child -> {
            // 开启事务
            child.jdbc(jdbc -> jdbc
                    .title("开启事务")
                    .config(config -> config
                            .datasource("jdbc_source")
                            .sql("START TRANSACTION")
                    )
            );

            // 执行多个操作
            child.jdbc(jdbc -> jdbc
                    .title("插入操作1")
                    .config(config -> config
                            .datasource("jdbc_source")
                            .sql("INSERT INTO users (name, email) VALUES (?, ?)")
                            .args("事务用户1", "tx1@test.com")
                    )
            );

            child.jdbc(jdbc -> jdbc
                    .title("插入操作2")
                    .config(config -> config
                            .datasource("jdbc_source")
                            .sql("INSERT INTO users (name, email) VALUES (?, ?)")
                            .args("事务用户2", "tx2@test.com")
                    )
            );

            // 提交事务
            child.jdbc(jdbc -> jdbc
                    .title("提交事务")
                    .config(config -> config
                            .datasource("jdbc_source")
                            .sql("COMMIT")
                    )
            );
        });
    });
}
```

### 参数化查询

```java

@Test
@RyzeTest
public void parameterizedQuery() {
    MagicBox.jdbc(jdbc -> {
        jdbc.title("参数化查询用户");
        jdbc.variables("userId", 1);
        jdbc.variables("status", "active");
        jdbc.config(config -> config
                .datasource("jdbc_source")
                .sql("SELECT * FROM users WHERE id = ? AND status = ?")
                .args("${userId}", "${status}")
        );
        jdbc.assertions(assertions -> assertions
                .json("$[0].id", "${userId}")
                .json("$[0].status", "${status}")
        );
    });
}
```

## ❓ 常见问题

### 支持的数据库

Ryze JDBC 支持所有兼容 JDBC 标准的数据库：

- **MySQL**: `com.mysql.cj.jdbc.Driver`
- **PostgreSQL**: `org.postgresql.Driver`
- **Oracle**: `oracle.jdbc.driver.OracleDriver`
- **SQL Server**: `com.microsoft.sqlserver.jdbc.SQLServerDriver`
- **H2**: `org.h2.Driver`
- **SQLite**: `org.sqlite.JDBC`

### 连接池配置

数据源支持连接池配置参数：

- `max_active`: 最大活跃连接数
- `max_idle`: 最大空闲连接数
- `min_idle`: 最小空闲连接数
- `max_wait`: 最大等待时间（毫秒）

### FAQ

1. **如何处理数据库连接超时？**
    - 在数据源配置中设置 `max_wait` 参数控制连接超时时间。

2. **如何使用数据库事务？**
    - 通过 SQL 语句 `START TRANSACTION`、`COMMIT`、`ROLLBACK` 控制事务。

3. **如何处理 SQL 注入问题？**
    - 使用参数化查询，通过 `args` 配置传递参数值。

4. **如何验证查询结果？**
    - 使用 `validators` 和 `assertions` 对查询结果进行 JSON 路径验证。

## 📚 相关文档

- [快速开始指南](../QuickStart.md)
- [变量与函数](../help/变量与函数.md)
- [验证器](../help/验证器.md)
- [提取器](../help/提取器.md)
- [示例项目](../../example/jdbc-example/)

---

**💡 提示**: 更多详细示例请参考 [example/jdbc-example](../../example/jdbc-example/) 目录下的完整示例代码。