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

package io.github.xiaomisum.ryze.protocol.websocket.builder;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.builder.ExtensiblePostprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.websocket.processor.WebsocketPostprocessor;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * Websocket自定义后置处理器列表构建器
 * <p>
 * 提供构建Websocket自定义后置处理器列表的多种方式：
 * 1. 直接添加已构建的WebsocketPostprocessor实例
 * 2. 通过Customizer函数式接口配置并构建WebsocketPostprocessor实例
 * 3. 通过WebsocketPostprocessor.Builder实例构建并添加
 * 4. 通过Groovy闭包配置并构建WebsocketPostprocessor实例
 * </p>
 *
 * @author xiaomi
 */
public class WebsocketPostprocessorsBuilder extends ExtensiblePostprocessorsBuilder<WebsocketPostprocessorsBuilder, DefaultExtractorsBuilder> {

    /**
     * 创建WebsocketPostprocessorsBuilder实例
     *
     * @return 新的WebsocketPostprocessorsBuilder实例
     */
    public static WebsocketPostprocessorsBuilder builder() {
        return new WebsocketPostprocessorsBuilder();
    }

    /**
     * 添加已构建的WebsocketPostprocessor后置处理器
     *
     * @param postprocessor 已构建的WebsocketPostprocessor实例
     * @return 当前构建器实例，支持链式调用
     */
    public WebsocketPostprocessorsBuilder ws(WebsocketPostprocessor postprocessor) {
        postprocessors.add(postprocessor);
        return self;
    }

    /**
     * 通过Customizer函数式接口配置并构建WebsocketPostprocessor实例
     * <p>
     * 执行流程：
     * 1. 创建WebsocketPostprocessor.Builder实例
     * 2. 使用Customizer配置Builder
     * 3. 构建WebsocketPostprocessor实例并添加到后置处理器列表
     * </p>
     *
     * @param customizer Customizer函数式接口，用于配置WebsocketPostprocessor.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public WebsocketPostprocessorsBuilder ws(Customizer<WebsocketPostprocessor.Builder> customizer) {
        var builder = WebsocketPostprocessor.builder();
        customizer.customize(builder);
        postprocessors.add(builder.build());
        return self;
    }

    /**
     * 通过WebsocketPostprocessor.Builder实例构建并添加后置处理器
     *
     * @param builder WebsocketPostprocessor.Builder实例
     * @return 当前构建器实例，支持链式调用
     */
    public WebsocketPostprocessorsBuilder ws(WebsocketPostprocessor.Builder builder) {
        postprocessors.add(builder.build());
        return self;
    }

    /**
     * 通过Groovy闭包配置并构建WebsocketPostprocessor实例
     * <p>
     * 执行流程：
     * 1. 创建WebsocketPostprocessor.Builder实例
     * 2. 使用Groovy闭包配置Builder
     * 3. 构建WebsocketPostprocessor实例并添加到后置处理器列表
     * </p>
     *
     * @param closure Groovy闭包，用于配置WebsocketPostprocessor.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public WebsocketPostprocessorsBuilder ws(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = WebsocketPostprocessor.Builder.class) Closure<?> closure) {
        var builder = WebsocketPostprocessor.builder();
        call(closure, builder);
        postprocessors.add(builder.build());
        return self;
    }
}