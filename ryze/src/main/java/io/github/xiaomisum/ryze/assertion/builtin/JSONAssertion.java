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

import com.alibaba.fastjson2.JSONPath;
import io.github.xiaomisum.ryze.assertion.AbstractAssertion;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.support.ValidateResult;
import org.apache.commons.lang3.StringUtils;

/**
 * JSON断言类，用于验证JSON格式响应数据的特定字段
 *
 * <p>该类继承自AbstractAssertion，专门用于处理JSON格式的响应数据。
 * 它使用JSONPath表达式从响应中提取特定字段的值，并与期望值进行比较。</p>
 *
 * <p>使用示例：
 * <pre>
 * {
 *   "testclass": "json"
 *   "field": "$.data.id",
 *   "expected": 123,
 *   "rule": "=="
 * }
 * </pre>
 * </p>
 *
 * @author xiaomi
 * @see AbstractAssertion 抽象断言类
 */
@KW({"JSONAssertion", "json_assertion", "json"})
public class JSONAssertion extends AbstractAssertion {


    /**
     * 创建JSON断言构建器
     *
     * @return JSON断言构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 初始化断言期望值，从JSON响应中提取指定字段的值
     *
     * <p>该方法使用field属性作为JSONPath表达式从响应中提取实际值。</p>
     *
     * @param result 取样结果对象
     * @return 断言期望值对象
     */
    @Override
    protected Object extractActualValue(SampleResult result) {
        var target = result.getResponse().bytesAsString();
        return JSONPath.extract(target, field);
    }

    /**
     * 验证断言配置的有效性
     *
     * <p>该方法检查field字段是否为空，如果为空则返回验证失败结果。</p>
     *
     * @return 验证结果
     */
    @Override
    public ValidateResult validate() {
        ValidateResult result = new ValidateResult();
        if (StringUtils.isBlank(field)) {
            result.append("\n提取表达式 %s 字段值缺失或为空，当前值：%s", field, toString());
        }
        return result;
    }

    /**
     * JSON断言构建器类
     */
    public static class Builder extends AbstractAssertion.Builder<Builder, JSONAssertion> {

        /**
         * 构造函数，创建JSON断言构建器
         */
        public Builder() {
            super(new JSONAssertion());
        }
    }

}