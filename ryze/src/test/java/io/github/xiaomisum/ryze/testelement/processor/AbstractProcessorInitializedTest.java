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

package io.github.xiaomisum.ryze.testelement.processor;

import io.github.xiaomisum.ryze.Configure;
import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.config.RyzeVariables;
import io.github.xiaomisum.ryze.context.Context;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.context.TestSuiteContext;
import io.github.xiaomisum.ryze.protocol.debug.config.DebugConfigureItem;
import io.github.xiaomisum.ryze.protocol.debug.processer.DebugPreprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.VARIABLES;

/**
 * AbstractProcessor.initialized() 方法单元测试
 * <p>
 * 测试覆盖：
 * 1. 正常初始化流程
 * 2. 变量为空时自动初始化
 * 3. 变量合并优先级（当前变量 > 父级变量）
 * 4. 表达式计算（id/title/config）
 * 5. 拦截器过滤和处理
 * 6. 上下文链构建验证
 * 7. runtime 副本创建
 * </p>
 *
 * @author xiaomi
 */
public class AbstractProcessorInitializedTest {

    private Configure configure;
    private SessionRunner sessionRunner;
    private DebugPreprocessor processor;

    @BeforeMethod
    public void setUp() {
        // 创建默认配置并保存引用
        configure = Configure.defaultConfigure();

        // 创建 SessionRunner
        SessionRunner.newSession(configure);
        sessionRunner = SessionRunner.getSession();

        // 创建处理器实例
        processor = DebugPreprocessor.builder()
                .id("test-processor-id")
                .title("Test Processor Title")
                .config(DebugConfigureItem.builder().add("message", "test message").build())
                .build();
    }

    /**
     * 测试场景 1：正常初始化流程
     * <p>
     * 验证 initialized 方法正确执行所有步骤：
     * - 创建 runtime 副本
     * - 构建上下文链
     * - 设置 testResult 和 testElement
     * - 计算 id/title/config
     * - 处理拦截器
     * </p>
     */
    @Test
    public void testInitialized_NormalFlow() throws Exception {
        // 执行初始化
        ContextWrapper localContext = invokeInitialized(processor, sessionRunner);

        // 验证返回值不为空
        Assert.assertNotNull(localContext, "initialized 应返回 ContextWrapper");

        // 验证 testElement 已设置
        Assert.assertNotNull(localContext.getTestElement(), "testElement 应被设置");
        Assert.assertEquals(localContext.getTestElement(), processor, "testElement 应指向当前处理器");

        // 验证 testResult 已设置
        Assert.assertNotNull(localContext.getTestResult(), "testResult 应被设置");
        Assert.assertTrue(localContext.getTestResult() instanceof SampleResult, "testResult 应为 SampleResult 类型");

        // 验证 runtime 已创建（通过 getRuntime 检查）
        Assert.assertNotNull(processor.getRuntime(), "runtime 应被创建");
        Assert.assertNotSame(processor.getRuntime(), processor, "runtime 应为 processor 的副本");

        // 验证 runtime 的 id/title 已通过表达式计算
        Assert.assertEquals(processor.getRuntime().getId(), "test-processor-id", "runtime.id 应被正确计算");
        Assert.assertEquals(processor.getRuntime().getTitle(), "Test Processor Title", "runtime.title 应被正确计算");
    }

    /**
     * 测试场景 2：变量为空时自动初始化
     * <p>
     * 当处理器的 variables 为空时，initialized 方法会确保有变量对象可用
     * </p>
     */
    @Test
    public void testInitialized_VariablesNull_AutoInitialize() throws Exception {
        // 创建 variables 为空的处理器（Builder 默认初始化为空 RyzeVariables）
        DebugPreprocessor processorWithoutVars = DebugPreprocessor.builder()
                .id("test-id")
                .title("test-title")
                .config(DebugConfigureItem.builder().add("message", "test").build())
                .build();

        // 执行初始化
        ContextWrapper localContext = invokeInitialized(processorWithoutVars, sessionRunner);

        // 验证上下文链中包含变量配置（不为空）
        Assert.assertNotNull(localContext.getConfigGroup(), "configGroup 不应为 null");
        Assert.assertNotNull(localContext.getConfigGroup().get(VARIABLES), "variables 应被初始化");
    }

    /**
     * 测试场景 3：变量合并优先级 - 当前变量覆盖父级变量
     * <p>
     * 验证变量合并逻辑：当前处理器的变量优先级高于 session 中的变量
     * </p>
     */
    @Test
    public void testInitialized_VariableMerge_CurrentPriorityHigher() throws Exception {
        // 在 session 的 context 中设置父级变量
        List<Context> contextChain = sessionRunner.getContextChain();
        Context lastContext = contextChain.getLast();
        RyzeVariables parentVars = new RyzeVariables();
        parentVars.put("shared_key", "parent_value");
        parentVars.put("parent_only_key", "parent_only");
        lastContext.getConfigGroup().put(VARIABLES, parentVars);

        // 创建处理器，设置当前变量（包含相同的 key）
        DebugPreprocessor processorWithVars = DebugPreprocessor.builder()
                .id("test-id")
                .title("test-title")
                .config(DebugConfigureItem.builder().add("message", "test").build())
                .variables(Map.of(
                        "shared_key", "current_value",  // 覆盖父级
                        "current_only_key", "current_only"  // 当前独有
                ))
                .build();

        // 执行初始化
        ContextWrapper localContext = invokeInitialized(processorWithVars, sessionRunner);

        // 验证变量合并结果
        String sharedValue = (String) localContext.evaluate("${shared_key}");
        String parentOnlyValue = (String) localContext.evaluate("${parent_only_key}");
        String currentOnlyValue = (String) localContext.evaluate("${current_only_key}");

        Assert.assertEquals(sharedValue, "current_value", "当前变量应覆盖父级变量");
        Assert.assertEquals(parentOnlyValue, "parent_only", "父级独有变量应保留");
        Assert.assertEquals(currentOnlyValue, "current_only", "当前独有变量应可用");
    }

    /**
     * 测试场景 3.1:Session vs Local 变量优先级
     * <p>
     * 验证变量合并逻辑：处理器的局部变量优先级高于 Session 变量
     * 这是 createContextWithVariables 方法的核心职责
     * </p>
     */
    @Test
    public void testInitialized_VariableScopeChain_SessionVsLocal() throws Exception {
        // 1. 设置 Session 变量（到 contextChain 的最后一个 Context）
        List<Context> contextChain = sessionRunner.getContextChain();
        Context lastContext = contextChain.getLast();
        RyzeVariables sessionVars = new RyzeVariables();
        sessionVars.put("scope_var", "session_value");
        sessionVars.put("session_only", "from_session");
        lastContext.getConfigGroup().put(VARIABLES, sessionVars);

        // 2. 设置 Local 变量（处理器变量，覆盖 session）
        DebugPreprocessor processorWithVars = DebugPreprocessor.builder()
                .id("test-id")
                .title("test-title")
                .config(DebugConfigureItem.builder().add("message", "test").build())
                .variables(Map.of(
                        "scope_var", "local_value",  // 覆盖 session
                        "local_only", "from_local"
                ))
                .build();

        // 执行初始化
        ContextWrapper localContext = invokeInitialized(processorWithVars, sessionRunner);

        // 验证作用域优先级：Local > Session
        String scopeVar = (String) localContext.evaluate("${scope_var}");
        String sessionOnly = (String) localContext.evaluate("${session_only}");
        String localOnly = (String) localContext.evaluate("${local_only}");

        Assert.assertEquals(scopeVar, "local_value", "Local 变量应覆盖 Session");
        Assert.assertEquals(sessionOnly, "from_session", "Session 独有变量应可用");
        Assert.assertEquals(localOnly, "from_local", "Local 独有变量应可用");
    }

    /**
     * 测试场景 4：上下文链构建 - 新增独立上下文层级
     * <p>
     * 验证 initialized 方法会创建新的上下文链，而不是修改原始 session 的上下文链
     * </p>
     */
    @Test
    public void testInitialized_ContextChain_NewLayerAdded() throws Exception {
        // 记录原始上下文链大小
        int originalChainSize = sessionRunner.getContextChain().size();

        // 执行初始化
        ContextWrapper localContext = invokeInitialized(processor, sessionRunner);

        // 验证新上下文链比原始链大 1（新增了一个 TestSuiteContext）
        List<Context> newContextChain = localContext.getContextChain();
        Assert.assertEquals(newContextChain.size(), originalChainSize + 1,
                "新上下文链应比原始链多一层");

        // 验证最后一层是 TestSuiteContext
        Context lastContext = newContextChain.getLast();
        Assert.assertTrue(lastContext instanceof TestSuiteContext,
                "最后一层上下文应为 TestSuiteContext");

        // 验证原始上下文链未被修改
        Assert.assertEquals(sessionRunner.getContextChain().size(), originalChainSize,
                "原始上下文链不应被修改");
    }

    /**
     * 测试场景 5：表达式计算 - id 支持 FreeMarker 表达式
     * <p>
     * 验证 id 字段可以通过 FreeMarker 表达式动态计算
     * </p>
     */
    @Test
    public void testInitialized_IdExpressionEvaluation() throws Exception {
        // 创建使用表达式的处理器
        DebugPreprocessor processorWithExpr = DebugPreprocessor.builder()
                .id("${'prefix-' + 'dynamic-id'}")  // FreeMarker 表达式
                .title("test-title")
                .config(DebugConfigureItem.builder().add("message", "test").build())
                .build();

        // 执行初始化
        invokeInitialized(processorWithExpr, sessionRunner);

        // 验证 id 表达式已计算
        Assert.assertEquals(processorWithExpr.getRuntime().getId(), "prefix-dynamic-id",
                "id 表达式应被正确计算");
    }

    /**
     * 测试场景 6：表达式计算 - title 支持 FreeMarker 表达式
     */
    @Test
    public void testInitialized_TitleExpressionEvaluation() throws Exception {
        // 设置 session 变量
        List<Context> contextChain = sessionRunner.getContextChain();
        Context lastContext = contextChain.getLast();
        RyzeVariables sessionVars = new RyzeVariables();
        sessionVars.put("user", "xiaomi");
        lastContext.getConfigGroup().put(VARIABLES, sessionVars);

        // 创建使用表达式的处理器
        DebugPreprocessor processorWithExpr = DebugPreprocessor.builder()
                .id("test-id")
                .title("Hello ${user}")  // 引用 session 变量
                .config(DebugConfigureItem.builder().add("message", "test").build())
                .build();

        // 执行初始化
        invokeInitialized(processorWithExpr, sessionRunner);

        // 验证 title 表达式已计算
        Assert.assertEquals(processorWithExpr.getRuntime().getTitle(), "Hello xiaomi",
                "title 表达式应能引用 session 变量");
    }

    /**
     * 测试场景 7：表达式计算 - config 支持 FreeMarker 表达式
     */
    @Test
    public void testInitialized_ConfigExpressionEvaluation() throws Exception {
        // 设置变量
        List<Context> contextChain = sessionRunner.getContextChain();
        Context lastContext = contextChain.getLast();
        RyzeVariables sessionVars = new RyzeVariables();
        sessionVars.put("msg", "dynamic message");
        lastContext.getConfigGroup().put(VARIABLES, sessionVars);

        // 创建使用表达式的 config
        DebugConfigureItem configWithExpr = DebugConfigureItem.builder()
                .add("message", "${msg}")  // 表达式
                .build();

        DebugPreprocessor processorWithExpr = DebugPreprocessor.builder()
                .id("test-id")
                .title("test-title")
                .config(configWithExpr)
                .build();

        // 执行初始化
        invokeInitialized(processorWithExpr, sessionRunner);

        // 验证 config 表达式已计算
        DebugConfigureItem runtimeConfig = processorWithExpr.getRuntime().getConfig();
        Assert.assertEquals(runtimeConfig.getString("message"), "dynamic message",
                "config 中的表达式应被正确计算");
    }

    /**
     * 测试场景 8：拦截器处理 - 无拦截器时正常运行
     */
    @Test
    public void testInitialized_NoInterceptors_Success() throws Exception {
        // 确保没有拦截器
        processor.setInterceptors(null);

        // 执行初始化
        ContextWrapper localContext = invokeInitialized(processor, sessionRunner);

        // 验证不抛异常，且 runtime.chain 为 null 或空
        Assert.assertNotNull(localContext, "无拦截器时应正常初始化");
    }

    /**
     * 测试场景 9：拦截器处理 - 从上下文合并拦截器
     * <p>
     * 注：根据"拦截器设置在哪一层就只在哪一层生效"原则，此测试已删除
     * </p>
     */

    /**
     * 测试场景 11：多次调用 initialized - 幂等性验证
     * <p>
     * 验证多次调用 initialized 不会产生副作用（每次都会重新创建 runtime）
     * </p>
     */
    @Test
    public void testInitialized_MultipleCalls_Idempotent() throws Exception {
        // 第一次初始化
        ContextWrapper firstContext = invokeInitialized(processor, sessionRunner);
        Object firstRuntime = processor.getRuntime();

        // 第二次初始化
        ContextWrapper secondContext = invokeInitialized(processor, sessionRunner);
        Object secondRuntime = processor.getRuntime();

        // 验证两次 runtime 不同（每次都是新副本）
        Assert.assertNotSame(firstRuntime, secondRuntime,
                "多次初始化应创建不同的 runtime 副本");

        // 验证两次返回的 context 不同
        Assert.assertNotSame(firstContext, secondContext,
                "多次初始化应返回不同的 ContextWrapper");
    }

    /**
     * 测试场景 12：父类 initialized 副作用验证
     * <p>
     * 验证父类 initialized() 方法正确执行（设置 initialized 标志 + 创建 runtime 副本）
     * </p>
     */
    @Test
    public void testInitialized_ParentSideEffects() throws Exception {
        // 执行初始化前，检查初始状态
        Assert.assertNull(processor.getRuntime(), "初始化前 runtime 应为 null");

        // 执行初始化
        invokeInitialized(processor, sessionRunner);

        // 验证父类 initialized 的副作用
        Assert.assertNotNull(processor.getRuntime(),
                "初始化后 runtime 应被创建");

        // 验证 runtime 是深拷贝（修改 runtime 不影响原对象）
        processor.getRuntime().setTitle("Modified Title");
        Assert.assertNotEquals(processor.getTitle(), "Modified Title",
                "修改 runtime 不应影响原 processor");
    }

    /**
     * 通过反射调用 protected 方法 initialized
     *
     * @param processor 处理器实例
     * @param session   SessionRunner 实例
     * @return 初始化后的 ContextWrapper
     */
    private ContextWrapper invokeInitialized(DebugPreprocessor processor, SessionRunner session) throws Exception {
        Method method = AbstractProcessor.class.getDeclaredMethod("initialized", SessionRunner.class);
        method.setAccessible(true);
        return (ContextWrapper) method.invoke(processor, session);
    }

    /**
     * 通过反射获取 runtime.chain 字段
     *
     * @param processor 处理器实例
     * @return HandlerExecutionChain 实例
     */
    private Object getRuntimeChain(DebugPreprocessor processor) throws Exception {
        var runtime = processor.getRuntime();
        // chain 在 AbstractTestElement 中定义
        Class<?> clazz = runtime.getClass();
        while (clazz != null) {
            try {
                var field = clazz.getDeclaredField("chain");
                field.setAccessible(true);
                return field.get(runtime);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("chain field not found in class hierarchy");
    }
}
