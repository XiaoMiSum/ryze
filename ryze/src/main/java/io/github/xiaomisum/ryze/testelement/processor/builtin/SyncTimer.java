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

package io.github.xiaomisum.ryze.testelement.processor.builtin;

import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Postprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import org.apache.commons.lang3.StringUtils;

/**
 * @author mi.xiao
 * @since 2021/4/7 19:49
 */
@KW({"SyncTimer", "Timer", "sync_timer", "def_timer", "defTimer"})
public class SyncTimer extends AbstractProcessor<SyncTimer, SyncTimer.TimerConfigureItem, DefaultSampleResult> implements Postprocessor {

    public SyncTimer(Builder builder) {
        super(builder);
    }

    public SyncTimer() {
        super();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.getId(),
                StringUtils.isBlank(runtime.getTitle()) ? "同步定时：" + config.timeout + "秒" : runtime.getTitle());
    }

    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        if (config.timeout > 0) {
            synchronized (this) {
                try {
                    this.wait(config.timeout * 1000L);
                } catch (InterruptedException e) {
                    this.notify();
                }
            }
        }
    }

    public static class TimerConfigureItem implements ConfigureItem<TimerConfigureItem> {

        private int timeout;

        public TimerConfigureItem() {
        }

        public TimerConfigureItem(Builder builder) {
            this.timeout = builder.timeout;
        }

        @Override
        public TimerConfigureItem merge(TimerConfigureItem other) {
            // 无需合并，返回当前对象的拷贝
            return copy();
        }

        @Override
        public TimerConfigureItem copy() {
            var res = new TimerConfigureItem();
            res.timeout = timeout;
            return res;
        }

        @Override
        public TimerConfigureItem evaluate(ContextWrapper context) {
            return this;
        }

        public static class Builder extends ConfigureBuilder<Builder, TimerConfigureItem> {

            protected int timeout;

            public Builder timeout(int timeout) {
                this.timeout = timeout;
                return self;
            }

            @Override
            public TimerConfigureItem build() {
                return new TimerConfigureItem(this);
            }
        }

    }

    /**
     * 定时器 构建器
     */
    public static class Builder extends PostprocessorBuilder<SyncTimer, Builder, TimerConfigureItem,
            TimerConfigureItem.Builder, DefaultExtractorsBuilder, DefaultSampleResult> {
        @Override
        public SyncTimer build() {
            return new SyncTimer(this);
        }

        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        @Override
        protected TimerConfigureItem.Builder getConfigureItemBuilder() {
            return new TimerConfigureItem.Builder();
        }
    }
}
