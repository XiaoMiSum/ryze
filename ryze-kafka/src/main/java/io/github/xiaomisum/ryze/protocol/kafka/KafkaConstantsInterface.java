/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze.protocol.kafka;

import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;
import org.apache.kafka.clients.producer.ProducerConfig;

/**
 * Kafka常量接口，定义了Kafka协议测试中使用的所有常量
 * <p>
 * 该接口继承自TestElementConstantsInterface，定义了Kafka协议测试中使用的所有常量，
 * 包括默认引用名称、消息属性名称、Kafka生产者配置等常量定义，
 * 为整个Kafka协议测试模块提供统一的常量定义。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>定义Kafka测试的默认引用名称</li>
 *   <li>定义Kafka消息属性名称</li>
 *   <li>定义Kafka生产者配置常量</li>
 *   <li>提供常量统一管理</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @since 2021/11/11 11:08
 * @see TestElementConstantsInterface
 * @see ProducerConfig
 */
public interface KafkaConstantsInterface extends TestElementConstantsInterface {

    /**
     * 默认配置引用名称键，用于标识默认的Kafka配置
     */
    String DEF_REF_NAME_KEY = "__kafka_configure_element_default_ref_name__";

    /**
     * Kafka消息内容属性名称
     */
    String KAFKA_MESSAGE = "message";

    /**
     * Kafka主题属性名称
     */
    String KAFKA_TOPIC = "topic";

    /**
     * Kafka消息键属性名称
     */
    String KAFKA_KEY = "key";

    /**
     * Kafka服务器地址配置键，对应ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
     */
    String BOOTSTRAP_SERVERS = ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

    /**
     * Kafka确认机制配置键，对应ProducerConfig.ACKS_CONFIG
     */
    String ACKS = ProducerConfig.ACKS_CONFIG;

    /**
     * Kafka重试次数配置键，对应ProducerConfig.RETRIES_CONFIG
     */
    String RETRIES = ProducerConfig.RETRIES_CONFIG;

    /**
     * Kafka延迟时间配置键，对应ProducerConfig.LINGER_MS_CONFIG
     */
    String LINGER_MS = ProducerConfig.LINGER_MS_CONFIG;

    /**
     * Kafka键序列化器配置键，对应ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
     */
    String KEY_SERIALIZER = ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;

    /**
     * Kafka值序列化器配置键，对应ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
     */
    String VALUE_SERIALIZER = ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

}