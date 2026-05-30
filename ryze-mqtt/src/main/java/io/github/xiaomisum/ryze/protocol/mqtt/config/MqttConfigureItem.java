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

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.mqtt.MqttConstantsInterface;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import org.apache.commons.lang3.StringUtils;

/**
 * MQTT协议配置项类
 * <p>
 * 该类封装了MQTT协议的所有配置参数，包括数据源引用、主题、QoS等级、消息负载、响应超时等。
 * 实现了ConfigureItem接口，支持配置项的合并和变量求值。
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MqttConfigureItem implements ConfigureItem<MqttConfigureItem>, MqttConstantsInterface {

    /**
     * 数据源引用名称
     */
    @JSONField(name = DATASOURCE)
    protected String datasource;

    /**
     * Broker地址
     */
    @JSONField(name = BROKER, ordinal = 1)
    protected String broker;

    /**
     * 端口
     */
    @JSONField(name = PORT, ordinal = 2)
    protected Integer port;

    /**
     * 客户端ID
     */
    @JSONField(name = CLIENT_ID, ordinal = 3)
    protected String clientId;

    /**
     * 用户名
     */
    @JSONField(name = USERNAME, ordinal = 4)
    protected String username;

    /**
     * 密码
     */
    @JSONField(name = PASSWORD, ordinal = 5)
    protected String password;

    /**
     * 清除会话
     */
    @JSONField(name = CLEAN_SESSION, ordinal = 6)
    protected Boolean cleanSession;

    /**
     * 心跳间隔(秒)
     */
    @JSONField(name = KEEP_ALIVE, ordinal = 7)
    protected Integer keepAlive;

    /**
     * TLS启用
     */
    @JSONField(name = TLS_ENABLED, ordinal = 8)
    protected Boolean tlsEnabled;

    /**
     * 主题
     */
    @JSONField(name = TOPIC, ordinal = 9)
    protected String topic;

    /**
     * QoS等级 (0/1/2)
     */
    @JSONField(name = QOS, ordinal = 10)
    protected Integer qos;

    /**
     * 消息负载
     */
    @JSONField(name = PAYLOAD, ordinal = 11)
    protected Object payload;

    /**
     * 响应超时时间(秒)
     */
    @JSONField(name = RESPONSE_TIMEOUT, ordinal = 12)
    protected Integer responseTimeout;

    /**
     * 默认构造函数
     */
    public MqttConfigureItem() {
    }

    /**
     * 创建MQTT配置项构建器
     *
     * @return MQTT配置项构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 合并配置项
     *
     * @param other 要合并的另一个配置项
     * @return 合并后的新配置项
     */
    @Override
    public MqttConfigureItem merge(MqttConfigureItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.datasource = StringUtils.isBlank(self.datasource) ? localOther.datasource : self.datasource;
        self.broker = StringUtils.isBlank(self.broker) ? localOther.broker : self.broker;
        self.port = self.port == null ? localOther.port : self.port;
        self.clientId = StringUtils.isBlank(self.clientId) ? localOther.clientId : self.clientId;
        self.username = StringUtils.isBlank(self.username) ? localOther.username : self.username;
        self.password = StringUtils.isBlank(self.password) ? localOther.password : self.password;
        self.cleanSession = self.cleanSession == null ? localOther.cleanSession : self.cleanSession;
        self.keepAlive = self.keepAlive == null ? localOther.keepAlive : self.keepAlive;
        self.tlsEnabled = self.tlsEnabled == null ? localOther.tlsEnabled : self.tlsEnabled;
        self.topic = StringUtils.isBlank(self.topic) ? localOther.topic : self.topic;
        self.qos = self.qos == null ? localOther.qos : self.qos;
        self.payload = self.payload == null ? localOther.payload : self.payload;
        self.responseTimeout = self.responseTimeout == null ? localOther.responseTimeout : self.responseTimeout;
        return self;
    }

    /**
     * 对配置项中的变量进行求值
     *
     * @param context 测试上下文包装器
     * @return 求值后的配置项
     */
    @Override
    public MqttConfigureItem evaluate(ContextWrapper context) {
        datasource = (String) context.evaluate(datasource);
        broker = (String) context.evaluate(broker);
        clientId = (String) context.evaluate(clientId);
        username = (String) context.evaluate(username);
        password = (String) context.evaluate(password);
        topic = (String) context.evaluate(topic);
        payload = context.evaluate(payload);
        return this;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getDatasource(String defaultName) {
        return StringUtils.isNotBlank(datasource) ? datasource : defaultName;
    }

    public String getBroker() {
        return StringUtils.isNotBlank(broker) ? broker : "localhost";
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public int getPortValue() {
        if (port != null) return port;
        return Boolean.TRUE.equals(tlsEnabled) ? DEFAULT_MQTT_TLS_PORT : DEFAULT_MQTT_PORT;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isCleanSession() {
        return cleanSession == null || cleanSession;
    }

    public void setCleanSession(Boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public int getKeepAliveValue() {
        return keepAlive != null && keepAlive > 0 ? keepAlive : DEFAULT_KEEP_ALIVE;
    }

    public void setKeepAlive(Integer keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isTlsEnabled() {
        return Boolean.TRUE.equals(tlsEnabled);
    }

    public void setTlsEnabled(Boolean tlsEnabled) {
        this.tlsEnabled = tlsEnabled;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getQos() {
        return qos;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    public int getQosValue() {
        return qos != null ? qos : 1;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public Integer getResponseTimeout() {
        return responseTimeout;
    }

    public void setResponseTimeout(Integer responseTimeout) {
        this.responseTimeout = responseTimeout;
    }

    public int getResponseTimeoutValue() {
        return responseTimeout != null && responseTimeout > 0 ? responseTimeout : DEFAULT_RESPONSE_TIMEOUT;
    }

    /**
     * MQTT协议配置项构建类
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, MqttConfigureItem> {

        private MqttConfigureItem configure = new MqttConfigureItem();

        public Builder() {
        }

        public Builder datasource(String datasource) {
            configure.datasource = datasource;
            return self;
        }

        public Builder broker(String broker) {
            configure.broker = broker;
            return self;
        }

        public Builder port(int port) {
            configure.port = port;
            return self;
        }

        public Builder clientId(String clientId) {
            configure.clientId = clientId;
            return self;
        }

        public Builder username(String username) {
            configure.username = username;
            return self;
        }

        public Builder password(String password) {
            configure.password = password;
            return self;
        }

        public Builder cleanSession(boolean cleanSession) {
            configure.cleanSession = cleanSession;
            return self;
        }

        public Builder keepAlive(int keepAlive) {
            configure.keepAlive = keepAlive;
            return self;
        }

        public Builder tlsEnabled(boolean tlsEnabled) {
            configure.tlsEnabled = tlsEnabled;
            return self;
        }

        public Builder topic(String topic) {
            configure.topic = topic;
            return self;
        }

        public Builder qos(int qos) {
            configure.qos = qos;
            return self;
        }

        public Builder payload(Object payload) {
            configure.payload = payload;
            return self;
        }

        public Builder responseTimeout(int responseTimeout) {
            configure.responseTimeout = responseTimeout;
            return self;
        }

        public Builder config(MqttConfigureItem config) {
            configure = configure.merge(config);
            return self;
        }

        @Override
        public MqttConfigureItem build() {
            return configure;
        }
    }
}
