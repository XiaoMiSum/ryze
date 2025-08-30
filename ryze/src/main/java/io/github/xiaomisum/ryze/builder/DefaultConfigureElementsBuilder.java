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

package io.github.xiaomisum.ryze.builder;

/**
 * 默认配置元件列表构建器，提供 core 模块下的 各协议配置元件构建
 *
 * <p>该构建器是 {@link ExtensibleConfigureElementsBuilder}的具体实现，
 * 提供了核心模块中各种协议配置元件的构建方法。</p>
 *
 * <p>配置元件用于初始化测试执行环境，如数据库连接、HTTP客户端等。
 * 通过该构建器可以方便地构建各种协议的配置元件。</p>
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>提供核心模块下各协议配置元件的构建方法</li>
 *   <li>支持流畅的构建器模式</li>
 *   <li>可作为其他自定义配置元件构建器的基础</li>
 * </ul></p>
 *
 * @author xiaomi
 */
public class DefaultConfigureElementsBuilder extends ExtensibleConfigureElementsBuilder<DefaultConfigureElementsBuilder> {

    /**
     * 创建默认配置元件列表构建器实例
     *
     * @return 默认配置元件列表构建器实例
     */
    public static DefaultConfigureElementsBuilder builder() {
        return new DefaultConfigureElementsBuilder();
    }
}