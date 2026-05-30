package io.github.xiaomisum.ryze.mqtt.example;

import org.testng.ITestContext;
import org.testng.ITestListener;

/**
 * MQTT 测试监听器
 * 在测试套件开始前启动 MQTT Consumer，测试套件结束后停止 MQTT Consumer
 * 
 * @author xiaomi
 */
public class MqttTestListener implements ITestListener {

    private static MqttConsumer mqttConsumer;

    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n========== [MQTT Test Listener] 开始启动 MQTT 消费者 ==========");
        try {
            mqttConsumer = new MqttConsumer();
            mqttConsumer.start();
            System.out.println("========== [MQTT Test Listener] MQTT 消费者启动成功 ==========\n");
        } catch (Exception e) {
            System.err.println("[MQTT Test Listener] MQTT 消费者启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n========== [MQTT Test Listener] 开始停止 MQTT 消费者 ==========");
        try {
            if (mqttConsumer != null) {
                mqttConsumer.stop();
                mqttConsumer = null;
                System.out.println("========== [MQTT Test Listener] MQTT 消费者已停止 ==========\n");
            }
        } catch (Exception e) {
            System.err.println("[MQTT Test Listener] MQTT 消费者停止失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
