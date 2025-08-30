/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze;

import io.github.xiaomisum.ryze.config.RyzeVariables;
import io.github.xiaomisum.ryze.context.Context;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.context.SessionContext;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.TestElementConfigureGroup;
import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;
import io.github.xiaomisum.ryze.support.ValidateResult;

import java.util.*;

/**
 * 测试会话执行器
 * <p>
 * 测试用例实际执行入口，非线程安全类，每个用例应使用各自的 SessionRunner 对象。
 * 该类负责管理测试执行的整个生命周期，包括上下文管理、配置管理、测试元素执行等核心功能。
 * </p>
 * <p>
 * SessionRunner通过ThreadLocal机制确保每个线程拥有独立的实例，避免并发执行时的冲突问题。
 * 它维护了测试执行过程中的上下文链、配置信息和临时存储空间，是整个测试执行过程的核心协调者。
 * </p>
 */
public class SessionRunner {

    /**
     * ThreadLocal变量，用于存储当前线程的SessionRunner实例
     */
    public static final ThreadLocal<SessionRunner> HOLDER = new ThreadLocal<>();
    
    /**
     * 会话上下文，存储当前会话的上下文信息
     */
    private final SessionContext sessionContext = new SessionContext();
    
    /**
     * 用例执行过程中可用来进行数据存取的存储空间
     * <p>用于在测试执行过程中存储和共享数据</p>
     */
    private final Map<String, Object> storage = new HashMap<>();
    
    /**
     * 配置对象，包含测试执行的各种配置信息
     */
    private final Configure configure;
    
    /**
     * Session 当前执行上下文链
     * <p>维护测试执行过程中的上下文层次结构</p>
     */
    private List<Context> contextChain = new ArrayList<>();
    
    /**
     * 当前上下文包装器
     */
    private ContextWrapper context;
    
    /**
     * 标识是否在测试框架支持模式下运行
     */
    private boolean runInTestFrameworkSupport = false;

    /**
     * 构造函数
     *
     * @param configure 配置对象
     */
    SessionRunner(Configure configure) {
        this.configure = configure;
        initContextChain();
    }

    /**
     * 获取当前线程的SessionRunner实例，如果不存在则创建新的实例
     *
     * @return 当前线程的SessionRunner实例
     */
    public static SessionRunner getSessionIfNoneCreateNew() {
        var sessionRunner = HOLDER.get();
        if (Objects.nonNull(sessionRunner)) {
            return sessionRunner;
        }
        sessionRunner = new SessionRunner(Configure.defaultConfigure());
        setSession(sessionRunner);
        return sessionRunner;
    }


    /**
     * 获取当前线程的SessionRunner实例
     *
     * @return 当前线程的SessionRunner实例
     * @throws IllegalStateException 如果SessionRunner未设置
     */
    public static SessionRunner getSession() {
        SessionRunner sessionRunner = HOLDER.get();
        if (sessionRunner == null) {
            throw new IllegalStateException("SessionRunner 未设置，请先调用 setSession 方法设置");
        }
        return sessionRunner;
    }


    /**
     * 设置当前线程的SessionRunner实例
     *
     * @param sessionRunner 要设置的SessionRunner实例
     */
    public static void setSession(SessionRunner sessionRunner) {
        HOLDER.set(sessionRunner);
    }

    /**
     * 创建新的SessionRunner实例并设置为当前线程的实例
     *
     * @param configure 配置对象
     */
    public static void newSession(Configure configure) {
        HOLDER.set(new SessionRunner(configure));
    }

    /**
     * 创建新的测试框架支持模式的SessionRunner实例并设置为当前线程的实例
     *
     * @param configure 配置对象
     */
    public static void newTestFrameworkSession(Configure configure) {
        var session = new SessionRunner(configure);
        session.runInTestFrameworkSupport = true;
        HOLDER.set(session);
    }

    /**
     * 移除当前线程的SessionRunner实例
     */
    public static void removeSession() {
        HOLDER.remove();
    }

    /**
     * 初始化上下文链
     * <p>
     * 初始化测试执行所需的上下文链，包括全局上下文和会话上下文，
     * 并设置默认的配置组，如变量配置等。
     * </p>
     */
    private void initContextChain() {
        // 会话上下文（运行用例）
        contextChain.add(configure.getGlobalContext());
        contextChain.add(sessionContext);

        // 会话上下文默认值：添加一个空的变量配置
        // SessionRunner 可能直接运行某个 Sampler，而不是 Testsuite，比如 Groovy/Java 用例
        // 示例：SessionRunner 连续运行多个 Http 请求，Http 请求中设置和读取 Session 变量
        TestElementConfigureGroup testElementConfig = new TestElementConfigureGroup();
        testElementConfig.put(TestElementConstantsInterface.VARIABLES, new RyzeVariables());
        sessionContext.setConfigGroup(testElementConfig);

        context = new ContextWrapper(this);
    }

    /**
     * Session 上下文配置，如变量配置、Http 配置等。
     *
     * @param config 要添加到会话上下文的配置组
     */
    public void config(TestElementConfigureGroup config) {
        var oldConfig = (TestElementConfigureGroup) sessionContext.getConfigGroup();
        sessionContext.setConfigGroup(oldConfig.merge(config));
    }

    /**
     * 运行任意的测试元件，如 TestSuite 或 Sampler
     * <p>
     * 默认校验：执行前校验测试元件数据是否合法
     * </p>
     *
     * @param element 测试元件
     * @param <T>     测试元件对应的执行结果类
     * @return 测试元件的执行结果
     * @see #runTest(TestElement, boolean)
     */
    public <T extends Result> T runTest(TestElement<T> element) {
        return runTest(element, true);
    }

    /**
     * 运行任意的测试元件，如 TestSuite 或 Sampler
     *
     * @param element  测试元件
     * @param <T>      测试元件对应的执行结果类
     * @param validate 是否校验测试元件数据
     * @return 测试元件的执行结果
     */
    public <T extends Result> T runTest(TestElement<T> element, boolean validate) {
        if (validate) {
            ValidateResult validateResult = element.validate();
            if (validateResult.isValid()) {
                throw new RuntimeException(validateResult.getReason());
            }
        }
        return element.run(this);
    }

    /**
     * 获取上下文链
     *
     * @return 上下文链列表
     */
    public List<Context> getContextChain() {
        return contextChain;
    }

    /**
     * 设置上下文链
     *
     * @param contextChain 上下文链列表
     */
    public void setContextChain(List<Context> contextChain) {
        this.contextChain = contextChain;
    }

    /**
     * 获取当前上下文包装器
     *
     * @return 当前上下文包装器
     */
    public ContextWrapper getContext() {
        return context;
    }

    /**
     * 设置当前上下文包装器
     *
     * @param context 上下文包装器
     */
    public void setContext(ContextWrapper context) {
        this.context = context;
    }

    /**
     * 获取存储空间
     *
     * @return 存储空间映射
     */
    public Map<String, Object> getStorage() {
        return storage;
    }

    /**
     * 判断是否在测试框架支持模式下运行
     *
     * @return 如果在测试框架支持模式下运行返回true，否则返回false
     */
    public boolean isRunInTestFrameworkSupport() {
        return runInTestFrameworkSupport;
    }

    /**
     * 获取配置对象
     *
     * @return 配置对象
     */
    public Configure getConfigure() {
        return configure;
    }
}