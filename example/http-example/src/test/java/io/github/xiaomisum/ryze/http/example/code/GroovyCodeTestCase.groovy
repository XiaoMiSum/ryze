package io.github.xiaomisum.ryze.http.example.code

import io.github.xiaomisum.ryze.MagicBox
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
        MagicBox.suite {
            title "测试用例"
            variables("id", 1)
            variables("t_body", [id: "ryze", name: "ryze_http_preprocessor", age: 0])
            variables {
                // 函数式写法
                put([a: 1, b: 2])
            }
            variables Collections.newHashMap([c: 3, d: 4])
            configureElements {
                http {
                    config {
                        baseUrl "http://127.0.0.1:58081"
                        method "get"
                    }
                }
            }
            preprocessors {
                http {
                    title "前置处理器新增用户"
                    config {
                        method "PUT"
                        path '/user'
                        body '${t_body}'
                    }
                    extractors {
                        json {
                            field '$.data.id'
                            refName "t_id"
                        }
                    }
                }
            }
            children {
                http {
                    title "步骤1——获取用户：id = \${id}"
                    config {
                        method "GET"
                        path '/user/${id}'
                    }
                    validators {
                        json {
                            field '$.data.id'
                            rule '=='
                            expected '${id}'
                        }
                    }
                }
            }
            children {
                http {
                    title "步骤2——修改用户：id=\${t_id}"
                    config {
                        method "POST"
                        path '/user'
                        body { body -> body.putAll([id: "ryze", name: "ryze_http_sampler", age: 0]) }

                    }
                    validators {
                        http {
                            field 'statusCode'
                            rule "=="
                            expected 400
                        }
                    }
                }
            }
            children {
                http {
                    title "步骤3——获取用户：id =\${t_body.id}"
                    config {
                        method "GET"
                        path '/user/${t_body.id}'
                    }
                    validators {
                        json {
                            field '$.data.name'
                            rule '=='
                            expected 'ryze_http_sampler'
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
        MagicBox.http {
            title "测试用例- test2()"
            configureElements {
                http {
                    config {
                        baseUrl "http://127.0.0.1:58081"
                    }
                }
            }
            preprocessors {
                http {
                    title "前置处理器修改用户：ryze"
                    config {
                        method "POST"
                        path '/user'
                        body { body -> body.putAll([id: "ryze", name: "ryze_http_preprocessor", age: 0]) }
                    }
                    extractors {
                        json {
                            field '$.data.id'
                            refName "id"
                        }
                        json {
                            field '$.data.name'
                            refName "name"
                        }
                    }
                }
            }
            config {
                method "GET"
                path '/user/${id}'
            }
            validators {
                json {
                    field '$.data.name'
                    rule '=='
                    expected '${name}'
                }
            }
        }
    }


    @Test
    @RyzeTest
    void test3() {
        // 以独立的 http sampler 执行测试步骤
        // 无法共用前置步骤提取的变量，建议在单步骤用例下使用此方式
        MagicBox.http {
            variables {
                put "id", "1"
            }
            title "步骤1——获取用户：id = \${id}"
            config {
                baseUrl "http://127.0.0.1:58081"
                method "GET"
                path '/user/${id}'
            }
            validators {
                json {
                    field '$.data.id'
                    rule '=='
                    expected '${id}'
                }
            }
        }

        MagicBox.http {
            title "步骤2——修改用户：id=ryze"
            config {
                baseUrl "http://127.0.0.1:58081"
                method "POST"
                path '/user'
                body { body -> body.putAll([id: "ryze", name: "ryze_http_sampler", age: 0]) }

            }
            validators {
                http {
                    field 'statusCode'
                    rule "=="
                    expected 200
                }
            }
        }

        MagicBox.http {
            variables {
                put "t_body", Collections.of("id", "ryze", "name", "ryze_http_preprocessor", "age", 0)
            }
            title "步骤3——获取用户：id =\${t_body.id}"
            config {
                baseUrl "http://127.0.0.1:58081"
                method "GET"
                path '/user/${t_body.id}'
            }
            validators {
                json {
                    field '$.data.name'
                    rule '=='
                    expected 'ryze_http_sampler'
                }
            }
        }
    }
}
