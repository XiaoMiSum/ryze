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

package io.github.xiaomisum.ryze.protocol.mqtt;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.Result;
import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.protocol.mqtt.sampler.MqttPublishSampler;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.groovy.Groovy;

/**
 * MQTT协议魔法盒子类
 * <p>
 * 提供函数式编程方式执行MQTT取样器的便捷入口，支持多种配置方式：
 * 1. 通过Groovy闭包方式配置取样器
 * 2. 通过Customizer函数式接口配置取样器
 * </p>
 *
 * @author xiaomi
 */
public class Mqtt {

    // ==================== Publish ====================

    /**
     * 通过Groovy闭包执行MQTT发布取样器测试（无标题）
     */
    public static Result publish(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = MqttPublishSampler.Builder.class) Closure<?> closure) {
        return publish("", closure);
    }

    /**
     * 通过Groovy闭包执行MQTT发布取样器测试（带标题）
     */
    public static Result publish(String title,
                                 @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = MqttPublishSampler.Builder.class) Closure<?> closure) {
        var builder = MqttPublishSampler.builder();
        Groovy.call(closure, builder);
        return Ryze.runTest(title, builder.build());
    }

    /**
     * 通过Customizer函数式接口执行MQTT发布取样器测试（无标题）
     */
    public static Result publish(Customizer<MqttPublishSampler.Builder> customizer) {
        return publish("", customizer);
    }

    /**
     * 通过Customizer函数式接口执行MQTT发布取样器测试（带标题）
     */
    public static Result publish(String title, Customizer<MqttPublishSampler.Builder> customizer) {
        var builder = MqttPublishSampler.builder();
        customizer.customize(builder);
        return Ryze.runTest(title, builder.build());
    }
}
