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

package io.github.xiaomisum.ryze.protocol.mongo.builder;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.builder.ExtensibleChildrenBuilder;
import io.github.xiaomisum.ryze.protocol.mongo.sampler.MongoSampler;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * MongoDB 采样器列表构建器
 * <p>
 * 该类用于构建 MongoDB 采样器列表，提供多种方式添加 MongoDB 采样器。
 * 它继承自 ExtensibleChildrenBuilder，支持链式调用和多种配置方式。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>提供多种 mongo 方法重载，支持不同配置方式</li>
 *   <li>支持直接添加 MongoSampler 实例</li>
 *   <li>支持使用 Customizer 配置 MongoSampler</li>
 *   <li>支持使用 Builder 配置 MongoSampler</li>
 *   <li>支持使用 Groovy Closure 配置 MongoSampler</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class MongoSamplersBuilder extends ExtensibleChildrenBuilder<MongoSamplersBuilder> {

    /**
     * 创建 MongoSamplersBuilder 实例
     * <p>
     * 静态工厂方法，用于创建 MongoSamplersBuilder 实例。
     * </p>
     *
     * @return MongoSamplersBuilder 实例
     */
    public static MongoSamplersBuilder builder() {
        return new MongoSamplersBuilder();
    }

    /**
     * 添加 MongoDB 采样器
     * <p>
     * 直接添加 MongoSampler 实例到采样器列表中。
     * </p>
     *
     * @param child MongoSampler 实例
     * @return 当前构建器实例，支持链式调用
     */
    public MongoSamplersBuilder mongo(MongoSampler child) {
        this.children.add(child);
        return self;
    }

    /**
     * 添加 MongoDB 采样器
     * <p>
     * 使用 MongoSampler.Builder 构建 MongoSampler 实例，然后添加到采样器列表中。
     * </p>
     *
     * @param child MongoSampler.Builder 实例
     * @return 当前构建器实例，支持链式调用
     */
    public MongoSamplersBuilder mongo(MongoSampler.Builder child) {
        this.children.add(child.build());
        return self;
    }

    /**
     * 添加 MongoDB 采样器
     * <p>
     * 使用 Customizer 配置 MongoSampler.Builder，然后构建并添加到采样器列表中。
     * </p>
     *
     * @param customizer 用于配置 MongoSampler.Builder 的 Customizer
     * @return 当前构建器实例，支持链式调用
     */
    public MongoSamplersBuilder mongo(Customizer<MongoSampler.Builder> customizer) {
        var builder = MongoSampler.builder();
        customizer.customize(builder);
        this.children.add(builder.build());
        return self;
    }

    /**
     * 添加 MongoDB 采样器
     * <p>
     * 使用 Groovy Closure 配置 MongoSampler.Builder，然后构建并添加到采样器列表中。
     * </p>
     *
     * @param closure 用于配置 MongoSampler.Builder 的 Groovy Closure
     * @return 当前构建器实例，支持链式调用
     */
    public MongoSamplersBuilder mongo(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = MongoSampler.Builder.class) Closure<?> closure) {
        var builder = MongoSampler.builder();
        call(closure, builder);
        this.children.add(builder.build());
        return self;
    }
}