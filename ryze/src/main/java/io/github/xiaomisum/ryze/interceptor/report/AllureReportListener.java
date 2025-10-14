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

package io.github.xiaomisum.ryze.interceptor.report;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.qameta.allure.Allure;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.StringJoiner;

import static io.github.xiaomisum.ryze.TestStatus.broken;
import static io.github.xiaomisum.ryze.TestStatus.failed;
import static io.qameta.allure.util.ResultsUtils.getStatus;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;

/**
 * Allure 测试报告监听器接口
 * <p>
 * AllureReportListener 提供了与 Allure 测试报告框架集成的功能，
 * 用于在测试执行过程中生成详细的测试步骤报告。
 * </p>
 * <p>
 * 该接口主要功能包括：
 * <ul>
 *   <li>启动测试步骤并设置步骤状态</li>
 *   <li>停止测试步骤</li>
 *   <li>根据测试结果设置适当的 Allure 状态</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * Created at 2025/7/20 14:15
 */
public interface AllureReportListener<T extends AbstractTestElement<?, ?, ?>> extends ReporterListener<T> {

    /**
     * 启动 Allure 测试步骤
     * <p>
     * 功能上相当于 <code>Allure.step(...)</code>，创建一个新的测试步骤并启动它。
     * 根据测试结果中的异常信息或状态设置步骤的 Allure 状态和详细信息。
     * </p>
     *
     * @param name    步骤名称
     * @param context 上下文包装器，包含测试执行过程中的各种信息
     */
    static void startStep(String name, ContextWrapper context) {
        var uuid = context.getUuid();
        var step = new StepResult().setName(name)
                .setStatus(getStatus(context.getTestResult().getThrowable()).orElse(context.getTestResult().getStatus().getAllureStatus()))
                .setStatusDetails(getStatusDetails(context.getTestResult().getThrowable()).orElse(new StatusDetails()));
        if (context.getTestResult() instanceof SampleResult result) {
            var details = step.getStatusDetails() != null ? step.getStatusDetails() : new StatusDetails();
            var joiner = new StringJoiner("\n\n");
            if (StringUtils.isNotBlank(details.getMessage())) {
                joiner.add(step.getStatusDetails().getMessage());
            }
            if (result.getRequest() != null) {
                joiner.add(result.getRequest().format());
            }
            if (result.getResponse() != null) {
                joiner.add(result.getResponse().format());
            }
            joiner.add(result.getRequest().format() + "\n\n" + result.getResponse().format());
            step.getStatusDetails().setMessage(joiner.toString());
        }
        Allure.getLifecycle().startStep(uuid, step);
    }

    /**
     * 停止 Allure 测试步骤
     * <p>
     * 结束指定上下文关联的测试步骤。
     * </p>
     *
     * @param context 上下文包装器，用于标识要停止的测试步骤
     */
    static void stopStep(ContextWrapper context) {
        var startTime = context.getTestResult() instanceof SampleResult result ? result.getSampleStartTime() : context.getTestResult().getStartTime();
        // 使用测试结果中的开始时间，如果为空则使用 startStep执行时的默认时间
        if (startTime != null) {
            Allure.getLifecycle().updateStep(context.getUuid(), step -> step.setStart(Timestamp.valueOf(startTime).getTime()));
        }
        if (!Objects.isNull(context.getTestResult().getThrowable())) {
            context.getTestResult().setStatus(context.getTestResult().getThrowable() instanceof AssertionError ? failed : broken);
        }
        Allure.getLifecycle().stopStep(context.getUuid());
    }
}