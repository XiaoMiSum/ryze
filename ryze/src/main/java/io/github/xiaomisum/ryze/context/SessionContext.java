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
 * 会话上下文
 * <p>
 * 会话上下文用于管理特定会话范围内的配置和状态信息。在migoo测试框架中，每个Session对应一个SessionContext，
 * 用于存储该会话执行期间的所有相关配置。会话上下文的生命周期与会话执行周期一致，会话结束后上下文会被销毁。
 * </p>
 * <p>
 * 会话上下文的主要特点：
 * <ul>
 *   <li>作用域适中：在整个会话执行期间都有效</li>
 *   <li>优先级中等：会覆盖全局配置，但会被测试级别的同名配置覆盖</li>
 *   <li>生命周期适中：从会话开始到结束都存在</li>
 *   <li>共享性：该会话内的所有测试都可以访问会话上下文中的配置</li>
 * </ul>
 * </p>
 * <p>
 * 典型应用场景包括：
 * <ul>
 *   <li>在会话级别设置共享的变量或配置</li>
 *   <li>管理会话范围内的连接池或资源</li>
 *   <li>存储会话级别的测试数据或状态</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class SessionContext extends TestRunContext {
}