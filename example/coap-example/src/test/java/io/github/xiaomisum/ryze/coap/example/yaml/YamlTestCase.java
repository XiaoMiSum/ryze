package io.github.xiaomisum.ryze.coap.example.yaml;

import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.coap.example.CoapTestListener;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.TestSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(CoapTestListener.class)
public class YamlTestCase {


    /**
     * 普通TestNg测试用例中执行 ryze yaml模板测试用例
     */
    @Test
    public void test1() {
        var result = Ryze.start("测试用例/coap.yaml");
        // 非 ryze-testng 环境，需要自行断言测试结果，否则 allure 报告最外层的测试会标记成功
        assert result.getStatus().isPassed();
    }

    /**
     * ryze-testng 环境下执行 ryze yaml模板测试用例
     */
    @RyzeTest
    @Test
    public void test2() {
        Ryze.start("取样器/coap_get.yaml");
    }

    /**
     * 🚀 推荐使用方式
     * ryze-testng 环境下 自动执行 ryze yaml模板测试用例
     */
    @RyzeTest(value = "测试用例/coap.yaml", type = TestSuite.class)
    @Test
    public void test3(TestElement<?> element) {
        // 无需像test2一样 编写执行代码 Ryze.start("取样器/coap_get.yaml");
    }

    @RyzeTest("取样器/coap_get.yaml")
    @Test
    public void test4(TestElement<?> element) {

    }

    @RyzeTest("取样器/coap_post.yaml")
    @Test
    public void test5(TestElement<?> element) {

    }
}
