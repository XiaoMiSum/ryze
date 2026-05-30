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

package io.github.xiaomisum.ryze.protocol.mqtt.builder;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.builder.ExtensiblePostprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.mqtt.processor.MqttPostprocessor;
import io.github.xiaomisum.ryze.support.Customizer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * MQTT自定义后置处理器列表构建器
 *
 * @author xiaomi
 */
public class MqttPostprocessorsBuilder extends ExtensiblePostprocessorsBuilder<MqttPostprocessorsBuilder, DefaultExtractorsBuilder> {

    public static MqttPostprocessorsBuilder builder() {
        return new MqttPostprocessorsBuilder();
    }

    public MqttPostprocessorsBuilder mqtt(MqttPostprocessor postprocessor) {
        postprocessors.add(postprocessor);
        return self;
    }

    public MqttPostprocessorsBuilder mqtt(Customizer<MqttPostprocessor.Builder> customizer) {
        var builder = MqttPostprocessor.builder();
        customizer.customize(builder);
        postprocessors.add(builder.build());
        return self;
    }

    public MqttPostprocessorsBuilder mqtt(MqttPostprocessor.Builder builder) {
        postprocessors.add(builder.build());
        return self;
    }

    public MqttPostprocessorsBuilder mqtt(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = MqttPostprocessor.Builder.class) Closure<?> closure) {
        var builder = MqttPostprocessor.builder();
        call(closure, builder);
        postprocessors.add(builder.build());
        return self;
    }
}
