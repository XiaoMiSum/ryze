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

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.TestStatus;
import io.github.xiaomisum.ryze.extractor.AbstractExtractor;
import io.github.xiaomisum.ryze.extractor.ExtractResult;
import io.github.xiaomisum.ryze.extractor.builtin.JSONExtractor;
import io.github.xiaomisum.ryze.extractor.builtin.RegexExtractor;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.protocol.http.RealHTTPRealResultResponse;

/**
 * HTTP响应头提取器，专门用于从HTTP响应头中提取数据
 *
 * <p>该提取器仅支持提取HTTP响应头中的数据。当响应头中存在多个同名字段时，
 * 默认返回第一个匹配的值，也可以通过matchNum指定要获取第几个同名头字段的值。</p>
 *
 * <p>使用示例：
 * <pre>
 * 提取Content-Type头：
 * field = "Content-Type"
 * refName = "contentType"
 *
 * 提取Set-Cookie头（当有多个时提取第一个）：
 * field = "Set-Cookie"
 * refName = "cookie"
 *
 * 提取第二个Set-Cookie头：
 * field = "Set-Cookie"
 * refName = "cookie2"
 * matchNum = 1
 * </pre></p>
 *
 * <p>注意：如需提取响应体中的JSON数据，请使用 {@link JSONExtractor}
 * 如需提取响应体中的HTML数据，请使用 {@link RegexExtractor}</p>
 *
 * @author xiaomi
 * @see JSONExtractor 用于提取JSON响应体数据
 * @see RegexExtractor 用于提取HTML或其他文本响应体数据
 */
@KW({"http_header_extractor", "http_header", "HTTPHeader", "http", "https"})
public class HTTPHeaderExtractor extends AbstractExtractor {

    /**
     * 匹配序号，用于指定要提取第几个同名头字段
     *
     * <p>当响应中存在多个同名头字段时，通过该字段指定要提取第几个头字段的值。
     * 序号从0开始计数，即0表示第一个头字段，1表示第二个头字段，以此类推。
     * 默认值为0，表示提取第一个同名头字段。</p>
     */
    @JSONField(name = MATCH_NUM)
    protected int matchNum;

    /**
     * 创建一个新的HTTP头提取器构建器
     *
     * @return HTTP头提取器构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 执行HTTP响应头数据提取
     *
     * <p>提取流程包括：
     * <ol>
     *   <li>获取HTTP响应对象</li>
     *   <li>查找指定名称的头字段</li>
     *   <li>根据matchNum选择头字段</li>
     *   <li>提取头字段值</li>
     *   <li>封装提取结果</li>
     *   <li>处理提取失败情况</li>
     * </ol></p>
     *
     * @param result 取样结果，包含HTTP响应数据
     * @return 提取结果对象
     */
    @Override
    protected ExtractResult extract(SampleResult result) {
        var res = new ExtractResult("HTTP响应 提取: " + field);
        var response = (RealHTTPRealResultResponse) result.getResponse();
        Object value = null;
        var headers = response.headers().stream().filter(header -> header.getName().equalsIgnoreCase(field)).toList();
        if (!headers.isEmpty()) {
            matchNum = (headers.size() < matchNum + 1) ? headers.size() - 1 : matchNum;
            value = headers.get(matchNum).getValue();
            res.setValue(value);
        }
        if (value == null) {
            res.setStatus(TestStatus.failed);
            res.setMessage("响应头中不存在：" + field);
        }
        return res;
    }


    /**
     * HTTP头提取器构建器，提供链式调用方式创建HTTP头提取器实例
     */
    public static class Builder extends AbstractExtractor.Builder<Builder, HTTPHeaderExtractor> {

        /**
         * 构造一个新的HTTP头提取器构建器实例
         */
        public Builder() {
            super(new HTTPHeaderExtractor());
        }

        /**
         * 设置匹配序号
         *
         * @param matchNum 匹配序号，从0开始计数
         * @return 构建器自身实例，用于链式调用
         */
        public Builder matchNum(int matchNum) {
            extractor.matchNum = matchNum;
            return self;
        }
    }
}