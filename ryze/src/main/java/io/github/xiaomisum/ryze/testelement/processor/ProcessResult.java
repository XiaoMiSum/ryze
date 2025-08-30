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

import io.github.xiaomisum.ryze.TestStatus;

/**
 * 处理器执行结果类
 * <p>
 * ProcessResult 用于封装处理器执行的结果信息，包括处理器的标题、执行状态和错误信息。
 * 该类主要用于跟踪和记录处理器的执行情况，便于测试报告生成和问题诊断。
 * </p>
 *
 * @author xiaomi
 */
public class ProcessResult {

    /**
     * Processor 名称
     */
    private final String title;
    private TestStatus status = TestStatus.passed;
    /**
     * 失败时的异常信息
     */
    private String message;

    /**
     * 构造函数，使用指定的标题创建处理器执行结果
     *
     * @param title 处理器标题
     */
    public ProcessResult(String title) {
        this.title = title;
    }

    /**
     * 获取处理器执行状态
     *
     * @return 执行状态
     */
    public TestStatus getStatus() {
        return status;
    }

    /**
     * 设置处理器执行状态
     *
     * @param status 执行状态
     */
    public void setStatus(TestStatus status) {
        this.status = status;
    }

    /**
     * 获取失败时的异常信息
     *
     * @return 异常信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置失败时的异常信息
     *
     * @param message 异常信息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取处理器标题
     *
     * @return 处理器标题
     */
    public String getTitle() {
        return title;
    }
}