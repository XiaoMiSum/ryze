package io.github.xiaomisum.ryze.rabbit.example;

import org.testng.ITestContext;
import org.testng.ITestListener;

/**
 * RabbitMQ 测试监听器
 * 在测试套件开始前启动 RabbitMQ Consumer，测试套件结束后停止 RabbitMQ Consumer
 * 
 * @author xiaomi
 */
public class RabbitTestListener implements ITestListener {

    private static Consumer rabbitConsumer;

    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n========== [RabbitMQ Test Listener] 开始启动 RabbitMQ 消费者 ==========");
        try {
            rabbitConsumer = new Consumer();
            rabbitConsumer.start();
            System.out.println("========== [RabbitMQ Test Listener] RabbitMQ 消费者启动成功 ==========\n");
        } catch (Exception e) {
            System.err.println("[RabbitMQ Test Listener] RabbitMQ 消费者启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n========== [RabbitMQ Test Listener] 开始停止 RabbitMQ 消费者 ==========");
        try {
            if (rabbitConsumer != null) {
                rabbitConsumer.stop();
                rabbitConsumer = null;
                System.out.println("========== [RabbitMQ Test Listener] RabbitMQ 消费者已停止 ==========\n");
            }
        } catch (Exception e) {
            System.err.println("[RabbitMQ Test Listener] RabbitMQ 消费者停止失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
