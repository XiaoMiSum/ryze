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

package io.github.xiaomisum.ryze.protocol.websocket.processor;

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.websocket.RealWebsocketRequest;
import io.github.xiaomisum.ryze.protocol.websocket.RealWebsocketResponse;
import io.github.xiaomisum.ryze.protocol.websocket.Websocket;
import io.github.xiaomisum.ryze.protocol.websocket.WebsocketConstantsInterface;
import io.github.xiaomisum.ryze.protocol.websocket.config.WebsocketConfigureItem;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Postprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.simplewebsocket.Request;
import io.github.xiaomisum.simplewebsocket.Response;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author xiaomi
 */
@KW(value = {"websocket_postprocessor", "websocket_post_processor", "websocket", "ws", "wss"})
@SuppressWarnings({"unchecked", "rawtypes"})
public class WebsocketPostprocessor extends AbstractProcessor<WebsocketPostprocessor, WebsocketConfigureItem, DefaultSampleResult> implements Postprocessor, WebsocketConstantsInterface {

    @JSONField(serialize = false)
    private Request request;
    @JSONField(serialize = false)
    private Response response;

    public WebsocketPostprocessor(Builder builder) {
        super(builder);
    }

    public WebsocketPostprocessor() {
        super();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.getId(), StringUtils.isBlank(runtime.getTitle()) ? "Websocket 后置处理器" : runtime.getTitle());
    }

    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        response = Websocket.execute(request, runtime.getConfig(), result);
    }

    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        // 1. 合并配置项
        var localConfig = Objects.isNull(runtime.getConfig()) ? new WebsocketConfigureItem() : runtime.getConfig();
        var otherConfig = (WebsocketConfigureItem) context.getLocalVariablesWrapper().get(localConfig.getRef());
        runtime.setConfig(localConfig.merge(otherConfig));
        // 2. 创建websocket request对象
        request = Websocket.build(runtime.getConfig());
        result.setRequest(new RealWebsocketRequest(request));
    }

    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setResponse(new RealWebsocketResponse(response, runtime.getConfig()));
    }

    /**
     * Websocket 后置处理器构建器
     */
    public static class Builder extends PostprocessorBuilder<WebsocketPostprocessor, Builder, WebsocketConfigureItem,
            WebsocketConfigureItem.Builder, DefaultExtractorsBuilder, DefaultSampleResult> {
        @Override
        public WebsocketPostprocessor build() {
            return new WebsocketPostprocessor(this);
        }

        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        @Override
        protected WebsocketConfigureItem.Builder getConfigureItemBuilder() {
            return WebsocketConfigureItem.builder();
        }
    }
}
