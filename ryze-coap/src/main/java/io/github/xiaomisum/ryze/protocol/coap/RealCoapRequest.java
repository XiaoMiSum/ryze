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

package io.github.xiaomisum.ryze.protocol.coap;

import io.github.xiaomisum.ryze.protocol.coap.config.CoapConfigureItem;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;

/**
 * CoAP实际请求结果类
 * <p>
 * 该类封装了CoAP请求的详细信息，包括方法、URI、请求体、内容格式等。
 * </p>
 *
 * @author xiaomi
 */
public class RealCoapRequest extends SampleResult.RealRequest {

    /**
     * 请求方法
     */
    private final String method;

    /**
     * 请求URI
     */
    private final String uri;

    /**
     * 请求体
     */
    private final String payload;

    /**
     * 内容格式
     */
    private final String contentFormat;

    /**
     * 是否确认消息
     */
    private final boolean confirmable;

    /**
     * 构造CoAP请求结果对象
     *
     * @param config CoAP配置项
     */
    public RealCoapRequest(CoapConfigureItem config) {
        this.method = config.getMethod();
        this.uri = config.getUri();
        this.payload = config.getPayload();
        this.contentFormat = config.getContentFormat();
        this.confirmable = config.isConfirmable();
    }

    @Override
    public byte[] bytes() {
        return payload != null && !payload.isEmpty() ? payload.getBytes() : new byte[0];
    }

    @Override
    public String format() {
        var buf = new StringBuilder();
        buf.append(method).append(" ").append(uri);
        buf.append("\nType: ").append(confirmable ? "CON" : "NON");
        buf.append("\nContent-Format: ").append(contentFormat);
        if (StringUtils.isNotBlank(payload)) {
            buf.append("\n\nPayload: ").append(payload);
        }
        return buf.toString();
    }
}
