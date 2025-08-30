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

package io.github.xiaomisum.ryze.protocol.dubbo.builder;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.builder.ExtensibleConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.dubbo.config.DubboDefaults;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * Dubbo配置元件列表构建器
 * <p>
 * 该类用于构建Dubbo协议相关的配置元件列表，提供多种方式添加Dubbo配置元件。
 * 它继承自ExtensibleConfigureElementsBuilder，支持链式调用和多种配置方式。
 * </p>
 * <p>
 * 主要功能包括：
 * 1. 支持直接添加DubboDefaults配置对象
 * 2. 支持通过Customizer函数式接口自定义配置
 * 3. 支持通过Builder构建器添加配置
 * 4. 支持通过Groovy闭包方式添加配置
 * </p>
 * <p>
 * 在测试框架中，该构建器用于收集和管理Dubbo相关的默认配置，
 * 这些配置可以在测试执行过程中被引用和合并使用。
 * </p>
 *
 * @author xiaomi
 */
public class DubboConfigureElementsBuilder extends ExtensibleConfigureElementsBuilder<DubboConfigureElementsBuilder> {

    /**
     * 创建Dubbo配置元件列表构建器实例
     *
     * @return Dubbo配置元件列表构建器实例
     */
    public static DubboConfigureElementsBuilder builder() {
        return new DubboConfigureElementsBuilder();
    }

    /**
     * 添加Dubbo默认配置对象到配置元件列表中
     * <p>
     * 该方法直接将已构建好的DubboDefaults对象添加到配置元件列表中。
     * </p>
     *
     * @param defaults 已构建好的Dubbo默认配置对象
     * @return 当前构建器实例，支持链式调用
     */
    public DubboConfigureElementsBuilder dubbo(DubboDefaults defaults) {
        configureElements.add(defaults);
        return self;
    }

    /**
     * 通过自定义函数添加Dubbo配置到配置元件列表中
     * <p>
     * 该方法使用Customizer函数式接口来自定义Dubbo配置，
     * 通过回调方式配置DubboDefaults.Builder，然后构建并添加到配置元件列表中。
     * </p>
     *
     * @param customizer 自定义配置函数，用于配置DubboDefaults.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public DubboConfigureElementsBuilder dubbo(Customizer<DubboDefaults.Builder> customizer) {
        var builder = DubboDefaults.builder();
        customizer.customize(builder);
        configureElements.add(builder.build());
        return self;
    }

    /**
     * 通过构建器添加Dubbo配置到配置元件列表中
     * <p>
     * 该方法接收一个已配置的DubboDefaults.Builder对象，
     * 构建DubboDefaults实例并添加到配置元件列表中。
     * </p>
     *
     * @param builder Dubbo默认配置构建器
     * @return 当前构建器实例，支持链式调用
     */
    public DubboConfigureElementsBuilder dubbo(DubboDefaults.Builder builder) {
        configureElements.add(builder.build());
        return self;
    }

    /**
     * 通过Groovy闭包添加Dubbo配置到配置元件列表中
     * <p>
     * 该方法使用Groovy闭包语法来自定义Dubbo配置，
     * 通过Groovy.call方法执行闭包配置DubboDefaults.Builder，
     * 然后构建并添加到配置元件列表中。
     * </p>
     *
     * @param closure Groovy闭包，用于配置DubboDefaults.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public DubboConfigureElementsBuilder dubbo(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DubboDefaults.Builder.class) Closure<?> closure) {
        var builder = DubboDefaults.builder();
        call(closure, builder);
        configureElements.add(builder.build());
        return self;
    }
}