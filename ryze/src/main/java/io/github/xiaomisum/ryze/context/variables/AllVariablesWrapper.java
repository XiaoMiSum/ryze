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
 * 全局变量包装类
 * <p>
 * 该类用于管理全局作用域的变量，继承自AbstractVariablesWrapper，提供对整个测试运行过程中
 * 所有变量的访问和操作能力。它通过上下文链来管理变量，支持变量的继承和覆盖机制。
 * </p>
 * <p>
 * 在migoo测试框架中，AllVariablesWrapper通常用于需要访问所有层级变量的场景，
 * 它会合并所有上下文中的变量，提供一个统一的变量视图。变量的查找和设置遵循标准的作用域规则，
 * 即优先使用更近的作用域中的变量值。
 * </p>
 *
 * @author xiaomi
 */
public class AllVariablesWrapper extends AbstractVariablesWrapper {

    /**
     * 构造一个新的全局变量包装器实例
     * <p>
     * 该构造函数接收一个上下文链，用于初始化变量包装器。上下文链定义了变量的作用域层次结构，
     * 通常包括全局上下文、测试套件上下文、会话上下文和测试上下文等。
     * </p>
     *
     * @param contextChain 上下文链，定义了变量的作用域层次结构，不能为空
     */
    public AllVariablesWrapper(List<Context> contextChain) {
        super(contextChain);
    }

}