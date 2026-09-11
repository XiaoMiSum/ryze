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

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.Configure;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.report.AllureTestCaseHelper;
import io.github.xiaomisum.ryze.support.testng.annotation.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private static final Logger log = LoggerFactory.getLogger(RyzeInvokedMethodListener.class);

    /**
     * 在测试方法执行前调用
     * <p>
     * 该方法会判断当前执行的方法是否为Ryze测试方法，如果是则:
     * 1. 在测试结果中添加RYZE_TEST_METHOD标识
     * 2. 创建一个新的测试框架会话
     * </p>
     *
     * @param method 被调用的方法
     * @param result 测试结果
     */
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        if (!method.isTestMethod()) {
            return;
        }
        var isExtendRyzeBasicTestNGTestcase = RyzeBasicTestcase4TestNG.class.isAssignableFrom(method.getTestMethod().getTestClass().getRealClass());
        var isRyzeTest = AnnotationUtils.isRyzeTest(result.getMethod().getConstructorOrMethod().getMethod());
        if (!isExtendRyzeBasicTestNGTestcase && !isRyzeTest) {
            // 如果 不是 RyzeBasicTestcase4TestNG 的子类 且 没有 RyzeTest 注解，则无需处理 Ryze框架中的业务
            return;
        }
        // 每个测试方法执行前重置 Allure testcase 名称标志，以便本方法内的首个 TestSuite / Sampler 能重新设置名称
        AllureTestCaseHelper.reset();
        // 添加 ryze test method 标识
        result.setAttribute(RYZE_TEST_METHOD, true);
        // 创建一个 在测试框架中运行时使用的 session
        SessionRunner.newTestFrameworkSessionIfNone(Configure.defaultConfigure(true));
    }

    /**
     * 在测试方法执行后调用
     * <p>
     * 该方法会判断当前执行的方法是否为Ryze测试方法，如果是则：
     * <ul>
     *   <li>将带有 {@code __Ryze_Native_Result__} 属性的原生测试结果序列化为 JSON，
     *       保存到工作目录下的 {@code __Ryze_Native_Result__} 目录，文件名为结果的 id 或 title；</li>
     *   <li>移除测试会话</li>
     * </ul>
     * </p>
     *
     * @param method 被调用的方法
     * @param result 测试结果
     */
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (!method.isTestMethod()) {
            return;
        }
        if (!Objects.equals(result.getAttribute(RYZE_TEST_METHOD), true)) {
            //  RYZE_TEST_METHOD 标志不是 true，则不在监听器中移除 session
            return;
        }
        exportNativeResult(result.getAttribute(RYZE_NATIVE_RESULT));
        SessionRunner.removeSession();
    }

    /**
     * 将 ryze 引擎执行后的原生测试结果序列化为 JSON 并落盘
     * <p>
     * 输出目录为当前工作目录下的 {@code ./__Ryze_Native_Result__}，文件名为结果的 id（缺省时退化为 title），
     * 扩展名为 {@code .json}。属性不存在（例如测试方法由用户手动调用）时静默跳过。
     * </p>
     *
     * @param nativeResult 由 {@link RyzeTestcaseAutoRunListener} 写入的 ryze 原生结果对象
     */
    private void exportNativeResult(Object nativeResult) {
        if (!(nativeResult instanceof Result ryzeResult)) {
            return;
        }
        var fileName = ryzeResult.getId() != null ? ryzeResult.getId() : ryzeResult.getTitle();
        if (fileName == null || fileName.trim().isBlank()) {
            return;
        }
        try {
            Path dir = Paths.get("./__Ryze_Native_Result__");
            Files.createDirectories(dir);
            Files.writeString(dir.resolve(String.format("%s.json", fileName)), JSON.toJSONString(ryzeResult), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("导出 ryze 原生测试结果失败: {}", fileName);
        }
    }
}