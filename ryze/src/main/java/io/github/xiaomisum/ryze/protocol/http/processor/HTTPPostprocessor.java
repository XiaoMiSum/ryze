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

package io.github.xiaomisum.ryze.protocol.http.processor;

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Postprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.protocol.http.*;
import io.github.xiaomisum.ryze.protocol.http.config.HTTPConfigureItem;
import org.apache.commons.lang3.StringUtils;
import xyz.migoo.simplehttp.Request;
import xyz.migoo.simplehttp.Response;

import java.util.Objects;

/**
 * @author xiaomi
 */
@KW(value = {"http_postprocessor", "http_post_processor", "http", "https"})
@SuppressWarnings({"unchecked", "rawtypes"})
public class HTTPPostprocessor extends AbstractProcessor<HTTPPostprocessor, HTTPConfigureItem, DefaultSampleResult> implements Postprocessor, HTTPConstantsInterface {

    @JSONField(serialize = false)
    private Request request;
    @JSONField(serialize = false)
    private Response response;

    public HTTPPostprocessor(Builder builder) {
        super(builder);
    }

    public HTTPPostprocessor() {
        super();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.getId(), StringUtils.isBlank(runtime.getTitle()) ? "HTTP 后置处理器" : runtime.getTitle());
    }

    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        response = HTTP.execute(request, result);
    }

    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        // 1. 合并配置项
        var localConfig = Objects.isNull(runtime.getConfig()) ? new HTTPConfigureItem() : runtime.getConfig();
        var datasource = StringUtils.isBlank(localConfig.getRef()) ?
                DEF_REF_NAME_KEY : localConfig.getRef();
        var otherConfig = (HTTPConfigureItem) context.getLocalVariablesWrapper().get(datasource);
        runtime.setConfig(localConfig.merge(otherConfig));
        // 2. 创建http对象
        request = HTTPClient.build(runtime.getConfig());
        result.setRequest(new RealHTTPRealResultRequest(request));
    }

    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setResponse(new RealHTTPRealResultResponse(response));
    }

    /**
     * HTTP 后置处理器构建器
     */
    public static class Builder extends PostprocessorBuilder<HTTPPostprocessor, Builder, HTTPConfigureItem,
            HTTPConfigureItem.Builder, DefaultExtractorsBuilder, DefaultSampleResult> {
        @Override
        public HTTPPostprocessor build() {
            return new HTTPPostprocessor(this);
        }

        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        @Override
        protected HTTPConfigureItem.Builder getConfigureItemBuilder() {
            return HTTPConfigureItem.builder();
        }
    }
}
