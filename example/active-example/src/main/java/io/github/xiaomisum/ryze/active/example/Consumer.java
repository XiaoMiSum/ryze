package io.github.xiaomisum.ryze.active.example;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer {

    private static final String brokerURL = "tcp://127.0.0.1:61616";
    private Connection connection;

    /**
     * 启动 ActiveMQ 消费者
     */
    public void start() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
        factory.setUserName("artemis");
        factory.setPassword("artemis");
        connection = factory.createConnection();
        connection.start();
        queue(connection);
        topic(connection);
        System.out.println("[ActiveMQ Consumer] 已启动，等待消息...");
    }

    /**
     * 停止 ActiveMQ 消费者
     */
    public void stop() {
        if (connection != null) {
            System.out.println("[ActiveMQ Consumer] 正在停止...");
            try {
                connection.close();
                System.out.println("[ActiveMQ Consumer] 已停止");
            } catch (JMSException e) {
                System.err.println("[ActiveMQ Consumer] 停止失败: " + e.getMessage());
            }
        }
    }

    public void queue(Connection connection) throws Exception {
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue("ryze.queue");
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(message -> {
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.println("[ActiveMQ Consumer] queue：" + textMessage.getText());
                textMessage.acknowledge();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void topic(Connection connection) throws JMSException {

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createTopic("ryze.topic");

        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(message -> {
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.println("[ActiveMQ Consumer] topic：" + textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * standalone 模式启动（用于独立运行）
     */
    public static void main(String[] args) throws Exception {
        Consumer consumer = new Consumer();
        consumer.start();
        System.out.println("[ActiveMQ Consumer] 等待消息... (按 Ctrl+C 退出)");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[ActiveMQ Consumer] 正在关闭...");
            consumer.stop();
        }));

        Thread.currentThread().join();
    }
}
