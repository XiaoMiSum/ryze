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

package io.github.xiaomisum.ryze.protocol.http;

import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.hc.core5.http.Header;
import xyz.migoo.simplehttp.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * HTTP实际响应结果类
 * <p>
 * 该类封装了HTTP响应的详细信息，包括状态码、状态消息、响应头和响应体等。
 * 实现了SampleResult.Real接口，用于在测试报告中展示HTTP响应的详细信息。
 * </p>
 *
 * @author xiaomi
 */
public class RealHTTPResponse extends SampleResult.RealResponse {


    /**
     * HTTP响应头列表
     * <p>包含响应中的所有HTTP头信息</p>
     */
    protected List<Header> headers;
    /**
     * HTTP协议版本
     * <p>例如："HTTP/1.1" 或 "HTTP/2"</p>
     */
    protected String version;
    /**
     * HTTP状态消息
     */
    private String message;

    private byte[] bytes;

    /**
     * 构造HTTP实际响应结果对象
     *
     * @param response HTTP响应对象
     */
    public RealHTTPResponse(Response response) {
        if (Objects.nonNull(response)) {
            status = response.statusCode();
            version = response.version();
            message = response.message();
            headers = Arrays.stream(response.headers()).toList();
            bytes = response.bytes();
        }
    }

    /**
     * 通过直接设置属性的方式创建对象
     * <p>
     * 当前仅用于单元测试
     *
     * @param bytes   响应数据
     * @param status  http状态码
     * @param version http版本
     * @param message http消息
     * @param headers 响应头信息
     */
    public RealHTTPResponse(byte[] bytes, int status, String version, String message, Header... headers) {
        this.bytes = bytes;
        this.status = status;
        this.version = version;
        this.message = message;
        this.headers = Arrays.stream(headers).toList();
    }


    @Override
    public byte[] bytes() {
        return bytes;
    }

    /**
     * 格式化HTTP响应信息
     * <p>
     * 将HTTP响应信息格式化为易于阅读的字符串格式，包含响应行、响应头和响应体。
     * 格式遵循HTTP协议标准格式：
     * <pre>
     * HTTP/1.1 200 OK
     * Content-Type: text/html; charset=utf-8
     * Content-Length: 55743
     * Connection: keep-alive
     * Cache-Control: s-maxage=300, public, max-age=0
     * Content-Language: en-US
     * Date: Thu, 06 Dec 2018 17:37:18 GMT
     * ETag: "2e77ad1dc6ab0b53a2996dfd4653c1c3"
     * Server: meinheld/0.6.1
     * Strict-Transport-Security: max-age=63072000
     * X-Content-Type-Options: nosniff
     * X-Frame-Options: DENY
     * X-XSS-Protection: 1; mode=block
     * Vary: Accept-Encoding,Cookie
     * Age: 7
     *
     * Response body: <!DOCTYPE html>
     * <html lang="en">
     * <head>
     * <meta charset="utf-8">
     * <title>A simple webpage</title>
     * </head>
     * <body>
     * <h1>Simple HTML webpage</h1>
     * <p>Hello, world!</p>
     * </body>
     * </html>
     * </pre>
     * </p>
     *
     * @return 格式化后的响应信息字符串
     */
    @Override
    public String format() {
        var buf = new StringBuilder();
        buf.append(version).append(" ").append(status).append(" ").append(message);
        if (headers != null && !headers.isEmpty()) {
            buf.append("\n");
            headers.forEach(header -> buf.append(header.getName()).append(": ").append(header.getValue()).append("\n"));
        }
        if (bytes != null && bytes.length > 0) {
            buf.append("\n").append("Response body: ").append(new String(bytes));
        }
        return buf.toString();
    }

    public List<Header> headers() {
        return headers;
    }

}