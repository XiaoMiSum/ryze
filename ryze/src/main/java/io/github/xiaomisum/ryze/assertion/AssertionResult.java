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

package io.github.xiaomisum.ryze.assertion;

import io.github.xiaomisum.ryze.testelement.processor.ProcessResult;

/**
 * 断言结果类，用于记录断言执行的结果信息
 *
 * <p>该类继承自ProcessResult，专门用于存储断言执行的结果信息，
 * 包括断言标题、执行状态、消息等。</p>
 *
 * <p>在断言执行过程中，会创建AssertionResult对象来记录断言的执行情况，
 * 包括成功或失败的信息，以及相关的描述信息。</p>
 *
 * @author xiaomi
 * @see ProcessResult 处理结果基类
 */
@Deprecated(since = "6.0.2", forRemoval = true)
public class AssertionResult extends ProcessResult {

    /**
     * 构造函数，创建指定标题的断言结果对象
     *
     * @param title 断言结果标题
     */
    public AssertionResult(String title) {
        super(title);
    }
}