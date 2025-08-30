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

package io.github.xiaomisum.ryze.protocol.http.config;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import io.github.xiaomisum.ryze.protocol.http.HTTPConstantsInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * HTTP 默认配置元件
 * <p>
 * 该类实现了HTTP配置和管理功能
 * </p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>设置HTTP默认配置</li>
 *   <li>注册默认配置到测试上下文</li>
 *   <li>验证配置的有效性</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@KW(value = {"http_defaults", "http"})
public class HTTPDefaults extends AbstractConfigureElement<HTTPDefaults, HTTPConfigureItem, TestSuiteResult> implements HTTPConstantsInterface {

    public HTTPDefaults() {
    }

    public HTTPDefaults(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * HTTP默认配置元件处理
     * 1. 如果存在其他与当前同类型的配置，或者当前有通过 datasource 指定数据源，则将其他配置元件的配置项合并到当前配置元件中
     * 2. 如果有指定 ref_name 则使用 ref_name 作为key 将当前配置存入 localVariables 中，否则以默认 DEF_REF_NAME_KEY 作为key
     *
     * @param context 测试上下文
     */
    @Override
    protected void doProcess(ContextWrapper context) {
        refName = StringUtils.isBlank(refName) ? DEF_REF_NAME_KEY : refName;
        var localConfig = runtime.getConfig();
        var otherRefName = StringUtils.isBlank(localConfig.ref) ? DEF_REF_NAME_KEY : localConfig.ref;
        var config = (HTTPConfigureItem) context.getSessionRunner().getContext().getLocalVariablesWrapper().get(otherRefName);
        if (Objects.nonNull(config)) {
            runtime.setConfig(localConfig = localConfig.merge(config));
        }
        context.getSessionRunner().getContext().getLocalVariablesWrapper().put(refName, localConfig);
    }

    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult("HTTP 默认配置");
    }

    /**
     * HTTP默认配置 测试元件 构建类
     */
    public static class Builder extends AbstractConfigureElement.Builder<HTTPDefaults, Builder, HTTPConfigureItem, HTTPConfigureItem.Builder, TestSuiteResult> {

        @Override
        public HTTPDefaults build() {
            return new HTTPDefaults(this);
        }

        @Override
        protected HTTPConfigureItem.Builder getConfigureItemBuilder() {
            return HTTPConfigureItem.builder();
        }
    }
}