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

public class ResultExtractorTest {

    @Test
    public void testExtract() {
        // 准备测试数据
        String response = "{\"status\":\"success\",\"message\":\"操作成功\",\"data\":{\"id\":12345}}";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(response.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));

        // 创建结果提取器实例
        ResultExtractor extractor = ResultExtractor.builder()
                .refName("fullResponse")
                .build();

        // 测试提取完整响应
        Object extractedValue = extractor.extract(result);
        Assert.assertEquals(extractedValue, response);
    }

    @Test
    public void testValidate() {
        // 测试引用名称为空的情况
        ResultExtractor extractor = ResultExtractor.builder()
                .refName("")
                .build();

        Assert.assertFalse(extractor.validate().isValid());
        Assert.assertTrue(extractor.validate().getReason().contains("提取变量引用名称"));

        // 测试正常情况
        extractor.setRefName("fullResponse");
        Assert.assertTrue(extractor.validate().isValid());
    }

    @Test
    public void testProcess() {
        // 准备测试数据
        String response = "<html><head><title>测试页面</title></head><body><h1>欢迎访问</h1></body></html>";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(response.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "text/html")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建结果提取器实例
        ResultExtractor extractor = ResultExtractor.builder()
                .refName("htmlResponse")
                .build();

        // 执行提取过程
        extractor.process(context);

        // 验证提取结果是否正确存储在上下文中
        Object responseValue = context.getLocalVariablesWrapper().get("htmlResponse");
        Assert.assertEquals(responseValue, response);
    }

    @Test
    public void testProcessWithDefaultValue() {
        // 准备测试数据 - 空响应
        String response = "";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(response.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "text/plain")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建带默认值的结果提取器实例
        ResultExtractor extractor = ResultExtractor.builder()
                .refName("fullResponse")
                .defaultValue("defaultResponse")
                .build();

        // 执行提取过程
        extractor.process(context);

        // 验证默认值是否正确存储在上下文中
        Object responseValue = context.getLocalVariablesWrapper().get("fullResponse");
        Assert.assertEquals(responseValue, "defaultResponse");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testProcessWithoutDefaultValueAndEmptyResponse() {
        // 准备测试数据 - 空响应
        String response = "";

        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(new RealHTTPResponse(response.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "text/plain")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建不带默认值的结果提取器实例
        ResultExtractor extractor = ResultExtractor.builder()
                .refName("fullResponse")
                .build();

        // 执行提取过程 - 应该抛出异常
        extractor.process(context);
    }
}