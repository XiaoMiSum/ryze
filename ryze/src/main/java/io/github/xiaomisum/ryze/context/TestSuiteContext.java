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

/**
 * 测试套件上下文
 * <p>
 * 测试套件上下文用于管理特定测试套件范围内的配置和状态信息。在migoo测试框架中，每个TestSuite对应一个TestSuiteContext，
 * 用于存储该测试套件执行期间的所有相关配置。测试套件上下文的生命周期与测试套件执行周期一致，测试套件结束后上下文会被销毁。
 * </p>
 * <p>
 * 测试套件上下文的主要特点：
 * <ul>
 *   <li>作用域适中：在整个测试套件执行期间都有效</li>
 *   <li>优先级中等：会覆盖全局配置，但会被会话和测试级别的同名配置覆盖</li>
 *   <li>生命周期适中：从测试套件开始到结束都存在</li>
 *   <li>共享性：该测试套件内的所有测试都可以访问测试套件上下文中的配置</li>
 * </ul>
 * </p>
 * <p>
 * 典型应用场景包括：
 * <ul>
 *   <li>在测试套件级别设置共享的变量或配置</li>
 *   <li>管理测试套件范围内的前置条件或后置操作</li>
 *   <li>存储测试套件级别的测试数据或状态</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class TestSuiteContext extends TestRunContext {
}