/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * 'Software'), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package io.github.xiaomisum.ryze.context.variables;

import io.github.xiaomisum.ryze.context.Context;

import java.util.List;

/**
 * 会话变量包装器
 * <p>
 * 该类用于管理会话作用域的变量，继承自AbstractVariablesWrapper。它主要用于SessionRunner中，
 * 管理特定会话范围内的变量，这些变量在整个会话执行期间可见和可访问。
 * </p>
 * <p>
 * SessionVariablesWrapper提供了对会话级别变量的管理能力，包括变量的获取、设置、删除等操作。
 * 会话变量的作用域介于全局变量和测试变量之间，会覆盖同名的全局变量，但会被同名的测试变量覆盖。
 * 会话变量的生命周期与会话执行周期一致，会话结束后这些变量通常会被销毁。
 * </p>
 *
 * @author xiaomi
 */
public class SessionVariablesWrapper extends AbstractVariablesWrapper {

    /**
     * 构造一个新的会话变量包装器实例
     * <p>
     * 该构造函数接收一个上下文链，用于初始化变量包装器。对于会话变量包装器，
     * 上下文链通常包含全局上下文、测试套件上下文和当前会话上下文，
     * 用于支持变量的继承和覆盖机制。
     * </p>
     *
     * @param contextChain 上下文链，定义了变量的作用域层次结构，不能为空
     */
    public SessionVariablesWrapper(List<Context> contextChain) {
        super(contextChain);
    }

}