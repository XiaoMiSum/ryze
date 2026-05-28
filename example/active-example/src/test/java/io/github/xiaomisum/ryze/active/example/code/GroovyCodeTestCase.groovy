package io.github.xiaomisum.ryze.active.example.code

import io.github.xiaomisum.ryze.Ryze
import io.github.xiaomisum.ryze.active.example.TestObj
import io.github.xiaomisum.ryze.protocol.active.Active
import io.github.xiaomisum.ryze.protocol.active.builder.ActiveConfigureElementsBuilder
import io.github.xiaomisum.ryze.protocol.active.builder.ActivePostprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.active.builder.ActivePreprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.active.builder.ActiveSamplersBuilder
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
            configureElements(ActiveConfigureElementsBuilder.class, {
                active {
                    config {
                        username "artemis"
                        password "artemis"
                        topic "ryze.topic"
                        brokerUrl "tcp://127.0.0.1:61616"
                    }
                }
            })
            preprocessors(ActivePreprocessorsBuilder.class, {
                active {
                    config {
                        message(HashMap.class, message -> {
                            message.put("username", "\${tick}")
                            message.put("age", 18)
                        })
                    }
                }
            })
            postprocessors(ActivePostprocessorsBuilder.class, {
                active {
                    config {
                        message "test1: active_postprocessor_test "
                    }
                }
            })
            children(ActiveSamplersBuilder.class, {
                active {
                    title "步骤1"
                    config {
                        message Map.of("name", "\${tick}  步骤1：标准active取样器")
                    }
                }
            })
            children(ActiveSamplersBuilder.class, {
                active {
                    title "步骤2"
                    config {
                        message Map.of("name", "\${tick}  步骤2：标准active取样器")
                    }
                }
            })
        })
    }

    @Test
    @RyzeTest
    void test2() {
        Active.active("测试用例- test2()", sampler -> {
            configureElements(ActiveConfigureElementsBuilder.class, {
                active {
                    config {
                        username "artemis"
                        password "artemis"
                        topic "ryze.topic"
                        brokerUrl "tcp://127.0.0.1:61616"
                    }
                }
            })
            preprocessors(ActivePreprocessorsBuilder.class, {
                active {
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
        Active.active({
            title "步骤1——插入用户：tick = redis_preprocessor"
            configureElements(ActiveConfigureElementsBuilder.class, {
                active {
                    config {
                        username "artemis"
                        password "artemis"
                        topic "ryze.topic"
                        brokerUrl "tcp://127.0.0.1:61616"
                    }
                }
            })
            config {
                message([1, 2, 3, 4, 5])
            }
        })

        Active.active({
            title "步骤2——查找用户：tick = ryze_http_sampler"
            configureElements(ActiveConfigureElementsBuilder.class, {
                active {
                    config {
                        username "artemis"
                        password "artemis"
                        topic "ryze.topic"
                        brokerUrl "tcp://127.0.0.1:61616"
                    }
                }
            })
            config {
                message true
            }
        })
    }
}
