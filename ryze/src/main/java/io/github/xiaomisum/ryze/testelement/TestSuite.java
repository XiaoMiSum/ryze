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

import io.github.xiaomisum.ryze.builder.DefaultChildrenBuilder;
import io.github.xiaomisum.ryze.builder.DefaultConfigureElementsBuilder;
import io.github.xiaomisum.ryze.builder.DefaultPostprocessorsBuilder;
import io.github.xiaomisum.ryze.builder.DefaultPreprocessorsBuilder;
import io.github.xiaomisum.ryze.config.EmptyConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;

/**
 * 测试集合执行类
 * <p>
 * TestSuite是测试框架中的核心容器类，用于组织和执行一组相关的测试元素（如其他TestSuite或Sampler）。
 * 它继承自TestContainerExecutable，具备容器类的所有功能，可以包含子测试元素并按顺序执行它们。
 * </p>
 * <p>
 * TestSuite使用@KW("__testsuite__")注解标识，可以通过"__testsuite__"关键字在配置中引用该类。
 * </p>
 *
 * @author xiaomi
 */
@KW("__testsuite__")
public class TestSuite extends TestContainerExecutable<TestSuite, EmptyConfigureItem, TestSuiteResult> {

    /**
     * 基于构建器的构造函数
     *
     * @param builder 测试套件构建器实例
     */
    public TestSuite(Builder builder) {
        super(builder);
    }

    /**
     * 默认构造函数
     */
    public TestSuite() {
        super();
    }

    /**
     * 创建测试套件构建器
     * <p>
     * 提供静态方法创建测试套件构建器实例，用于构建TestSuite对象。
     * </p>
     *
     * @return 测试套件构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 获取测试结果对象
     * <p>
     * 创建并返回与当前测试套件关联的测试结果对象，用于记录测试执行过程中的各种信息。
     * </p>
     *
     * @return 测试套件结果对象
     */
    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult(runtime.id, runtime.title);
    }

    /**
     * 执行测试套件
     * <p>
     * 执行测试套件的核心逻辑，通过调用executeChildren方法执行所有子测试元素。
     * </p>
     *
     * @param context 测试上下文，提供执行环境和变量管理
     * @param result  测试结果对象，用于记录执行结果
     */
    @Override
    protected void execute(ContextWrapper context, TestSuiteResult result) {
        executeChildren(context);
    }

    /**
     * 测试套件构建器
     * <p>
     * 用于构建TestSuite对象的内部静态类，继承自TestContainerExecutable.Builder，
     * 提供了构建测试套件所需的各种配置方法。
     * </p>
     */
    public static class Builder extends TestContainerExecutable.Builder<TestSuite, Builder, EmptyConfigureItem, EmptyConfigureItem.Builder,
            DefaultConfigureElementsBuilder, DefaultPreprocessorsBuilder, DefaultPostprocessorsBuilder, DefaultChildrenBuilder, TestSuiteResult> {

        /**
         * 获取配置元素构建器
         *
         * @return 默认配置元素构建器实例
         */
        @Override
        protected DefaultConfigureElementsBuilder getConfiguresBuilder() {
            return DefaultConfigureElementsBuilder.builder();
        }

        /**
         * 获取子元素构建器
         *
         * @return 默认子元素构建器实例
         */
        @Override
        protected DefaultChildrenBuilder getChildrenBuilder() {
            return DefaultChildrenBuilder.builder();
        }

        /**
         * 获取前置处理器构建器
         *
         * @return 默认前置处理器构建器实例
         */
        @Override
        protected DefaultPreprocessorsBuilder getPreprocessorsBuilder() {
            return DefaultPreprocessorsBuilder.builder();
        }

        /**
         * 获取后置处理器构建器
         *
         * @return 默认后置处理器构建器实例
         */
        @Override
        protected DefaultPostprocessorsBuilder getPostprocessorsBuilder() {
            return DefaultPostprocessorsBuilder.builder();
        }

        /**
         * 构建测试套件对象
         *
         * @return 构建完成的测试套件对象
         */
        @Override
        public TestSuite build() {
            return new TestSuite(this);
        }

        /**
         * 获取配置项构建器
         *
         * @return 空配置项构建器实例
         */
        @Override
        protected EmptyConfigureItem.Builder getConfigureItemBuilder() {
            return EmptyConfigureItem.builder();
        }
    }
}