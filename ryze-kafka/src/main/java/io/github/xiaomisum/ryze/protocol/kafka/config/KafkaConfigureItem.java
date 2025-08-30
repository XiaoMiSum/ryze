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

package io.github.xiaomisum.ryze.protocol.kafka.config;

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import io.github.xiaomisum.ryze.protocol.kafka.KafkaConstantsInterface;
import io.github.xiaomisum.ryze.support.Customizer;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Kafka配置项类，用于封装Kafka客户端的所有配置参数
 * <p>
 * 该类实现了ConfigureItem接口和KafkaConstantsInterface接口，提供了Kafka客户端所需的全部配置参数，
 * 包括服务器地址、主题、键、消息内容、序列化器、确认机制、重试次数和延迟时间等。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>封装Kafka客户端配置参数</li>
 *   <li>支持配置合并</li>
 *   <li>支持变量替换</li>
 *   <li>提供默认值</li>
 * </ul>
 * </p>
 * <p>
 * 业务处理逻辑：
 * <ol>
 *   <li>提供各种Kafka配置参数的getter和setter方法</li>
 *   <li>实现配置合并逻辑，支持多层配置覆盖</li>
 *   <li>实现变量替换逻辑，支持动态配置</li>
 *   <li>提供合理的默认值</li>
 * </ol>
 * </p>
 *
 * @author xiaomi
 * @see ConfigureItem
 * @see KafkaConstantsInterface
 */
public class KafkaConfigureItem implements ConfigureItem<KafkaConfigureItem>, KafkaConstantsInterface {

    /**
     * 配置引用名称，用于标识和引用特定的配置
     */
    @JSONField(name = REF)
    protected String ref;

    /**
     * Kafka服务器地址列表，多个地址用逗号分隔
     */
    @JSONField(name = BOOTSTRAP_SERVERS)
    protected String bootstrapServers;

    /**
     * Kafka主题名称，消息将发送到该主题
     */
    @JSONField(name = KAFKA_TOPIC, ordinal = 1)
    protected String topic;

    /**
     * Kafka消息键，用于消息分区和检索
     */
    @JSONField(name = KAFKA_KEY, ordinal = 2)
    protected String key;

    /**
     * Kafka消息内容，可以是各种类型的对象
     */
    @JSONField(name = KAFKA_MESSAGE, ordinal = 3)
    protected Object message;

    /**
     * 键序列化器类名，用于序列化消息键
     */
    @JSONField(name = KEY_SERIALIZER, ordinal = 5)
    protected String keySerializer;

    /**
     * 值序列化器类名，用于序列化消息值
     */
    @JSONField(name = VALUE_SERIALIZER, ordinal = 6)
    protected String valueSerializer;

    /**
     * 确认机制，控制消息发送的可靠性级别
     */
    @JSONField(name = ACKS, ordinal = 7)
    protected Integer acks;

    /**
     * 重试次数，发送失败时的重试次数
     */
    @JSONField(name = RETRIES, ordinal = 8)
    protected Integer retries;

    /**
     * 延迟时间（毫秒），用于批量发送消息的延迟时间
     */
    @JSONField(name = LINGER_MS, ordinal = 9)
    protected Integer lingerMs;

    /**
     * 默认构造函数
     */
    public KafkaConfigureItem() {
    }

    /**
     * 创建KafkaConfigureItem构建器的静态工厂方法
     *
     * @return Builder构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 合并另一个配置项到当前配置项
     *
     * @param other 另一个Kafka配置项
     * @return 合并后的Kafka配置项
     */
    @Override
    public KafkaConfigureItem merge(KafkaConfigureItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.ref = StringUtils.isBlank(self.ref) ? localOther.ref : self.ref;
        self.bootstrapServers = StringUtils.isBlank(self.bootstrapServers) ? localOther.bootstrapServers : self.bootstrapServers;
        self.topic = StringUtils.isBlank(self.topic) ? localOther.topic : self.topic;
        self.key = StringUtils.isBlank(self.key) ? localOther.key : self.key;
        self.message = Objects.isNull(message) ? localOther.message : self.message;
        self.keySerializer = StringUtils.isBlank(self.keySerializer) ? localOther.keySerializer : self.keySerializer;
        self.valueSerializer = StringUtils.isBlank(self.valueSerializer) ? localOther.valueSerializer : self.valueSerializer;
        self.acks = Objects.isNull(acks) ? localOther.acks : self.acks;
        self.retries = Objects.isNull(retries) ? localOther.retries : self.retries;
        self.lingerMs = Objects.isNull(lingerMs) ? localOther.lingerMs : self.lingerMs;
        return self;
    }

    /**
     * 在测试上下文中评估配置项，替换其中的变量
     *
     * @param context 测试上下文包装器
     * @return 评估后的Kafka配置项
     */
    @Override
    public KafkaConfigureItem evaluate(ContextWrapper context) {
        ref = (String) context.evaluate(ref);
        bootstrapServers = (String) context.evaluate(bootstrapServers);
        topic = (String) context.evaluate(topic);
        key = (String) context.evaluate(key);
        keySerializer = (String) context.evaluate(keySerializer);
        valueSerializer = (String) context.evaluate(valueSerializer);
        message = context.evaluate(message);
        return this;
    }

    /**
     * 获取配置引用名称
     *
     * @return 配置引用名称
     */
    public String getRef() {
        return ref;
    }

    /**
     * 设置配置引用名称
     *
     * @param ref 配置引用名称
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * 获取Kafka服务器地址列表
     *
     * @return Kafka服务器地址列表
     */
    public String getBootstrapServers() {
        return bootstrapServers;
    }

    /**
     * 设置Kafka服务器地址列表
     *
     * @param bootstrapServers Kafka服务器地址列表
     */
    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    /**
     * 获取Kafka主题名称
     *
     * @return Kafka主题名称
     */
    public String getTopic() {
        return topic;
    }

    /**
     * 设置Kafka主题名称
     *
     * @param topic Kafka主题名称
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * 获取Kafka消息键
     *
     * @return Kafka消息键
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置Kafka消息键
     *
     * @param key Kafka消息键
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 获取Kafka消息内容
     *
     * @return Kafka消息内容
     */
    public Object getMessage() {
        return message;
    }

    /**
     * 设置Kafka消息内容
     *
     * @param message Kafka消息内容
     */
    public void setMessage(Object message) {
        this.message = message;
    }

    /**
     * 获取键序列化器类名
     *
     * @return 键序列化器类名
     */
    public String getKeySerializer() {
        return StringUtils.isBlank(keySerializer) ? "org.apache.kafka.common.serialization.StringSerializer" : valueSerializer;
    }

    /**
     * 设置键序列化器类名
     *
     * @param keySerializer 键序列化器类名
     */
    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    /**
     * 获取值序列化器类名
     *
     * @return 值序列化器类名
     */
    public String getValueSerializer() {
        return StringUtils.isBlank(valueSerializer) ? "org.apache.kafka.common.serialization.StringSerializer" : valueSerializer;
    }

    /**
     * 设置值序列化器类名
     *
     * @param valueSerializer 值序列化器类名
     */
    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    /**
     * 获取确认机制
     *
     * @return 确认机制
     */
    public Integer getAcks() {
        return Objects.isNull(acks) ? 1 : acks;
    }

    /**
     * 设置确认机制
     *
     * @param acks 确认机制
     */
    public void setAcks(Integer acks) {
        this.acks = acks;
    }

    /**
     * 获取重试次数
     *
     * @return 重试次数
     */
    public Integer getRetries() {
        return Objects.isNull(retries) ? 5 : retries;
    }

    /**
     * 设置重试次数
     *
     * @param retries 重试次数
     */
    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    /**
     * 获取延迟时间（毫秒）
     *
     * @return 延迟时间（毫秒）
     */
    public Integer getLingerMs() {
        return Objects.isNull(lingerMs) ? 20 : lingerMs;
    }

    /**
     * 设置延迟时间（毫秒）
     *
     * @param lingerMs 延迟时间（毫秒）
     */
    public void setLingerMs(Integer lingerMs) {
        this.lingerMs = lingerMs;
    }

    /**
     * Kafka 配置属性 构建器
     * <p>
     * 用于构建KafkaConfigureItem实例的构建器类，提供了链式调用的API。
     * </p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, KafkaConfigureItem> {

        /**
         * 待构建的Kafka配置项实例
         */
        private KafkaConfigureItem configure = new KafkaConfigureItem();

        /**
         * 默认构造函数
         */
        public Builder() {
        }

        /**
         * 设置配置引用名称
         *
         * @param ref 配置引用名称
         * @return 构建器实例
         */
        public Builder ref(String ref) {
            configure.ref = ref;
            return self;
        }

        /**
         * 设置Kafka服务器地址
         *
         * @param bootstrapServers Kafka服务器地址列表
         * @return 构建器实例
         */
        public Builder address(String bootstrapServers) {
            configure.bootstrapServers = bootstrapServers;
            return self;
        }

        /**
         * 设置Kafka服务器地址（与address方法功能相同）
         *
         * @param bootstrapServers Kafka服务器地址列表
         * @return 构建器实例
         */
        public Builder bootstrapServers(String bootstrapServers) {
            configure.bootstrapServers = bootstrapServers;
            return self;
        }

        /**
         * 设置Kafka主题名称
         *
         * @param topic Kafka主题名称
         * @return 构建器实例
         */
        public Builder topic(String topic) {
            configure.topic = topic;
            return self;
        }

        /**
         * 设置Kafka消息键
         *
         * @param key Kafka消息键
         * @return 构建器实例
         */
        public Builder key(String key) {
            configure.key = key;
            return self;
        }

        /**
         * 使用自定义函数式接口设置消息内容
         *
         * @param type       消息类型
         * @param customizer 自定义函数式接口
         * @param <T>        消息类型参数
         * @return 构建器实例
         */
        public <T> Builder message(Class<T> type, Customizer<T> customizer) {
            try {
                T message = type.getConstructor().newInstance();
                customizer.customize(message);
                configure.setMessage(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return self;
        }


        /**
         * 设置消息内容
         *
         * @param message 消息内容
         * @return 构建器实例
         */
        public Builder message(Object message) {
            this.configure.message = message;
            return self;
        }

        /**
         * 设置消息内容（列表类型）
         *
         * @param message 消息内容列表
         * @return 构建器实例
         */
        public Builder message(List<?> message) {
            this.configure.message = message;
            return self;
        }

        /**
         * 设置消息内容（映射类型）
         *
         * @param message 消息内容映射
         * @return 构建器实例
         */
        public Builder message(Map<?, ?> message) {
            this.configure.message = message;
            return self;
        }


        /**
         * 设置键序列化器类名
         *
         * @param keySerializer 键序列化器类名
         * @return 构建器实例
         */
        public Builder keySerializer(String keySerializer) {
            configure.keySerializer = keySerializer;
            return self;
        }

        /**
         * 设置值序列化器类名
         *
         * @param valueSerializer 值序列化器类名
         * @return 构建器实例
         */
        public Builder valueSerializer(String valueSerializer) {
            configure.valueSerializer = valueSerializer;
            return self;
        }

        /**
         * 设置确认机制
         *
         * @param acks 确认机制
         * @return 构建器实例
         */
        public Builder acks(Integer acks) {
            configure.acks = acks;
            return self;
        }

        /**
         * 设置重试次数
         *
         * @param retries 重试次数
         * @return 构建器实例
         */
        public Builder retries(Integer retries) {
            configure.retries = retries;
            return self;
        }

        /**
         * 设置延迟时间（毫秒）
         *
         * @param lingerMs 延迟时间（毫秒）
         * @return 构建器实例
         */
        public Builder lingerMs(Integer lingerMs) {
            configure.lingerMs = lingerMs;
            return self;
        }

        /**
         * 合并另一个配置项
         *
         * @param config 另一个Kafka配置项
         * @return 构建器实例
         */
        public Builder config(KafkaConfigureItem config) {
            configure = configure.merge(config);
            return self;
        }


        /**
         * 构建Kafka配置项实例
         *
         * @return KafkaConfigureItem实例
         */
        @Override
        public KafkaConfigureItem build() {
            return configure;
        }
    }

}