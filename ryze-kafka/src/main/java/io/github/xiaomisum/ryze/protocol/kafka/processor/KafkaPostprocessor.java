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

package io.github.xiaomisum.ryze.protocol.kafka.processor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Postprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.protocol.kafka.Kafka;
import io.github.xiaomisum.ryze.protocol.kafka.KafkaConstantsInterface;
import io.github.xiaomisum.ryze.protocol.kafka.RealKafkaRequest;
import io.github.xiaomisum.ryze.protocol.kafka.config.KafkaConfigureItem;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Kafka后置处理器类，
 * <p>
 * 该类实现了Postprocessor接口，继承自AbstractProcessor。
 * 后置处理器通常用于数据清理工作。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>执行Kafka消息发送操作</li>
 *   <li>处理响应数据</li>
 *   <li>支持变量提取</li>
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
 * @see AbstractProcessor
 * @see Postprocessor
 * @see KafkaConstantsInterface
 * @since 2021/4/13 20:08
 */
@KW({"kafka_postprocessor", "kafka_post_processor", "kafka"})
@SuppressWarnings({"unchecked", "rawtypes"})
public class KafkaPostprocessor extends AbstractProcessor<KafkaPostprocessor, KafkaConfigureItem, DefaultSampleResult> implements Postprocessor, KafkaConstantsInterface {

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
     * 默认构造函数
     */
    public KafkaPostprocessor() {
        super();
    }

    /**
     * 使用构建器创建KafkaPostprocessor实例的构造函数
     *
     * @param builder KafkaPostprocessor构建器
     */
    public KafkaPostprocessor(Builder builder) {
        super(builder);
    }

    /**
     * 创建KafkaPostprocessor构建器的静态工厂方法
     *
     * @return KafkaPostprocessor.Builder实例
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
        return new DefaultSampleResult(runtime.getId(), StringUtils.isBlank(runtime.getTitle()) ? "Kafka 后置处理器" : runtime.getTitle());

    }

    /**
     * 执行Kafka消息发送操作的核心方法
     *
     * @param context 测试上下文对象
     * @param result  测试结果对象
     */
    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        response = Kafka.execute(runtime.getConfig(), message, result);
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
     * KafkaPostprocessor构建器类，用于构建KafkaPostprocessor实例
     * <p>
     * 该构建器继承自AbstractProcessor.PostprocessorBuilder，提供了构建KafkaPostprocessor实例的所有必要方法。
     * </p>
     */
    public static class Builder extends PostprocessorBuilder<KafkaPostprocessor, Builder, KafkaConfigureItem,
            KafkaConfigureItem.Builder, DefaultExtractorsBuilder, DefaultSampleResult> {

        /**
         * 构建KafkaPostprocessor实例
         *
         * @return KafkaPostprocessor实例
         */
        @Override
        public KafkaPostprocessor build() {
            return new KafkaPostprocessor(this);
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