package io.github.xiaomisum.ryze.coap.example.code;

import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.coap.example.CoapTestListener;
import io.github.xiaomisum.ryze.protocol.coap.builder.CoapConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.coap.builder.CoapSamplersBuilder;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.github.xiaomisum.ryze.protocol.coap.Coap.coap;
import static io.github.xiaomisum.ryze.protocol.coap.Coap.get;
import static io.github.xiaomisum.ryze.protocol.coap.Coap.post;

@Listeners(CoapTestListener.class)
public class CodeTestCase {

    @Test
    @RyzeTest
    public void test1() {
        Ryze.suite("CoAP测试用例", suite -> {
            suite.configureElements(CoapConfigureElementsBuilder.class, ele -> ele.coap(coap -> coap
                    .config(config -> config.uri("coap://127.0.0.1:5683").confirmable(true).timeout(10000))));
            suite.children(CoapSamplersBuilder.class, child -> child.coap(c -> c.title("步骤1——POST创建资源")
                    .config(config -> config.uri("coap://127.0.0.1:5683/test").post()
                            .contentFormat("application/json").payload("{\"key\": \"value\"}"))));
            suite.children(CoapSamplersBuilder.class, child -> child.coap(c -> c.title("步骤2——GET获取资源")
                    .config(config -> config.uri("coap://127.0.0.1:5683/test").get())));
        });
    }

    @Test
    @RyzeTest
    public void test2() {
        get("CoAP GET 请求", sampler -> {
            sampler.config(config -> config.uri("coap://127.0.0.1:5683/test").confirmable(true).timeout(10000));
        });
    }

    @Test
    @RyzeTest
    public void test3() {
        post("CoAP POST 请求", sampler -> {
            sampler.config(config -> config.uri("coap://127.0.0.1:5683/test")
                    .contentFormat("application/json").payload("{\"key\": \"value\"}")
                    .confirmable(true).timeout(10000));
        });
    }
}
