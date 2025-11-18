package io.github.xiaomisum.ryze.proto.example.code;

import io.github.xiaomisum.ryze.protocol.proto.ProtoMagicBox;
import io.github.xiaomisum.ryze.protocol.proto.builder.ProtoConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.proto.builder.ProtoPreprocessorsBuilder;
import io.github.xiaomisum.ryze.support.testng.RyzeBasicTestcase4TestNG;
import org.testng.annotations.Test;

public class CodeExTestCase extends RyzeBasicTestcase4TestNG {

    @Test
    public void test1() {
        // 基于 http 协议的 protobuf
        ProtoMagicBox.proto("测试用例-http-protobuf", sampler -> {
            sampler.configureElements(ProtoConfigureElementsBuilder.class, ele -> ele.proto(
                    proto -> proto.config(config -> config.protocol("http").host("127.0.0.1").port("8080")
                            .protoDesc(b -> b.descPath("D:\\Github\\ryze\\example\\proto-example\\user.desc")
                                    .requestMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User")
                                    .responseMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User")))));
            sampler.preprocessors(ProtoPreprocessorsBuilder.class, pre -> pre.proto(proto ->
                    proto.title("前置处理器修改用户：ryze").config(config -> config.method("POST").path("/user").body(body -> {
                                body.put("id", 1);
                                body.put("name", "ryze_http_preprocessor");
                                body.put("age", 18);
                            }))
                            .extractors(extract -> extract.json("name", "$.name"))
                            .extractors(extract -> extract.json("id", "$.id"))));
            sampler.config(config -> config.method("GET").path("/user/${id}"));
            sampler.validators(validator -> validator.json("$.name", "${name}"));
        });
    }

    @Test
    public void test2() {
        // 基于 websocket 协议的 protobuf
        ProtoMagicBox.proto("测试用例-websocket-protobuf", sampler -> {
            sampler.configureElements(ProtoConfigureElementsBuilder.class, ele -> ele.proto(
                    proto -> proto.config(config -> config.protocol("ws").host("127.0.0.1").port("8080")
                            .protoDesc(b -> b.descPath("D:\\Github\\ryze\\example\\proto-example\\user.desc")
                                    .requestMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User")
                                    .responseMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User")))));
            sampler.preprocessors(ProtoPreprocessorsBuilder.class, pre -> pre.proto(proto ->
                    proto.title("前置处理器修改用户：ryze").config(config -> config.path("/user").body(body -> {
                                        body.put("id", 1);
                                        body.put("name", "ryze_http_preprocessor");
                                        body.put("age", 18);
                                    }).protoDesc(b -> b.descPath("D:\\Github\\ryze\\example\\proto-example\\user.desc")
                                            .requestMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User")
                                            .responseMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User"))
                                    .responsePattern("name"))
                            .extractors(extract -> extract.json("name", "$.name"))
                            .extractors(extract -> extract.json("id", "$.id"))));
            sampler.config(config -> config.path("/user").body(body -> {
                body.put("id", 1);
                body.put("name", "ryze_http_preprocessor");
                body.put("age", 18);
            }).responsePattern("name"));
            sampler.validators(validator -> validator.json("$.name", "${name}"));
        });
    }
}
