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

package io.github.xiaomisum.ryze.protocol.http.assertion;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.http.RealHTTPResponse;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.hc.core5.http.message.BasicHeader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HTTPResponseAssertionTest {

    @Test
    public void testExtractActualValueFromBody() {
        // 准备测试数据


        String json = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\", \"data\":{\"id\":123}}";

        SampleResult result = new DefaultSampleResult("test");

        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));


        // 创建HTTP响应断言实例 - 测试body字段
        HTTPResponseAssertion assertion = HTTPResponseAssertion.builder()
                .field("body")
                .expected("test response")
                .rule("contains")
                .build();

        // 测试提取实际值
        Object actualValue = assertion.extractActualValue(result);
        Assert.assertEquals(actualValue, json);
    }

    @Test
    public void testExtractActualValueFromStatus() {
        // 准备测试数据
        String json = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\", \"data\":{\"id\":123}}";

        SampleResult result = new DefaultSampleResult("test");

        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));


        // 创建HTTP响应断言实例 - 测试status字段
        HTTPResponseAssertion assertion = HTTPResponseAssertion.builder()
                .field("status")
                .expected(200)
                .rule("==")
                .build();

        // 测试提取实际值
        Object actualValue = assertion.extractActualValue(result);
        Assert.assertEquals(actualValue, 200);
    }

    @Test
    public void testExtractActualValueFromHeader() {
        // 准备测试数据
        String json = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\", \"data\":{\"id\":123}}";

        SampleResult result = new DefaultSampleResult("test");

        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));

        // 创建HTTP响应断言实例 - 测试header字段
        HTTPResponseAssertion assertion = HTTPResponseAssertion.builder()
                .field("header[0].Content-Type")
                .expected("application/json")
                .rule("==")
                .build();

        // 测试提取实际值
        Object actualValue = assertion.extractActualValue(result);
        Assert.assertEquals(actualValue, "application/json");
    }

    @Test
    public void testAssertThat() {
        // 准备测试数据
        String json = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\", \"data\":{\"id\":123}}";

        SampleResult result = new DefaultSampleResult("test");

        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));


        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建HTTP响应断言实例
        HTTPResponseAssertion assertion = HTTPResponseAssertion.builder()
                .field("status")
                .expected(200)
                .rule("==")
                .build();

        // 执行断言
        assertion.assertThat(context);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testAssertThat2() {
        // 准备测试数据
        String json = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\", \"data\":{\"id\":123}}";

        SampleResult result = new DefaultSampleResult("test");

        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));


        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建HTTP响应断言实例
        HTTPResponseAssertion assertion = HTTPResponseAssertion.builder()
                .field("status")
                .expected(300)
                .rule("==")
                .build();

        // 执行断言
        assertion.assertThat(context);
    }

}