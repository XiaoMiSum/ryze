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

public class RegexExtractorTest {

    @Test
    public void testExtract() {
        // 准备测试数据
        String response = "用户名：张三，年龄：25岁，邮箱：zhangsan@example.com";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(response.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "text/plain")));

        // 创建正则表达式提取器实例 - 提取用户名
        RegexExtractor extractor = RegexExtractor.builder()
                .field("用户名：(\\S+)，年龄")
                .refName("userName")
                .build();

        // 测试提取用户名
        Object extractedValue = extractor.extract(result);
        Assert.assertEquals(extractedValue, "张三");

        // 测试提取年龄
        extractor.setField("年龄：(\\d+)岁，邮箱");
        Object ageValue = extractor.extract(result);
        Assert.assertEquals(ageValue, "25");

        // 测试提取邮箱
        extractor.setField("邮箱：([\\w.@]+)");
        Object emailValue = extractor.extract(result);
        Assert.assertEquals(emailValue, "zhangsan@example.com");
    }

    @Test
    public void testExtractWithMatchNum1() {
        // 准备测试数据 - 包含多个匹配项
        String response = "价格：100元，折扣价：80元，会员价：60元";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(response.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "text/plain")));

        // 创建正则表达式提取器实例 - 提取价格（默认第一个）
        RegexExtractor extractor = RegexExtractor.builder()
                .field("(\\d+)元")
                .refName("price")
                .build();

        // 测试默认提取第一个匹配项
        Object firstPrice = extractor.extract(result);
        Assert.assertEquals(firstPrice, "100");

    }

    @Test
    public void testExtractWithMatchNum2() {
        // 准备测试数据 - 包含多个匹配项
        String response = "价格：100元，折扣价：80元，会员价：60元";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(response.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "text/plain")));

        // 创建正则表达式提取器实例 - 提取价格（默认第一个）
        RegexExtractor extractor = RegexExtractor.builder()
                .field("(\\d+)元")
                .refName("price")
                .build();

        // 测试提取第二个匹配项
        extractor.setMatchNum(1);
        Object secondPrice = extractor.extract(result);
        Assert.assertEquals(secondPrice, "80");

    }

    @Test
    public void testExtractWithMatchNum3() {
        // 准备测试数据 - 包含多个匹配项
        String response = "价格：100元，折扣价：80元，会员价：60元";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(response.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "text/plain")));

        // 创建正则表达式提取器实例 - 提取价格（默认第一个）
        RegexExtractor extractor = RegexExtractor.builder()
                .field("(\\d+)元")
                .refName("price")
                .build();

        // 测试提取第三个匹配项
        extractor.setMatchNum(2);
        Object thirdPrice = extractor.extract(result);
        Assert.assertEquals(thirdPrice, "60");
    }

    @Test
    public void testValidate() {
        // 测试字段为空的情况
        RegexExtractor extractor = RegexExtractor.builder()
                .field("")
                .refName("testRef")
                .build();

        Assert.assertFalse(extractor.validate().isValid());
        Assert.assertTrue(extractor.validate().getReason().contains("提取表达式"));

        // 测试引用名称为空的情况
        extractor.setField("(\\d+)");
        extractor.setRefName("");
        Assert.assertFalse(extractor.validate().isValid());
        Assert.assertTrue(extractor.validate().getReason().contains("提取引用名称"));

        // 测试正常情况
        extractor.setRefName("testRef");
        Assert.assertTrue(extractor.validate().isValid());
    }

    @Test
    public void testProcess() {
        // 准备测试数据
        String response = "认证令牌：token_abc123xyz_结束";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(response.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "text/plain")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建正则表达式提取器实例
        RegexExtractor extractor = RegexExtractor.builder()
                .field("认证令牌：token_(\\w+)_结束")
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
        String response = "";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(response.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "text/plain")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建带默认值的正则表达式提取器实例
        RegexExtractor extractor = RegexExtractor.builder()
                .field("认证令牌：token_(\\w+)_结束")
                .refName("authToken")
                .defaultValue("defaultToken")
                .build();

        // 执行提取过程
        extractor.process(context);

        // 验证默认值是否正确存储在上下文中
        Object tokenValue = context.getLocalVariablesWrapper().get("authToken");
        Assert.assertEquals(tokenValue, "defaultToken");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testProcessWithoutDefaultValueAndEmptyResponse() {
        // 准备测试数据 - 空响应
        String response = "";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(response.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "text/plain")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建不带默认值的正则表达式提取器实例
        RegexExtractor extractor = RegexExtractor.builder()
                .field("认证令牌：token_(\\w+)_结束")
                .refName("authToken")
                .build();

        // 执行提取过程 - 应该抛出异常
        extractor.process(context);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testProcessWithoutDefaultValueAndEmptyResponse2() {
        // 准备测试数据 - 空响应
        String response = "认证令牌3：token_abc123xyz_结束";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(response.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "text/plain")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建不带默认值的正则表达式提取器实例
        RegexExtractor extractor = RegexExtractor.builder()
                .field("认证令牌：token_(\\w+)_结束")
                .refName("authToken")
                .build();

        // 执行提取过程 - 应该抛出异常
        extractor.process(context);
    }
}