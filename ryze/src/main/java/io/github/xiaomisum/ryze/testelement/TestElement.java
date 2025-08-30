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

package io.github.xiaomisum.ryze.testelement;

import com.alibaba.fastjson2.annotation.JSONType;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.support.Cloneable;
import io.github.xiaomisum.ryze.support.Validatable;
import io.github.xiaomisum.ryze.support.fastjson.deserializer.TestElementObjectReader;

/**
 * 测试元件是能根据其父上下文链独立执行的一个逻辑执行单元。
 * 测试元件使用 {@link KW} 注解配置关键字，
 * 若未使用该注解则通过 this.getClass().getSimpleName()获取类名用作关键字，以便识别测试集中组件的类型（代码风格中组件类型是已知的）。
 * 配置方式: testclass: http_sampler
 *
 * <p>测试元件是整个工具的核心，
 * 环境管理、变量管理、前置处理器、后置处理器、插值表达式、函数、断言、配置继承、取样器、控制器、用例引用等功能都将围绕测试元件展开。
 *
 * <p>一个测试元件可以包含多个子测试元件。
 *
 * <p>设计开发一个新的测试元件前，应当先明确该测试元件要实现的功能、需要的参数，是否和其他测试元件功能重合，实现粒度，然后再编码实现。
 *
 * <p>important! TestElement 为非线程安全类，不要在多个线程间共享 TestElement 对象。
 * 如果要在多个线程中运行同一个 TestElement 对象，请先 {@link #copy()}，每个线程使用该 TestElement 的拷贝进行运行。
 *
 * @author xiaomi
 */
@JSONType(deserializer = TestElementObjectReader.class)
public interface TestElement<T extends Result> extends Validatable, Cloneable<TestElement<T>> {

    /**
     * 默认的空方法
     */
    default T run(SessionRunner session) {
        return null;
    }


    /**
     * 对象拷贝，用于解决 TestElement对象的非线程安全问题
     *
     * <p>
     * 所有该接口的实现类均应当重写该方法
     * <p>
     *
     * @return 对象的拷贝
     */
    @Override
    default TestElement<T> copy() {
        // 默认认为当前类是线程安全的，否则应当重写该方法
        return this;
    }

}
