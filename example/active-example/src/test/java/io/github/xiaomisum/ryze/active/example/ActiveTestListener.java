package io.github.xiaomisum.ryze.active.example;

import org.testng.ITestContext;
import org.testng.ITestListener;

/**
 * ActiveMQ 测试监听器
 * 在测试套件开始前启动 ActiveMQ Consumer，测试套件结束后停止 ActiveMQ Consumer
 * 
 * @author xiaomi
 */
public class ActiveTestListener implements ITestListener {

    private static Consumer activeConsumer;

    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n========== [ActiveMQ Test Listener] 开始启动 ActiveMQ 消费者 ==========");
        try {
            activeConsumer = new Consumer();
            activeConsumer.start();
            System.out.println("========== [ActiveMQ Test Listener] ActiveMQ 消费者启动成功 ==========\n");
        } catch (Exception e) {
            System.err.println("[ActiveMQ Test Listener] ActiveMQ 消费者启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n========== [ActiveMQ Test Listener] 开始停止 ActiveMQ 消费者 ==========");
        try {
            if (activeConsumer != null) {
                activeConsumer.stop();
                activeConsumer = null;
                System.out.println("========== [ActiveMQ Test Listener] ActiveMQ 消费者已停止 ==========\n");
            }
        } catch (Exception e) {
            System.err.println("[ActiveMQ Test Listener] ActiveMQ 消费者停止失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
