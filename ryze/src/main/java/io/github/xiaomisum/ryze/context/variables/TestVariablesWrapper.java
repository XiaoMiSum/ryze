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

package io.github.xiaomisum.ryze.context.variables;

import io.github.xiaomisum.ryze.context.Context;

import java.util.List;

/**
 * 测试变量包装类
 * <p>
 * 该类用于管理测试作用域的变量，继承自AbstractVariablesWrapper。它主要用于TestRunner中，
 * 管理特定测试范围内的变量，这些变量只在当前测试执行期间可见和可访问。
 * </p>
 * <p>
 * TestVariablesWrapper提供了对测试级别变量的管理能力，包括变量的获取、设置、删除等操作。
 * 这些变量具有最高的作用域优先级，会覆盖同名的全局、测试套件或会话级别变量。
 * 测试变量的生命周期与测试执行周期一致，测试结束后这些变量通常会被销毁。
 * </p>
 *
 * @author xiaomi
 */
public class TestVariablesWrapper extends AbstractVariablesWrapper {

    /**
     * 构造一个新的测试变量包装器实例
     * <p>
     * 该构造函数接收一个上下文链，用于初始化变量包装器。对于测试变量包装器，
     * 上下文链通常包含全局上下文、测试套件上下文、会话上下文和当前测试上下文，
     * 用于支持完整的变量继承和覆盖机制。
     * </p>
     *
     * @param contextChain 上下文链，定义了变量的作用域层次结构，不能为空
     */
    public TestVariablesWrapper(List<Context> contextChain) {
        super(contextChain);
    }

}