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

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MQTT协议客户端封装
 * <p>
 * 基于HiveMQ MQTT Client封装，提供连接、断开、发布、订阅等基本操作。
 * </p>
 *
 * @author xiaomi
 */
public class MqttProtocolClient implements MqttConstantsInterface {

    private Mqtt5BlockingClient client;
    private final String broker;
    private final int port;
    private final String clientId;
    private final String username;
    private final String password;
    private final boolean cleanSession;
    private final int keepAlive;
    private final boolean tlsEnabled;

    public MqttProtocolClient(String broker, int port, String clientId, String username,
                              String password, boolean cleanSession, int keepAlive, boolean tlsEnabled) {
        this.broker = broker;
        this.port = port;
        this.clientId = clientId != null ? clientId : UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.cleanSession = cleanSession;
        this.keepAlive = keepAlive;
        this.tlsEnabled = tlsEnabled;
    }

    /**
     * 建立MQTT连接
     */
    public void connect() {
        var clientBuilder = MqttClient.builder()
                .useMqttVersion5()
                .identifier(clientId)
                .serverHost(broker)
                .serverPort(port);

        if (tlsEnabled) {
            clientBuilder.sslWithDefaultConfig();
        }

        client = clientBuilder.buildBlocking();

        var connectBuilder = client.connectWith()
                .cleanStart(cleanSession)
                .keepAlive(keepAlive);

        if (username != null && !username.isEmpty()) {
            connectBuilder.simpleAuth()
                    .username(username)
                    .password(password != null ? password.getBytes(StandardCharsets.UTF_8) : new byte[0])
                    .applySimpleAuth();
        }

        connectBuilder.send();
    }

    /**
     * 断开MQTT连接
     */
    public void disconnect() {
        if (client != null) {
            client.disconnect();
        }
    }

    /**
     * 发布消息
     *
     * @param topic   主题
     * @param payload 消息负载
     * @param qos     QoS等级(0/1/2)
     */
    public void publish(String topic, byte[] payload, int qos) {
        client.publishWith()
                .topic(topic)
                .payload(payload)
                .qos(toMqttQos(qos))
                .send();
    }

    /**
     * 订阅主题并等待消息
     *
     * @param topic   主题
     * @param qos     QoS等级
     * @param timeout 超时(秒)
     * @return 收到的消息内容，超时返回null
     */
    public String subscribe(String topic, int qos, int timeout) {
        client.subscribeWith()
                .topicFilter(topic)
                .qos(toMqttQos(qos))
                .send();

        try (Mqtt5BlockingClient.Mqtt5Publishes publishes = client.publishes(MqttGlobalPublishFilter.ALL)) {
            Optional<Mqtt5Publish> message = publishes.receive(timeout, TimeUnit.SECONDS);
            if (message.isPresent()) {
                return message.get().getPayloadAsBytes().length > 0
                        ? new String(message.get().getPayloadAsBytes(), StandardCharsets.UTF_8)
                        : "";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("MQTT subscribe interrupted", e);
        }
        return null;
    }

    /**
     * 获取底层客户端
     *
     * @return Mqtt5BlockingClient
     */
    public Mqtt5BlockingClient getClient() {
        return client;
    }

    private MqttQos toMqttQos(int qos) {
        return switch (qos) {
            case 0 -> MqttQos.AT_MOST_ONCE;
            case 1 -> MqttQos.AT_LEAST_ONCE;
            case 2 -> MqttQos.EXACTLY_ONCE;
            default -> MqttQos.AT_LEAST_ONCE;
        };
    }
}
