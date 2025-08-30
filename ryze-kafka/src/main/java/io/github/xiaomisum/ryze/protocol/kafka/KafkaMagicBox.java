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

package io.github.xiaomisum.ryze.protocol.kafka;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.MagicBox;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.protocol.kafka.sampler.KafkaSampler;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.groovy.Groovy;

/**
 * Kafka魔法盒子类，提供函数式取样器执行入口
 * <p>
 * 该类继承自MagicBox，是Kafka协议测试的入口类，提供了多种便捷方法来执行Kafka测试，
 * 支持函数式编程风格和Groovy DSL语法，简化了Kafka测试的编写过程。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>提供函数式Kafka测试执行入口</li>
 *   <li>支持多种参数配置方式</li>
 *   <li>支持函数式接口和Groovy闭包</li>
 * </ul>
 * </p>
 * <p>
 * 业务处理逻辑：
 * <ol>
 *   <li>创建KafkaSampler构建器</li>
 *   <li>应用用户配置（函数式接口或Groovy闭包）</li>
 *   <li>构建KafkaSampler实例</li>
 *   <li>调用MagicBox执行测试</li>
 * </ol>
 * </p>
 *
 * @author xiaomi
 * @see MagicBox
 * @see KafkaSampler
 */
public class KafkaMagicBox extends MagicBox {

    /**
     * 执行Kafka测试（无标题版本）
     * <p>
     * 使用Groovy闭包配置KafkaSampler并执行测试，标题默认为空字符串
     * </p>
     *
     * @param closure Groovy闭包，用于配置KafkaSampler.Builder
     * @return 测试结果对象
     */
    public static Result kafka(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = KafkaSampler.Builder.class) Closure<?> closure) {
        return kafka("", closure);
    }

    /**
     * 执行Kafka测试（带标题版本）
     * <p>
     * 使用Groovy闭包配置KafkaSampler并执行测试，可以指定测试标题
     * </p>
     *
     * @param title   测试标题
     * @param closure Groovy闭包，用于配置KafkaSampler.Builder
     * @return 测试结果对象
     */
    public static Result kafka(String title,
                               @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = KafkaSampler.Builder.class) Closure<?> closure) {
        var builder = KafkaSampler.builder();
        Groovy.call(closure, builder);
        return MagicBox.runTest(title, builder.build());
    }

    /**
     * 执行Kafka测试（无标题版本）
     * <p>
     * 使用自定义函数式接口配置KafkaSampler并执行测试，标题默认为空字符串
     * </p>
     *
     * @param customizer KafkaSampler.Builder的自定义函数式接口
     * @return 测试结果对象
     */
    public static Result kafka(Customizer<KafkaSampler.Builder> customizer) {
        return kafka("", customizer);
    }

    /**
     * 执行Kafka测试（带标题版本）
     * <p>
     * 使用自定义函数式接口配置KafkaSampler并执行测试，可以指定测试标题
     * </p>
     *
     * @param title      测试标题
     * @param customizer KafkaSampler.Builder的自定义函数式接口
     * @return 测试结果对象
     */
    public static Result kafka(String title, Customizer<KafkaSampler.Builder> customizer) {
        var builder = KafkaSampler.builder();
        customizer.customize(builder);
        return MagicBox.runTest(title, builder.build());
    }
}