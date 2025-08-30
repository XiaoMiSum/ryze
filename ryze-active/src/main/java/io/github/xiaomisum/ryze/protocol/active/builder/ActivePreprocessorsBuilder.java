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

package io.github.xiaomisum.ryze.protocol.active.builder;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.builder.ExtensiblePreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.active.processor.ActivePreprocessor;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * ActiveMQ自定义前置处理器列表构建器
 * <p>
 * 提供构建ActiveMQ自定义前置处理器列表的多种方式：
 * 1. 直接添加已构建的ActivePreprocessor实例
 * 2. 通过Customizer函数式接口配置并构建ActivePreprocessor实例
 * 3. 通过ActivePreprocessor.Builder实例构建并添加
 * 4. 通过Groovy闭包配置并构建ActivePreprocessor实例
 * </p>
 *
 * @author xiaomi
 */
public class ActivePreprocessorsBuilder extends ExtensiblePreprocessorsBuilder<ActivePreprocessorsBuilder, DefaultExtractorsBuilder> {

    /**
     * 创建ActivePreprocessorsBuilder实例
     *
     * @return 新的ActivePreprocessorsBuilder实例
     */
    public static ActivePreprocessorsBuilder builder() {
        return new ActivePreprocessorsBuilder();
    }

    /**
     * 添加已构建的ActivePreprocessor前置处理器
     *
     * @param preprocessor 已构建的ActivePreprocessor实例
     * @return 当前构建器实例，支持链式调用
     */
    public ActivePreprocessorsBuilder active(ActivePreprocessor preprocessor) {
        preprocessors.add(preprocessor);
        return self;
    }

    /**
     * 通过Customizer函数式接口配置并构建ActivePreprocessor实例
     * <p>
     * 执行流程：
     * 1. 创建ActivePreprocessor.Builder实例
     * 2. 使用Customizer配置Builder
     * 3. 构建ActivePreprocessor实例并添加到前置处理器列表
     * </p>
     *
     * @param customizer Customizer函数式接口，用于配置ActivePreprocessor.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public ActivePreprocessorsBuilder active(Customizer<ActivePreprocessor.Builder> customizer) {
        var builder = ActivePreprocessor.builder();
        customizer.customize(builder);
        preprocessors.add(builder.build());
        return self;
    }

    /**
     * 通过ActivePreprocessor.Builder实例构建并添加前置处理器
     *
     * @param builder ActivePreprocessor.Builder实例
     * @return 当前构建器实例，支持链式调用
     */
    public ActivePreprocessorsBuilder active(ActivePreprocessor.Builder builder) {
        preprocessors.add(builder.build());
        return self;
    }

    /**
     * 通过Groovy闭包配置并构建ActivePreprocessor实例
     * <p>
     * 执行流程：
     * 1. 创建ActivePreprocessor.Builder实例
     * 2. 使用Groovy闭包配置Builder
     * 3. 构建ActivePreprocessor实例并添加到前置处理器列表
     * </p>
     *
     * @param closure Groovy闭包，用于配置ActivePreprocessor.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public ActivePreprocessorsBuilder active(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ActivePreprocessor.Builder.class) Closure<?> closure) {
        var builder = ActivePreprocessor.builder();
        call(closure, builder);
        preprocessors.add(builder.build());
        return self;
    }
}