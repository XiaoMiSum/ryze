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

package io.github.xiaomisum.ryze.protocol.mongo.config;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import io.github.xiaomisum.ryze.protocol.mongo.MongoConstantsInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * MongoDB 默认配置元件类
 * <p>
 * 该类用于定义 MongoDB 操作的默认配置，继承自 AbstractConfigureElement。
 * 它可以在测试套件中提供全局的 MongoDB 配置，其他 MongoDB 元件可以引用这些默认配置。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>提供 MongoDB 操作的默认配置</li>
 *   <li>支持配置项的合并和引用</li>
 *   <li>在测试上下文中存储和管理 MongoDB 配置</li>
 *   <li>使用 @KW 注解支持多种关键字识别</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@KW({"mongo_defaults", "mongo"})
public class MongoDefaults extends AbstractConfigureElement<MongoDefaults, MongoConfigItem, TestSuiteResult> implements MongoConstantsInterface {

    /**
     * 使用 Builder 构造方法创建 MongoDefaults 实例
     *
     * @param builder Builder 实例
     */
    public MongoDefaults(Builder builder) {
        super(builder);
    }

    /**
     * 无参构造方法
     */
    public MongoDefaults() {
    }


    /**
     * 创建 Builder 实例
     * <p>
     * 静态工厂方法，用于创建 MongoDefaults.Builder 实例。
     * </p>
     *
     * @return MongoDefaults.Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 处理配置元素
     * <p>
     * 在测试执行过程中处理 MongoDB 默认配置，包括设置引用名称、
     * 合并配置项以及在上下文中存储配置。
     * </p>
     *
     * @param context 上下文包装器
     */
    @Override
    protected void doProcess(ContextWrapper context) {
        refName = StringUtils.isBlank(refName) ? DEF_REF_NAME_KEY : refName;
        var localConfig = runtime.getConfig();
        var otherRefName = StringUtils.isBlank(localConfig.ref) ? DEF_REF_NAME_KEY : localConfig.ref;
        var config = (MongoConfigItem) context.getSessionRunner().getContext().getLocalVariablesWrapper().get(otherRefName);
        if (Objects.nonNull(config)) {
            runtime.setConfig(localConfig = localConfig.merge(config));
        }
        context.getSessionRunner().getContext().getLocalVariablesWrapper().put(refName, localConfig);
    }

    /**
     * 获取测试结果
     * <p>
     * 创建并返回 MongoDB 默认配置的测试结果实例。
     * </p>
     *
     * @return TestSuiteResult 实例
     */
    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult("MONGO 默认配置");
    }

    /**
     * MongoDB 默认配置测试元件构建器类
     * <p>
     * 该类用于通过 Builder 模式构建 MongoDefaults 实例，继承自 AbstractConfigureElement.Builder。
     * </p>
     */
    public static class Builder extends AbstractConfigureElement.Builder<MongoDefaults, Builder, MongoConfigItem, MongoConfigItem.Builder, TestSuiteResult> {

        /**
         * 构建 MongoDefaults 实例
         *
         * @return MongoDefaults 实例
         */
        @Override
        public MongoDefaults build() {
            return new MongoDefaults(this);
        }

        /**
         * 获取配置项构建器
         *
         * @return MongoConfigItem.Builder 实例
         */
        @Override
        protected MongoConfigItem.Builder getConfigureItemBuilder() {
            return MongoConfigItem.builder();
        }
    }
}