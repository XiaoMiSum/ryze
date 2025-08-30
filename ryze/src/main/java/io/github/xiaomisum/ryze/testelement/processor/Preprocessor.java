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

package io.github.xiaomisum.ryze.testelement.processor;

import com.alibaba.fastjson2.annotation.JSONType;
import io.github.xiaomisum.ryze.support.fastjson.deserializer.PreprocessorObjectReader;

/**
 * 前置处理器接口
 * <p>
 * 前置处理器是在测试步骤执行之前运行的特殊处理器，用于执行测试前的准备工作。
 * 例如设置环境变量、准备测试数据、初始化外部资源等操作。
 * </p>
 * <p>
 * 前置处理器的典型应用场景包括：
 * <ul>
 *   <li>在HTTP请求发送前设置请求头或请求参数</li>
 *   <li>准备测试需要的数据，如创建测试用户或测试数据</li>
 *   <li>设置或重置测试环境状态</li>
 *   <li>执行前置条件检查</li>
 * </ul>
 * </p>
 * <p>
 * 该接口通过 {@link JSONType} 注解指定了特定的反序列化器 {@link PreprocessorObjectReader}，
 * 用于在从JSON配置文件加载处理器时正确地反序列化前置处理器对象。
 * </p>
 *
 * @author xiaomi
 */
@JSONType(deserializer = PreprocessorObjectReader.class)
public interface Preprocessor extends Processor {

}