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

package io.github.xiaomisum.ryze.testelement;

import com.alibaba.fastjson2.annotation.JSONField;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.builder.ExtensibleConfigureElementsBuilder;
import io.github.xiaomisum.ryze.builder.ExtensiblePostprocessorsBuilder;
import io.github.xiaomisum.ryze.builder.ExtensiblePreprocessorsBuilder;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.config.RyzeVariables;
import io.github.xiaomisum.ryze.context.Context;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.context.TestSuiteContext;
import io.github.xiaomisum.ryze.testelement.configure.ConfigureElement;
import io.github.xiaomisum.ryze.testelement.processor.Postprocessor;
import io.github.xiaomisum.ryze.testelement.processor.Preprocessor;
import io.github.xiaomisum.ryze.support.Closeable;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.KryoUtil;
import io.github.xiaomisum.ryze.support.groovy.Groovy;

import java.util.*;
import java.util.function.Consumer;

/**
 * 可执行测试组件抽象类，提供公共属性和方法。
 * 
 * <p>该类是所有可执行测试元素的基类，定义了测试执行的基本流程和结构。
 * 它包含了变量、配置元件、前置处理器和后置处理器等核心组件，
 * 并提供了完整的测试执行生命周期管理。</p>
 *
 * <p>执行流程包括：
 * <ol>
 *   <li>初始化检查</li>
 *   <li>上下文管理</li>
 *   <li>变量和配置计算</li>
 *   <li>配置元件处理</li>
 *   <li>前置处理器执行</li>
 *   <li>核心测试逻辑执行</li>
 *   <li>后置处理器执行</li>
 *   <li>资源清理</li>
 * </ol>
 * </p>
 *
 * @param <SELF> 继承此类的具体类型，用于实现Fluent API
 * @param <CONFIG> 配置项类型，必须继承自ConfigureItem
 * @param <R> 结果类型，必须继承自Result
 * @author xiaomi
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractTestElementExecutable<SELF extends AbstractTestElementExecutable<SELF, CONFIG, R>,
        CONFIG extends ConfigureItem<CONFIG>, R extends Result>
        extends AbstractTestElement<SELF, CONFIG, R> implements TestElementExecutable<R>, TestElementConstantsInterface {

    @JSONField(name = VARIABLES, ordinal = 4)
    protected RyzeVariables variables;

    @JSONField(name = CONFIG_ELEMENTS, ordinal = 5)
    protected List<ConfigureElement> configureElements;

    @JSONField(name = PREPROCESSORS, ordinal = 6)
    protected List<Preprocessor> preprocessors = new ArrayList<>();

    @JSONField(name = POSTPROCESSORS, ordinal = 7)
    protected List<Postprocessor> postprocessors = new ArrayList<>();


    protected TestElementConfigureGroup configGroup = new TestElementConfigureGroup();

    /**
     * 默认构造函数
     */
    public AbstractTestElementExecutable() {
        super();
    }

    /**
     * 使用构建器初始化测试元素
     * 
     * @param builder 构建器实例
     */
    public AbstractTestElementExecutable(Builder builder) {
        super(builder);
        this.variables = builder.variables;
        this.configureElements = builder.configureElements;
        this.preprocessors = builder.preprocessors;
        this.postprocessors = builder.postprocessors;
    }

    /**
     * 获取当前测试元件的上下文链，当子类有额外的需求时可重写该方法
     * 
     * <p>该方法负责构建测试执行时的上下文链，包括合并父级上下文中的变量配置。
     * 上下文链用于在测试执行过程中传递配置和状态信息。</p>
     *
     * @param parentContext 父上下文链
     * @return 当前测试元件的完整上下文链
     */
    protected List<Context> getContextChain(List<Context> parentContext) {
        List<Context> contextChain = new ArrayList<>(parentContext);

        TestSuiteContext context = new TestSuiteContext();
        if (Objects.isNull(variables)) {
            variables = new RyzeVariables();
        }
        runtime.configGroup.put(VARIABLES, variables.merge(parentContext.getLast().getConfigGroup().getVariables()));
        context.setConfigGroup(runtime.getConfigGroup());
        contextChain.add(context);
        return contextChain;
    }


    /**
     * 计算并应用配置到上下文中
     * 
     * <p>该方法处理当前测试元件的变量和配置项，将它们应用到执行上下文中。
     * 注意：只会计算当前元件的配置，不会处理父级元件的配置。</p>
     *
     * @param context 执行上下文包装器
     */
    protected void evalConfig(ContextWrapper context) {
        // 变量与config
        RyzeVariables item;
        if (runtime.variables != null && (item = runtime.configGroup.getVariables()) != null) {
            context.evaluate(item);
        }
        CONFIG config;
        if (runtime.config != null && (config = runtime.configGroup.get(CONFIG)) != null) {
            context.evaluate(config);
        }
    }


    /**
     * 执行测试元素的主要入口方法
     * 
     * <p>该方法定义了测试元素执行的完整流程：
     * <ol>
     *   <li>检查是否禁用，如果禁用则直接返回结果</li>
     *   <li>确保测试元素已初始化</li>
     *   <li>更新执行上下文信息</li>
     *   <li>处理过滤器和拦截器</li>
     *   <li>执行内部逻辑</li>
     *   <li>恢复原始上下文信息</li>
     *   <li>结束测试并返回结果</li>
     * </ol></p>
     *
     * @param session 会话运行器
     * @return 测试执行结果
     */
    @Override
    public final R run(SessionRunner session) {
        Snapshot snapshot = new Snapshot();
        if (disabled) {
            snapshot.testResult.testEnd();
            return snapshot.testResult;
        }
        if (!initialized) {
            initialized();
        }

        ContextWrapper context = updateCurrentContextInfo(session, snapshot);
        runtime.id = (String) context.evaluate(id);
        runtime.title = (String) context.evaluate(title);
        snapshot.testResult = getTestResult();
        context.setTestResult(snapshot.testResult);
        snapshot.testResult.testStart();
        handleFilterInterceptors(context);
        internalRun(context);
        restoreCurrentContextInfo(session, snapshot);
        snapshot.testResult.testEnd();
        return snapshot.testResult;
    }

    /**
     * 更新当前执行上下文信息
     * 
     * <p>在测试执行前，需要保存当前的上下文信息并设置新的上下文信息，
     * 以便在执行完成后能够正确恢复原始状态。</p>
     *
     * @param session 会话运行器
     * @param snapshot 快照数据，用于存储和恢复上下文信息
     * @return 更新后的上下文包装器
     */
    private ContextWrapper updateCurrentContextInfo(SessionRunner session, Snapshot snapshot) {
        // 记录更新前的上下文信息
        List<Context> parentContextChain = session.getContextChain();
        ContextWrapper previousContextWrapper = session.getContext();
        snapshot.parentContextChain = parentContextChain;
        snapshot.previousContextWrapper = previousContextWrapper;

        // 更新当前上下文信息
        List<Context> currentContextChain = getContextChain(parentContextChain);
        session.setContextChain(currentContextChain);
        // 构建上下文包装器，封装本次执行的相关信息
        // 在后续多个方法间传递该对象，使用了方法传参，而不是成员变量，防止该对象在不正确的位置被使用，
        // 另一方面，该对象不是对象状态表示，只是一个临时对象，没有必要使用成员变量
        ContextWrapper context = new ContextWrapper(session);
        context.setTestElement(this);

        session.setContext(context);
        return context;
    }

    /**
     * 恢复执行前的上下文信息
     * 
     * <p>测试执行完成后，需要恢复执行前的上下文状态，确保不会影响后续测试的执行。</p>
     *
     * @param session 会话运行器
     * @param snapshotData 快照数据，包含执行前的上下文信息
     */
    private void restoreCurrentContextInfo(SessionRunner session, Snapshot snapshotData) {
        session.setContextChain(snapshotData.parentContextChain);
        session.setContext(snapshotData.previousContextWrapper);
    }

    /**
     * 内部执行逻辑
     * 
     * <p>该方法定义了测试元素内部执行的具体步骤：
     * <ol>
     *   <li>计算并应用配置</li>
     *   <li>处理配置元件</li>
     *   <li>执行前置处理器</li>
     *   <li>检查前置处理结果，如果失败则终止执行</li>
     *   <li>执行核心测试逻辑</li>
     *   <li>执行后置处理器</li>
     *   <li>关闭配置元件资源</li>
     * </ol></p>
     *
     * @param context 执行上下文包装器
     */
    protected void internalRun(ContextWrapper context) {
        // 模板计算：当前元件的变量配置项（不会计算父级元件）
        evalConfig(context);
        // 处理配置元件
        Optional.ofNullable(configureElements).orElse(Collections.emptyList()).forEach(ele -> ele.process(context));
        // 执行前置动作
        Optional.ofNullable(preprocessors).orElse(Collections.emptyList())
                .stream().filter(preprocessor -> !preprocessor.isDisabled())
                .forEach(preprocessor -> preprocessor.process(context));
        if (context.getTestResult().getStatus().isFailed() || context.getTestResult().getStatus().isBroken()) {
            // 前置步骤执行失败，后续步骤无需执行
            return;
        }
        // 执行请求
        execute(context, (R) context.getTestResult());
        Optional.ofNullable(postprocessors).orElse(Collections.emptyList())
                .stream().filter(postprocessor -> !postprocessor.isDisabled())
                .forEach(postprocessor -> postprocessor.process(context));
        // 关闭配置元件
        Optional.ofNullable(configureElements).orElse(Collections.emptyList()).stream()
                .filter(ele -> ele instanceof Closeable)
                .forEach(ele -> ((Closeable) ele).close());
    }

    /**
     * 拷贝当前测试元素实例
     * 
     * <p>创建当前测试元素的一个深拷贝副本，包括变量、前置处理器和后置处理器等所有相关组件。
     * 这确保了副本与原对象之间不会共享可变状态。</p>
     *
     * @return 当前测试元素的拷贝副本
     */
    @Override
    public SELF copy() {
        SELF self = super.copy();
        self.variables = KryoUtil.copy(variables);
        self.preprocessors = KryoUtil.copy(preprocessors);
        self.postprocessors = KryoUtil.copy(postprocessors);
        return self;
    }


    /**
     * 测试元件的核心功能实现，比如发起 HTTP 请求
     * 
     * <p>这是一个抽象方法，需要由具体的子类实现。该方法包含了测试元素的核心业务逻辑，
     * 例如发送HTTP请求、执行数据库操作等。</p>
     *
     * @param context 执行上下文包装器
     * @param result 测试结果对象
     */
    protected abstract void execute(ContextWrapper context, R result);

    // getter/setter

    /**
     * 获取测试变量集合
     * 
     * @return 测试变量集合
     */
    public RyzeVariables getVariables() {
        return variables;
    }

    /**
     * 设置测试变量集合
     * 
     * @param variables 测试变量集合
     */
    public void setVariables(RyzeVariables variables) {
        this.variables = variables;
    }

    /**
     * 获取配置组
     * 
     * @return 配置组
     */
    public TestElementConfigureGroup getConfigGroup() {
        return configGroup;
    }

    /**
     * 设置配置组
     * 
     * @param configGroup 配置组
     */
    public void setConfigGroup(TestElementConfigureGroup configGroup) {
        this.configGroup = configGroup;
    }

    /**
     * 获取配置元件列表
     * 
     * @return 配置元件列表
     */
    public List<ConfigureElement> getConfigureElements() {
        return configureElements;
    }

    /**
     * 设置配置元件列表
     * 
     * @param configureElements 配置元件列表
     */
    public void setConfigureElements(List<ConfigureElement> configureElements) {
        this.configureElements = configureElements;
    }

    /**
     * 获取前置处理器列表
     * 
     * @return 前置处理器列表
     */
    public List<Preprocessor> getPreprocessors() {
        return preprocessors;
    }

    /**
     * 设置前置处理器列表
     * 
     * @param preprocessors 前置处理器列表
     */
    public void setPreprocessors(List<Preprocessor> preprocessors) {
        this.preprocessors = preprocessors;
    }

    /**
     * 获取后置处理器列表
     * 
     * @return 后置处理器列表
     */
    public List<Postprocessor> getPostprocessors() {
        return postprocessors;
    }

    /**
     * 设置后置处理器列表
     * 
     * @param postprocessors 后置处理器列表
     */
    public void setPostprocessors(List<Postprocessor> postprocessors) {
        this.postprocessors = postprocessors;
    }

    /**
     * 测试元素构建器抽象类，提供构建可执行测试元素的通用方法
     * 
     * <p>该构建器支持多种配置方式，包括：
     * <ul>
     *   <li>变量配置：通过多种方式设置测试变量</li>
     *   <li>配置元件：添加和配置测试所需的配置元件</li>
     *   <li>前置处理器：配置测试执行前需要执行的处理器</li>
     *   <li>后置处理器：配置测试执行后需要执行的处理器</li>
     * </ul></p>
     *
     * @param <ELE> 测试元素类型
     * @param <SELF> 构建器自身类型，用于实现Fluent API
     * @param <CONFIG> 配置项类型
     * @param <CONFIGURE_BUILDER> 配置构建器类型
     * @param <CONFIGURES_BUILDER> 配置元件构建器类型
     * @param <PREPROCESSORS_BUILDER> 前置处理器构建器类型
     * @param <POSTPROCESSORS_BUILDER> 后置处理器构建器类型
     * @param <R> 结果类型
     */
    public static abstract class Builder<ELE extends AbstractTestElementExecutable<ELE, CONFIG, R>,
            SELF extends Builder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, CONFIGURES_BUILDER, PREPROCESSORS_BUILDER, POSTPROCESSORS_BUILDER, R>,
            CONFIG extends ConfigureItem<CONFIG>,
            CONFIGURE_BUILDER extends ConfigureBuilder<?, CONFIG>,
            CONFIGURES_BUILDER extends AbstractTestElement.ConfigureElementsBuilder,
            PREPROCESSORS_BUILDER extends AbstractTestElement.PreprocessorsBuilder,
            POSTPROCESSORS_BUILDER extends AbstractTestElement.PostprocessorsBuilder,
            R extends Result>
            extends AbstractTestElement.Builder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, R> {

        protected RyzeVariables variables;

        protected List<ConfigureElement> configureElements;

        protected List<Preprocessor> preprocessors;

        protected List<Postprocessor> postprocessors;

        /**
         * 通过消费者函数配置变量
         * 
         * @param consumer 变量构建器消费者函数
         * @return 构建器实例
         */
        public SELF variables(Consumer<RyzeVariables.Builder> consumer) {
            RyzeVariables.Builder builder = RyzeVariables.builder();
            consumer.accept(builder);
            this.variables = (RyzeVariables) Collections.putAllIfNonNull(this.variables, builder.build());
            return self;
        }

        /**
         * 通过Groovy闭包配置变量
         * 
         * @param closure Groovy闭包
         * @return 构建器实例
         */
        public SELF variables(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RyzeVariables.Builder.class) Closure<?> closure) {
            RyzeVariables.Builder builder = RyzeVariables.builder();
            Groovy.call(closure, builder);
            this.variables = (RyzeVariables) Collections.putAllIfNonNull(this.variables, builder.build());
            return self;
        }

        /**
         * 通过Map配置变量
         * 
         * @param variables 变量Map
         * @return 构建器实例
         */
        public SELF variables(Map<? extends String, ?> variables) {
            this.variables = (RyzeVariables) Collections.putAllIfNonNull(this.variables, new RyzeVariables(variables));
            return self;
        }

        /**
         * 直接设置变量集合
         * 
         * @param variables 变量集合
         * @return 构建器实例
         */
        public SELF variables(RyzeVariables variables) {
            this.variables = (RyzeVariables) Collections.putAllIfNonNull(this.variables, variables);
            return self;
        }

        /**
         * 添加单个变量
         * 
         * @param name 变量名
         * @param value 变量值
         * @return 构建器实例
         */
        public SELF variables(String name, Object value) {
            synchronized (this) {
                if (Objects.isNull(variables)) {
                    synchronized (this) {
                        this.variables = new RyzeVariables();
                    }
                }
            }
            this.variables.put(name, value);
            return self;
        }

        /**
         * 设置配置元件列表
         *
         * @param configureElements 配置元件列表
         * @return 当前对象
         */
        public SELF configureElements(List<ConfigureElement> configureElements) {
            this.configureElements = Collections.addAllIfNonNull(this.configureElements, configureElements);
            return self;
        }

        /**
         * 通过指定类型的构建器配置配置元件
         * 
         * @param type 扩展配置元件构建器类型
         * @param customizer 自定义配置函数
         * @param <T> 扩展配置元件构建器类型
         * @return 构建器实例
         */
        public <T extends ExtensibleConfigureElementsBuilder> SELF configureElements(Class<T> type, Customizer<T> customizer) {
            try {
                T builder = type.getConstructor().newInstance();
                customizer.customize(builder);
                this.configureElements = Collections.addAllIfNonNull(this.configureElements, builder.build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return self;
        }

        /**
         * 通过自定义函数配置配置元件
         * 
         * @param customizer 自定义配置函数
         * @return 构建器实例
         */
        public SELF configureElements(Customizer<CONFIGURES_BUILDER> customizer) {
            CONFIGURES_BUILDER builder = getConfiguresBuilder();
            customizer.customize(builder);
            this.configureElements = Collections.addAllIfNonNull(this.configureElements, builder.build());
            return self;
        }

        /**
         * 通过Groovy闭包和指定类型配置配置元件
         * 
         * @param type 扩展配置元件构建器类型
         * @param closure Groovy闭包
         * @param <T> 扩展配置元件构建器类型
         * @return 构建器实例
         */
        public <T extends ExtensibleConfigureElementsBuilder> SELF configureElements(Class<T> type, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "T") Closure<T> closure) {
            var builder = Groovy.builder(type, closure);
            this.configureElements = Collections.addAllIfNonNull(this.configureElements, builder.build());
            return self;
        }

        /**
         * 通过Groovy闭包配置配置元件
         * 
         * @param closure Groovy闭包
         * @return 构建器实例
         */
        public SELF configureElements(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "CONFIGURES_BUILDER") Closure<?> closure) {
            CONFIGURES_BUILDER builder = getConfiguresBuilder();
            Groovy.call(closure, builder);
            this.configureElements = Collections.addAllIfNonNull(this.configureElements, builder.build());
            return self;
        }

        /**
         * 设置前置处理器列表
         * 
         * @param preprocessors 前置处理器列表
         * @return 构建器实例
         */
        public SELF preprocessors(List<Preprocessor> preprocessors) {
            this.preprocessors = Collections.addAllIfNonNull(this.preprocessors, preprocessors);
            return self;
        }

        /**
         * 通过指定类型的构建器配置前置处理器
         * 
         * @param type 扩展前置处理器构建器类型
         * @param customizer 自定义配置函数
         * @param <T> 扩展前置处理器构建器类型
         * @return 构建器实例
         */
        public <T extends ExtensiblePreprocessorsBuilder> SELF preprocessors(Class<T> type, Customizer<T> customizer) {
            try {
                T builder = type.getConstructor().newInstance();
                customizer.customize(builder);
                this.preprocessors = Collections.addAllIfNonNull(this.preprocessors, builder.build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return self;
        }

        /**
         * 通过自定义函数配置前置处理器
         * 
         * @param customizer 自定义配置函数
         * @return 构建器实例
         */
        public SELF preprocessors(Customizer<PREPROCESSORS_BUILDER> customizer) {
            PREPROCESSORS_BUILDER builder = getPreprocessorsBuilder();
            customizer.customize(builder);
            this.preprocessors = Collections.addAllIfNonNull(this.preprocessors, builder.build());
            return self;
        }

        /**
         * 通过Groovy闭包和指定类型配置前置处理器
         * 
         * @param type 扩展前置处理器构建器类型
         * @param closure Groovy闭包
         * @param <T> 扩展前置处理器构建器类型
         * @return 构建器实例
         */
        public <T extends ExtensiblePreprocessorsBuilder> SELF preprocessors(Class<T> type, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "T") Closure<T> closure) {
            var builder = Groovy.builder(type, closure);
            this.preprocessors = Collections.addAllIfNonNull(this.preprocessors, builder.build());
            return self;
        }

        /**
         * 通过Groovy闭包配置前置处理器
         * 
         * @param closure Groovy闭包
         * @return 构建器实例
         */
        public SELF preprocessors(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "PREPROCESSORS_BUILDER") Closure<?> closure) {
            PREPROCESSORS_BUILDER builder = getPreprocessorsBuilder();
            Groovy.call(closure, builder);
            this.preprocessors = Collections.addAllIfNonNull(this.preprocessors, builder.build());
            return self;
        }

        /**
         * 设置后置处理器列表
         * 
         * @param postprocessors 后置处理器列表
         * @return 构建器实例
         */
        public SELF postprocessors(List<Postprocessor> postprocessors) {
            this.postprocessors = Collections.addAllIfNonNull(this.postprocessors, postprocessors);
            return self;
        }


        /**
         * 通过指定类型的构建器配置后置处理器
         * 
         * @param type 扩展后置处理器构建器类型
         * @param customizer 自定义配置函数
         * @param <T> 扩展后置处理器构建器类型
         * @return 构建器实例
         */
        public <T extends ExtensiblePostprocessorsBuilder> SELF postprocessors(Class<T> type, Customizer<T> customizer) {
            try {
                T builder = type.getConstructor().newInstance();
                customizer.customize(builder);
                this.postprocessors = Collections.addAllIfNonNull(this.postprocessors, builder.build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return self;
        }

        /**
         * 通过自定义函数配置后置处理器
         * 
         * @param customizer 自定义配置函数
         * @return 构建器实例
         */
        public SELF postprocessors(Customizer<POSTPROCESSORS_BUILDER> customizer) {
            POSTPROCESSORS_BUILDER builder = getPostprocessorsBuilder();
            customizer.customize(builder);
            this.postprocessors = Collections.addAllIfNonNull(this.postprocessors, builder.build());
            return self;
        }

        /**
         * 通过Groovy闭包和指定类型配置后置处理器
         * 
         * @param type 扩展后置处理器构建器类型
         * @param closure Groovy闭包
         * @param <T> 扩展后置处理器构建器类型
         * @return 构建器实例
         */
        public <T extends ExtensiblePostprocessorsBuilder> SELF postprocessors(Class<T> type, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "T") Closure<T> closure) {
            var builder = Groovy.builder(type, closure);
            this.postprocessors = Collections.addAllIfNonNull(this.postprocessors, builder.build());
            return self;
        }

        /**
         * 通过Groovy闭包配置后置处理器
         * 
         * @param closure Groovy闭包
         * @return 构建器实例
         */
        public SELF postprocessors(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "POSTPROCESSORS_BUILDER") Closure<?> closure) {
            POSTPROCESSORS_BUILDER builder = getPostprocessorsBuilder();
            Groovy.call(closure, builder);
            this.postprocessors = Collections.addAllIfNonNull(this.postprocessors, builder.build());
            return self;
        }

        /**
         * 获取配置元件构建器
         * 
         * @return 配置元件构建器
         */
        protected abstract CONFIGURES_BUILDER getConfiguresBuilder();

        /**
         * 获取前置处理器构建器
         * 
         * @return 前置处理器构建器
         */
        protected abstract PREPROCESSORS_BUILDER getPreprocessorsBuilder();

        /**
         * 获取后置处理器构建器
         * 
         * @return 后置处理器构建器
         */
        protected abstract POSTPROCESSORS_BUILDER getPostprocessorsBuilder();
    }

    /**
     * 运行快照数据，用于在测试执行过程中保存和恢复上下文信息
     */
    private final class Snapshot {

        private List<Context> parentContextChain;
        private ContextWrapper previousContextWrapper;
        private R testResult;
    }
}