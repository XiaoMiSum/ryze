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

package io.github.xiaomisum.ryze.protocol.coap.processor;

import com.alibaba.fastjson2.annotation.JSONField;
import org.eclipse.californium.core.CoapResponse;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.coap.CoapConstantsInterface;
import io.github.xiaomisum.ryze.protocol.coap.CoapProtocolClient;
import io.github.xiaomisum.ryze.protocol.coap.RealCoapRequest;
import io.github.xiaomisum.ryze.protocol.coap.RealCoapResponse;
import io.github.xiaomisum.ryze.protocol.coap.config.CoapConfigureItem;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Preprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * CoAP前置处理器
 *
 * @author xiaomi
 */
@KW(value = {"coap_preprocessor", "coap_pre_processor", "coap"})
@SuppressWarnings({"unchecked", "rawtypes"})
public class CoapPreprocessor extends AbstractProcessor<CoapPreprocessor, CoapConfigureItem, DefaultSampleResult> implements Preprocessor, CoapConstantsInterface {

    @JSONField(serialize = false)
    private CoapResponse response;

    public CoapPreprocessor(Builder builder) {
        super(builder);
    }

    public CoapPreprocessor() {
        super();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.getId(), StringUtils.isBlank(runtime.getTitle()) ? "CoAP 前置处理器" : runtime.getTitle());
    }

    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        var config = runtime.getConfig();
        result.sampleStart();
        try {
            response = CoapProtocolClient.sendRequest(
                    config.getMethod(),
                    config.getUri(),
                    config.getPayload(),
                    config.isConfirmable(),
                    config.getTimeout()
            );
        } finally {
            result.sampleEnd();
        }
    }

    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        // 1. 合并配置项
        var localConfig = Objects.isNull(runtime.getConfig()) ? new CoapConfigureItem() : runtime.getConfig();
        var otherConfig = (CoapConfigureItem) context.getLocalVariablesWrapper().get(localConfig.getRef());
        runtime.setConfig(localConfig.merge(otherConfig));
        // 2. 设置请求信息
        result.setRequest(new RealCoapRequest(runtime.getConfig()));
    }

    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setResponse(new RealCoapResponse(response));
    }

    /**
     * CoAP前置处理器构建器
     */
    public static class Builder extends PreprocessorBuilder<CoapPreprocessor, Builder, CoapConfigureItem,
            CoapConfigureItem.Builder, DefaultExtractorsBuilder, DefaultSampleResult> {
        @Override
        public CoapPreprocessor build() {
            return new CoapPreprocessor(this);
        }

        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        @Override
        protected CoapConfigureItem.Builder getConfigureItemBuilder() {
            return CoapConfigureItem.builder();
        }
    }
}
