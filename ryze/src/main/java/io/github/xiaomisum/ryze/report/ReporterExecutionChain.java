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
import io.github.xiaomisum.ryze.testelement.TestElement;

import java.util.List;

/**
 * 报告执行链，负责管理报告监听器的执行顺序和生命周期
 * <p>
 * ReporterExecutionChain 实现了报告监听器的链式执行机制，确保监听器按照预定义的顺序执行，
 * 并正确处理前置处理、后置处理和最终处理三个阶段。
 * </p>
 * <p>
 * 执行流程如下：
 * <ol>
 *   <li>前置处理(preHandle)：按顺序执行所有报告监听器的preHandle方法，如果任一监听器返回false，则中断执行并触发已完成监听器的afterCompletion</li>
 *   <li>业务处理：执行目标处理器的核心业务逻辑</li>
 *   <li>后置处理(postHandle)：按逆序执行所有报告监听器的postHandle方法</li>
 *   <li>最终处理(afterCompletion)：按逆序执行所有已执行过的报告监听器的afterCompletion方法</li>
 * </ol>
 * </p>
 * <p>
 * 该类通过executedIndex字段跟踪已成功执行前置处理的监听器索引，
 * 确保在异常情况下能够正确触发相应监听器的最终处理方法。
 * </p>
 *
 * @param <T> TestElement的子类型，表示被拦截的测试元件类型
 * @author xiaomi
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ReporterExecutionChain<T extends TestElement<?>> {
    /**
     * 报告监听器列表，按顺序存储所有注册的监听器
     */
    private final List<ReporterListener> reporters;

    /**
     * 已成功执行前置处理的监听器索引，用于异常情况下的清理工作
     * -1表示尚未执行任何监听器的前置处理
     */
    private int executedIndex = -1;

    /**
     * 构造一个报告执行链实例
     *
     * @param reporters 报告监听器列表，按执行优先级排序
     */
    public ReporterExecutionChain(List<ReporterListener> reporters) {
        this.reporters = reporters;
    }

    /**
     * 应用前置处理逻辑，按顺序执行所有报告监听器的preHandle方法
     * <p>
     * 前置处理阶段按报告监听器列表的顺序依次执行每个监听器的preHandle方法。
     * 如果某个监听器的preHandle方法返回false，表示中断执行流程，
     * </p>
     *
     * @param context 测试上下文，包含执行环境信息
     * @param runtime TestElement运行时数据
     */
    public void applyPreHandle(ContextWrapper context, T runtime) {
        for (int i = 0; i < reporters.size(); i++) {
            ReporterListener reporter = reporters.get(i);
            if (!reporter.preHandle(context, runtime)) {
                break;
            }
            executedIndex = i;
        }
    }

    /**
     * 应用后置处理逻辑，按逆序执行所有报告监听器的postHandle方法
     * <p>
     * 后置处理阶段在目标处理器的核心业务逻辑执行完成后进行，
     * 按报告监听器列表的逆序依次执行每个监听器的postHandle方法。
     * 这确保了与前置处理阶段相反的执行顺序，符合拦截器的对称性原则。
     * </p>
     *
     * @param context 测试上下文，包含执行环境信息
     * @param runtime TestElement运行时数据
     */
    public void applyPostHandle(ContextWrapper context, T runtime) {
        for (int i = reporters.size() - 1; i >= 0; i--) {
            reporters.get(i).postHandle(context, runtime);
        }
    }

    /**
     * 触发最终处理逻辑，按逆序执行所有已执行过前置处理的报告监听器的afterCompletion方法
     * <p>
     * 最终处理阶段在请求处理完成后进行，无论处理成功还是失败都会执行。
     * 按照与前置处理相反的顺序执行已成功执行过前置处理的报告监听器的afterCompletion方法，
     * 确保资源的正确释放和清理。
     * </p>
     *
     * @param context 测试上下文，包含执行环境信息
     */
    public void triggerAfterCompletion(ContextWrapper context) {
        for (int i = executedIndex; i >= 0; i--) {
            reporters.get(i).afterCompletion(context);
        }
    }
}
