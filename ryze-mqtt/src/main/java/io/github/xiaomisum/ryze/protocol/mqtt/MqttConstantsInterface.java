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

import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;

/**
 * MQTT协议常量接口
 * <p>
 * 该接口定义了MQTT协议相关的核心常量，包括连接参数、消息参数等。
 * 所有MQTT协议相关的类都应该实现此接口以确保常量的一致性。
 * </p>
 *
 * @author xiaomi
 */
public interface MqttConstantsInterface extends TestElementConstantsInterface {

    /**
     * 默认引用名称键
     */
    String DEF_REF_NAME_KEY = "__mqtt_configure_element_default_ref_name__";

    /**
     * Broker地址
     */
    String BROKER = "broker";

    /**
     * 端口
     */
    String PORT = "port";

    /**
     * 客户端ID
     */
    String CLIENT_ID = "client_id";

    /**
     * 主题
     */
    String TOPIC = "topic";

    /**
     * QoS等级
     */
    String QOS = "qos";

    /**
     * 消息负载
     */
    String PAYLOAD = "payload";

    /**
     * 用户名
     */
    String USERNAME = "username";

    /**
     * 密码
     */
    String PASSWORD = "password";

    /**
     * 清除会话
     */
    String CLEAN_SESSION = "clean_session";

    /**
     * 心跳间隔
     */
    String KEEP_ALIVE = "keep_alive";

    /**
     * 超时时间
     */
    String TIMEOUT = "timeout";

    /**
     * TLS启用
     */
    String TLS_ENABLED = "tls_enabled";

    /**
     * 遗嘱主题
     */
    String LAST_WILL_TOPIC = "last_will_topic";

    /**
     * 遗嘱消息
     */
    String LAST_WILL_PAYLOAD = "last_will_payload";

    /**
     * 遗嘱QoS
     */
    String LAST_WILL_QOS = "last_will_qos";

    /**
     * 响应超时时间
     */
    String RESPONSE_TIMEOUT = "response_timeout";

    /**
     * MQTT版本
     */
    String MQTT_VERSION = "mqtt_version";

    /**
     * 数据源引用
     */
    String DATASOURCE = "datasource";

    /**
     * 默认MQTT端口
     */
    int DEFAULT_MQTT_PORT = 1883;

    /**
     * 默认MQTT TLS端口
     */
    int DEFAULT_MQTT_TLS_PORT = 8883;

    /**
     * 默认心跳间隔(秒)
     */
    int DEFAULT_KEEP_ALIVE = 60;

    /**
     * 默认响应超时(秒)
     */
    int DEFAULT_RESPONSE_TIMEOUT = 30;

    /**
     * 默认MQTT版本
     */
    String DEFAULT_MQTT_VERSION = "5.0";
}
