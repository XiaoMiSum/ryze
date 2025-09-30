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

package io.github.xiaomisum.ryze.assertion.builtin;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.http.RealHTTPResponse;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.hc.core5.http.message.BasicHeader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JSONAssertionTest {

    @Test
    public void testExtractActualValue() {
        // 准备测试数据
        String json = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\", \"data\":{\"id\":123}}";

        SampleResult result = new DefaultSampleResult("test");

        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));

        // 创建JSON断言实例
        JSONAssertion assertion = JSONAssertion.builder()
                .field("$.name")
                .expected("John")
                .rule("==")
                .build();

        // 测试提取实际值
        Object actualValue = assertion.extractActualValue(result);
        Assert.assertEquals(actualValue, "John");

        // 测试嵌套字段
        assertion.setField("$.data.id");
        Object nestedValue = assertion.extractActualValue(result);
        Assert.assertEquals(nestedValue, 123);
    }

    @Test
    public void testValidate() {
        // 测试字段为空的情况
        JSONAssertion assertion = JSONAssertion.builder()
                .field("")
                .expected("test")
                .build();

        Assert.assertFalse(assertion.validate().isValid());
        Assert.assertTrue(assertion.validate().getReason().contains("字段值缺失或为空"));

        // 测试字段不为空的情况
        assertion.setField("$.name");
        Assert.assertTrue(assertion.validate().isValid());
    }

    @Test
    public void testAssertThat() {
        // 准备测试数据
        String json = "{\"status\":\"success\", \"code\":200}";

        SampleResult result = new DefaultSampleResult("test");

        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());


        // 创建JSON断言实例
        JSONAssertion assertion = JSONAssertion.builder()
                .field("$.status")
                .expected("success")
                .rule("==")
                .build();

        // 执行断言
        assertion.assertThat(context);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testAssertThat2() {
        // 准备测试数据
        String json = "{\"status\":\"success\", \"code\":200}";

        SampleResult result = new DefaultSampleResult("test");

        result.setResponse(new RealHTTPResponse(json.getBytes(), 200, "HTTP/1.1", "OK", new BasicHeader("Content-Type", "application/json")));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建JSON断言实例
        JSONAssertion assertion = JSONAssertion.builder()
                .field("$.status")
                .expected("success")
                .rule("!=")
                .build();

        // 执行断言

        assertion.assertThat(context);


    }
}