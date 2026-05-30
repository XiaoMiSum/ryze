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

package io.github.xiaomisum.ryze.protocol.mqtt.sampler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.builder.*;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.mqtt.MqttConstantsInterface;
import io.github.xiaomisum.ryze.protocol.mqtt.MqttProtocolClient;
import io.github.xiaomisum.ryze.protocol.mqtt.RealMqttRequest;
import io.github.xiaomisum.ryze.protocol.mqtt.RealMqttResponse;
import io.github.xiaomisum.ryze.protocol.mqtt.config.MqttConfigureItem;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.AbstractSampler;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.Sampler;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * MQTT发布取样器
 * <p>
 * 执行MQTT消息发布操作，记录发送时间和响应。
 * </p>
 *
 * @author xiaomi
 */
@KW(value = {"mqtt_publish", "mqtt_pub", "mqtt", "mqtt_publish_sampler"})
public class MqttPublishSampler extends AbstractSampler<MqttPublishSampler, MqttConfigureItem, DefaultSampleResult> implements Sampler<DefaultSampleResult>, MqttConstantsInterface {

    @JSONField(serialize = false)
    private MqttProtocolClient mqttClient;
    @JSONField(serialize = false)
    private String payloadStr;

    public MqttPublishSampler() {
        super();
    }

    public MqttPublishSampler(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.id, runtime.title);
    }

    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        var config = runtime.getConfig();
        result.sampleStart();
        try {
            mqttClient.publish(config.getTopic(), payloadStr.getBytes(StandardCharsets.UTF_8), config.getQosValue());
        } finally {
            result.sampleEnd();
        }
    }

    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        var localConfig = Objects.isNull(runtime.getConfig()) ? new MqttConfigureItem() : runtime.getConfig();
        var otherConfig = (MqttConfigureItem) context.getLocalVariablesWrapper().get(localConfig.getDatasource(DEF_REF_NAME_KEY));
        runtime.setConfig(localConfig.merge(otherConfig));

        var config = runtime.getConfig();
        // 构建payload字符串
        Object payload = config.getPayload();
        payloadStr = payload == null ? "" : payload instanceof String s ? s : JSON.toJSONString(payload);

        // 创建MQTT客户端并连接
        mqttClient = new MqttProtocolClient(
                config.getBroker(), config.getPortValue(), config.getClientId(),
                config.getUsername(), config.getPassword(), config.isCleanSession(),
                config.getKeepAliveValue(), config.isTlsEnabled());
        mqttClient.connect();

        result.setRequest(new RealMqttRequest(config.getTopic(), config.getQosValue(), payloadStr));
    }

    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setResponse(new RealMqttResponse("Published successfully"));
        if (mqttClient != null) {
            mqttClient.disconnect();
        }
    }

    /**
     * MQTT 发布取样器构建器
     */
    public static class Builder extends AbstractSampler.Builder<MqttPublishSampler, Builder, MqttConfigureItem,
            MqttConfigureItem.Builder, DefaultConfigureElementsBuilder, DefaultPreprocessorsBuilder, DefaultPostprocessorsBuilder,
            DefaultAssertionsBuilder, DefaultExtractorsBuilder, DefaultSampleResult> {
        @Override
        public MqttPublishSampler build() {
            return new MqttPublishSampler(this);
        }

        @Override
        protected DefaultConfigureElementsBuilder getConfiguresBuilder() {
            return DefaultConfigureElementsBuilder.builder();
        }

        @Override
        protected DefaultAssertionsBuilder getAssertionsBuilder() {
            return DefaultAssertionsBuilder.builder();
        }

        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        @Override
        protected DefaultPreprocessorsBuilder getPreprocessorsBuilder() {
            return DefaultPreprocessorsBuilder.builder();
        }

        @Override
        protected DefaultPostprocessorsBuilder getPostprocessorsBuilder() {
            return DefaultPostprocessorsBuilder.builder();
        }

        @Override
        protected MqttConfigureItem.Builder getConfigureItemBuilder() {
            return MqttConfigureItem.builder();
        }
    }
}
