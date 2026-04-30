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
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Processor;
import org.apache.commons.lang3.StringUtils;

/**
 * 处理器 Allure 报告监听器
 * <p>
 * ProcessorAllureReportListener 是专门用于为处理器(Processor)生成 Allure 测试报告的监听器。
 * 它会在处理器执行完成后创建对应的 Allure 测试步骤报告。
 * </p>
 * <p>
 * 该监听器支持所有实现了 Processor 接口的处理器，包括前置处理器和后置处理器。
 * 在报告中会根据处理器是否有标题来决定显示名称：
 * <ul>
 *   <li>如果处理器设置了标题，则显示标题</li>
 *   <li>如果没有设置标题，则显示"匿名处理器：+ 处理器类名"</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"rawtypes"})
public class ProcessorAllureReportListener implements ReporterListener {

    /**
     * 判断当前监听器是否支持指定的上下文
     * <p>
     * 只有当测试元素是 Processor 类型时才支持。
     * </p>
     *
     * @param context 上下文包装器
     * @return 如果测试元素是 Processor 则返回 true，否则返回 false
     */
    @Override
    public boolean supports(ContextWrapper context) {
        return context.getTestElement() instanceof Processor;
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
     * 在处理器执行完成后创建 Allure 测试步骤报告
     * <p>
     * 根据处理器类型和标题信息创建相应的 Allure 测试步骤，并立即结束该步骤。
     * </p>
     *
     * @param context 上下文包装器
     */
    @Override
    public void afterCompletion(ContextWrapper context) {
        var handler = context.getTestElement();
        if (handler instanceof AbstractProcessor<?, ?, ?> processor) {
            AllureReportListener.startStep(StringUtils.isNotBlank(processor.getRuntime().getTitle()) ? processor.getRuntime().getTitle()
                    : "匿名处理器：" + processor.getClass().getSimpleName(), context);
        } else if (handler instanceof Processor processor) {
            AllureReportListener.startStep("匿名处理器：" + processor.getClass().getSimpleName(), context);
        }
        // 若本处理器被拦截器 preHandle 拒绝，将拦截事实标记到 step
        AllureReportListener.markRejectionIfAny(context);
        AllureReportListener.stopStep(context);
    }
}