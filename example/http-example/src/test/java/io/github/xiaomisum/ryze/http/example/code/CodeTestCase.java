package io.github.xiaomisum.ryze.http.example.code;

import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class CodeTestCase {

    @Test
    @RyzeTest
    public void test1() {
        Ryze.suite("测试用例", suite -> {
            suite.variables("id", 1);
            suite.variables("t_body", Collections.of("id", "ryze", "name", "ryze_http_preprocessor", "age", 0));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.variables(var -> var.put("c", 3).put("d", 4));
            suite.configureElements(ele ->
                    ele.http(http -> http.config(config -> config.baseUrl("http://127.0.0.1:58081"))));
            suite.preprocessors(pre ->
                    pre.http(http -> {
                                http.title("前置处理器新增用户");
                                http.config(config -> config.method("PUT").path("/user").body("${t_body}"));
                                http.extractors(extract -> extract.json("t_id", "$.data.id"));
                            }
                    )
            );
            suite.children(child -> {
                child.http(http -> http.title("步骤1——获取用户：id = ${id}")
                        .config(config -> config.method("GET").path("/user/${id}"))
                        .validators(validator -> validator.json("$.data.id", "${id}")));
                child.http(http -> http.title("步骤2——修改用户：id = ${t_id}")
                        .config(config -> config.method("POST").path("/user").body(body -> {
                            body.put("id", "ryze");
                            body.put("name", "ryze_http_sampler");
                            body.put("age", 1);
                        }))
                        .validators(validator -> validator.httpStatus(200)));
                child.http(http -> http.title("步骤3——获取用户：id = ${t_body.id}")
                        .config(config -> config.method("GET").path("/user/${t_body.id}"))
                        .validators(validator -> validator.json("$.data.name", "ryze_http_sampler")));

            });
        });
    }

    @Test
    @RyzeTest
    public void test2() {
        Ryze.http("测试用例- test2()", sampler -> {
            sampler.configureElements(ele -> ele.http(http -> http.config(config -> config.baseUrl("http://127.0.0.1:58081"))));
            sampler.preprocessors(pre -> pre.http(http ->
                    http.title("前置处理器修改用户：ryze").config(config -> config.method("POST").path("/user").body(body -> {
                                body.put("id", "ryze");
                                body.put("name", "ryze_http_preprocessor");
                                body.put("age", 1);
                            })).extractors(extract -> extract.json("name", "$.data.name"))
                            .extractors(extract -> extract.json("id", "$.data.id"))));
            sampler.config(config -> config.method("GET").path("/user/${id}"));
            sampler.validators(validator -> validator.json("$.data.name", "${name}"));
        });
    }

    @Test
    @RyzeTest
    public void test3() {
        Ryze.http(http -> {
            http.variables("id", 1);
            http.title("步骤1——获取用户：id = ${id}");
            http.config(config -> config.baseUrl("http://127.0.0.1:58081")
                    .method("GET").path("/user/${id}"));
            http.assertions(assertions -> assertions.json("$.data.id", "${id}"));
        });

        Ryze.http(http -> {
            http.title("步骤2——修改用户：id = ryze");
            http.config(config -> config.baseUrl("http://127.0.0.1:58081")
                    .method("POST").path("/user").body(body -> {
                        body.put("id", "ryze");
                        body.put("name", "ryze_http_sampler");
                        body.put("age", 1);
                    }));
            http.assertions(assertions -> assertions.httpStatus(200));
        });

        Ryze.http(http -> {
            http.title("步骤3——获取用户：id = ryze");
            http.config(config -> config.baseUrl("http://127.0.0.1:58081")
                    .method("GET").path("/user/ryze"));
            http.assertions(assertions -> assertions.json("$.data.name", "ryze_http_sampler"));
        });
    }
}
