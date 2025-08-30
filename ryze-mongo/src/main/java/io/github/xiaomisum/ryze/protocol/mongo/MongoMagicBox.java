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

package io.github.xiaomisum.ryze.protocol.mongo;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.MagicBox;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.protocol.mongo.sampler.MongoSampler;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.groovy.Groovy;

/**
 * MongoDB 魔法盒子工具类
 * <p>
 * 提供函数式编程风格的 MongoDB 采样器执行入口，简化 MongoDB 操作的调用方式。
 * 支持使用 Groovy Closure 或 Java Customizer 来配置 MongoDB 采样器。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>提供多种 mongo 方法重载，支持不同配置方式</li>
 *   <li>封装 MongoDB 采样器的构建和执行过程</li>
 *   <li>支持带标题和不带标题的 MongoDB 操作执行</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class MongoMagicBox extends MagicBox {

    /**
     * 执行 MongoDB 操作（无标题）
     * <p>
     * 使用 Groovy Closure 方式配置并执行 MongoDB 采样器，不指定操作标题。
     * </p>
     *
     * @param closure Groovy Closure，用于配置 MongoSampler.Builder
     * @return 执行结果
     */
    public static Result mongo(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = MongoSampler.Builder.class) Closure<?> closure) {
        return mongo("", closure);
    }

    /**
     * 执行 MongoDB 操作（指定标题）
     * <p>
     * 使用 Groovy Closure 方式配置并执行 MongoDB 采样器，指定操作标题。
     * </p>
     *
     * @param title   操作标题，用于标识此次 MongoDB 操作
     * @param closure Groovy Closure，用于配置 MongoSampler.Builder
     * @return 执行结果
     */
    public static Result mongo(String title,
                               @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = MongoSampler.Builder.class) Closure<?> closure) {
        var builder = MongoSampler.builder();
        Groovy.call(closure, builder);
        return MagicBox.runTest(title, builder.build());
    }

    /**
     * 执行 MongoDB 操作（无标题）
     * <p>
     * 使用 Java Customizer 方式配置并执行 MongoDB 采样器，不指定操作标题。
     * </p>
     *
     * @param customizer Java Customizer，用于配置 MongoSampler.Builder
     * @return 执行结果
     */
    public static Result mongo(Customizer<MongoSampler.Builder> customizer) {
        return mongo("", customizer);
    }

    /**
     * 执行 MongoDB 操作（指定标题）
     * <p>
     * 使用 Java Customizer 方式配置并执行 MongoDB 采样器，指定操作标题。
     * </p>
     *
     * @param title      操作标题，用于标识此次 MongoDB 操作
     * @param customizer Java Customizer，用于配置 MongoSampler.Builder
     * @return 执行结果
     */
    public static Result mongo(String title, Customizer<MongoSampler.Builder> customizer) {
        var builder = MongoSampler.builder();
        customizer.customize(builder);
        return MagicBox.runTest(title, builder.build());
    }
}