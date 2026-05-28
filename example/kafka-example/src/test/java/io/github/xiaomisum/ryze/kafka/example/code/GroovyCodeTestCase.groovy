package io.github.xiaomisum.ryze.kafka.example.code

import io.github.xiaomisum.ryze.Ryze
import io.github.xiaomisum.ryze.kafka.example.TestObj
import io.github.xiaomisum.ryze.protocol.kafka.Kafka
import io.github.xiaomisum.ryze.protocol.kafka.builder.KafkaConfigureElementsBuilder
import io.github.xiaomisum.ryze.protocol.kafka.builder.KafkaPostprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.kafka.builder.KafkaPreprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.kafka.builder.KafkaSamplersBuilder
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyCodeTestCase {


    @Test
    @RyzeTest
    void test1() {
        Ryze.suite("测试用例", {
            variables("id", 1)
            variables { put("tick", "ryze") }
            variables Map.of("a", 1, "b", 2)
            configureElements(KafkaConfigureElementsBuilder.class, {
                kafka {
                    config {
                        bootstrapServers "127.0.0.1:9092"
                        topic "ryze.topic"
                        key "ryze"
                    }
                }
            })
            preprocessors(KafkaPreprocessorsBuilder.class, {
                kafka {
                    config {
                        message(HashMap.class, message -> {
                            message.put("username", "\${tick}")
                            message.put("age", 18)
                        })
                    }
                }
            })
            postprocessors(KafkaPostprocessorsBuilder.class, {
                kafka {
                    config {
                        message "test1: kafka_postprocessor_test "
                    }
                }
            })
            children(KafkaSamplersBuilder.class, {
                kafka {
                    title "步骤1"
                    config {
                        message Map.of("name", "\${tick}  步骤1：标准kafka取样器")
                    }
                }
            })
            children(KafkaSamplersBuilder.class, {
                kafka {
                    title "步骤2"
                    config {
                        message Map.of("name", "\${tick}  步骤2：标准kafka取样器")
                    }
                }
            })
        })
    }

    @Test
    @RyzeTest
    void test2() {
        Kafka.kafka("测试用例- test2()", sampler -> {
            configureElements(KafkaConfigureElementsBuilder.class, {
                kafka {
                    config {
                        bootstrapServers "127.0.0.1:9092"
                        topic "ryze.topic"
                        key "ryze"
                    }
                }
            })
            preprocessors(KafkaPreprocessorsBuilder.class, {
                kafka {
                    config {
                        message(TestObj.class, test -> {
                            test.setName("hahaha")
                        })
                    }
                }
            })
            config {
                message 123456
            }
        })
    }


    @Test
    @RyzeTest
    void test3() {
        Kafka.kafka({
            title "步骤1——插入用户：tick = redis_preprocessor"
            configureElements(KafkaConfigureElementsBuilder.class, {
                kafka {
                    config {
                        bootstrapServers "127.0.0.1:9092"
                        topic "ryze.topic"
                        key "ryze"
                    }
                }
            })
            config {
                message([1, 2, 3, 4, 5])
            }
        })

        Kafka.kafka({
            title "步骤2——查找用户：tick = ryze_http_sampler"
            configureElements(KafkaConfigureElementsBuilder.class, {
                kafka {
                    config {
                        bootstrapServers "127.0.0.1:9092"
                        topic "ryze.topic"
                        key "ryze"
                    }
                }
            })
            config {
                message true
            }
        })
    }
}
