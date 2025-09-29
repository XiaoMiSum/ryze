package io.github.xiaomisum.ryze.kafka.example.code;

import io.github.xiaomisum.ryze.protocol.kafka.KafkaMagicBox;
import io.github.xiaomisum.ryze.protocol.kafka.builder.KafkaConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.kafka.builder.KafkaPostprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.kafka.builder.KafkaPreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.kafka.builder.KafkaSamplersBuilder;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class CodeTestCase {

    @Test
    @RyzeTest
    public void test1() {
        KafkaMagicBox.suite("测试用例", suite -> {
            suite.variables("id", 1);
            suite.variables(var -> var.put("tick", "kafka_preprocessor"));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.configureElements(KafkaConfigureElementsBuilder.class, builder -> builder
                    .kafka(kafka -> kafka.config(config -> config.bootstrapServers("127.0.0.1:9092").topic("ryze.topic").key("ryze")))
            );
            suite.preprocessors(KafkaPreprocessorsBuilder.class, builder -> builder
                    .kafka(kafka -> kafka.config(config -> config
                            .message(Map.of("username", "${tick}", "age", 18))
                    ))
            );
            suite.postprocessors(KafkaPostprocessorsBuilder.class, builder -> builder
                    .kafka(kafka -> kafka.config(config -> config.message("test1: kafka_postprocessor_test ")))
            );
            suite.children(KafkaSamplersBuilder.class, builder -> builder
                    .kafka(kafka -> kafka.title("步骤1").variables("username", "ryze")
                            .config(config -> config.message(Map.of("name", "${username}  步骤1：标准kafka取样器"))))
            );
            suite.children(KafkaSamplersBuilder.builder()
                    .kafka(kafka -> kafka.title("步骤2").variables("username", "ryze")
                            .config(config -> config.message(Map.of("name", "${username}  步骤2：标准kafka取样器"))))
                    .build());
        });
    }

    @Test
    @RyzeTest
    public void test2() {
        KafkaMagicBox.kafka("测试用例- test2()", sampler -> {
            sampler.configureElements(KafkaConfigureElementsBuilder.class, builder -> builder
                    .kafka(kafka -> kafka.config(config -> config.bootstrapServers("127.0.0.1:9092").topic("ryze.topic").key("ryze")))
            );
            sampler.preprocessors(KafkaPreprocessorsBuilder.class, builder -> builder
                    .kafka(kafka -> kafka.config(config -> config
                            .message(Map.of("username", "${tick}", "age", 18))
                    ))
            );
            sampler.config(config -> config.message(123456));
        });
    }


    @Test
    @RyzeTest
    public void test3() {
        KafkaMagicBox.kafka("测试用例- test3()-1", sampler -> {
            sampler.configureElements(KafkaConfigureElementsBuilder.class, builder -> builder
                    .kafka(kafka -> kafka.config(config -> config.bootstrapServers("127.0.0.1:9092").topic("ryze.topic").key("ryze")))
            );
            sampler.config(config -> config.message(List.of(1, 2, 3, 4)));
        });


        KafkaMagicBox.kafka("测试用例- test3()-2", sampler -> {
            sampler.configureElements(KafkaConfigureElementsBuilder.class, builder -> builder
                    .kafka(kafka -> kafka.config(config -> config.bootstrapServers("127.0.0.1:9092").topic("ryze.topic").key("ryze")))
            );
            sampler.config(config -> config.message(true));
        });
    }
}
