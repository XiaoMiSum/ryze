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
package io.github.xiaomisum.ryze.interceptor;

import io.github.xiaomisum.ryze.Configure;
import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.TestStatus;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * HandlerExecutionChain preHandle 拦截行为单元测试
 * <p>
 * 验证：当拦截器 preHandle 返回 false 时
 * 1. 结果记录拦截者标识（result.rejectBy = 拦截器 SimpleName）
 * 2. 若结果尚未被业务改写，status 自动置为 skipped
 * 3. 若拦截器已显式设置 status 或 throwable，保留不覆盖
 * </p>
 *
 * @author xiaomi
 */
public class HandlerExecutionChainRejectionTest {

    private ContextWrapper context;

    @BeforeMethod
    public void setUp() {
        SessionRunner.newSession(Configure.defaultConfigure());
        context = new ContextWrapper(SessionRunner.getSession());
        context.setTestResult(new DefaultSampleResult("test-case"));
    }

    /**
     * 场景 1：所有拦截器 preHandle 返回 true → result.rejectBy 保持 null，status 保持 passed
     */
    @Test
    public void testAllInterceptorsPassed() {
        var chain = new HandlerExecutionChain<>(List.of(new AcceptInterceptor()));
        boolean passed = chain.applyPreHandle(context, null);

        Assert.assertTrue(passed, "所有拦截器放行，applyPreHandle 应返回 true");
        Assert.assertNull(context.getTestResult().getRejectBy(), "未发生拦截，rejectBy 应为 null");
        Assert.assertEquals(context.getTestResult().getStatus(), TestStatus.passed, "status 应保持 passed");
    }

    /**
     * 场景 2：某拦截器返回 false → result.rejectBy 记录拦截器 SimpleName，status 自动置为 skipped
     */
    @Test
    public void testRejectedByInterceptor_AutoMarkSkipped() {
        var rejecting = new RejectInterceptor();
        var chain = new HandlerExecutionChain<>(List.of(new AcceptInterceptor(), rejecting));
        boolean passed = chain.applyPreHandle(context, null);

        Assert.assertFalse(passed, "有拦截器投反对票，applyPreHandle 应返回 false");
        Assert.assertEquals(context.getTestResult().getRejectBy(), RejectInterceptor.class.getSimpleName(),
                "result.rejectBy 应记录拦截器 SimpleName");
        Assert.assertEquals(context.getTestResult().getStatus(), TestStatus.skipped,
                "业务未改写 status 时，应自动置为 skipped 以便报告可见");
    }

    /**
     * 场景 3：拦截器自身已设置 status=failed → rejectBy 仍写入，但 status 不被覆盖
     */
    @Test
    public void testRejected_RespectPreSetStatus() {
        var chain = new HandlerExecutionChain<>(List.of(new RejectAndMarkFailedInterceptor()));
        chain.applyPreHandle(context, null);

        Assert.assertEquals(context.getTestResult().getRejectBy(),
                RejectAndMarkFailedInterceptor.class.getSimpleName(),
                "rejectBy 应记录拦截器标识");
        Assert.assertEquals(context.getTestResult().getStatus(), TestStatus.failed,
                "拦截器已显式 setStatus，HandlerExecutionChain 不应覆盖");
    }

    /**
     * 场景 4：拦截器自身设置了 throwable → rejectBy 仍写入，status 保持原值不被覆盖为 skipped
     */
    @Test
    public void testRejected_RespectPreSetThrowable() {
        var chain = new HandlerExecutionChain<>(List.of(new RejectAndMarkThrowableInterceptor()));
        chain.applyPreHandle(context, null);

        Assert.assertEquals(context.getTestResult().getRejectBy(),
                RejectAndMarkThrowableInterceptor.class.getSimpleName(),
                "rejectBy 应记录拦截器标识");
        Assert.assertNotNull(context.getTestResult().getThrowable(),
                "拦截器设置的 throwable 应保留");
        Assert.assertEquals(context.getTestResult().getStatus(), TestStatus.passed,
                "throwable 已被拦截器设置，HandlerExecutionChain 不应篡改 status");
    }

    // ================= 测试用 Interceptor =================

    static class AcceptInterceptor implements RyzeInterceptor<TestElement> {
        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean supports(ContextWrapper context) {
            return true;
        }

        @Override
        public boolean preHandle(ContextWrapper context, TestElement runtime) {
            return true;
        }
    }

    static class RejectInterceptor implements RyzeInterceptor<TestElement> {
        @Override
        public int getOrder() {
            return 1;
        }

        @Override
        public boolean supports(ContextWrapper context) {
            return true;
        }

        @Override
        public boolean preHandle(ContextWrapper context, TestElement runtime) {
            return false;
        }
    }

    static class RejectAndMarkFailedInterceptor implements RyzeInterceptor<TestElement> {
        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean supports(ContextWrapper context) {
            return true;
        }

        @Override
        public boolean preHandle(ContextWrapper context, TestElement runtime) {
            context.getTestResult().setStatus(TestStatus.failed);
            return false;
        }
    }

    static class RejectAndMarkThrowableInterceptor implements RyzeInterceptor<TestElement> {
        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean supports(ContextWrapper context) {
            return true;
        }

        @Override
        public boolean preHandle(ContextWrapper context, TestElement runtime) {
            context.getTestResult().setThrowable(new IllegalStateException("custom reason"));
            return false;
        }
    }
}
