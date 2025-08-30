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

package io.github.xiaomisum.ryze.protocol.debug.config;

import com.alibaba.fastjson2.JSONObject;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;

/**
 * @author xiaomi
 * Created at 2025/7/19 20:17
 */
public class DebugConfigureItem extends JSONObject implements ConfigureItem<DebugConfigureItem> {

    public DebugConfigureItem() {
    }

    public DebugConfigureItem(DebugConfigureItem debugConfigureItem) {
        super(debugConfigureItem);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static DebugConfigureItem of(Object... keyWords) {
        DebugConfigureItem object = new DebugConfigureItem();
        for (int i = 0; i < keyWords.length; i += 2) {
            if (i + 1 > keyWords.length) {
                break;
            }
            object.put(keyWords[i].toString(), keyWords[i + 1]);
        }
        return object;
    }

    @Override
    public DebugConfigureItem merge(DebugConfigureItem other) {
        // 合并时，使用当前对象的属性覆盖other对象的属性，返回新对象
        var self = other.copy();
        self.putAll(this);
        return self;
    }

    @Override
    public DebugConfigureItem evaluate(ContextWrapper context) {
        this.replaceAll((key, value) -> context.evaluate(value));
        return this;
    }

    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, DebugConfigureItem> {

        private final DebugConfigureItem configure = new DebugConfigureItem();

        public Builder() {
        }

        public Builder add(String key, Object value) {
            configure.put(key, value);
            return self;
        }

        @Override
        public DebugConfigureItem build() {
            return configure;
        }
    }
}
