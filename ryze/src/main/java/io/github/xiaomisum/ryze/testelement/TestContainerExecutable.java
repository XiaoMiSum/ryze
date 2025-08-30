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

package io.github.xiaomisum.ryze.testelement;

import com.alibaba.fastjson2.annotation.JSONField;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.TestStatus;
import io.github.xiaomisum.ryze.builder.ExtensibleChildrenBuilder;
import io.github.xiaomisum.ryze.builder.ExtensibleConfigureElementsBuilder;
import io.github.xiaomisum.ryze.builder.ExtensiblePostprocessorsBuilder;
import io.github.xiaomisum.ryze.builder.ExtensiblePreprocessorsBuilder;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.sampler.Sampler;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.ValidateResult;
import io.github.xiaomisum.ryze.support.groovy.Groovy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 测试容器抽象类
 * <p>
 * 负责调度子元件执行，是所有测试容器类的基类。该类继承自AbstractTestElementExecutable，
 * 提供了容器类测试组件的核心功能，包括子元素管理、执行调度、结果收集等。
 * </p>
 * <p>
 * 测试容器可以包含多个子测试元素（如其他容器或取样器），并负责按顺序执行这些子元素，
 * 收集和汇总执行结果。容器在执行过程中会处理前置处理器、后置处理器和拦截器等组件，
 * 确保测试执行流程的完整性和一致性。
 * </p>
 *
 * @param <SELF>   测试容器自身类型，用于支持链式调用和类型安全的返回值
 * @param <CONFIG> 测试容器配置项类型，必须是ConfigureItem的子类
 * @param <R>      测试结果类型，必须是Result的子类
 * @author xiaomi
 */
@SuppressWarnings("all")
public abstract class TestContainerExecutable<SELF extends TestContainerExecutable<SELF, CONFIG, R>,
        CONFIG extends ConfigureItem<CONFIG>, R extends Result>
        extends AbstractTestElementExecutable<SELF, CONFIG, R> {

    /**
     * 子测试元素列表
     * <p>存储容器包含的所有子测试元素，在JSON序列化中对应"children"字段，序列化顺序为10</p>
     */
    @JSONField(name = CHILDREN, ordinal = 10)
    protected List<TestElement<R>> children;

    /**
     * 默认构造函数
     */
    public TestContainerExecutable() {
    }

    /**
     * 基于构建器的构造函数
     *
     * @param builder 测试容器构建器实例
     */
    public TestContainerExecutable(Builder builder) {
        super(builder);
        this.children = builder.children;
    }

    /**
     * 执行子测试元素
     * <p>
     * 该方法是测试容器的核心执行逻辑，负责按顺序执行所有子测试元素，并处理前置处理器、
     * 后置处理器和拦截器等组件。如果某个子元素是取样器且执行失败，则会中断后续执行。
     * </p>
     *
     * @param context 上下文包装器，提供执行环境和变量管理
     */
    protected void executeChildren(ContextWrapper context) {
        if (children == null) {
            return;
        }

        try {
            // 执行前置处理
            if (!chain.applyPreHandle(context, runtime)) {
                return;
            }
            // 业务处理
            for (TestElement<R> child : children) {
                if (Objects.isNull(child)) {
                    continue;
                }
                R result = context.getSessionRunner().runTest(child);
                if (context.getTestResult() instanceof TestSuiteResult suiteResult) {
                    suiteResult.addChild(result);
                }
                if (child instanceof Sampler<R> && !result.getStatus().isPassed()) {
                    // 如果子元件是取样器，并且执行失败，则后续步骤无需执行
                    break;
                }
            }
            var hasFailedChildren = !((TestSuiteResult) context.getTestResult()).getChildren().stream()
                    .filter(result -> result.getStatus().isBroken() || result.getStatus().isFailed())
                    .toList().isEmpty();
            if (hasFailedChildren) {
                context.getTestResult().setStatus(TestStatus.failed);
            }
            chain.applyPostHandle(context, runtime);
        } catch (Throwable throwable) {
            context.getTestResult().setStatus(throwable instanceof AssertionError ? TestStatus.failed : TestStatus.broken);
            if (SessionRunner.getSession().isRunInTestFrameworkSupport()) {
                throw throwable;
            }
        } finally {
            // 最终处理
            chain.triggerAfterCompletion(context);
        }

    }

    /**
     * 验证测试容器
     * <p>
     * 验证测试容器及其所有子元素的数据有效性，确保测试可以正常执行。
     * </p>
     *
     * @return 验证结果
     */
    @Override
    public ValidateResult validate() {
        ValidateResult result = super.validate();
        if (children == null) {
            result.append("\n容器类测试元件 %s 字段值缺失或为空，当前值：%s", CHILDREN, toString());
            return result;
        }
        for (TestElement<?> child : children) {
            result.append(child);
        }
        return result;
    }

    /**
     * 复制测试容器
     * <p>
     * 创建当前测试容器的深拷贝副本，包括所有子元素的复制。
     * </p>
     *
     * @return 复制的测试容器实例
     */
    @Override
    public SELF copy() {
        SELF self = super.copy();
        if (children == null) {
            return self;
        }
        self.children = new ArrayList<>();
        for (TestElement<R> step : children) {
            self.children.add(step.copy());
        }
        return self;
    }

    /**
     * 获取子测试元素列表
     *
     * @return 子测试元素列表
     */
    public List<TestElement<R>> getChildren() {
        return children;
    }

    /**
     * 设置子测试元素列表
     *
     * @param children 子测试元素列表
     */
    public void setChildren(List<TestElement<R>> children) {
        this.children = children;
    }

    /**
     * 容器基础构建器
     * <p>
     * 该构建器为测试容器提供构建支持，继承自AbstractTestElementExecutable.Builder，
     * 支持配置项、前置处理器、后置处理器等组件的配置，同时扩展了子元素配置功能。
     * </p>
     *
     * @param <ELE>                   容器类型，必须是TestContainerExecutable的子类
     * @param <SELF>                  自己的类型，用于支持链式调用
     * @param <CONFIG>                容器配置类型，必须是ConfigureItem的子类
     * @param <CONFIGURE_BUILDER>     容器配置类型构建器，必须是ConfigureBuilder的子类
     * @param <CONFIGURES_BUILDER>    配置元素构建器，必须是ExtensibleConfigureElementsBuilder的子类
     * @param <PREPROCESSORS_BUILDER> 前置处理器构建器，必须是ExtensiblePreprocessorsBuilder的子类
     * @param <POSTPROCESSORS_BUILDER>后置处理器构建器，必须是ExtensiblePostprocessorsBuilder的子类
     * @param <CHILDREN_BUILDER>      子元素构建器，必须是ExtensibleChildrenBuilder的子类
     * @param <R>                     处理结果类型，必须是Result的子类
     */
    public static abstract class Builder<ELE extends TestContainerExecutable<ELE, CONFIG, R>,
            SELF extends Builder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, CONFIGURES_BUILDER, PREPROCESSORS_BUILDER, POSTPROCESSORS_BUILDER, CHILDREN_BUILDER, R>,
            CONFIG extends ConfigureItem<CONFIG>,
            CONFIGURE_BUILDER extends ConfigureBuilder<?, CONFIG>,
            CONFIGURES_BUILDER extends ExtensibleConfigureElementsBuilder,
            PREPROCESSORS_BUILDER extends ExtensiblePreprocessorsBuilder,
            POSTPROCESSORS_BUILDER extends ExtensiblePostprocessorsBuilder,
            CHILDREN_BUILDER extends ExtensibleChildrenBuilder,
            R extends Result>
            extends AbstractTestElementExecutable.Builder<ELE, SELF, CONFIG, CONFIGURE_BUILDER, CONFIGURES_BUILDER, PREPROCESSORS_BUILDER, POSTPROCESSORS_BUILDER, R> {

        /**
         * 子测试元素列表
         */
        protected List<TestElement<?>> children;

        /**
         * 设置子节点列表
         *
         * @param children 子节点列表
         * @return 当前构建器实例，用于链式调用
         */
        public SELF children(List<TestElement<?>> children) {
            this.children = Collections.addAllIfNonNull(this.children, children);
            return self;
        }


        /**
         * 通过类型和自定义器配置子节点
         *
         * @param type       子节点构建器类型
         * @param customizer 子节点自定义器
         * @param <T>        子节点构建器类型
         * @return 当前构建器实例，用于链式调用
         */
        public <T extends ExtensibleChildrenBuilder> SELF children(Class<T> type, Customizer<T> customizer) {
            try {
                T builder = type.getConstructor().newInstance();
                customizer.customize(builder);
                this.children = Collections.addAllIfNonNull(this.children, builder.build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return self;
        }

        /**
         * 通过自定义器配置子节点
         *
         * @param customizer 子节点自定义器
         * @return 当前构建器实例，用于链式调用
         */
        public SELF children(Customizer<CHILDREN_BUILDER> customizer) {
            CHILDREN_BUILDER builder = getChildrenBuilder();
            customizer.customize(builder);
            this.children = Collections.addAllIfNonNull(this.children, builder.build());
            return self;
        }

        /**
         * 通过类型和闭包配置子节点
         *
         * @param type    子节点构建器类型
         * @param closure Groovy闭包
         * @param <T>     子节点构建器类型
         * @return 当前构建器实例，用于链式调用
         */
        public <T extends ExtensibleChildrenBuilder> SELF children(Class<T> type, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "T") Closure<T> closure) {
            var builder = Groovy.builder(type, closure);
            this.children = Collections.addAllIfNonNull(this.children, builder.build());
            return self;
        }

        /**
         * 通过闭包配置子节点
         *
         * @param closure Groovy闭包
         * @return 当前构建器实例，用于链式调用
         */
        public SELF children(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, type = "CHILDREN_BUILDER") Closure<?> closure) {
            CHILDREN_BUILDER builder = getChildrenBuilder();
            Groovy.call(closure, builder);
            this.children = Collections.addAllIfNonNull(this.children, builder.build());
            return self;
        }

        /**
         * 获取配置元素构建器
         * <p>抽象方法，由具体子类实现</p>
         *
         * @return 配置元素构建器实例
         */
        protected abstract CONFIGURES_BUILDER getConfiguresBuilder();

        /**
         * 获取子元素构建器
         * <p>抽象方法，由具体子类实现</p>
         *
         * @return 子元素构建器实例
         */
        protected abstract CHILDREN_BUILDER getChildrenBuilder();

    }
}