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

package io.github.xiaomisum.ryze.testelement;

import io.github.xiaomisum.ryze.builder.IBuilder;

/**
 * 测试组件对象构建器接口
 * <p>
 * 该接口定义了测试组件构建器的标准，所有测试组件的构建器都应实现此接口。
 * 通过构建器模式，可以灵活地创建各种测试组件对象，支持链式调用和复杂的配置过程。
 * </p>
 *
 * @param <T> 测试组件对象类型
 */
public interface TestElementBuilder<T extends TestElement<?>> extends IBuilder<T> {

    /**
     * 构建测试组件对象
     * <p>
     * 根据构建器中设置的参数和配置，创建并返回一个测试组件对象实例。
     * 该方法是构建过程的最后一步，返回完全初始化的测试组件对象。
     * </p>
     *
     * @return 构建完成的测试组件对象
     */
    T build();
}