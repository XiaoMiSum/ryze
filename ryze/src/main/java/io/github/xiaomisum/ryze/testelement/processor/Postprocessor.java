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
import io.github.xiaomisum.ryze.support.fastjson.deserializer.PostprocessorObjectReader;

/**
 * 后置处理器接口
 * <p>
 * 后置处理器是在测试步骤执行之后运行的特殊处理器，用于执行测试后的清理和处理工作。
 * 例如提取响应数据、清理测试数据、关闭资源连接、生成报告等操作。
 * </p>
 * <p>
 * 后置处理器的典型应用场景包括：
 * <ul>
 *   <li>从HTTP响应中提取需要的值并保存为变量</li>
 *   <li>清理测试过程中创建的临时数据</li>
 *   <li>关闭数据库连接或其他资源</li>
 *   <li>执行结果验证和断言</li>
 *   <li>记录测试执行日志</li>
 * </ul>
 * </p>
 * <p>
 * 该接口通过 {@link JSONType} 注解指定了特定的反序列化器 {@link PostprocessorObjectReader}，
 * 用于在从JSON配置文件加载处理器时正确地反序列化后置处理器对象。
 * </p>
 *
 * @author xiaomi
 */
@JSONType(deserializer = PostprocessorObjectReader.class)
public interface Postprocessor extends Processor {
}