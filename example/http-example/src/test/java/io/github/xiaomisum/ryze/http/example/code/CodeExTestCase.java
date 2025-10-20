package io.github.xiaomisum.ryze.http.example.code;

import io.github.xiaomisum.ryze.MagicBox;
import io.github.xiaomisum.ryze.support.testng.RyzeBasicTestcase4TestNG;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

public class CodeExTestCase extends RyzeBasicTestcase4TestNG {

    @Test
    @RyzeTest
    public void test1() {
        MagicBox.http("测试用例- test2()", sampler -> {
            sampler.configureElements(ele -> ele.http(http -> http.config(config -> config.protocol("http").host("127.0.0.1").port("58081"))));
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
    public void test2() {
        MagicBox.http("测试用例- test2()", sampler -> {
            sampler.configureElements(ele -> ele.http(http -> http.config(config -> config.protocol("http").host("127.0.0.1").port("58081"))));
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
}
