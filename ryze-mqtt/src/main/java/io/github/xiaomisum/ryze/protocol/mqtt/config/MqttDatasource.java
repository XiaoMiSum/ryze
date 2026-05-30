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

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.mqtt.MqttConstantsInterface;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * MQTT 数据源配置元件
 * <p>
 * 该类实现了MQTT连接配置和管理功能，使用HiveMQ客户端建立连接并注册到上下文。
 * </p>
 *
 * @author xiaomi
 */
@KW(value = {"mqtt_defaults", "mqtt_datasource", "mqtt"})
public class MqttDatasource extends AbstractConfigureElement<MqttDatasource, MqttConfigureItem, TestSuiteResult> implements MqttConstantsInterface {

    public MqttDatasource() {
    }

    public MqttDatasource(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * MQTT数据源配置元件处理
     * 1. 合并已有配置
     * 2. 使用HiveMQ客户端建立MQTT连接
     * 3. 将连接注册到测试上下文
     *
     * @param context 测试上下文
     */
    @Override
    protected void doProcess(ContextWrapper context) {
        var localConfig = runtime.getConfig();
        var config = (MqttConfigureItem) context.getLocalVariablesWrapper().get(localConfig.getDatasource(DEF_REF_NAME_KEY));
        if (Objects.nonNull(config)) {
            runtime.setConfig(localConfig = localConfig.merge(config));
        }
        context.getLocalVariablesWrapper().put(runtime.getRefName(DEF_REF_NAME_KEY), localConfig);
    }

    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult("MQTT 默认配置" + (StringUtils.isBlank(refName) ? "" : "：" + runtime.getRefName()));
    }

    /**
     * MQTT 数据源配置 测试元件 构建类
     */
    public static class Builder extends AbstractConfigureElement.Builder<MqttDatasource, Builder, MqttConfigureItem, MqttConfigureItem.Builder, TestSuiteResult> {

        @Override
        public MqttDatasource build() {
            return new MqttDatasource(this);
        }

        @Override
        protected MqttConfigureItem.Builder getConfigureItemBuilder() {
            return MqttConfigureItem.builder();
        }
    }
}
