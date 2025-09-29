/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * 'Software'), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.xiaomisum.ryze.extractor.builtin;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.http.RealHTTPResponse;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.hc.core5.http.message.BasicHeader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JSONExtractorTest {

    @Test
    public void testExtract() {
        // 准备测试数据
        String json = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\", \"data\":{\"id\":123}}";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));

        // 创建JSON提取器实例
        JSONExtractor extractor = JSONExtractor.builder()
                .field("$.name")
                .refName("userName")
                .build();

        // 测试提取值
        Object extractedValue = extractor.extract(result);
        Assert.assertEquals(extractedValue, "John");

        // 测试嵌套字段提取
        extractor.setField("$.data.id");
        Object nestedValue = extractor.extract(result);
        Assert.assertEquals(nestedValue, 123);
    }

    @Test
    public void testValidate1() {
        // 测试字段为空的情况
        JSONExtractor extractor = JSONExtractor.builder()
                .field("")
                .refName("testRef")
                .build();

        Assert.assertFalse(extractor.validate().isValid());
        Assert.assertTrue(extractor.validate().getReason().contains("提取表达式"));
    }

    @Test
    public void testValidate2() {
        // 测试引用名称为空的情况
        JSONExtractor extractor = JSONExtractor.builder()
                .field("$.name")
                .refName("")
                .build();

        Assert.assertFalse(extractor.validate().isValid());
        Assert.assertTrue(extractor.validate().getReason().contains("提取引用名称"));
    }

    @Test
    public void testProcess() {
        // 准备测试数据
        String json = "{\"token\":\"abc123xyz\", \"userId\":1001}";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建JSON提取器实例
        JSONExtractor extractor = JSONExtractor.builder()
                .field("$.token")
                .refName("authToken")
                .build();

        // 执行提取过程
        extractor.process(context);

        // 验证提取结果是否正确存储在上下文中
        Object tokenValue = context.getLocalVariablesWrapper().get("authToken");
        Assert.assertEquals(tokenValue, "abc123xyz");
    }

    @Test
    public void testProcessWithDefaultValue() {
        // 准备测试数据 - 空响应
        String json = "";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建带默认值的JSON提取器实例
        JSONExtractor extractor = JSONExtractor.builder()
                .field("$.token")
                .refName("authToken")
                .defaultValue("defaultToken")
                .build();

        // 执行提取过程
        extractor.process(context);

        // 验证默认值是否正确存储在上下文中
        Object tokenValue = context.getLocalVariablesWrapper().get("authToken");
        Assert.assertEquals(tokenValue, "defaultToken");
    }

    @Test
    public void testProcessWithDefaultValue2() {
        // 准备测试数据 - 空响应
        String json = "{\"token\":\"abc123xyz\", \"userId\":1001}";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建带默认值的JSON提取器实例
        JSONExtractor extractor = JSONExtractor.builder()
                .field("$.token2")
                .refName("authToken")
                .defaultValue("")
                .build();

        // 执行提取过程
        try {
            extractor.process(context);

        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("目标字符串没有匹配的数据"));
        }

    }

    @Test
    public void testProcessWithDefaultValue3() {
        // 准备测试数据 - 空响应
        String json = "";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建带默认值的JSON提取器实例
        JSONExtractor extractor = JSONExtractor.builder()
                .field("$.token")
                .refName("authToken")
                .defaultValue("")
                .build();

        // 执行提取过程
        try {
            extractor.process(context);

        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("待提取的字符串为 null 或空白"));
        }

    }
}