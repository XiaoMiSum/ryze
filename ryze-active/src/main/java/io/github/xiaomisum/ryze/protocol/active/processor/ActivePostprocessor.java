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

package io.github.xiaomisum.ryze.protocol.active.processor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Postprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.protocol.active.Active;
import io.github.xiaomisum.ryze.protocol.active.ActiveConstantsInterface;
import io.github.xiaomisum.ryze.protocol.active.RealActiveRequest;
import io.github.xiaomisum.ryze.protocol.active.config.ActiveConfigureItem;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * ActiveMQ后置处理器，在测试执行后发送ActiveMQ消息
 * <p>
 * 该类实现了在测试流程执行后向ActiveMQ发送消息的功能。它继承自AbstractProcessor，
 * 可以作为测试流程中的后置步骤，在主要测试逻辑执行完成后进行清理或通知操作。
 * </p>
 * <p>
 * 主要应用场景：
 * <ul>
 *   <li>测试完成后发送清理消息</li>
 *   <li>通知其他系统测试已完成</li>
 *   <li>发送测试结果到指定队列</li>
 * </ul>
 * </p>
 * <p>
 * 执行流程：
 * <ol>
 *   <li>合并本地配置和全局配置</li>
 *   <li>创建ActiveMQ连接工厂</li>
 *   <li>处理消息内容格式</li>
 *   <li>构建请求信息</li>
 *   <li>执行消息发送</li>
 *   <li>设置测试结果</li>
 *   <li>释放资源</li>
 * </ol>
 * </p>
 *
 * @author mi.xiao
 * @since 2021/4/13 20:08
 */
@KW({"active_mq", "activemq", "active", "active_postprocessor"})
@SuppressWarnings({"unchecked", "rawtypes"})
public class ActivePostprocessor extends AbstractProcessor<ActivePostprocessor, ActiveConfigureItem, DefaultSampleResult> implements Postprocessor, ActiveConstantsInterface {

    @JSONField(serialize = false)
    private ConnectionFactory factory;

    @JSONField(serialize = false)
    private String message;

    /**
     * 默认构造函数
     */
    public ActivePostprocessor() {
        super();
    }

    /**
     * 使用构建器构造ActivePostprocessor实例
     *
     * @param builder 构建器实例
     */
    public ActivePostprocessor(Builder builder) {
        super(builder);
    }

    /**
     * 获取构建器实例
     *
     * @return ActivePostprocessor.Builder 构建器实例
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
        return new DefaultSampleResult(runtime.getId(), StringUtils.isBlank(runtime.getTitle()) ? "Active 后置处理器" : runtime.getTitle());
    }

    /**
     * 执行消息发送操作
     *
     * @param context 上下文包装器
     * @param result  测试结果
     */
    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        Active.execute(runtime.getConfig(), factory, message, result);
    }


    /**
     * 处理请求前的配置和准备工作
     * <p>
     * 主要完成以下操作：
     * <ol>
     *   <li>合并本地配置和全局配置</li>
     *   <li>创建ActiveMQ连接工厂</li>
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
        factory = new ActiveMQConnectionFactory(runtime.getConfig().getUsername(), runtime.getConfig().getPassword(), runtime.getConfig().getBrokerUrl());
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
     * ActivePostprocessor构建器类
     * <p>
     * 提供构建ActivePostprocessor实例的方法，包括配置构建器和变量提取器构建器。
     * </p>
     */
    public static class Builder extends PostprocessorBuilder<ActivePostprocessor, Builder, ActiveConfigureItem,
            ActiveConfigureItem.Builder, DefaultExtractorsBuilder, DefaultSampleResult> {
        /**
         * 构建ActivePostprocessor实例
         *
         * @return ActivePostprocessor实例
         */
        @Override
        public ActivePostprocessor build() {
            return new ActivePostprocessor(this);
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