package io.github.xiaomisum.ryze.kafka.example;

import org.testng.ITestContext;
import org.testng.ITestListener;

/**
 * Kafka 测试监听器
 * 在测试套件开始前启动 Kafka Consumer，测试套件结束后停止 Kafka Consumer
 * 
 * @author xiaomi
 */
public class KafkaTestListener implements ITestListener {

    private static Consumer kafkaConsumer;

    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n========== [Kafka Test Listener] 开始启动 Kafka 消费者 ==========");
        try {
            kafkaConsumer = new Consumer();
            kafkaConsumer.start();
            System.out.println("========== [Kafka Test Listener] Kafka 消费者启动成功 ==========\n");
        } catch (Exception e) {
            System.err.println("[Kafka Test Listener] Kafka 消费者启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n========== [Kafka Test Listener] 开始停止 Kafka 消费者 ==========");
        try {
            if (kafkaConsumer != null) {
                kafkaConsumer.stop();
                kafkaConsumer = null;
                System.out.println("========== [Kafka Test Listener] Kafka 消费者已停止 ==========\n");
            }
        } catch (Exception e) {
            System.err.println("[Kafka Test Listener] Kafka 消费者停止失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
