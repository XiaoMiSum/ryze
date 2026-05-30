package io.github.xiaomisum.ryze.mqtt.example;

import org.eclipse.paho.client.mqttv3.*;

/**
 * MQTT 消费者示例
 * 用于接收 MQTT 消息，配合 Ryze 测试框架的 MQTT 生产者使用
 */
public class MqttConsumer {

    private static final String brokerUrl = "tcp://127.0.0.1:1883";
    private static final String clientId = "ryze-mqtt-consumer";
    private MqttClient client;

    /**
     * 启动 MQTT 消费者
     */
    public void start() throws Exception {
        client = new MqttClient(brokerUrl, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(60);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("[MQTT Consumer] 连接丢失: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload());
                System.out.println("[MQTT Consumer] 收到消息 - Topic: " + topic + ", Payload: " + payload);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // 发布消息完成回调
            }
        });

        client.connect(options);
        System.out.println("[MQTT Consumer] 已连接到: " + brokerUrl);

        // 订阅多个主题
        subscribe(client, "sensor/temperature", 1);
        subscribe(client, "device/status/#", 1);
        subscribe(client, "ryze/test/#", 1);
        subscribe(client, "test/preprocessor", 1);
        subscribe(client, "test/postprocessor", 1);
        
        System.out.println("[MQTT Consumer] 等待接收消息...");
    }

    /**
     * 停止 MQTT 消费者
     */
    public void stop() {
        if (client != null && client.isConnected()) {
            System.out.println("[MQTT Consumer] 正在停止...");
            try {
                client.disconnect();
                client.close();
                System.out.println("[MQTT Consumer] 已停止");
            } catch (MqttException e) {
                System.err.println("[MQTT Consumer] 停止失败: " + e.getMessage());
            }
        }
    }

    private void subscribe(MqttClient client, String topic, int qos) throws Exception {
        client.subscribe(topic, qos);
        System.out.println("[MQTT Consumer] 已订阅主题: " + topic + " (QoS: " + qos + ")");
    }

    /**
     * standalone 模式启动（用于独立运行）
     */
    public static void main(String[] args) throws Exception {
        MqttConsumer consumer = new MqttConsumer();
        consumer.start();
        System.out.println("[MQTT Consumer] 等待接收消息... (按 Ctrl+C 退出)");
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[MQTT Consumer] 正在关闭...");
            consumer.stop();
        }));
        
        Thread.currentThread().join();
    }
}
