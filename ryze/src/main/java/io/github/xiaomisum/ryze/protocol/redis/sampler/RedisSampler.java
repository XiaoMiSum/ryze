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

package io.github.xiaomisum.ryze.protocol.redis.sampler;

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.builder.*;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.AbstractSampler;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.Sampler;
import io.github.xiaomisum.ryze.protocol.jdbc.JDBCConstantsInterface;
import io.github.xiaomisum.ryze.protocol.redis.RealRedisRequest;
import io.github.xiaomisum.ryze.protocol.redis.Redis;
import io.github.xiaomisum.ryze.protocol.redis.config.RedisConfigureItem;
import io.github.xiaomisum.ryze.protocol.redis.config.RedisDatasource;

/**
 * @author xiaomi
 */
@KW(value = {"redis", "redis_sampler"})
public class RedisSampler extends AbstractSampler<RedisSampler, RedisConfigureItem, DefaultSampleResult> implements Sampler<DefaultSampleResult>, JDBCConstantsInterface {

    @JSONField(serialize = false)
    private RedisDatasource datasource;

    private byte[] bytes;

    public RedisSampler(Builder builder) {
        super(builder);
    }

    public RedisSampler() {
        super();
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
        bytes = Redis.execute(datasource, runtime.config.getCommand(), runtime.config.getArgs(), result);
    }

    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        datasource = (RedisDatasource) context.getLocalVariablesWrapper().get(runtime.config.getDatasource());
        result.setRequest(new RealRedisRequest(datasource.getUrl(), runtime.getConfig().getCommand(), runtime.getConfig().getArgs()));
    }

    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setResponse(SampleResult.DefaultReal.build(bytes));
    }

    /**
     * Redis 取样器构建器
     */
    public static class Builder extends AbstractSampler.Builder<RedisSampler, Builder, RedisConfigureItem,
            RedisConfigureItem.Builder, DefaultConfigureElementsBuilder, DefaultPreprocessorsBuilder, DefaultPostprocessorsBuilder,
            DefaultAssertionsBuilder, DefaultExtractorsBuilder, DefaultSampleResult> {
        @Override
        public RedisSampler build() {
            return new RedisSampler(this);
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
        protected RedisConfigureItem.Builder getConfigureItemBuilder() {
            return RedisConfigureItem.builder();
        }
    }
}
