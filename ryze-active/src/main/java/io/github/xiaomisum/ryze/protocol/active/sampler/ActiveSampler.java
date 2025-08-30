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

package io.github.xiaomisum.ryze.protocol.active.sampler;

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
import io.github.xiaomisum.ryze.protocol.active.Active;
import io.github.xiaomisum.ryze.protocol.active.ActiveConstantsInterface;
import io.github.xiaomisum.ryze.protocol.active.RealActiveRequest;
import io.github.xiaomisum.ryze.protocol.active.builder.ActiveConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.active.builder.ActivePostprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.active.builder.ActivePreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.active.config.ActiveConfigureItem;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * ActiveMQ消息取样器，用于向ActiveMQ发送消息
 * <p>
 * 该类实现了ActiveMQ消息的发送功能，支持发送到Topic或Queue。它继承自AbstractSampler，
 * 提供了完整的测试组件生命周期管理，包括配置处理、消息发送、结果收集等。
 * </p>
 * <p>
 * 主要功能包括：
 * <ul>
 *   <li>支持配置ActiveMQ连接参数（Broker URL、用户名、密码）</li>
 *   <li>支持发送消息到Topic或Queue</li>
 *   <li>支持多种消息格式（字符串、数字、布尔值、JSON对象等）</li>
 *   <li>自动管理连接和会话资源</li>
 *   <li>提供详细的请求和响应信息记录</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * ActiveSampler.builder()
 *     .config(c -> c.brokerUrl("tcp://localhost:61616")
 *                   .topic("test.topic")
 *                   .message("Hello ActiveMQ"))
 *     .build();
 * </pre>
 * </p>
 *
 * @author mi.xiao
 * @since 2021/4/10 21:10
 */
@KW({"active_mq", "activemq", "active"})
@SuppressWarnings({"unchecked", "rawtypes"})
public class ActiveSampler extends AbstractSampler<ActiveSampler, ActiveConfigureItem, DefaultSampleResult> implements Sampler<DefaultSampleResult>, ActiveConstantsInterface {

    @JSONField(serialize = false)
    private ConnectionFactory factory;

    @JSONField(serialize = false)
    private String message;

    /**
     * 默认构造函数
     */
    public ActiveSampler() {
        super();
    }

    /**
     * 使用构建器构造ActiveSampler实例
     *
     * @param builder 构建器实例
     */
    public ActiveSampler(Builder builder) {
        super(builder);
    }

    /**
     * 获取构建器实例
     *
     * @return ActiveSampler.Builder 构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 创建测试结果实例
     *
     * @return DefaultSampleResult 测试结果实例
     */
    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.id, runtime.title);
    }

    /**
     * 执行消息发送操作
     *
     * @param context 上下文包装器
     * @param result  测试结果
     */
    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        Active.execute(runtime.config, factory, message, result);
    }

    /**
     * 处理请求前的配置和准备工作
     * <p>
     * 主要完成以下操作：
     * <ol>
     *   <li>合并本地配置和全局配置</li>
     *   <li>创建ActiveMQ 连接池;</li>
     *   <li>处理消息内容格式</li>
     *   <li>构建请求信息</li>
     * </ol>
     * </p>
     *
     * @param context 上下文包装器
     * @param result  测试结果
     */
    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        // 1. 合并配置项
        var localConfig = Objects.isNull(runtime.getConfig()) ? new ActiveConfigureItem() : runtime.getConfig();
        var ref = StringUtils.isBlank(localConfig.getRef()) ? DEF_REF_NAME_KEY : localConfig.getRef();
        var otherConfig = (ActiveConfigureItem) context.getLocalVariablesWrapper().get(ref);
        runtime.setConfig(localConfig.merge(otherConfig));
        // 2. 创建ActiveMQ 连接池;
        factory = new ActiveMQConnectionFactory(runtime.config.getUsername(), runtime.config.getPassword(), runtime.config.getBrokerUrl());
        message = switch (config.getMessage()) {
            case Number number -> number.toString();
            case Boolean bool -> bool.toString();
            case null -> "";
            default -> JSON.toJSONString(config.getMessage());
        };
        result.setRequest(RealActiveRequest.build(runtime.getConfig(), message));
    }

    /**
     * 处理响应后的工作
     * <p>
     * 主要完成以下操作：
     * <ol>
     *   <li>设置空的响应结果（ActiveMQ发送操作本身不返回响应内容）</li>
     *   <li>释放连接工厂资源</li>
     * </ol>
     * </p>
     *
     * @param context 上下文包装器
     * @param result  测试结果
     */
    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setResponse(SampleResult.DefaultReal.build(new byte[0]));
        factory = null;
    }

    /**
     * ActiveSampler构建器类
     * <p>
     * 提供构建ActiveSampler实例的方法，包括配置构建器、前置处理器构建器、
     * 后置处理器构建器、断言构建器和变量提取器构建器。
     * </p>
     */
    public static class Builder extends AbstractSampler.Builder<ActiveSampler, Builder, ActiveConfigureItem,
            ActiveConfigureItem.Builder, ActiveConfigureElementsBuilder, ActivePreprocessorsBuilder, ActivePostprocessorsBuilder,
            DefaultAssertionsBuilder, DefaultExtractorsBuilder, DefaultSampleResult> {
        /**
         * 构建ActiveSampler实例
         *
         * @return ActiveSampler实例
         */
        @Override
        public ActiveSampler build() {
            return new ActiveSampler(this);
        }

        /**
         * 获取断言构建器
         *
         * @return DefaultAssertionsBuilder 断言构建器
         */
        @Override
        protected DefaultAssertionsBuilder getAssertionsBuilder() {
            return DefaultAssertionsBuilder.builder();
        }

        /**
         * 获取变量提取器构建器
         *
         * @return DefaultExtractorsBuilder 变量提取器构建器
         */
        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        /**
         * 获取配置元件构建器
         *
         * @return ActiveConfigureElementsBuilder 配置元件构建器
         */
        @Override
        protected ActiveConfigureElementsBuilder getConfiguresBuilder() {
            return ActiveConfigureElementsBuilder.builder();
        }

        /**
         * 获取前置处理器构建器
         *
         * @return ActivePreprocessorsBuilder 前置处理器构建器
         */
        @Override
        protected ActivePreprocessorsBuilder getPreprocessorsBuilder() {
            return ActivePreprocessorsBuilder.builder();
        }

        /**
         * 获取后置处理器构建器
         *
         * @return ActivePostprocessorsBuilder 后置处理器构建器
         */
        @Override
        protected ActivePostprocessorsBuilder getPostprocessorsBuilder() {
            return ActivePostprocessorsBuilder.builder();
        }

        /**
         * 获取配置项构建器
         *
         * @return ActiveConfigureItem.Builder 配置项构建器
         */
        @Override
        protected ActiveConfigureItem.Builder getConfigureItemBuilder() {
            return ActiveConfigureItem.builder();
        }
    }
}