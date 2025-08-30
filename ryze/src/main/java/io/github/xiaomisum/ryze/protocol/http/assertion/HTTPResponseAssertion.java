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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONPath;
import io.github.xiaomisum.ryze.assertion.AbstractAssertion;
import io.github.xiaomisum.ryze.protocol.http.RealHTTPRealResultResponse;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * HTTP响应断言类，用于验证HTTP请求的响应结果
 *
 * <p>该类继承自AbstractAssertion，专门用于处理HTTP协议的响应断言。
 * 支持对HTTP响应的状态码、响应头、响应体等不同部分进行验证。</p>
 *
 * <p>字段说明：
 * <ul>
 *   <li>status/statusCode/statusLine: 验证响应状态码</li>
 *   <li>header[索引].字段名: 验证响应头信息</li>
 *   <li>body或空: 验证响应体内容</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>
 * {
 *   "testclass": "http"
 *   "field": "status",
 *   "expected": 200,
 *   "rule": "=="
 * }
 *
 * {
 *   "testclass": "http"
 *   "field": "header[0].Content-Type",
 *   "expected": "application/json",
 *   "rule": "contains"
 * }
 *
 * {
 *   "testclass": "http"
 *   "field": "body",
 *   "expected": "success",
 *   "rule": "contains"
 * }
 * </pre>
 * </p>
 *
 * @author xiaomi
 * @see AbstractAssertion 抽象断言类
 */
@KW({"HTTPAssertion", "HTTP_Assertion", "http", "https"})
public class HTTPResponseAssertion extends AbstractAssertion {

    /**
     * 状态码相关字段名列表
     */
    private static final List<String> STATUS = Arrays.asList("line", "status", "code", "statuscode", "statusline", "status_code", "status_line");

    /**
     * 响应体字段名
     */
    private static final String BODY = "body";

    /**
     * 响应头字段名匹配模式
     */
    private static final Pattern PATTERN = Pattern.compile("^header(\\[\\d+])?(\\.\\w+.?)?");

    /**
     * 创建HTTP响应断言构建器
     *
     * @return HTTP响应断言构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 根据字段名提取相应的HTTP响应数据
     *
     * <p>该方法根据field字段的值决定提取HTTP响应的哪部分数据：
     * <ul>
     *   <li>status相关字段: 提取响应状态码</li>
     *   <li>header相关字段: 提取响应头信息</li>
     *   <li>body或空字段: 提取响应体内容</li>
     * </ul>
     * </p>
     *
     * @param result 取样结果对象
     * @return 期望值对象
     */
    @Override
    protected Object extractActualValue(SampleResult result) {
        var response = (RealHTTPRealResultResponse) result.getResponse();
        field = StringUtils.isBlank(field) ? BODY : field;
        var matcher = PATTERN.matcher(field);
        if (matcher.find()) {
            var path = "$" + (matcher.group(1) == null ? "[0]" : matcher.group(1)) + matcher.group(2);
            return JSONPath.extract(JSON.toJSONString(response.headers()), path);
        } else {
            return STATUS.contains(field.toLowerCase()) ? response.statusCode() : response.bytesAsString();
        }

    }


    /**
     * HTTP响应断言构建器类
     */
    public static class Builder extends AbstractAssertion.Builder<Builder, HTTPResponseAssertion> {

        /**
         * 构造函数，创建HTTP响应断言构建器
         */
        public Builder() {
            super(new HTTPResponseAssertion());
        }
    }
}