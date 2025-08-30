/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze.protocol.active.config;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import io.github.xiaomisum.ryze.protocol.active.ActiveConstantsInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * ActiveMQ默认配置元件，用于定义ActiveMQ连接的默认配置
 * <p>
 * 该类实现了ActiveMQ的默认配置功能，可以作为测试套件或测试用例的全局配置，
 * 为所有ActiveMQ相关的测试组件提供默认连接参数。它继承自AbstractConfigureElement，
 * 在测试执行前处理配置合并和变量设置。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>定义ActiveMQ连接的默认参数（Broker URL、用户名、密码等）</li>
 *   <li>支持配置引用名称，便于多个测试组件共享同一配置</li>
 *   <li>在测试上下文中注册配置，供其他组件使用</li>
 *   <li>支持与其他配置合并，实现配置的继承和覆盖</li>
 * </ul>
 * </p>
 * <p>
 * 配置处理流程：
 * <ol>
 *   <li>确定配置引用名称（使用默认名称或自定义名称）</li>
 *   <li>从上下文中获取已存在的同名配置</li>
 *   <li>将当前配置与已存在配置进行合并</li>
 *   <li>将合并后的配置存储到上下文中</li>
 * </ol>
 * </p>
 *
 * @author mi.xiao
 * @since 2021/4/10 20:38
 */
@KW({"active_mq", "activemq", "active"})
public class ActiveDefaults extends AbstractConfigureElement<ActiveDefaults, ActiveConfigureItem, TestSuiteResult> implements ActiveConstantsInterface {

    /**
     * 使用构建器构造ActiveDefaults实例
     *
     * @param builder 构建器实例
     */
    public ActiveDefaults(Builder builder) {
        super(builder);
    }

    /**
     * 默认构造函数
     */
    public ActiveDefaults() {
        super();
    }

    /**
     * 获取构建器实例
     *
     * @return ActiveDefaults.Builder 构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 处理配置逻辑
     * <p>
     * 主要完成以下操作：
     * <ol>
     *   <li>确定配置引用名称</li>
     *   <li>获取上下文中已存在的同名配置</li>
     *   <li>合并当前配置与已存在配置</li>
     *   <li>将合并后的配置存储到上下文中</li>
     * </ol>
     * </p>
     *
     * @param context 上下文包装器
     */
    @Override
    protected void doProcess(ContextWrapper context) {
        refName = StringUtils.isBlank(refName) ? DEF_REF_NAME_KEY : refName;
        var localConfig = runtime.getConfig();
        var otherRefName = StringUtils.isBlank(localConfig.getRef()) ? DEF_REF_NAME_KEY : localConfig.getRef();
        var config = (ActiveConfigureItem) context.getSessionRunner().getContext().getLocalVariablesWrapper().get(otherRefName);
        if (Objects.nonNull(config)) {
            runtime.setConfig(localConfig = localConfig.merge(config));
        }
        context.getSessionRunner().getContext().getLocalVariablesWrapper().put(refName, localConfig);
    }


    /**
     * 创建测试结果实例
     *
     * @return TestSuiteResult 测试结果实例
     */
    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult("Active MQ 默认配置");
    }

    /**
     * ActiveMQ默认配置 测试元件 构建类
     */
    /**
     * ActiveDefaults构建器类
     * <p>
     * 提供构建ActiveDefaults实例的方法，主要是配置项构建器。
     * </p>
     */
    public static class Builder extends AbstractConfigureElement.Builder<ActiveDefaults, Builder, ActiveConfigureItem, ActiveConfigureItem.Builder, TestSuiteResult> {

        /**
         * 构建ActiveDefaults实例
         *
         * @return ActiveDefaults实例
         */
        @Override
        public ActiveDefaults build() {
            return new ActiveDefaults(this);
        }

        /**
         * 获取配置项构建器
         *
         * @return ActiveConfigureItem.Builder 配置项构建器
         */
        @Override
        protected ActiveConfigureItem.Builder getConfigureItemBuilder() {
            return ActiveConfigureItem.builder();
        }
    }
}
