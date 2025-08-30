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
 * 测试运行上下文抽象基类
 * <p>
 * 该类是所有测试运行上下文的基类，提供了配置组的管理功能。它实现了Context接口，
 * 为具体的上下文实现类（如GlobalContext、TestSuiteContext等）提供了统一的配置管理机制。
 * </p>
 * <p>
 * TestRunContext的主要职责包括：
 * <ul>
 *   <li>存储和管理配置组数据</li>
 *   <li>提供配置组的获取和设置方法</li>
 *   <li>作为不同层级上下文实现的共同基类</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public abstract class TestRunContext implements Context {

    /**
     * 配置组，包含该上下文关联的所有配置信息
     * <p>
     * 配置组是一个键值对集合，存储了测试运行所需的各种配置，如变量、HTTP配置、数据库配置等。
     * 不同层级的上下文可以拥有自己的配置组，通过上下文链实现配置的继承和覆盖机制。
     * </p>
     */
    private ConfigureGroup configureGroup;

    /**
     * 获取当前上下文的配置组数据
     * <p>
     * 配置组包含了该测试元件相关的所有配置信息。如果配置组尚未初始化，则返回null。
     * </p>
     *
     * @return 当前上下文的配置组数据，如果未设置则返回null
     * @see Context#getConfigGroup()
     */
    @Override
    public ConfigureGroup getConfigGroup() {
        return configureGroup;
    }

    /**
     * 设置当前上下文的配置组数据
     * <p>
     * 该方法用于设置当前上下文关联的配置组。通常在上下文初始化时调用，
     * 将从配置文件或其他来源加载的配置数据设置到上下文中。
     * </p>
     *
     * @param configureGroup 要设置的配置组数据，可以为null
     */
    public void setConfigGroup(ConfigureGroup configureGroup) {
        this.configureGroup = configureGroup;
    }
}