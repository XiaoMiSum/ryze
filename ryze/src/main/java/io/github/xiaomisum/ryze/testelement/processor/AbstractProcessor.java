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

package io.github.xiaomisum.ryze.testelement.processor;

import com.alibaba.fastjson2.annotation.JSONField;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.builder.ExtensibleExtractorsBuilder;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.extractor.Extractor;
import io.github.xiaomisum.ryze.interceptor.RyzeInterceptor;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.ValidateResult;
import io.github.xiaomisum.ryze.support.groovy.Groovy;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.xiaomisum.ryze.TestStatus.broken;

/**
 * 处理器抽象基类，实现了处理器的核心处理逻辑
 * <p>
 * AbstractProcessor 是所有处理器（包括前置处理器和后置处理器）的基类，
 * 定义了处理器执行的核心流程，包括初始化、请求处理、采样、响应处理、变量提取等步骤。
 * </p>
 * <p>
 * 该类提供了完整的处理器执行生命周期：
 * <ol>
 *   <li>初始化上下文环境</li>
 *   <li>执行前置拦截器</li>
 *   <li>处理请求数据</li>
 *   <li>执行采样（核心业务逻辑）</li>
 *   <li>处理响应数据</li>
 *   <li>执行后置拦截器</li>
 *   <li>提取变量</li>
 *   <li>执行最终清理工作</li>
 * </ol>
 * </p>
 *
 * @param <SELF>   处理器自身类型，用于构建器模式
 * @param <CONFIG> 配置项类型，继承自 ConfigureItem
 * @param <R>      执行结果类型，继承自 SampleResult
 * @author xiaomi
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractProcessor<SELF extends AbstractProcessor<SELF, CONFIG, R>, CONFIG extends ConfigureItem<CONFIG>, R extends SampleResult>
        extends AbstractTestElement<AbstractProcessor<SELF, CONFIG, R>, CONFIG, R>
        implements Processor, TestElementConstantsInterface {

    /**
     * 静态线程池，用于异步执行处理器（使用虚拟线程）
     */
    private static final ExecutorService asyncExecutor = Executors.newVirtualThreadPerTaskExecutor();
    @JSONField(name = EXTRACTORS, ordinal = 10)
    protected List<Extractor> extractors;
    @JSONField(name = ASYNC, ordinal = 11)
    protected boolean async = false;
    @JSONField(name = CONDITION, ordinal = 12)
    protected String condition;

    /**
     * 默认构造函数
     */
    public AbstractProcessor() {
        super();
    }

    /**
     * 使用构建器初始化处理器
     *
     * @param builder 处理器构建器
     */
    public AbstractProcessor(Builder<SELF, ?, CONFIG, ?, ?, R> builder) {
        super(builder);
        this.extractors = builder.extractors;
        this.async = builder.async;
        this.condition = builder.condition;
    }

    /**
     * 关闭异步执行器线程池
     */
    public static void shutdownAsyncExecutor() {
        asyncExecutor.shutdown();
    }

    /**
     * 设置变量提取器列表
     *
     * @param extractors 变量提取器列表
     */
    public void setExtractors(List<Extractor> extractors) {
        this.extractors = extractors;
    }

    /**
     * 初始化处理器上下文环境
     * <p>
     * 该方法会在处理器首次执行时调用，用于创建本地上下文环境，设置测试结果、测试元素，
     * 计算运行时ID和标题，处理过滤拦截器等初始化工作。
     * </p>
     *
     * @param session 会话运行器
     * @return 初始化完成的上下文包装器
     */
    protected ContextWrapper initialized(SessionRunner session) {
        super.initialized();
        var localContext = new ContextWrapper(session);
        localContext.setTestResult(getTestResult());
        localContext.setTestElement(this);
        runtime.id = (String) localContext.evaluate(id);
        runtime.title = (String) localContext.evaluate(title);
        runtime.config = (CONFIG) localContext.evaluate(runtime.config);
        handleFilterInterceptors(localContext);
        return localContext;
    }

    /**
     * 执行处理器核心逻辑
     * <p>
     * 这是处理器的入口方法，负责协调整个处理器的执行流程，包括：
     * <ul>
     *   <li>初始化上下文（如果尚未初始化）</li>
     *   <li>应用前置拦截器</li>
     *   <li>处理请求数据</li>
     *   <li>执行采样（业务逻辑）</li>
     *   <li>处理响应数据</li>
     *   <li>应用后置拦截器</li>
     *   <li>执行变量提取</li>
     *   <li>处理异常情况</li>
     *   <li>执行最终清理工作</li>
     * </ul>
     * </p>
     *
     * @param context 上下文包装器
     */
    @Override
    public void process(ContextWrapper context) {
        var localContext = initialized(context.getSessionRunner());
        var result = (R) localContext.getTestResult();
        var isConditionPassed = StringUtils.isBlank(condition) || switch (localContext.evaluate(condition)) {
            case Boolean bool -> bool;
            case String str -> !str.isEmpty() && Strings.CS.equalsAny(str, "1", "t", "T", "true", "TRUE", "True");
            case Number num -> num.intValue() == 1;
            case null, default -> false;
        };

        // 条件判断，如果条件不满足则直接返回
        if (!(boolean) isConditionPassed) {
            return;
        }
        Runnable task = () -> {
            try {
                // 执行前置处理
                if (runtime.chain.applyPreHandle(localContext, runtime)) {
                    // 业务处理
                    handleRequest(localContext, result);
                    result.sampleStart();
                    sample(localContext, result);
                    result.sampleEnd();
                    handleResponse(localContext, result);
                    // 执行后置处理
                    runtime.chain.applyPostHandle(localContext, runtime);
                    Optional.ofNullable(extractors).orElse(Collections.emptyList()).forEach(extractor -> extractor.process(localContext));
                }
            } catch (Throwable throwable) {
                result.setThrowable(throwable);
                context.getTestResult().setThrowable(throwable);
                context.getTestResult().setStatus(broken);
            } finally {
                // 最终处理
                runtime.chain.triggerAfterCompletion(localContext);
            }
        };

        if (async) {
            // 异步执行处理器
            CompletableFuture.runAsync(task, asyncExecutor);
        } else {
            // 同步执行处理器
            task.run();
        }
    }

    /**
     * 验证测试执行元件
     * <p>
     * 验证测试执行元件的数据有效性，确保测试可以正常执行。
     * </p>
     *
     * @return 验证结果
     */
    @Override
    public ValidateResult validate() {
        var result = super.validate();
        if (config == null) {
            result.append("执行类测试元件 %s 字段值缺失或为空", CONFIG);
        }
        return result;
    }

    /**
     * 请求执行前处理。比如请求数据的表达式计算。
     *
     * <p>该方法在 {@link RyzeInterceptor#preHandle(ContextWrapper, TestElement)} )} 之前调用。
     * 子类可以重写此方法来实现请求前的特殊处理逻辑，例如参数计算、数据预处理等。</p>
     *
     * @param context 上下文包装器
     * @param result  执行结果对象
     */
    protected void handleRequest(ContextWrapper context, R result) {
        // do nothing.
    }

    /**
     * 执行请求核心业务逻辑
     * <p>
     * 这是处理器的核心方法，需要子类实现具体的业务逻辑。
     * 不要在该方法内进行请求的动态数据替换，请使用 {@link AbstractProcessor#handleRequest(ContextWrapper, SampleResult)}。
     * </p>
     *
     * @param context 上下文包装器
     * @param result  执行结果对象
     */
    protected abstract void sample(ContextWrapper context, R result);

    /**
     * 请求执行后处理。
     *
     * <p>该方法在 {@link RyzeInterceptor#postHandle(ContextWrapper, TestElement)} 之后调用。
     * 子类可以重写此方法来实现响应后的特殊处理逻辑，例如数据格式化、结果验证等。</p>
     *
     * @param context 上下文包装器
     * @param result  执行结果对象
     */
    protected void handleResponse(ContextWrapper context, R result) {
        // do nothing.
    }

    /**
     * 处理器 构建器抽象类
     *
     * @param <ELE>               处理器元件类型
     * @param <SELF>              构建器自己
     * @param <CONFIG>            配置项类型
     * @param <CONFIGURE_BUILDER> 配置项构建器
     * @param <R>                 执行结果类型
     */
    public static abstract class Builder<ELE extends AbstractProcessor<ELE, CONFIG, R>,
            SELF extends Builder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, EXTRACTORS_BUILDER, R>,
            CONFIG extends ConfigureItem<CONFIG>,
            CONFIGURE_BUILDER extends ConfigureBuilder<?, CONFIG>,
            EXTRACTORS_BUILDER extends ExtensibleExtractorsBuilder<EXTRACTORS_BUILDER>,
            R extends SampleResult>
            extends AbstractTestElement.Builder<AbstractProcessor<ELE, CONFIG, R>, SELF, CONFIG, CONFIGURE_BUILDER, R> {

        protected List<Extractor> extractors;
        protected boolean async;
        protected String condition;

        /**
         * 配置变量提取器
         *
         * @param customizer 提取器自定义配置器
         * @return 构建器自身实例
         */
        public SELF extractors(Customizer<EXTRACTORS_BUILDER> customizer) {
            EXTRACTORS_BUILDER builder = getExtractorsBuilder();
            customizer.customize(builder);
            this.extractors = Collections.addAllIfNonNull(this.extractors, builder.build());
            return self;
        }

        /**
         * 使用Groovy闭包配置变量提取器
         *
         * @param closure Groovy闭包
         * @return 构建器自身实例
         */
        public SELF extractors(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "EXTRACTORS_BUILDER") Closure<?> closure) {
            EXTRACTORS_BUILDER builder = getExtractorsBuilder();
            Groovy.call(closure, builder);
            this.extractors = Collections.addAllIfNonNull(this.extractors, builder.build());
            return self;
        }

        /**
         * 设置异步执行
         *
         * @param async 是否异步执行
         * @return 构建器自身实例
         */
        public SELF async(boolean async) {
            this.async = async;
            return self;
        }

        /**
         * 设置异步执行
         *
         * @return 构建器自身实例
         */
        public SELF async() {
            this.async = true;
            return self;
        }

        /**
         * 设置执行条件， FreeMarker 表达式
         *
         * @param condition FreeMarker 表达式
         * @return 构建器自身实例
         */
        public SELF condition(String condition) {
            this.condition = condition;
            return self;
        }

        /**
         * 获取提取器构建器实例
         *
         * @return 提取器构建器
         */
        protected abstract EXTRACTORS_BUILDER getExtractorsBuilder();
    }

    /**
     * 前置处理器构建器抽象类
     *
     * @param <ELE>               处理器元件类型
     * @param <SELF>              构建器自己
     * @param <CONFIG>            配置项类型
     * @param <CONFIGURE_BUILDER> 配置项构建器
     * @param <R>                 执行结果类型
     */
    public static abstract class PreprocessorBuilder<ELE extends AbstractProcessor<ELE, CONFIG, R>,
            SELF extends PreprocessorBuilder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, EXTRACTORS_BUILDER, R>,
            CONFIG extends ConfigureItem<CONFIG>,
            CONFIGURE_BUILDER extends ConfigureBuilder<?, CONFIG>,
            EXTRACTORS_BUILDER extends ExtensibleExtractorsBuilder<EXTRACTORS_BUILDER>,
            R extends SampleResult>
            extends Builder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, EXTRACTORS_BUILDER, R> {
    }

    /**
     * 后置处理器构建器抽象类
     *
     * @param <ELE>               处理器元件类型
     * @param <SELF>              构建器自己
     * @param <CONFIG>            配置项类型
     * @param <CONFIGURE_BUILDER> 配置项构建器
     * @param <R>                 执行结果类型
     */
    public static abstract class PostprocessorBuilder<ELE extends AbstractProcessor<ELE, CONFIG, R>,
            SELF extends PostprocessorBuilder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, EXTRACTORS_BUILDER, R>,
            CONFIG extends ConfigureItem<CONFIG>,
            CONFIGURE_BUILDER extends ConfigureBuilder<?, CONFIG>,
            EXTRACTORS_BUILDER extends ExtensibleExtractorsBuilder<EXTRACTORS_BUILDER>,
            R extends SampleResult>
            extends Builder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, EXTRACTORS_BUILDER, R> {
    }


}