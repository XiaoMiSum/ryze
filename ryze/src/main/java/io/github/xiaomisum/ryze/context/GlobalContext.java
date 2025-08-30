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

import io.github.xiaomisum.ryze.config.GlobalConfigure;

/**
 * 全局上下文
 * <p>
 * 全局上下文是migoo测试框架中作用域最广的上下文类型，贯穿整个测试运行过程。
 * 它存储了全局级别的配置信息，这些配置对所有测试套件、会话和测试都可见且有效。
 * </p>
 * <p>
 * 全局上下文的主要特点：
 * <ul>
 *   <li>作用域最广：在整个测试运行期间都有效</li>
 *   <li>优先级最低：会被测试套件、会话和测试级别的同名配置覆盖</li>
 *   <li>生命周期最长：从测试开始到结束都存在</li>
 *   <li>共享性：所有其他上下文都可以访问全局上下文中的配置</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class GlobalContext extends TestRunContext {

    /**
     * 创建一个新的全局上下文实例
     * <p>
     * 使用此构造函数创建的全局上下文不包含任何初始配置，需要后续通过setConfigGroup方法设置配置组数据，
     * 或者在运行时动态添加配置信息。
     * </p>
     */
    public GlobalContext() {
        super();
    }

    /**
     * 创建一个新的全局上下文实例并初始化全局配置
     * <p>
     * 使用此构造函数可以直接将全局配置设置到上下文中，避免了后续手动设置配置组的步骤。
     * 这在需要预加载全局配置的场景中非常有用。
     * </p>
     *
     * @param globalConfigure 全局配置数据，可以为null
     */
    public GlobalContext(GlobalConfigure globalConfigure) {
        super();
        setConfigGroup(globalConfigure);
    }
}