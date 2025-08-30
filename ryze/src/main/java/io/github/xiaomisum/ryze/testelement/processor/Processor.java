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

import io.github.xiaomisum.ryze.context.ContextWrapper;

/**
 * 处理器接口，定义了处理器的基本行为
 * <p>
 * 处理器是在测试执行过程中用于执行特定处理逻辑的组件，分为前置处理器和后置处理器两种类型。
 * 前置处理器在测试步骤执行前运行，后置处理器在测试步骤执行后运行。
 * </p>
 * <p>
 * 处理器的主要用途包括：
 * <ul>
 *   <li>在测试步骤执行前准备数据或环境</li>
 *   <li>在测试步骤执行后清理资源或处理结果</li>
 *   <li>提取测试结果中的变量供后续步骤使用</li>
 *   <li>执行特定的验证逻辑</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public interface Processor {

    /**
     * 是否禁用当前处理器
     * <p>
     * 当返回true时表示禁用该处理器，处理器将不会被执行。
     * 默认实现返回false，表示默认不禁用。
     * </p>
     *
     * @return true 表示禁用，默认不禁用
     */
    default boolean isDisabled() {
        return false;
    }

    /**
     * 执行处理器逻辑
     * <p>
     * 这是处理器的核心方法，在测试执行过程中会被调用。
     * 根据处理器类型（前置或后置），该方法会在测试步骤的相应阶段被调用。
     * </p>
     *
     * @param context 上下文包装器，包含测试执行过程中的各种信息
     */
    void process(ContextWrapper context);
}