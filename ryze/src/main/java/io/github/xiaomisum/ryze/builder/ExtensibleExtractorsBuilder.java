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

import io.github.xiaomisum.ryze.testelement.AbstractTestElement;

/**
 * 可扩展的提取器列表构建器，可通过扩展此类实现自定义的提取器列表构建器
 * 
 * <p>该构建器用于构建测试元件的变量提取器列表，允许用户通过继承此类来添加自定义的提取器构建方法。
 * 它是所有提取器列表构建器的基类，提供了构建提取器列表的通用方法。</p>
 * 
 * <p>提取器用于从测试执行结果中提取变量，供后续测试步骤使用，实现测试步骤间的数据传递。</p>
 * 
 * <p>主要功能包括：
 * <ul>
 *   <li>提供构建提取器列表的框架</li>
 *   <li>支持通过继承扩展自定义提取器构建方法</li>
 *   <li>与测试元件构建系统集成</li>
 * </ul></p>
 *
 * @param <SELF> 实际的构建器类型，用于实现流畅的构建器模式
 * @author xiaomi
 */
public abstract class ExtensibleExtractorsBuilder<SELF extends ExtensibleExtractorsBuilder<SELF>> extends AbstractTestElement.ExtractorsBuilder<SELF> {
}