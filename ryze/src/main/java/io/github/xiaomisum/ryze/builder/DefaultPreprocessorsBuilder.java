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
 * 默认前置处理器列表构建器，提供 core模块下所有前置处理器构建
 *
 * <p>该构建器是 {@link ExtensiblePreprocessorsBuilder}的具体实现，
 * 提供了核心模块中所有前置处理器的构建方法。</p>
 *
 * <p>前置处理器在测试元件核心业务逻辑执行前运行，用于执行准备工作、数据初始化等操作。
 * 通过该构建器可以方便地构建各种类型的前置处理器。</p>
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>提供核心模块下所有前置处理器的构建方法</li>
 *   <li>支持与提取器构建器集成</li>
 *   <li>支持流畅的构建器模式</li>
 *   <li>可作为其他自定义前置处理器构建器的基础</li>
 * </ul></p>
 *
 * @author xiaomi
 */
public class DefaultPreprocessorsBuilder extends ExtensiblePreprocessorsBuilder<DefaultPreprocessorsBuilder, DefaultExtractorsBuilder> {

    /**
     * 创建默认前置处理器列表构建器实例
     *
     * @return 默认前置处理器列表构建器实例
     */
    public static DefaultPreprocessorsBuilder builder() {
        return new DefaultPreprocessorsBuilder();
    }
}