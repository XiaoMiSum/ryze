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

package io.github.xiaomisum.ryze.protocol.rabbit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.protocol.rabbit.config.RabbitConfigureItem;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

import static io.github.xiaomisum.ryze.protocol.rabbit.RabbitConstantsInterface.EXCHANGE_TYPE_DIRECT;
import static io.github.xiaomisum.ryze.protocol.rabbit.RabbitConstantsInterface.EXCHANGE_TYPE_FANOUT;

/**
 * RabbitMQ 操作工具类
 * <p>
 * 该类提供 RabbitMQ 消息发送的核心功能，包括连接管理、队列声明、交换机声明、消息发布等功能。
 * 主要用于执行 RabbitMQ 消息发送操作，并处理相关配置。
 * </p>
 *
 * @author xiaomi
 * @since 2025/7/26 22:24
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Rabbit {

    /**
     * 执行 RabbitMQ 消息发送操作
     * <p>
     * 该方法负责建立与 RabbitMQ 服务器的连接，声明队列和交换机（如果配置了交换机），
     * 并将指定的消息发布到队列中。执行完成后会关闭连接和通道。
     * </p>
     *
     * @param factory 连接工厂，用于创建与 RabbitMQ 服务器的连接
     * @param config  RabbitMQ 配置项，包含连接信息、队列配置、交换机配置等
     * @param message 要发送的消息内容
     * @param result  采样结果对象，用于记录执行时间和结果状态
     */
    public static void execute(ConnectionFactory factory, RabbitConfigureItem config, String message, DefaultSampleResult result) {
        try (var connection = factory.newConnection(); var channel = connection.createChannel()) {
            var queue = config.getQueue();
            channel.queueDeclare(queue.getName(), queue.getDurable(), queue.getExclusive(), queue.getAutoDelete(), queue.getArguments());
            result.sampleStart();
            var properties = getBasicProperties(config);
            var exchange = config.getExchange();
            var exchangeName = Objects.isNull(exchange) ? "" : exchange.getName();
            var routingKey = Objects.isNull(exchange) ? null : exchange.getRoutingKey();
            if (Objects.nonNull(exchange)) {
                channel.exchangeDeclare(exchangeName,
                        switch (exchange.getType()) {
                            case EXCHANGE_TYPE_FANOUT -> BuiltinExchangeType.FANOUT;
                            case EXCHANGE_TYPE_DIRECT -> BuiltinExchangeType.DIRECT;
                            default -> BuiltinExchangeType.TOPIC;
                        });
                routingKey = EXCHANGE_TYPE_FANOUT.equals(exchange.getType()) ? null : exchange.getRoutingKey();
                channel.queueBind(exchange.getName(), exchange.getType(), routingKey);
            }
            channel.basicPublish(exchangeName, queue.getName(), properties, message.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            result.sampleEnd();
        }
    }

    /**
     * 根据配置获取消息的基本属性
     * <p>
     * 根据提供的 RabbitMQ 配置项构造 AMQP.BasicProperties 对象，
     * 该对象包含消息的各种属性，如内容类型、编码、优先级等。
     * </p>
     *
     * @param config RabbitMQ 配置项
     * @return AMQP.BasicProperties 对象，如果配置中没有属性设置则返回 null
     */
    private static AMQP.BasicProperties getBasicProperties(RabbitConfigureItem config) {
        AMQP.BasicProperties properties = null;
        if (Objects.nonNull(config.getProps())) {
            properties = new AMQP.BasicProperties(
                    config.getProps().getContentType(),
                    config.getProps().getContentEncoding(),
                    config.getProps().getHeaders(),
                    config.getProps().getDeliveryMode(),
                    config.getProps().getPriority(),
                    config.getProps().getCorrelationId(),
                    config.getProps().getReplyTo(),
                    config.getProps().getExpiration(),
                    config.getProps().getMessageId(),
                    new Date(),
                    config.getProps().getType(),
                    config.getProps().getUserId(),
                    config.getProps().getAppId(),
                    config.getProps().getClusterId()
            );
        }
        return properties;
    }

    /**
     * 根据配置处理 RabbitMQ 连接请求
     * <p>
     * 根据提供的 RabbitMQ 配置项创建并配置 ConnectionFactory 对象，
     * 用于建立与 RabbitMQ 服务器的连接。
     * </p>
     *
     * @param config RabbitMQ 配置项，包含连接所需的各种参数
     * @return 配置好的 ConnectionFactory 对象
     */
    public static ConnectionFactory handleRequest(RabbitConfigureItem config) {
        var factory = new ConnectionFactory();
        factory.setConnectionTimeout(config.getTimeout());
        factory.setVirtualHost(config.getVirtualHost());
        factory.setHost(config.getHost());
        factory.setPort(Integer.parseInt(config.getPort()));
        factory.setUsername(config.getUsername());
        factory.setPassword(config.getPassword());
        return factory;
    }
}