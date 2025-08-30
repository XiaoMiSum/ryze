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

package io.github.xiaomisum.ryze.protocol.active;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.MagicBox;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.protocol.active.sampler.ActiveSampler;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.groovy.Groovy;

/**
 * ActiveMQ协议魔法盒子类
 * <p>
 * 提供函数式编程方式执行ActiveMQ取样器的便捷入口，支持多种配置方式：
 * 1. 通过Groovy闭包方式配置取样器
 * 2. 通过Customizer函数式接口配置取样器
 * </p>
 *
 * @author xiaomi
 */
public class ActiveMagicBox extends MagicBox {

    /**
     * 通过Groovy闭包执行ActiveMQ取样器测试（无标题）
     * <p>
     * 执行流程：
     * 1. 调用带标题的重载方法，标题传空字符串
     * </p>
     *
     * @param closure Groovy闭包，用于配置ActiveSampler.Builder
     * @return 测试执行结果
     */
    public static Result active(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ActiveSampler.Builder.class) Closure<?> closure) {
        return active("", closure);
    }

    /**
     * 通过Groovy闭包执行ActiveMQ取样器测试（带标题）
     * <p>
     * 执行流程：
     * 1. 创建ActiveSampler.Builder实例
     * 2. 使用Groovy闭包配置Builder
     * 3. 构建ActiveSampler实例并执行测试
     * </p>
     *
     * @param title   测试标题
     * @param closure Groovy闭包，用于配置ActiveSampler.Builder
     * @return 测试执行结果
     */
    public static Result active(String title,
                                @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ActiveSampler.Builder.class) Closure<?> closure) {
        var builder = ActiveSampler.builder();
        Groovy.call(closure, builder);
        return MagicBox.runTest(title, builder.build());
    }

    /**
     * 通过Customizer函数式接口执行ActiveMQ取样器测试（无标题）
     * <p>
     * 执行流程：
     * 1. 调用带标题的重载方法，标题传空字符串
     * </p>
     *
     * @param customizer Customizer函数式接口，用于配置ActiveSampler.Builder
     * @return 测试执行结果
     */
    public static Result active(Customizer<ActiveSampler.Builder> customizer) {
        return active("", customizer);
    }

    /**
     * 通过Customizer函数式接口执行ActiveMQ取样器测试（带标题）
     * <p>
     * 执行流程：
     * 1. 创建ActiveSampler.Builder实例
     * 2. 使用Customizer配置Builder
     * 3. 构建ActiveSampler实例并执行测试
     * </p>
     *
     * @param title      测试标题
     * @param customizer Customizer函数式接口，用于配置ActiveSampler.Builder
     * @return 测试执行结果
     */
    public static Result active(String title, Customizer<ActiveSampler.Builder> customizer) {
        var builder = ActiveSampler.builder();
        customizer.customize(builder);
        return MagicBox.runTest(title, builder.build());
    }
}