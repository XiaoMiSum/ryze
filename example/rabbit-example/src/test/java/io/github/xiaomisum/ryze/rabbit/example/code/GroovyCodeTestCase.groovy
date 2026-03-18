package io.github.xiaomisum.ryze.rabbit.example.code

import io.github.xiaomisum.ryze.protocol.rabbit.RabbitMagicBox
import io.github.xiaomisum.ryze.protocol.rabbit.builder.RabbitConfigureElementsBuilder
import io.github.xiaomisum.ryze.protocol.rabbit.builder.RabbitPostprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.rabbit.builder.RabbitPreprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.rabbit.builder.RabbitSamplersBuilder
import io.github.xiaomisum.ryze.rabbit.example.TestObj
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyCodeTestCase {


    @Test
    @RyzeTest
    void test1() {
        RabbitMagicBox.suite("测试用例", {
            variables("id", 1)
            variables { put("tick", "ryze") }
            variables Map.of("a", 1, "b", 2)
            configureElements(RabbitConfigureElementsBuilder.class, {
                rabbit {
                    config {
                        url "amqp://guest:guest@127.0.0.1:5672/"
                    }
                }
            })
            preprocessors(RabbitPreprocessorsBuilder.class, {
                rabbit {
                    config {
                        message(HashMap.class, message -> {
                            message.put("username", "\${tick}")
                            message.put("age", 18)
                        })
                        queue {
                            name "ryze.topic"
                        }
                    }
                }
            })
            postprocessors(RabbitPostprocessorsBuilder.class, {
                rabbit {
                    config {
                        message "test1: rabbit_postprocessor_test "
                        queue {
                            name "ryze.topic"
                        }
                    }
                }
            })
            children(RabbitSamplersBuilder.class, {
                rabbit {
                    title "步骤1"
                    config {
                        message Map.of("name", "\${tick}  步骤1：标准rabbit取样器")
                        queue {
                            name "ryze.topic"
                        }
                    }

                }
            })
            children(RabbitSamplersBuilder.class, {
                rabbit {
                    title "步骤2"
                    config {
                        message Map.of("name", "\${tick}  步骤2：标准rabbit取样器")
                        queue {
                            name "ryze.topic"
                        }
                    }
                }
            })
        })
    }

    @Test
    @RyzeTest
    void test2() {
        RabbitMagicBox.rabbit("测试用例- test2()", sampler -> {
            configureElements(RabbitConfigureElementsBuilder.class, {
                rabbit {
                    config {
                        url "amqp://guest:guest@127.0.0.1:5672/"
                    }
                }
            })
            preprocessors(RabbitPreprocessorsBuilder.class, {
                rabbit {
                    config {
                        message(TestObj.class, test -> {
                            test.setName("hahaha")
                        })
                        queue {
                            name "ryze.topic"
                        }
                    }
                }
            })
            config {
                message 123456
                queue {
                    name "ryze.topic"
                }
            }
        })
    }


    @Test
    @RyzeTest
    void test3() {
        RabbitMagicBox.rabbit({
            title "步骤1——插入用户：tick = redis_preprocessor"
            configureElements(RabbitConfigureElementsBuilder.class, {
                rabbit {
                    config {
                        url "amqp://guest:guest@127.0.0.1:5672/"
                    }
                }
            })
            config {
                message([1, 2, 3, 4, 5])
                queue {
                    name "ryze.topic"
                }
            }
        })

        RabbitMagicBox.rabbit({
            title "步骤2——查找用户：tick = ryze_http_sampler"
            configureElements(RabbitConfigureElementsBuilder.class, {
                rabbit {
                    config {
                        url "amqp://guest:guest@127.0.0.1:5672/"
                    }
                }
            })
            config {
                message true
                queue {
                    name "ryze.topic"
                }
            }
        })
    }
}
