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

package io.github.xiaomisum.ryze.protocol.kafka.config;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import io.github.xiaomisum.ryze.protocol.kafka.KafkaConstantsInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Kafka默认配置类，用于定义Kafka测试的全局默认配置
 * <p>
 * 该类继承自AbstractConfigureElement，实现了KafkaConstantsInterface接口，
 * 用于设置和管理Kafka测试的全局默认配置参数。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>定义Kafka测试的全局默认配置</li>
 *   <li>处理配置合并逻辑</li>
 *   <li>管理配置引用名称</li>
 * </ul>
 * </p>
 * <p>
 * 业务处理逻辑：
 * <ol>
 *   <li>初始化测试结果对象</li>
 *   <li>处理阶段：设置默认引用名称，合并配置项</li>
 *   <li>将配置存储到测试上下文中供其他组件使用</li>
 * </ol>
 * </p>
 *
 * @author xiaomi
 * @since 2021/11/11 11:06
 * @see AbstractConfigureElement
 * @see KafkaConstantsInterface
 */
@KW({"kafka_defaults", "KafkaDefault", "Kafka_Default", "kafka"})
public class KafkaDefaults extends AbstractConfigureElement<KafkaDefaults, KafkaConfigureItem, TestSuiteResult> implements KafkaConstantsInterface {

    /**
     * 使用构建器创建KafkaDefaults实例的构造函数
     *
     * @param builder KafkaDefaults构建器
     */
    public KafkaDefaults(Builder builder) {
        super(builder);
    }

    /**
     * 默认构造函数
     */
    public KafkaDefaults() {
        super();
    }

    /**
     * 创建KafkaDefaults构建器的静态工厂方法
     *
     * @return Builder构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 处理配置阶段，设置默认引用名称并合并配置项
     *
     * @param context 测试上下文包装器
     */
    @Override
    protected void doProcess(ContextWrapper context) {
        refName = StringUtils.isBlank(refName) ? DEF_REF_NAME_KEY : refName;
        var localConfig = runtime.getConfig();
        var otherRefName = StringUtils.isBlank(localConfig.getRef()) ? DEF_REF_NAME_KEY : localConfig.getRef();
        var config = (KafkaConfigureItem) context.getSessionRunner().getContext().getLocalVariablesWrapper().get(otherRefName);
        if (Objects.nonNull(config)) {
            runtime.setConfig(localConfig = localConfig.merge(config));
        }
        context.getSessionRunner().getContext().getLocalVariablesWrapper().put(refName, localConfig);
    }


    /**
     * 获取测试结果对象
     *
     * @return TestSuiteResult测试结果对象
     */
    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult("Kafka 默认配置");
    }

    /**
     * Kafka默认配置 测试元件 构建类
     * <p>
     * 用于构建KafkaDefaults实例的构建器类，继承自AbstractConfigureElement.Builder。
     * </p>
     */
    public static class Builder extends AbstractConfigureElement.Builder<KafkaDefaults, Builder, KafkaConfigureItem, KafkaConfigureItem.Builder, TestSuiteResult> {

        /**
         * 构建KafkaDefaults实例
         *
         * @return KafkaDefaults实例
         */
        @Override
        public KafkaDefaults build() {
            return new KafkaDefaults(this);
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