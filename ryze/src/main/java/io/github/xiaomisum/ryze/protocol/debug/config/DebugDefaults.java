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

package io.github.xiaomisum.ryze.protocol.debug.config;

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xiaomi
 */
@KW(value = {"debugconfig", "debug_config", "debug"})
public class DebugDefaults extends AbstractConfigureElement<DebugDefaults, DebugConfigureItem, TestSuiteResult> {

    public static final String DEF_REF_NAME_KEY = "__debug_configure_element_default_ref_name__";

    public DebugDefaults(Builder builder) {
        super(builder);
    }

    public DebugDefaults() {
        super();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected void doProcess(ContextWrapper context) {
        refName = StringUtils.isBlank(refName) ? DEF_REF_NAME_KEY : refName;
        context.getSessionRunner().getContext().getLocalVariablesWrapper().put(refName, config);
    }

    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult("Debug 默认配置");
    }

    public static class Builder extends AbstractConfigureElement.Builder<DebugDefaults, Builder, DebugConfigureItem, DebugConfigureItem.Builder, TestSuiteResult> {

        @Override
        public DebugDefaults build() {
            return new DebugDefaults(this);
        }

        @Override
        protected DebugConfigureItem.Builder getConfigureItemBuilder() {
            return DebugConfigureItem.builder();
        }
    }
}