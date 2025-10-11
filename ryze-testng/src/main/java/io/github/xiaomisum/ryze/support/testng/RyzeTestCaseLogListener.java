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

package io.github.xiaomisum.ryze.support.testng;

import org.slf4j.MDC;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG测试监听器，用于在测试执行期间管理日志追踪ID。
 * <p>
 * 该监听器通过SLF4J的MDC（Mapped Diagnostic Context）机制为每个测试方法设置唯一的追踪ID，
 * 追踪ID的格式为"{实例名}.{方法名}.{调用次数}"，便于在日志中区分不同测试方法的输出。
 * <p>
 * 在测试开始时设置追踪ID，在测试结束（无论成功、失败或跳过）时清除追踪ID。
 */
public class RyzeTestCaseLogListener implements ITestListener {

    private static final String KEY = "traceId";

    @Override
    public void onTestStart(ITestResult result) {
        setTraceID(result);
    }

    /**
     * 当测试方法成功执行时调用此方法。
     * 清除当前线程的追踪ID。
     *
     * @param result 包含测试方法结果信息的对象
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        removeTraceID();
    }

    /**
     * 当测试方法执行失败时调用此方法。
     * 清除当前线程的追踪ID。
     *
     * @param result 包含测试方法结果信息的对象
     */
    @Override
    public void onTestFailure(ITestResult result) {
        removeTraceID();
    }

    /**
     * 当测试方法被跳过时调用此方法。
     * 清除当前线程的追踪ID。
     *
     * @param result 包含测试方法结果信息的对象
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        removeTraceID();
    }

    /**
     * 当测试方法失败但在成功百分比范围内时调用此方法。
     * 清除当前线程的追踪ID。
     *
     * @param result 包含测试方法结果信息的对象
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        removeTraceID();
    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {

    }

    /**
     * 设置当前测试方法的追踪ID到MDC中。
     * 追踪ID格式为"{实例名}.{方法名}.{调用次数}"。
     *
     * @param result 包含测试方法信息的对象
     */
    private void setTraceID(ITestResult result) {
        MDC.put(KEY, result.getInstanceName() + "." + result.getName() + "." + result.getMethod().getCurrentInvocationCount());
    }

    /**
     * 从MDC中移除追踪ID。
     * 只有当追踪ID存在时才会执行移除操作。
     */
    private void removeTraceID() {
        if (MDC.get(KEY) != null) {
            MDC.remove(KEY);
        }
    }

}