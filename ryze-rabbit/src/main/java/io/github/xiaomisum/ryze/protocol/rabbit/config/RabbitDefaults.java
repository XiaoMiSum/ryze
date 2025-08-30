/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze.protocol.rabbit.config;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import io.github.xiaomisum.ryze.protocol.rabbit.RabbitConstantsInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * RabbitMQ 默认配置元件
 * <p>
 * 该类用于定义 RabbitMQ 测试的默认配置，作为其他 RabbitMQ 测试元件的配置基础。
 * 它继承自 AbstractConfigureElement，实现了 RabbitMQ 配置的处理逻辑。
 * </p>
 *
 * @author mi.xiao
 * @since 2024/11/04 20:38
 */
@KW({"rabbitmq", "rabbit", "rabbit_mq", "rabbit_defaults"})
public class RabbitDefaults extends AbstractConfigureElement<RabbitDefaults, RabbitConfigureItem, TestSuiteResult> implements RabbitConstantsInterface {

    /**
     * 构造函数，使用构建器创建 RabbitDefaults 实例
     *
     * @param builder 构建器实例
     */
    public RabbitDefaults(Builder builder) {
        super(builder);
    }

    /**
     * 默认构造函数
     */
    public RabbitDefaults() {
        super();
    }

    /**
     * 创建构建器实例
     * <p>
     * 工厂方法，用于创建 RabbitDefaults.Builder 构建器实例。
     * </p>
     *
     * @return RabbitDefaults.Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 处理配置元素
     * <p>
     * 该方法在测试执行时被调用，用于处理 RabbitMQ 配置。
     * 它会合并上下文中的配置，并将配置存储在本地变量包装器中供后续使用。
     * </p>
     *
     * @param context 上下文包装器
     */
    @Override
    protected void doProcess(ContextWrapper context) {
        refName = StringUtils.isBlank(refName) ? DEF_REF_NAME_KEY : refName;
        var localConfig = runtime.getConfig();
        var otherRefName = StringUtils.isBlank(localConfig.getRef()) ? DEF_REF_NAME_KEY : localConfig.getRef();
        var config = (RabbitConfigureItem) context.getSessionRunner().getContext().getLocalVariablesWrapper().get(otherRefName);
        if (Objects.nonNull(config)) {
            runtime.setConfig(localConfig = localConfig.merge(config));
        }
        context.getSessionRunner().getContext().getLocalVariablesWrapper().put(refName, localConfig);
    }


    /**
     * 获取测试结果对象
     * <p>
     * 创建并返回用于记录测试结果的对象。
     * </p>
     *
     * @return 测试结果对象
     */
    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult("Rabbit MQ 默认配置");
    }

    /**
     * RabbitMQ 默认配置构建器
     * <p>
     * 用于构建 RabbitMQ 默认配置的内部构建器类。
     * </p>
     */
    public static class Builder extends AbstractConfigureElement.Builder<RabbitDefaults, Builder, RabbitConfigureItem, RabbitConfigureItem.Builder, TestSuiteResult> {

        /**
         * 构建 RabbitDefaults 实例
         *
         * @return RabbitDefaults 实例
         */
        @Override
        public RabbitDefaults build() {
            return new RabbitDefaults(this);
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