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
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.builder.ExtensiblePreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.mongo.processsor.MongoPreprocessor;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * MongoDB 前置处理器列表构建器
 * <p>
 * 该类用于构建 MongoDB 前置处理器列表，提供多种方式添加 MongoDB 前置处理器。
 * 它继承自 ExtensiblePreprocessorsBuilder，支持链式调用和多种配置方式。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>提供多种 mongo 方法重载，支持不同配置方式</li>
 *   <li>支持直接添加 MongoPreprocessor 实例</li>
 *   <li>支持使用 Customizer 配置 MongoPreprocessor</li>
 *   <li>支持使用 Builder 配置 MongoPreprocessor</li>
 *   <li>支持使用 Groovy Closure 配置 MongoPreprocessor</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class MongoPreprocessorsBuilder extends ExtensiblePreprocessorsBuilder<MongoPreprocessorsBuilder, DefaultExtractorsBuilder> {

    /**
     * 创建 MongoPreprocessorsBuilder 实例
     * <p>
     * 静态工厂方法，用于创建 MongoPreprocessorsBuilder 实例。
     * </p>
     *
     * @return MongoPreprocessorsBuilder 实例
     */
    public static MongoPreprocessorsBuilder builder() {
        return new MongoPreprocessorsBuilder();
    }

    /**
     * 添加 MongoDB 前置处理器
     * <p>
     * 直接添加 MongoPreprocessor 实例到前置处理器列表中。
     * </p>
     *
     * @param preprocessor MongoPreprocessor 实例
     * @return 当前构建器实例，支持链式调用
     */
    public MongoPreprocessorsBuilder mongo(MongoPreprocessor preprocessor) {
        preprocessors.add(preprocessor);
        return self;
    }

    /**
     * 添加 MongoDB 前置处理器
     * <p>
     * 使用 Customizer 配置 MongoPreprocessor.Builder，然后构建并添加到前置处理器列表中。
     * </p>
     *
     * @param customizer 用于配置 MongoPreprocessor.Builder 的 Customizer
     * @return 当前构建器实例，支持链式调用
     */
    public MongoPreprocessorsBuilder mongo(Customizer<MongoPreprocessor.Builder> customizer) {
        var builder = MongoPreprocessor.builder();
        customizer.customize(builder);
        preprocessors.add(builder.build());
        return self;
    }

    /**
     * 添加 MongoDB 前置处理器
     * <p>
     * 使用 MongoPreprocessor.Builder 构建 MongoPreprocessor 实例，然后添加到前置处理器列表中。
     * </p>
     *
     * @param builder MongoPreprocessor.Builder 实例
     * @return 当前构建器实例，支持链式调用
     */
    public MongoPreprocessorsBuilder mongo(MongoPreprocessor.Builder builder) {
        preprocessors.add(builder.build());
        return self;
    }

    /**
     * 添加 MongoDB 前置处理器
     * <p>
     * 使用 Groovy Closure 配置 MongoPreprocessor.Builder，然后构建并添加到前置处理器列表中。
     * </p>
     *
     * @param closure 用于配置 MongoPreprocessor.Builder 的 Groovy Closure
     * @return 当前构建器实例，支持链式调用
     */
    public MongoPreprocessorsBuilder mongo(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = MongoPreprocessor.Builder.class) Closure<?> closure) {
        var builder = MongoPreprocessor.builder();
        call(closure, builder);
        preprocessors.add(builder.build());
        return self;
    }
}