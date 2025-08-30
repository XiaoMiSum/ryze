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

package io.github.xiaomisum.ryze.context;

import io.github.xiaomisum.ryze.config.ConfigureGroup;

/**
 * 测试上下文接口，通过该接口可以获取每个测试元件的上下文信息
 * <p>
 * 在migoo测试框架中，上下文(Context)是管理测试执行过程中各种配置和状态的核心概念。
 * 每个测试元件(TestElement)都关联一个上下文实例，通过该上下文可以访问配置信息、变量以及其他运行时数据。
 * </p>
 * <p>
 * 上下文按照作用域分为多个层级：
 * <ul>
 *   <li>全局上下文(GlobalContext) - 整个测试运行期间的全局配置</li>
 *   <li>测试套件上下文(TestSuiteContext) - 特定测试套件范围内的配置</li>
 *   <li>会话上下文(SessionContext) - 特定会话范围内的配置</li>
 *   <li>测试上下文(TestContext) - 单个测试范围内的配置</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public interface Context {

    /**
     * 获取当前 TestElement 的配置组数据
     * <p>
     * TestElement 的配置组数据在运行时初始化，属于运行时配置数据。配置组包含该测试元件相关的所有配置信息，
     * 如变量、HTTP配置、数据库配置等。这些配置可能来自不同层级的上下文，通过合并机制实现配置的继承和覆盖。
     * </p>
     *
     * @return TestElement 的配置组数据，如果未设置则返回null
     */
    ConfigureGroup getConfigGroup();
}