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
import io.github.xiaomisum.ryze.builder.ExtensibleConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.rabbit.config.RabbitDefaults;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * RabbitMQ 自定义配置元件列表构建器
 * <p>
 * 该类提供 RabbitMQ 自定义配置元件列表的构建方法，用于构建和管理一组 RabbitMQ 配置元件。
 * 支持多种方式添加配置元件，包括直接添加、使用构建器、使用自定义器和使用 Groovy Closure。
 * </p>
 *
 * @author xiaomi
 */
public class RabbitConfigureElementsBuilder extends ExtensibleConfigureElementsBuilder<RabbitConfigureElementsBuilder> {

    /**
     * 创建 RabbitConfigureElementsBuilder 实例
     * <p>
     * 工厂方法，用于创建 RabbitConfigureElementsBuilder 构建器实例。
     * </p>
     *
     * @return RabbitConfigureElementsBuilder 实例
     */
    public static RabbitConfigureElementsBuilder builder() {
        return new RabbitConfigureElementsBuilder();
    }

    /**
     * 添加 RabbitDefaults 配置元件
     * <p>
     * 直接添加已构建好的 RabbitDefaults 配置元件到配置元件列表中。
     * </p>
     *
     * @param child RabbitDefaults 配置元件实例
     * @return 当前构建器实例，支持链式调用
     */
    public RabbitConfigureElementsBuilder rabbit(RabbitDefaults child) {
        this.configureElements.add(child);
        return self;
    }

    /**
     * 添加通过构建器创建的 RabbitDefaults 配置元件
     * <p>
     * 使用 RabbitDefaults.Builder 构建器创建配置元件并添加到配置元件列表中。
     * </p>
     *
     * @param builder RabbitDefaults.Builder 构建器实例
     * @return 当前构建器实例，支持链式调用
     */
    public RabbitConfigureElementsBuilder rabbit(RabbitDefaults.Builder builder) {
        this.configureElements.add(builder.build());
        return self;
    }

    /**
     * 使用自定义器添加 RabbitDefaults 配置元件
     * <p>
     * 通过 Customizer 自定义 RabbitDefaults.Builder 并构建配置元件，
     * 然后添加到配置元件列表中。
     * </p>
     *
     * @param customizer Customizer，用于自定义 RabbitDefaults.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public RabbitConfigureElementsBuilder rabbit(Customizer<RabbitDefaults.Builder> customizer) {
        var builder = RabbitDefaults.builder();
        customizer.customize(builder);
        this.configureElements.add(builder.build());
        return self;
    }

    /**
     * 使用 Groovy Closure 添加 RabbitDefaults 配置元件
     * <p>
     * 通过 Groovy Closure 自定义 RabbitDefaults.Builder 并构建配置元件，
     * 然后添加到配置元件列表中。
     * </p>
     *
     * @param closure Groovy Closure，用于自定义 RabbitDefaults.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public RabbitConfigureElementsBuilder rabbit(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RabbitDefaults.Builder.class) Closure<?> closure) {
        var builder = RabbitDefaults.builder();
        call(closure, builder);
        this.configureElements.add(builder.build());
        return self;
    }
}