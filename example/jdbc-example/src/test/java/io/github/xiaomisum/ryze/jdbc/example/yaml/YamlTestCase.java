package io.github.xiaomisum.ryze.jdbc.example.yaml;

import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.TestSuite;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

public class YamlTestCase {


    /**
     * 普通TestNg测试用例中执行 ryze yaml模板测试用例
     */
    @Test
    public void test1() {
        var result = Ryze.start("测试用例/jdbc.yaml");
        // 非 ryze-testng 环境，需要自行断言测试结果，否则 allure 报告最外层的测试会标记成功
        assert result.getStatus().isPassed();
    }

    /**
     * ryze-testng 环境下执行 ryze yaml模板测试用例
     */
    @RyzeTest
    @Test
    public void test2() {
        Ryze.start("取样器/jdbc_sampler5.yaml");
    }

    /**
     * 🚀 推荐使用方式
     * ryze-testng 环境下 自动执行 ryze yaml模板测试用例
     */
    @RyzeTest(value = "测试用例/jdbc.yaml", type = TestSuite.class)
    @Test
    public void test3(TestElement<?> element) {
        // 无需像test2一样 编写执行代码 Ryze.start("取样器/jdbc_sampler5.yaml");
    }
}
