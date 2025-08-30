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
import io.github.xiaomisum.ryze.builder.ExtensibleConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.active.config.ActiveDefaults;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * ActiveMQ自定义配置元件列表构建器
 * <p>
 * 提供构建ActiveMQ自定义配置元件列表的多种方式：
 * 1. 直接添加已构建的ActiveDefaults实例
 * 2. 通过Customizer函数式接口配置并构建ActiveDefaults实例
 * 3. 通过ActiveDefaults.Builder实例构建并添加
 * 4. 通过Groovy闭包配置并构建ActiveDefaults实例
 * </p>
 *
 * @author xiaomi
 */
public class ActiveConfigureElementsBuilder extends ExtensibleConfigureElementsBuilder<ActiveConfigureElementsBuilder> {

    /**
     * 创建ActiveConfigureElementsBuilder实例
     *
     * @return 新的ActiveConfigureElementsBuilder实例
     */
    public static ActiveConfigureElementsBuilder builder() {
        return new ActiveConfigureElementsBuilder();
    }

    /**
     * 添加已构建的ActiveDefaults配置元件
     *
     * @param defaults 已构建的ActiveDefaults实例
     * @return 当前构建器实例，支持链式调用
     */
    public ActiveConfigureElementsBuilder active(ActiveDefaults defaults) {
        configureElements.add(defaults);
        return self;
    }

    /**
     * 通过Customizer函数式接口配置并构建ActiveDefaults实例
     * <p>
     * 执行流程：
     * 1. 创建ActiveDefaults.Builder实例
     * 2. 使用Customizer配置Builder
     * 3. 构建ActiveDefaults实例并添加到配置元件列表
     * </p>
     *
     * @param customizer Customizer函数式接口，用于配置ActiveDefaults.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public ActiveConfigureElementsBuilder active(Customizer<ActiveDefaults.Builder> customizer) {
        var builder = ActiveDefaults.builder();
        customizer.customize(builder);
        configureElements.add(builder.build());
        return self;
    }

    /**
     * 通过ActiveDefaults.Builder实例构建并添加配置元件
     *
     * @param builder ActiveDefaults.Builder实例
     * @return 当前构建器实例，支持链式调用
     */
    public ActiveConfigureElementsBuilder active(ActiveDefaults.Builder builder) {
        configureElements.add(builder.build());
        return self;
    }

    /**
     * 通过Groovy闭包配置并构建ActiveDefaults实例
     * <p>
     * 执行流程：
     * 1. 创建ActiveDefaults.Builder实例
     * 2. 使用Groovy闭包配置Builder
     * 3. 构建ActiveDefaults实例并添加到配置元件列表
     * </p>
     *
     * @param closure Groovy闭包，用于配置ActiveDefaults.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public ActiveConfigureElementsBuilder active(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ActiveDefaults.Builder.class) Closure<?> closure) {
        var builder = ActiveDefaults.builder();
        call(closure, builder);
        configureElements.add(builder.build());
        return self;
    }
}