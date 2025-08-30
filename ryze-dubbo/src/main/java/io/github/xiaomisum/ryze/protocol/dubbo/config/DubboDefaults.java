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

package io.github.xiaomisum.ryze.protocol.dubbo.config;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import io.github.xiaomisum.ryze.protocol.dubbo.DubboConstantsInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Dubbo默认配置元件
 * <p>
 * 该类用于定义和管理Dubbo协议的默认配置信息，作为测试用例中Dubbo相关配置的基准配置。
 * 它继承自AbstractConfigureElement并实现了DubboConstantsInterface接口。
 * </p>
 * <p>
 * 主要功能包括：
 * 1. 提供Dubbo服务调用的默认配置参数
 * 2. 支持配置合并，将默认配置与具体测试用例配置进行合并
 * 3. 在测试执行前处理配置信息，将其存储到上下文中供后续使用
 * </p>
 * <p>
 * 在测试执行流程中，DubboDefaults会在测试套件执行初期处理配置信息，
 * 将默认配置存储在上下文变量中，供后续的DubboSampler、DubboPreprocessor和DubboPostprocessor使用。
 * </p>
 *
 * @author mi.xiao
 * @since 2021/4/10 20:38
 */
@KW(value = {"Dubbo_Default", "DubboDefault", "Dubbo_Defaults", "dubbo"})
public class DubboDefaults extends AbstractConfigureElement<DubboDefaults, DubboConfigureItem, TestSuiteResult> implements DubboConstantsInterface {

    /**
     * 带构建器的构造函数，使用指定的构建器创建Dubbo默认配置实例
     *
     * @param builder Dubbo默认配置构建器
     */
    public DubboDefaults(Builder builder) {
        super(builder);
    }

    /**
     * 默认构造函数，创建一个空的Dubbo默认配置实例
     */
    public DubboDefaults() {
        super();
    }

    /**
     * 创建Dubbo默认配置构建器实例
     *
     * @return Dubbo默认配置构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 处理Dubbo默认配置的核心方法
     * <p>
     * 该方法在测试执行前调用，主要完成以下任务：
     * 1. 设置默认引用名称，如果未指定则使用默认名称
     * 2. 获取当前运行时配置和上下文中的配置
     * 3. 合并本地配置和上下文配置
     * 4. 将合并后的配置存储到会话上下文的本地变量中
     * </p>
     *
     * @param context 测试上下文包装器，提供变量替换和上下文信息
     */
    @Override
    protected void doProcess(ContextWrapper context) {
        refName = StringUtils.isBlank(refName) ? DEF_REF_NAME_KEY : refName;
        var localConfig = runtime.getConfig();
        var otherRefName = StringUtils.isBlank(localConfig.getRef()) ? DEF_REF_NAME_KEY : localConfig.getRef();
        var config = (DubboConfigureItem) context.getSessionRunner().getContext().getLocalVariablesWrapper().get(otherRefName);
        if (Objects.nonNull(config)) {
            runtime.setConfig(localConfig = localConfig.merge(config));
        }
        context.getSessionRunner().getContext().getLocalVariablesWrapper().put(refName, localConfig);
    }

    /**
     * 获取测试结果对象，用于记录配置处理的结果信息
     *
     * @return TestSuiteResult测试结果对象
     */
    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult("Dubbo 默认配置");
    }

    /**
     * Dubbo默认配置测试元件构建类
     * <p>
     * 提供构建Dubbo默认配置实例的方法，继承自AbstractConfigureElement的构建器。
     * </p>
     */
    public static class Builder extends AbstractConfigureElement.Builder<DubboDefaults, Builder, DubboConfigureItem, DubboConfigureItem.Builder, TestSuiteResult> {

        /**
         * 构建Dubbo默认配置实例
         *
         * @return Dubbo默认配置实例
         */
        @Override
        public DubboDefaults build() {
            return new DubboDefaults(this);
        }

        /**
         * 获取Dubbo配置项构建器实例
         *
         * @return Dubbo配置项构建器
         */
        @Override
        protected DubboConfigureItem.Builder getConfigureItemBuilder() {
            return DubboConfigureItem.builder();
        }
    }
}