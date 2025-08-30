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
 * 全局变量包装器
 * <p>
 * 该类用于管理全局作用域的变量，继承自AbstractVariablesWrapper。它主要用于GlobalContext中，
 * 管理整个测试运行期间全局可见的变量，这些变量在所有测试、会话和测试套件中都可见和可访问。
 * </p>
 * <p>
 * GlobalVariablesWrapper提供了对全局变量的管理能力，包括变量的获取、设置、删除等操作。
 * 全局变量具有最低的作用域优先级，会被同名的会话变量或测试变量覆盖。全局变量的生命周期
 * 贯穿整个测试运行过程，从测试开始到结束都有效。
 * </p>
 *
 * @author xiaomi
 * Created at 2025/7/20 14:02
 */
public class GlobalVariablesWrapper extends AbstractVariablesWrapper {

    /**
     * 构造一个新的全局变量包装器实例
     * <p>
     * 该构造函数接收一个上下文链，用于初始化变量包装器。对于全局变量包装器，
     * 上下文链通常以全局上下文为主，用于管理整个测试运行期间的全局变量。
     * </p>
     *
     * @param contextChain 上下文链，定义了变量的作用域层次结构，不能为空
     */
    public GlobalVariablesWrapper(List<Context> contextChain) {
        super(contextChain);
    }

}