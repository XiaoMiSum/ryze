package io.github.xiaomisum.ryze.rabbit.example;


import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class Consumer {

    private static final String host = "localhost";
    private com.rabbitmq.client.Connection connection;
    private com.rabbitmq.client.Channel channel;

    /**
     * 启动 RabbitMQ 消费者
     */
    public void start() throws Exception {
        // 创建连接工厂
        var factory = new ConnectionFactory();
        factory.setHost(host); // RabbitMQ服务器地址
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        // 创建连接
        connection = factory.newConnection();
        // 创建通道
        channel = connection.createChannel();
        System.out.println("[RabbitMQ Consumer] 已启动，等待消息...");
        // 定义回调函数
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[RabbitMQ Consumer] 接收到消息: " + message);
        };
        // 开始消费消息（不声明队列，直接消费，让测试用例负责声明）
        try {
            channel.basicConsume("ryze.topic", true, deliverCallback, consumerTag -> {
            });
        } catch (Exception e) {
            System.out.println("[RabbitMQ Consumer] 消费失败，队列可能不存在: " + e.getMessage());
        }
    }

    /**
     * 停止 RabbitMQ 消费者
     */
    public void stop() {
        System.out.println("[RabbitMQ Consumer] 正在停止...");
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
            System.out.println("[RabbitMQ Consumer] 已停止");
        } catch (Exception e) {
            System.err.println("[RabbitMQ Consumer] 停止失败: " + e.getMessage());
        }
    }

    /**
     * standalone 模式启动（用于独立运行）
     */
    public static void main(String[] args) throws Exception {
        Consumer consumer = new Consumer();
        consumer.start();
        System.out.println("[RabbitMQ Consumer] 等待消息... (按 Ctrl+C 退出)");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[RabbitMQ Consumer] 正在关闭...");
            consumer.stop();
        }));

        Thread.currentThread().join();
    }
}
