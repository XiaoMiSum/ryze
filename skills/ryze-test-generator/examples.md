# Ryze测试脚本生成示例

## 示例1: 用户登录接口 - 完整测试用例集

### 接口信息

```
POST /api/auth/login
请求体: {username: string, password: string}
响应: {code: int, data: {token: string, user_id: int}, message: string}
```

### 生成的测试用例（8个）

#### 1. 正常场景

```yaml
# user/login-happy-path.yaml
title: 用户登录_正常场景
variables:
  username: admin
  password: admin123
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
testclass: http_sampler
config:
  method: post
  path: /auth/login
  body:
    username: ${username}
    password: ${password}
extractors:
  - { testclass: json, field: '$.data.token', ref_name: auth_token }
  - { testclass: json, field: '$.data.user_id', ref_name: user_id }
validators:
  - { testclass: http_assertion, field: status, expected: 200, rule: == }
  - { testclass: json_assertion, field: '$.code', expected: 200, rule: == }
  - { testclass: json_assertion, field: '$.data.token', expected: '', rule: isNotEmpty }
  - { testclass: json_assertion, field: '$.data.user_id', expected: '', rule: isNotEmpty }
```

#### 2. 异常场景

```yaml
# user/login-error-no-username.yaml
title: 用户登录_缺少用户名
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
testclass: http_sampler
config:
  method: post
  path: /auth/login
  body:
    password: admin123
validators:
  - { testclass: http_assertion, field: status, expected: 400, rule: == }
  - { testclass: json_assertion, field: '$.code', expected: 400, rule: == }
```

```yaml
# user/login-error-no-password.yaml
title: 用户登录_缺少密码
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
testclass: http_sampler
config:
  method: post
  path: /auth/login
  body:
    username: admin
validators:
  - { testclass: http_assertion, field: status, expected: 400, rule: == }
  - { testclass: json_assertion, field: '$.code', expected: 400, rule: == }
```

```yaml
# user/login-error-wrong-password.yaml
title: 用户登录_密码错误
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
testclass: http_sampler
config:
  method: post
  path: /auth/login
  body:
    username: admin
    password: wrong_password
validators:
  - { testclass: http_assertion, field: status, expected: 401, rule: == }
  - { testclass: json_assertion, field: '$.code', expected: 401, rule: == }
```

```yaml
# user/login-error-account-locked.yaml
title: 用户登录_账号被锁定
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
testclass: http_sampler
config:
  method: post
  path: /auth/login
  body:
    username: locked_user
    password: any_password
validators:
  - { testclass: http_assertion, field: status, expected: 403, rule: == }
  - { testclass: json_assertion, field: '$.code', expected: 403, rule: == }
```

#### 3. 边界值

```yaml
# user/login-boundary-max-username-length.yaml
title: 用户登录_用户名最大长度
variables:
  max_username: "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
testclass: http_sampler
config:
  method: post
  path: /auth/login
  body:
    username: ${max_username}
    password: admin123
validators:
  - { testclass: http_assertion, field: status, expected: 200, rule: == }
```

```yaml
# user/login-boundary-empty-username.yaml
title: 用户登录_空用户名
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
testclass: http_sampler
config:
  method: post
  path: /auth/login
  body:
    username: ""
    password: admin123
validators:
  - { testclass: http_assertion, field: status, expected: 400, rule: == }
```

#### 4. 等价类

```yaml
# user/login-equivalence-valid-email.yaml
title: 用户登录_邮箱格式用户名
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
testclass: http_sampler
config:
  method: post
  path: /auth/login
  body:
    username: "user@example.com"
    password: admin123
validators:
  - { testclass: http_assertion, field: status, expected: 200, rule: == }
```

```yaml
# user/login-equivalence-invalid-special-chars.yaml
title: 用户登录_含特殊字符用户名
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
testclass: http_sampler
config:
  method: post
  path: /auth/login
  body:
    username: "invalid<user>@name!"
    password: admin123
validators:
  - { testclass: http_assertion, field: status, expected: 400, rule: == }
```

### 输入的OpenAPI片段

```yaml
paths:
  /auth/login:
    post:
      summary: 用户登录
      tags: [认证]
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                username:
                  type: string
                password:
                  type: string
      responses:
        '200':
          description: 登录成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                  data:
                    type: object
                    properties:
                      token:
                        type: string
                      user_id:
                        type: integer
                  message:
                    type: string

  /users:
    post:
      summary: 创建用户
      tags: [用户管理]
      security:
        - bearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                name:
                  type: string
                email:
                  type: string
                age:
                  type: integer
      responses:
        '200':
          description: 创建成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                  data:
                    type: object
                    properties:
                      id:
                        type: integer
                      name:
                        type: string
                      email:
                        type: string

  /users/{userId}:
    get:
      summary: 获取用户详情
      tags: [用户管理]
      security:
        - bearerAuth: []
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                  data:
                    type: object
                    properties:
                      id:
                        type: integer
                      name:
                        type: string
                      email:
                        type: string
                      age:
                        type: integer
```

### 生成的测试脚本

```yaml
title: 用户管理模块
variables:
  base_url: http://localhost:8080
  auth_token: ""
  test_user_id: ""
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
      headers:
        Content-Type: application/json
        Authorization: Bearer ${auth_token}
children:
  - title: 用户登录
    testclass: http_sampler
    config:
      method: post
      path: /auth/login
      body:
        username: admin
        password: admin123
    extractors:
      - { testclass: json, field: '$.data.token', ref_name: auth_token }
      - { testclass: json, field: '$.data.user_id', ref_name: test_user_id }
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.code', expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.token', expected: '', rule: isNotEmpty }
      - { testclass: json_assertion, field: '$.message', expected: 'success', rule: == }

  - title: 创建用户
    testclass: http_sampler
    config:
      method: post
      path: /users
      body:
        name: 测试用户
        email: test@example.com
        age: 25
    extractors:
      - { testclass: json, field: '$.data.id', ref_name: new_user_id }
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.code', expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.name', expected: '测试用户', rule: == }
      - { testclass: json_assertion, field: '$.data.email', expected: 'test@example.com', rule: == }

  - title: 获取用户详情
    testclass: http_sampler
    config:
      method: get
      path: /users/${new_user_id}
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.code', expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.id', expected: '${new_user_id}', rule: == }
      - { testclass: json_assertion, field: '$.data.name', expected: '', rule: isNotEmpty }
      - { testclass: json_assertion, field: '$.data.email', expected: '', rule: isNotEmpty }
```

## 示例2: 商品管理CRUD测试

### 生成的完整测试套件

```yaml
title: 商品管理模块
variables:
  base_url: http://localhost:8080
  auth_token: ""
  product_id: ""
  category_id: 1
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
      headers:
        Content-Type: application/json
        Authorization: Bearer ${auth_token}
children:
  - title: 管理员登录
    testclass: http_sampler
    config:
      method: post
      path: /admin/login
      body:
        username: admin
        password: admin123
    extractors:
      - { testclass: json, field: '$.data.token', ref_name: auth_token }
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.code', expected: 200, rule: == }

  - title: 创建商品分类
    testclass: http_sampler
    config:
      method: post
      path: /categories
      body:
        name: 电子产品
        description: 各类电子产品
    extractors:
      - { testclass: json, field: '$.data.id', ref_name: category_id }
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.name', expected: '电子产品', rule: == }

  - title: 创建商品
    testclass: http_sampler
    config:
      method: post
      path: /products
      body:
        name: iPhone 15 Pro
        price: 7999.00
        category_id: ${category_id}
        stock: 100
        description: 最新款iPhone
    extractors:
      - { testclass: json, field: '$.data.id', ref_name: product_id }
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.name', expected: 'iPhone 15 Pro', rule: == }
      - { testclass: json_assertion, field: '$.data.price', expected: 7999.00, rule: == }

  - title: 获取商品详情
    testclass: http_sampler
    config:
      method: get
      path: /products/${product_id}
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.id', expected: '${product_id}', rule: == }
      - { testclass: json_assertion, field: '$.data.name', expected: 'iPhone 15 Pro', rule: == }
      - { testclass: json_assertion, field: '$.data.stock', expected: 100, rule: == }

  - title: 更新商品信息
    testclass: http_sampler
    config:
      method: put
      path: /products/${product_id}
      body:
        name: iPhone 15 Pro Max
        price: 8999.00
        stock: 50
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.name', expected: 'iPhone 15 Pro Max', rule: == }
      - { testclass: json_assertion, field: '$.data.price', expected: 8999.00, rule: == }

  - title: 验证更新结果
    testclass: http_sampler
    config:
      method: get
      path: /products/${product_id}
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.name', expected: 'iPhone 15 Pro Max', rule: == }
      - { testclass: json_assertion, field: '$.data.stock', expected: 50, rule: == }

  - title: 查询商品列表
    testclass: http_sampler
    config:
      method: get
      path: /products
      query:
        category_id: ${category_id}
        page: 1
        size: 10
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.items', expected: '', rule: isNotEmpty }
      - { testclass: json_assertion, field: '$.data.total', expected: 0, rule: '>=' }

  - title: 删除商品
    testclass: http_sampler
    config:
      method: delete
      path: /products/${product_id}
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.code', expected: 200, rule: == }

  - title: 验证删除结果
    testclass: http_sampler
    config:
      method: get
      path: /products/${product_id}
    validators:
      - { testclass: http_assertion, field: status, expected: 404, rule: == }
```

## 示例3: 分页查询测试

```yaml
title: 订单分页查询测试
variables:
  base_url: http://localhost:8080
  auth_token: ""
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
      headers:
        Authorization: Bearer ${auth_token}
children:
  - title: 认证登录
    testclass: http_sampler
    config:
      method: post
      path: /auth/login
      body:
        username: user1
        password: pass123
    extractors:
      - { testclass: json, field: '$.data.token', ref_name: auth_token }
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }

  - title: 第一页查询
    testclass: http_sampler
    config:
      method: get
      path: /orders
      query:
        page: 1
        size: 10
        status: pending
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.items', expected: '', rule: isNotEmpty }
      - { testclass: json_assertion, field: '$.data.page', expected: 1, rule: == }
      - { testclass: json_assertion, field: '$.data.size', expected: 10, rule: == }

  - title: 第二页查询
    testclass: http_sampler
    config:
      method: get
      path: /orders
      query:
        page: 2
        size: 10
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.page', expected: 2, rule: == }

  - title: 空结果查询
    testclass: http_sampler
    config:
      method: get
      path: /orders
      query:
        page: 9999
        size: 10
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.items', expected: '', rule: isEmpty }
      - { testclass: json_assertion, field: '$.data.total', expected: 0, rule: == }
```

## 示例4: 文件上传测试

```yaml
title: 文件上传测试
variables:
  base_url: http://localhost:8080
  auth_token: ""
  uploaded_file_id: ""
configelements:
  - testclass: http
    config:
      base_url: ${base_url}
      headers:
        Authorization: Bearer ${auth_token}
children:
  - title: 用户登录
    testclass: http_sampler
    config:
      method: post
      path: /auth/login
      body:
        username: user1
        password: pass123
    extractors:
      - { testclass: json, field: '$.data.token', ref_name: auth_token }
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }

  - title: 上传图片文件
    testclass: http_sampler
    config:
      method: post
      path: /upload/image
      headers:
        Content-Type: multipart/form-data
      body:
        file: "@test-image.jpg"
        description: 测试图片
    extractors:
      - { testclass: json, field: '$.data.file_id', ref_name: uploaded_file_id }
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
      - { testclass: json_assertion, field: '$.data.file_url', expected: '', rule: isNotEmpty }

  - title: 下载上传的文件
    testclass: http_sampler
    config:
      method: get
      path: /files/${uploaded_file_id}
    validators:
      - { testclass: http_assertion, field: status, expected: 200, rule: == }
```
