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

package io.github.xiaomisum.ryze.protocol.mqtt;

import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;

/**
 * MQTT实际请求结果类
 * <p>
 * 封装了MQTT请求的详细信息，包括主题、QoS、负载等。
 * </p>
 *
 * @author xiaomi
 */
public class RealMqttRequest extends SampleResult.RealRequest {

    private final String topic;
    private final int qos;
    private final String payload;

    public RealMqttRequest(String topic, int qos, String payload) {
        this.topic = topic;
        this.qos = qos;
        this.payload = payload;
    }

    @Override
    public byte[] bytes() {
        return payload != null ? payload.getBytes() : new byte[0];
    }

    @Override
    public String format() {
        var buf = new StringBuilder();
        buf.append("Topic: ").append(topic);
        buf.append("\nQoS: ").append(qos);
        if (StringUtils.isNotBlank(payload)) {
            buf.append("\nPayload: ").append(payload);
        }
        return buf.toString();
    }
}
