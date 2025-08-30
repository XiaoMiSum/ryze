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
import io.github.xiaomisum.ryze.assertion.AbstractAssertion;
import io.github.xiaomisum.ryze.assertion.Assertion;
import io.github.xiaomisum.ryze.assertion.builtin.JSONAssertion;
import io.github.xiaomisum.ryze.assertion.builtin.ResultAssertion;
import io.github.xiaomisum.ryze.builder.ExtensibleExtractorsBuilder;
import io.github.xiaomisum.ryze.builder.IBuilder;
import io.github.xiaomisum.ryze.builder.LazyBuilder;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.config.InterceptorConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.extractor.AbstractExtractor;
import io.github.xiaomisum.ryze.extractor.Extractor;
import io.github.xiaomisum.ryze.extractor.builtin.JSONExtractor;
import io.github.xiaomisum.ryze.extractor.builtin.RegexExtractor;
import io.github.xiaomisum.ryze.extractor.builtin.ResultExtractor;
import io.github.xiaomisum.ryze.interceptor.HandlerExecutionChain;
import io.github.xiaomisum.ryze.interceptor.RyzeInterceptor;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import io.github.xiaomisum.ryze.testelement.configure.ConfigureElement;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Postprocessor;
import io.github.xiaomisum.ryze.testelement.processor.Preprocessor;
import io.github.xiaomisum.ryze.testelement.processor.builtin.SyncTimer;
import io.github.xiaomisum.ryze.protocol.debug.config.DebugDefaults;
import io.github.xiaomisum.ryze.protocol.debug.processer.DebugPostprocessor;
import io.github.xiaomisum.ryze.protocol.debug.processer.DebugPreprocessor;
import io.github.xiaomisum.ryze.protocol.debug.sampler.DebugSampler;
import io.github.xiaomisum.ryze.protocol.http.assertion.HTTPResponseAssertion;
import io.github.xiaomisum.ryze.protocol.http.config.HTTPDefaults;
import io.github.xiaomisum.ryze.protocol.http.extractor.HTTPHeaderExtractor;
import io.github.xiaomisum.ryze.protocol.http.processor.HTTPPostprocessor;
import io.github.xiaomisum.ryze.protocol.http.processor.HTTPPreprocessor;
import io.github.xiaomisum.ryze.protocol.http.sampler.HTTPSampler;
import io.github.xiaomisum.ryze.protocol.jdbc.config.JDBCDatasource;
import io.github.xiaomisum.ryze.protocol.jdbc.processor.JDBCPostprocessor;
import io.github.xiaomisum.ryze.protocol.jdbc.processor.JDBCPreprocessor;
import io.github.xiaomisum.ryze.protocol.jdbc.sampler.JDBCSampler;
import io.github.xiaomisum.ryze.protocol.redis.config.RedisDatasource;
import io.github.xiaomisum.ryze.protocol.redis.processor.RedisPostprocessor;
import io.github.xiaomisum.ryze.protocol.redis.processor.RedisPreprocessor;
import io.github.xiaomisum.ryze.protocol.redis.sampler.RedisSampler;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.KryoUtil;
import io.github.xiaomisum.ryze.support.groovy.Groovy;

import java.util.*;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * 测试组件抽象基类
 * <p>
 * 该类是所有测试组件的基类，提供了测试组件的公共属性和方法实现。它定义了测试组件的核心结构，
 * 包括ID、标题、配置、拦截器、元数据等基本属性，以及复制、初始化、验证等通用功能。
 * </p>
 * <p>
 * 该类使用泛型设计，支持不同类型的具体测试组件实现，如测试套件(TestSuite)、取样器(Sampler)等。
 * 通过Builder模式支持灵活的测试组件构建方式，并集成了JSON序列化/反序列化功能，便于测试配置的持久化管理。
 * </p>
 *
 * @param <SELF>   测试组件自身类型，用于支持链式调用和类型安全的返回值
 * @param <CONFIG> 测试组件配置项类型，必须是ConfigureItem的子类
 * @param <R>      测试结果类型，必须是Result的子类
 * @author xiaomi
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractTestElement<SELF extends AbstractTestElement<SELF, CONFIG, R>, CONFIG extends ConfigureItem<CONFIG>, R extends Result>
        implements TestElement<R>, TestElementConstantsInterface {

    /**
     * 测试组件唯一标识符
     * <p>用于唯一标识一个测试组件实例，在JSON序列化中对应"id"字段</p>
     */
    @JSONField(name = ID)
    protected String id;

    /**
     * 测试组件标题
     * <p>用于描述测试组件的名称或标题，在JSON序列化中对应"title"字段，序列化顺序为1</p>
     */
    @JSONField(name = TITLE, ordinal = 1)
    protected String title;

    /**
     * 测试组件禁用状态
     * <p>标识测试组件是否被禁用，在JSON序列化中对应"disabled"字段，序列化顺序为3，默认为false</p>
     */
    @JSONField(name = DISABLED, ordinal = 3)
    protected boolean disabled = false;

    /**
     * 测试组件配置项
     * <p>存储测试组件的配置信息，在JSON序列化中对应"config"字段，序列化顺序为4</p>
     */
    @JSONField(name = CONFIG, ordinal = 4)
    protected CONFIG config;

    /**
     * 测试组件拦截器列表
     * <p>存储测试组件的拦截器，在JSON序列化中对应"interceptors"字段，序列化顺序为8，
     * 使用RyzeInterceptor进行反序列化处理</p>
     */
    @JSONField(name = INTERCEPTORS, ordinal = 8, deserializeUsing = RyzeInterceptor.class)
    protected List<RyzeInterceptor> interceptors;

    /**
     * 测试组件元数据
     * <p>可以挂载一些辅助数据，在JSON序列化中对应"metadata"字段，序列化顺序为2，默认为空HashMap</p>
     */
    // 元数据，可以挂载一些辅助数据
    @JSONField(name = METADATA, ordinal = 2)
    protected Map<String, Object> metadata = new HashMap<>();

    /**
     * 原始数据映射
     * <p>存储测试组件的原始数据，不参与JSON序列化和反序列化</p>
     */
    @JSONField(serialize = false, deserialize = false)
    protected Map<String, Object> rawData;

    /**
     * 运行时实例
     * <p>存储测试组件的运行时副本，不参与JSON序列化和反序列化</p>
     */
    @JSONField(serialize = false, deserialize = false)
    protected SELF runtime;

    /**
     * 初始化状态标识
     * <p>标识测试组件是否已完成初始化，不参与JSON序列化和反序列化</p>
     */
    @JSONField(serialize = false, deserialize = false)
    protected boolean initialized = false;

    /**
     * 拦截器执行链
     * <p>存储测试组件的拦截器执行链，不参与JSON序列化和反序列化</p>
     */
    @JSONField(serialize = false, deserialize = false)
    protected HandlerExecutionChain chain;

    /**
     * 基于构建器的构造函数
     * <p>使用构建器初始化测试组件的各个属性</p>
     *
     * @param builder 测试组件构建器实例
     */
    public AbstractTestElement(Builder<SELF, ?, CONFIG, ?, R> builder) {
        id = builder.id;
        title = builder.title;
        disabled = builder.disabled;
        config = builder.config;
        interceptors = (builder.ryzeInterceptors);
        metadata.putAll(builder.metadata);
    }

    /**
     * 默认构造函数
     */
    public AbstractTestElement() {
    }

    /**
     * 初始化测试组件
     * <p>标记测试组件为已初始化状态，并创建运行时副本</p>
     */
    protected void initialized() {
        initialized = true;
        runtime = copy();
    }

    /**
     * 处理过滤器拦截器
     * <p>根据上下文信息过滤和排序拦截器，构建拦截器执行链</p>
     *
     * @param context 上下文包装器
     */
    protected void handleFilterInterceptors(ContextWrapper context) {
        interceptors = Collections.addAllIfNonNull(interceptors, context.getConfigGroup().get(INTERCEPTORS));
        if (Objects.isNull(interceptors) || interceptors.isEmpty()) {
            return;
        }
        //
        var runtimeInterceptors = interceptors.stream().filter(interceptor -> interceptor.supports(context)).distinct()
                .sorted(Comparator.comparingInt(RyzeInterceptor::getOrder)).toList();
        chain = new HandlerExecutionChain(runtimeInterceptors);
    }

    /**
     * 获取测试结果对象
     * <p>抽象方法，由具体子类实现，用于创建与当前测试组件关联的测试结果对象</p>
     *
     * @return 测试结果对象
     */
    protected abstract R getTestResult();

    /**
     * 创建新实例
     * <p>通过反射机制创建当前测试组件类的新实例</p>
     *
     * @return 新创建的测试组件实例
     * @throws RuntimeException 如果实例化失败
     */
    protected SELF newInstance() {
        try {
            return (SELF) this.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("实例化 %s 失败", this.getClass().getName()), e);
        }
    }

    /**
     * 复制测试组件
     * <p>创建当前测试组件的深拷贝副本，包括所有属性的复制</p>
     *
     * @return 复制的测试组件实例
     */
    @Override
    public SELF copy() {
        SELF self = newInstance();
        self.title = KryoUtil.copy(title);
        self.id = KryoUtil.copy(id);
        self.disabled = KryoUtil.copy(disabled);
        self.metadata = KryoUtil.copy(metadata);
        self.config = KryoUtil.copy(config);
        self.interceptors = KryoUtil.copy(interceptors);
        return self;
    }

    //  getter and setter

    /**
     * 获取测试组件ID
     *
     * @return 测试组件ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置测试组件ID
     *
     * @param id 测试组件ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取测试组件标题
     *
     * @return 测试组件标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置测试组件标题
     *
     * @param title 测试组件标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 判断测试组件是否被禁用
     *
     * @return 如果被禁用返回true，否则返回false
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * 设置测试组件禁用状态
     *
     * @param disabled 禁用状态
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * 获取测试组件配置项
     *
     * @return 测试组件配置项
     */
    public CONFIG getConfig() {
        return config;
    }

    /**
     * 设置测试组件配置项
     *
     * @param config 测试组件配置项
     */
    public void setConfig(CONFIG config) {
        this.config = config;
    }

    /**
     * 获取测试组件拦截器列表
     *
     * @return 测试组件拦截器列表
     */
    public List<RyzeInterceptor> getInterceptors() {
        return interceptors;
    }

    /**
     * 设置测试组件拦截器列表
     *
     * @param interceptors 测试组件拦截器列表
     */
    public void setInterceptors(List<RyzeInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * 获取测试组件元数据
     *
     * @return 测试组件元数据映射
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * 设置测试组件元数据
     *
     * @param metadata 测试组件元数据映射
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * 获取原始数据
     *
     * @return 原始数据映射
     */
    public Map<String, Object> getRawData() {
        return rawData;
    }

    /**
     * 设置原始数据
     *
     * @param rawData 原始数据映射
     */
    public void setRawData(Map<String, Object> rawData) {
        this.rawData = rawData;
    }

    /**
     * 获取运行时实例
     *
     * @return 运行时实例
     */
    public SELF getRuntime() {
        return runtime;
    }


    /**
     * 测试元件基础构建实现类
     * <p>
     * 该构建器为测试组件提供了一个通用的构建模式实现，支持链式调用方式配置测试组件的各种属性。
     * 通过泛型设计，可以适配不同类型的测试组件和配置项。
     * </p>
     *
     * @param <ELE>               测试元件类型，必须是AbstractTestElement的子类
     * @param <SELF>              构建类自身类型，用于支持链式调用
     * @param <CONFIG>            配置项类型，必须是ConfigureItem的子类
     * @param <CONFIGURE_BUILDER> 配置构建类类型，必须是ConfigureBuilder的子类
     * @param <R>                 结果类型，必须是Result的子类
     */
    public static abstract class Builder<ELE extends AbstractTestElement<ELE, CONFIG, R>,
            SELF extends Builder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, R>,
            CONFIG extends ConfigureItem<CONFIG>,
            CONFIGURE_BUILDER extends ConfigureBuilder<?, CONFIG>,
            R extends Result>
            implements TestElementBuilder<ELE> {

        /**
         * 测试组件ID
         */
        protected String id;

        /**
         * 测试组件标题
         */
        protected String title;

        /**
         * 测试组件禁用状态
         */
        protected boolean disabled;

        /**
         * 测试组件配置项
         */
        protected CONFIG config;

        /**
         * 测试组件拦截器列表
         */
        protected List<RyzeInterceptor> ryzeInterceptors;

        /**
         * 测试组件元数据
         */
        protected Map<String, Object> metadata = new HashMap<>();

        /**
         * 构建器自身实例，用于支持链式调用
         */
        protected SELF self;

        /**
         * 默认构造函数
         * <p>初始化自身实例引用</p>
         */
        public Builder() {
            self = (SELF) this;
        }

        /**
         * 设置测试组件ID
         *
         * @param id 测试组件ID
         * @return 当前构建器实例，用于链式调用
         */
        public SELF id(String id) {
            this.id = id;
            return self;
        }

        /**
         * 设置测试组件标题
         *
         * @param title 测试组件标题
         * @return 当前构建器实例，用于链式调用
         */
        public SELF title(String title) {
            this.title = title;
            return self;
        }

        /**
         * 设置测试组件禁用状态
         *
         * @param disabled 禁用状态
         * @return 当前构建器实例，用于链式调用
         */
        public SELF disabled(boolean disabled) {
            this.disabled = disabled;
            return self;
        }

        /**
         * 通过闭包配置测试组件配置项
         *
         * @param closure Groovy闭包，用于配置项的设置
         * @return 当前构建器实例，用于链式调用
         */
        public SELF config(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "CONFIGURE_BUILDER") Closure<?> closure) {
            CONFIGURE_BUILDER builder = getConfigureItemBuilder();
            Groovy.call(closure, builder);
            this.config = builder.build();
            return self;
        }

        /**
         * 通过自定义器配置测试组件配置项
         *
         * @param customizer 配置项自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF config(Customizer<CONFIGURE_BUILDER> customizer) {
            CONFIGURE_BUILDER builder = getConfigureItemBuilder();
            customizer.customize(builder);
            this.config = builder.build();
            return self;
        }

        /**
         * 通过构建器配置测试组件配置项
         *
         * @param builder 配置项构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF config(CONFIGURE_BUILDER builder) {
            this.config = builder.build();
            return self;
        }

        /**
         * 直接设置测试组件配置项
         *
         * @param config 配置项实例
         * @return 当前构建器实例，用于链式调用
         */
        public SELF config(CONFIG config) {
            this.config = config;
            return self;
        }

        /**
         * 设置测试组件元数据
         *
         * @param metadata 元数据映射
         * @return 当前构建器实例，用于链式调用
         */
        public SELF metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return self;
        }

        /**
         * 添加测试组件元数据项
         *
         * @param name  元数据项名称
         * @param value 元数据项值
         * @return 当前构建器实例，用于链式调用
         */
        public SELF metadata(String name, Object value) {
            this.metadata.put(name, value);
            return self;
        }

        /**
         * 设置测试组件拦截器
         *
         * @param interceptor 拦截器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF interceptors(RyzeInterceptor interceptor) {
            return interceptors(Collections.newArrayList(interceptor));
        }

        /**
         * 设置测试组件拦截器列表
         *
         * @param ryzeInterceptors 拦截器列表
         * @return 当前构建器实例，用于链式调用
         */
        public SELF interceptors(List<RyzeInterceptor> ryzeInterceptors) {
            this.ryzeInterceptors = Collections.addAllIfNonNull(this.ryzeInterceptors, ryzeInterceptors);
            return self;
        }

        /**
         * 通过自定义构建器添加测试组件拦截器
         *
         * @param customizer 拦截器构建器自定义函数
         * @return 构建器实例
         */
        public SELF interceptors(Customizer<InterceptorConfigureItem.Builder> customizer) {
            var builder = InterceptorConfigureItem.builder();
            customizer.customize(builder);
            return interceptors((List<RyzeInterceptor>) builder.build());
        }

        /**
         * 使用Groovy闭包添加测试组件拦截器
         *
         * @param closure Groovy闭包，用于配置测试组件拦截器构建器
         * @return 当前构建器实例，支持链式调用
         */
        public SELF interceptors(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = InterceptorConfigureItem.Builder.class) Closure<?> closure) {
            var builder = new InterceptorConfigureItem.Builder();
            Groovy.call(closure, builder);
            return interceptors((List<RyzeInterceptor>) builder.build());
        }

        /**
         * 设置测试组件拦截器列表
         *
         * @param interceptors 拦截器列表
         * @return 当前构建器实例，用于链式调用
         */
        public SELF interceptors(InterceptorConfigureItem interceptors) {
            return interceptors((List<RyzeInterceptor>) interceptors);
        }

        /**
         * 设置测试组件拦截器列表
         *
         * @param builder 拦截器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF interceptors(InterceptorConfigureItem.Builder builder) {
            return interceptors((List<RyzeInterceptor>) builder.build());
        }

        /**
         * 获取配置项构建器
         * <p>抽象方法，由具体子类实现</p>
         *
         * @return 配置项构建器实例
         */
        protected abstract CONFIGURE_BUILDER getConfigureItemBuilder();

    }

    /**
     * 测试配置基础构建实现类
     * <p>
     * 该类为测试配置项提供基础构建功能，作为各种具体配置构建器的基类。
     * </p>
     *
     * @param <SELF>   构建类自身类型，用于支持链式调用
     * @param <CONFIG> 配置项类型，必须是ConfigureItem的子类
     */
    public static abstract class ConfigureBuilder<SELF extends ConfigureBuilder<SELF, CONFIG>, CONFIG extends ConfigureItem<CONFIG>>
            implements IBuilder<CONFIG> {

        /**
         * 构建器自身实例，用于支持链式调用
         */
        protected SELF self;

        /**
         * 默认构造函数
         * <p>初始化自身实例引用</p>
         */
        protected ConfigureBuilder() {
            self = (SELF) this;
        }
    }

    /**
     * 配置元件构建器
     * <p>
     * 该构建器用于构建配置元件列表，支持多种协议的配置元件，如DebugDefaults、HTTPDefaults、
     * JDBCDatasource、RedisDatasource等。通过扩展ExtensibleConfigureElementsBuilder可以支持更多协议。
     * </p>
     *
     * @param <SELF> 构建类自身类型，用于支持链式调用
     */
    public static abstract class ConfigureElementsBuilder<SELF extends ConfigureElementsBuilder<SELF>> implements IBuilder<List<ConfigureElement>> {
        /**
         * 配置元件列表
         * <p>使用LazyBuilder延迟初始化</p>
         */
        protected final List<ConfigureElement> configureElements = new LazyBuilder<>();

        /**
         * 构建器自身实例，用于支持链式调用
         */
        protected SELF self;

        /**
         * 默认构造函数
         * <p>初始化自身实例引用</p>
         */
        protected ConfigureElementsBuilder() {
            self = (SELF) this;
        }

        /**
         * 应用自定义配置元件
         *
         * @param builder 自定义配置元件构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF apply(AbstractConfigureElement.Builder builder) {
            configureElements.add((ConfigureElement) builder.build());
            return self;
        }

        /**
         * 应用配置元件
         *
         * @param configureElement 配置元件实例
         * @return 当前构建器实例，用于链式调用
         */
        public SELF apply(ConfigureElement configureElement) {
            configureElements.add(configureElement);
            return self;
        }

        /**
         * 通过类型和闭包应用自定义配置元件
         *
         * @param type    配置元件构建器类型
         * @param closure Groovy闭包
         * @param <T>     配置元件构建器类型
         * @return 当前构建器实例，用于链式调用
         */
        public <T extends AbstractConfigureElement.Builder> SELF apply(Class<T> type, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "T") Closure<T> closure) {
            var builder = Groovy.builder(type, closure);
            configureElements.add((ConfigureElement) builder.build());
            return self;
        }

        // -----------------------DebugDefaults----------------------------

        /**
         * 应用Debug配置默认值
         *
         * @param defaults Debug配置默认值
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(DebugDefaults defaults) {
            configureElements.add(defaults);
            return self;
        }

        /**
         * 通过自定义器配置Debug配置默认值
         *
         * @param customizer Debug配置默认值自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(Customizer<DebugDefaults.Builder> customizer) {
            var builder = DebugDefaults.builder();
            customizer.customize(builder);
            configureElements.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置Debug配置默认值
         *
         * @param builder Debug配置默认值构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(DebugDefaults.Builder builder) {
            configureElements.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置Debug配置默认值
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DebugDefaults.Builder.class) Closure<?> closure) {
            var builder = DebugDefaults.builder();
            call(closure, builder);
            configureElements.add(builder.build());
            return self;
        }

        // -----------------------HTTPDefaults----------------------------

        /**
         * 应用HTTP配置默认值
         *
         * @param defaults HTTP配置默认值
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(HTTPDefaults defaults) {
            configureElements.add(defaults);
            return self;
        }

        /**
         * 通过自定义器配置HTTP配置默认值
         *
         * @param customizer HTTP配置默认值自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(Customizer<HTTPDefaults.Builder> customizer) {
            var builder = HTTPDefaults.builder();
            customizer.customize(builder);
            configureElements.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置HTTP配置默认值
         *
         * @param builder HTTP配置默认值构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(HTTPDefaults.Builder builder) {
            configureElements.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置HTTP配置默认值
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HTTPDefaults.Builder.class) Closure<?> closure) {
            var builder = HTTPDefaults.builder();
            call(closure, builder);
            configureElements.add(builder.build());
            return self;
        }

        // -----------------------JDBCDatasource----------------------------

        /**
         * 应用JDBC数据源配置
         *
         * @param datasource JDBC数据源配置
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(JDBCDatasource datasource) {
            configureElements.add(datasource);
            return self;
        }

        /**
         * 通过自定义器配置JDBC数据源
         *
         * @param customizer JDBC数据源自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(Customizer<JDBCDatasource.Builder> customizer) {
            var builder = JDBCDatasource.builder();
            customizer.customize(builder);
            configureElements.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置JDBC数据源
         *
         * @param builder JDBC数据源构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(JDBCDatasource.Builder builder) {
            configureElements.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置JDBC数据源
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = JDBCDatasource.Builder.class) Closure<?> closure) {
            var builder = JDBCDatasource.builder();
            call(closure, builder);
            configureElements.add(builder.build());
            return self;
        }

        // -----------------------RedisDatasource----------------------------

        /**
         * 应用Redis数据源配置
         *
         * @param datasource Redis数据源配置
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(RedisDatasource datasource) {
            configureElements.add(datasource);
            return self;
        }

        /**
         * 通过自定义器配置Redis数据源
         *
         * @param customizer Redis数据源自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(Customizer<RedisDatasource.Builder> customizer) {
            var builder = RedisDatasource.builder();
            customizer.customize(builder);
            configureElements.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置Redis数据源
         *
         * @param builder Redis数据源构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(RedisDatasource.Builder builder) {
            configureElements.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置Redis数据源
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RedisDatasource.Builder.class) Closure<?> closure) {
            var builder = RedisDatasource.builder();
            call(closure, builder);
            configureElements.add(builder.build());
            return self;
        }


        /**
         * 构建配置元件列表
         *
         * @return 配置元件列表
         */
        public List<ConfigureElement> build() {
            return configureElements;
        }
    }

    /**
     * 前置处理器构建器
     * <p>
     * 该构建器用于构建前置处理器列表，支持多种协议的前置处理器，如DebugPreprocessor、
     * HTTPPreprocessor、JDBCPreprocessor、RedisPreprocessor等。通过扩展ExtensiblePreprocessorsBuilder
     * 可以支持更多协议的前置处理器。
     * </p>
     *
     * @param <SELF>               构建类自身类型，用于支持链式调用
     * @param <EXTRACTORS_BUILDER> 提取器构建器类型
     */
    public static abstract class PreprocessorsBuilder<SELF extends PreprocessorsBuilder<SELF, EXTRACTORS_BUILDER>,
            EXTRACTORS_BUILDER extends ExtensibleExtractorsBuilder>
            implements IBuilder<List<Preprocessor>> {

        /**
         * 前置处理器列表
         * <p>使用LazyBuilder延迟初始化</p>
         */
        protected final List<Preprocessor> preprocessors = new LazyBuilder<>();

        /**
         * 构建器自身实例，用于支持链式调用
         */
        protected SELF self;

        /**
         * 默认构造函数
         * <p>初始化自身实例引用</p>
         */
        protected PreprocessorsBuilder() {
            self = (SELF) this;
        }

        /**
         * 应用自定义前置处理器
         *
         * @param builder 自定义前置处理器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF apply(AbstractProcessor.PreprocessorBuilder builder) {
            preprocessors.add((Preprocessor) builder.build());
            return self;
        }

        /**
         * 应用前置处理器
         *
         * @param preprocessor 前置处理器实例
         * @return 当前构建器实例，用于链式调用
         */
        public SELF apply(Preprocessor preprocessor) {
            preprocessors.add(preprocessor);
            return self;
        }

        /**
         * 通过类型和闭包应用自定义前置处理器
         *
         * @param type    前置处理器构建器类型
         * @param closure Groovy闭包
         * @param <T>     前置处理器构建器类型
         * @return 当前构建器实例，用于链式调用
         */
        public <T extends AbstractProcessor.PreprocessorBuilder> SELF apply(Class<T> type, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "T") Closure<T> closure) {
            var builder = Groovy.builder(type, closure);
            preprocessors.add((Preprocessor) builder.build());
            return self;
        }

        // -----------------------DebugPreprocessor----------------------------

        /**
         * 应用Debug前置处理器
         *
         * @param preprocessor Debug前置处理器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(DebugPreprocessor preprocessor) {
            preprocessors.add(preprocessor);
            return self;
        }

        /**
         * 通过自定义器配置Debug前置处理器
         *
         * @param customizer Debug前置处理器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(Customizer<DebugPreprocessor.Builder> customizer) {
            var builder = DebugPreprocessor.builder();
            customizer.customize(builder);
            preprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置Debug前置处理器
         *
         * @param builder Debug前置处理器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(DebugPreprocessor.Builder builder) {
            preprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置Debug前置处理器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DebugPreprocessor.Builder.class) Closure<?> closure) {
            var builder = DebugPreprocessor.builder();
            call(closure, builder);
            preprocessors.add(builder.build());
            return self;
        }

        // -----------------------HTTPPreprocessor----------------------------

        /**
         * 应用HTTP前置处理器
         *
         * @param preprocessor HTTP前置处理器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(HTTPPreprocessor preprocessor) {
            preprocessors.add(preprocessor);
            return self;
        }

        /**
         * 通过自定义器配置HTTP前置处理器
         *
         * @param customizer HTTP前置处理器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(Customizer<HTTPPreprocessor.Builder> customizer) {
            var builder = HTTPPreprocessor.builder();
            customizer.customize(builder);
            preprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置HTTP前置处理器
         *
         * @param builder HTTP前置处理器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(HTTPPreprocessor.Builder builder) {
            preprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置HTTP前置处理器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HTTPPreprocessor.Builder.class) Closure<?> closure) {
            var builder = HTTPPreprocessor.builder();
            call(closure, builder);
            preprocessors.add(builder.build());
            return self;
        }

        // -----------------------JDBCPreprocessor----------------------------

        /**
         * 应用JDBC前置处理器
         *
         * @param preprocessor JDBC前置处理器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(JDBCPreprocessor preprocessor) {
            preprocessors.add(preprocessor);
            return self;
        }

        /**
         * 通过自定义器配置JDBC前置处理器
         *
         * @param customizer JDBC前置处理器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(Customizer<JDBCPreprocessor.Builder> customizer) {
            var builder = JDBCPreprocessor.builder();
            customizer.customize(builder);
            preprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置JDBC前置处理器
         *
         * @param builder JDBC前置处理器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(JDBCPreprocessor.Builder builder) {
            preprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置JDBC前置处理器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = JDBCPreprocessor.Builder.class) Closure<?> closure) {
            var builder = JDBCPreprocessor.builder();
            call(closure, builder);
            preprocessors.add(builder.build());
            return self;
        }

        // -----------------------RedisPreprocessor----------------------------

        /**
         * 应用Redis前置处理器
         *
         * @param preprocessor Redis前置处理器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(RedisPreprocessor preprocessor) {
            preprocessors.add(preprocessor);
            return self;
        }

        /**
         * 通过自定义器配置Redis前置处理器
         *
         * @param customizer Redis前置处理器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(Customizer<RedisPreprocessor.Builder> customizer) {
            var builder = RedisPreprocessor.builder();
            customizer.customize(builder);
            preprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置Redis前置处理器
         *
         * @param builder Redis前置处理器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(RedisPreprocessor.Builder builder) {
            preprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置Redis前置处理器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RedisPreprocessor.Builder.class) Closure<?> closure) {
            var builder = RedisPreprocessor.builder();
            call(closure, builder);
            preprocessors.add(builder.build());
            return self;
        }

        /**
         * 构建前置处理器列表
         *
         * @return 前置处理器列表
         */
        public List<Preprocessor> build() {
            return preprocessors;
        }
    }


    /**
     * 后置处理器构建器
     * <p>
     * 该构建器用于构建后置处理器列表，支持多种协议的后置处理器，如DebugPostprocessor、
     * HTTPPostprocessor、JDBCPostprocessor、RedisPostprocessor等，以及通用的SyncTimer。
     * 通过扩展ExtensiblePostprocessorsBuilder可以支持更多协议的后置处理器。
     * </p>
     *
     * @param <SELF>               构建类自身类型，用于支持链式调用
     * @param <EXTRACTORS_BUILDER> 提取器构建器类型
     */
    public static abstract class PostprocessorsBuilder<SELF extends PostprocessorsBuilder<SELF, EXTRACTORS_BUILDER>,
            EXTRACTORS_BUILDER extends ExtensibleExtractorsBuilder>
            implements IBuilder<List<Postprocessor>> {

        /**
         * 后置处理器列表
         * <p>使用LazyBuilder延迟初始化</p>
         */
        protected final List<Postprocessor> postprocessors = new LazyBuilder<>();

        /**
         * 构建器自身实例，用于支持链式调用
         */
        protected SELF self;

        /**
         * 默认构造函数
         * <p>初始化自身实例引用</p>
         */
        protected PostprocessorsBuilder() {
            self = (SELF) this;
        }


        /**
         * 应用自定义后置处理器
         *
         * @param builder 自定义后置处理器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF apply(AbstractProcessor.PostprocessorBuilder builder) {
            postprocessors.add((Postprocessor) builder.build());
            return self;
        }

        /**
         * 应用后置处理器
         *
         * @param postprocessor 后置处理器实例
         * @return 当前构建器实例，用于链式调用
         */
        public SELF apply(Postprocessor postprocessor) {
            postprocessors.add(postprocessor);
            return self;
        }

        /**
         * 通过类型和闭包应用自定义后置处理器
         *
         * @param type    后置处理器构建器类型
         * @param closure Groovy闭包
         * @param <T>     后置处理器构建器类型
         * @return 当前构建器实例，用于链式调用
         */
        public <T extends AbstractProcessor.PostprocessorBuilder> SELF apply(Class<T> type, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "T") Closure<T> closure) {
            var builder = Groovy.builder(type, closure);
            postprocessors.add((Postprocessor) builder.build());
            return self;
        }

        // -----------------------SyncTimer----------------------------

        /**
         * 应用同步定时器
         *
         * @param timer 同步定时器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF timer(SyncTimer timer) {
            postprocessors.add(timer);
            return self;
        }

        /**
         * 通过自定义器配置同步定时器
         *
         * @param customizer 同步定时器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF timer(Customizer<SyncTimer.Builder> customizer) {
            var builder = SyncTimer.builder();
            customizer.customize(builder);
            postprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置同步定时器
         *
         * @param builder 同步定时器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF timer(SyncTimer.Builder builder) {
            postprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置同步定时器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF timer(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = SyncTimer.Builder.class) Closure<?> closure) {
            var builder = SyncTimer.builder();
            call(closure, builder);
            postprocessors.add(builder.build());
            return self;
        }

        // -----------------------DebugPostprocessor----------------------------

        /**
         * 应用Debug后置处理器
         *
         * @param postprocessor Debug后置处理器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(DebugPostprocessor postprocessor) {
            postprocessors.add(postprocessor);
            return self;
        }

        /**
         * 通过自定义器配置Debug后置处理器
         *
         * @param customizer Debug后置处理器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(Customizer<DebugPostprocessor.Builder> customizer) {
            var builder = DebugPostprocessor.builder();
            customizer.customize(builder);
            postprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置Debug后置处理器
         *
         * @param builder Debug后置处理器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(DebugPostprocessor.Builder builder) {
            postprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置Debug后置处理器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DebugPostprocessor.Builder.class) Closure<?> closure) {
            var builder = DebugPostprocessor.builder();
            call(closure, builder);
            postprocessors.add(builder.build());
            return self;
        }

        // -----------------------HTTPPostprocessor----------------------------

        /**
         * 应用HTTP后置处理器
         *
         * @param postprocessor HTTP后置处理器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(HTTPPostprocessor postprocessor) {
            postprocessors.add(postprocessor);
            return self;
        }

        /**
         * 通过自定义器配置HTTP后置处理器
         *
         * @param customizer HTTP后置处理器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(Customizer<HTTPPostprocessor.Builder> customizer) {
            var builder = HTTPPostprocessor.builder();
            customizer.customize(builder);
            postprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置HTTP后置处理器
         *
         * @param builder HTTP后置处理器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(HTTPPostprocessor.Builder builder) {
            postprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置HTTP后置处理器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HTTPPostprocessor.Builder.class) Closure<?> closure) {
            var builder = HTTPPostprocessor.builder();
            call(closure, builder);
            postprocessors.add(builder.build());
            return self;
        }

        // -----------------------JDBCPostprocessor----------------------------

        /**
         * 应用JDBC后置处理器
         *
         * @param postprocessor JDBC后置处理器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(JDBCPostprocessor postprocessor) {
            postprocessors.add(postprocessor);
            return self;
        }

        /**
         * 通过自定义器配置JDBC后置处理器
         *
         * @param customizer JDBC后置处理器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(Customizer<JDBCPostprocessor.Builder> customizer) {
            var builder = JDBCPostprocessor.builder();
            customizer.customize(builder);
            postprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置JDBC后置处理器
         *
         * @param builder JDBC后置处理器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(JDBCPostprocessor.Builder builder) {
            postprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置JDBC后置处理器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = JDBCPostprocessor.Builder.class) Closure<?> closure) {
            var builder = JDBCPostprocessor.builder();
            call(closure, builder);
            postprocessors.add(builder.build());
            return self;
        }

        // -----------------------RedisPostprocessor----------------------------

        /**
         * 应用Redis后置处理器
         *
         * @param postprocessor Redis后置处理器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(RedisPostprocessor postprocessor) {
            postprocessors.add(postprocessor);
            return self;
        }

        /**
         * 通过自定义器配置Redis后置处理器
         *
         * @param customizer Redis后置处理器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(Customizer<RedisPostprocessor.Builder> customizer) {
            var builder = RedisPostprocessor.builder();
            customizer.customize(builder);
            postprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置Redis后置处理器
         *
         * @param builder Redis后置处理器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(RedisPostprocessor.Builder builder) {
            postprocessors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置Redis后置处理器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RedisPostprocessor.Builder.class) Closure<?> closure) {
            var builder = RedisPostprocessor.builder();
            call(closure, builder);
            postprocessors.add(builder.build());
            return self;
        }

        /**
         * 构建后置处理器列表
         *
         * @return 后置处理器列表
         */
        public List<Postprocessor> build() {
            return postprocessors;
        }
    }

    /**
     * 验证器构建器
     * <p>
     * 该构建器用于构建验证器列表，支持多种验证器，如JSONAssertion、ResultAssertion、
     * HTTPResponseAssertion等。通过扩展ExtensibleAssertionsBuilder可以支持更多协议的验证器。
     * </p>
     *
     * @param <SELF> 构建类自身类型，用于支持链式调用
     */
    public static abstract class AssertionsBuilder<SELF extends AssertionsBuilder<SELF>> implements IBuilder<List<Assertion>> {

        /**
         * 验证器列表
         * <p>使用LazyBuilder延迟初始化</p>
         */
        protected List<Assertion> assertions = new LazyBuilder<>();

        /**
         * 构建器自身实例，用于支持链式调用
         */
        protected SELF self;

        /**
         * 默认构造函数
         * <p>初始化自身实例引用</p>
         */
        public AssertionsBuilder() {
            self = (SELF) this;
        }

        /**
         * 应用自定义验证器
         *
         * @param builder 自定义验证器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF apply(AbstractAssertion.Builder builder) {
            assertions.add(builder.build());
            return self;
        }

        /**
         * 应用验证器
         *
         * @param extractor 验证器实例
         * @return 当前构建器实例，用于链式调用
         */
        public SELF apply(Assertion extractor) {
            assertions.add(extractor);
            return self;
        }

        /**
         * 通过类型和闭包应用自定义验证器
         *
         * @param type    验证器构建器类型
         * @param closure Groovy闭包
         * @param <T>     验证器构建器类型
         * @return 当前构建器实例，用于链式调用
         */
        public <T extends AbstractAssertion.Builder> SELF apply(Class<T> type, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "T") Closure<T> closure) {
            var builder = Groovy.builder(type, closure);
            assertions.add(builder.build());
            return self;
        }

        // -----------------------JSONAssertion----------------------------

        /**
         * 通过自定义器配置JSON验证器
         *
         * @param customizer JSON验证器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF json(Customizer<JSONAssertion.Builder> customizer) {
            var builder = JSONAssertion.builder();
            customizer.customize(builder);
            assertions.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置JSON验证器
         *
         * @param builder JSON验证器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF json(JSONAssertion.Builder builder) {
            assertions.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置JSON验证器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF json(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = JSONAssertion.Builder.class) Closure<?> closure) {
            var builder = JSONAssertion.builder();
            call(closure, builder);
            assertions.add(builder.build());
            return self;
        }

        /**
         * 配置JSON字段验证
         *
         * @param field    字段名
         * @param expected 期望值
         * @return 当前构建器实例，用于链式调用
         */
        public SELF json(String field, Object expected) {
            assertions.add(JSONAssertion.builder().field(field).expected(expected).build());
            return self;
        }

        /**
         * 配置JSON字段验证（带规则）
         *
         * @param rule     验证规则
         * @param field    字段名
         * @param expected 期望值
         * @return 当前构建器实例，用于链式调用
         */
        public SELF json(String rule, String field, Object expected) {
            assertions.add(JSONAssertion.builder().rule(rule).field(field).expected(expected).build());
            return self;
        }

        // -----------------------ResultAssertion----------------------------

        /**
         * 通过自定义器配置结果验证器
         *
         * @param customizer 结果验证器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF result(Customizer<ResultAssertion.Builder> customizer) {
            var builder = ResultAssertion.builder();
            customizer.customize(builder);
            assertions.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置结果验证器
         *
         * @param builder 结果验证器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF result(ResultAssertion.Builder builder) {
            assertions.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置结果验证器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF result(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ResultAssertion.Builder.class) Closure<?> closure) {
            var builder = ResultAssertion.builder();
            call(closure, builder);
            assertions.add(builder.build());
            return self;
        }

        /**
         * 配置结果验证
         *
         * @param rule     验证规则
         * @param expected 期望值
         * @return 当前构建器实例，用于链式调用
         */
        public SELF result(String rule, Object expected) {
            assertions.add(ResultAssertion.builder().rule(rule).expected(expected).build());
            return self;
        }

        // -----------------------HTTPResponseAssertion----------------------------

        /**
         * 通过自定义器配置HTTP响应验证器
         *
         * @param customizer HTTP响应验证器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(Customizer<HTTPResponseAssertion.Builder> customizer) {
            var builder = HTTPResponseAssertion.builder();
            customizer.customize(builder);
            assertions.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置HTTP响应验证器
         *
         * @param builder HTTP响应验证器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(HTTPResponseAssertion.Builder builder) {
            assertions.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置HTTP响应验证器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HTTPResponseAssertion.Builder.class) Closure<?> closure) {
            var builder = HTTPResponseAssertion.builder();
            call(closure, builder);
            assertions.add(builder.build());
            return self;
        }

        /**
         * 配置HTTP状态码验证
         *
         * @param expected 期望状态码
         * @return 当前构建器实例，用于链式调用
         */
        public SELF httpStatus(Object expected) {
            assertions.add(HTTPResponseAssertion.builder().rule("==").field("status").expected(expected).build());
            return self;
        }

        /**
         * 配置HTTP状态码验证（带规则）
         *
         * @param rule     验证规则
         * @param expected 期望状态码
         * @return 当前构建器实例，用于链式调用
         */
        public SELF httpStatus(String rule, Object expected) {
            assertions.add(HTTPResponseAssertion.builder().rule(rule).field("status").expected(expected).build());
            return self;
        }

        /**
         * 配置HTTP响应字段验证
         *
         * @param rule     验证规则
         * @param field    字段名
         * @param expected 期望值
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(String rule, String field, Object expected) {
            assertions.add(HTTPResponseAssertion.builder().rule(rule).field(field).expected(expected).build());
            return self;
        }

        /**
         * 构建验证器列表
         *
         * @return 验证器列表
         */
        public List<Assertion> build() {
            return assertions;
        }
    }

    /**
     * 提取器构建器
     * <p>
     * 该构建器用于构建提取器列表，支持多种提取器，如JSONExtractor、RegexExtractor、
     * ResultExtractor、HTTPHeaderExtractor等。通过扩展ExtensibleExtractorsBuilder可以支持更多协议的提取器。
     * </p>
     *
     * @param <SELF> 构建类自身类型，用于支持链式调用
     */
    public static abstract class ExtractorsBuilder<SELF extends ExtractorsBuilder<SELF>> implements IBuilder<List<Extractor>> {

        /**
         * 提取器列表
         * <p>使用LazyBuilder延迟初始化</p>
         */
        protected List<Extractor> extractors = new LazyBuilder<>();

        /**
         * 构建器自身实例，用于支持链式调用
         */
        protected SELF self;

        /**
         * 默认构造函数
         * <p>初始化自身实例引用</p>
         */
        public ExtractorsBuilder() {
            self = (SELF) this;
        }

        /**
         * 应用自定义提取器
         *
         * @param builder 自定义提取器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF apply(AbstractExtractor.Builder builder) {
            extractors.add(builder.build());
            return self;
        }

        /**
         * 应用提取器
         *
         * @param extractor 提取器实例
         * @return 当前构建器实例，用于链式调用
         */
        public SELF apply(Extractor extractor) {
            extractors.add(extractor);
            return self;
        }

        /**
         * 通过类型和闭包应用自定义提取器
         *
         * @param type    提取器构建器类型
         * @param closure Groovy闭包
         * @param <T>     提取器构建器类型
         * @return 当前构建器实例，用于链式调用
         */
        public <T extends AbstractExtractor.Builder> SELF apply(Class<T> type, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "T") Closure<T> closure) {
            var builder = Groovy.builder(type, closure);
            extractors.add(builder.build());
            return self;
        }

        // -----------------------JSONExtractor----------------------------

        /**
         * 通过自定义器配置JSON提取器
         *
         * @param customizer JSON提取器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF json(Customizer<JSONExtractor.Builder> customizer) {
            var builder = JSONExtractor.builder();
            customizer.customize(builder);
            extractors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置JSON提取器
         *
         * @param builder JSON提取器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF json(JSONExtractor.Builder builder) {
            extractors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置JSON提取器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF json(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = JSONExtractor.Builder.class) Closure<?> closure) {
            var builder = JSONExtractor.builder();
            call(closure, builder);
            extractors.add(builder.build());
            return self;
        }

        /**
         * 配置JSON字段提取
         *
         * @param refName 引用名称
         * @param field   字段名
         * @return 当前构建器实例，用于链式调用
         */
        public SELF json(String refName, String field) {
            extractors.add(JSONExtractor.builder().refName(refName).field(field).build());
            return self;
        }

        /**
         * 配置JSON字段提取（带默认值）
         *
         * @param refName      引用名称
         * @param field        字段名
         * @param defaultValue 默认值
         * @return 当前构建器实例，用于链式调用
         */
        public SELF json(String refName, String field, Object defaultValue) {
            extractors.add(JSONExtractor.builder().refName(refName).field(field).defaultValue(defaultValue).build());
            return self;
        }

        // -----------------------RegexExtractor----------------------------

        /**
         * 通过自定义器配置正则表达式提取器
         *
         * @param customizer 正则表达式提取器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF regex(Customizer<RegexExtractor.Builder> customizer) {
            var builder = RegexExtractor.builder();
            customizer.customize(builder);
            extractors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置正则表达式提取器
         *
         * @param builder 正则表达式提取器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF regex(RegexExtractor.Builder builder) {
            extractors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置正则表达式提取器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF regex(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RegexExtractor.Builder.class) Closure<?> closure) {
            var builder = RegexExtractor.builder();
            call(closure, builder);
            extractors.add(builder.build());
            return self;
        }

        /**
         * 配置正则表达式字段提取
         *
         * @param refName 引用名称
         * @param field   字段名
         * @return 当前构建器实例，用于链式调用
         */
        public SELF regex(String refName, String field) {
            extractors.add(RegexExtractor.builder().refName(refName).field(field).build());
            return self;
        }

        /**
         * 配置正则表达式字段提取（带匹配序号）
         *
         * @param refName  引用名称
         * @param field    字段名
         * @param matchNum 匹配序号
         * @return 当前构建器实例，用于链式调用
         */
        public SELF regex(String refName, String field, int matchNum) {
            extractors.add(RegexExtractor.builder().refName(refName).field(field).matchNum(matchNum).build());
            return self;
        }

        /**
         * 配置正则表达式字段提取（带默认值）
         *
         * @param refName      引用名称
         * @param field        字段名
         * @param defaultValue 默认值
         * @return 当前构建器实例，用于链式调用
         */
        public SELF regex(String refName, String field, Object defaultValue) {
            extractors.add(RegexExtractor.builder().refName(refName).field(field).defaultValue(defaultValue).build());
            return self;
        }

        /**
         * 配置正则表达式字段提取（带匹配序号和默认值）
         *
         * @param refName      引用名称
         * @param field        字段名
         * @param matchNum     匹配序号
         * @param defaultValue 默认值
         * @return 当前构建器实例，用于链式调用
         */
        public SELF regex(String refName, String field, int matchNum, Object defaultValue) {
            extractors.add(RegexExtractor.builder().refName(refName).field(field).matchNum(matchNum).defaultValue(defaultValue).build());
            return self;
        }

        // -----------------------ResultExtractor----------------------------

        /**
         * 通过自定义器配置结果提取器
         *
         * @param customizer 结果提取器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF result(Customizer<ResultExtractor.Builder> customizer) {
            var builder = ResultExtractor.builder();
            customizer.customize(builder);
            extractors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置结果提取器
         *
         * @param builder 结果提取器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF result(ResultExtractor.Builder builder) {
            extractors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置结果提取器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF result(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ResultExtractor.Builder.class) Closure<?> closure) {
            var builder = ResultExtractor.builder();
            call(closure, builder);
            extractors.add(builder.build());
            return self;
        }

        /**
         * 配置结果提取
         *
         * @param refName 引用名称
         * @return 当前构建器实例，用于链式调用
         */
        public SELF result(String refName) {
            extractors.add(ResultExtractor.builder().refName(refName).build());
            return self;
        }

        // -----------------------HTTPHeaderExtractor----------------------------

        /**
         * 通过自定义器配置HTTP头部提取器
         *
         * @param customizer HTTP头部提取器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF httpHeader(Customizer<HTTPHeaderExtractor.Builder> customizer) {
            var builder = HTTPHeaderExtractor.builder();
            customizer.customize(builder);
            extractors.add(builder.build());
            return self;
        }

        /**
         * 通过构建器配置HTTP头部提取器
         *
         * @param builder HTTP头部提取器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF httpHeader(HTTPHeaderExtractor.Builder builder) {
            extractors.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置HTTP头部提取器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF httpHeader(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HTTPHeaderExtractor.Builder.class) Closure<?> closure) {
            var builder = HTTPHeaderExtractor.builder();
            call(closure, builder);
            extractors.add(builder.build());
            return self;
        }

        /**
         * 配置HTTP头部字段提取
         *
         * @param refName 引用名称
         * @param field   字段名
         * @return 当前构建器实例，用于链式调用
         */
        public SELF httpHeader(String refName, String field) {
            extractors.add(HTTPHeaderExtractor.builder().refName(refName).field(field).build());
            return self;
        }

        /**
         * 配置HTTP头部字段提取（带默认值）
         *
         * @param refName      引用名称
         * @param field        字段名
         * @param defaultValue 默认值
         * @return 当前构建器实例，用于链式调用
         */
        public SELF httpHeader(String refName, String field, Object defaultValue) {
            extractors.add(HTTPHeaderExtractor.builder().refName(refName).field(field).defaultValue(defaultValue).build());
            return self;
        }

        /**
         * 配置HTTP头部字段提取（带匹配序号）
         *
         * @param refName  引用名称
         * @param field    字段名
         * @param matchNum 匹配序号
         * @return 当前构建器实例，用于链式调用
         */
        public SELF httpHeader(String refName, String field, int matchNum) {
            extractors.add(HTTPHeaderExtractor.builder().refName(refName).field(field).matchNum(matchNum).build());
            return self;
        }

        /**
         * 配置HTTP头部字段提取（带匹配序号和默认值）
         *
         * @param refName      引用名称
         * @param field        字段名
         * @param matchNum     匹配序号
         * @param defaultValue 默认值
         * @return 当前构建器实例，用于链式调用
         */
        public SELF httpHeader(String refName, String field, int matchNum, Object defaultValue) {
            extractors.add(HTTPHeaderExtractor.builder().refName(refName).field(field).matchNum(matchNum).defaultValue(defaultValue).build());
            return self;
        }

        /**
         * 构建提取器列表
         *
         * @return 提取器列表
         */
        public List<Extractor> build() {
            return extractors;
        }
    }

    /**
     * 容器或取样器构建器
     * <p>
     * 该构建器用于构建测试元素列表，支持多种测试元素，如TestSuite、DebugSampler、
     * HTTPSampler、JDBCSampler、RedisSampler等。通过扩展ExtensibleExtractorsBuilder可以支持更多协议的测试元素。
     * </p>
     *
     * @param <SELF> 构建类自身类型，用于支持链式调用
     */
    public static abstract class ChildrenBuilder<SELF extends ChildrenBuilder<SELF>> implements IBuilder<List<TestElement<?>>> {

        /**
         * 子测试元素列表
         * <p>使用LazyBuilder延迟初始化</p>
         */
        protected List<TestElement<? extends Result>> children = new LazyBuilder<>();

        /**
         * 构建器自身实例，用于支持链式调用
         */
        protected SELF self;

        /**
         * 默认构造函数
         * <p>初始化自身实例引用</p>
         */
        public ChildrenBuilder() {
            self = (SELF) this;
        }

        /**
         * 应用自定义容器或取样器
         *
         * @param builder 自定义容器或取样器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF apply(AbstractTestElementExecutable.Builder builder) {
            children.add(builder.build());
            return self;
        }

        /**
         * 应用容器或取样器
         *
         * @param child 容器或取样器实例
         * @return 当前构建器实例，用于链式调用
         */
        public SELF apply(AbstractTestElementExecutable child) {
            children.add(child);
            return self;
        }

        /**
         * 通过类型和闭包应用自定义容器或取样器
         *
         * @param type    容器或取样器构建器类型
         * @param closure Groovy闭包
         * @param <T>     容器或取样器构建器类型
         * @return 当前构建器实例，用于链式调用
         */
        public <T extends AbstractTestElementExecutable.Builder> SELF apply(Class<T> type, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "T") Closure<T> closure) {
            var builder = Groovy.builder(type, closure);
            children.add(builder.build());
            return self;
        }

        /**
         * 内置测试集合
         *
         * @return 当前构建器实例，用于链式调用
         */

        // -----------------------TestSuite----------------------------

        /**
         * 应用测试套件
         *
         * @param child 测试套件实例
         * @return 当前构建器实例，用于链式调用
         */
        public SELF suite(TestSuite child) {
            this.children.add(child);
            return self;
        }

        /**
         * 通过构建器应用测试套件
         *
         * @param child 测试套件构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF suite(TestSuite.Builder child) {
            this.children.add(child.build());
            return self;
        }

        /**
         * 通过自定义器配置测试套件
         *
         * @param customizer 测试套件自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF suite(Customizer<TestSuite.Builder> customizer) {
            var builder = TestSuite.builder();
            customizer.customize(builder);
            children.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置测试套件
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF suite(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = TestSuite.Builder.class) Closure<?> closure) {
            var builder = TestSuite.builder();
            call(closure, builder);
            this.children.add(builder.build());
            return self;
        }

        // -----------------------DebugSampler----------------------------

        /**
         * 应用Debug取样器
         *
         * @param child Debug取样器实例
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(DebugSampler child) {
            this.children.add(child);
            return self;
        }

        /**
         * 通过构建器应用Debug取样器
         *
         * @param child Debug取样器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(DebugSampler.Builder child) {
            this.children.add(child.build());
            return self;
        }

        /**
         * 通过自定义器配置Debug取样器
         *
         * @param customizer Debug取样器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(Customizer<DebugSampler.Builder> customizer) {
            var builder = DebugSampler.builder();
            customizer.customize(builder);
            children.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置Debug取样器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF debug(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = DebugSampler.Builder.class) Closure<?> closure) {
            var builder = DebugSampler.builder();
            call(closure, builder);
            this.children.add(builder.build());
            return self;
        }

        // -----------------------HTTPSampler----------------------------

        /**
         * 应用HTTP取样器
         *
         * @param child HTTP取样器实例
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(HTTPSampler child) {
            this.children.add(child);
            return self;
        }

        /**
         * 通过构建器应用HTTP取样器
         *
         * @param child HTTP取样器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(HTTPSampler.Builder child) {
            this.children.add(child.build());
            return self;
        }

        /**
         * 通过自定义器配置HTTP取样器
         *
         * @param customizer HTTP取样器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(Customizer<HTTPSampler.Builder> customizer) {
            var builder = HTTPSampler.builder();
            customizer.customize(builder);
            children.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置HTTP取样器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF http(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = HTTPSampler.Builder.class) Closure<?> closure) {
            var builder = HTTPSampler.builder();
            call(closure, builder);
            this.children.add(builder.build());
            return self;
        }

        // -----------------------JDBCSampler----------------------------

        /**
         * 应用JDBC取样器
         *
         * @param child JDBC取样器实例
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(JDBCSampler child) {
            this.children.add(child);
            return self;
        }

        /**
         * 通过构建器应用JDBC取样器
         *
         * @param child JDBC取样器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(JDBCSampler.Builder child) {
            this.children.add(child.build());
            return self;
        }

        /**
         * 通过自定义器配置JDBC取样器
         *
         * @param customizer JDBC取样器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(Customizer<JDBCSampler.Builder> customizer) {
            var builder = JDBCSampler.builder();
            customizer.customize(builder);
            children.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置JDBC取样器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF jdbc(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = JDBCSampler.Builder.class) Closure<?> closure) {
            var builder = JDBCSampler.builder();
            call(closure, builder);
            this.children.add(builder.build());
            return self;
        }

        // -----------------------RedisSampler----------------------------

        /**
         * 应用Redis取样器
         *
         * @param child Redis取样器实例
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(RedisSampler child) {
            this.children.add(child);
            return self;
        }

        /**
         * 通过构建器应用Redis取样器
         *
         * @param child Redis取样器构建器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(RedisSampler.Builder child) {
            this.children.add(child.build());
            return self;
        }

        /**
         * 通过自定义器配置Redis取样器
         *
         * @param customizer Redis取样器自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(Customizer<RedisSampler.Builder> customizer) {
            var builder = RedisSampler.builder();
            customizer.customize(builder);
            children.add(builder.build());
            return self;
        }

        /**
         * 通过闭包配置Redis取样器
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF redis(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RedisSampler.Builder.class) Closure<?> closure) {
            var builder = RedisSampler.builder();
            call(closure, builder);
            this.children.add(builder.build());
            return self;
        }

        /**
         * 构建子测试元素列表
         *
         * @return 子测试元素列表
         */
        @Override
        public List<TestElement<?>> build() {
            return children;
        }
    }
}