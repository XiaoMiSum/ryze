package io.github.xiaomisum.ryze.rabbit.example.code;

import io.github.xiaomisum.ryze.protocol.rabbit.RabbitMagicBox;
import io.github.xiaomisum.ryze.protocol.rabbit.builder.RabbitConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.rabbit.builder.RabbitPostprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.rabbit.builder.RabbitPreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.rabbit.builder.RabbitSamplersBuilder;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class CodeTestCase {

    @Test
    @RyzeTest
    public void test1() {
        RabbitMagicBox.suite("测试用例", suite -> {
            suite.variables("id", 1);
            suite.variables(var -> var.put("tick", "rabbit_preprocessor"));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.configureElements(RabbitConfigureElementsBuilder.class, builder -> builder
                    .rabbit(rabbit -> rabbit.config(config -> config.host("127.0.0.1").port("5672").username("guest").password("guest")))
            );
            suite.preprocessors(RabbitPreprocessorsBuilder.class, builder -> builder
                    .rabbit(rabbit -> rabbit.config(config -> config
                            .message(Map.of("username", "${tick}", "age", 18))
                            .queue(queue -> queue.name("ryze.topic"))))
            );
            suite.postprocessors(RabbitPostprocessorsBuilder.class, builder -> builder
                    .rabbit(rabbit -> rabbit.config(config -> config.message("test1: rabbit_postprocessor_test ")
                            .queue(queue -> queue.name("ryze.topic"))))
            );
            suite.children(RabbitSamplersBuilder.class, builder -> builder
                    .rabbit(rabbit -> rabbit.title("步骤1").variables("username", "ryze")
                            .config(config -> config.message(Map.of("name", "${username}  步骤1：标准rabbit取样器"))
                                    .queue(queue -> queue.name("ryze.topic"))))
            );
            suite.children(RabbitSamplersBuilder.builder()
                    .rabbit(rabbit -> rabbit.title("步骤2").variables("username", "ryze")
                            .config(config -> config.message(Map.of("name", "${username}  步骤2：标准rabbit取样器"))
                                    .queue(queue -> queue.name("ryze.topic"))))
                    .build());
        });
    }

    @Test
    @RyzeTest
    public void test2() {
        RabbitMagicBox.rabbit("测试用例- test2()", sampler -> {
            sampler.configureElements(RabbitConfigureElementsBuilder.class, builder -> builder
                    .rabbit(rabbit -> rabbit.config(config -> config.host("127.0.0.1").port("5672").username("guest").password("guest")))
            );
            sampler.preprocessors(RabbitPreprocessorsBuilder.class, builder -> builder
                    .rabbit(rabbit -> rabbit.config(config -> config
                            .message(Map.of("username", "${tick}", "age", 18))
                            .queue(queue -> queue.name("ryze.topic"))))
            );
            sampler.config(config -> config.message(123456).queue(queue -> queue.name("ryze.topic")));
        });
    }


    @Test
    @RyzeTest
    public void test3() {
        RabbitMagicBox.rabbit("测试用例- test3()-1", sampler -> {
            sampler.configureElements(RabbitConfigureElementsBuilder.class, builder -> builder
                    .rabbit(rabbit -> rabbit.config(config -> config.host("127.0.0.1").port("5672").username("guest").password("guest")))

            );
            sampler.config(config -> config.message(List.of(1, 2, 3, 4)).queue(queue -> queue.name("ryze.topic")));
        });


        RabbitMagicBox.rabbit("测试用例- test3()-2", sampler -> {
            sampler.configureElements(RabbitConfigureElementsBuilder.class, builder -> builder
                    .rabbit(rabbit -> rabbit.config(config -> config.host("127.0.0.1").port("5672").username("guest").password("guest")))
            );
            sampler.config(config -> config.message(true).queue(queue -> queue.name("ryze.topic")));
        });
    }
}
