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
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Processor;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 处理器日志监听器
 * <p>
 * ProcessorLogListener 是用于记录处理器执行日志的监听器，
 * 在处理器执行前后输出相关信息到日志系统。
 * </p>
 * <p>
 * 该监听器提供以下日志功能：
 * <ul>
 *   <li>在处理器执行前记录开始信息</li>
 *   <li>在处理器执行后记录请求和响应信息</li>
 *   <li>记录异常堆栈信息</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * Created at 2025/7/20 13:46
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProcessorLogListener implements ReporterListener {

    static final Logger log = LoggerFactory.getLogger("");

    /**
     * 获取监听器的执行顺序
     * <p>
     * 返回 Integer.MIN_VALUE 确保在所有监听器中最先执行。
     * </p>
     *
     * @return 执行顺序值
     */
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

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
     * 在处理器执行前记录日志信息
     * <p>
     * 输出处理器的标题信息，如果未设置标题则显示"匿名 - 处理器类名"。
     * </p>
     *
     * @param context 上下文包装器
     * @param runtime 运行时测试元素
     * @return 始终返回 true，表示继续执行后续处理
     */
    @Override
    public boolean preHandle(ContextWrapper context, TestElement runtime) {
        var title = runtime instanceof AbstractProcessor ? ((AbstractProcessor) runtime).getTitle() : "";
        log.info("执行处理器：{}\n", StringUtils.isNotBlank(title) ? title : "匿名 - " + context.getTestElement().getClass().getSimpleName());
        return true;
    }

    /**
     * 在处理器执行完成后记录请求和响应信息
     * <p>
     * 输出处理器执行过程中的请求和响应详细信息，以及异常堆栈（如果有的话）。
     * </p>
     *
     * @param context 上下文包装器
     */
    @Override
    public void afterCompletion(ContextWrapper context) {
        if (context.getTestResult() instanceof SampleResult result) {
            log.info("{}{}{}{}{}{}",
                    "\n--------------- 请求信息 -----------------\n", Objects.isNull(result.getRequest()) ? "" : result.getRequest().format(), "\n",
                    "\n--------------- 响应信息 -----------------\n", Objects.isNull(result.getResponse()) ? "" : result.getResponse().format(), "\n");
            printStackTrace(result.getThrowable(), log);
        }
    }
}