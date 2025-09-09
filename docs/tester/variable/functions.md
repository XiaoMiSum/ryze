# 🔧 函数

本文档详细介绍 Ryze 测试框架中内置函数的使用方法。

## ⚡ 函数使用

### 基本概念

当需要动态生成数据时，可以使用函数在测试执行过程中实时计算值。

### 调用语法

**基本语法**：`${函数名(参数1, 参数2, ...)}`

**命名参数**：`${函数名(参数名=值, 参数名=值)}`

```yaml
testclass: http
variables:
  # 使用函数生成随机字符串
  random_id: ${random_string(10)}
  # 使用命名参数
  timestamp: ${timestamp('yyyy-MM-dd HH:mm:ss')}
config:
  body:
    id: ${random_id}
    created_time: ${timestamp}
    # 直接在配置中使用函数
    token: ${random_string(32, '0123456789ABCDEF')}
```

### 使用建议

- 函数参数使用单引号包围字符串值（Freemarker 模板引擎要求）
- 复杂参数建议使用命名参数形式，提高可读性
- 函数执行顺序：先执行函数生成变量值，再进行变量替换

## 📚 内置函数列表

Ryze 提供了丰富的内置函数来满足常见的测试数据生成需求：

### 🔐 安全加密函数

#### digest

生成信息摘要，支持多种散列算法。

**参数**：

| 参数          | 类型      | 默认值   | 说明                      |
|-------------|---------|-------|-------------------------|
| `algorithm` | String  | md5   | 算法类型（md5、sha1、sha256 等） |
| `content`   | String  | 无     | 待加密的原始内容（必填）            |
| `salt`      | String  | 无     | 盐值，用于增强安全性              |
| `upper`     | Boolean | false | 是否转为大写                  |

**使用示例**：

```yaml
variables:
  # 简单 MD5 加密（如果只传一个参数，算法默认为md5）
  simple_md5: ${digest('hello world')}
  # 指定算法的加密
  sha1_hash: ${digest('md5', 'password')}
  # 带盐值的 SHA256 加密
  secure_hash: ${digest('sha256', 'password', 'mysalt', true)}
```

#### google2fa

生成谷歌身份验证器的动态验证码。

**参数**：

| 参数       | 类型     | 说明           |
|----------|--------|--------------|
| `secret` | String | 谷歌验证器安全码（必填） |

**使用示例**：

```yaml
variables:
  auth_code: ${google2fa('JBSWY3DPEHPK3PXP')}
```

### 📊 数据生成函数

#### random

生成随机数。

**参数**：

| 参数      | 类型      | 默认值 | 说明          |
|---------|---------|-----|-------------|
| `bound` | Integer | 无限制 | 随机数的上限（不包含） |

**使用示例**：

```yaml
variables:
  # 0-99 的随机数
  random_num: ${random(100)}
  # 任意随机数（可能为负数）
  any_random: ${random()}
```

#### random_string

生成随机字符串。

**参数**：

| 参数       | 类型      | 默认值   | 说明          |
|----------|---------|-------|-------------|
| `length` | Integer | 10    | 随机字符串长度     |
| `string` | String  | 字母    | 生成随机字符串的字符集 |
| `upper`  | Boolean | false | 是否转为大写      |

**使用示例**：

```yaml
variables:
  # 默认随机字符串（长度10的字母字符串）
  simple_string: ${random_string()}
  # 自定义长度
  length_string: ${random_string(5)}
  # 自定义长度和字符集
  custom_string: ${random_string(8, '0123456789ABCDEF')}
  # 大写随机字符串
  upper_string: ${random_string(6, '', true)}
```

#### faker

基于Faker库生成各种类型的模拟数据。

**参数**：

| 参数       | 类型     | 默认值   | 说明                             |
|----------|--------|-------|--------------------------------|
| `key`    | String | 无     | 数据类型，格式为"ClassName.Method"（必填） |
| `locale` | String | zh-CN | 语言区域设置                         |

**使用示例**：

```yaml
variables:
  # 生成中文姓名
  chinese_name: ${faker('name.fullName')}
  # 生成英文城市名
  english_city: ${faker('address.city', 'en-US')}
  # 生成手机号码
  phone_number: ${faker('phoneNumber.cellPhone')}
```

#### uuid

生成标准 UUID。

**参数**：无

**使用示例**：

```yaml
variables:
  unique_id: ${uuid()}
```

### 🕰️ 时间日期函数

#### timestamp

获取当前时间或格式化时间。

**参数**：

| 参数       | 类型     | 默认值      | 说明       |
|----------|--------|----------|----------|
| `format` | String | 无（返回时间戳） | 日期格式化字符串 |

**使用示例**：

```yaml
variables:
  # 时间戳
  current_timestamp: ${timestamp()}
  # 格式化日期
  current_date: ${timestamp('yyyy-MM-dd')}
  # 完整日期时间
  full_datetime: ${timestamp('yyyy-MM-dd HH:mm:ss')}
```

#### time_shift

对时间进行偏移操作。

**参数**：

| 参数       | 类型     | 默认值      | 说明       |
|----------|--------|----------|----------|
| `format` | String | 无（返回时间戳） | 日期格式化字符串 |
| `amount` | String | 无（不偏移）   | 时间偏移量    |

**偏移量格式说明**：

- `PT20.345S`：20.345秒
- `PT15M`：15分钟
- `PT10H`：10小时
- `P2D`：2天
- `P2DT3H4M`：2天3小时4分钟
- `P-6H3M`：-6小时+3分钟
- `-P6H3M`：-6小时-3分钟

**使用示例**：

```yaml
variables:
  # 当前时间
  now: ${time_shift()}
  # 明天的日期
  tomorrow: ${time_shift('yyyy-MM-dd', 'P1D')}
  # 一小时前
  hour_ago: ${time_shift('yyyy-MM-dd HH:mm:ss', '-PT1H')}
```

### 🔗 数据处理函数

#### json

将传入的参数转换为 JSON 对象。

**参数**：多个 "key=value" 格式的键值对

**使用示例**：

```yaml
variables:
  user_data: ${json('name=张三', 'age=25', 'email=zhangsan@example.com')}
```

#### json_read

通过 JSONPath 读取 JSON 数据。

**参数**：

| 参数     | 类型     | 说明               |
|--------|--------|------------------|
| `json` | Object | JSON 对象（必填）      |
| `path` | String | JSONPath 表达式（必填） |

**使用示例**：

```yaml
variables:
  user_name: ${json_read(response_data, '$.user.name')}
  user_age: ${json_read(user_data, '$.age')}
```

#### url_encode

对字符串进行 URL 编码。

**参数**：

| 参数        | 类型     | 说明          |
|-----------|--------|-------------|
| `content` | String | 待编码的字符串（必填） |

**使用示例**：

```yaml
variables:
  encoded_param: ${url_encode('测试参数 with space')}
```

#### url_decode

对字符串进行 URL 解码。

**参数**：

| 参数        | 类型     | 说明          |
|-----------|--------|-------------|
| `content` | String | 待解码的字符串（必填） |

**使用示例**：

```yaml
variables:
  decoded_param: ${url_decode('%E6%B5%8B%E8%AF%95%E5%8F%82%E6%95%B0')}
```

### 🎭 模拟数据函数

#### faker

使用 JavaFaker 库生成各种类型的模拟数据。

**参数**：

| 参数       | 类型     | 默认值   | 说明                                |
|----------|--------|-------|-----------------------------------|
| `key`    | String | 无     | 数据类型，格式为 ClassName.MethodName（必填） |
| `locale` | String | zh-CN | 本地化设置                             |

**常用数据类型**：

- `name.fullName`：完整姓名
- `name.firstName`：名字
- `name.lastName`：姓氏
- `internet.emailAddress`：邮箱地址
- `phoneNumber.cellPhone`：手机号码
- `address.fullAddress`：完整地址
- `company.name`：公司名称
- `lorem.sentence`：随机句子

**使用示例**：

```yaml
variables:
  # 生成中文姓名
  user_name: ${faker('name.fullName', 'zh-CN')}
  # 生成邮箱地址
  email: ${faker('internet.emailAddress')}
  # 生成手机号码
  phone: ${faker('phoneNumber.cellPhone')}
  # 生成公司名称
  company: ${faker('company.name')}
```

**详细参考**：[JavaFaker API 文档](http://dius.github.io/java-faker/apidocs/index.html)

## 💡 扩展功能

当内置函数无法满足特定需求时，Ryze 框架支持自定义函数扩展。详细的开发指南请参考：

- **开发文档**：[Development.md](../Development.md) - 完整的开发指南和最佳实践
- **代码示例**
  ：查看框架源码中的[内置函数实现](../../ryze/src/main/resources/META-INF/services/io.github.xiaomisum.ryze.function.Function)

**💡 提示**：变量和函数是 Ryze 框架中实现动态测试的核心功能，合理使用可以大大提高测试的灵活性和可维护性！