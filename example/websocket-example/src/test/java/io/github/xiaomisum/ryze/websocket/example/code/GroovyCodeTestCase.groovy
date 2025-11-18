package io.github.xiaomisum.ryze.websocket.example.code

import io.github.xiaomisum.ryze.protocol.websocket.WebsocketMagicBox
import io.github.xiaomisum.ryze.protocol.websocket.builder.WebsocketConfigureElementsBuilder
import io.github.xiaomisum.ryze.protocol.websocket.builder.WebsocketPreprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.websocket.builder.WebsocketSamplersBuilder
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyCodeTestCase {

    /**
     * 编码模式测试，必须以 RyzeTest 注解方式执行，否则需要自行创建 SessionRunner实例
     */
    @Test
    @RyzeTest
    void test1() {
        // 将所有执行步骤放在 suite 中执行
        // 这样的好处：
        //      1、所有步骤共用suite中配置的变量和默认配置，可以减少重复代码
        //      2、前置步骤提取的变量，会自动添加到 suite 中，后续步骤可以直接使用
        WebsocketMagicBox.suite {
            title "测试用例"
            configureElements(WebsocketConfigureElementsBuilder.class, {
                ws {
                    config {
                        protocol "ws"
                        host "127.0.0.1"
                        port "8080"
                    }
                }
            })
            preprocessors(WebsocketPreprocessorsBuilder.class, {
                ws {
                    title "前置处理器发送消息"
                    config {
                        path '/ws/body/string'
                        body 'websocket postprocessor test'
                        responsePattern "websocket postprocessor test"
                    }
                }
            })
            children(WebsocketSamplersBuilder.class, {
                ws {
                    title "步骤1——获取用户：id = \${id}"
                    config {
                        path '/ws/body/string'
                        body 'websocket sampler test'
                        responsePattern "websocket sampler test"
                    }
                    validators {
                        result {
                            rule '=='
                            expected 'websocket sampler test'
                        }
                    }
                }
            })
        }
    }

    @Test
    @RyzeTest
    void test2() {
        // 以 Sampler 作为主执行步骤，其他步骤以 preprocessors 、 postprocessors 执行
        // 执行顺序：preprocessors -> sampler -> postprocessors
        // 注意：preprocessors 、 postprocessors 无断言功能，需断言的步骤应当为 Sampler
        WebsocketMagicBox.ws {
            title "测试用例- test2()"
            configureElements(WebsocketConfigureElementsBuilder.class, {
                ws {
                    config {
                        protocol "ws"
                        host "127.0.0.1"
                        port "8080"
                    }
                }
            })
            preprocessors(WebsocketPreprocessorsBuilder.class, {
                ws {
                    title "前置处理器发送消息"
                    config {
                        path '/ws/body/string'
                        body 'websocket postprocessor test'
                        responsePattern "websocket postprocessor test"
                    }
                }
            })
            config {
                path '/ws/body/string'
                body 'websocket sampler test'
                responsePattern "websocket sampler test"
            }
            validators {
                result {
                    rule '=='
                    expected 'websocket sampler test'
                }
            }
        }
    }
}
