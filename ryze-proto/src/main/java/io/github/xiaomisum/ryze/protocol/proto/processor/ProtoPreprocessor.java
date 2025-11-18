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

package io.github.xiaomisum.ryze.protocol.proto.processor;

import com.alibaba.fastjson2.annotation.JSONField;
import com.google.protobuf.Descriptors;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.proto.Proto;
import io.github.xiaomisum.ryze.protocol.proto.ProtoConstantsInterface;
import io.github.xiaomisum.ryze.protocol.proto.RealProtoRequest;
import io.github.xiaomisum.ryze.protocol.proto.RealProtoResponse;
import io.github.xiaomisum.ryze.protocol.proto.config.ProtoConfigureItem;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Preprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * Proto协议后置处理器
 * <p>
 * 用于处理Proto协议请求的后置处理器，负责执行Proto请求、处理响应结果并提供数据提取功能
 * </p>
 *
 * @author xiaomi
 */
@KW(value = {"proto_preprocessor", "protoc_preprocessor", "protobuf_preprocessor",
        "proto_pre_processor", "protoc_pre_processor", "protobuf_pre_processor", "proto", "protoc", "protobuf"})
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProtoPreprocessor extends AbstractProcessor<ProtoPreprocessor, ProtoConfigureItem, DefaultSampleResult> implements Preprocessor, ProtoConstantsInterface {

    @JSONField(serialize = false)
    private RealProtoRequest request;
    @JSONField(serialize = false)
    private RealProtoResponse response;
    @JSONField(serialize = false)
    private Map<String, Descriptors.FileDescriptor> descriptorMap;


    /**
     * 构造函数，使用构建器初始化后置处理器
     *
     * @param builder 构建器实例
     */
    public ProtoPreprocessor(Builder builder) {
        super(builder);
    }

    /**
     * 默认构造函数
     */
    public ProtoPreprocessor() {
        super();
    }

    /**
     * 创建Proto后置处理器构建器
     *
     * @return 构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 获取测试结果对象
     *
     * @return 默认采样结果实例
     */
    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.getId(), StringUtils.isBlank(runtime.getTitle()) ? "Proto 后置处理器" : runtime.getTitle());
    }

    /**
     * 执行采样操作
     *
     * @param context 上下文包装器
     * @param result  采样结果对象
     */
    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        response = Proto.execute(runtime.getConfig(), descriptorMap, result, request);
    }

    /**
     * 处理请求阶段
     *
     * @param context 上下文包装器
     * @param result  采样结果对象
     */
    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        // 1. 合并配置项
        var localConfig = Objects.isNull(runtime.getConfig()) ? new ProtoConfigureItem() : runtime.getConfig();
        var otherConfig = (ProtoConfigureItem) context.getLocalVariablesWrapper().get(localConfig.getRef());
        runtime.setConfig(localConfig.merge(otherConfig));
        // 2. 创建http对象
        request = new RealProtoRequest();
        descriptorMap = Proto.loadFileDescriptors(runtime.getConfig().getProtoDesc().getDescPath());
        result.setRequest(request);
    }

    /**
     * 处理响应阶段
     *
     * @param context 上下文包装器
     * @param result  采样结果对象
     */
    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setResponse(response);
    }

    /**
     * Proto 后置处理器构建器
     */
    public static class Builder extends PostprocessorBuilder<ProtoPreprocessor, Builder, ProtoConfigureItem,
            ProtoConfigureItem.Builder, DefaultExtractorsBuilder, DefaultSampleResult> {
        /**
         * 构建Proto后置处理器实例
         *
         * @return Proto后置处理器实例
         */
        @Override
        public ProtoPreprocessor build() {
            return new ProtoPreprocessor(this);
        }

        /**
         * 获取提取器构建器
         *
         * @return 默认提取器构建器实例
         */
        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        /**
         * 获取配置项构建器
         *
         * @return Proto配置项构建器实例
         */
        @Override
        protected ProtoConfigureItem.Builder getConfigureItemBuilder() {
            return ProtoConfigureItem.builder();
        }
    }
}