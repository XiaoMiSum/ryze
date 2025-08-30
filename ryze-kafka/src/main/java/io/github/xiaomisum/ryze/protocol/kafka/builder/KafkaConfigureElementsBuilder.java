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

package io.github.xiaomisum.ryze.protocol.kafka.builder;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.builder.ExtensibleConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.kafka.config.KafkaDefaults;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * Kafka配置元件构建器类，用于构建Kafka测试场景中的配置元件列表
 * <p>
 * 该类继承自ExtensibleConfigureElementsBuilder，提供了构建Kafka配置元件的便捷方法，
 * 支持多种构建方式，包括直接传入配置对象、使用构建器模式、使用自定义函数式接口或Groovy闭包。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>提供多种方式添加Kafka配置元件</li>
 *   <li>支持函数式编程风格</li>
 *   <li>支持Groovy DSL语法</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @see ExtensibleConfigureElementsBuilder
 * @see KafkaDefaults
 */
public class KafkaConfigureElementsBuilder extends ExtensibleConfigureElementsBuilder<KafkaConfigureElementsBuilder> {

    /**
     * 创建KafkaConfigureElementsBuilder实例的静态工厂方法
     *
     * @return 新创建的KafkaConfigureElementsBuilder实例
     */
    public static KafkaConfigureElementsBuilder builder() {
        return new KafkaConfigureElementsBuilder();
    }

    /**
     * 添加Kafka配置元件到构建器中
     *
     * @param defaults 已构建的Kafka默认配置对象
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaConfigureElementsBuilder kafka(KafkaDefaults defaults) {
        this.configureElements.add(defaults);
        return self;
    }

    /**
     * 使用构建器添加Kafka配置元件到构建器中
     *
     * @param builder Kafka默认配置构建器
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaConfigureElementsBuilder kafka(KafkaDefaults.Builder builder) {
        this.configureElements.add(builder.build());
        return self;
    }

    /**
     * 使用自定义函数式接口添加Kafka配置元件到构建器中
     *
     * @param customizer Kafka默认配置构建器的自定义函数式接口
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaConfigureElementsBuilder kafka(Customizer<KafkaDefaults.Builder> customizer) {
        var builder = KafkaDefaults.builder();
        customizer.customize(builder);
        this.configureElements.add(builder.build());
        return self;
    }

    /**
     * 使用Groovy闭包添加Kafka配置元件到构建器中
     *
     * @param closure Groovy闭包，用于配置Kafka默认配置构建器
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaConfigureElementsBuilder kafka(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = KafkaDefaults.Builder.class) Closure<?> closure) {
        var builder = KafkaDefaults.builder();
        call(closure, builder);
        this.configureElements.add(builder.build());
        return self;
    }
}