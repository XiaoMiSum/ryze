package io.github.xiaomisum.ryze.proto.example.yaml;

import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.proto.example.mock.MockProtoHttpServer;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import io.github.xiaomisum.ryze.testelement.TestElement;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class YamlTestCase {

    @BeforeSuite
    public void setup() {
        MockProtoHttpServer.start();
    }

    @AfterSuite
    public void teardown() {
        MockProtoHttpServer.stop();
    }

    /**
     * 普通TestNg测试用例中执行 ryze yaml模板测试用例
     */
    @Test
    public void test1() {
        var result = Ryze.start("测试用例/proto.yaml");
        // 非 ryze-testng 环境，需要自行断言测试结果，否则 allure 报告最外层的测试会标记成功
        assert result.getStatus().isPassed();
    }

    /**
     * ryze-testng 环境下执行 ryze yaml模板测试用例
     */
    @RyzeTest
    @Test
    public void test2() {
        Ryze.start("取样器/proto_sampler_http.yaml");
    }

    /**
     * 🚀 推荐使用方式
     * ryze-testng 环境下 自动执行 ryze yaml模板测试用例
     */
    @RyzeTest("测试用例/proto.yaml")
    @Test
    public void test3(TestElement<?> element) {
        // 无需像test2一样 编写执行代码 Ryze.start("取样器/proto_sampler_http.yaml");
    }

    @RyzeTest("测试用例/proto_ws.yaml")
    @Test
    public void test4(TestElement<?> element) {
    }

    @RyzeTest("取样器/proto_sampler_http.yaml")
    @Test
    public void test5(TestElement<?> element) {
    }

    @RyzeTest("取样器/proto_sampler_ws.yaml")
    @Test
    public void test6(TestElement<?> element) {
    }
}
