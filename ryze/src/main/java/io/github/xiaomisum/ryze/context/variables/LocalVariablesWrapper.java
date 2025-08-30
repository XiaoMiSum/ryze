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
 * 本地变量包装器
 * <p>
 * 该类用于管理本地作用域（Session级别）的变量，继承自AbstractVariablesWrapper。
 * 它主要用于SessionRunner中，管理特定会话范围内的变量，这些变量只在当前会话中可见和可访问。
 * </p>
 * <p>
 * LocalVariablesWrapper提供了对会话级别变量的管理能力，包括变量的获取、设置、删除等操作。
 * 与全局变量不同，本地变量只在特定的会话范围内有效，会话结束后这些变量通常会被销毁。
 * </p>
 *
 * @author xiaomi
 */
public class LocalVariablesWrapper extends AbstractVariablesWrapper {

    /**
     * 构造一个新的本地变量包装器实例
     * <p>
     * 该构造函数接收一个上下文链，用于初始化变量包装器。对于本地变量包装器，
     * 上下文链通常包含全局上下文和当前会话上下文，用于支持变量的继承和覆盖机制。
     * </p>
     *
     * @param contextChain 上下文链，定义了变量的作用域层次结构，不能为空
     */
    public LocalVariablesWrapper(List<Context> contextChain) {
        super(contextChain);
    }

}