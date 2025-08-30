/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze.protocol.kafka.sampler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.builder.DefaultAssertionsBuilder;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.AbstractSampler;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.Sampler;
import io.github.xiaomisum.ryze.protocol.kafka.Kafka;
import io.github.xiaomisum.ryze.protocol.kafka.KafkaConstantsInterface;
import io.github.xiaomisum.ryze.protocol.kafka.RealKafkaRequest;
import io.github.xiaomisum.ryze.protocol.kafka.builder.KafkaConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.kafka.builder.KafkaPostprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.kafka.builder.KafkaPreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.kafka.config.KafkaConfigureItem;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Kafka取样器类，用于执行Kafka消息发送操作
 * <p>
 * 该类是Kafka协议测试的核心组件，负责执行实际的Kafka消息发送操作。
 * 它继承自AbstractSampler，具有完整的测试元件生命周期管理能力。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>执行Kafka消息发送操作</li>
 *   <li>管理测试元件生命周期</li>
 *   <li>处理请求和响应数据</li>
 *   <li>支持配置合并和变量替换</li>
 * </ul>
 * </p>
 * <p>
 * 业务处理逻辑：
 * <ol>
 *   <li>初始化测试结果对象</li>
 *   <li>处理请求阶段：合并配置项、处理消息内容</li>
 *   <li>执行阶段：调用Kafka客户端发送消息</li>
 *   <li>响应处理阶段：封装响应结果</li>
 * </ol>
 * </p>
 *
 * @author mi.xiao
 * @since 2021/4/10 21:10
 * @see AbstractSampler
 * @see Sampler
 * @see KafkaConstantsInterface
 */
@KW({"kafka_sampler", "kafka"})
@SuppressWarnings({"unchecked", "rawtypes"})
public class KafkaSampler extends AbstractSampler<KafkaSampler, KafkaConfigureItem, DefaultSampleResult> implements Sampler<DefaultSampleResult>, KafkaConstantsInterface {

    /**
     * 响应数据字节数组，用于存储Kafka发送操作的响应结果
     */
    @JSONField(serialize = false)
    private byte[] response;

    /**
     * 消息内容字符串，用于存储实际发送的Kafka消息内容
     */
    @JSONField(serialize = false)
    private String message;


    /**
     * 使用构建器创建KafkaSampler实例的构造函数
     *
     * @param builder KafkaSampler构建器
     */
    public KafkaSampler(Builder builder) {
        super(builder);
    }

    /**
     * 默认构造函数
     */
    public KafkaSampler() {
        super();
    }

    /**
     * 创建KafkaSampler构建器的静态工厂方法
     *
     * @return KafkaSampler.Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 获取测试结果对象，用于记录测试执行结果
     *
     * @return DefaultSampleResult测试结果对象
     */
    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.id, runtime.title);
    }

    /**
     * 执行Kafka消息发送操作的核心方法
     *
     * @param context 测试上下文对象
     * @param result  测试结果对象
     */
    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        response = Kafka.execute(runtime.config, message, result);
    }


    /**
     * 处理请求阶段，合并配置项并准备消息内容
     *
     * @param context 测试上下文对象
     * @param result  测试结果对象
     */
    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        // 1. 合并配置项
        var localConfig = Objects.isNull(runtime.getConfig()) ? new KafkaConfigureItem() : runtime.getConfig();
        var ref = StringUtils.isBlank(localConfig.getRef()) ? DEF_REF_NAME_KEY : localConfig.getRef();
        var otherConfig = (KafkaConfigureItem) context.getLocalVariablesWrapper().get(ref);
        runtime.setConfig(localConfig.merge(otherConfig));
        message = switch (config.getMessage()) {
            case Number number -> number.toString();
            case Boolean bool -> bool.toString();
            case null -> "";
            default -> JSON.toJSONString(config.getMessage());
        };
        result.setRequest(RealKafkaRequest.build(runtime.getConfig(), message));
    }

    /**
     * 处理响应阶段，封装响应结果
     *
     * @param context 测试上下文对象
     * @param result  测试结果对象
     */
    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setResponse(SampleResult.DefaultReal.build(response));
    }

    /**
     * KafkaSampler构建器类，用于构建KafkaSampler实例
     * <p>
     * 该构建器继承自AbstractSampler.Builder，提供了构建KafkaSampler实例的所有必要方法。
     * </p>
     */
    public static class Builder extends AbstractSampler.Builder<KafkaSampler, Builder, KafkaConfigureItem,
            KafkaConfigureItem.Builder, KafkaConfigureElementsBuilder, KafkaPreprocessorsBuilder, KafkaPostprocessorsBuilder,
            DefaultAssertionsBuilder, DefaultExtractorsBuilder, DefaultSampleResult> {
        
        /**
         * 构建KafkaSampler实例
         *
         * @return KafkaSampler实例
         */
        @Override
        public KafkaSampler build() {
            return new KafkaSampler(this);
        }

        /**
         * 获取断言构建器
         *
         * @return DefaultAssertionsBuilder实例
         */
        @Override
        protected DefaultAssertionsBuilder getAssertionsBuilder() {
            return DefaultAssertionsBuilder.builder();
        }

        /**
         * 获取提取器构建器
         *
         * @return DefaultExtractorsBuilder实例
         */
        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        /**
         * 获取配置元件构建器
         *
         * @return KafkaConfigureElementsBuilder实例
         */
        @Override
        protected KafkaConfigureElementsBuilder getConfiguresBuilder() {
            return KafkaConfigureElementsBuilder.builder();
        }

        /**
         * 获取前置处理器构建器
         *
         * @return KafkaPreprocessorsBuilder实例
         */
        @Override
        protected KafkaPreprocessorsBuilder getPreprocessorsBuilder() {
            return KafkaPreprocessorsBuilder.builder();
        }

        /**
         * 获取后置处理器构建器
         *
         * @return KafkaPostprocessorsBuilder实例
         */
        @Override
        protected KafkaPostprocessorsBuilder getPostprocessorsBuilder() {
            return KafkaPostprocessorsBuilder.builder();
        }

        /**
         * 获取配置项构建器
         *
         * @return KafkaConfigureItem.Builder实例
         */
        @Override
        protected KafkaConfigureItem.Builder getConfigureItemBuilder() {
            return KafkaConfigureItem.builder();
        }
    }
}