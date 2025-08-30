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
import io.github.xiaomisum.ryze.builder.ExtensibleChildrenBuilder;
import io.github.xiaomisum.ryze.protocol.kafka.sampler.KafkaSampler;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * Kafka取样器构建器类，用于构建Kafka测试场景中的取样器列表
 * <p>
 * 该类继承自ExtensibleChildrenBuilder，提供了构建Kafka取样器的便捷方法，
 * 支持多种构建方式，包括直接传入取样器对象、使用构建器模式、使用自定义函数式接口或Groovy闭包。
 * </p>
 * <p>
 * 取样器是Kafka测试的核心组件，负责执行实际的Kafka消息发送操作。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>提供多种方式添加Kafka取样器</li>
 *   <li>支持函数式编程风格</li>
 *   <li>支持Groovy DSL语法</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @see ExtensibleChildrenBuilder
 * @see KafkaSampler
 */
public class KafkaSamplersBuilder extends ExtensibleChildrenBuilder<KafkaSamplersBuilder> {

    /**
     * 创建KafkaSamplersBuilder实例的静态工厂方法
     *
     * @return 新创建的KafkaSamplersBuilder实例
     */
    public static KafkaSamplersBuilder builder() {
        return new KafkaSamplersBuilder();
    }

    /**
     * 添加Kafka取样器到构建器中
     *
     * @param child 已构建的Kafka取样器对象
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaSamplersBuilder kafka(KafkaSampler child) {
        this.children.add(child);
        return self;
    }

    /**
     * 使用构建器添加Kafka取样器到构建器中
     *
     * @param child Kafka取样器构建器
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaSamplersBuilder kafka(KafkaSampler.Builder child) {
        this.children.add(child.build());
        return self;
    }

    /**
     * 使用自定义函数式接口添加Kafka取样器到构建器中
     *
     * @param customizer Kafka取样器构建器的自定义函数式接口
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaSamplersBuilder kafka(Customizer<KafkaSampler.Builder> customizer) {
        var builder = KafkaSampler.builder();
        customizer.customize(builder);
        this.children.add(builder.build());
        return self;
    }

    /**
     * 使用Groovy闭包添加Kafka取样器到构建器中
     *
     * @param closure Groovy闭包，用于配置Kafka取样器构建器
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaSamplersBuilder kafka(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = KafkaSampler.Builder.class) Closure<?> closure) {
        var builder = KafkaSampler.builder();
        call(closure, builder);
        this.children.add(builder.build());
        return self;
    }
}