/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2026.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze.report;

import io.github.xiaomisum.ryze.Result;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Stage;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.TestResult;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Allure TestCase 辅助工具
 * <p>
 * 用于统一处理 Allure 测试用例的名称设置与 suite 层级折叠。
 * <ul>
 *   <li>testcase name：由 YAML 最顶层 title（首个 TestSuite 或首个 Sampler）决定</li>
 *   <li>suite 层级：折叠 parentSuite / suite / subSuite 三个默认 label，仅保留单层 suite=TestClass 简名</li>
 * </ul>
 * 使用 ThreadLocal 标志确保单个 TestNG @Test 方法内只设置一次（以最先触发的为准）。
 * </p>
 *
 * @author xiaomi
 */
public final class AllureTestCaseHelper {

    /**
     * 标记当前线程（当前 @Test 方法）是否已设置过 testcase 名称。
     */
    private static final ThreadLocal<Boolean> TESTCASE_NAME_SET = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private AllureTestCaseHelper() {
    }

    /**
     * 在 Allure testcase 生命周期内执行指定动作。
     * <p>
     * 如果当前线程已存在外部驱动创建的 testcase（例如 TestNG 模式下由 allure-testng 创建），
     * 则直接透传执行，不做额外的生命周期管理。
     * </p>
     * <p>
     * 否则（如 Main / API 调用模式），由本方法自建 testcase 生命周期（
     * scheduleTestCase → startTestCase → 执行动作 → stopTestCase → writeTestCase），
     * 以保证 allure-results 能够落盘。
     * </p>
     *
     * @param action 需要在 testcase 生命周期内执行的动作
     * @return 动作的返回值
     */
    public static Result runInTestCase(Supplier<Result> action) {
        AllureLifecycle lifecycle = Allure.getLifecycle();
        boolean owned = lifecycle.getCurrentTestCase().isEmpty();
        String uuid = null;
        if (owned) {
            uuid = UUID.randomUUID().toString();
            lifecycle.scheduleTestCase(new TestResult()
                    .setUuid(uuid)
                    .setName("Ryze")
                    .setFullName("ryze.main." + uuid)
                    .setStage(Stage.RUNNING)
                    .setStart(System.currentTimeMillis()));
            lifecycle.startTestCase(uuid);
        }
        Result result = null;
        Throwable fatal = null;
        try {
            result = action.get();
            return result;
        } catch (RuntimeException | Error e) {
            fatal = e;
            throw e;
        } finally {
            if (owned) {
                finalizeOwnedTestCase(lifecycle, uuid, result, fatal);
                reset();
            }
        }
    }

    /**
     * 完成自建 testcase 的收尾工作：根据 Result / Throwable 回填 status，然后 stop + write。
     * 任何异常均静默吸收，避免覆盖业务路径上的真实异常。
     */
    private static void finalizeOwnedTestCase(AllureLifecycle lifecycle, String uuid, Result result, Throwable fatal) {
        try {
            lifecycle.updateTestCase(uuid, tr -> {
                tr.setStage(Stage.FINISHED);
                if (fatal != null) {
                    tr.setStatus(Status.BROKEN);
                    tr.setStatusDetails(new StatusDetails().setMessage(fatal.getMessage()));
                } else if (result != null) {
                    tr.setStatus(result.getStatus() != null ? result.getStatus().getAllureStatus() : Status.PASSED);
                    if (result.getThrowable() != null) {
                        tr.setStatusDetails(new StatusDetails().setMessage(result.getThrowable().getMessage()));
                    }
                } else if (tr.getStatus() == null) {
                    tr.setStatus(Status.PASSED);
                }
                tr.setStop(System.currentTimeMillis());
            });
            lifecycle.stopTestCase(uuid);
            lifecycle.writeTestCase(uuid);
        } catch (Exception ignored) {
            // Allure 生命周期异常不应影响业务结果
        }
    }

    /**
     * 尝试设置 Allure testcase 名称（仅首个调用生效），并折叠 suite 层级。
     * 后续调用将被忽略，以确保 testcase 名称为 YAML 最顶层 title。
     *
     * @param title YAML 最顶层 title
     */
    public static void trySetTestCaseName(String title) {
        if (Boolean.TRUE.equals(TESTCASE_NAME_SET.get())) {
            return;
        }
        TESTCASE_NAME_SET.set(Boolean.TRUE);
        if (StringUtils.isBlank(title)) {
            return;
        }
        try {
            Allure.getLifecycle().updateTestCase(testResult -> {
                testResult.setName(title);
                collapseSuiteLabels(testResult.getLabels());
            });
        } catch (Exception ignored) {
            // Allure lifecycle 在非测试上下文中可能不可用，静默忽略
        }
    }

    /**
     * 重置当前线程的标志。应在每个 TestNG @Test 方法开始前调用，
     * 以便新的测试方法能够重新设置 testcase 名称。
     */
    public static void reset() {
        TESTCASE_NAME_SET.remove();
    }

    /**
     * 折叠 Allure TestNG 默认生成的 parentSuite / suite / subSuite 三层 label，
     * 仅保留一层 suite = TestClass 简名。
     *
     * @param labels 当前 testcase 的 label 列表（可直接修改）
     */
    private static void collapseSuiteLabels(List<Label> labels) {
        if (labels == null) {
            return;
        }
        // 提取 TestClass 简名（去掉包名）
        String testClassFullName = labels.stream()
                .filter(l -> "testClass".equals(l.getName()))
                .map(Label::getValue)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse(null);
        String simpleName = simpleClassName(testClassFullName);

        // 移除 allure-testng 默认设置的三层 suite 标签
        labels.removeIf(l -> "parentSuite".equals(l.getName())
                || "suite".equals(l.getName())
                || "subSuite".equals(l.getName()));

        // 重新添加单层 suite = TestClass 简名
        if (StringUtils.isNotBlank(simpleName)) {
            labels.add(new Label().setName("suite").setValue(simpleName));
        }
    }

    private static String simpleClassName(String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return null;
        }
        int idx = fullName.lastIndexOf('.');
        return idx > 0 && idx < fullName.length() - 1 ? fullName.substring(idx + 1) : fullName;
    }
}
