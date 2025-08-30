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

package io.github.xiaomisum.ryze.testelement.sampler;

import com.alibaba.fastjson2.annotation.JSONField;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.assertion.Assertion;
import io.github.xiaomisum.ryze.builder.*;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.config.RyzeVariables;
import io.github.xiaomisum.ryze.context.Context;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.context.TestRunContext;
import io.github.xiaomisum.ryze.extractor.Extractor;
import io.github.xiaomisum.ryze.interceptor.RyzeInterceptor;
import io.github.xiaomisum.ryze.testelement.AbstractTestElementExecutable;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.KryoUtil;
import io.github.xiaomisum.ryze.support.groovy.Groovy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Sampler 抽象实现类，是所有取样器的基类。
 *
 * <p>取样器是测试框架中最基本的执行单元，用于执行具体的测试操作，如发送HTTP请求、执行数据库查询等。
 * 该类提供了完整的执行流程框架，包括前置处理、核心业务处理、后置处理、断言验证和变量提取等功能。</p>
 *
 * <p>执行流程如下：
 * <ol>
 *   <li>检查前置处理器执行结果，如果失败则跳过核心业务逻辑</li>
 *   <li>对配置项进行表达式求值</li>
 *   <li>执行拦截器前置处理</li>
 *   <li>调用 {@link #handleRequest(ContextWrapper, SampleResult)} 方法进行请求前处理</li>
 *   <li>标记采样开始时间</li>
 *   <li>调用 {@link #sample(ContextWrapper, SampleResult)} 方法执行核心业务逻辑</li>
 *   <li>标记采样结束时间</li>
 *   <li>调用 {@link #handleResponse(ContextWrapper, SampleResult)} 方法进行响应后处理</li>
 *   <li>执行拦截器后置处理</li>
 *   <li>执行断言验证</li>
 *   <li>执行变量提取</li>
 *   <li>执行拦截器最终处理</li>
 * </ol></p>
 *
 * <p>扩展 Sampler 请继承此类。</p>
 *
 * @param <SELF>   实际的取样器类型，用于实现流畅的构建器模式
 * @param <CONFIG> 取样器配置项类型，用于存储协议相关的配置信息
 * @param <R>      取样结果类型，表示执行结果的具体实现
 * @author xiaomi
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractSampler<SELF extends AbstractSampler<SELF, CONFIG, R>, CONFIG extends ConfigureItem<CONFIG>, R extends SampleResult>
        extends AbstractTestElementExecutable<SELF, CONFIG, R> implements Sampler<R> {

    /**
     * 断言列表，用于验证取样器执行结果是否符合预期
     */
    @JSONField(name = VALIDATORS, ordinal = 9)
    protected List<Assertion> assertions;

    /**
     * 提取器列表，用于从取样器执行结果中提取变量
     */
    @JSONField(name = EXTRACTORS, ordinal = 10)
    protected List<Extractor> extractors;


    /**
     * 基于构建器的构造函数
     *
     * @param builder 构建器实例
     */
    public AbstractSampler(Builder builder) {
        super(builder);
        this.assertions = builder.assertions;
        this.extractors = builder.extractors;
    }

    /**
     * 默认构造函数
     */
    public AbstractSampler() {
        super();
    }

    /**
     * 覆盖父类该方法，取样器无需创建新的 context, 返回最后一个  context
     *
     * <p>取样器与其他测试元件不同，它直接使用父级上下文中的变量配置，
     * 而不创建新的上下文。这样可以确保变量在测试流程中的正确传递。</p>
     *
     * @param parentContext 测试上下文链路
     * @return 拷贝的新上下文链路
     */
    protected List<Context> getContextChain(List<Context> parentContext) {
        List<Context> contextChain = new ArrayList<>(parentContext);
        Context context = parentContext.getLast();
        if (Objects.isNull(variables)) {
            variables = new RyzeVariables();
        }
        runtime.configGroup.put(VARIABLES, variables.merge(context.getConfigGroup().getVariables()));
        if (context instanceof TestRunContext testRunContext) {
            testRunContext.setConfigGroup(runtime.configGroup);
        }
        return contextChain;
    }

    // ---------------------------------------------------------------------
    // 重写 AbstractTestElementExecutable 中的方法
    // ---------------------------------------------------------------------

    /**
     * 执行取样器的核心逻辑，按照预定义的执行流程处理测试请求
     *
     * <p>该方法是取样器执行的入口点，负责协调整个执行过程，包括前置处理、核心业务执行、
     * 后置处理、断言验证和变量提取等环节。</p>
     *
     * @param context 测试上下文
     * @param result  测试结果对象
     */
    @Override
    protected final void execute(ContextWrapper context, R result) {
        if (!result.getStatus().isPassed()) {
            // 前置处理器可能执行失败，无需执行测试步骤
            return;
        }
        runtime.config = (CONFIG) context.evaluate(runtime.config);
        try {
            // 执行前置处理
            if (chain.applyPreHandle(context, runtime)) {
                // 业务处理
                handleRequest(context, result);
                result.sampleStart();
                sample(context, result);
                result.sampleEnd();
                handleResponse(context, result);
                // 执行后置处理
                chain.applyPostHandle(context, runtime);
                Optional.ofNullable(assertions).orElse(Collections.emptyList()).forEach(assertion -> assertion.assertThat(context));
                Optional.ofNullable(extractors).orElse(Collections.emptyList()).forEach(extractor -> extractor.process(context));
            }
        } catch (Throwable throwable) {
            // 1、sampler 执行异常 2、assertion 断言异常 3、extractor 提取异常
            result.setThrowable(throwable);
        } finally {
            // 最终处理
            chain.triggerAfterCompletion(context);
        }
    }


    @Override
    public SELF copy() {
        SELF self = super.copy();
        self.assertions = KryoUtil.copy(assertions);
        self.extractors = KryoUtil.copy(extractors);
        return self;
    }

    /**
     * 请求执行前处理，用于在发送请求前对数据进行预处理，比如进行表达式计算、数据格式化等操作。
     * <p>
     * 该方法在 {@link RyzeInterceptor#preHandle(ContextWrapper, TestElement)} 之前调用，
     * 子类可以重写此方法来实现特定的请求前处理逻辑。
     * </p>
     *
     * @param context 测试上下文
     * @param result  测试结果对象
     */
    protected void handleRequest(ContextWrapper context, R result) {
        // do nothing.
    }

    /**
     * 执行请求的核心方法，由子类实现具体的业务逻辑，如发送HTTP请求、执行数据库查询等操作。
     * <p>不要在该方法内进行请求的动态数据替换，请使用 {@link AbstractSampler#handleRequest(ContextWrapper, SampleResult)}。
     *
     * @param context 测试上下文
     * @param result  测试结果对象
     */
    protected abstract void sample(ContextWrapper context, R result);

    /**
     * 请求执行后处理，用于在请求执行完成后对结果进行后处理，比如数据解析、格式化等操作。
     * <p>
     * 该方法在 {@link RyzeInterceptor#postHandle(ContextWrapper, TestElement)} 之后调用，
     * 子类可以重写此方法来实现特定的响应后处理逻辑。
     * </p>
     *
     * @param context 测试上下文
     * @param result  测试结果对象
     */
    protected void handleResponse(ContextWrapper context, R result) {
        // do nothing.
    }


    /**
     * 获取提取器列表
     *
     * @return 提取器列表
     */
    public List<Extractor> getExtractors() {
        return extractors;
    }

    /**
     * 设置提取器列表
     *
     * @param extractors 提取器列表
     */
    public void setExtractors(List<Extractor> extractors) {
        this.extractors = extractors;
    }

    /**
     * 获取断言列表
     *
     * @return 断言列表
     */
    public List<Assertion> getAssertions() {
        return assertions;
    }

    /**
     * 设置断言列表
     *
     * @param assertions 断言列表
     */
    public void setAssertions(List<Assertion> assertions) {
        this.assertions = assertions;
    }

    /**
     * 取样器基础构建器，提供构建取样器实例的通用方法
     *
     * @param <ELE>               取样器类型
     * @param <SELF>              自己的类型
     * @param <CONFIG>            取样器配置类型
     * @param <CONFIGURE_BUILDER> 取样器配置类型构建器
     * @param <R>                 处理结果类型
     */
    public static abstract class Builder<ELE extends AbstractSampler<ELE, CONFIG, R>,
            SELF extends Builder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, CONFIGURES_BUILDER, PREPROCESSORS_BUILDER, POSTPROCESSORS_BUILDER, ASSERTIONS_BUILDER, EXTRACTORS_BUILDER, R>,
            CONFIG extends ConfigureItem<CONFIG>,
            CONFIGURE_BUILDER extends ConfigureBuilder<?, CONFIG>,
            CONFIGURES_BUILDER extends ExtensibleConfigureElementsBuilder,
            PREPROCESSORS_BUILDER extends ExtensiblePreprocessorsBuilder,
            POSTPROCESSORS_BUILDER extends ExtensiblePostprocessorsBuilder,
            ASSERTIONS_BUILDER extends ExtensibleAssertionsBuilder<ASSERTIONS_BUILDER>,
            EXTRACTORS_BUILDER extends ExtensibleExtractorsBuilder<EXTRACTORS_BUILDER>,
            R extends SampleResult>
            extends AbstractTestElementExecutable.Builder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, CONFIGURES_BUILDER, PREPROCESSORS_BUILDER, POSTPROCESSORS_BUILDER, R> {

        /**
         * 断言列表
         */
        protected List<Assertion> assertions;

        /**
         * 提取器列表
         */
        protected List<Extractor> extractors;

        /**
         * 设置断言列表
         *
         * @param assertions 断言列表
         * @return 构建器实例
         */
        public SELF assertions(List<Assertion> assertions) {
            this.assertions = Collections.addAllIfNonNull(this.assertions, assertions);
            return self;
        }

        /**
         * 通过自定义构建器添加断言
         *
         * @param customizer 断言构建器自定义函数
         * @return 构建器实例
         */
        public SELF assertions(Customizer<ASSERTIONS_BUILDER> customizer) {
            ASSERTIONS_BUILDER builder = getAssertionsBuilder();
            customizer.customize(builder);
            this.assertions = Collections.addAllIfNonNull(this.assertions, builder.build());
            return self;
        }

        /**
         * 通过闭包添加断言
         *
         * @param closure 断言构建器闭包
         * @return 构建器实例
         */
        public SELF assertions(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "ASSERTIONS_BUILDER") Closure<?> closure) {
            ASSERTIONS_BUILDER builder = getAssertionsBuilder();
            Groovy.call(closure, builder);
            this.assertions = Collections.addAllIfNonNull(this.assertions, builder.build());
            return self;
        }

        /**
         * 设置断言列表的别名方法
         *
         * @param assertions 断言列表
         * @return 构建器实例
         */
        public SELF validators(List<Assertion> assertions) {
            return assertions(assertions);
        }

        /**
         * 通过自定义构建器添加断言的别名方法
         *
         * @param customizer 断言构建器自定义函数
         * @return 构建器实例
         */
        public SELF validators(Customizer<ASSERTIONS_BUILDER> customizer) {
            return assertions(customizer);
        }

        /**
         * 通过闭包添加断言的别名方法
         *
         * @param closure 断言构建器闭包
         * @return 构建器实例
         */
        public SELF validators(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "ASSERTIONS_BUILDER") Closure<?> closure) {
            return assertions(closure);
        }

        /**
         * 设置提取器列表
         *
         * @param extractors 提取器列表
         * @return 构建器实例
         */
        public SELF extractors(List<Extractor> extractors) {
            this.extractors = Collections.addAllIfNonNull(this.extractors, extractors);
            return self;
        }

        /**
         * 通过自定义构建器添加提取器
         *
         * @param customizer 提取器构建器自定义函数
         * @return 构建器实例
         */
        public SELF extractors(Customizer<EXTRACTORS_BUILDER> customizer) {
            EXTRACTORS_BUILDER builder = getExtractorsBuilder();
            customizer.customize(builder);
            this.extractors = Collections.addAllIfNonNull(this.extractors, builder.build());
            return self;
        }

        /**
         * 通过闭包添加提取器
         *
         * @param closure 提取器构建器闭包
         * @return 构建器实例
         */
        public SELF extractors(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "EXTRACTORS_BUILDER") Closure<?> closure) {
            EXTRACTORS_BUILDER builder = getExtractorsBuilder();
            Groovy.call(closure, builder);
            this.extractors = Collections.addAllIfNonNull(this.extractors, builder.build());
            return self;
        }


        /**
         * 获取断言构建器
         *
         * @return 断言构建器
         */
        protected abstract ASSERTIONS_BUILDER getAssertionsBuilder();

        /**
         * 获取提取器构建器
         *
         * @return 提取器构建器
         */
        protected abstract EXTRACTORS_BUILDER getExtractorsBuilder();
    }

}