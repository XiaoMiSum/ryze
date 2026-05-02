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

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.TestContainerExecutable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.qameta.allure.Allure.getLifecycle;

/**
 * 测试容器 Allure 报告监听器
 * <p>
 * TestContainerAllureReportListener 是专门用于为测试容器(TestContainer)生成 Allure
 * 测试报告的监听器。
 * 它会在测试容器执行前后创建对应的 Allure 测试步骤报告。
 * </p>
 * <p>
 * 该监听器支持所有实现了 TestContainerExecutable 接口的测试容器。
 * 在报告中会根据测试容器是否有标题来决定显示名称：
 * <ul>
 * <li>如果测试容器设置了标题，则显示标题</li>
 * <li>如果没有设置标题，则显示"匿名 - 测试容器类名"</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 *         Created at 2025/7/20 13:46
 */
public class TestContainerAllureReportListener implements AllureReportListener<TestContainerExecutable<?, ?, ?>> {

    private static final Logger logger = LoggerFactory.getLogger(TestContainerAllureReportListener.class);

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
     * 在测试容器执行前处理
     * <p>
     * 1. 将当前容器 title 压入线程级嵌套栈，供首个 Sampler 完成时按嵌套深度回填
     * parentSuite / suite / subSuite 三级 label，从而在 Allure 左侧 Suites 导航中呈现
     * 「项目 → 模块 → 接口 → 用例」的层级结构。
     * 2. 当检测到"兄弟用例"进入（即当前栈深度等于本 @Test 方法内首个 Sampler 已锚定的
     * {@link AllureTestCaseHelper#leafDepth() 叶子层深度}，且当前 allure testcase 已被
     * 上一个用例结束 stop/write）时，主动调用
     * {@link AllureTestCaseHelper#openSiblingTestCase()} 新建一个 allure testcase，
     * 使每个叶子级兄弟用例在 Allure 左侧导航中独立呈现。
     * 3. 仅当栈深度超过叶子层深度时，才将当前容器作为 Allure step 呈现在右侧 Test body 中，
     * 避免已被映射为 label / testcase name 的层级在右侧重复为 step。
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

        // 将当前容器 title 压入线程栈，首个 Sampler 完成时会据此回填 Allure 层级 label
        AllureTestCaseHelper.pushContainer(title);
        int depth = AllureTestCaseHelper.currentDepth();
        int leafDepth = AllureTestCaseHelper.leafDepth();

        // 兄弟用例识别：叶子深度已锚定 && 当前深度≤叶子深度 && 当前 testcase 已被上一个用例 stop
        // 放宽到"≤ 叶子深度"是为了覆盖 main 模式下的场景：
        // Ryze.start(...) 连续执行多个 yaml，或同一 yaml 内跨祖先兄弟（如 scene1 的 模块级1→模块级2 切换）时，
        // 在 depth < leafDepth 的祖先层已经出现"currentTestCase 为空 + 需要 startStep"的真空态；
        // 若等到 depth == leafDepth 才开 sibling，中间祖先层 step 将以 "no test case running" 报错丢失。
        if (leafDepth > 0 && depth <= leafDepth && getLifecycle().getCurrentTestCase().isEmpty()) {
            AllureTestCaseHelper.openSiblingTestCase();
        }

        // 是否抑制为 step 的判定统一由 Helper 提供，保证 preHandle/afterCompletion 的对称性，
        // 避免某层在 preHandle startStep 了但 afterCompletion 跳过 stopStep 导致 step 泄漏。
        if (!AllureTestCaseHelper.suppressStep(depth)) {
            AllureReportListener.startStep(title, context);
            // 记录实际 startStep 的 uuid —— afterCompletion 只对真正 startStep 过的 uuid
            // 执行 updateStep/stopStep，避免 suppressStep 在 pre/after 两次调用之间因
            // LEAF_DEPTH 状态变化导致判定不一致（即 pre 时 leaf=-1 全部抑制、after 时
            // leaf 已锚定部分层解除抑制），从而出现 "step with uuid xxx not found" 错误。
            AllureTestCaseHelper.markStartedStep(context.getUuid());
        }
        return true;
    }

    /**
     * 在测试容器执行完成后处理
     * <p>
     * 与 {@link #preHandle} 对称：
     * <ol>
     * <li>仅当当初真正 startStep 了的层级（栈深度 > 叶子层深度）才需要 updateStep + stopStep。</li>
     * <li>若当前容器本身就是叶子层（栈深度 == 叶子层深度），主动结束当前 allure testcase 生命周期
     * （stop + write），使该用例对应一份独立的 allure-results/*.json 落盘。</li>
     * <li>无论如何都要将线程栈对应层级弹出以维持嵌套深度的正确性。</li>
     * </ol>
     * </p>
     *
     * @param context 上下文包装器
     */
    @Override
    public void afterCompletion(ContextWrapper context) {
        // 注意此时尚未 popContainer，所以栈深度仍反映当前容器所处的层级
        int depth = AllureTestCaseHelper.currentDepth();
        int leafDepth = AllureTestCaseHelper.leafDepth();
        // 事实驱动：以 preHandle 阶段"是否真正 startStep"作为唯一判据，不再重新计算 suppressStep，
        // 彻底消除 LEAF_DEPTH 锚定状态在 pre/after 之间变化导致的不对称问题。
        boolean emittedAsStep = AllureTestCaseHelper.consumeStartedStep(context.getUuid());
        boolean isLeafContainer = leafDepth > 0 && depth == leafDepth;
        try {
            if (emittedAsStep) {
                AllureReportListener.markRejectionIfAny(context);
                getLifecycle().updateStep(context.getUuid(),
                        step -> step.setStatus(context.getTestResult().getStatus().getAllureStatus()));
                AllureReportListener.stopStep(context);
            }
            if (isLeafContainer) {
                // 叶子容器结束 → 主动落盘 testcase，后续兄弟用例将触发 openSiblingTestCase
                AllureTestCaseHelper.finishLeafTestCase(context.getTestResult().getStatus().getAllureStatus());
            }
        } finally {
            // 与 preHandle 对称：保证异常路径下栈也能正确回退
            AllureTestCaseHelper.popContainer();
        }
    }
}