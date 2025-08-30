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

import com.alibaba.fastjson2.JSONPath;
import io.github.xiaomisum.ryze.TestStatus;
import io.github.xiaomisum.ryze.extractor.AbstractExtractor;
import io.github.xiaomisum.ryze.extractor.ExtractResult;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;

/**
 * JSON提取器，用于从JSON格式的响应数据中提取指定字段的值
 *
 * <p>该提取器使用JsonPath表达式从JSON格式的响应体中提取数据。
 * JsonPath是一种专门用于解析JSON文档的查询语言，类似于XPath之于XML。</p>
 *
 * <p>使用示例：
 * <pre>
 * 假设响应体为：{"user":{"name":"John","age":30}}
 *
 * 提取用户名：
 * field = "$.user.name"
 * refName = "userName"
 *
 * 提取年龄：
 * field = "$.user.age"
 * refName = "userAge"
 * </pre></p>
 *
 * <p>支持的JsonPath语法包括：
 * <ul>
 *   <li>$ - 根对象</li>
 *   <li>. - 子节点</li>
 *   <li>[] - 数组索引</li>
 *   <li>* - 通配符</li>
 * </ul></p>
 *
 * @author xiaomi
 * @see JSONPath
 */
@KW({"JSONExtractor", "json_extractor", "json"})
public class JSONExtractor extends AbstractExtractor {

    /**
     * 创建一个新的JSON提取器构建器
     *
     * @return JSON提取器构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 执行JSON数据提取
     *
     * <p>使用FastJSON2的JSONPath工具从响应体中提取数据：
     * <ol>
     *   <li>获取响应体的字符串表示</li>
     *   <li>使用JsonPath表达式提取数据</li>
     *   <li>封装提取结果</li>
     *   <li>处理提取失败情况</li>
     * </ol></p>
     *
     * @param result 取样结果，包含待提取的响应数据
     * @return 提取结果对象
     */
    @Override
    protected ExtractResult extract(SampleResult result) {
        var res = new ExtractResult("JSON 提取: " + field);
        var target = result.getResponse().bytesAsString();
        Object value = JSONPath.extract(target, field);
        res.setValue(value);
        if (value == null) {
            res.setStatus(TestStatus.failed);
            res.setMessage(String.format("目标字符串不存在 JsonPath %s，目标字符串：\n%s", field, target));
        }
        return res;
    }

    /**
     * JSON提取器构建器，提供链式调用方式创建JSON提取器实例
     */
    public static class Builder extends AbstractExtractor.Builder<Builder, JSONExtractor> {

        /**
         * 构造一个新的JSON提取器构建器实例
         */
        public Builder() {
            super(new JSONExtractor());
        }
    }
}