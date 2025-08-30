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

package io.github.xiaomisum.ryze.extractor;

import io.github.xiaomisum.ryze.testelement.processor.ProcessResult;

/**
 * 提取结果类，封装提取器执行后的结果信息
 * 
 * <p>该类用于存储提取器执行后的结果数据，包括：
 * <ul>
 *   <li>提取到的值</li>
 *   <li>提取过程的状态信息</li>
 *   <li>提取失败时的异常信息</li>
 * </ul></p>
 * 
 * <p>提取结果的状态通过继承自ProcessResult的status字段表示，
 * 可以是成功(passed)、失败(failed)或损坏(broken)等状态。</p>
 * 
 * @author xiaomi
 * @see ProcessResult
 */
public class ExtractResult extends ProcessResult {

    /**
     * 提取结果值
     * 
     * <p>存储从目标数据中提取到的实际值，类型可以是任意对象。
     * 当提取成功时，该字段包含提取到的数据；
     * 当提取失败时，该字段可能为null。</p>
     */
    private Object value;

    /**
     * 提取失败时的异常对象
     * 
     * <p>当提取过程中发生异常时，将异常对象存储在此字段中，
     * 以便上层代码能够获取详细的错误信息。</p>
     */
    private Exception exception;

    /**
     * 构造一个新的提取结果实例
     *
     * @param title 结果标题，用于标识该提取结果的来源或目的
     */
    public ExtractResult(String title) {
        super(title);
    }

    /**
     * 获取提取结果值
     *
     * @return 提取到的值，可能为null
     */
    public Object getValue() {
        return value;
    }

    /**
     * 设置提取结果值
     *
     * @param value 提取到的值
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * 获取提取失败时的异常对象
     *
     * @return 异常对象，如果未发生异常则为null
     */
    public Exception getException() {
        return exception;
    }

    /**
     * 设置提取失败时的异常对象
     *
     * @param exception 异常对象
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
}