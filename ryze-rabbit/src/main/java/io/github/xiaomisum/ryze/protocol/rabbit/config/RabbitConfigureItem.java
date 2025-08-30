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

package io.github.xiaomisum.ryze.protocol.rabbit.config;

import com.alibaba.fastjson2.annotation.JSONField;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import io.github.xiaomisum.ryze.protocol.rabbit.RabbitConstantsInterface;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.support.groovy.Groovy;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * RabbitMQ 配置项类
 * <p>
 * 该类用于封装 RabbitMQ 相关的配置信息，包括连接参数、队列配置、交换机配置、消息属性等。
 * 实现了 ConfigureItem 接口，支持配置合并和变量替换功能。
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class RabbitConfigureItem implements ConfigureItem<RabbitConfigureItem>, RabbitConstantsInterface {

    /**
     * 引用名称，用于在上下文中标识该配置项
     */
    @JSONField(name = REF)
    protected String ref;

    /**
     * 虚拟主机
     */
    @JSONField(name = VIRTUAL_HOST, ordinal = 1)
    protected String virtualHost;

    /**
     * 主机地址
     */
    @JSONField(name = HOST, ordinal = 2)
    protected String host;

    /**
     * 端口号
     */
    @JSONField(name = PORT, ordinal = 3)
    protected String port;

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
     * 消息内容
     */
    @JSONField(name = MESSAGE, ordinal = 6)
    protected Object message;

    /**
     * 连接超时时间（毫秒）
     */
    @JSONField(name = TIMEOUT, ordinal = 7)
    protected Integer timeout;

    /**
     * 队列配置
     */
    @JSONField(name = QUEUE_CONFIG, ordinal = 8)
    protected Queue queue;

    /**
     * 交换机配置
     */
    @JSONField(name = EXCHANGE, ordinal = 9)
    protected Exchange exchange;

    /**
     * 消息属性配置
     */
    @JSONField(name = PROPS, ordinal = 10)
    protected Props props;

    /**
     * 默认构造函数
     */
    public RabbitConfigureItem() {
    }

    /**
     * 创建构建器实例
     * <p>
     * 工厂方法，用于创建 RabbitConfigureItem.Builder 构建器实例。
     * </p>
     *
     * @return RabbitConfigureItem.Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 合并配置项
     * <p>
     * 将当前配置项与另一个配置项合并，优先使用当前配置项的值。
     * </p>
     *
     * @param other 另一个配置项
     * @return 合并后的新配置项
     */
    @Override
    public RabbitConfigureItem merge(RabbitConfigureItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.ref = StringUtils.isBlank(self.ref) ? localOther.ref : self.ref;
        self.virtualHost = StringUtils.isBlank(self.virtualHost) ? localOther.virtualHost : self.virtualHost;
        self.host = StringUtils.isBlank(self.host) ? localOther.host : self.host;
        self.port = StringUtils.isBlank(self.port) ? localOther.port : self.port;
        self.message = Objects.isNull(message) ? localOther.message : self.message;
        self.username = StringUtils.isBlank(self.username) ? localOther.username : self.username;
        self.password = StringUtils.isBlank(self.password) ? localOther.password : self.password;
        self.timeout = Objects.isNull(timeout) ? localOther.timeout : self.timeout;
        self.queue = Objects.isNull(queue) ? localOther.queue : self.queue;
        self.exchange = Objects.isNull(exchange) ? localOther.exchange : self.exchange;
        self.props = Objects.isNull(props) ? localOther.props : self.props;
        return self;
    }

    /**
     * 在上下文中评估配置项
     * <p>
     * 使用上下文中的变量替换配置项中的变量占位符。
     * </p>
     *
     * @param context 上下文包装器
     * @return 评估后的配置项
     */
    @Override
    public RabbitConfigureItem evaluate(ContextWrapper context) {
        ref = (String) context.evaluate(ref);
        virtualHost = (String) context.evaluate(virtualHost);
        host = (String) context.evaluate(host);
        port = (String) context.evaluate(port);
        username = (String) context.evaluate(username);
        password = (String) context.evaluate(password);
        message = context.evaluate(message);
        queue = (Queue) context.evaluate(queue);
        exchange = (Exchange) context.evaluate(exchange);
        props = (Props) context.evaluate(props);
        return this;
    }

    /**
     * 获取引用名称
     *
     * @return 引用名称
     */
    public String getRef() {
        return ref;
    }

    /**
     * 设置引用名称
     *
     * @param ref 引用名称
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * 获取虚拟主机
     * <p>
     * 如果未设置虚拟主机，则返回默认值 "/"
     * </p>
     *
     * @return 虚拟主机
     */
    public String getVirtualHost() {
        return StringUtils.isBlank(virtualHost) ? "/" : virtualHost;
    }

    /**
     * 设置虚拟主机
     *
     * @param virtualHost 虚拟主机
     */
    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    /**
     * 获取主机地址
     *
     * @return 主机地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置主机地址
     *
     * @param host 主机地址
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取端口号
     * <p>
     * 如果未设置端口号，则返回默认值 "5672"
     * </p>
     *
     * @return 端口号
     */
    public String getPort() {
        return StringUtils.isBlank(port) ? "5672" : port;
    }

    /**
     * 设置端口号
     *
     * @param port 端口号
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * 获取用户名
     * <p>
     * 如果未设置用户名，则返回默认值 "guest"
     * </p>
     *
     * @return 用户名
     */
    public String getUsername() {
        return StringUtils.isBlank(username) ? "guest" : username;
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
     * 如果未设置密码，则返回默认值 "guest"
     * </p>
     *
     * @return 密码
     */
    public String getPassword() {
        return StringUtils.isBlank(password) ? "guest" : password;
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
     * 获取连接超时时间
     * <p>
     * 如果未设置超时时间，则返回默认值 60000 毫秒
     * </p>
     *
     * @return 连接超时时间（毫秒）
     */
    public Integer getTimeout() {
        return Objects.isNull(timeout) ? 60000 : timeout;
    }

    /**
     * 设置连接超时时间
     *
     * @param timeout 连接超时时间（毫秒）
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * 获取队列配置
     *
     * @return 队列配置
     */
    public Queue getQueue() {
        return queue;
    }

    /**
     * 设置队列配置
     *
     * @param queue 队列配置
     */
    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    /**
     * 获取交换机配置
     *
     * @return 交换机配置
     */
    public Exchange getExchange() {
        return exchange;
    }

    /**
     * 设置交换机配置
     *
     * @param exchange 交换机配置
     */
    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    /**
     * 获取消息属性配置
     *
     * @return 消息属性配置
     */
    public Props getProps() {
        return props;
    }

    /**
     * 设置消息属性配置
     *
     * @param props 消息属性配置
     */
    public void setProps(Props props) {
        this.props = props;
    }

    /**
     * 队列配置内部类
     * <p>
     * 用于封装 RabbitMQ 队列的相关配置信息。
     * </p>
     */
    public static class Queue implements ConfigureItem<Queue> {

        /**
         * 队列名称
         */
        @JSONField(name = QUEUE_NAME)
        protected String name;

        /**
         * 是否持久化
         */
        @JSONField(name = QUEUE_DURABLE, ordinal = 1)
        protected Boolean durable;

        /**
         * 是否独占
         */
        @JSONField(name = QUEUE_EXCLUSIVE, ordinal = 2)
        protected Boolean exclusive;

        /**
         * 是否自动删除
         */
        @JSONField(name = QUEUE_AUTO_DELETE, ordinal = 3)
        protected Boolean autoDelete;

        /**
         * 队列参数
         */
        @JSONField(name = QUEUE_ARGUMENTS, ordinal = 4)
        protected Map<String, Object> arguments;

        /**
         * 默认构造函数
         */
        public Queue() {
        }

        /**
         * 合并队列配置
         * <p>
         * 将当前队列配置与另一个队列配置合并，优先使用当前配置的值。
         * </p>
         *
         * @param other 另一个队列配置
         * @return 合并后的新队列配置
         */
        @Override
        public Queue merge(Queue other) {
            if (other == null) {
                return copy();
            }
            var localOther = other.copy();
            var self = copy();
            self.name = StringUtils.isBlank(name) ? localOther.name : self.name;
            self.durable = Objects.isNull(durable) ? localOther.durable : self.durable;
            self.exclusive = Objects.isNull(exclusive) ? localOther.exclusive : self.exclusive;
            self.autoDelete = Objects.isNull(autoDelete) ? localOther.autoDelete : self.autoDelete;
            self.arguments = Objects.isNull(arguments) ? localOther.arguments : self.arguments;
            return self;
        }

        /**
         * 在上下文中评估队列配置
         * <p>
         * 使用上下文中的变量替换队列配置中的变量占位符。
         * </p>
         *
         * @param context 上下文包装器
         * @return 评估后的队列配置
         */
        @Override
        public Queue evaluate(ContextWrapper context) {
            name = (String) context.evaluate(name);
            durable = (Boolean) context.evaluate(durable);
            exclusive = (Boolean) context.evaluate(exclusive);
            autoDelete = (Boolean) context.evaluate(autoDelete);
            arguments = (Map<String, Object>) context.evaluate(arguments);
            return this;
        }

        /**
         * 获取队列名称
         *
         * @return 队列名称
         */
        public String getName() {
            return name;
        }

        /**
         * 设置队列名称
         *
         * @param name 队列名称
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * 获取是否持久化标志
         * <p>
         * 如果未设置，则默认为 false
         * </p>
         *
         * @return 是否持久化
         */
        public Boolean getDurable() {
            return Objects.nonNull(durable) && durable;
        }

        /**
         * 设置是否持久化
         *
         * @param durable 是否持久化
         */
        public void setDurable(Boolean durable) {
            this.durable = durable;
        }

        /**
         * 获取是否独占标志
         * <p>
         * 如果未设置，则默认为 false
         * </p>
         *
         * @return 是否独占
         */
        public Boolean getExclusive() {
            return Objects.nonNull(exclusive) && exclusive;
        }

        /**
         * 设置是否独占
         *
         * @param exclusive 是否独占
         */
        public void setExclusive(Boolean exclusive) {
            this.exclusive = exclusive;
        }

        /**
         * 获取是否自动删除标志
         * <p>
         * 如果未设置，则默认为 false
         * </p>
         *
         * @return 是否自动删除
         */
        public Boolean getAutoDelete() {
            return Objects.nonNull(autoDelete) && autoDelete;
        }

        /**
         * 设置是否自动删除
         *
         * @param autoDelete 是否自动删除
         */
        public void setAutoDelete(Boolean autoDelete) {
            this.autoDelete = autoDelete;
        }

        /**
         * 获取队列参数
         *
         * @return 队列参数
         */
        public Map<String, Object> getArguments() {
            return arguments;
        }

        /**
         * 设置队列参数
         *
         * @param arguments 队列参数
         */
        public void setArguments(Map<String, Object> arguments) {
            this.arguments = arguments;
        }

        /**
         * 队列配置构建器
         * <p>
         * 用于构建队列配置的内部构建器类。
         * </p>
         */
        public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, Queue> {

            /**
             * 队列配置实例
             */
            private Queue queue = new Queue();

            /**
             * 创建构建器实例
             * <p>
             * 工厂方法，用于创建 Queue.Builder 构建器实例。
             * </p>
             *
             * @return Queue.Builder 实例
             */
            public static Builder builder() {
                return new Builder();
            }

            /**
             * 设置队列名称
             *
             * @param name 队列名称
             * @return 当前构建器实例，支持链式调用
             */
            public Builder name(String name) {
                queue.name = name;
                return self;
            }

            /**
             * 设置队列为持久化
             *
             * @return 当前构建器实例，支持链式调用
             */
            public Builder durable() {
                queue.durable = true;
                return self;
            }

            /**
             * 设置队列为独占
             *
             * @return 当前构建器实例，支持链式调用
             */
            public Builder exclusive() {
                queue.exclusive = true;
                return self;
            }

            /**
             * 设置队列为自动删除
             *
             * @return 当前构建器实例，支持链式调用
             */
            public Builder autoDelete() {
                queue.autoDelete = true;
                return self;
            }

            /**
             * 设置队列参数
             *
             * @param arguments 队列参数
             * @return 当前构建器实例，支持链式调用
             */
            public Builder arguments(Map<String, Object> arguments) {
                queue.arguments = arguments;
                return self;
            }

            /**
             * 添加队列参数
             *
             * @param name  参数名
             * @param value 参数值
             * @return 当前构建器实例，支持链式调用
             */
            public Builder argument(String name, Object value) {
                synchronized (this) {
                    if (queue.arguments == null) {
                        synchronized (this) {
                            this.queue.arguments = new HashMap<>();
                        }
                    }
                }
                queue.arguments.put(name, value);
                return self;
            }

            /**
             * 配置队列
             * <p>
             * 将指定的队列配置合并到当前队列配置中。
             * </p>
             *
             * @param queue 队列配置
             * @return 当前构建器实例，支持链式调用
             */
            public Builder config(Queue queue) {
                this.queue = this.queue.merge(queue);
                return self;
            }

            /**
             * 构建队列配置实例
             *
             * @return 队列配置实例
             */
            @Override
            public Queue build() {
                return queue;
            }
        }
    }

    /**
     * 交换机配置内部类
     * <p>
     * 用于封装 RabbitMQ 交换机的相关配置信息。
     * </p>
     */
    public static class Exchange implements ConfigureItem<Exchange> {

        /**
         * 交换机名称
         */
        @JSONField(name = EXCHANGE_NAME)
        protected String name;
        
        /**
         * 交换机类型
         */
        @JSONField(name = EXCHANGE_TYPE, ordinal = 1)
        protected String type;
        
        /**
         * 路由键
         */
        @JSONField(name = EXCHANGE_ROUTING_KEY, ordinal = 2)
        protected String routingKey;

        /**
         * 默认构造函数
         */
        public Exchange() {
        }

        /**
         * 合并交换机配置
         * <p>
         * 将当前交换机配置与另一个交换机配置合并，优先使用当前配置的值。
         * </p>
         *
         * @param other 另一个交换机配置
         * @return 合并后的新交换机配置
         */
        @Override
        public Exchange merge(Exchange other) {
            if (other == null) {
                return copy();
            }
            var localOther = other.copy();
            var self = copy();
            self.name = StringUtils.isBlank(name) ? localOther.name : self.name;
            self.type = StringUtils.isBlank(type) ? localOther.type : self.type;
            self.routingKey = StringUtils.isBlank(routingKey) ? localOther.routingKey : self.routingKey;
            return self;
        }

        /**
         * 在上下文中评估交换机配置
         * <p>
         * 使用上下文中的变量替换交换机配置中的变量占位符。
         * </p>
         *
         * @param context 上下文包装器
         * @return 评估后的交换机配置
         */
        @Override
        public Exchange evaluate(ContextWrapper context) {
            name = (String) context.evaluate(name);
            type = (String) context.evaluate(type);
            routingKey = (String) context.evaluate(routingKey);
            return this;
        }

        /**
         * 获取交换机名称
         *
         * @return 交换机名称
         */
        public String getName() {
            return name;
        }

        /**
         * 设置交换机名称
         *
         * @param name 交换机名称
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * 获取交换机类型
         * <p>
         * 如果未设置交换机类型，则返回默认值 EXCHANGE_TYPE_TOPIC
         * </p>
         *
         * @return 交换机类型
         */
        public String getType() {
            return StringUtils.isBlank(type) ? EXCHANGE_TYPE_TOPIC : type;
        }

        /**
         * 设置交换机类型
         *
         * @param type 交换机类型
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * 获取路由键
         *
         * @return 路由键
         */
        public String getRoutingKey() {
            return routingKey;
        }

        /**
         * 设置路由键
         *
         * @param routingKey 路由键
         */
        public void setRoutingKey(String routingKey) {
            this.routingKey = routingKey;
        }

        /**
         * 交换机配置构建器
         * <p>
         * 用于构建交换机配置的内部构建器类。
         * </p>
         */
        public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, Exchange> {

            /**
             * 交换机配置实例
             */
            private Exchange exchange = new Exchange();

            /**
             * 创建构建器实例
             * <p>
             * 工厂方法，用于创建 Exchange.Builder 构建器实例。
             * </p>
             *
             * @return Exchange.Builder 实例
             */
            public static Builder builder() {
                return new Builder();
            }

            /**
             * 设置交换机名称
             *
             * @param name 交换机名称
             * @return 当前构建器实例，支持链式调用
             */
            public Builder name(String name) {
                exchange.name = name;
                return self;
            }

            /**
             * 设置交换机类型
             *
             * @param type 交换机类型
             * @return 当前构建器实例，支持链式调用
             */
            public Builder type(String type) {
                exchange.type = type;
                return self;
            }

            /**
             * 设置路由键
             *
             * @param routingKey 路由键
             * @return 当前构建器实例，支持链式调用
             */
            public Builder routingKey(String routingKey) {
                exchange.routingKey = routingKey;
                return self;
            }

            /**
             * 配置交换机
             * <p>
             * 将指定的交换机配置合并到当前交换机配置中。
             * </p>
             *
             * @param queue 交换机配置
             * @return 当前构建器实例，支持链式调用
             */
            public Builder config(Exchange queue) {
                this.exchange = this.exchange.merge(queue);
                return self;
            }

            /**
             * 构建交换机配置实例
             *
             * @return 交换机配置实例
             */
            @Override
            public Exchange build() {
                return exchange;
            }
        }
    }

    /**
     * 消息属性配置内部类
     * <p>
     * 用于封装 RabbitMQ 消息属性的相关配置信息。
     * </p>
     */
    public static class Props implements ConfigureItem<Props> {

        /**
         * 内容类型
         */
        @JSONField(name = PROPS_CONTENT_TYPE)
        protected String contentType;
        
        /**
         * 内容编码
         */
        @JSONField(name = PROPS_CONTENT_ENCODING, ordinal = 1)
        protected String contentEncoding;
        
        /**
         * 头部信息
         */
        @JSONField(name = PROPS_HEADERS, ordinal = 2)
        protected Map<String, Object> headers;
        
        /**
         * 传递模式
         */
        @JSONField(name = PROPS_DELIVERY_MODE, ordinal = 3)
        protected Integer deliveryMode;
        
        /**
         * 优先级
         */
        @JSONField(name = PROPS_PRIORITY, ordinal = 4)
        protected Integer priority;
        
        /**
         * 关联ID
         */
        @JSONField(name = PROPS_CORRELATION_ID, ordinal = 5)
        protected String correlationId;
        
        /**
         * 回复地址
         */
        @JSONField(name = PROPS_REPLY_TO, ordinal = 6)
        protected String replyTo;
        
        /**
         * 过期时间
         */
        @JSONField(name = PROPS_EXPIRATION, ordinal = 7)
        protected String expiration;
        
        /**
         * 消息ID
         */
        @JSONField(name = PROPS_MESSAGE_ID, ordinal = 8)
        protected String messageId;
        
        /**
         * 类型
         */
        @JSONField(name = PROPS_TIMESTAMP, ordinal = 9)
        protected String type;
        
        /**
         * 用户ID
         */
        @JSONField(name = PROPS_USER_ID, ordinal = 11)
        protected String userId;
        
        /**
         * 应用ID
         */
        @JSONField(name = PROPS_APP_ID, ordinal = 12)
        protected String appId;
        
        /**
         * 集群ID
         */
        @JSONField(name = PROPS_CLUSTER_ID, ordinal = 13)
        protected String clusterId;

        /**
         * 默认构造函数
         */
        public Props() {
        }

        /**
         * 合并消息属性配置
         * <p>
         * 将当前消息属性配置与另一个消息属性配置合并，优先使用当前配置的值。
         * </p>
         *
         * @param other 另一个消息属性配置
         * @return 合并后的新消息属性配置
         */
        @Override
        public Props merge(Props other) {
            if (other == null) {
                return copy();
            }
            var localOther = other.copy();
            var self = copy();
            self.contentType = StringUtils.isBlank(contentType) ? localOther.contentType : self.contentType;
            self.contentEncoding = StringUtils.isBlank(contentEncoding) ? localOther.contentEncoding : self.contentEncoding;
            self.headers = Objects.isNull(headers) || headers.isEmpty() ? localOther.headers : self.headers;
            self.deliveryMode = self.deliveryMode == null ? localOther.deliveryMode : self.deliveryMode;
            self.priority = self.priority == null ? localOther.priority : self.priority;
            self.correlationId = StringUtils.isBlank(correlationId) ? localOther.correlationId : self.correlationId;
            self.replyTo = StringUtils.isBlank(replyTo) ? localOther.replyTo : self.replyTo;
            self.expiration = StringUtils.isBlank(expiration) ? localOther.expiration : self.expiration;
            self.messageId = StringUtils.isBlank(messageId) ? localOther.messageId : self.messageId;
            self.type = StringUtils.isBlank(type) ? localOther.type : self.type;
            self.userId = StringUtils.isBlank(userId) ? localOther.userId : self.userId;
            self.appId = StringUtils.isBlank(appId) ? localOther.appId : self.appId;
            self.clusterId = StringUtils.isBlank(clusterId) ? localOther.clusterId : self.clusterId;
            return self;
        }

        /**
         * 在上下文中评估消息属性配置
         * <p>
         * 使用上下文中的变量替换消息属性配置中的变量占位符。
         * </p>
         *
         * @param context 上下文包装器
         * @return 评估后的消息属性配置
         */
        @Override
        public Props evaluate(ContextWrapper context) {
            contentType = (String) context.evaluate(contentType);
            contentEncoding = (String) context.evaluate(contentEncoding);
            headers = (Map<String, Object>) context.evaluate(headers);
            deliveryMode = (Integer) context.evaluate(deliveryMode);
            priority = (Integer) context.evaluate(priority);
            correlationId = (String) context.evaluate(correlationId);
            replyTo = (String) context.evaluate(replyTo);
            expiration = (String) context.evaluate(expiration);
            messageId = (String) context.evaluate(messageId);
            type = (String) context.evaluate(type);
            userId = (String) context.evaluate(userId);
            appId = (String) context.evaluate(appId);
            clusterId = (String) context.evaluate(clusterId);
            return this;
        }

        /**
         * 获取内容类型
         *
         * @return 内容类型
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * 设置内容类型
         *
         * @param contentType 内容类型
         */
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        /**
         * 获取内容编码
         *
         * @return 内容编码
         */
        public String getContentEncoding() {
            return contentEncoding;
        }

        /**
         * 设置内容编码
         *
         * @param contentEncoding 内容编码
         */
        public void setContentEncoding(String contentEncoding) {
            this.contentEncoding = contentEncoding;
        }

        /**
         * 获取头部信息
         *
         * @return 头部信息
         */
        public Map<String, Object> getHeaders() {
            return headers;
        }

        /**
         * 设置头部信息
         *
         * @param headers 头部信息
         */
        public void setHeaders(Map<String, Object> headers) {
            this.headers = headers;
        }

        /**
         * 获取传递模式
         *
         * @return 传递模式
         */
        public Integer getDeliveryMode() {
            return deliveryMode;
        }

        /**
         * 设置传递模式
         *
         * @param deliveryMode 传递模式
         */
        public void setDeliveryMode(Integer deliveryMode) {
            this.deliveryMode = deliveryMode;
        }

        /**
         * 获取优先级
         *
         * @return 优先级
         */
        public Integer getPriority() {
            return priority;
        }

        /**
         * 设置优先级
         *
         * @param priority 优先级
         */
        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        /**
         * 获取关联ID
         *
         * @return 关联ID
         */
        public String getCorrelationId() {
            return correlationId;
        }

        /**
         * 设置关联ID
         *
         * @param correlationId 关联ID
         */
        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }

        /**
         * 获取回复地址
         *
         * @return 回复地址
         */
        public String getReplyTo() {
            return replyTo;
        }

        /**
         * 设置回复地址
         *
         * @param replyTo 回复地址
         */
        public void setReplyTo(String replyTo) {
            this.replyTo = replyTo;
        }

        /**
         * 获取过期时间
         *
         * @return 过期时间
         */
        public String getExpiration() {
            return expiration;
        }

        /**
         * 设置过期时间
         *
         * @param expiration 过期时间
         */
        public void setExpiration(String expiration) {
            this.expiration = expiration;
        }

        /**
         * 获取消息ID
         *
         * @return 消息ID
         */
        public String getMessageId() {
            return messageId;
        }

        /**
         * 设置消息ID
         *
         * @param messageId 消息ID
         */
        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        /**
         * 获取类型
         *
         * @return 类型
         */
        public String getType() {
            return type;
        }

        /**
         * 设置类型
         *
         * @param type 类型
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * 获取用户ID
         *
         * @return 用户ID
         */
        public String getUserId() {
            return userId;
        }

        /**
         * 设置用户ID
         *
         * @param userId 用户ID
         */
        public void setUserId(String userId) {
            this.userId = userId;
        }

        /**
         * 获取应用ID
         *
         * @return 应用ID
         */
        public String getAppId() {
            return appId;
        }

        /**
         * 设置应用ID
         *
         * @param appId 应用ID
         */
        public void setAppId(String appId) {
            this.appId = appId;
        }

        /**
         * 获取集群ID
         *
         * @return 集群ID
         */
        public String getClusterId() {
            return clusterId;
        }

        /**
         * 设置集群ID
         *
         * @param clusterId 集群ID
         */
        public void setClusterId(String clusterId) {
            this.clusterId = clusterId;
        }

        /**
         * 消息属性配置构建器
         * <p>
         * 用于构建消息属性配置的内部构建器类。
         * </p>
         */
        public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, Props> {

            /**
             * 消息属性配置实例
             */
            private Props props = new Props();

            /**
             * 创建构建器实例
             * <p>
             * 工厂方法，用于创建 Props.Builder 构建器实例。
             * </p>
             *
             * @return Props.Builder 实例
             */
            public static Builder builder() {
                return new Builder();
            }

            /**
             * 设置内容类型
             *
             * @param contentType 内容类型
             * @return 当前构建器实例，支持链式调用
             */
            public Builder contentType(String contentType) {
                this.props.contentType = contentType;
                return self;
            }

            /**
             * 设置内容编码
             *
             * @param contentEncoding 内容编码
             * @return 当前构建器实例，支持链式调用
             */
            public Builder contentEncoding(String contentEncoding) {
                this.props.contentEncoding = contentEncoding;
                return self;
            }

            /**
             * 设置头部信息
             *
             * @param headers 头部信息
             * @return 当前构建器实例，支持链式调用
             */
            public Builder headers(Map<String, Object> headers) {
                this.props.headers = headers;
                return self;
            }

            /**
             * 设置传递模式
             *
             * @param deliveryMode 传递模式
             * @return 当前构建器实例，支持链式调用
             */
            public Builder deliveryMode(Integer deliveryMode) {
                this.props.deliveryMode = deliveryMode;
                return self;
            }

            /**
             * 设置优先级
             *
             * @param priority 优先级
             * @return 当前构建器实例，支持链式调用
             */
            public Builder priority(Integer priority) {
                this.props.priority = priority;
                return self;
            }

            /**
             * 设置关联ID
             *
             * @param correlationId 关联ID
             * @return 当前构建器实例，支持链式调用
             */
            public Builder correlationId(String correlationId) {
                this.props.correlationId = correlationId;
                return self;
            }

            /**
             * 设置回复地址
             *
             * @param replyTo 回复地址
             * @return 当前构建器实例，支持链式调用
             */
            public Builder replyTo(String replyTo) {
                this.props.replyTo = replyTo;
                return self;
            }

            /**
             * 设置过期时间
             *
             * @param expiration 过期时间
             * @return 当前构建器实例，支持链式调用
             */
            public Builder expiration(String expiration) {
                this.props.expiration = expiration;
                return self;
            }

            /**
             * 设置消息ID
             *
             * @param messageId 消息ID
             * @return 当前构建器实例，支持链式调用
             */
            public Builder messageId(String messageId) {
                this.props.messageId = messageId;
                return self;
            }

            /**
             * 设置类型
             *
             * @param type 类型
             * @return 当前构建器实例，支持链式调用
             */
            public Builder type(String type) {
                this.props.type = type;
                return self;
            }

            /**
             * 设置用户ID
             *
             * @param userId 用户ID
             * @return 当前构建器实例，支持链式调用
             */
            public Builder userId(String userId) {
                this.props.userId = userId;
                return self;
            }

            /**
             * 设置应用ID
             *
             * @param appId 应用ID
             * @return 当前构建器实例，支持链式调用
             */
            public Builder appId(String appId) {
                this.props.appId = appId;
                return self;
            }

            /**
             * 设置集群ID
             *
             * @param clusterId 集群ID
             * @return 当前构建器实例，支持链式调用
             */
            public Builder clusterId(String clusterId) {
                this.props.clusterId = clusterId;
                return self;
            }

            /**
             * 配置消息属性
             * <p>
             * 将指定的消息属性配置合并到当前消息属性配置中。
             * </p>
             *
             * @param props 消息属性配置
             * @return 当前构建器实例，支持链式调用
             */
            public Builder config(Props props) {
                this.props = this.props.merge(props);
                return self;
            }

            /**
             * 构建消息属性配置实例
             *
             * @return 消息属性配置实例
             */
            @Override
            public Props build() {
                return props;
            }
        }
    }

    /**
     * RabbitMQ 配置属性构建器
     * <p>
     * 用于构建 RabbitMQ 配置项的构建器类。
     * </p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, RabbitConfigureItem> {

        /**
         * RabbitMQ 配置项实例
         */
        private RabbitConfigureItem configure = new RabbitConfigureItem();

        /**
         * 设置引用名称
         *
         * @param ref 引用名称
         * @return 当前构建器实例，支持链式调用
         */
        public Builder ref(String ref) {
            this.configure.ref = ref;
            return self;
        }

        /**
         * 设置虚拟主机
         *
         * @param virtualHost 虚拟主机
         * @return 当前构建器实例，支持链式调用
         */
        public Builder virtualHost(String virtualHost) {
            this.configure.virtualHost = virtualHost;
            return self;
        }

        /**
         * 设置主机地址
         *
         * @param host 主机地址
         * @return 当前构建器实例，支持链式调用
         */
        public Builder host(String host) {
            this.configure.host = host;
            return self;
        }

        /**
         * 设置端口号
         *
         * @param port 端口号
         * @return 当前构建器实例，支持链式调用
         */
        public Builder port(String port) {
            this.configure.port = port;
            return self;
        }

        /**
         * 设置用户名
         *
         * @param username 用户名
         * @return 当前构建器实例，支持链式调用
         */
        public Builder username(String username) {
            this.configure.username = username;
            return self;
        }

        /**
         * 设置密码
         *
         * @param password 密码
         * @return 当前构建器实例，支持链式调用
         */
        public Builder password(String password) {
            this.configure.password = password;
            return self;
        }

        /**
         * 设置消息内容（使用自定义器）
         *
         * @param type       消息类型
         * @param customizer 自定义器
         * @param <T>        消息类型
         * @return 当前构建器实例，支持链式调用
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
         * 设置消息内容（使用对象）
         *
         * @param message 消息内容
         * @return 当前构建器实例，支持链式调用
         */
        public Builder message(Object message) {
            this.configure.message = message;
            return self;
        }

        /**
         * 设置消息内容（使用列表）
         *
         * @param message 消息内容列表
         * @return 当前构建器实例，支持链式调用
         */
        public Builder message(List<?> message) {
            this.configure.message = message;
            return self;
        }

        /**
         * 设置消息内容（使用映射）
         *
         * @param message 消息内容映射
         * @return 当前构建器实例，支持链式调用
         */
        public Builder message(Map<?, ?> message) {
            this.configure.message = message;
            return self;
        }

        /**
         * 设置连接超时时间
         *
         * @param timeout 连接超时时间（毫秒）
         * @return 当前构建器实例，支持链式调用
         */
        public Builder timeout(Integer timeout) {
            this.configure.timeout = timeout;
            return self;
        }

        /**
         * 设置队列配置
         *
         * @param queue 队列配置
         * @return 当前构建器实例，支持链式调用
         */
        public Builder queue(Queue queue) {
            this.configure.queue = queue;
            return self;
        }

        /**
         * 设置队列配置（使用构建器）
         *
         * @param builder 队列配置构建器
         * @return 当前构建器实例，支持链式调用
         */
        public Builder queue(Queue.Builder builder) {
            this.configure.queue = builder.build();
            return self;
        }

        /**
         * 设置队列配置（使用消费者）
         *
         * @param consumer 队列配置消费者
         * @return 当前构建器实例，支持链式调用
         */
        public Builder queue(Consumer<Queue.Builder> consumer) {
            var builder = Queue.Builder.builder();
            consumer.accept(builder);
            this.configure.queue = builder.build();
            return self;
        }

        /**
         * 设置队列配置（使用 Groovy Closure）
         *
         * @param closure Groovy Closure，用于自定义队列配置构建器
         * @return 当前构建器实例，支持链式调用
         */
        public Builder queue(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = Queue.Builder.class) Closure<?> closure) {
            var builder = Queue.Builder.builder();
            Groovy.call(closure, builder);
            this.configure.queue = builder.build();
            return self;
        }

        /**
         * 设置交换机配置
         *
         * @param exchange 交换机配置
         * @return 当前构建器实例，支持链式调用
         */
        public Builder exchange(Exchange exchange) {
            this.configure.exchange = exchange;
            return self;
        }

        /**
         * 设置交换机配置（使用构建器）
         *
         * @param builder 交换机配置构建器
         * @return 当前构建器实例，支持链式调用
         */
        public Builder exchange(Exchange.Builder builder) {
            this.configure.exchange = builder.build();
            return self;
        }

        /**
         * 设置交换机配置（使用消费者）
         *
         * @param consumer 交换机配置消费者
         * @return 当前构建器实例，支持链式调用
         */
        public Builder exchange(Consumer<Exchange.Builder> consumer) {
            var builder = Exchange.Builder.builder();
            consumer.accept(builder);
            this.configure.exchange = builder.build();
            return self;
        }

        /**
         * 设置交换机配置（使用 Groovy Closure）
         *
         * @param closure Groovy Closure，用于自定义交换机配置构建器
         * @return 当前构建器实例，支持链式调用
         */
        public Builder exchange(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = Exchange.Builder.class) Closure<?> closure) {
            var builder = Exchange.Builder.builder();
            Groovy.call(closure, builder);
            this.configure.exchange = builder.build();
            return self;
        }

        /**
         * 设置消息属性配置
         *
         * @param props 消息属性配置
         * @return 当前构建器实例，支持链式调用
         */
        public Builder props(Props props) {
            this.configure.props = props;
            return self;
        }

        /**
         * 设置消息属性配置（使用构建器）
         *
         * @param builder 消息属性配置构建器
         * @return 当前构建器实例，支持链式调用
         */
        public Builder props(Props.Builder builder) {
            this.configure.props = builder.build();
            return self;
        }

        /**
         * 设置消息属性配置（使用消费者）
         *
         * @param consumer 消息属性配置消费者
         * @return 当前构建器实例，支持链式调用
         */
        public Builder props(Consumer<Props.Builder> consumer) {
            var builder = Props.Builder.builder();
            consumer.accept(builder);
            this.configure.props = builder.build();
            return self;
        }

        /**
         * 设置消息属性配置（使用 Groovy Closure）
         *
         * @param closure Groovy Closure，用于自定义消息属性配置构建器
         * @return 当前构建器实例，支持链式调用
         */
        public Builder props(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = Props.Builder.class) Closure<?> closure) {
            var builder = Props.Builder.builder();
            Groovy.call(closure, builder);
            this.configure.props = builder.build();
            return self;
        }

        /**
         * 配置 RabbitMQ 配置项
         * <p>
         * 将指定的 RabbitMQ 配置项合并到当前配置项中。
         * </p>
         *
         * @param config RabbitMQ 配置项
         * @return 当前构建器实例，支持链式调用
         */
        public Builder config(RabbitConfigureItem config) {
            this.configure = configure.merge(config);
            return self;
        }

        /**
         * 构建 RabbitMQ 配置项实例
         *
         * @return RabbitMQ 配置项实例
         */
        @Override
        public RabbitConfigureItem build() {
            return configure;
        }
    }

}