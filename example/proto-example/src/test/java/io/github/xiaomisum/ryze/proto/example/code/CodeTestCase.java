package io.github.xiaomisum.ryze.proto.example.code;

import io.github.xiaomisum.ryze.protocol.proto.ProtoMagicBox;
import io.github.xiaomisum.ryze.protocol.proto.builder.ProtoConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.proto.builder.ProtoPreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.proto.builder.ProtoSamplersBuilder;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class CodeTestCase {

    @Test
    @RyzeTest
    public void test1() {
        ProtoMagicBox.suite("测试用例-http-protobuf-test1", suite -> {
            suite.variables("id", 1);
            suite.variables("t_body", Collections.of("id", 1, "name", "ryze_http_proto_preprocessor", "age", 0));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.variables(var -> var.put("c", 3).put("d", 4));
            suite.configureElements(ProtoConfigureElementsBuilder.class, ele ->
                    ele.proto(proto -> proto.config(config -> config.protocol("http").host("127.0.0.1").port("8080")
                            .protoDesc(b -> b.descPath("D:\\Github\\ryze\\example\\proto-example\\user.desc")
                                    .requestMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User")
                                    .responseMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User")))));
            suite.preprocessors(ProtoPreprocessorsBuilder.class, pre -> pre.proto(proto -> {
                                proto.title("前置处理器新增用户");
                                proto.config(config -> config.method("PUT").path("/user").body("${t_body}"));
                                proto.extractors(extract -> extract.json("t_id", "$.id"));
                            }
                    )
            );
            suite.children(ProtoSamplersBuilder.class, child -> {
                child.proto(proto -> proto.title("步骤1——获取用户：id = ${id}")
                        .config(config -> config.method("GET").path("/user/${id}"))
                        .validators(validator -> validator.json("$.id", "${id}")));
                child.proto(proto -> proto.title("步骤2——修改用户：id = ${t_id}")
                        .config(config -> config.method("POST").path("/user").body(body -> {
                            body.put("id", 1);
                            body.put("name", "ryze_http_proto_sampler");
                            body.put("age", 1);
                        }))
                        .validators(validator -> validator.json("$.name", "ryze_http_proto_sampler")));
                child.proto(proto -> proto.title("步骤3——获取用户：id = ${t_body.id}")
                        .config(config -> config.method("GET").path("/user/${t_body.id}"))
                        .validators(validator -> validator.json("$.name", "ryze_http_proto_sampler")));

            });
        });
    }

    @Test
    @RyzeTest
    public void test2() {
        ProtoMagicBox.proto("测试用例-http-protobuf-test2", sampler -> {
            sampler.configureElements(ProtoConfigureElementsBuilder.class, ele ->
                    ele.proto(proto -> proto.config(config -> config.protocol("http").host("127.0.0.1").port("8080")
                            .protoDesc(b -> b.descPath("D:\\Github\\ryze\\example\\proto-example\\user.desc")
                                    .requestMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User")
                                    .responseMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User")))));
            sampler.preprocessors(ProtoPreprocessorsBuilder.class, pre -> pre.proto(proto ->
                    proto.title("前置处理器修改用户：ryze").config(config -> config.method("POST").path("/user").body(body -> {
                                body.put("id", 1);
                                body.put("name", "ryze_http_proto_preprocessor");
                                body.put("age", 1);
                            })).extractors(extract -> extract.json("name", "$.name"))
                            .extractors(extract -> extract.json("id", "$.id"))));
            sampler.config(config -> config.method("GET").path("/user/${id}"));
            sampler.validators(validator -> validator.json("$.name", "${name}"));
        });
    }

    @Test
    @RyzeTest
    public void test3() {
        ProtoMagicBox.suite("测试用例-websocket-protobuf-test3", suite -> {
            suite.variables("id", 1);
            suite.variables("t_body", Collections.of("id", 1, "name", "ryze_websocket_proto_preprocessor", "age", 0));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.variables(var -> var.put("c", 3).put("d", 4));
            suite.configureElements(ProtoConfigureElementsBuilder.class, ele ->
                    ele.proto(proto -> proto.config(config -> config.protocol("ws").host("127.0.0.1").port("8080")
                            .protoDesc(b -> b.descPath("D:\\Github\\ryze\\example\\proto-example\\user.desc")
                                    .requestMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User")
                                    .responseMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User"))
                    )));
            suite.preprocessors(ProtoPreprocessorsBuilder.class, pre -> pre.proto(proto -> {
                                proto.title("前置处理器-websocket-protobuf-test3");
                                proto.config(config -> config.path("/user").body("${t_body}").responsePattern("name"));
                                proto.extractors(extract -> extract.json("t_id", "$.id"));
                            }
                    )
            );
            suite.children(ProtoSamplersBuilder.class, child -> child.proto(proto ->
                    proto.title("取样器-websocket-protobuf-test3")
                            .config(config -> config.path("/user").body(body -> {
                                body.put("id", 1);
                                body.put("name", "ryze_websocket_proto_sampler");
                                body.put("age", 1);
                            }).responsePattern("name"))
                            .validators(validator -> validator.json("$.name", "ryze_websocket_proto_sampler")))
            );
        });
    }

    @Test
    @RyzeTest
    public void test4() {
        ProtoMagicBox.proto("测试用例-websocket-protobuf-test4", sampler -> {
            sampler.configureElements(ProtoConfigureElementsBuilder.class, ele -> ele.proto(proto ->
                    proto.config(config -> config.protocol("ws").host("127.0.0.1").port("8080")
                            .protoDesc(b -> b.descPath("D:\\Github\\ryze\\example\\proto-example\\user.desc")
                                    .requestMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User")
                                    .responseMessageName("io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User")))));
            sampler.preprocessors(ProtoPreprocessorsBuilder.class, pre -> pre.proto(proto ->
                    proto.title("前置处理器-websocket-protobuf-test4").config(config -> config.path("/user").body(body -> {
                                body.put("id", 1);
                                body.put("name", "ryze_websocket_proto_preprocessor");
                                body.put("age", 1);
                            }).responsePattern("name"))
                            .extractors(extract -> extract.json("name", "$.name"))
                            .extractors(extract -> extract.json("id", "$.id"))));
            sampler.config(config -> config.path("/user").body(body -> {
                body.put("id", 1);
                body.put("name", "ryze_websocket_proto_sampler");
                body.put("age", 1);
            }).responsePattern("name"));
            sampler.validators(validator -> validator.json("$.name", "ryze_websocket_proto_sampler"));
        });
    }
}
