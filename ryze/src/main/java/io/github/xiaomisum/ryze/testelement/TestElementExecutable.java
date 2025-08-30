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

package io.github.xiaomisum.ryze.testelement;

import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.SessionRunner;

/**
 * 可执行测试组件接口
 * <p>
 * 该接口扩展了TestElement接口，定义了测试组件的执行方法，是所有可执行测试组件的顶层接口。
 * 实现该接口的测试组件可以通过SessionRunner执行，并返回相应的执行结果。
 * </p>
 *
 * @param <T> 测试执行结果类型，必须是Result的子类
 * @author xiaomi
 * Created at 2025/7/20 11:55
 */
@FunctionalInterface
public interface TestElementExecutable<T extends Result> extends TestElement<T> {

    /**
     * 执行测试组件
     * <p>
     * 该方法是测试组件执行的入口点，负责执行测试组件的核心逻辑并返回执行结果。
     * 用户应避免直接调用该方法，推荐使用 {@link SessionRunner#runTest} 方法来执行测试组件，
     * 以确保正确的执行环境和上下文管理。
     * </p>
     *
     * @param session 每个测试用例使用各自的 SessionRunner，提供执行环境和上下文信息
     * @return 执行结果，包含测试执行的状态、时间等信息
     */
    T run(SessionRunner session);

}