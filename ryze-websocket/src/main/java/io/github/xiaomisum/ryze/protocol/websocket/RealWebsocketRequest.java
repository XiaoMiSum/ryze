/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * 'Software'), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package io.github.xiaomisum.ryze.protocol.websocket;

import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.simplewebsocket.Request;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class RealWebsocketRequest extends SampleResult.Real {

    /**
     * 请求URL
     */
    private final String url;

    /**
     * 查询参数字符串
     */
    private final String query;

    /**
     * 请求体字节数组
     */
    private final String body;

    /**
     * Websocket响应头列表
     * <p>包含响应中的所有Socket头信息</p>
     */
    protected Map<String, String> headers;

    /**
     * 构造Websocket请求结果对象
     *
     * @param request Websocket请求对象
     */
    public RealWebsocketRequest(Request request) {
        super(new byte[0]);
        url = request.url();
        query = request.query();
        body = request.bytes() != null && request.bytes().length > 0 ? new String(request.bytes()) : request.body();
        headers = request.headers();
    }


    /**
     * 获取请求字节数组
     * <p>优先返回请求体，如果没有请求体则返回查询参数</p>
     *
     * @return 请求字节数组
     */
    @Override
    public byte[] bytes() {
        return body != null && !body.isEmpty() ? body.getBytes() : query != null ? query.getBytes() : super.bytes();
    }

    /**
     * 获取请求字符串表示
     *
     * @return 请求字符串
     */
    @Override
    public String bytesAsString() {
        return new String(bytes());
    }

    @Override
    public String format() {
        var buf = new StringBuilder();
        buf.append(url);
        headers.forEach((key, value) -> buf.append(key).append(": ").append(value).append("\n"));
        if (StringUtils.isNotBlank(query)) {
            buf.append("\n").append("Request Query:").append(query);
        }
        if (body != null && !body.isEmpty() && !body.equals("null")) {
            buf.append("\n").append("Request Body:").append(body);
        }
        return buf.toString();
    }
}
