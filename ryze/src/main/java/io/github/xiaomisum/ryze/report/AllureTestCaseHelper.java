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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * Allure TestCase 辅助工具
 * <p>
 * 统一处理 Allure 测试用例的名称设置与 suite 层级映射。
 * <ul>
 * <li>维护当前线程的 TestContainer 嵌套路径（push/pop）</li>
 * <li>在首个 Sampler 完成时，按嵌套深度一次性回填 parentSuite / suite / subSuite 和 testcase
 * name</li>
 * <li>嵌套深度 -> Allure label 映射规则（所有场景统一挂载到 TestClass 顶层 suite 下）：
 * 
 * <pre>
 *     深度 0（仅 Sampler）: suite=TestClass简名, name = sampler.title
 *     深度 1            : suite=TestClass简名, name = path[0]
 *     深度 2            : parentSuite=TestClass简名, suite=path[0], name = path[1]
 *     深度 3            : parentSuite=TestClass简名, suite=path[0], subSuite=path[1], name = path[2]
 *     深度 >=4          : parentSuite=TestClass简名, suite=path[0], subSuite=path[1],
 *                         path[2..depth-2] 降级为右侧 Test body 的 step，name = path[depth-1]
 * </pre>
 * 
 * </li>
 * </ul>
 * 使用 ThreadLocal 标志确保单个 TestNG @Test 方法内 label 只回填一次（以最先触发的 Sampler 为准）。
 * </p>
 *
 * @author xiaomi
 */
public final class AllureTestCaseHelper {

    /**
     * 标记当前线程（当前 @Test 方法）是否已回填过 testcase 名称与 suite 层级。
     */
    private static final ThreadLocal<Boolean> TESTCASE_NAME_SET = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /**
     * 当前线程上次处理过的 Allure testcase uuid。
     * <p>
     * 当检测到当前 uuid 发生变化时，自动视为进入了新的 @Test 方法并重置标志，
     * 从而避免 report 模块依赖外部（如 TestNG InvokedMethodListener）显式调用 {@link #reset()}。
     * </p>
     */
    private static final ThreadLocal<String> LAST_TESTCASE_UUID = ThreadLocal.withInitial(() -> "");

    /**
     * 当前线程的 TestContainer 嵌套栈（栈底为最外层容器 title，栈顶为最内层容器 title）。
     */
    private static final ThreadLocal<Deque<String>> CONTAINER_STACK = ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * 当前线程的"叶子层级深度"快照：即本 @Test 方法里，首个 Sampler 完成时 TestContainer 栈的深度。
     * <p>
     * 该深度被视为 YAML 中"测试用例层"的自然边界，用于识别并列兄弟用例：
     * 每当栈深度回到该值并有新的 TestContainer 进入时，report 模块会主动 stop/write 前一个
     * allure testcase，并 schedule/start 一个新的 testcase，使 Allure 左侧导航呈现独立的兄弟 testcase。
     * </p>
     * <p>
     * 值为 -1 表示当前 @Test 方法尚未触发过首个 Sampler（未锚定叶子深度）。
     * </p>
     */
    private static final ThreadLocal<Integer> LEAF_DEPTH = ThreadLocal.withInitial(() -> -1);

    /**
     * 当前线程缓存的 testClass 全限定名（由 allure-testng 在 onTestStart 注入的 testClass label 取值）。
     * <p>
     * 首个 allure testcase 应用 label 时捕获；后续由 {@link #openSiblingTestCase()} 创建的
     * 兄弟 testcase 缺失该 label，通过本缓存回填，避免 Allure 左侧 Suites 导航出现"无根"的独立分组。
     * </p>
     */
    private static final ThreadLocal<String> TEST_CLASS_FULL_NAME = ThreadLocal.withInitial(() -> "");

    /**
     * 当前线程缓存的 testMethod 名称（由 allure-testng 在 onTestStart 注入的 testMethod label 取值）。
     * <p>
     * 与 {@link #TEST_CLASS_FULL_NAME} 配合，用于构造跨次执行稳定的 fullName / historyId：
     * 保证同一个叶子用例在多次执行中具有一致的 Allure 身份，从而被正确聚合为一条 history / trend。
     * </p>
     */
    private static final ThreadLocal<String> TEST_METHOD_NAME = ThreadLocal.withInitial(() -> "");

    /**
     * 当前线程中已被 {@link #finishLeafTestCase} 主动 stop+write 的 testcase uuid 集合。
     * <p>
     * 用于 main 模式下 {@link #finalizeOwnedTestCase} 的存活检查：
     * <ul>
     * <li>main 模式下 {@link #runInTestCase} 会创建一个 owned testcase 包围整个 yaml 执行；</li>
     * <li>若该 owned testcase 恰好也被作为叶子 testcase 被 {@link #finishLeafTestCase} 提前写出，
     * 则 finally 块里 {@link #finalizeOwnedTestCase} 再次尝试 update/stop/write 时
     * Allure 会以 "test case with uuid xxx not found" 报错；</li>
     * <li>引入本集合后，owned uuid 一旦落入其中即视为 dead，finalize 直接静默返回，避免错误日志。</li>
     * </ul>
     * </p>
     */
    private static final ThreadLocal<Set<String>> DEAD_UUIDS = ThreadLocal.withInitial(HashSet::new);

    /**
     * 当前线程中在 preHandle 阶段真正完成 {@code startStep} 调用的 TestContainer uuid 集合。
     * <p>
     * 修复背景：{@link #suppressStep(int)} 的判定依赖 {@link #LEAF_DEPTH}，
     * 但 LEAF_DEPTH 在首个 Sampler 运行时才锚定。这导致同一个 TestContainer 在
     * preHandle 时（leaf=-1，所有层均抑制）未 startStep，但在 afterCompletion 时
     * （leaf 已锚定，某些祖先层 suppress 返回 false）又尝试 stopStep 一个并不存在的 step，
     * 触发 Allure "Could not stop step: step with uuid xxx not found" 错误。
     * </p>
     * <p>
     * 解决策略：不再用 suppressStep 在 afterCompletion 重算一次，
     * 而是以 preHandle 阶段的"实际发生"为事实：真正 startStep 的 uuid 才会被加入本集合，
     * afterCompletion 通过 {@link #consumeStartedStep(String)} 原子查询并移除。
     * </p>
     */
    private static final ThreadLocal<Set<String>> STEP_STARTED_UUIDS = ThreadLocal.withInitial(HashSet::new);

    private AllureTestCaseHelper() {
    }

    /**
     * 进入 TestContainer 时调用：将当前容器 title 压入线程栈。
     * <p>
     * <b>自我闭环兜底</b>：当检测到"从空栈开始入第一层"（即新的 @Test 方法根容器进入）时，
     * 自动清理上一轮 @Test 方法残留的 {@link #LEAF_DEPTH}、{@link #TESTCASE_NAME_SET}、
     * {@link #LAST_TESTCASE_UUID}、{@link #TEST_CLASS_FULL_NAME} 等线程级状态。
     * 避免在外部测试框架监听器（例如 ryze-testng 模块的 RyzeInvokedMethodListener.beforeInvocation）
     * 未如期调度 {@link #reset()} 时（如 SPI 未生效），
     * 因 ThreadLocal 残留污染下一个 @Test 方法的层级判定（典型症状：
     * 上一个方法叶子深度为 4 时，下一个方法深度 3 的容器被误判为"中间层"生成多余 step）。
     * </p>
     *
     * @param title 当前 TestContainer 的 title（可为空串，空串也会入栈以保持深度一致性）
     */
    public static void pushContainer(String title) {
        Deque<String> stack = CONTAINER_STACK.get();
        if (stack.isEmpty()) {
            // 新 @Test 方法根容器入栈的瞬间：自动清理上一轮方法残留的线程级状态
            LEAF_DEPTH.remove();
            TESTCASE_NAME_SET.remove();
            LAST_TESTCASE_UUID.remove();
            TEST_CLASS_FULL_NAME.remove();
            TEST_METHOD_NAME.remove();
            DEAD_UUIDS.remove();
            STEP_STARTED_UUIDS.remove();
        }
        stack.push(title == null ? "" : title);
    }

    /**
     * 离开 TestContainer 时调用：将栈顶元素弹出。
     */
    public static void popContainer() {
        Deque<String> stack = CONTAINER_STACK.get();
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }

    /**
     * 返回当前线程 TestContainer 栈的深度。
     * <p>
     * 主要用于 {@link TestContainerAllureReportListener} 判断当前容器在 Allure 报告中的呈现方式：
     * 与 {@link #leafDepth() 叶子层深度} 结合使用，深度 ≤ 叶子深度的容器映射为层级 label / testcase name，
     * 不在右侧 Test body 生成重复的 step；深度 &gt; 叶子深度的更深层容器才生成 step。
     * </p>
     *
     * @return 当前线程 TestContainer 栈的深度
     */
    public static int currentDepth() {
        return CONTAINER_STACK.get().size();
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
        // 存活检查：若该 uuid 已被 finishLeafTestCase 主动 write 落盘（典型场景：main 模式下
        // owned testcase 恰好作为首个叶子 testcase 被先写出），则静默跳过，
        // 避免 Allure "test case with uuid xxx not found" 噪声错误日志。
        if (DEAD_UUIDS.get().contains(uuid)) {
            return;
        }
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
     * 尝试设置 Allure testcase 名称（仅首个调用生效），并按栈深度映射 suite 层级。
     * <p>
     * 首个 Sampler 完成时由 {@link SamplerAllureReportListener#afterCompletion} 调用；
     * 后续 Sampler 调用将被 TESTCASE_NAME_SET 标志拦截，保持首次写入的结果不被覆盖。
     * </p>
     *
     * @param samplerTitle 当前（首个）Sampler 的 title，用于栈为空（仅 Sampler 场景）时回填 testcase
     *                     name
     */
    public static void trySetFromStack(String samplerTitle) {
        AllureLifecycle lifecycle = Allure.getLifecycle();
        String currentUuid = lifecycle.getCurrentTestCase().orElse(null);

        // uuid 变化 → 新 allure testcase（TestNG 新 @Test 方法 或 ryze 主动切分的兄弟 testcase）
        // 自动重置线程级标志（report 模块自我闭环，不依赖外部 reset 调用）
        if (StringUtils.isNotBlank(currentUuid) && !currentUuid.equals(LAST_TESTCASE_UUID.get())) {
            TESTCASE_NAME_SET.set(Boolean.FALSE);
            LAST_TESTCASE_UUID.set(currentUuid);
        }

        if (Boolean.TRUE.equals(TESTCASE_NAME_SET.get())) {
            return;
        }
        TESTCASE_NAME_SET.set(Boolean.TRUE);

        // 快照栈内容：从栈底（最外层容器）到栈顶（最内层容器）
        Deque<String> stack = CONTAINER_STACK.get();
        int depth = stack.size();
        List<String> path = new ArrayList<>(depth);
        Iterator<String> it = stack.descendingIterator();
        while (it.hasNext()) {
            path.add(it.next());
        }

        // 首次锚定叶子层深度：本 @Test 方法内出现的第一个 Sampler 所处栈深度
        // 视为 YAML 中"测试用例层"的自然边界，后续的兄弟用例将在该深度按需切分 testcase
        if (LEAF_DEPTH.get() < 0) {
            LEAF_DEPTH.set(depth);
        }

        // 按栈深度决定 testcase name
        String testcaseName = resolveTestCaseName(path, samplerTitle);

        try {
            // 显式按 uuid 更新，避免当前线程 currentStep 指向子 step 时 updateTestCase(Consumer) 的歧义
            if (StringUtils.isNotBlank(currentUuid)) {
                lifecycle.updateTestCase(currentUuid, testResult -> applyNameAndLabels(testResult, testcaseName, path));
            } else {
                lifecycle.updateTestCase(testResult -> applyNameAndLabels(testResult, testcaseName, path));
            }
        } catch (Exception ignored) {
            // Allure lifecycle 在非测试上下文中可能不可用，静默忽略
        }
    }

    /**
     * 返回当前线程已锚定的叶子层深度。
     *
     * @return 叶子层深度；{@code -1} 表示尚未锚定（本 @Test 方法内还未触发过首个 Sampler）
     */
    public static int leafDepth() {
        return LEAF_DEPTH.get();
    }

    /**
     * 判定当前深度的 TestContainer 是否应抑制为 step（不生成右侧 Test body 的 step）。
     * <p>
     * 规则：
     * <ul>
     * <li>叶子深度未锚定时一律抑制，保证 preHandle / afterCompletion 对称，避免 step 泄漏。</li>
     * <li>label 占位层（parentSuite=TestClass, suite=path[0], subSuite=path[1]）抑制：
     * 即 {@code depth <= min(leafDepth-1, 2)} 的层。</li>
     * <li>叶子层本身对应 testcase name，不作为 step 抑制：即 {@code depth == leafDepth}。</li>
     * <li>介于其间的中间层（仅在 leafDepth &gt;= 4 时存在）不抑制，作为右侧 step 呈现
     * 用户在 YAML 中声明的完整嵌套路径。</li>
     * </ul>
     * </p>
     *
     * @param depth 当前 TestContainer 在线程栈中的深度
     * @return 是否抑制为 step
     */
    public static boolean suppressStep(int depth) {
        int leaf = LEAF_DEPTH.get();
        if (leaf < 0) {
            return true;
        }
        int labelOccupied = Math.min(leaf - 1, 2);
        return depth <= labelOccupied || depth == leaf;
    }

    /**
     * 记录某个 TestContainer 的 preHandle 阶段真正完成了 {@code startStep}。
     * <p>
     * 与 {@link #consumeStartedStep(String)} 配对使用，afterCompletion 只对真正
     * startStep 过的 uuid 执行 updateStep / stopStep，避免 suppressStep 判定结果
     * 在 pre / after 两次调用之间因 {@link #LEAF_DEPTH} 锚定状态变化而不一致。
     * </p>
     *
     * @param uuid 当前 TestContainer 对应的 Allure step uuid
     */
    public static void markStartedStep(String uuid) {
        if (uuid != null) {
            STEP_STARTED_UUIDS.get().add(uuid);
        }
    }

    /**
     * 消费一次 startStep 标记：若存在则移除并返回 true，否则返回 false。
     * <p>
     * afterCompletion 调用该方法决定是否需要 updateStep + stopStep，
     * 从而以"实际发生"取代"重新计算 suppressStep"，彻底消除 pre/after 不对称问题。
     * </p>
     *
     * @param uuid 当前 TestContainer 对应的 Allure step uuid
     * @return 是否曾真正 startStep
     */
    public static boolean consumeStartedStep(String uuid) {
        if (uuid == null) {
            return false;
        }
        return STEP_STARTED_UUIDS.get().remove(uuid);
    }

    /**
     * 结束当前 allure testcase 的生命周期（stop + write）。
     * <p>
     * 由 {@link TestContainerAllureReportListener} 在叶子层 TestContainer 结束时调用，
     * 使每个叶子级用例对应一份独立的 allure-results/*.json 落盘。
     * </p>
     *
     * @param status 当前 testcase 的最终状态
     */
    public static void finishLeafTestCase(Status status) {
        AllureLifecycle lifecycle = Allure.getLifecycle();
        String uuid = lifecycle.getCurrentTestCase().orElse(null);
        if (StringUtils.isBlank(uuid)) {
            return;
        }
        try {
            lifecycle.updateTestCase(uuid, tr -> {
                tr.setStage(Stage.FINISHED);
                if (tr.getStatus() == null) {
                    tr.setStatus(status == null ? Status.PASSED : status);
                }
                tr.setStop(System.currentTimeMillis());
            });
            lifecycle.stopTestCase(uuid);
            lifecycle.writeTestCase(uuid);
            // 记录为 dead，避免 finalizeOwnedTestCase 在 finally 中再次尝试
            // update/stop/write 同一 uuid（典型场景：main 模式下 owned testcase 恰好即首个叶子）
            DEAD_UUIDS.get().add(uuid);
        } catch (Exception ignored) {
            // 静默：allure 状态异常不应影响业务执行
        }
    }

    /**
     * 主动创建并启动一个新的 allure testcase，用于承载下一个叶子级兄弟用例。
     * <p>
     * 由 {@link TestContainerAllureReportListener} 在检测到兄弟用例进入时调用：
     * 新 testcase 的 name / parentSuite / suite / subSuite、fullName、historyId 将由首个
     * Sampler 触发
     * {@link #trySetFromStack} → {@link #applyNameAndLabels} 时自动回填为跨次稳定值，
     * 因此这里的 {@code name} 和 {@code fullName} 仅为临时占位，不影响最终落盘结果。
     * </p>
     */
    public static void openSiblingTestCase() {
        AllureLifecycle lifecycle = Allure.getLifecycle();
        String uuid = UUID.randomUUID().toString();
        lifecycle.scheduleTestCase(new TestResult()
                .setUuid(uuid)
                .setName("Ryze")
                // 占位 fullName —— 将由 applyStableIdentity 基于 testClass.testMethod + YAML
                // 路径覆盖为跨次稳定值
                .setFullName("ryze.sibling." + uuid)
                .setStage(Stage.RUNNING)
                .setStart(System.currentTimeMillis()));
        lifecycle.startTestCase(uuid);
        // uuid 变化将由 trySetFromStack 通过 LAST_TESTCASE_UUID 比对后自动重置 TESTCASE_NAME_SET
    }

    /**
     * 将解析后的 testcase name 与层级 label 应用到给定的 TestResult。
     * <p>
     * 同时调用 {@link #applyStableIdentity} 写入跨次执行确定性稳定的 fullName 与 historyId，
     * 保证多次执行同一个 YAML 用例在 Allure trend / history 中被正确聚合为同一条记录。
     * </p>
     */
    private static void applyNameAndLabels(TestResult testResult, String testcaseName, List<String> path) {
        if (StringUtils.isNotBlank(testcaseName)) {
            testResult.setName(testcaseName);
        }
        applySuiteLabels(testResult.getLabels(), path);
        applyStableIdentity(testResult, path);
    }

    /**
     * 为 TestResult 写入跨次执行稳定的 fullName 和 historyId。
     * <p>
     * <b>底层逻辑</b>：Allure 跨次执行的聚合唯一标识是 {@code historyId}，默认由
     * {@code md5(fullName + parametersHash)} 计算。若 fullName 或 parameters 包含每次执行都变化的
     * 内容（例如随机 UUID、运行时对象 hash），同一用例在多次执行中会得到不同的 historyId，
     * 导致被 Allure 识别为不同用例、trend 无法聚合。
     * </p>
     * <p>
     * <b>稳定策略</b>：fullName = {@code testClass.testMethod} + 可选 {@code #YAML容器路径}。
     * historyId = md5(fullName)，显式设置后 Allure 不会再拼 parameters hash，避免被 ryze 运行时对象的
     * {@code toString()}（如 {@code TestSuite@xxx}）污染。
     * </p>
     *
     * @param testResult 当前 allure TestResult
     * @param path       当前 testcase 在 YAML 中的容器路径（从外到内）；根节点直接为 Sampler 时可为空
     */
    private static void applyStableIdentity(TestResult testResult, List<String> path) {
        String testClassFullName = labelValue(testResult.getLabels(), "testClass");
        if (StringUtils.isBlank(testClassFullName)) {
            testClassFullName = TEST_CLASS_FULL_NAME.get();
        }
        String testMethodName = labelValue(testResult.getLabels(), "testMethod");
        if (StringUtils.isBlank(testMethodName)) {
            testMethodName = TEST_METHOD_NAME.get();
        }
        // main / API 模式兜底：TestNG 未注入 testClass/testMethod label 且线程缓存亦为空时，
        // 通过当前调用栈找到 Ryze.start(...) 的外部 caller（用户业务代码帧），
        // 以其 {ClassName, MethodName} 作为 seed。
        // 跨次稳定性：只要用户 main / 调度入口不变，seed 就不变 → fullName / historyId 跨次一致。
        if (StringUtils.isBlank(testClassFullName) || StringUtils.isBlank(testMethodName)) {
            String[] callerSeed = resolveMainModeCallerSeed();
            if (StringUtils.isBlank(testClassFullName)) {
                testClassFullName = callerSeed[0];
            }
            if (StringUtils.isBlank(testMethodName)) {
                testMethodName = callerSeed[1];
            }
        }
        // 最终兜底：仍然无法确定 seed 时退出，保留原占位 fullName，避免抛异常影响业务
        if (StringUtils.isBlank(testClassFullName) || StringUtils.isBlank(testMethodName)) {
            return;
        }
        StringBuilder fullName = new StringBuilder(testClassFullName).append('.').append(testMethodName);
        if (path != null && !path.isEmpty()) {
            fullName.append('#').append(String.join("/", path));
        }
        String stableFullName = fullName.toString();
        testResult.setFullName(stableFullName);
        testResult.setHistoryId(md5(stableFullName));
    }

    /**
     * 通过当前线程调用栈定位 {@code io.github.xiaomisum.ryze.Ryze.start(...)} 的外部 caller，
     * 作为 main / API 模式下的 {testClass, testMethod} seed。
     * <p>
     * 规则：跳过连续的 {@code Ryze.start} 帧（因 start(String) 内部会再调 start(Map)），
     * 取栈中第一个 Ryze.start 之后紧邻的用户栈帧。找不到则返回固定兜底值。
     * </p>
     * <p>
     * 跨次稳定性：栈帧的 className / methodName 是编译期静态信息，
     * 同一 main 入口多次执行产生的 seed 完全一致。
     * </p>
     *
     * @return 长度为 2 的数组：{callerClassName, callerMethodName}
     */
    private static String[] resolveMainModeCallerSeed() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        int i = 0;
        // 找到第一个 Ryze.start 帧
        while (i < stack.length
                && !("io.github.xiaomisum.ryze.Ryze".equals(stack[i].getClassName())
                        && "start".equals(stack[i].getMethodName()))) {
            i++;
        }
        if (i >= stack.length) {
            return new String[] { "ryze.main.Unknown", "run" };
        }
        // 跳过连续的 Ryze.start 重载帧（start(String) -> start(Map)）
        while (i + 1 < stack.length
                && "io.github.xiaomisum.ryze.Ryze".equals(stack[i + 1].getClassName())
                && "start".equals(stack[i + 1].getMethodName())) {
            i++;
        }
        if (i + 1 >= stack.length) {
            return new String[] { "ryze.main.Unknown", "run" };
        }
        StackTraceElement caller = stack[i + 1];
        return new String[] { caller.getClassName(), caller.getMethodName() };
    }

    /**
     * 从 label 列表取指定 name 对应的首个非空 value。
     */
    private static String labelValue(List<Label> labels, String name) {
        if (labels == null) {
            return null;
        }
        return labels.stream()
                .filter(l -> name.equals(l.getName()))
                .map(Label::getValue)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse(null);
    }

    /**
     * 计算字符串的 MD5 十六进制摘要（小写）。
     */
    private static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // MD5 为 JDK 必备算法，不会走到此分支；作为保险兜底返回原字符串
            return s;
        }
    }

    /**
     * 兼容入口：旧版本监听器通过 {@link #trySetTestCaseName} 只设置 name + 折叠单层 suite。
     * <p>
     * 新版本推荐使用 {@link #trySetFromStack}。本方法保留以避免外部调用方破坏。
     * </p>
     *
     * @param title 顶层 title
     * @deprecated 新代码请使用 {@link #trySetFromStack(String)}
     */
    @Deprecated
    public static void trySetTestCaseName(String title) {
        trySetFromStack(title);
    }

    /**
     * 重置当前线程的状态（TestCase 名称写入标志 + 容器栈）。
     * 应在每个 TestNG @Test 方法开始前调用，确保新方法从干净状态开始。
     */
    public static void reset() {
        TESTCASE_NAME_SET.remove();
        LAST_TESTCASE_UUID.remove();
        CONTAINER_STACK.remove();
        LEAF_DEPTH.remove();
        TEST_CLASS_FULL_NAME.remove();
        TEST_METHOD_NAME.remove();
        DEAD_UUIDS.remove();
        STEP_STARTED_UUIDS.remove();
    }

    /**
     * 根据栈深度决定 testcase name：
     * <ul>
     * <li>深度 0：使用 samplerTitle（仅 Sampler 场景）</li>
     * <li>深度 &gt;= 1：使用栈顶 title，即最深 TestContainer 的 title（叶子容器自身）</li>
     * </ul>
     */
    private static String resolveTestCaseName(List<String> path, String samplerTitle) {
        int depth = path.size();
        if (depth == 0) {
            return samplerTitle;
        }
        return path.get(depth - 1);
    }

    /**
     * 按栈深度回填 parentSuite / suite / subSuite 三级 label。
     * <p>
     * 映射规则见类注释：所有场景统一挂载到 TestClass 简名作为 Allure 左侧 Suites 导航的顶层。
     * </p>
     *
     * @param labels 当前 testcase 的 label 列表（原地修改）
     * @param path   从最外层到最内层的 TestContainer title 路径
     */
    private static void applySuiteLabels(List<Label> labels, List<String> path) {
        if (labels == null) {
            return;
        }
        // 先移除 allure-testng 默认设置的三层 suite 标签
        labels.removeIf(l -> "parentSuite".equals(l.getName())
                || "suite".equals(l.getName())
                || "subSuite".equals(l.getName()));

        // 1) 优先取当前 testcase 自带的 testClass label（allure-testng 在 onTestStart 注入）
        String testClassFullName = labels.stream()
                .filter(l -> "testClass".equals(l.getName()))
                .map(Label::getValue)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse(null);
        // 2) 捕获后缓存到线程级变量；兄弟 testcase 创建时 label 里没有 testClass，届时从缓存回填
        if (StringUtils.isNotBlank(testClassFullName)) {
            TEST_CLASS_FULL_NAME.set(testClassFullName);
        } else {
            String cached = TEST_CLASS_FULL_NAME.get();
            if (StringUtils.isNotBlank(cached)) {
                testClassFullName = cached;
                // 将 testClass label 补齐到当前 testcase，保持 Allure 其它分组（behaviors/categories）一致性
                labels.add(new Label().setName("testClass").setValue(cached));
            }
        }
        // testMethod label 与 testClass 对称处理：捕获 / 缓存 / 回填，保证兄弟 testcase 也有该 label，
        // 后续 applyStableIdentity 才能基于 testClass + testMethod 构造跨次稳定的 fullName /
        // historyId
        String testMethodName = labels.stream()
                .filter(l -> "testMethod".equals(l.getName()))
                .map(Label::getValue)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse(null);
        if (StringUtils.isNotBlank(testMethodName)) {
            TEST_METHOD_NAME.set(testMethodName);
        } else {
            String cached = TEST_METHOD_NAME.get();
            if (StringUtils.isNotBlank(cached)) {
                labels.add(new Label().setName("testMethod").setValue(cached));
            }
        }
        String testClassSimpleName = simpleClassName(testClassFullName);

        int depth = path.size();
        if (depth <= 1) {
            // 深度 0/1：仅设置 suite = TestClass 简名
            if (StringUtils.isNotBlank(testClassSimpleName)) {
                labels.add(new Label().setName("suite").setValue(testClassSimpleName));
            }
            return;
        }

        // 深度 >=2：parentSuite = TestClass 简名（统一顶层），YAML 层级依次下推
        if (StringUtils.isNotBlank(testClassSimpleName)) {
            labels.add(new Label().setName("parentSuite").setValue(testClassSimpleName));
        }
        // suite = path[0]（YAML 最外层 TestContainer）
        labels.add(new Label().setName("suite").setValue(path.get(0)));
        if (depth == 2) {
            return;
        }
        // 深度 >=3：subSuite = path[1]（YAML 次外层 TestContainer）
        // 对于深度 >=4 的场景，path[2..depth-2] 不再占用 label 位，而是作为右侧 Test body 的 step 展示
        labels.add(new Label().setName("subSuite").setValue(path.get(1)));
    }

    private static String simpleClassName(String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return null;
        }
        int idx = fullName.lastIndexOf('.');
        return idx > 0 && idx < fullName.length() - 1 ? fullName.substring(idx + 1) : fullName;
    }
}
