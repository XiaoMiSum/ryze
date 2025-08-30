package io.github.xiaomisum.ryze.protocol.http.sampler;

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.builder.*;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.AbstractSampler;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.Sampler;
import io.github.xiaomisum.ryze.protocol.http.*;
import io.github.xiaomisum.ryze.protocol.http.config.HTTPConfigureItem;
import org.apache.commons.lang3.StringUtils;
import xyz.migoo.simplehttp.Request;
import xyz.migoo.simplehttp.Response;

import java.util.Objects;

@KW(value = {"http", "http_sampler", "https"})
public class HTTPSampler extends AbstractSampler<HTTPSampler, HTTPConfigureItem, DefaultSampleResult> implements Sampler<DefaultSampleResult>, HTTPConstantsInterface {

    @JSONField(serialize = false)
    private Request request;
    @JSONField(serialize = false)
    private Response response;

    public HTTPSampler() {
        super();
    }

    public HTTPSampler(Builder builder) {
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
     * HTTP 取样器构建器
     */
    public static class Builder extends AbstractSampler.Builder<HTTPSampler, Builder, HTTPConfigureItem,
            HTTPConfigureItem.Builder, DefaultConfigureElementsBuilder, DefaultPreprocessorsBuilder, DefaultPostprocessorsBuilder,
            DefaultAssertionsBuilder, DefaultExtractorsBuilder, DefaultSampleResult> {
        @Override
        public HTTPSampler build() {
            return new HTTPSampler(this);
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
        protected HTTPConfigureItem.Builder getConfigureItemBuilder() {
            return HTTPConfigureItem.builder();
        }
    }
}
