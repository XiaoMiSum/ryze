package io.github.xiaomisum.ryze.redis.example.code;

import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class CodeTestCase {

    @Test
    @RyzeTest
    public void test1() {
        MagicBox.suite("测试用例", suite -> {
            suite.variables("id", 1);
            suite.variables(var -> var.put("tick", "ryze"));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.configureElements(ele -> ele.redis(redis -> redis.refName("redis_source")
                    .config(config -> config.url("redis://127.0.0.1:6379"))));
            suite.children(child -> child.redis(redis -> redis.title("步骤1").variables("username", "ryze")
                    .config(config -> config.datasource("redis_source").command("set").args("test_case,${tick}"))));
            suite.children(child -> child.redis(redis -> redis.title("步骤2").config(config -> config.datasource("redis_source").command("get").args("test_case"))
                    .validators(validator -> validator.result(result -> result.expected("${tick}")))));
        });
    }

    @Test
    @RyzeTest
    public void test2() {
        MagicBox.redis("测试用例- test2()", sampler -> {
            sampler.configureElements(ele -> ele.redis(redis -> redis.refName("redis_source")
                    .config(config -> config.url("redis://127.0.0.1:6379"))));
            sampler.preprocessors(pre -> pre.redis(redis -> redis.title("前置处理器写入key")
                    .config(config -> config.datasource("redis_source").command("set").args("test2_redis_preprocessor,test2_redis_preprocessor")))
            );
            sampler.config(config -> config.datasource("redis_source").command("get").args("test2_redis_preprocessor"));
            sampler.validators(validator -> validator.result(result -> result.expected("test2_redis_preprocessor")));
        });
    }


    @Test
    @RyzeTest
    public void test3() {
        MagicBox.redis(redis -> redis.title("步骤1——插入用户：tick = redis_preprocessor")
                .configureElements(ele -> ele.redis(r -> r.refName("redis_source")
                        .config(config -> config.url("redis://127.0.0.1:6379"))))
                .config(config -> config.datasource("redis_source").command("set").args("test3_redis_sampler,test3_redis_sampler"))
        );

        MagicBox.redis(redis -> redis.title("步骤2——查找用户：tick = test3_redis_sampler")
                .configureElements(ele -> ele.redis(r -> r.refName("redis_source")
                        .config(config -> config.url("redis://127.0.0.1:6379"))))
                .config(config -> config.datasource("redis_source").command("get").args("test3_redis_sampler"))
                .validators(validator -> validator.result(result -> result.expected("test3_redis_sampler")))
        );
    }
}
