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
import io.github.xiaomisum.ryze.builder.ExtensibleChildrenBuilder;
import io.github.xiaomisum.ryze.protocol.dubbo.sampler.DubboSampler;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * Dubbo取样器列表构建器
 * <p>
 * 该类用于构建Dubbo协议相关的取样器列表，提供多种方式添加Dubbo取样器。
 * 它继承自ExtensibleChildrenBuilder，支持链式调用和多种配置方式。
 * </p>
 * <p>
 * 主要功能包括：
 * 1. 支持直接添加DubboSampler取样器对象
 * 2. 支持通过Customizer函数式接口自定义取样器
 * 3. 支持通过Builder构建器添加取样器
 * 4. 支持通过Groovy闭包方式添加取样器
 * </p>
 * <p>
 * 在测试框架中，该构建器用于收集和管理Dubbo相关的取样器，
 * 这些取样器是测试用例的核心执行单元，负责实际调用Dubbo服务。
 * </p>
 *
 * @author xiaomi
 */
public class DubboSamplersBuilder extends ExtensibleChildrenBuilder<DubboSamplersBuilder> {

    /**
     * 创建Dubbo取样器列表构建器实例
     *
     * @return Dubbo取样器列表构建器实例
     */
    public static DubboSamplersBuilder builder() {
        return new DubboSamplersBuilder();
    }

    /**
     * 添加Dubbo取样器对象到取样器列表中
     * <p>
     * 该方法直接将已构建好的DubboSampler对象添加到取样器列表中。
     * </p>
     *
     * @param child 已构建好的Dubbo取样器对象
     * @return 当前构建器实例，支持链式调用
     */
    public DubboSamplersBuilder dubbo(DubboSampler child) {
        this.children.add(child);
        return self;
    }

    /**
     * 通过构建器添加Dubbo取样器到取样器列表中
     * <p>
     * 该方法接收一个已配置的DubboSampler.Builder对象，
     * 构建DubboSampler实例并添加到取样器列表中。
     * </p>
     *
     * @param child Dubbo取样器构建器
     * @return 当前构建器实例，支持链式调用
     */
    public DubboSamplersBuilder dubbo(DubboSampler.Builder child) {
        this.children.add(child.build());
        return self;
    }

    /**
     * 通过自定义函数添加Dubbo取样器到取样器列表中
     * <p>
     * 该方法使用Customizer函数式接口来自定义Dubbo取样器，
     * 通过回调方式配置DubboSampler.Builder，然后构建并添加到取样器列表中。
     * </p>
     *
     * @param customizer 自定义配置函数，用于配置DubboSampler.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public DubboSamplersBuilder dubbo(Customizer<DubboSampler.Builder> customizer) {
        var builder = DubboSampler.builder();
        customizer.customize(builder);
        this.children.add(builder.build());
        return self;
    }

    /**
     * 通过Groovy闭包添加Dubbo取样器到取样器列表中
     * <p>
     * 该方法使用Groovy闭包语法来自定义Dubbo取样器，
     * 通过Groovy.call方法执行闭包配置DubboSampler.Builder，
     * 然后构建并添加到取样器列表中。
     * </p>
     *
     * @param closure Groovy闭包，用于配置DubboSampler.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public DubboSamplersBuilder dubbo(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DubboSampler.Builder.class) Closure<?> closure) {
        var builder = DubboSampler.builder();
        call(closure, builder);
        this.children.add(builder.build());
        return self;
    }
}