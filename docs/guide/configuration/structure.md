# 配置文件结构

Ryze框架支持YAML和JSON两种配置格式来描述测试场景。

## YAML配置结构

```yaml
title: 测试套件标题
variables:
  var1: value1
  var2: value2
configelements:
  - testclass: http
    # 配置元件
preprocessors:
  - testclass: http
postprocessors:
  - testclass: http
children:
  - testclass: http
    title: HTTP请求
    config:
      # 请求配置
    validators:
      - testclass: json
        # 断言配置
    extractors:
      - testclass: json
        # 提取器配置
```

## JSON配置结构

```json
{
  "title": "测试套件标题",
  "variables": {
    "var1": "value1",
    "var2": "value2"
  },
  "configelements": [
    {
      "testclass": "http"
      // 配置元件
    }
  ],
  "preprocessors": [
    {
        "testclass": "http"
    }
  ],
  "postprocessors": [
    {
        "testclass": "http"
    }
  ]
  "children": [
    {
      "testclass": "http",
      "title": "HTTP请求",
      "config": {
        // 请求配置
      },
      "validators": [
        {
          "testclass": "json"
          // 断言配置
        }
      ],
      "extractors": [
        {
          "testclass": "json"
          // 提取器配置
        }
      ]
    }
  ]
}
```

## 配置元素说明

### title
测试元素的标题，用于标识和日志输出。

### variables
变量定义，可在子元素中引用。

### configelements
配置元件列表，为子元素提供默认配置。

### preprocessors
前置处理器列表。

### postprocessors
后置处理器列表。

### children
子测试元素列表，按顺序执行。

### testclass
测试元素类型，决定使用哪种协议或功能。