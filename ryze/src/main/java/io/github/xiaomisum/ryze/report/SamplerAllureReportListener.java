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
import io.github.xiaomisum.ryze.testelement.sampler.AbstractSampler;
import io.github.xiaomisum.ryze.testelement.sampler.Sampler;

/**
 * 取样器 Allure 报告监听器
 * <p>
 * SamplerAllureReportListener 是专门用于为取样器(Sampler)生成 Allure 测试报告的监听器。
 * 它会在取样器执行完成后创建对应的 Allure 测试步骤报告。
 * </p>
 * <p>
 * 该监听器支持所有实现了 Sampler 接口的取样器。
 * 在报告中会根据取样器是否有标题来决定显示名称：
 * <ul>
 * <li>如果取样器设置了标题，则显示标题</li>
 * <li>如果没有设置标题，则显示"匿名取样器：+ 取样器类名"</li>
 * </ul>
 * 同时，若当前线程上尚未有 TestContainer 设置过 Allure testcase 名称（例如 YAML 根节点直接为 Sampler
 * 的场景），
 * 则由首个 Sampler 负责设置 testcase 名称并折叠 suite 层级。
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"rawtypes"})
public class SamplerAllureReportListener implements AllureReportListener {

    /**
     * 判断当前监听器是否支持指定的上下文
     * <p>
     * 只有当测试元素是 Sampler 类型时才支持。
     * </p>
     *
     * @param context 上下文包装器
     * @return 如果测试元素是 Sampler 则返回 true，否则返回 false
     */
    @Override
    public boolean supports(ContextWrapper context) {
        return context.getTestElement() instanceof Sampler<?>;
    }

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
     * 在取样器执行完成后创建 Allure 测试步骤报告
     * <p>
     * 根据取样器类型和标题信息创建相应的 Allure 测试步骤，并立即结束该步骤。
     * 首个 Sampler 完成时，会结合 {@link AllureTestCaseHelper} 中的 TestContainer 嵌套栈
     * 一次性回填 parentSuite / suite / subSuite 三级 label 与 testcase name，
     * 从而使 Allure 左侧 Suites 导航呈现层级结构；
     * 当 YAML 根节点直接为 Sampler（栈为空）时，由首个 Sampler 的 title 作为 testcase name。
     * </p>
     *
     * @param context 上下文包装器
     */
    @Override
    public void afterCompletion(ContextWrapper context) {
        String title = resolveTitle(context);
        // 仅首次生效：首个 Sampler 完成时按 TestContainer 栈深度回填 Allure 层级 label
        AllureTestCaseHelper.trySetFromStack(title);
        AllureReportListener.startStep(title, context);
        // 若本 Sampler 被拦截器 preHandle 拒绝，将拦截事实标记到 step
        AllureReportListener.markRejectionIfAny(context);
        AllureReportListener.stopStep(context);
    }

    /**
     * 解析当前 Sampler 的显示标题。
     *
     * @param context 上下文包装器
     * @return 非空的标题字符串
     */
    private String resolveTitle(ContextWrapper context) {
        var handler = context.getTestElement();
        if (handler instanceof AbstractSampler<?, ?, ?> sampler) {
            return sampler.getRuntime().getTitle();
        } else if (handler instanceof Sampler<?> sampler) {
            return "匿名取样器：" + sampler.getClass().getSimpleName();
        }
        return "";
    }
}
