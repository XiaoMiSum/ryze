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

package io.github.xiaomisum.ryze.protocol.rabbit.processor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import com.rabbitmq.client.ConnectionFactory;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Postprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.protocol.rabbit.Rabbit;
import io.github.xiaomisum.ryze.protocol.rabbit.RabbitConstantsInterface;
import io.github.xiaomisum.ryze.protocol.rabbit.RealRabbitRequest;
import io.github.xiaomisum.ryze.protocol.rabbit.config.RabbitConfigureItem;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * RabbitMQ 后置处理器
 * <p>
 * 该类用于在测试执行后发送 RabbitMQ 消息，通常用于清理操作或发送测试结果通知。
 * 它继承自 AbstractProcessor 并实现了 Postprocessor 接口。
 * </p>
 *
 * @author mi.xiao
 * @since 2024/11/04 20:09
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@KW({"rabbitmq", "rabbit", "rabbit_mq", "rabbit_postprocessor"})
public class RabbitPostprocessor extends AbstractProcessor<RabbitPostprocessor, RabbitConfigureItem, DefaultSampleResult> implements Postprocessor, RabbitConstantsInterface {

    /**
     * 消息内容
     */
    @JSONField(serialize = false)
    private String message;

    /**
     * 连接工厂
     */
    @JSONField(serialize = false)
    private ConnectionFactory factory;

    /**
     * 默认构造函数
     */
    public RabbitPostprocessor() {
        super();
    }

    /**
     * 构造函数，使用构建器创建 RabbitPostprocessor 实例
     *
     * @param builder 构建器实例
     */
    public RabbitPostprocessor(Builder builder) {
        super(builder);
    }

    /**
     * 创建构建器实例
     * <p>
     * 工厂方法，用于创建 RabbitPostprocessor.Builder 构建器实例。
     * </p>
     *
     * @return RabbitPostprocessor.Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 获取测试结果对象
     * <p>
     * 创建并返回用于记录测试结果的 DefaultSampleResult 对象。
     * </p>
     *
     * @return 测试结果对象
     */
    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.getId(), StringUtils.isBlank(runtime.getTitle()) ? "Rabbit 后置处理器" : runtime.getTitle());

    }

    /**
     * 执行采样操作
     * <p>
     * 使用 Rabbit 工具类执行消息发送操作。
     * </p>
     *
     * @param context 上下文包装器
     * @param result  采样结果对象
     */
    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        Rabbit.execute(factory, runtime.getConfig(), message, result);
    }

    /**
     * 处理请求
     * <p>
     * 在发送消息前处理请求，包括合并配置项、创建连接工厂和准备消息内容。
     * </p>
     *
     * @param context 上下文包装器
     * @param result  采样结果对象
     */
    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        // 1. 合并配置项
        var localConfig = Objects.isNull(runtime.getConfig()) ? new RabbitConfigureItem() : runtime.getConfig();
        var ref = StringUtils.isBlank(localConfig.getRef()) ? DEF_REF_NAME_KEY : localConfig.getRef();
        var otherConfig = (RabbitConfigureItem) context.getLocalVariablesWrapper().get(ref);
        runtime.setConfig(localConfig.merge(otherConfig));
        // 2. 创建Rabbit 连接池对象
        factory = Rabbit.handleRequest(runtime.getConfig());
        message = switch (config.getMessage()) {
            case Number number -> number.toString();
            case Boolean bool -> bool.toString();
            case null -> "";
            default -> JSON.toJSONString(config.getMessage());
        };
        result.setRequest(RealRabbitRequest.build(runtime.getConfig(), message));
    }

    /**
     * 处理响应
     * <p>
     * 在发送消息后处理响应，包括设置响应结果和清理资源。
     * </p>
     *
     * @param context 上下文包装器
     * @param result  采样结果对象
     */
    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setResponse(SampleResult.DefaultReal.build(new byte[0]));
        factory = null;
    }

    /**
     * RabbitMQ 后置处理器构建器
     * <p>
     * 用于构建 RabbitMQ 后置处理器的内部构建器类。
     * </p>
     */
    public static class Builder extends PostprocessorBuilder<RabbitPostprocessor, Builder, RabbitConfigureItem,
            RabbitConfigureItem.Builder, DefaultExtractorsBuilder, DefaultSampleResult> {
        /**
         * 构建 RabbitPostprocessor 实例
         *
         * @return RabbitPostprocessor 实例
         */
        @Override
        public RabbitPostprocessor build() {
            return new RabbitPostprocessor(this);
        }

        /**
         * 获取提取器构建器
         * <p>
         * 创建并返回 DefaultExtractorsBuilder 实例。
         * </p>
         *
         * @return DefaultExtractorsBuilder 实例
         */
        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        /**
         * 获取配置项构建器
         * <p>
         * 创建并返回 RabbitConfigureItem.Builder 实例。
         * </p>
         *
         * @return RabbitConfigureItem.Builder 实例
         */
        @Override
        protected RabbitConfigureItem.Builder getConfigureItemBuilder() {
            return RabbitConfigureItem.builder();
        }
    }
}