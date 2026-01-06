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

package io.github.xiaomisum.ryze.protocol.email.processor;

import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.email.EMailConstantsInterface;
import io.github.xiaomisum.ryze.protocol.email.EmailSender;
import io.github.xiaomisum.ryze.protocol.email.RealEMailRequest;
import io.github.xiaomisum.ryze.protocol.email.config.EMailConfigureItem;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Postprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author xiaomi
 */
@KW(value = {"email_postprocessor", "email_post_processor", "mail_postprocessor", "mail_post_processor", "email", "mail"})
public class EMailPostProcessor extends AbstractProcessor<EMailPostProcessor, EMailConfigureItem, DefaultSampleResult> implements Postprocessor, EMailConstantsInterface {

    public EMailPostProcessor(Builder builder) {
        super(builder);
    }

    public EMailPostProcessor() {
        super();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.getId(), StringUtils.isBlank(runtime.getTitle()) ? "EMAIL 后置处理器" : runtime.getTitle());
    }

    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        EmailSender.send(runtime.getConfig(), result);
    }

    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        var localConfig = Objects.isNull(runtime.getConfig()) ? new EMailConfigureItem() : runtime.getConfig();
        var otherConfig = (EMailConfigureItem) context.getLocalVariablesWrapper().get(DEF_REF_NAME_KEY);
        runtime.setConfig(localConfig.merge(otherConfig));
        result.setRequest(new RealEMailRequest(runtime.getConfig()));
    }

    /**
     * 处理响应阶段，封装响应结果
     *
     * @param context 测试上下文对象
     * @param result  测试结果对象
     */
    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setResponse(SampleResult.DefaultRealResponse.build(new byte[0]));
    }

    /**
     * EMAIL 后置处理器构建器
     */
    public static class Builder extends PostprocessorBuilder<EMailPostProcessor, EMailPostProcessor.Builder, EMailConfigureItem,
            EMailConfigureItem.Builder, DefaultExtractorsBuilder, DefaultSampleResult> {
        @Override
        public EMailPostProcessor build() {
            return new EMailPostProcessor(this);
        }

        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        @Override
        protected EMailConfigureItem.Builder getConfigureItemBuilder() {
            return EMailConfigureItem.builder();
        }
    }
}
