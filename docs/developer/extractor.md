# 🔍 提取器开发指南

## 概述

提取器用于从测试结果中提取数据并保存到上下文变量中，供后续测试步骤使用。通过继承 `AbstractExtractor`
类，您可以创建自定义提取器来处理各种数据格式。

## 提取器接口

### 基本接口定义

```java
public interface Extractor extends Validatable {
    /**
     * 提取器执行方法，从取样结果中提取数据并保存到变量中
     * @param context 测试上下文，包含测试结果和变量信息
     */
    void process(ContextWrapper context);
}
```

### 抽象基类

```java
public abstract class AbstractExtractor implements Extractor, ExtractorConstantsInterface {

    protected String field;        // 提取表达式
    protected String refName;      // 变量名称
    protected Object defaultValue; // 默认值
    protected int matchNum = 0;    // 匹配序号

    /**
     * 执行具体的提取逻辑，由子类实现
     */
    protected abstract Object extract(SampleResult context);
}
```

## 提取器开发实例

### 1. 基础提取器示例

#### CSV 提取器

```java

@KW({"csv_extractor", "csv"})
public class CSVExtractor extends AbstractExtractor {

    private int columnIndex = 0;
    private int rowIndex = 0;
    private final String delimiter = ",";

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected Object extract(SampleResult result) {
        try {
            String csvContent = result.getResponse().bytesAsString();
            String[] lines = csvContent.split("\n");

            if (rowIndex > lines.length) {
                throw new RuntimeException("行索引超出范围: " + rowIndex);
            }

            String targetLine = lines[rowIndex + 1]; // 跳过标题行
            String[] columns = targetLine.split(delimiter);

            if (columnIndex >= columns.length) {
                throw new RuntimeException("列索引超出范围: " + columnIndex);
            }

            return columns[columnIndex].trim();

        } catch (Exception e) {
            if (e instanceof RuntimeException r) {
                throw r;
            }
            throw new RuntimeException("CSV 解析失败 ", e);
        }
    }

    @Override
    public ValidateResult validate() {
        ValidateResult result = new ValidateResult();
        if (columnIndex < 0) result.append("列索引不能为负数");
        if (rowIndex < 0) result.append("行索引不能为负数");
        if (StringUtils.isBlank(delimiter)) result.append("分隔符不能为空");
        result.append(super.validate());
        return result;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public static class Builder extends AbstractExtractor.Builder<Builder, CSVExtractor> {

        public Builder() {
            super(new CSVExtractor());
        }

        public Builder columnIndex(int columnIndex) {
            extractor.columnIndex = columnIndex;
            return self;
        }

        public Builder rowIndex(int rowIndex) {
            extractor.rowIndex = rowIndex;
            return self;
        }
    }
}
```

#### XPath 提取器

```java

@KW({"xpath_extractor", "xpath", "xml_extractor"})
public class XPathExtractor extends AbstractExtractor {

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected Object extract(SampleResult result) {
        try {
            String xmlContent = result.getResponse().bytesAsString();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expression = xpath.compile(field);

            return expression.evaluate(document);
        } catch (Exception e) {
            if (e instanceof RuntimeException r) {
                throw r;
            }
            throw new RuntimeException("XML 解析失败 ", e);
        }
    }

    public static class Builder extends AbstractExtractor.Builder<Builder, XPathExtractor> {

        public Builder() {
            super(new XPathExtractor());
        }
    }
}
```

### 2. 高级提取器示例

#### 多格式提取器

```java

@KW({"multi_format_extractor", "multi"})
public class MultiFormatExtractor extends AbstractExtractor {

    private String format = "json"; // json, xml, yaml, csv, regex

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected Object extract(SampleResult result) {
        try {
            String content = result.getResponse().bytesAsString();
            return value = switch (format.toLowerCase()) {
                case "json" -> extractFromJson(content, field);
                case "xml" -> extractFromXml(content, field);
                case "yaml", "yml" -> extractFromYaml(content, field);
                case "csv" -> extractFromCsv(content, field);
                case "regex" -> extractFromRegex(content, field);
                default -> throw new IllegalArgumentException("不支持的格式: " + format);
            };
        } catch (Exception e) {
            if (e instanceof RuntimeException r) {
                throw r;
            }
            throw new RuntimeException(format + " 提取失败 ", e);
        }
    }

    public void setFormat(String format) {
        this.format = format;
    }

    private Object extractFromJson(String content, String path) {
        return JSONPath.extract(content, path);
    }

    private Object extractFromXml(String content, String xpath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(content.getBytes()));

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpathObj = xPathFactory.newXPath();
        return xpathObj.compile(xpath).evaluate(document);
    }

    private Object extractFromYaml(String content, String path) {
        Yaml yaml = new Yaml();
        Object data = yaml.load(content);
        return JSONPath.extract(JSON.toJSONString(data), path);
    }

    private Object extractFromCsv(String content, String columnName) {
        String[] lines = content.split("\n");
        if (lines.length < 2) return null;

        String[] headers = lines[0].split(",");
        int columnIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equals(columnName)) {
                columnIndex = i;
                break;
            }
        }

        if (columnIndex == -1) return null;

        List<String> values = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String[] columns = lines[i].split(",");
            if (columnIndex < columns.length) {
                values.add(columns[columnIndex].trim());
            }
        }

        return values.size() == 1 ? values.get(0) : values;
    }

    private Object extractFromRegex(String content, String pattern) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(content);

        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.groupCount() > 0) {
                matches.add(matcher.group(1)); // 返回第一个捕获组
            } else {
                matches.add(matcher.group()); // 返回整个匹配
            }
        }

        return matches.size() == 1 ? matches.get(0) : matches;
    }

    public static class Builder extends AbstractExtractor.Builder<Builder, MultiFormatExtractor> {

        public Builder() {
            super(new MultiFormatExtractor());
        }

        public Builder format(String format) {
            extractor.format = format;
            return self;
        }
    }
}
```

## 提取器注册

### SPI 注册

创建文件 `src/main/resources/META-INF/services/io.github.xiaomisum.ryze.extractor.Extractor`：

```
com.example.CSVExtractor
com.example.XPathExtractor
com.example.MultiFormatExtractor
```

## 提取器使用

### 在 Java API 中使用

```java

@Test
@RyzeTest
public void testWithExtractors() {
    Ryze.http("提取器测试", http -> {
        http.config(config -> config
                .method("GET")
                .url("https://api.example.com/data")
        );

        http.extractors(extractors ->
                extractors.csv(csv -> csv
                                .columnIndex(0)
                                .rowIndex(0)
                                .refName("csvValue")
                        )
                        .xpath(xpath -> xpath
                                .field("//data/id")
                                .refName("xmlId")
                        )
        );
    });
}
```

### 在 YAML 中使用

```yaml
title: 数据提取测试
testclass: http
config:
  method: GET
  url: "https://api.example.com/data"

extractors:
  - testclass: csv
    column_index: 0
    row_index: 0
    ref_name: "csvValue"
  - testclass: xpath
    field: "//data/id"
    ref_name: "xmlId"
```

## 开发规范

1. **继承基类**：继承 `AbstractExtractor` 类
2. **实现验证**：重写 `validate()` 方法
3. **异常处理**：提供清晰的错误信息
4. **性能优化**：避免重复解析大数据
5. **线程安全**：确保多线程环境安全

## 最佳实践

1. **数据类型**：合理选择返回数据类型
2. **默认值**：支持默认值机制
3. **错误处理**：完善的异常处理
4. **性能考虑**：优化大数据处理
5. **文档完整**：提供使用示例

通过遵循这些指导原则，您可以开发出高效、可靠的数据提取器。