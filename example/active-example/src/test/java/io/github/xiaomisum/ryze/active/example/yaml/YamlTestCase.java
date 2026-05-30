package io.github.xiaomisum.ryze.active.example.yaml;

import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.active.example.ActiveTestListener;
import io.github.xiaomisum.ryze.protocol.active.sampler.ActiveSampler;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import io.github.xiaomisum.ryze.testelement.TestElement;
import io.github.xiaomisum.ryze.testelement.TestSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(ActiveTestListener.class)
public class YamlTestCase {


    /**
     * 普通TestNg测试用例中执行 ryze yaml模板测试用例
     */
    @Test
    public void test1() {
        var result = Ryze.start("测试用例/active.yaml");
        // 非 ryze-testng 环境，需要自行断言测试结果，否则 allure 报告最外层的测试会标记成功
        assert result.getStatus().isPassed();
    }

    /**
     * ryze-testng 环境下执行 ryze yaml模板测试用例
     */
    @RyzeTest
    @Test
    public void test2() {
        Ryze.start("取样器/active_sampler_topic0.yaml");
    }

    /**
     * 🚀 推荐使用方式
     * ryze-testng 环境下 自动执行 ryze yaml模板测试用例
     */
    @RyzeTest(value = "测试用例/active.yaml", type = TestSuite.class)
    @Test
    public void test3(TestElement<?> element) {
        // 无需像test2一样 编写执行代码 Ryze.start("取样器/active_sampler_topic0.yaml");
    }

    /**
     * 🚀 推荐使用方式
     * ryze-testng 环境下 自动执行 ryze yaml模板测试用例
     */
    @RyzeTest(value = "取样器/active_sampler_queue0.yaml", type = ActiveSampler.class)
    @Test
    public void test4(TestElement<?> element) {
        // 无需像test2一样 编写执行代码 Ryze.start("取样器/active_sampler_topic0.yaml");
    }

    /**
     * 🚀 推荐使用方式
     * ryze-testng 环境下 自动执行 ryze yaml模板测试用例
     */
    @RyzeTest(value = "取样器/active_sampler_queue1.yaml", type = ActiveSampler.class)
    @Test
    public void test5(TestElement<?> element) {
        // 无需像test2一样 编写执行代码 Ryze.start("取样器/active_sampler_topic0.yaml");
    }

    /**
     * 🚀 推荐使用方式
     * ryze-testng 环境下 自动执行 ryze yaml模板测试用例
     */
    @RyzeTest(value = "取样器/active_sampler_queue2.yaml", type = ActiveSampler.class)
    @Test
    public void test6(TestElement<?> element) {
        // 无需像test2一样 编写执行代码 Ryze.start("取样器/active_sampler_topic0.yaml");
    }

    /**
     * 🚀 推荐使用方式
     * ryze-testng 环境下 自动执行 ryze yaml模板测试用例
     */
    @RyzeTest(value = "取样器/active_sampler_queue3.yaml", type = ActiveSampler.class)
    @Test
    public void test7(TestElement<?> element) {
        // 无需像test2一样 编写执行代码 Ryze.start("取样器/active_sampler_topic0.yaml");
    }
}
