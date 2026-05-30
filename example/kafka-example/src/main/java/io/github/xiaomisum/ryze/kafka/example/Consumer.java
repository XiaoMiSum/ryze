package io.github.xiaomisum.ryze.kafka.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class Consumer {
    private KafkaConsumer<String, String> consumer;
    private Thread consumerThread;
    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * 启动 Kafka 消费者
     */
    public void start() {
        //配置信息
        Properties props = new Properties();
        //kafka服务器地址
        props.put("bootstrap.servers", "localhost:9092");
        //必须指定消费者组
        props.put("group.id", "ryze");
        //设置数据key和value的序列化处理类
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", StringDeserializer.class);
        //创建消息者实例
        consumer = new KafkaConsumer<>(props);
        //订阅 ryze.topic 的消息
        consumer.subscribe(List.of("ryze.topic"));
        
        running.set(true);
        consumerThread = new Thread(() -> {
            System.out.println("[Kafka Consumer] 已启动，等待消息...");
            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("[Kafka Consumer] 收到消息 - key: " + record.key() + ", value: " + record.value());
                }
            }
        }, "kafka-consumer-thread");
        consumerThread.start();
    }

    /**
     * 停止 Kafka 消费者
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            System.out.println("[Kafka Consumer] 正在停止...");
            if (consumerThread != null) {
                consumerThread.interrupt();
                try {
                    consumerThread.join(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            // 在消费者线程中关闭，避免线程安全问题
            if (consumer != null) {
                consumer.wakeup(); // 唤醒 poll 操作
                try {
                    consumer.close();
                } catch (Exception e) {
                    // 忽略关闭时的异常
                }
            }
            System.out.println("[Kafka Consumer] 已停止");
        }
    }

    /**
     * standalone 模式启动（用于独立运行）
     */
    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        consumer.start();
        System.out.println("[Kafka Consumer] 等待消息... (按 Ctrl+C 退出)");
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[Kafka Consumer] 正在关闭...");
            consumer.stop();
        }));
        
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}