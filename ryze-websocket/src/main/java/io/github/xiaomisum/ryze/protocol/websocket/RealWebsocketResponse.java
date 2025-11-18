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

package io.github.xiaomisum.ryze.protocol.websocket;

import io.github.xiaomisum.ryze.protocol.websocket.config.WebsocketConfigureItem;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.simplewebsocket.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Websocket实际响应结果类
 * <p>
 * 该类封装了Websocket响应的详细信息，包括状态码、状态消息、响应头和响应体等。
 * 实现了SampleResult.Real接口，用于在测试报告中展示Websocket响应的详细信息。
 * </p>
 *
 * @author xiaomi
 */
public class RealWebsocketResponse extends SampleResult.Real {

    /**
     * Websocket状态码
     */
    private int statusCode;


    /**
     * 构造Websocket实际响应结果对象
     *
     * @param response Websocket响应对象
     */
    public RealWebsocketResponse(Response response, WebsocketConfigureItem config) {
        super(Objects.isNull(response) ? new byte[0] : response.text(config.getBytesToStringConverter() == null ?
                String::new : config.getBytesToStringConverter()).getBytes(StandardCharsets.UTF_8));
        if (Objects.isNull(response)) {
            return;
        }
        statusCode = response.status();
    }


    /**
     * 格式化Websocket响应信息
     * <p>
     * 将Websocket响应信息格式化为易于阅读的字符串格式，包含响应行、响应头和响应体。
     * 格式遵循Websocket协议标准格式：
     * <pre>
     * status: 1000
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
        buf.append("status: ").append(statusCode);
        if (StringUtils.isNotBlank(bytesAsString())) {
            buf.append("\n").append("Response body: ").append(StringEscapeUtils.unescapeHtml4(bytesAsString()));
        }
        return buf.toString();
    }

    /**
     * 获取Websocket状态码
     *
     * @return Websocket状态码
     */
    public int statusCode() {
        return statusCode;
    }
}