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

import io.github.xiaomisum.ryze.Configure;
import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.support.testng.annotation.AnnotationUtils;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.util.Objects;

/**
 * 方法执行监听器，用于监听@Test注解的测试方法是否是ryze测试方法，并管理测试会话
 * <p>
 * 该类实现了TestNG的 {@link IInvokedMethodListener}接口，
 * 在每个测试方法执行前后进行监听和处理。
 * 主要功能包括：
 * <ul>
 *   <li>识别Ryze测试方法</li>
 *   <li>在测试方法执行前创建测试框架会话</li>
 *   <li>在测试方法执行后清理测试框架会话</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * Created at 2025/8/2 12:58
 */
public class RyzeInvokedMethodListener implements IInvokedMethodListener, TestNGConstantsInterface {

    /**
     * 在测试方法执行前调用
     * <p>
     * 该方法会判断当前执行的方法是否为Ryze测试方法，如果是则:
     * 1. 在测试结果中添加RYZE_TEST_METHOD标识
     * 2. 创建一个新的测试框架会话
     * </p>
     *
     * @param method     被调用的方法
     * @param testResult 测试结果
     */
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (!method.isTestMethod()) {
            return;
        }
        if (!AnnotationUtils.isRyzeTest(testResult.getMethod().getConstructorOrMethod().getMethod())) {
            return;
        }
        // 添加 ryze test method 标识
        testResult.setAttribute(RYZE_TEST_METHOD, true);
        // 创建一个 在测试框架中运行时使用的 session
        SessionRunner.newTestFrameworkSession(Configure.defaultConfigure());
    }

    /**
     * 在测试方法执行后调用
     * <p>
     * 该方法会判断当前执行的方法是否为Ryze测试方法，如果是则移除测试会话
     * </p>
     *
     * @param method     被调用的方法
     * @param testResult 测试结果
     */
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (!method.isTestMethod() || (Objects.nonNull(testResult.getAttribute(RYZE_TEST_METHOD)) &&
                !(boolean) testResult.getAttribute(RYZE_TEST_METHOD))) {
            return;
        }
        SessionRunner.removeSession();
    }
}