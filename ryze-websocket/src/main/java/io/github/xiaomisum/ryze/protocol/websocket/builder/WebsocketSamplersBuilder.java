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
import io.github.xiaomisum.ryze.builder.ExtensibleChildrenBuilder;
import io.github.xiaomisum.ryze.protocol.websocket.sampler.WebsocketSampler;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * WebsocketMQ自定义取样器列表构建器
 * <p>
 * 提供构建WebsocketMQ自定义取样器列表的多种方式：
 * 1. 直接添加已构建的WebsocketSampler实例
 * 2. 通过Customizer函数式接口配置并构建WebsocketSampler实例
 * 3. 通过WebsocketSampler.Builder实例构建并添加
 * 4. 通过Groovy闭包配置并构建WebsocketSampler实例
 * </p>
 *
 * @author xiaomi
 */
public class WebsocketSamplersBuilder extends ExtensibleChildrenBuilder<WebsocketSamplersBuilder> {

    /**
     * 创建WebsocketSamplersBuilder实例
     *
     * @return 新的WebsocketSamplersBuilder实例
     */
    public static WebsocketSamplersBuilder builder() {
        return new WebsocketSamplersBuilder();
    }

    /**
     * 添加已构建的WebsocketSampler取样器
     *
     * @param child 已构建的WebsocketSampler实例
     * @return 当前构建器实例，支持链式调用
     */
    public WebsocketSamplersBuilder ws(WebsocketSampler child) {
        this.children.add(child);
        return self;
    }

    /**
     * 通过WebsocketSampler.Builder实例构建并添加取样器
     *
     * @param child WebsocketSampler.Builder实例
     * @return 当前构建器实例，支持链式调用
     */
    public WebsocketSamplersBuilder ws(WebsocketSampler.Builder child) {
        this.children.add(child.build());
        return self;
    }

    /**
     * 通过Customizer函数式接口配置并构建WebsocketSampler实例
     * <p>
     * 执行流程：
     * 1. 创建WebsocketSampler.Builder实例
     * 2. 使用Customizer配置Builder
     * 3. 构建WebsocketSampler实例并添加到取样器列表
     * </p>
     *
     * @param customizer Customizer函数式接口，用于配置WebsocketSampler.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public WebsocketSamplersBuilder ws(Customizer<WebsocketSampler.Builder> customizer) {
        var builder = WebsocketSampler.builder();
        customizer.customize(builder);
        this.children.add(builder.build());
        return self;
    }

    /**
     * 通过Groovy闭包配置并构建WebsocketSampler实例
     * <p>
     * 执行流程：
     * 1. 创建WebsocketSampler.Builder实例
     * 2. 使用Groovy闭包配置Builder
     * 3. 构建WebsocketSampler实例并添加到取样器列表
     * </p>
     *
     * @param closure Groovy闭包，用于配置WebsocketSampler.Builder
     * @return 当前构建器实例，支持链式调用
     */
    public WebsocketSamplersBuilder ws(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = WebsocketSampler.Builder.class) Closure<?> closure) {
        var builder = WebsocketSampler.builder();
        call(closure, builder);
        this.children.add(builder.build());
        return self;
    }
}