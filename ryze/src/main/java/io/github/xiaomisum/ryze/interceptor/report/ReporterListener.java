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

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.interceptor.RyzeInterceptor;
import io.github.xiaomisum.ryze.testelement.TestElement;
import org.slf4j.Logger;

/**
 * 测试报告监听器接口
 * <p>
 * ReporterListener 是测试报告生成的核心接口，继承自 RyzeInterceptor，
 * 用于在测试执行过程中收集信息并生成测试报告。
 * </p>
 * <p>
 * 该接口提供了以下功能：
 * <ul>
 *   <li>打印异常堆栈信息到日志</li>
 *   <li>在测试框架环境中重新抛出异常</li>
 *   <li>提供测试元素的拦截处理能力</li>
 * </ul>
 * </p>
 *
 * @param <T> 测试元素类型
 * @author xiaomi
 * Created at 2025/7/20 14:15
 */
public interface ReporterListener<T extends TestElement<?>> extends RyzeInterceptor<T> {


    /**
     * 打印异常堆栈信息到日志
     * <p>
     * 如果 throwable 不为 null，则使用指定的日志记录器记录异常信息和堆栈跟踪。
     * 如果当前会话在测试框架支持模式下运行，则重新抛出异常。
     * </p>
     *
     * @param throwable 异常对象
     * @param log       日志记录器
     */
    default void printStackTrace(Throwable throwable, Logger log) {
        if (throwable == null) {
            return;
        }
        log.error(throwable.getMessage(), throwable);
        try {
            if (SessionRunner.getSession().isRunInTestFrameworkSupport()) {
                throw throwable;
            }
        } catch (AssertionError | RuntimeException t) {
            throw t;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}