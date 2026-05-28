package io.github.xiaomisum.ryze.mongo.example.code

import io.github.xiaomisum.ryze.Ryze
import io.github.xiaomisum.ryze.protocol.mongo.Mongo
import io.github.xiaomisum.ryze.protocol.mongo.builder.MongoConfigureElementsBuilder
import io.github.xiaomisum.ryze.protocol.mongo.builder.MongoPostprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.mongo.builder.MongoPreprocessorsBuilder
import io.github.xiaomisum.ryze.protocol.mongo.builder.MongoSamplersBuilder
import io.github.xiaomisum.ryze.support.Collections
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyCodeTestCase {


    @Test
    @RyzeTest
    void test1() {
        Ryze.suite("测试用例", {
            variables("author", "\${faker('book.author')}")
            variables { put("title", "\${faker('book.title')}") }
            variables Map.of("a", 1, "b", 2)
            configureElements(MongoConfigureElementsBuilder.class, {
                mongo {
                    config {
                        url "mongodb://root:123456@127.0.0.1:27017/?authSource=admin"
                        database "demo"
                        collection "book"
                    }
                }
            })
            preprocessors(MongoPreprocessorsBuilder.class, {
                mongo {
                    config {
                        action "insert"
                        dataMap(data -> {
                            data.put("name", "\${title}")
                            data.put("author", "\${author}")
                            data.put("isbn", "\${timestamp()}")
                        })
                    }
                }
            })
            postprocessors(MongoPostprocessorsBuilder.class, {
                mongo {
                    config {
                        action "delete"
                        condition { condition -> { condition.put("name", Map.of("\$eq", "\${title}")) } }
                    }
                }
            })
            children(MongoSamplersBuilder.class, {
                mongo {
                    title "步骤1"
                    config {
                        insertData { data ->
                            {
                                data.put("name", "\${title}")
                                data.put("author", "\${author}")
                                data.put("isbn", "\${timestamp()}")
                            }
                        }
                    }
                }
            })
            children(MongoSamplersBuilder.class, {
                mongo {
                    title "步骤2"
                    config {
                        select { condition -> { condition.put("name", Map.of("\$eq", "\${name}")) } }
                    }
                    validators {
                        json {
                            field "\$[0].name"
                            rule "=="
                            expected "\${name}"
                        }
                    }
                }
            })
        })
    }

    @Test
    @RyzeTest
    void test2() {
        Mongo.mongo("测试用例- test2()", sampler -> {
            configureElements(MongoConfigureElementsBuilder.class, {
                mongo {
                    config {
                        url "mongodb://root:123456@127.0.0.1:27017/?authSource=admin"
                        database "demo"
                        collection "book"
                    }
                }
            })
            config {
                action "find"
            }
            validators {
                json {
                    field "\$.size()"
                    rule ">="
                    expected "1"
                }
            }
        })
    }


    @Test
    @RyzeTest
    void test3() {
        var variable = ["name": "ryze", "author": "test"]
        Mongo.mongo({
            title "步骤1——插入用户：tick = redis_preprocessor"
            variables(variable)
            configureElements(MongoConfigureElementsBuilder.class, {
                mongo {
                    config {
                        url "mongodb://root:123456@127.0.0.1:27017/?authSource=admin"
                        database "demo"
                        collection "book"
                    }
                }
            })
            config {
                update(Collections.of("\$set", Collections.of("author", "\${author}")), Collections.of("name", "\${name}"))
            }
        })

        Mongo.mongo({
            title "步骤2——查找用户：tick = ryze_http_sampler"
            variables(variable)
            configureElements(MongoConfigureElementsBuilder.class, {
                mongo {
                    config {
                        url "mongodb://root:123456@127.0.0.1:27017/?authSource=admin"
                        database "demo"
                        collection "book"
                    }
                }
            })
            config {
                select { condition -> condition.put("name", Map.of("\$eq", "\${name}")) }
            }
            validators {
                json {
                    field "\$[0].author"
                    rule "=="
                    expected "\${author}"
                }
            }
        })
    }
}
