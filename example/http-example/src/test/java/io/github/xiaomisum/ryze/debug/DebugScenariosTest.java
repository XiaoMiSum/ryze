package io.github.xiaomisum.ryze.debug;

import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import io.github.xiaomisum.ryze.testelement.TestElement;
import org.testng.annotations.Test;

/**
 * Debug Sampler 多场景 TestNG 验证测试类
 * 严格参照 example/YamlTestCase.java 官方示例写法
 */
public class DebugScenariosTest {

    /**
     * 场景1: testsuite(项目) → testsuite(模块) → testsuite(接口1) → testsuite(用例) → sampler(步骤)
     */
    @RyzeTest("debug-scenarios/scene1-full-nested.yaml")
    @Test
    public void testScene1(TestElement<?> element) {
        // Ryze 框架自动执行，层级 label 由 report 模块根据 TestContainer 嵌套栈回填
    }

    /**
     * 场景2: testsuite(模块) → testsuite(接口1) → testsuite(用例) → sampler(步骤)
     */
    @RyzeTest("debug-scenarios/scene2-module-nested.yaml")
    @Test
    public void testScene2(TestElement<?> element) {
        // Ryze 框架自动执行，层级 label 由 report 模块根据 TestContainer 嵌套栈回填
    }

    /**
     * 场景3: testsuite(接口1) → testsuite(用例) → sampler(步骤)
     */
    @RyzeTest("debug-scenarios/scene3-interface-nested.yaml")
    @Test
    public void testScene3(TestElement<?> element) {
        // Ryze 框架自动执行，层级 label 由 report 模块根据 TestContainer 嵌套栈回填
    }

    /**
     * 场景4: testsuite(用例) → sampler(步骤)
     */
    @RyzeTest("debug-scenarios/scene4-case-nested.yaml")
    @Test
    public void testScene4(TestElement<?> element) {
        // Ryze 框架自动执行，层级 label 由 report 模块根据 TestContainer 嵌套栈回填
    }

    /**
     * 场景5: sampler(用例) —— 单取样器作为顶级元素
     */
    @RyzeTest("debug-scenarios/scene5-standalone.yaml")
    @Test
    public void testScene5(TestElement<?> element) {
        // Ryze 框架自动执行，层级 label 由 report 模块根据 TestContainer 嵌套栈回填
    }
}
