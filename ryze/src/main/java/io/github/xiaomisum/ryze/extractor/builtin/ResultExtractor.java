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

import io.github.xiaomisum.ryze.extractor.AbstractExtractor;
import io.github.xiaomisum.ryze.extractor.ExtractResult;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.support.ValidateResult;
import org.apache.commons.lang3.StringUtils;

/**
 * 结果提取器，用于提取完整的响应结果内容
 *
 * <p>该提取器用于将整个响应结果（如HTTP响应体）提取为一个变量，
 * 便于后续处理或验证。与JSON提取器和正则表达式提取器不同，
 * 结果提取器不需要指定提取表达式[field]，而是提取完整的响应内容。</p>
 *
 * <p>使用场景示例：
 * <ul>
 *   <li>需要对整个响应体进行后续处理</li>
 *   <li>需要将响应体传递给自定义函数处理</li>
 *   <li>需要保存完整的响应内容用于调试</li>
 * </ul></p>
 *
 * @author xiaomi
 */
@KW({"ResultExtractor", "result_extractor", "result"})
public class ResultExtractor extends AbstractExtractor {

    /**
     * 创建一个新的结果提取器构建器
     *
     * @return 结果提取器构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 执行完整结果提取
     *
     * <p>将响应体的完整内容提取为字符串并存储到指定变量中。</p>
     *
     * @param result 取样结果，包含待提取的响应数据
     * @return 提取结果对象
     */
    @Override
    protected ExtractResult extract(SampleResult result) {
        var res = new ExtractResult("Result 提取");
        res.setValue(result.getResponse().bytesAsString());
        return res;
    }

    /**
     * 验证提取器配置的有效性
     *
     * <p>与基类的验证逻辑不同，结果提取器只验证引用名称[refName]是否为空，
     * 因为它不需要提取表达式[field]。</p>
     *
     * @return 验证结果，包含所有验证错误信息
     */
    @Override
    public ValidateResult validate() {
        ValidateResult result = new ValidateResult();
        if (StringUtils.isBlank(refName)) {
            result.append("\n提取变量引用名称 %s 字段值缺失或为空，当前值：%s", REF_NAME, toString());
        }
        return result;
    }

    /**
     * 结果提取器构建器，提供链式调用方式创建结果提取器实例
     */
    public static class Builder extends AbstractExtractor.Builder<Builder, ResultExtractor> {

        /**
         * 构造一个新的结果提取器构建器实例
         */
        public Builder() {
            super(new ResultExtractor());
        }
    }
}