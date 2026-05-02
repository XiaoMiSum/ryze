package io.github.xiaomisum.ryze.jdbc.example.code;

import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class CodeTestCase {

    @Test
    @RyzeTest
    public void test1() {
        Ryze.suite("测试用例", suite -> {
            suite.variables("id", 1);
            suite.variables(var -> var.put("tick", "ryze"));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.configureElements(ele -> ele.jdbc(jdbc -> jdbc.refName("jdbc_source")
                    .config(config -> config.url("jdbc:mysql://127.0.0.1:3306/ryze-test?user=root&password=123456qq!&characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false")
                    )));
            suite.preprocessors(pre ->
                    pre.jdbc(jdbc -> jdbc.config(config -> config.datasource("jdbc_source").sql("insert into t_001 (tick, name) values (\"jdbc_preprocessor\", \"jdbc_preprocessor\");")))
            );
            suite.postprocessors(post -> post.jdbc(jdbc -> jdbc.config(config -> config.datasource("jdbc_source").sql("truncate table t_001;"))));
            suite.children(child -> child.jdbc(jdbc -> jdbc.title("步骤1").variables("username", "ryze")
                    .config(config -> config.datasource("jdbc_source").sql("select * from t_001  where tick = \"jdbc_preprocessor\";"))
                    .validators(validator -> validator.json(json -> json.field("$.name").expected("jdbc_preprocessor")))));
            suite.children(child -> child.jdbc(jdbc -> jdbc.title("步骤2").config(config -> config.datasource("jdbc_source").sql("update t_001  set name = \"步骤2:jdbcSampler\" where tick = \"jdbc_preprocessor\";"))));
            suite.children(child -> child.jdbc(jdbc -> jdbc.title("步骤3").config(config -> config.datasource("jdbc_source").sql("select * from t_001  where tick = \"jdbc_preprocessor\";"))
                    .validators(validator -> validator.json(json -> json.field("$.name").expected("步骤2:jdbcSampler")))));
        });
    }

    @Test
    @RyzeTest
    public void test2() {
        Ryze.jdbc("测试用例- test2()", sampler -> {
            sampler.configureElements(ele -> ele.jdbc(jdbc -> jdbc.refName("jdbc_source")
                    .config(config -> config.url("jdbc:mysql://127.0.0.1:3306/ryze-test?user=root&password=123456qq!&characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false"))
            ));
            sampler.preprocessors(pre -> pre.jdbc(jdbc -> jdbc.title("前置处理器插入用户")
                    .config(config -> config.datasource("jdbc_source").sql("insert into t_001 (tick, name) values (\"jdbc_preprocessor\", \"ryze_jdbc_preprocessor\");")))
            );
            sampler.postprocessors(post -> post.jdbc(jdbc -> jdbc.config(config -> config.datasource("jdbc_source").sql("truncate table t_001;"))));
            sampler.config(config -> config.datasource("jdbc_source").sql("select * from t_001  where tick = \"jdbc_preprocessor\";"));
            sampler.validators(validator -> validator.json(json -> json.field("$.name").expected("ryze_jdbc_preprocessor")));
        });
    }


    @Test
    @RyzeTest
    public void test3() {
        Ryze.jdbc(jdbc -> jdbc.title("步骤1——插入用户：tick = jdbc_preprocessor")
                .configureElements(ele -> ele.jdbc(j -> j.refName("jdbc_source")
                        .config(config -> config.url("jdbc:mysql://127.0.0.1:3306/ryze-test?user=root&password=123456qq!&characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false"))
                ))
                .config(config -> config.datasource("jdbc_source").sql("insert into t_001 (tick, name) values (\"jdbc_preprocessor\", \"ryze_http_sampler\");"))
        );

        Ryze.jdbc(jdbc -> jdbc.title("步骤2——查找用户：tick = jdbc_preprocessor")
                .configureElements(ele -> ele.jdbc(j -> j.refName("jdbc_source")
                        .config(config -> config.url("jdbc:mysql://127.0.0.1:3306/ryze-test?user=root&password=123456qq!&characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false"))
                ))
                .postprocessors(post -> post.jdbc(j -> j.config(config -> config.datasource("jdbc_source").sql("truncate table t_001;"))))
                .config(config -> config.datasource("jdbc_source").sql("select * from t_001  where tick = \"jdbc_preprocessor\";"))
                .assertions(assertions -> assertions.json("$.name", "ryze_http_sampler"))
        );
    }
}
