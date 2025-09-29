package io.github.xiaomisum.ryze.active.example.code;

import io.github.xiaomisum.ryze.protocol.active.ActiveMagicBox;
import io.github.xiaomisum.ryze.protocol.active.builder.ActiveConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.active.builder.ActivePostprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.active.builder.ActivePreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.active.builder.ActiveSamplersBuilder;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class CodeTestCase {

    @Test
    @RyzeTest
    public void test1() {
        ActiveMagicBox.suite("测试用例", suite -> {
            suite.variables("id", 1);
            suite.variables(var -> var.put("tick", "active_preprocessor"));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.configureElements(ActiveConfigureElementsBuilder.class, builder -> builder
                    .active(active -> active.config(config -> config.username("artemis").password("artemis").topic("ryze.topic").brokerUrl("tcp://127.0.0.1:61616")))
            );
            suite.preprocessors(ActivePreprocessorsBuilder.class, builder -> builder
                    .active(active -> active.config(config ->
                            config.message(Map.of("username", "${tick}", "age", 18))
                    ))
            );
            suite.postprocessors(ActivePostprocessorsBuilder.class, builder -> builder
                    .active(active -> active.config(config -> config.message("test1: active_postprocessor_test ")))
            );
            suite.children(ActiveSamplersBuilder.class, builder -> builder
                    .active(active -> active.title("步骤1").variables("username", "ryze")
                            .config(config -> config.message(Map.of("name", "${username}  步骤1：标准active取样器"))))
            );
            suite.children(ActiveSamplersBuilder.builder()
                    .active(active -> active.title("步骤2").variables("username", "ryze")
                            .config(config -> config.message(Map.of("name", "${username}  步骤2：标准active取样器"))))
                    .build());
        });
    }

    @Test
    @RyzeTest
    public void test2() {
        ActiveMagicBox.active("测试用例- test2()", sampler -> {
            sampler.configureElements(ActiveConfigureElementsBuilder.class, builder -> builder
                    .active(active -> active.config(config -> config.username("artemis").password("artemis").topic("ryze.topic").brokerUrl("tcp://127.0.0.1:61616")))
            );
            sampler.preprocessors(ActivePreprocessorsBuilder.class, builder -> builder
                    .active(active -> active.config(config ->
                            config.message(Map.of("username", "${tick}", "age", 18))
                    ))
            );
            sampler.config(config -> config.message(123456));
        });
    }


    @Test
    @RyzeTest
    public void test3() {
        ActiveMagicBox.active("测试用例- test3()-1", sampler -> {
            sampler.configureElements(ActiveConfigureElementsBuilder.class, builder -> builder
                    .active(active -> active.config(config -> config.username("artemis").password("artemis").topic("ryze.topic").brokerUrl("tcp://127.0.0.1:61616")))
            );
            sampler.config(config -> config.message(List.of(1, 2, 3, 4)));
        });


        ActiveMagicBox.active("测试用例- test3()-2", sampler -> {
            sampler.configureElements(ActiveConfigureElementsBuilder.class, builder -> builder
                    .active(active -> active.config(config -> config.username("artemis").password("artemis").topic("ryze.topic").brokerUrl("tcp://127.0.0.1:61616")))
            );
            sampler.config(config -> config.message(true));
        });
    }
}
