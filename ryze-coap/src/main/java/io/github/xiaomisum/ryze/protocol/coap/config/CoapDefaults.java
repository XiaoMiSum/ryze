/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze.protocol.coap.config;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.coap.CoapConstantsInterface;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * CoAP默认配置元件
 * <p>
 * 该类实现了CoAP配置和管理功能
 * </p>
 *
 * @author xiaomi
 */
@KW(value = {"coap_defaults", "coap"})
public class CoapDefaults extends AbstractConfigureElement<CoapDefaults, CoapConfigureItem, TestSuiteResult> implements CoapConstantsInterface {

    public CoapDefaults() {
    }

    public CoapDefaults(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected void doProcess(ContextWrapper context) {
        var localConfig = runtime.getConfig();
        var config = (CoapConfigureItem) context.getLocalVariablesWrapper().get(localConfig.getRef());
        if (Objects.nonNull(config)) {
            runtime.setConfig(localConfig = localConfig.merge(config));
        }
        context.getLocalVariablesWrapper().put(runtime.getRefName(DEF_REF_NAME_KEY), localConfig);
    }

    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult("CoAP 默认配置" + (StringUtils.isBlank(refName) ? "" : "：" + runtime.getRefName()));
    }

    /**
     * CoAP默认配置测试元件构建类
     */
    public static class Builder extends AbstractConfigureElement.Builder<CoapDefaults, Builder, CoapConfigureItem, CoapConfigureItem.Builder, TestSuiteResult> {

        @Override
        public CoapDefaults build() {
            return new CoapDefaults(this);
        }

        @Override
        protected CoapConfigureItem.Builder getConfigureItemBuilder() {
            return CoapConfigureItem.builder();
        }
    }
}
