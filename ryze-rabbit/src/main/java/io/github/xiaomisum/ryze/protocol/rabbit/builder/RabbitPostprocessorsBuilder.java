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

package io.github.xiaomisum.ryze.protocol.rabbit.builder;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.builder.ExtensiblePostprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.rabbit.processor.RabbitPostprocessor;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * RabbitMQ 自定义后置处理器列表构建器
 * <p>
 * 该类提供 RabbitMQ 自定义后置处理器列表的构建方法，用于构建和管理一组 RabbitMQ 后置处理器。
 * 支持多种方式添加后置处理器，包括直接添加、使用构建器、使用自定义器和使用 Groovy Closure。
 * </p>
 *
 * @author xiaomi
 */
public class RabbitPostprocessorsBuilder extends ExtensiblePostprocessorsBuilder<RabbitPostprocessorsBuilder, DefaultExtractorsBuilder> {

    /**
     * 创建 RabbitPostprocessorsBuilder 实例
     * <p>
     * 工厂方法，用于创建 RabbitPostprocessorsBuilder 构建器实例。
     * </p>
     *
     * @return RabbitPostprocessorsBuilder 实例
     */
    public static RabbitPostprocessorsBuilder builder() {
        return new RabbitPostprocessorsBuilder();
    }

    /**
     * 添加 RabbitPostprocessor 后置处理器
     * <p>
     * 直接添加已构建好的 RabbitPostprocessor 后置处理器到后置处理器列表中。
     * </p>
     *
     * @param postprocessor RabbitPostprocessor 后置处理器实例
     * @return 当前构建器实例，支持链式调用
     */
    public RabbitPostprocessorsBuilder rabbit(RabbitPostprocessor postprocessor) {
        this.postprocessors.add(postprocessor);
        return self;
    }

    /**
     * 添加通过构建器创建的 RabbitPostprocessor 后置处理器
     * <p>
     * 使用 RabbitPostprocessor.Builder 构建器创建后置处理器并添加到后置处理器列表中。
     * </p>
     *
     * @param builder RabbitPostprocessor.Builder 构建器实例
     * @return 当前构建器实例，支持链式调用
     */
    public RabbitPostprocessorsBuilder rabbit(RabbitPostprocessor.Builder builder) {
        this.postprocessors.add(builder.build());
        return self;
    }

    /**
     * 使用自定义器添加 RabbitPostprocessor 后置处理器
     * <p>
     * 通过 Customizer 自定义 RabbitPostprocessor.Builder 并构建后置处理器，
     * 然后添加到后置处理器列表中。
     * </p>
     *
     * @param customizer Customizer，用于自定义 RabbitPostprocessor.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public RabbitPostprocessorsBuilder rabbit(Customizer<RabbitPostprocessor.Builder> customizer) {
        var builder = RabbitPostprocessor.builder();
        customizer.customize(builder);
        this.postprocessors.add(builder.build());
        return self;
    }

    /**
     * 使用 Groovy Closure 添加 RabbitPostprocessor 后置处理器
     * <p>
     * 通过 Groovy Closure 自定义 RabbitPostprocessor.Builder 并构建后置处理器，
     * 然后添加到后置处理器列表中。
     * </p>
     *
     * @param closure Groovy Closure，用于自定义 RabbitPostprocessor.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public RabbitPostprocessorsBuilder rabbit(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RabbitPostprocessor.Builder.class) Closure<?> closure) {
        var builder = RabbitPostprocessor.builder();
        call(closure, builder);
        this.postprocessors.add(builder.build());
        return self;
    }
}