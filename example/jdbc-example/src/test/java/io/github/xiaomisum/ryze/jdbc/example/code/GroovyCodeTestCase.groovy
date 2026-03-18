package io.github.xiaomisum.ryze.jdbc.example.code

import io.github.xiaomisum.ryze.MagicBox
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest
import org.testng.annotations.Test

class GroovyCodeTestCase {


    @Test
    @RyzeTest
    void test1() {
        MagicBox.suite("测试用例", {
            variables("id", 1)
            variables { put("tick", "ryze") }
            variables Map.of("a", 1, "b", 2)
            configureElements {
                jdbc {
                    refName "jdbc_source"
                    config {
                        url "jdbc:mysql://127.0.0.1:3306/ryze-test?user=root&password=123456qq!&characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false"
                    }
                }
            }

            preprocessors {
                jdbc {
                    config {
                        datasource "jdbc_source"
                        sql "insert into t_001 (tick, name) values (\"jdbc_preprocessor\", \"jdbc_preprocessor\");"
                    }
                }
            }

            postprocessors {
                jdbc {
                    config {
                        datasource "jdbc_source"
                        sql "truncate table t_001;"
                    }
                }
            }
            children {
                jdbc {
                    title "步骤1"
                    variables("username", "ryze")
                    config {
                        datasource "jdbc_source"
                        sql "select * from t_001  where tick = \"jdbc_preprocessor\";"
                    }
                    validators {
                        json { field "\$.name" expected "jdbc_preprocessor" }
                    }
                }
            }
            children {
                jdbc {
                    title "步骤2"
                    config {
                        datasource "jdbc_source"
                        sql "update t_001  set name = \"步骤2:jdbcSampler\" where tick = \"jdbc_preprocessor\";"
                    }
                }
            }
            children {
                jdbc {
                    title "步骤3"
                    variables("username", "ryze")
                    config {
                        datasource "jdbc_source"
                        sql "select * from t_001  where tick = \"jdbc_preprocessor\";"
                    }
                    validators {
                        json { field "\$.name" expected "步骤2:jdbcSampler" }
                    }
                }
            }
        })
    }

    @Test
    @RyzeTest
    void test2() {
        MagicBox.jdbc("测试用例- test2()", sampler -> {
            configureElements {
                jdbc {
                    refName "jdbc_source"
                    config {
                        url "jdbc:mysql://127.0.0.1:3306/ryze-test?user=root&password=123456qq!&characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false"
                    }
                }
            }
            preprocessors {
                jdbc {
                    title "前置处理器写入用户"
                    config {
                        datasource "jdbc_source"
                        sql "insert into t_001 (tick, name) values (\"jdbc_preprocessor\", \"ryze_jdbc_preprocessor\");"
                    }
                }
            }
            postprocessors {
                jdbc {
                    config {
                        datasource "jdbc_source"
                        sql "truncate table t_001;"
                    }
                }
            }
            config {
                datasource "jdbc_source"
                sql "select * from t_001  where tick = \"jdbc_preprocessor\";"
            }
            validators {
                json { field "\$.name" expected "ryze_jdbc_preprocessor" }
            }
        })
    }


    @Test
    @RyzeTest
    void test3() {
        MagicBox.jdbc({
            title "步骤1——插入用户：tick = jdbc_preprocessor"
            configureElements {
                jdbc {
                    refName "jdbc_source"
                    config {
                        url "jdbc:mysql://127.0.0.1:3306/ryze-test?user=root&password=123456qq!&characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false"
                    }
                }
            }
            config {
                datasource "jdbc_source"
                sql "insert into t_001 (tick, name) values (\"http_sampler\", \"ryze_http_sampler\");"
            }
        })

        MagicBox.jdbc({
            title "步骤2——查找用户：tick = ryze_http_sampler"
            configureElements {
                jdbc {
                    refName "jdbc_source"
                    config {
                        url "jdbc:mysql://127.0.0.1:3306/ryze-test?user=root&password=123456qq!&characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2b8&failOverReadOnly=false"
                    }
                }
            }
            postprocessors {
                jdbc {
                    config {
                        datasource "jdbc_source"
                        sql "truncate table t_001;"
                    }
                }
            }
            config {
                datasource "jdbc_source"
                sql "select * from t_001  where tick = \"http_sampler\";"
            }
            assertions {
                json { field "\$.name" expected "ryze_http_sampler" }
            }
        })
    }
}
