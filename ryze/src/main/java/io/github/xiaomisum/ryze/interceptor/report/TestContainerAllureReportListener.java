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
import io.github.xiaomisum.ryze.testelement.TestContainerExecutable;
import org.apache.commons.lang3.StringUtils;

import static io.qameta.allure.Allure.getLifecycle;

/**
 * 测试容器 Allure 报告监听器
 * <p>
 * TestContainerAllureReportListener 是专门用于为测试容器(TestContainer)生成 Allure 测试报告的监听器。
 * 它会在测试容器执行前后创建对应的 Allure 测试步骤报告。
 * </p>
 * <p>
 * 该监听器支持所有实现了 TestContainerExecutable 接口的测试容器。
 * 在报告中会根据测试容器是否有标题来决定显示名称：
 * <ul>
 *   <li>如果测试容器设置了标题，则显示标题</li>
 *   <li>如果没有设置标题，则显示"匿名 - 测试容器类名"</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * Created at 2025/7/20 13:46
 */
public class TestContainerAllureReportListener implements AllureReportListener<TestContainerExecutable<?, ?, ?>> {

    /**
     * 获取监听器的执行顺序
     * <p>
     * 返回 Integer.MIN_VALUE + 1 确保在日志监听器之后执行。
     * </p>
     *
     * @return 执行顺序值
     */
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1;
    }

    /**
     * 判断当前监听器是否支持指定的上下文
     * <p>
     * 只有当测试元素是 TestContainerExecutable 类型时才支持。
     * </p>
     *
     * @param context 上下文包装器
     * @return 如果测试元素是 TestContainerExecutable 则返回 true，否则返回 false
     */
    @Override
    public boolean supports(ContextWrapper context) {
        return context.getTestElement() instanceof TestContainerExecutable<?, ?, ?>;
    }

    /**
     * 在测试容器执行前创建 Allure 测试步骤报告
     * <p>
     * 根据测试容器的标题信息创建相应的 Allure 测试步骤并启动该步骤。
     * </p>
     *
     * @param context 上下文包装器
     * @param runtime 运行时测试容器
     * @return 始终返回 true，表示继续执行后续处理
     */
    @Override
    public boolean preHandle(ContextWrapper context, TestContainerExecutable<?, ?, ?> runtime) {
        var title = StringUtils.isNotBlank(context.getTestResult().getTitle()) ? context.getTestResult().getTitle()
                : "匿名 - " + context.getTestElement().getClass().getSimpleName();
        AllureReportListener.startStep(title, context);
        return true;
    }

    /**
     * 在测试容器执行完成后更新并结束 Allure 测试步骤报告
     * <p>
     * 根据测试容器的执行结果更新测试步骤状态，并结束该步骤。
     * </p>
     *
     * @param context 上下文包装器
     */
    @Override
    public void afterCompletion(ContextWrapper context) {
        getLifecycle().updateStep(context.getUuid(), step -> step.setStatus(context.getTestResult().getStatus().getAllureStatus()));
        AllureReportListener.stopStep(context);
    }
}