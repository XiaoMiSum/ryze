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

package io.github.xiaomisum.ryze.protocol.active.config;

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import io.github.xiaomisum.ryze.protocol.active.ActiveConstantsInterface;
import io.github.xiaomisum.ryze.support.Customizer;
import org.apache.activemq.ActiveMQConnection;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ActiveMQ配置项，封装了ActiveMQ连接和消息发送所需的所有配置参数
 * <p>
 * 该类定义了ActiveMQ连接和消息发送所需的所有配置信息，包括连接参数和消息内容。
 * 它实现了ConfigureItem接口，支持配置合并和变量替换功能。
 * </p>
 * <p>
 * 主要配置项包括：
 * <ul>
 *   <li>Broker URL：ActiveMQ服务器地址</li>
 *   <li>Topic/Queue：消息发送目标</li>
 *   <li>用户名/密码：连接认证信息</li>
 *   <li>消息内容：支持多种数据类型</li>
 *   <li>引用名称：用于配置引用和合并</li>
 * </ul>
 * </p>
 * <p>
 * 支持的功能：
 * <ul>
 *   <li>配置合并：支持将多个配置项合并，实现配置继承</li>
 *   <li>变量替换：支持在配置中使用变量表达式</li>
 *   <li>默认值：为各项配置提供合理的默认值</li>
 *   <li>多种消息格式：支持字符串、数字、布尔值、对象等多种消息格式</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class ActiveConfigureItem implements ConfigureItem<ActiveConfigureItem>, ActiveConstantsInterface {

    /**
     * ActiveMQ服务器地址
     */
    @JSONField(name = BROKER_URL)
    protected String brokerUrl;
    
    /**
     * 消息发送目标Topic
     */
    @JSONField(name = ACTIVEMQ_TOPIC, ordinal = 1)
    protected String topic;
    
    /**
     * 消息发送目标Queue
     */
    @JSONField(name = ACTIVEMQ_QUEUE, ordinal = 2)
    protected String queue;
    
    /**
     * 消息内容，支持多种数据类型
     */
    @JSONField(name = ACTIVEMQ_MESSAGE, ordinal = 3)
    protected Object message;
    
    /**
     * 连接用户名
     */
    @JSONField(name = ACTIVEMQ_USERNAME, ordinal = 5)
    protected String username;
    
    /**
     * 连接密码
     */
    @JSONField(name = ACTIVEMQ_PASSWORD, ordinal = 6)
    protected String password;
    
    /**
     * 配置引用名称，用于配置合并和引用
     */
    @JSONField(name = REF)
    protected String ref;

    /**
     * 默认构造函数
     */
    public ActiveConfigureItem() {
    }

    /**
     * 获取构建器实例
     *
     * @return ActiveConfigureItem.Builder 构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 合并当前配置与另一个配置
     * <p>
     * 合并规则：当前配置中的非空值优先，空值使用另一个配置中的值
     * </p>
     *
     * @param other 另一个配置项
     * @return 合并后的新配置项
     */
    @Override
    public ActiveConfigureItem merge(ActiveConfigureItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.ref = StringUtils.isBlank(self.ref) ? localOther.ref : self.ref;
        self.brokerUrl = StringUtils.isBlank(self.brokerUrl) ? localOther.brokerUrl : self.brokerUrl;
        self.topic = StringUtils.isBlank(self.topic) ? localOther.topic : self.topic;
        self.queue = StringUtils.isBlank(self.queue) ? localOther.queue : self.queue;
        self.message = Objects.isNull(message) ? localOther.message : self.message;
        self.username = StringUtils.isBlank(self.username) ? localOther.username : self.username;
        self.password = StringUtils.isBlank(self.password) ? localOther.password : self.password;
        return self;
    }

    /**
     * 在上下文中评估配置中的变量表达式
     *
     * @param context 上下文包装器
     * @return 评估后的配置项
     */
    @Override
    public ActiveConfigureItem evaluate(ContextWrapper context) {
        ref = (String) context.evaluate(ref);
        brokerUrl = (String) context.evaluate(brokerUrl);
        topic = (String) context.evaluate(topic);
        queue = (String) context.evaluate(queue);
        username = (String) context.evaluate(username);
        password = (String) context.evaluate(password);
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
     * 获取Broker URL
     * <p>
     * 如果未设置，返回ActiveMQ默认Broker URL
     * </p>
     *
     * @return Broker URL
     */
    public String getBrokerUrl() {
        return StringUtils.isBlank(brokerUrl) ? ActiveMQConnection.DEFAULT_BROKER_URL : brokerUrl;
    }

    /**
     * 设置Broker URL
     *
     * @param brokerUrl Broker URL
     */
    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    /**
     * 获取Topic名称
     *
     * @return Topic名称
     */
    public String getTopic() {
        return topic;
    }

    /**
     * 设置Topic名称
     *
     * @param topic Topic名称
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * 获取Queue名称
     *
     * @return Queue名称
     */
    public String getQueue() {
        return queue;
    }

    /**
     * 设置Queue名称
     *
     * @param queue Queue名称
     */
    public void setQueue(String queue) {
        this.queue = queue;
    }

    /**
     * 获取消息内容
     *
     * @return 消息内容
     */
    public Object getMessage() {
        return message;
    }

    /**
     * 设置消息内容
     *
     * @param message 消息内容
     */
    public void setMessage(Object message) {
        this.message = message;
    }

    /**
     * 获取用户名
     * <p>
     * 如果未设置，返回ActiveMQ默认用户名
     * </p>
     *
     * @return 用户名
     */
    public String getUsername() {
        return StringUtils.isBlank(username) ? ActiveMQConnection.DEFAULT_USER : username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     * <p>
     * 如果未设置，返回ActiveMQ默认密码
     * </p>
     *
     * @return 密码
     */
    public String getPassword() {
        return StringUtils.isBlank(password) ? ActiveMQConnection.DEFAULT_PASSWORD : password;

    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * ActiveMQ配置项构建器
     * <p>
     * 提供链式调用方式构建ActiveMQ配置项，支持多种消息格式设置方法。
     * </p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, ActiveConfigureItem> {

        private ActiveConfigureItem configure = new ActiveConfigureItem();

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
            configure.setRef(ref);
            return self;
        }

        /**
         * 设置Broker URL
         *
         * @param brokerUrl Broker URL
         * @return 构建器实例
         */
        public Builder brokerUrl(String brokerUrl) {
            configure.setBrokerUrl(brokerUrl);
            return self;
        }

        /**
         * 设置Topic名称
         *
         * @param topic Topic名称
         * @return 构建器实例
         */
        public Builder topic(String topic) {
            configure.setTopic(topic);
            return self;
        }

        /**
         * 设置Queue名称
         *
         * @param queue Queue名称
         * @return 构建器实例
         */
        public Builder queue(String queue) {
            configure.setQueue(queue);
            return self;
        }

        /**
         * 设置消息内容（通用方法）
         *
         * @param message 消息内容
         * @return 构建器实例
         */
        public Builder message(Object message) {
            configure.setMessage(message);
            return self;
        }

        /**
         * 设置消息内容（使用自定义构建器）
         *
         * @param type       消息类型
         * @param customizer 自定义构建器
         * @param <T>        消息类型泛型
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
         * 设置用户名
         *
         * @param username 用户名
         * @return 构建器实例
         */
        public Builder username(String username) {
            configure.setUsername(username);
            return self;
        }

        /**
         * 设置密码
         *
         * @param password 密码
         * @return 构建器实例
         */
        public Builder password(String password) {
            configure.setPassword(password);
            return self;
        }

        /**
         * 设置消息内容（字符串）
         *
         * @param message 消息内容
         * @return 构建器实例
         */
        public Builder message(String message) {
            configure.setMessage(message);
            return self;
        }

        /**
         * 设置消息内容（列表）
         *
         * @param message 消息内容
         * @return 构建器实例
         */
        public Builder message(List<?> message) {
            configure.setMessage(message);
            return self;
        }

        /**
         * 设置消息内容（Map）
         *
         * @param message 消息内容
         * @return 构建器实例
         */
        public Builder message(Map<?, ?> message) {
            configure.setMessage(message);
            return self;
        }

        /**
         * 设置配置项（用于配置合并）
         *
         * @param config 配置项
         * @return 构建器实例
         */
        public Builder config(ActiveConfigureItem config) {
            configure = configure.merge(config);
            return self;
        }

        /**
         * 构建ActiveConfigureItem实例
         *
         * @return ActiveConfigureItem实例
         */
        @Override
        public ActiveConfigureItem build() {
            return configure;
        }
    }
}