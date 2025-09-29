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

package io.github.xiaomisum.ryze.protocol.http.extractor;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.http.RealHTTPResponse;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.hc.core5.http.message.BasicHeader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HTTPHeaderExtractorTest {

    @Test
    public void testExtract1() {
        // 准备测试数据
        String responseBody = "Test response body";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(
                responseBody.getBytes(),
                200,
                "HTTP/1.1",
                "OK",
                new BasicHeader("Content-Type", "application/json"),
                new BasicHeader("Set-Cookie", "session_id=abc123"),
                new BasicHeader("Set-Cookie", "user_pref=dark_mode")
        ));

        // 创建HTTP头提取器实例 - 提取第一个Set-Cookie
        HTTPHeaderExtractor extractor = HTTPHeaderExtractor.builder()
                .field("Set-Cookie")
                .refName("sessionId")
                .build();

        // 测试提取值
        Object extractedValue = extractor.extract(result);
        Assert.assertEquals(extractedValue, "session_id=abc123");
    }

    @Test
    public void testExtract2() {
        // 准备测试数据
        String responseBody = "Test response body";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(
                responseBody.getBytes(),
                200,
                "HTTP/1.1",
                "OK",
                new BasicHeader("Content-Type", "application/json"),
                new BasicHeader("Set-Cookie", "session_id=abc123"),
                new BasicHeader("Set-Cookie", "user_pref=dark_mode")
        ));

        // 测试提取指定索引的头字段
        HTTPHeaderExtractor extractor = HTTPHeaderExtractor.builder()
                .field("Set-Cookie")
                .refName("userPref")
                .matchNum(1)
                .build();

        Object extractedValue = extractor.extract(result);
        Assert.assertEquals(extractedValue, "user_pref=dark_mode");
    }

    @Test
    public void testExtractWithNonExistentHeader() {
        // 准备测试数据
        String responseBody = "Test response body";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(
                responseBody.getBytes(),
                200,
                "HTTP/1.1",
                "OK",
                new BasicHeader("Content-Type", "application/json")
        ));

        // 创建HTTP头提取器实例 - 提取不存在的头字段
        HTTPHeaderExtractor extractor = HTTPHeaderExtractor.builder()
                .field("X-Custom-Header")
                .refName("customHeader")
                .build();

        // 测试提取不存在的头字段
        Object extractedValue = extractor.extract(result);
        Assert.assertNull(extractedValue);
    }

    @Test
    public void testExtractWithInvalidMatchNum() {
        // 准备测试数据
        String responseBody = "Test response body";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(
                responseBody.getBytes(),
                200,
                "HTTP/1.1",
                "OK",
                new BasicHeader("Set-Cookie", "session_id=abc123"),
                new BasicHeader("Set-Cookie", "user_pref=dark_mode")
        ));

        // 创建HTTP头提取器实例 - 指定超出范围的索引
        HTTPHeaderExtractor extractor = HTTPHeaderExtractor.builder()
                .field("Set-Cookie")
                .refName("sessionId")
                .matchNum(5) // 超出实际存在的头字段数量
                .build();

        // 测试提取超出范围的头字段索引（应该返回最后一个）
        Object extractedValue = extractor.extract(result);
        Assert.assertEquals(extractedValue, "user_pref=dark_mode");
    }

    @Test
    public void testValidate1() {
        // 测试字段为空的情况
        HTTPHeaderExtractor extractor = HTTPHeaderExtractor.builder()
                .field("")
                .refName("testRef")
                .build();

        Assert.assertFalse(extractor.validate().isValid());
        Assert.assertTrue(extractor.validate().getReason().contains("提取请求头"));
    }

    @Test
    public void testValidate2() {
        // 测试字段为空的情况
        HTTPHeaderExtractor extractor = HTTPHeaderExtractor.builder()
                .field("Content-Type")
                .refName("")
                .build();


        // 测试引用名称为空的情况
        Assert.assertFalse(extractor.validate().isValid());
        Assert.assertTrue(extractor.validate().getReason().contains("提取引用名称"));
    }

    @Test
    public void testProcess() {
        // 准备测试数据
        String responseBody = "Test response body";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(
                responseBody.getBytes(),
                200,
                "HTTP/1.1",
                "OK",
                new BasicHeader("Content-Type", "application/json"),
                new BasicHeader("Authorization", "Bearer abc123xyz")
        ));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建HTTP头提取器实例
        HTTPHeaderExtractor extractor = HTTPHeaderExtractor.builder()
                .field("Authorization")
                .refName("authToken")
                .build();

        // 执行提取过程
        extractor.process(context);

        // 验证提取结果是否正确存储在上下文中
        Object tokenValue = context.getLocalVariablesWrapper().get("authToken");
        Assert.assertEquals(tokenValue, "Bearer abc123xyz");
    }

    @Test
    public void testProcessWithDefaultValue() {
        // 准备测试数据 - 不存在目标头字段
        String responseBody = "Test response body";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(
                responseBody.getBytes(),
                200,
                "HTTP/1.1",
                "OK",
                new BasicHeader("Content-Type", "application/json")
        ));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建带默认值的HTTP头提取器实例
        HTTPHeaderExtractor extractor = HTTPHeaderExtractor.builder()
                .field("X-Custom-Header")
                .refName("customHeader")
                .defaultValue("defaultHeaderValue")
                .build();

        // 执行提取过程
        extractor.process(context);

        // 验证默认值是否正确存储在上下文中
        Object headerValue = context.getLocalVariablesWrapper().get("customHeader");
        Assert.assertEquals(headerValue, "defaultHeaderValue");
    }
}