/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * 'Software'), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */
package io.github.xiaomisum.ryze.testelement.sampler;

import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.testelement.TestElement;

/**
 * Sampler 接口，表示一个测试元件是最基本的测试执行单元，其下可以存在子元件，如配置元件、前后置处理器等。
 *
 * <p>Sampler 一般是各种协议请求实现，如 JDBC 请求、HTTP 请求、Dubbo 请求等等，
 * 或者是最基本的动作，如打开一个网页、点击一个按钮等等。</p>
 * 
 * <p>作为测试执行的基本单元，Sampler负责执行具体的测试操作，并生成相应的测试结果。
 * 它是测试流程中的叶子节点，不包含其他子测试元件。</p>
 * 
 * <p>Sampler的主要特点：
 * <ul>
 *   <li>作为测试执行的基本单元，不包含子元件</li>
 *   <li>负责执行具体的测试操作，如发送请求、执行命令等</li>
 *   <li>生成测试结果，供上层组件处理和分析</li>
 *   <li>支持前置处理器、后置处理器、断言和变量提取等扩展功能</li>
 * </ul></p>
 *
 * @param <T> 测试结果类型，表示执行结果的具体实现
 * @author xiaomi
 */
public interface Sampler<T extends Result> extends TestElement<T> {


}