/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * 'Software'), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.xiaomisum.ryze.testelement;

import io.github.xiaomisum.ryze.Configure;
import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.config.RyzeVariables;
import io.github.xiaomisum.ryze.context.Context;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.debug.config.DebugConfigureItem;
import io.github.xiaomisum.ryze.protocol.debug.sampler.DebugSampler;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * AbstractTestElementExecutable.updateCurrentContextInfo() 方法单元测试
 * <p>
 * 测试策略：
 * - 通过调用 run() 方法间接触试（因为 updateCurrentContextInfo 是 private 方法）
 * - 验证 run() 方法的上下文更新和恢复行为
 * - 验证 getContextChain() 的变量合并逻辑
 * </p>
 *
 * @author xiaomi
 */
public class AbstractTestElementExecutableUpdateContextTest {

    private Configure configure;
    private SessionRunner sessionRunner;

    @BeforeMethod
    public void setUp() {
        // 创建默认配置
        configure = Configure.defaultConfigure();

        // 创建 SessionRunner
        SessionRunner.newSession(configure);
        sessionRunner = SessionRunner.getSession();
    }

    /**
     * 测试场景 1：上下文恢复 - 执行后上下文链恢复到原始状态
     * <p>
     * 验证 run() 方法执行完成后：
     * - restoreCurrentContextInfo 恢复原始上下文链
     * - sessionRunner 的上下文链与执行前完全一致
     * </p>
     */
    @Test
    public void testUpdateContext_ContextRestoredAfterExecution() {
        // 记录初始状态
        List<Context> initialChain = sessionRunner.getContextChain();
        ContextWrapper initialContext = sessionRunner.getContext();

        // 创建并执行 TestSuite
        TestSuite testSuite = TestSuite.builder()
                .id("test-suite-id")
                .title("Test Suite")
                .children(List.of(
                        DebugSampler.builder()
                                .id("debug-1")
                                .title("Debug Sampler 1")
                                .config(DebugConfigureItem.builder().add("msg", "test").build())
                                .build()
                ))
                .build();

        testSuite.run(sessionRunner);

        // 验证上下文已恢复
        Assert.assertEquals(sessionRunner.getContextChain(), initialChain,
                "上下文链应恢复到初始引用");
        Assert.assertEquals(sessionRunner.getContext(), initialContext,
                "上下文包装器应恢复到初始值");
    }

    /**
     * 测试场景 2：ContextWrapper 恢复 - 执行后恢复到原始 ContextWrapper
     * <p>
     * 验证 run() 方法执行完成后：
     * - session 的 context 被恢复为原始实例
     * </p>
     */
    @Test
    public void testUpdateContext_ContextWrapperRestoredAfterExecution() {
        ContextWrapper initialContext = sessionRunner.getContext();

        // 创建 TestSuite
        TestSuite testSuite = TestSuite.builder()
                .id("test-suite-id")
                .title("Test Suite")
                .children(List.of(
                        DebugSampler.builder()
                                .id("debug-1")
                                .title("Debug Sampler 1")
                                .config(DebugConfigureItem.builder().add("msg", "test").build())
                                .build()
                ))
                .build();

        // 执行 run 方法
        testSuite.run(sessionRunner);

        // 验证执行后恢复到初始值
        Assert.assertEquals(sessionRunner.getContext(), initialContext,
                "执行后应恢复到初始 ContextWrapper");
    }

    /**
     * 测试场景 3：变量合并 - 执行后 runtime.configGroup.variables 包含当前元件的变量
     * <p>
     * 验证 run() 方法执行后：
     * - getContextChain() 设置了当前元件的 variables
     * - runtime.configGroup.variables 反映当前元件的变量
     * </p>
     */
    @Test
    public void testUpdateContext_RuntimeVariablesSetAfterExecution() {
        // 创建 TestSuite，设置变量
        TestSuite testSuite = TestSuite.builder()
                .id("test-suite-id")
                .title("Test Suite")
                .variables(Map.of(
                        "suite_var", "from_suite",
                        "another_var", "test_value"
                ))
                .children(List.of(
                        DebugSampler.builder()
                                .id("debug-1")
                                .title("Debug Sampler 1")
                                .config(DebugConfigureItem.builder().add("msg", "test").build())
                                .build()
                ))
                .build();

        // 执行 run 方法
        testSuite.run(sessionRunner);

        // 验证执行后 runtime.configGroup.variables 包含当前元件的变量
        Assert.assertNotNull(testSuite.runtime.configGroup.getVariables(),
                "runtime.configGroup.variables 应被设置");

        RyzeVariables vars = testSuite.runtime.configGroup.getVariables();
        Assert.assertEquals(vars.get("suite_var"), "from_suite",
                "suite 变量应可用");
        Assert.assertEquals(vars.get("another_var"), "test_value",
                "另一个变量应可用");
    }
}
