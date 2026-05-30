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

import org.eclipse.californium.core.CoapResponse;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * CoAP实际响应结果类
 * <p>
 * 该类封装了CoAP响应的详细信息，包括响应码、响应体、内容格式等。
 * </p>
 *
 * @author xiaomi
 */
public class RealCoapResponse extends SampleResult.RealResponse {

    /**
     * 响应体
     */
    private final String body;

    /**
     * 响应码描述
     */
    private final String responseCode;

    /**
     * 构造CoAP响应结果对象
     *
     * @param response CoAP响应对象
     */
    public RealCoapResponse(CoapResponse response) {
        if (response != null) {
            this.responseCode = response.getCode().toString();
            this.body = response.getResponseText() != null ? response.getResponseText() : "";
            this.status = response.getCode().value;
        } else {
            this.responseCode = "No Response";
            this.body = "";
            this.status = -1;
        }
    }

    @Override
    public byte[] bytes() {
        return body.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String format() {
        var buf = new StringBuilder();
        buf.append("Response Code: ").append(responseCode);
        if (StringUtils.isNotBlank(body)) {
            buf.append("\n\nResponse Payload: ").append(body);
        }
        return buf.toString();
    }
}
