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

public abstract class RyzeBasicTestcase4TestNG {

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(ITestResult result) {
        if (!result.getMethod().isTest()) {
            return;
        }
        if (!AnnotationUtils.isRyzeTest(result.getMethod().getConstructorOrMethod().getMethod())) {
            return;
        }
        // 添加 ryze test clazz 标识，以便监听器执行时不再创建 session
        result.setAttribute(RYZE_TEST_CLASS, true);
        // 添加 ryze test method 标识，以便监听器自动执行测试时的判断
        result.setAttribute(RYZE_TEST_METHOD, true);
        // 创建一个 在测试框架中运行时使用的 session
        SessionRunner.newTestFrameworkSessionIfNone(Configure.defaultConfigure());
    }


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
