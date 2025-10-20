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
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.Objects;

import static io.github.xiaomisum.ryze.support.testng.TestNGConstantsInterface.RYZE_TEST_CLASS;
import static io.github.xiaomisum.ryze.support.testng.TestNGConstantsInterface.RYZE_TEST_METHOD;

/**
 * Ryze测试基础类，用于TestNG框架集成
 * <p>
 * 该类提供了TestNG测试的基础支持，包括:
 * <ul>
 *   <li>在测试方法执行前自动创建Ryze会话</li>
 *   <li>根据注解标记识别Ryze测试方法</li>
 *   <li>在测试方法执行后清理会话资源</li>
 * </ul>
 * </p>
 *
 * @author mi_xiao@qq.com
 * @see AnnotationUtils#isRyzeTest(java.lang.reflect.Method)
 * @see SessionRunner#newTestFrameworkSessionIfNone(Configure)
 * @see SessionRunner#removeSession()
 */
public abstract class RyzeBasicTestcase4TestNG {

    /**
     * 测试方法执行前的准备工作
     * <p>
     * 主要功能包括:
     * <ol>
     *   <li>检测当前方法是否为测试方法</li>
     *   <li>检查方法是否有RyzeTest注解，并设置相应标识</li>
     *   <li>为测试类设置标识以避免重复创建会话</li>
     *   <li>创建测试框架专用的Ryze会话</li>
     * </ol>
     * </p>
     *
     * @param result TestNG测试结果对象，用于获取当前测试方法信息
     * @see TestNGConstantsInterface#RYZE_TEST_CLASS
     * @see TestNGConstantsInterface#RYZE_TEST_METHOD
     * @see SessionRunner#newTestFrameworkSessionIfNone(Configure)
     */
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(ITestResult result) {
        if (!result.getMethod().isTest()) {
            return;
        }
        if (AnnotationUtils.isRyzeTest(result.getMethod().getConstructorOrMethod().getMethod())) {
            // 添加 ryze test method 标识，以便监听器自动执行测试时的判断
            result.setAttribute(RYZE_TEST_METHOD, true);
        }
        // 添加 ryze test clazz 标识，以便监听器执行时不再创建 session
        result.setAttribute(RYZE_TEST_CLASS, true);
        // 创建一个 在测试框架中运行时使用的 session
        SessionRunner.newTestFrameworkSessionIfNone(Configure.defaultConfigure());
    }


    /**
     * 测试方法执行后的清理工作
     * <p>
     * 主要功能包括:
     * <ol>
     *   <li>检测当前方法是否为测试方法</li>
     *   <li>验证是否为Ryze测试类和测试方法</li>
     *   <li>移除测试会话以释放资源</li>
     * </ol>
     * </p>
     *
     * @param result TestNG测试结果对象，用于获取当前测试方法信息
     * @see TestNGConstantsInterface#RYZE_TEST_CLASS
     * @see TestNGConstantsInterface#RYZE_TEST_METHOD
     * @see SessionRunner#removeSession()
     */
    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        if (!result.getMethod().isTest()) {
            return;
        }
        if (!Objects.equals(result.getAttribute(RYZE_TEST_CLASS), true)) {
            return;
        }
        if (!Objects.equals(result.getAttribute(RYZE_TEST_METHOD), true)) {
            return;
        }
        SessionRunner.removeSession();
    }
}