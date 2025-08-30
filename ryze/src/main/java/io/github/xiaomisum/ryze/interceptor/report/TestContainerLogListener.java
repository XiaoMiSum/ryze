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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试容器日志监听器
 * <p>
 * TestContainerLogListener 是用于记录测试容器执行日志的监听器，
 * 在测试容器执行前输出相关信息到日志系统。
 * </p>
 * <p>
 * 该监听器提供以下日志功能：
 * <ul>
 *   <li>在测试容器执行前记录开始信息</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * Created at 2025/7/20 13:46
 */
public class TestContainerLogListener implements ReporterListener<TestContainerExecutable<?, ?, ?>> {

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
     * 在测试容器执行前记录日志信息
     * <p>
     * 输出测试容器的标题信息，如果未设置标题则显示"匿名 - 测试容器类名"。
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
        log.info("开始测试：{}\n", title);
        return true;
    }
}