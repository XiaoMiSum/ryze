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

package io.github.xiaomisum.ryze.protocol.debug.processer;

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Preprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.protocol.debug.config.DebugConfigureItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiaomi
 */
@KW(value = {"debug_preprocessor", "debug_pre_processor", "debug"})
@SuppressWarnings({"unchecked", "rawtypes"})
public class DebugPreprocessor extends AbstractProcessor<DebugPreprocessor, DebugConfigureItem, DefaultSampleResult> implements Preprocessor {

    static Logger logger = LoggerFactory.getLogger(DebugPreprocessor.class);

    public DebugPreprocessor(Builder builder) {
        super(builder);
    }

    public DebugPreprocessor() {
        super();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.getId(),
                StringUtils.isBlank(runtime.getTitle()) ? "Debug Preprocessor" : runtime.getTitle());
    }

    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        try {
            result.sampleStart();
            byte[] bytes = JSON.toJSONBytes(runtime.getConfig());
            result.setRequest(SampleResult.DefaultReal.build(bytes));
            result.setResponse(SampleResult.DefaultReal.build(bytes));
            logger.info("Debug Preprocessor");
        } finally {
            result.sampleEnd();
        }
    }

    public static class Builder extends PreprocessorBuilder<DebugPreprocessor, Builder, DebugConfigureItem,
            DebugConfigureItem.Builder, DefaultExtractorsBuilder, DefaultSampleResult> {
        @Override
        public DebugPreprocessor build() {
            return new DebugPreprocessor(this);
        }

        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        @Override
        protected DebugConfigureItem.Builder getConfigureItemBuilder() {
            return DebugConfigureItem.builder();
        }
    }
}
