package io.github.xiaomisum.ryze.coap.example;

import org.testng.ITestContext;
import org.testng.ITestListener;

/**
 * CoAP 测试监听器
 * 在测试套件开始前启动 CoapConsumer，测试套件结束后停止 CoapConsumer
 * 
 * @author xiaomi
 */
public class CoapTestListener implements ITestListener {

    private static CoapConsumer coapConsumer;

    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n========== [CoAP Test Listener] 开始启动 CoAP 服务器 ==========");
        try {
            coapConsumer = new CoapConsumer();
            coapConsumer.start();
            System.out.println("========== [CoAP Test Listener] CoAP 服务器启动成功 ==========\n");
        } catch (Exception e) {
            System.err.println("[CoAP Test Listener] CoAP 服务器启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n========== [CoAP Test Listener] 开始停止 CoAP 服务器 ==========");
        try {
            if (coapConsumer != null) {
                coapConsumer.stop();
                coapConsumer = null;
                System.out.println("========== [CoAP Test Listener] CoAP 服务器已停止 ==========\n");
            }
        } catch (Exception e) {
            System.err.println("[CoAP Test Listener] CoAP 服务器停止失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
