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

import io.github.xiaomisum.ryze.assertion.AbstractAssertion;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;

/**
 * 结果断言类，用于验证整个响应结果
 *
 * <p>该类继承自AbstractAssertion，用于对整个响应结果进行验证，
 * 而不是特定字段。它将响应的完整内容作为实际值与期望值进行比较。</p>
 *
 * <p>使用示例：
 * <pre>
 * {
 *   "testclass": "result"
 *   "expected": "success",
 *   "rule": "contains"
 * }
 * </pre>
 * </p>
 *
 * @author xiaomi
 * @see AbstractAssertion 抽象断言类
 */
@KW({"ResultAssertion", "Result_Assertion", "result"})
public class ResultAssertion extends AbstractAssertion {

    /**
     * 创建结果断言构建器
     *
     * @return 结果断言构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 初始化断言期望值，将响应的完整内容作为实际值
     *
     * @param result 取样结果对象
     * @return 断言期望值对象
     */
    @Override
    protected Object extractActualValue(SampleResult result) {
        return result.getResponse().bytesAsString();
    }

    /**
     * 结果断言构建器类
     */
    public static class Builder extends AbstractAssertion.Builder<Builder, ResultAssertion> {

        /**
         * 构造函数，创建结果断言构建器
         */
        public Builder() {
            super(new ResultAssertion());
        }
    }
}