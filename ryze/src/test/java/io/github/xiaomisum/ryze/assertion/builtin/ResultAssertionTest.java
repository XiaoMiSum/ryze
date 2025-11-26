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
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ResultAssertionTest {

    @Test
    public void testExtractActualValue() {
        // 准备测试数据
        String responseText = "This is a test response content";

        // 创建模拟的SampleResult
        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(SampleResult.DefaultRealResponse.build(responseText.getBytes()));

        // 创建Result断言实例
        ResultAssertion assertion = ResultAssertion.builder()
                .expected("test response")
                .rule("contains")
                .build();

        // 测试提取实际值
        Object actualValue = assertion.extractActualValue(result);
        Assert.assertEquals(actualValue, responseText);
    }

    @Test
    public void testAssertThat() {
        // 准备测试数据
        String responseText = "Status: SUCCESS, Code: 200";

        // 创建模拟的SampleResult
        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(SampleResult.DefaultRealResponse.build(responseText.getBytes()));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建Result断言实例
        ResultAssertion assertion = ResultAssertion.builder()
                .expected(responseText)
                .rule("==")
                .build();

        // 执行断言

        assertion.assertThat(context); // 注意：这里直接使用context而不是wrap(result)

    }

    @Test(expectedExceptions = AssertionError.class)
    public void testAssertThat2() {
        // 准备测试数据
        String responseText = "Status: SUCCESS, Code: 200";

        // 创建模拟的SampleResult
        SampleResult result = new DefaultSampleResult("test");
        result.setResponse(SampleResult.DefaultRealResponse.build(responseText.getBytes()));

        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.setTestResult(result);

        // 创建Result断言实例
        ResultAssertion assertion = ResultAssertion.builder()
                .expected("SUCCESS")
                .rule("==")
                .build();

        // 执行断言

        assertion.assertThat(context); // 注意：这里直接使用context而不是wrap(result)

    }
}