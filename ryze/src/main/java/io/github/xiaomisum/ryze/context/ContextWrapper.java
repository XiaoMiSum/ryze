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

package io.github.xiaomisum.ryze.context;


import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.config.ConfigureGroup;
import io.github.xiaomisum.ryze.context.variables.*;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.TestElementConfigureGroup;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 测试上下文包装器，提供了处理上下文链的各种方法
 * <p>
 * ContextWrapper是Ryze框架中核心的上下文管理组件，负责管理测试执行过程中的各种上下文信息。
 * 它将多个层级的上下文（全局、测试套件、会话、测试步骤等）组织成一个链式结构，
 * 并提供统一的访问接口来获取配置信息、变量信息等。
 * </p>
 * <p>
 * 主要功能包括：
 * <ul>
 *   <li>管理上下文链：将不同层级的Context对象组织成链式结构</li>
 *   <li>配置组合并：将上下文链中各层级的配置信息合并成统一视图</li>
 *   <li>变量管理：提供对全局变量、测试变量、会话变量和本地变量的访问</li>
 *   <li>表达式计算：提供基于模板引擎的表达式计算功能</li>
 *   <li>测试元件信息管理：维护当前正在执行的测试元件和结果信息</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class ContextWrapper {

    // 原始的上下文作用域链
    private final List<Context> rawContextChain;
    // 合并后的配置组
    private final ConfigureGroup configureGroup;
    private final AllVariablesWrapper allVariablesWrapper;
    private final LocalVariablesWrapper localVariablesWrapper;
    // 当前上下文，即最后一个上下文对象
    private Context sessionContext;
    private GlobalContext globalContext;
    private TestSuiteContext testContext;
    private GlobalVariablesWrapper globalVariablesWrapper;
    private TestVariablesWrapper testVariablesWrapper;
    private SessionVariablesWrapper sessionVariablesWrapper;
    // Runner 相关对象
    private SessionRunner sessionRunner;
    // 测试元件信息
    private TestElement<?> testElement;
    private Result testResult;
    private String uuid;

    // ----------- 构造器 -------------

    /**
     * 构造一个ContextWrapper实例，用于用例执行前的准备工作，如全局变量和环境变量的表达式计算
     * <p>
     * 该构造器会处理传入的上下文链，合并各层级的配置组，并初始化各种变量包装器。
     * 主要用于在测试执行前对全局配置和变量进行预处理。
     * </p>
     *
     * @param contextChain 上下文链，包含从全局到当前层级的所有Context对象，不能为空
     * @throws UnsupportedOperationException 当contextChain为空或包含空值时抛出
     */
    public ContextWrapper(List<Context> contextChain) {
        requiredNotNullAndNotEmpty(contextChain);
        // 处理上下文链
        this.rawContextChain = contextChain;
        // 合并配置组
        this.configureGroup = mergeConfigGroup();

        // 读取各级上下文
        searchNonTestStepContext();
        // 当前上下文，即最后一个上下文对象
        Context currentContext = contextChain.getLast();

        // 处理各级上下文
        if (globalContext != null) {
            this.globalVariablesWrapper = new GlobalVariablesWrapper(List.of(globalContext));
        }
        if (testContext != null) {
            this.testVariablesWrapper = new TestVariablesWrapper(List.of(testContext));
        }
        if (sessionContext != null) {
            this.sessionVariablesWrapper = new SessionVariablesWrapper(List.of(sessionContext));
        }
        this.allVariablesWrapper = new AllVariablesWrapper(Collections.unmodifiableList(rawContextChain));
        this.localVariablesWrapper = new LocalVariablesWrapper(List.of(currentContext));
    }

    /**
     * 构造一个ContextWrapper实例，用于测试用例执行时，包装当前元件的执行信息
     * <p>
     * 该构造器在测试执行过程中使用，会创建一个唯一的UUID用于标识当前执行步骤，
     * 并关联SessionRunner以提供完整的执行上下文。
     * </p>
     *
     * @param sessionRunner 用例执行器，提供测试执行的环境和配置信息
     */
    public ContextWrapper(SessionRunner sessionRunner) {
        this(sessionRunner.getContextChain());
        this.sessionRunner = sessionRunner;
        this.uuid = UUID.randomUUID().toString();
    }

    // == 构造器辅助方法 ==

    /**
     * 验证上下文链不为空且不包含空值
     *
     * @param contextChain 待验证的上下文链
     * @throws UnsupportedOperationException 当contextChain为空或包含空值时抛出
     */
    private void requiredNotNullAndNotEmpty(List<Context> contextChain) {
        if (contextChain == null || contextChain.isEmpty()) {
            throw new UnsupportedOperationException("上下文链为空");
        }

        long nullCount = contextChain.stream().filter(Objects::isNull).count();
        if (nullCount > 0) {
            throw new UnsupportedOperationException("上下文链中存在空值");
        }
    }

    /**
     * 在上下文链中搜索非测试步骤的上下文对象（全局上下文、测试套件上下文、会话上下文）
     * <p>
     * 遍历整个上下文链，识别并保存不同层级的上下文对象引用，为后续的变量和配置访问做准备。
     * 如果存在多个相同类型的上下文（如多个SessionContext），则取最后一个（最近的）。
     * </p>
     */
    private void searchNonTestStepContext() {
        // 如果上下文链中存在多个 SessionContext，则取最后一个，即最近的一个
        rawContextChain.forEach(ctx -> {
            if (ctx instanceof GlobalContext) {
                this.globalContext = (GlobalContext) ctx;
            } else if (ctx instanceof TestSuiteContext) {
                this.testContext = (TestSuiteContext) ctx;
            } else if (ctx instanceof SessionContext) {
                this.sessionContext = ctx;
            }
        });
    }

    /**
     * 合并上下文链中所有层级的配置组
     * <p>
     * 按照从全局到具体的顺序遍历上下文链，将各层级的配置信息合并成一个统一的配置组视图。
     * 后面层级的配置会覆盖前面层级的同名配置，实现配置的继承和覆盖机制。
     * </p>
     *
     * @return 合并后的配置组
     */
    private ConfigureGroup mergeConfigGroup() {
        ConfigureGroup config = new TestElementConfigureGroup();
        for (var context : rawContextChain) {
            var configureGroup = context.getConfigGroup();
            config = config.merge(configureGroup);
        }
        return config;
    }

    // ----------- ContextWrapper 对外 API -------------

    /**
     * 使用模板引擎计算对象中的表达式
     * <p>
     * 支持对字符串、Map、List等各种类型的对象进行表达式计算。
     * 对于字符串类型，会识别其中的模板表达式（如${variable}）并进行替换；
     * 对于集合类型，会递归处理其中的每个元素。
     * </p>
     *
     * @param object 待计算的对象，可以是字符串、Map、List或其他对象
     * @return 计算后的对象，对于字符串会返回表达式计算结果，其他类型可能返回原值
     */
    public Object evaluate(Object object) {
        return sessionRunner.getConfigure().getTemplateEngine().evaluate(this, object);
    }

    /**
     * 获取原始的上下文链
     *
     * @return 包含所有层级上下文的不可变列表
     */
    public List<Context> getContextChain() {
        return rawContextChain;
    }

    /**
     * 获取测试套件级别的变量包装器
     *
     * @return TestVariablesWrapper实例，用于访问测试套件级别的变量
     */
    public TestVariablesWrapper getTestVariablesWrapper() {
        return testVariablesWrapper;
    }

    /**
     * 获取会话级别的变量包装器
     *
     * @return SessionVariablesWrapper实例，用于访问会话级别的变量
     */
    public SessionVariablesWrapper getSessionVariablesWrapper() {
        return sessionVariablesWrapper;
    }

    /**
     * 获取所有层级变量的包装器
     * <p>
     * 该包装器提供对上下文链中所有层级变量的统一访问接口，
     * 按照从全局到具体的顺序合并变量，同名变量后面的会覆盖前面的。
     * </p>
     *
     * @return AllVariablesWrapper实例，用于访问所有层级的变量
     */
    public AllVariablesWrapper getAllVariablesWrapper() {
        return allVariablesWrapper;
    }

    /**
     * 获取本地（当前上下文）级别的变量包装器
     *
     * @return LocalVariablesWrapper实例，用于访问当前上下文的变量
     */
    public LocalVariablesWrapper getLocalVariablesWrapper() {
        return localVariablesWrapper;
    }

    /**
     * 获取会话执行器
     *
     * @return SessionRunner实例，用于执行测试用例
     */
    public SessionRunner getSessionRunner() {
        return sessionRunner;
    }

    /**
     * 获取合并后的配置组
     * <p>
     * 返回在构造时合并的所有层级配置信息，提供统一的配置访问接口。
     * </p>
     *
     * @return 合并后的配置组
     */
    public ConfigureGroup getConfigGroup() {
        return configureGroup;
    }

    /**
     * 获取当前正在执行的测试元件
     *
     * @return TestElement实例，表示当前正在执行的测试元件
     */
    public TestElement<?> getTestElement() {
        return testElement;
    }

    /**
     * 设置当前正在执行的测试元件
     *
     * @param testElement TestElement实例，表示当前正在执行的测试元件
     */
    public void setTestElement(TestElement<?> testElement) {
        this.testElement = testElement;
    }

    /**
     * 获取当前测试结果
     *
     * @return Result实例，表示当前测试的执行结果
     */
    public Result getTestResult() {
        return testResult;
    }

    /**
     * 设置当前测试结果
     *
     * @param testResult Result实例，表示当前测试的执行结果
     */
    public void setTestResult(Result testResult) {
        this.testResult = testResult;
    }

    /**
     * 获取全局上下文
     *
     * @return GlobalContext实例，表示全局上下文
     */
    public GlobalContext getGlobalContext() {
        return globalContext;
    }

    /**
     * 设置全局上下文
     *
     * @param globalContext GlobalContext实例，表示全局上下文
     */
    public void setGlobalContext(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    /**
     * 获取全局变量包装器
     *
     * @return GlobalVariablesWrapper实例，用于访问全局变量
     */
    public GlobalVariablesWrapper getGlobalVariablesWrapper() {
        return globalVariablesWrapper;
    }

    /**
     * 设置全局变量包装器
     *
     * @param globalVariablesWrapper GlobalVariablesWrapper实例，用于访问全局变量
     */
    public void setGlobalVariablesWrapper(GlobalVariablesWrapper globalVariablesWrapper) {
        this.globalVariablesWrapper = globalVariablesWrapper;
    }

    /**
     * 获取当前执行步骤的唯一标识符
     *
     * @return UUID字符串，用于唯一标识当前执行步骤
     */
    public String getUuid() {
        return uuid;
    }
}