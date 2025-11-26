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

package io.github.xiaomisum.ryze.protocol.proto;

import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * HTTP实际请求结果类
 * <p>
 * 该类封装了HTTP请求的详细信息，包括URL、方法、查询参数、请求体等。
 * 实现了SampleResult.Real接口，用于在测试报告中展示HTTP请求的详细信息。
 * </p>
 *
 * @author xiaomi
 */
public class RealProtoRequest extends SampleResult.RealRequest {

    /**
     * 请求URL
     */
    String url;

    /**
     * HTTP方法
     */
    String method;

    /**
     * 查询参数字符串
     */
    String query;

    /**
     * 请求体字节数组
     */
    String body;

    /**
     * HTTP协议版本
     * <p>例如："HTTP/1.1" 或 "HTTP/2"</p>
     */
    String version;

    /**
     * HTTP响应头列表
     * <p>包含响应中的所有HTTP头信息</p>
     */
    Map<String, String> headers;


    /**
     * 获取请求字节数组
     * <p>优先返回请求体，如果没有请求体则返回查询参数</p>
     *
     * @return 请求字节数组
     */
    @Override
    public byte[] bytes() {
        return StringUtils.isNotBlank(body) ? body.getBytes() : query != null ? query.getBytes() : new byte[0];
    }

    /**
     * 格式化HTTP请求信息
     * <p>
     * 将HTTP请求信息格式化为易于阅读的字符串格式，包含请求行、请求头和请求体。
     * 格式遵循HTTP协议标准格式：
     * <pre>
     * POST /contact_form.php HTTP/1.1
     * Host: developer.mozilla.org
     * Content-Length: 64
     * Content-Type: application/x-www-form-urlencoded
     *
     * Request Query: name=john&age=30
     * Request Body: {"id": 1, "name": "john"}
     * </pre>
     * </p>
     *
     * @return 格式化后的请求信息字符串
     */
    @Override
    public String format() {
        var buf = new StringBuilder();
        if (StringUtils.isNotBlank(method)) {
            buf.append(method).append(" ");
        }
        buf.append(url);
        if (StringUtils.isNotBlank(version)) {
            buf.append(" ").append(version);
        }
        if (!Collections.isEmpty(headers)) {
            buf.append("\n");
            headers.forEach((key, value) -> buf.append(key).append(": ").append(value).append("\n"));
        }
        if (StringUtils.isNotBlank(query)) {
            buf.append("\n").append("Request Query:").append(query);
        }
        if (StringUtils.isNotBlank(body)) {
            buf.append("\n").append("Request Body:").append(body);
        }
        return buf.toString();
    }
}