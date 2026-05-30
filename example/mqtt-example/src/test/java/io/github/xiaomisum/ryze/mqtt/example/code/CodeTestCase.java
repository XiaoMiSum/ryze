package io.github.xiaomisum.ryze.mqtt.example.code;

import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.mqtt.example.MqttTestListener;
import io.github.xiaomisum.ryze.protocol.mqtt.builder.MqttConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.mqtt.builder.MqttSamplersBuilder;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.github.xiaomisum.ryze.protocol.mqtt.Mqtt.publish;

@Listeners(MqttTestListener.class)
public class CodeTestCase {

    @Test
    @RyzeTest
    public void test1() {
        Ryze.suite("MQTT测试用例", suite -> {
            suite.configureElements(MqttConfigureElementsBuilder.class, ele -> ele.mqtt(mqtt -> mqtt.refName("mqtt_source")
                    .config(config -> config.broker("127.0.0.1").port(1883).clientId("ryze-test-client").cleanSession(true).keepAlive(60))));
            suite.children(MqttSamplersBuilder.class, child -> child.publish(pub -> pub.title("步骤1——发布消息")
                    .config(config -> config.datasource("mqtt_source").topic("test/topic").qos(1).payload("{\"message\": \"hello from ryze\"}"))));
        });
    }

    @Test
    @RyzeTest
    public void test2() {
        publish("发布测试消息", sampler -> {
            sampler.configureElements(MqttConfigureElementsBuilder.class, ele -> ele.mqtt(mqtt -> mqtt.refName("mqtt_source")
                    .config(config -> config.broker("127.0.0.1").port(1883).clientId("ryze-test-pub"))));
            sampler.config(config -> config.datasource("mqtt_source").topic("test/topic").qos(1).payload("{\"message\": \"publish test\"}"));
        });
    }
}
