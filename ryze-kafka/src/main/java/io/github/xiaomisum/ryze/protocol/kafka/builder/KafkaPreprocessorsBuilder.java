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
import io.github.xiaomisum.ryze.builder.ExtensiblePreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.kafka.processor.KafkaPreprocessor;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * Kafka前置处理器构建器类，用于构建Kafka测试场景中的前置处理器列表
 * <p>
 * 该类继承自ExtensiblePreprocessorsBuilder，提供了构建Kafka前置处理器的便捷方法，
 * 支持多种构建方式，包括直接传入前置处理器对象、使用构建器模式、使用自定义函数式接口或Groovy闭包。
 * </p>
 * <p>
 * 前置处理器在Kafka操作执行前运行，通常用于准备测试数据、设置变量或进行预处理操作。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>提供多种方式添加Kafka前置处理器</li>
 *   <li>支持函数式编程风格</li>
 *   <li>支持Groovy DSL语法</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @see ExtensiblePreprocessorsBuilder
 * @see KafkaPreprocessor
 */
public class KafkaPreprocessorsBuilder extends ExtensiblePreprocessorsBuilder<KafkaPreprocessorsBuilder, DefaultExtractorsBuilder> {

    /**
     * 创建KafkaPreprocessorsBuilder实例的静态工厂方法
     *
     * @return 新创建的KafkaPreprocessorsBuilder实例
     */
    public static KafkaPreprocessorsBuilder builder() {
        return new KafkaPreprocessorsBuilder();
    }

    /**
     * 添加Kafka前置处理器到构建器中
     *
     * @param preprocessor 已构建的Kafka前置处理器对象
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaPreprocessorsBuilder kafka(KafkaPreprocessor preprocessor) {
        this.preprocessors.add(preprocessor);
        return self;
    }

    /**
     * 使用构建器添加Kafka前置处理器到构建器中
     *
     * @param child Kafka前置处理器构建器
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaPreprocessorsBuilder kafka(KafkaPreprocessor.Builder child) {
        this.preprocessors.add(child.build());
        return self;
    }

    /**
     * 使用自定义函数式接口添加Kafka前置处理器到构建器中
     *
     * @param customizer Kafka前置处理器构建器的自定义函数式接口
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaPreprocessorsBuilder kafka(Customizer<KafkaPreprocessor.Builder> customizer) {
        var builder = KafkaPreprocessor.builder();
        customizer.customize(builder);
        this.preprocessors.add(builder.build());
        return self;
    }

    /**
     * 使用Groovy闭包添加Kafka前置处理器到构建器中
     *
     * @param closure Groovy闭包，用于配置Kafka前置处理器构建器
     * @return 当前构建器实例，支持链式调用
     */
    public KafkaPreprocessorsBuilder kafka(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = KafkaPreprocessor.Builder.class) Closure<?> closure) {
        var builder = KafkaPreprocessor.builder();
        call(closure, builder);
        this.preprocessors.add(builder.build());
        return self;
    }
}