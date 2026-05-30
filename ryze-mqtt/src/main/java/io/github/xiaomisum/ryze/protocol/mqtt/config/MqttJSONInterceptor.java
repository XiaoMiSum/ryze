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

package io.github.xiaomisum.ryze.protocol.mqtt.config;

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.protocol.mqtt.MqttConstantsInterface;
import io.github.xiaomisum.ryze.protocol.mqtt.processor.MqttPostprocessor;
import io.github.xiaomisum.ryze.protocol.mqtt.processor.MqttPreprocessor;
import io.github.xiaomisum.ryze.protocol.mqtt.sampler.MqttPublishSampler;
import io.github.xiaomisum.ryze.support.fastjson.interceptor.JSONInterceptor;

import java.util.List;
import java.util.Map;

/**
 * MQTT协议JSON拦截器
 * <p>
 * 该类用于处理MQTT协议相关测试元件的JSON反序列化过程。
 * 主要功能是将JSON配置转换为对应的MQTT配置项对象。
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MqttJSONInterceptor implements JSONInterceptor, MqttConstantsInterface {

    /**
     * 获取支持的类列表
     *
     * @return 支持的类列表
     */
    @Override
    public List<Class<?>> getSupportedClasses() {
        return List.of(MqttPublishSampler.class, MqttDatasource.class,
                MqttPreprocessor.class, MqttPostprocessor.class);
    }

    /**
     * 反序列化配置项
     *
     * @param value JSON对象
     * @return MQTT配置项对象
     */
    @Override
    public ConfigureItem<?> deserializeConfigureItem(Object value) {
        if (value instanceof Map configure) {
            var rawData = JSON.toJSONString(configure);
            return JSON.parseObject(rawData, MqttConfigureItem.class);
        }
        return null;
    }
}
