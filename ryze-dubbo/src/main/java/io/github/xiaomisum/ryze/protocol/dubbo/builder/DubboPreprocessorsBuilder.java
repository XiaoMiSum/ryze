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
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.builder.ExtensiblePreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.dubbo.processor.DubboPreprocessor;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * Dubbo前置处理器列表构建器
 * <p>
 * 该类用于构建Dubbo协议相关的前置处理器列表，提供多种方式添加Dubbo前置处理器。
 * 它继承自ExtensiblePreprocessorsBuilder，支持链式调用和多种配置方式。
 * </p>
 * <p>
 * 主要功能包括：
 * 1. 支持直接添加DubboPreprocessor前置处理器对象
 * 2. 支持通过Customizer函数式接口自定义前置处理器
 * 3. 支持通过Builder构建器添加前置处理器
 * 4. 支持通过Groovy闭包方式添加前置处理器
 * </p>
 * <p>
 * 在测试框架中，该构建器用于收集和管理Dubbo相关的前置处理器，
 * 这些处理器会在测试用例执行前按顺序执行。
 * </p>
 *
 * @author xiaomi
 */
public class DubboPreprocessorsBuilder extends ExtensiblePreprocessorsBuilder<DubboPreprocessorsBuilder, DefaultExtractorsBuilder> {


    /**
     * 创建Dubbo前置处理器列表构建器实例
     *
     * @return Dubbo前置处理器列表构建器实例
     */
    public static DubboPreprocessorsBuilder builder() {
        return new DubboPreprocessorsBuilder();
    }

    /**
     * 添加Dubbo前置处理器对象到前置处理器列表中
     * <p>
     * 该方法直接将已构建好的DubboPreprocessor对象添加到前置处理器列表中。
     * </p>
     *
     * @param preprocessor 已构建好的Dubbo前置处理器对象
     * @return 当前构建器实例，支持链式调用
     */
    public DubboPreprocessorsBuilder dubbo(DubboPreprocessor preprocessor) {
        preprocessors.add(preprocessor);
        return self;
    }

    /**
     * 通过自定义函数添加Dubbo前置处理器到前置处理器列表中
     * <p>
     * 该方法使用Customizer函数式接口来自定义Dubbo前置处理器，
     * 通过回调方式配置DubboPreprocessor.Builder，然后构建并添加到前置处理器列表中。
     * </p>
     *
     * @param customizer 自定义配置函数，用于配置DubboPreprocessor.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public DubboPreprocessorsBuilder dubbo(Customizer<DubboPreprocessor.Builder> customizer) {
        var builder = DubboPreprocessor.builder();
        customizer.customize(builder);
        preprocessors.add(builder.build());
        return self;
    }

    /**
     * 通过构建器添加Dubbo前置处理器到前置处理器列表中
     * <p>
     * 该方法接收一个已配置的DubboPreprocessor.Builder对象，
     * 构建DubboPreprocessor实例并添加到前置处理器列表中。
     * </p>
     *
     * @param builder Dubbo前置处理器构建器
     * @return 当前构建器实例，支持链式调用
     */
    public DubboPreprocessorsBuilder dubbo(DubboPreprocessor.Builder builder) {
        preprocessors.add(builder.build());
        return self;
    }

    /**
     * 通过Groovy闭包添加Dubbo前置处理器到前置处理器列表中
     * <p>
     * 该方法使用Groovy闭包语法来自定义Dubbo前置处理器，
     * 通过Groovy.call方法执行闭包配置DubboPreprocessor.Builder，
     * 然后构建并添加到前置处理器列表中。
     * </p>
     *
     * @param closure Groovy闭包，用于配置DubboPreprocessor.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public DubboPreprocessorsBuilder dubbo(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DubboPreprocessor.Builder.class) Closure<?> closure) {
        var builder = DubboPreprocessor.builder();
        call(closure, builder);
        preprocessors.add(builder.build());
        return self;
    }
}