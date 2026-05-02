package io.github.xiaomisum.ryze.dubbo.example.code;

import io.github.xiaomisum.ryze.Ryze;
import io.github.xiaomisum.ryze.protocol.dubbo.DubboMagicBox;
import io.github.xiaomisum.ryze.protocol.dubbo.builder.DubboConfigureElementsBuilder;
import io.github.xiaomisum.ryze.protocol.dubbo.builder.DubboPostprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.dubbo.builder.DubboPreprocessorsBuilder;
import io.github.xiaomisum.ryze.protocol.dubbo.builder.DubboSamplersBuilder;
import io.github.xiaomisum.ryze.support.testng.annotation.RyzeTest;
import org.testng.annotations.Test;

import java.util.Map;

public class CodeTestCase {

    @Test
    @RyzeTest
    public void test1() {
        Ryze.suite("测试用例", suite -> {
            suite.variables("id", 1);
            suite.variables(var -> var.put("tick", "dubbo_preprocessor"));
            suite.variables(Map.of("a", 1, "b", 2));
            suite.configureElements(DubboConfigureElementsBuilder.builder()
                    .dubbo(dubbo -> dubbo.config(config -> config
                            .registry(registry -> registry.address("zookeeper://localhost:42181"))
                            .reference(reference -> reference.retries(1).timeout(5000).async(false).loadBalance("random"))).build()
                    ).build());
            suite.preprocessors(DubboPreprocessorsBuilder.builder()
                    .dubbo(dubbo -> dubbo.config(config -> config.interfaceName("io.github.xiaomisum.ryze.dubbo.example.DemoService")
                            .method("sayHello")
                            .parameters("${tick}")))
                    .build());
            suite.postprocessors(DubboPostprocessorsBuilder.builder()
                    .dubbo(dubbo -> dubbo.config(config -> config.interfaceName("io.github.xiaomisum.ryze.dubbo.example.DemoService")
                            .method("sayHello")
                            .parameters("dubbo_postprocessor")))
                    .build());
            suite.children(DubboSamplersBuilder.builder()
                    .dubbo(dubbo -> dubbo.title("步骤1").variables("username", "ryze")
                            .config(config -> config.interfaceName("io.github.xiaomisum.ryze.dubbo.example.DemoService")
                                    .method("sayHello").parameters("步骤1: dubbo_sampler")))
                    .build());
            suite.children(DubboSamplersBuilder.builder()
                    .dubbo(dubbo -> dubbo.title("步骤1").variables("username", "ryze")
                            .config(config -> config.interfaceName("io.github.xiaomisum.ryze.dubbo.example.DemoService")
                                    .method("sayHello").parameters("步骤2: dubbo_sampler")))
                    .build());
        });
    }

    @Test
    @RyzeTest
    public void test2() {
        DubboMagicBox.dubbo("测试用例- test2()", sampler -> {
            sampler.configureElements(DubboConfigureElementsBuilder.builder()
                    .dubbo(dubbo -> dubbo.config(config -> config
                            .registry(registry -> registry.address("zookeeper://localhost:42181"))
                            .reference(reference -> reference.retries(1).timeout(5000).async(false).loadBalance("random"))).build()
                    ).build());
            sampler.preprocessors(DubboPreprocessorsBuilder.builder()
                    .dubbo(dubbo -> dubbo.config(config -> config.interfaceName("io.github.xiaomisum.ryze.dubbo.example.DemoService")
                            .method("sayHello")
                            .parameters("dubbo_preprocessor")))
                    .build());
            sampler.config(config -> config.interfaceName("io.github.xiaomisum.ryze.dubbo.example.DemoService")
                    .method("sayHello").parameters("test2(): dubbo_sampler")).build();
        });
    }


    @Test
    @RyzeTest
    public void test3() {
        DubboMagicBox.dubbo("测试用例- test3()-1", sampler -> {
            sampler.configureElements(DubboConfigureElementsBuilder.builder()
                    .dubbo(dubbo -> dubbo.config(config -> config
                            .registry(registry -> registry.address("zookeeper://localhost:42181"))
                            .reference(reference -> reference.retries(1).timeout(5000).async(false).loadBalance("random"))).build()
                    ).build());
            sampler.config(config -> config.interfaceName("io.github.xiaomisum.ryze.dubbo.example.DemoService")
                    .method("sayHello").parameters("步骤1: dubbo_sampler")).build();
        });

        DubboMagicBox.dubbo("测试用例- test3()-2", sampler -> {
            sampler.configureElements(DubboConfigureElementsBuilder.builder()
                    .dubbo(dubbo -> dubbo.config(config -> config
                            .registry(registry -> registry.address("zookeeper://localhost:42181"))
                            .reference(reference -> reference.retries(1).timeout(5000).async(false).loadBalance("random"))).build()
                    ).build());
            sampler.config(config -> config.interfaceName("io.github.xiaomisum.ryze.dubbo.example.DemoService")
                    .method("sayHello").parameters("步骤2: dubbo_sampler")).build();
        });
    }
}
