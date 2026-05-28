package io.github.xiaomisum.ryze.dubbo.example.code

import io.github.xiaomisum.ryze.Ryze
import io.github.xiaomisum.ryze.protocol.dubbo.Dubbo
import io.github.xiaomisum.ryze.protocol.dubbo.builder.DubboConfigureElementsBuilder
import io.github.xiaomisum.ryze.protocol.dubbo.builder.DubboPreprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.dubbo.builder.DubboSamplersBuilder
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
            configureElements(DubboConfigureElementsBuilder.class, {
                dubbo {
                    config {
                        registry {
                            address "zookeeper://localhost:42181"
                        }
                        reference {
                            retries 2
                            timeout 5000
                            async false
                            loadBalance "random"
                        }
                    }
                }
            })
            preprocessors(DubboPreprocessorsBuilder.class, {
                dubbo {
                    config {
                        interfaceName "io.github.xiaomisum.ryze.dubbo.example.DemoService"
                        method "sayHello"
                        parameters "\$tick"
                    }
                }
            })
            children(DubboSamplersBuilder.class, {
                dubbo {
                    title "步骤1"
                    config {
                        interfaceName "io.github.xiaomisum.ryze.dubbo.example.DemoService"
                        method "sayHello"
                        parameters "步骤1: dubbo_sampler"
                    }
                }
            })
            children(DubboSamplersBuilder.class, {
                dubbo {
                    title "步骤2"
                    config {
                        interfaceName "io.github.xiaomisum.ryze.dubbo.example.DemoService"
                        method "sayHello"
                        parameters "步骤2: dubbo_sampler"
                    }
                }
            })
        })
    }

    @Test
    @RyzeTest
    void test2() {
        Dubbo.dubbo("测试用例- test2()", sampler -> {
            configureElements(DubboConfigureElementsBuilder.class, {
                dubbo {
                    config {
                        registry {
                            address "zookeeper://localhost:42181"
                        }
                        reference {
                            retries 2
                            timeout 5000
                            async false
                            loadBalance "random"
                        }
                    }
                }
            })
            preprocessors(DubboPreprocessorsBuilder.class, {
                dubbo {
                    config {
                        interfaceName "io.github.xiaomisum.ryze.dubbo.example.DemoService"
                        method "sayHello"
                        parameters "dubbo_preprocessor"
                    }
                }
            })
            config {
                interfaceName "io.github.xiaomisum.ryze.dubbo.example.DemoService"
                method "sayHello"
                parameters "test2(): dubbo_sampler"
            }
        })
    }


    @Test
    @RyzeTest
    void test3() {
        Dubbo.dubbo({
            title "步骤1——插入用户：tick = redis_preprocessor"
            configureElements(DubboConfigureElementsBuilder.class, {
                dubbo {
                    config {
                        registry {
                            address "zookeeper://localhost:42181"
                        }
                        reference {
                            retries 2
                            timeout 5000
                            async false
                            loadBalance "random"
                        }
                    }
                }
            })
            config {
                interfaceName "io.github.xiaomisum.ryze.dubbo.example.DemoService"
                method "sayHello"
                parameters "test3(步骤1): dubbo_sampler"
            }
        })

        Dubbo.dubbo({
            title "步骤2——查找用户：tick = ryze_http_sampler"
            configureElements(DubboConfigureElementsBuilder.class, {
                dubbo {
                    config {
                        registry {
                            address "zookeeper://localhost:42181"
                        }
                        reference {
                            retries 2
                            timeout 5000
                            async false
                            loadBalance "random"
                        }
                    }
                }
            })
            config {
                interfaceName "io.github.xiaomisum.ryze.dubbo.example.DemoService"
                method "sayHello"
                parameters "test3(步骤2): dubbo_sampler"
            }
        })
    }
}
