package io.github.xiaomisum.ryze.websocket.example.code;

import io.github.xiaomisum.ryze.protocol.websocket.builder.WebsocketConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.websocket.builder.WebsocketPreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.websocket.builder.WebsocketSamplersBuilder;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import static io.github.xiaomisum.ryze.MagicBox.suite;
import static io.github.xiaomisum.ryze.protocol.websocket.WebsocketMagicBox.ws;

public class CodeTestCase {

    @Test
    @RyzeTest
    public void test1() {
        suite("测试用例", suite -> {
            suite.configureElements(WebsocketConfigureElementsBuilder.class, ele -> ele.ws(ws -> ws.config(config -> config.protocol("ws").host("127.0.0.1").port("8080"))));
            suite.preprocessors(WebsocketPreprocessorsBuilder.class, pre -> pre.ws(ws ->
                    ws.title("前置处理器发送消息").config(config -> config.path("/ws/body/string").body("websocket postprocessor test"))));
            suite.children(WebsocketSamplersBuilder.class, child -> {
                child.ws(ws -> ws.title("步骤1——获取用户：id = ${id}")
                        .config(config -> config.path("/ws/body/string").body("websocket sampler test").responsePattern("websocket sampler test"))
                        .validators(validator -> validator.result("==", "websocket sampler test")));
            });
        });
    }

    @Test
    @RyzeTest
    public void test2() {
        ws("测试用例- test2()", sampler -> {
            sampler.configureElements(WebsocketConfigureElementsBuilder.class, ele -> ele.ws(ws -> ws.config(config -> config.protocol("ws").host("127.0.0.1").port("8080"))));
            sampler.preprocessors(WebsocketPreprocessorsBuilder.class, pre -> pre.ws(ws ->
                    ws.title("前置处理器发送消息").config(config -> config.path("/ws/body/string").body("websocket postprocessor test"))));
            sampler.config(config -> config.path("/ws/body/string").body("websocket sampler test").responsePattern("websocket sampler test"));
            sampler.validators(validator -> validator.result("==", "websocket sampler test"));
        });
    }
}
