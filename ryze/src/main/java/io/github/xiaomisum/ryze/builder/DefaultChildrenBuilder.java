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
 * 默认子集合构建器，提供 TestSuite 及 core模块下的 协议取样器构建
 *
 * <p>该构建器是 {@link ExtensibleChildrenBuilder}的具体实现，
 * 提供了测试套件(TestSuite)以及核心模块中所有协议取样器的构建方法。</p>
 *
 * <p>子集合构建器用于构建测试容器的子元件列表，是构建测试结构树的重要工具。
 * 通过该构建器可以方便地将各种测试元件组织成树状结构。</p>
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>提供测试套件的构建方法</li>
 *   <li>提供核心模块下各协议取样器的构建方法</li>
 *   <li>支持流畅的构建器模式</li>
 *   <li>可作为其他自定义子集合构建器的基础</li>
 * </ul></p>
 *
 * @author xiaomi
 */
public class DefaultChildrenBuilder extends ExtensibleChildrenBuilder<DefaultChildrenBuilder> {

    /**
     * 创建默认子集合构建器实例
     *
     * @return 默认子集合构建器实例
     */
    public static DefaultChildrenBuilder builder() {
        return new DefaultChildrenBuilder();
    }
}