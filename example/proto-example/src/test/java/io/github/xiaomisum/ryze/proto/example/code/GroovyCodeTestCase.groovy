package io.github.xiaomisum.ryze.proto.example.code

import io.github.xiaomisum.ryze.protocol.proto.ProtoMagicBox
import io.github.xiaomisum.ryze.protocol.proto.builder.ProtoConfigureElementsBuilder
import io.github.xiaomisum.ryze.protocol.proto.builder.ProtoPreprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.proto.builder.ProtoSamplersBuilder
import io.github.xiaomisum.ryze.support.Collections
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
        ProtoMagicBox.suite {
            title '测试用例-http-protobuf-test1'
            variables('id', 1)
            variables('t_body', [id: '2', name: 'ryze_http_proto_preprocessor', age: 0])
            variables {
                // 函数式写法
                put([a: 1, b: 2])
            }
            variables Collections.newHashMap([c: 3, d: 4])
            configureElements ProtoConfigureElementsBuilder.class, {
                proto {
                    config {
                        protocol 'http'
                        method 'get'
                        host '127.0.0.1'
                        port '8080'
                        protoDesc {
                            descPath 'D:\\Github\\ryze\\example\\proto-example\\user.desc'
                            requestMessageName 'io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User'
                            responseMessageName 'io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User'
                        }
                    }
                }
            }
            preprocessors ProtoPreprocessorsBuilder.class, {
                proto {
                    title '前置处理器新增用户'
                    config {
                        method 'PUT'
                        path '/user'
                        body '${t_body}'
                    }
                    extractors {
                        json {
                            field '$.id'
                            refName 't_id'
                        }
                    }
                }
            }
            children ProtoSamplersBuilder.class, {
                proto {
                    title '步骤1——获取用户：id = \${id}'
                    config {
                        method 'GET'
                        path '/user/${id}'
                    }
                    validators {
                        json {
                            field '$.id'
                            rule '=='
                            expected '${id}'
                        }
                    }
                }
            }
            children ProtoSamplersBuilder.class, {
                proto {
                    title '步骤2——修改用户：id=\${t_id}'
                    config {
                        method 'POST'
                        path '/user'
                        body { body -> body.putAll([id: '2', name: 'ryze_http_proto_sampler', age: 0]) }

                    }
                    validators {
                        http {
                            field 'statusCode'
                            rule '=='
                            expected 200
                        }
                    }
                }
            }
            children ProtoSamplersBuilder.class, {
                http {
                    title '步骤3——获取用户：id =\${t_body.id}'
                    config {
                        method 'GET'
                        path '/user/${t_body.id}'
                    }
                    validators {
                        json {
                            field '$.name'
                            rule '=='
                            expected 'ryze_http_proto_sampler'
                        }
                    }
                }
            }
        }
    }

    @Test
    @RyzeTest
    void test2() {
        // 以 Sampler 作为主执行步骤，其他步骤以 preprocessors 、 postprocessors 执行
        // 执行顺序：preprocessors -> sampler -> postprocessors
        // 注意：preprocessors 、 postprocessors 无断言功能，需断言的步骤应当为 Sampler
        ProtoMagicBox.proto {
            title '测试用例-http-protobuf-test2'
            configureElements ProtoConfigureElementsBuilder.class, {
                proto {
                    config {
                        protocol 'http'
                        host '127.0.0.1'
                        port '8080'
                        protoDesc {
                            descPath 'D:\\Github\\ryze\\example\\proto-example\\user.desc'
                            requestMessageName 'io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User'
                            responseMessageName 'io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User'
                        }
                    }
                }
            }
            preprocessors ProtoPreprocessorsBuilder.class, {
                proto {
                    title '前置处理器修改用户：ryze'
                    config {
                        method 'POST'
                        path '/user'
                        body { body -> body.putAll([id: '1', name: 'ryze_http_preprocessor', age: 0]) }
                    }
                    extractors {
                        json {
                            field '$.id'
                            refName 'id'
                        }
                        json {
                            field '$.name'
                            refName 'name'
                        }
                    }
                }
            }
            config {
                method 'GET'
                path '/user/${id}'
            }
            validators {
                json {
                    field '$.name'
                    rule '=='
                    expected '${name}'
                }
            }
        }
    }


    @Test
    @RyzeTest
    void test3() {
        ProtoMagicBox.suite {
            title '测试用例-websocket-protobuf-test3'
            variables('id', 1)
            variables('t_body', ['id': 1, 'name': 'ryze_websocket_proto_preprocessor', 'age': 0])
            variables {
                put(['a': 1, 'b': 2])
            }
            variables { put('c', 3).put('d', 4) }
            configureElements ProtoConfigureElementsBuilder.class, {
                proto {
                    config {
                        protocol 'ws'
                        host '127.0.0.1'
                        port '8080'
                        protoDesc {
                            descPath 'D:\\Github\\ryze\\example\\proto-example\\user.desc'
                            requestMessageName 'io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User'
                            responseMessageName 'io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User'
                        }
                    }
                }
            }
            preprocessors ProtoPreprocessorsBuilder.class, {
                proto {
                    title '前置处理器-websocket-protobuf-test3'
                    config {
                        path '/user'
                        body '${t_body}'
                        responsePattern 'name'
                    }
                    extractors { json 't_id', '$.id' }
                }
            }
            children ProtoSamplersBuilder.class, {
                proto {
                    title '取样器-websocket-protobuf-test3'
                    config {
                        path '/user'
                        body { body -> body.putAll([id: '1', name: 'ryze_http_proto_sampler', age: 0]) }
                        responsePattern 'name'
                    }
                    validators {
                        json {
                            field '$.name'
                            rule '=='
                            expected 'ryze_websocket_proto_sampler'
                        }
                    }
                }
            }
        }
    }

    @Test
    @RyzeTest
    void test4() {
        ProtoMagicBox.proto {
            title '测试用例-websocket-protobuf-test4'
            configureElements ProtoConfigureElementsBuilder.class, {
                proto {
                    config {
                        protocol 'http'
                        host '127.0.0.1'
                        port '8080'
                        protoDesc {
                            descPath 'D:\\Github\\ryze\\example\\proto-example\\user.desc'
                            requestMessageName 'io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User'
                            responseMessageName 'io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User'
                        }
                    }
                }
            }

            preprocessors ProtoPreprocessorsBuilder.class, {
                proto {
                    title '前置处理器-websocket-protobuf-test4'
                    config {
                        path '/user'
                        body(body -> {
                            body.put('id', 1)
                            body.put('name', 'ryze_websocket_proto_preprocessor')
                            body.put('age', 1)
                        })
                        responsePattern 'name'
                    }
                    extractors {
                        json 'name', '$.name'
                        json 'id', '$.id'
                    }
                }
            }
            config {
                path '/user'
                body(body -> {
                    body.put('id', 1)
                    body.put('name', 'ryze_websocket_proto_sampler')
                    body.put('age', 1)
                })
                responsePattern 'name'
            }
            validators {
                json '$.name', 'ryze_websocket_proto_sampler'
            }
        }
    }
}
