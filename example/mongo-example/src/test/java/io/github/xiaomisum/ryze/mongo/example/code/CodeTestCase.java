package io.github.xiaomisum.ryze.mongo.example.code;

import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.protocol.mongo.Mongo;
import io.github.xiaomisum.ryze.protocol.mongo.builder.MongoConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.mongo.builder.MongoPostprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.mongo.builder.MongoPreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.mongo.builder.MongoSamplersBuilder;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

@SuppressWarnings({"unchecked"})
public class CodeTestCase {

    @Test
    @RyzeTest
    public void test1() {
        Ryze.suite("测试用例", suite -> {
            suite.variables("id", 1);
            suite.variables(var -> var.put("tick", "mongo_preprocessor"));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.configureElements(MongoConfigureElementsBuilder.class, builder -> builder
                    .mongo(mongo -> mongo.config(config -> config
                            .url("mongodb://root:123456@127.0.0.1:27017/?authSource=admin")
                            .database("demo")
                            .collection("book"))
                    )
            );
            suite.preprocessors(MongoPreprocessorsBuilder.class, builder -> builder
                    .mongo(mongo -> mongo.config(config -> config.insert(Collections.of("name", "${tick}", "author", "ryze", "isbn", "${random_string(10)}"))))
            );
            suite.postprocessors(MongoPostprocessorsBuilder.class, builder -> builder
                    .mongo(mongo -> mongo.config(config -> config.delete(condition -> condition.put("name", Map.of("$eq", "${tick}")))))
            );
            suite.children(MongoSamplersBuilder.class, builder -> builder
                    .mongo(mongo -> mongo.title("步骤1").variables("username", "ryze")
                            .config(config -> config.action("insert").dataList(list -> list.add(Collections.of("name", "${tick}", "author", "ryze", "isbn", "${random_string(10)}")))))
            );
            suite.children(MongoSamplersBuilder.builder()
                    .mongo(mongo -> mongo.title("步骤2").variables("username", "ryze")
                            .config(config -> config.action("find").condition(condition -> condition.put("name", Map.of("$eq", "${tick}"))))
                            .assertions(assertion -> assertion.json("$[0].name", "${tick}")))
                    .build());
        });
    }

    @Test
    @RyzeTest
    public void test2() {
        Mongo.mongo("测试用例- test2()", sampler -> {
            sampler.variables("name", "${random_string(10)}");
            sampler.configureElements(MongoConfigureElementsBuilder.class, builder -> builder
                    .mongo(mongo -> mongo.config(config -> config
                            .url("mongodb://root:123456@127.0.0.1:27017/?authSource=admin")
                            .database("demo")
                            .collection("book"))
                    )
            );
            sampler.preprocessors(MongoPreprocessorsBuilder.class, builder -> builder
                    .mongo(mongo -> mongo.config(config -> config.insert(Collections.of("name", "${name}", "author", "ryze", "isbn", "${random_string(10)}"))))
            );
            sampler.config(config -> config.action("find").condition(condition -> condition.put("name", Map.of("$eq", "${name}"))))
                    .assertions(assertion -> assertion.json("$.name", "${name}"));
        });
    }


    @Test
    @RyzeTest
    public void test3() {
        Mongo.mongo("测试用例- test3()-1", sampler -> {
            sampler.configureElements(MongoConfigureElementsBuilder.class, builder -> builder
                    .mongo(mongo -> mongo.config(config -> config
                            .url("mongodb://root:123456@127.0.0.1:27017/?authSource=admin")
                            .database("demo")
                            .collection("book"))
                    )
            );
            sampler.config(config -> config.insertList(list -> {
                list.add(Collections.of("name", "ryze", "author", "ryze", "isbn", "${random_string(10)}"));
                list.add(Collections.of("name", "ryze", "author", "ryze", "isbn", "${random_string(10)}"));
            }));
        });


        Mongo.mongo("测试用例- test3()-2", sampler -> {
            sampler.configureElements(MongoConfigureElementsBuilder.class, builder -> builder
                    .mongo(mongo -> mongo.config(config -> config
                            .url("mongodb://root:123456@127.0.0.1:27017/?authSource=admin")
                            .database("demo")
                            .collection("book"))
                    )
            );
            sampler.config(config -> config.find(condition -> condition.put("name", Map.of("$eq", "ryze"))))
                    .validators(validator -> validator.json(">=", "$.size()", "2"));
        });
    }
}
