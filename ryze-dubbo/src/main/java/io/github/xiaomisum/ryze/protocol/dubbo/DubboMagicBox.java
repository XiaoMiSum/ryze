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

package io.github.xiaomisum.ryze.protocol.dubbo;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.MagicBox;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.protocol.dubbo.sampler.DubboSampler;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.groovy.Groovy;

/**
 * Dubbo魔法盒子类
 * <p>
 * 该类是Dubbo协议测试的入口类，提供函数式编程接口来执行Dubbo测试。<br>
 * 继承自MagicBox基类，封装了测试执行的核心逻辑。<br>
 * 支持Groovy闭包和Java函数式接口两种配置方式，提供灵活的测试执行入口。<br>
 * </p>
 * <p>
 * 业务处理逻辑：
 * <ol>
 *   <li>提供静态方法作为测试执行入口</li>
 *   <li>支持通过Groovy闭包配置测试参数</li>
 *   <li>支持通过Java函数式接口配置测试参数</li>
 *   <li>创建Dubbo取样器并执行测试</li>
 *   <li>返回测试执行结果</li>
 * </ol>
 * </p>
 *
 * @author xiaomi
 * @since 1.0.0
 */
public class DubboMagicBox extends MagicBox {

    /**
     * 执行Dubbo测试（通过Groovy闭包配置，无标题）
     * <p>
     * 通过Groovy闭包配置并执行Dubbo测试，不指定测试标题。<br>
     * 该方法提供了一种Groovy DSL的方式来执行测试，适用于支持Groovy的环境。<br>
     * 例如：dubbo { interfaceName "com.example.service.DemoService" }<br>
     * 内部会创建默认标题为空字符串的测试并执行。
     * </p>
     *
     * @param closure Groovy闭包，用于配置Dubbo取样器
     * @return 测试执行结果
     */
    public static Result dubbo(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DubboSampler.Builder.class) Closure<?> closure) {
        return dubbo("", closure);
    }

    /**
     * 执行Dubbo测试（通过Groovy闭包配置，指定标题）
     * <p>
     * 通过Groovy闭包配置并执行Dubbo测试，指定测试标题。<br>
     * 该方法提供了一种Groovy DSL的方式来执行测试，适用于支持Groovy的环境。<br>
     * 例如：dubbo("测试用户查询") { interfaceName "com.example.service.DemoService" }<br>
     * 内部会创建指定标题的测试并执行，标题将显示在测试报告中。
     * </p>
     *
     * @param title   测试标题，显示在测试报告中
     * @param closure Groovy闭包，用于配置Dubbo取样器
     * @return 测试执行结果
     */
    public static Result dubbo(String title,
                               @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DubboSampler.Builder.class) Closure<?> closure) {
        var builder = DubboSampler.builder();
        Groovy.call(closure, builder);
        return runTest(title, builder.build());
    }

    /**
     * 执行Dubbo测试（通过Java函数式接口配置，无标题）
     * <p>
     * 通过Java函数式接口配置并执行Dubbo测试，不指定测试标题。<br>
     * 该方法提供了一种函数式编程的方式来执行测试，适用于Java 8及以上版本。<br>
     * 例如：dubbo(builder -> builder.interfaceName("com.example.service.DemoService"))<br>
     * 内部会创建默认标题为空字符串的测试并执行。
     * </p>
     *
     * @param customizer Dubbo取样器构建器的自定义器函数式接口
     * @return 测试执行结果
     */
    public static Result dubbo(Customizer<DubboSampler.Builder> customizer) {
        return dubbo("", customizer);
    }

    /**
     * 执行Dubbo测试（通过Java函数式接口配置，指定标题）
     * <p>
     * 通过Java函数式接口配置并执行Dubbo测试，指定测试标题。<br>
     * 该方法提供了一种函数式编程的方式来执行测试，适用于Java 8及以上版本。<br>
     * 例如：dubbo("测试用户查询", builder -> builder.interfaceName("com.example.service.DemoService"))<br>
     * 内部会创建指定标题的测试并执行，标题将显示在测试报告中。
     * </p>
     *
     * @param title      测试标题，显示在测试报告中
     * @param customizer Dubbo取样器构建器的自定义器函数式接口
     * @return 测试执行结果
     */
    public static Result dubbo(String title, Customizer<DubboSampler.Builder> customizer) {
        var builder = DubboSampler.builder();
        customizer.customize(builder);
        return runTest(title, builder.build());
    }
}