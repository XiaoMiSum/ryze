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
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.builder.ExtensiblePostprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.kafka.processor.KafkaPostprocessor;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * Kafka后置处理器构建器类，用于构建Kafka测试场景中的后置处理器列表
 * <p>
 * 该类继承自ExtensiblePostprocessorsBuilder，提供了构建Kafka后置处理器的便捷方法，
 * 支持多种构建方式，包括直接传入后置处理器对象、使用构建器模式、使用自定义函数式接口或Groovy闭包。
 * </p>
 * <p>
 * 后置处理器在Kafka操作执行后运行，通常用于处理响应数据、提取变量或进行断言检查。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>提供多种方式添加Kafka后置处理器</li>
 *   <li>支持函数式编程风格</li>
 *   <li>支持Groovy DSL语法</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @see ExtensiblePostprocessorsBuilder
 * @see KafkaPostprocessor
 */
public class KafkaPostprocessorsBuilder extends ExtensiblePostprocessorsBuilder<KafkaPostprocessorsBuilder, DefaultExtractorsBuilder> {

    /**
     * 创建KafkaPostprocessorsBuilder实例的静态工厂方法
     *
     * @return 新创建的KafkaPostprocessorsBuilder实例
     */
    public static KafkaPostprocessorsBuilder builder() {
        return new KafkaPostprocessorsBuilder();
    }

    /**
     * 添加Kafka后置处理器到构建器中
     *
     * @param postprocessor 已构建的Kafka后置处理器对象
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaPostprocessorsBuilder kafka(KafkaPostprocessor postprocessor) {
        this.postprocessors.add(postprocessor);
        return self;
    }

    /**
     * 使用构建器添加Kafka后置处理器到构建器中
     *
     * @param builder Kafka后置处理器构建器
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaPostprocessorsBuilder kafka(KafkaPostprocessor.Builder builder) {
        this.postprocessors.add(builder.build());
        return self;
    }

    /**
     * 使用自定义函数式接口添加Kafka后置处理器到构建器中
     *
     * @param customizer Kafka后置处理器构建器的自定义函数式接口
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaPostprocessorsBuilder kafka(Customizer<KafkaPostprocessor.Builder> customizer) {
        var builder = KafkaPostprocessor.builder();
        customizer.customize(builder);
        this.postprocessors.add(builder.build());
        return self;
    }

    /**
     * 使用Groovy闭包添加Kafka后置处理器到构建器中
     *
     * @param closure Groovy闭包，用于配置Kafka后置处理器构建器
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaPostprocessorsBuilder kafka(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = KafkaPostprocessor.Builder.class) Closure<?> closure) {
        var builder = KafkaPostprocessor.builder();
        call(closure, builder);
        this.postprocessors.add(builder.build());
        return self;
    }
}