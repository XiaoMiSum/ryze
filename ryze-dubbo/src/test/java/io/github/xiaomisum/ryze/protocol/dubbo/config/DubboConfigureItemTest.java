/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * 'Software'), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.xiaomisum.ryze.protocol.dubbo.config;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.xiaomisum.ryze.protocol.dubbo.DubboConstantsInterface.DEF_REF_NAME_KEY;

public class DubboConfigureItemTest {

    @Test
    public void testBuilder() {
        DubboConfigureItem item = DubboConfigureItem.builder()
                .ref("testRef")
                .interfaceName("com.example.DemoService")
                .method("sayHello")
                .parameters("param1", "param2")
                .build();

        Assert.assertEquals(item.getRef(), "testRef");
        Assert.assertEquals(item.getInterfaceName(), "com.example.DemoService");
        Assert.assertEquals(item.getMethod(), "sayHello");
        Assert.assertEquals(item.getParameters().size(), 2);
    }

    @Test
    public void testBuilderWithRegistryAndReference() {
        DubboConfigureItem item = DubboConfigureItem.builder()
                .registry(builder -> builder
                        .address("zookeeper://127.0.0.1:2181")
                        .username("testUser")
                        .password("testPass")
                        .group("testGroup")
                        .version("1.0.0"))
                .reference(builder -> builder
                        .version("1.0.0")
                        .group("testGroup")
                        .timeout(3000)
                        .retries(2)
                        .async(true)
                        .loadBalance("random"))
                .build();

        Assert.assertNotNull(item.getRegistry());
        Assert.assertEquals(item.getRegistry().getAddress(), "zookeeper://127.0.0.1:2181");
        Assert.assertEquals(item.getRegistry().getUsername(), "testUser");
        Assert.assertEquals(item.getRegistry().getPassword(), "testPass");
        Assert.assertEquals(item.getRegistry().getGroup(), "testGroup");
        Assert.assertEquals(item.getRegistry().getVersion(), "1.0.0");

        Assert.assertNotNull(item.getReference());
        Assert.assertEquals(item.getReference().getVersion(), "1.0.0");
        Assert.assertEquals(item.getReference().getGroup(), "testGroup");
        Assert.assertEquals(item.getReference().getTimeout().intValue(), 3000);
        Assert.assertEquals(item.getReference().getRetries().intValue(), 2);
        Assert.assertTrue(item.getReference().getAsync());
        Assert.assertEquals(item.getReference().getLoadBalance(), "random");
    }

    @Test
    public void testBuilderWithParameterTypes() {
        DubboConfigureItem item = DubboConfigureItem.builder()
                .interfaceName("com.example.DemoService")
                .method("sayHello")
                .parameterTypes(Arrays.asList("java.lang.String", "java.lang.Integer"))
                .parameters("test", 123)
                .build();

        Assert.assertEquals(item.getInterfaceName(), "com.example.DemoService");
        Assert.assertEquals(item.getMethod(), "sayHello");
        Assert.assertEquals(item.getParameterTypes().size(), 2);
        Assert.assertEquals(item.getParameterTypes().get(0), "java.lang.String");
        Assert.assertEquals(item.getParameterTypes().get(1), "java.lang.Integer");
        Assert.assertEquals(item.getParameters().size(), 2);
        Assert.assertEquals(item.getParameters().get(0), "test");
        Assert.assertEquals(item.getParameters().get(1), 123);
    }

    @Test
    public void testBuilderWithParameterTypesFromClass() {
        DubboConfigureItem item = DubboConfigureItem.builder()
                .interfaceName("com.example.DemoService")
                .method("sayHello")
                .parameterTypes(String.class, Integer.class)
                .parameters("test", 123)
                .build();

        Assert.assertEquals(item.getParameterTypes().size(), 2);
        Assert.assertEquals(item.getParameterTypes().get(0), "java.lang.String");
        Assert.assertEquals(item.getParameterTypes().get(1), "java.lang.Integer");
    }

    @Test
    public void testBuilderWithAttachmentArgs() {
        Map<String, String> attachments = new HashMap<>();
        attachments.put("traceId", "12345");
        attachments.put("spanId", "67890");

        DubboConfigureItem item = DubboConfigureItem.builder()
                .interfaceName("com.example.DemoService")
                .method("sayHello")
                .attachmentArgs(attachments)
                .build();

        Assert.assertEquals(item.getInterfaceName(), "com.example.DemoService");
        Assert.assertEquals(item.getMethod(), "sayHello");
        Assert.assertEquals(item.getAttachmentArgs().size(), 2);
        Assert.assertEquals(item.getAttachmentArgs().get("traceId"), "12345");
        Assert.assertEquals(item.getAttachmentArgs().get("spanId"), "67890");
    }

    @Test
    public void testBuilderWithSingleAttachmentArg() {
        DubboConfigureItem item = DubboConfigureItem.builder()
                .interfaceName("com.example.DemoService")
                .method("sayHello")
                .attachmentArgs("traceId", "12345")
                .build();

        Assert.assertEquals(item.getAttachmentArgs().size(), 1);
        Assert.assertEquals(item.getAttachmentArgs().get("traceId"), "12345");
    }

    @Test
    public void testBuilderWithInterfaceClass() {
        DubboConfigureItem item = DubboConfigureItem.builder()
                .interfaceName(List.class)
                .method("size")
                .build();

        Assert.assertEquals(item.getInterfaceName(), "java.util.List");
    }

    @Test
    public void testMerge() {
        DubboConfigureItem baseItem = DubboConfigureItem.builder()
                .ref("baseRef")
                .interfaceName("com.example.BaseService")
                .method("baseMethod")
                .build();

        DubboConfigureItem otherItem = DubboConfigureItem.builder()
                .ref("otherRef")
                .interfaceName("com.example.OtherService")
                .build();

        DubboConfigureItem mergedItem = baseItem.merge(otherItem);

        // Check that properties from baseItem take precedence
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getInterfaceName(), "com.example.BaseService");
        Assert.assertEquals(mergedItem.getMethod(), "baseMethod");
    }

    @Test
    public void testMergeWithNull() {
        DubboConfigureItem baseItem = DubboConfigureItem.builder()
                .ref("baseRef")
                .interfaceName("com.example.BaseService")
                .build();

        DubboConfigureItem mergedItem = baseItem.merge(null);

        // Should be a copy of the base item
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getInterfaceName(), "com.example.BaseService");
    }

    @Test
    public void testMergeWithComplexConfig() {
        DubboConfigureItem baseItem = DubboConfigureItem.builder()
                .registry(builder -> builder.address("zookeeper://127.0.0.1:2181"))
                .reference(builder -> builder.version("1.0.0"))
                .interfaceName("com.example.BaseService")
                .method("baseMethod")
                .parameterTypes("java.lang.String")
                .parameters("param1")
                .attachmentArgs("key1", "value1")
                .build();

        DubboConfigureItem otherItem = DubboConfigureItem.builder()
                .registry(builder -> builder.username("user"))
                .reference(builder -> builder.group("test"))
                .interfaceName("com.example.OtherService")
                .method("otherMethod")
                .parameterTypes("java.lang.Integer")
                .parameters(123)
                .attachmentArgs("key2", "value2")
                .build();

        DubboConfigureItem mergedItem = baseItem.merge(otherItem);

        // Check that properties from baseItem take precedence
        Assert.assertEquals(mergedItem.getRegistry().getAddress(), "zookeeper://127.0.0.1:2181");
        Assert.assertNull(mergedItem.getRegistry().getUsername());
        Assert.assertEquals(mergedItem.getReference().getVersion(), "1.0.0");
        Assert.assertNull(mergedItem.getReference().getGroup());
        Assert.assertEquals(mergedItem.getInterfaceName(), "com.example.BaseService");
        Assert.assertEquals(mergedItem.getMethod(), "baseMethod");
        Assert.assertEquals(mergedItem.getParameterTypes().getFirst(), "java.lang.String");
        Assert.assertEquals(mergedItem.getParameters().getFirst(), "param1");
        Assert.assertTrue(mergedItem.getAttachmentArgs().containsKey("key1"));
        // OtherItem has attachment args, be not merged
        Assert.assertFalse(mergedItem.getAttachmentArgs().containsKey("key2"));
    }

    @Test
    public void testEvaluate() {
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.getLocalVariablesWrapper().put("ref", "testRef");
        context.getLocalVariablesWrapper().put("interfaceName", "com.example.DemoService");
        context.getLocalVariablesWrapper().put("method", "sayHello");
        context.getLocalVariablesWrapper().put("address", "zookeeper://127.0.0.1:2181");

        DubboConfigureItem item = DubboConfigureItem.builder()
                .ref("${ref}")
                .interfaceName("${interfaceName}")
                .method("${method}")
                .registry(builder -> builder.address("${address}"))
                .build();

        DubboConfigureItem evaluatedItem = item.evaluate(context);

        Assert.assertEquals(evaluatedItem.getRef(), "testRef");
        Assert.assertEquals(evaluatedItem.getInterfaceName(), "com.example.DemoService");
        Assert.assertEquals(evaluatedItem.getMethod(), "sayHello");
        Assert.assertEquals(evaluatedItem.getRegistry().getAddress(), "zookeeper://127.0.0.1:2181");
    }

    @Test
    public void testGettersWithDefaults() {
        DubboConfigureItem item = new DubboConfigureItem();

        // Test default values
        Assert.assertEquals(item.getRef(), DEF_REF_NAME_KEY);
        Assert.assertNull(item.getInterfaceName());
        Assert.assertNull(item.getMethod());
        Assert.assertNull(item.getParameterTypes());
        Assert.assertNull(item.getParameters());
        Assert.assertNull(item.getAttachmentArgs());
        Assert.assertNull(item.getRegistry());
        Assert.assertNull(item.getReference());
    }

    @Test
    public void testParameterTypeAutoInference() {
        DubboConfigureItem item = DubboConfigureItem.builder()
                .parameters("test", 123, true)
                .build();

        List<String> parameterTypes = item.getParameterTypes();
        Assert.assertNotNull(parameterTypes);
        Assert.assertEquals(parameterTypes.size(), 3);
        Assert.assertEquals(parameterTypes.get(0), "java.lang.String");
        Assert.assertEquals(parameterTypes.get(1), "java.lang.Integer");
        Assert.assertEquals(parameterTypes.get(2), "java.lang.Boolean");
    }

    @Test
    public void testReferenceGettersWithDefaults() {
        DubboConfigureItem.Reference reference = new DubboConfigureItem.Reference();

        // Test default values
        Assert.assertNull(reference.getVersion());
        Assert.assertNull(reference.getGroup());
        Assert.assertEquals(reference.getRetries().intValue(), 1);
        Assert.assertEquals(reference.getTimeout().intValue(), 5000);
        Assert.assertFalse(reference.getAsync());
        Assert.assertNull(reference.getLoadBalance());
    }

    @Test
    public void testRegistryGettersWithDefaults() {
        DubboConfigureItem.Registry registry = new DubboConfigureItem.Registry();

        // Test default values
        Assert.assertNull(registry.getAddress());
        Assert.assertNull(registry.getUsername());
        Assert.assertNull(registry.getPassword());
        Assert.assertNull(registry.getGroup());
        Assert.assertNull(registry.getVersion());
    }

    @Test
    public void testRegistryMerge() {
        DubboConfigureItem.Registry baseRegistry = DubboConfigureItem.Registry.builder()
                .address("zookeeper://127.0.0.1:2181")
                .username("baseUser")
                .group("baseGroup")
                .build();

        DubboConfigureItem.Registry otherRegistry = DubboConfigureItem.Registry.builder()
                .address("zookeeper://127.0.0.1:2182")
                .password("otherPass")
                .version("2.0.0")
                .build();

        DubboConfigureItem.Registry mergedRegistry = baseRegistry.merge(otherRegistry);

        // Check that properties from baseRegistry take precedence
        Assert.assertEquals(mergedRegistry.getAddress(), "zookeeper://127.0.0.1:2181");
        Assert.assertEquals(mergedRegistry.getUsername(), "baseUser");
        Assert.assertEquals(mergedRegistry.getPassword(), "otherPass");
        Assert.assertEquals(mergedRegistry.getGroup(), "baseGroup");
        Assert.assertEquals(mergedRegistry.getVersion(), "2.0.0");
    }

    @Test
    public void testReferenceMerge() {
        DubboConfigureItem.Reference baseReference = DubboConfigureItem.Reference.builder()
                .version("1.0.0")
                .group("baseGroup")
                .timeout(1000)
                .async(true)
                .build();

        DubboConfigureItem.Reference otherReference = DubboConfigureItem.Reference.builder()
                .version("2.0.0")
                .group("otherGroup")
                .retries(5)
                .loadBalance("random")
                .build();

        DubboConfigureItem.Reference mergedReference = baseReference.merge(otherReference);

        // Check that properties from baseReference take precedence
        Assert.assertEquals(mergedReference.getVersion(), "1.0.0");
        Assert.assertEquals(mergedReference.getGroup(), "baseGroup");
        Assert.assertEquals(mergedReference.getTimeout().intValue(), 1000);
        Assert.assertEquals(mergedReference.getRetries().intValue(), 5);
        Assert.assertTrue(mergedReference.getAsync());
        Assert.assertEquals(mergedReference.getLoadBalance(), "random");
    }

    @Test
    public void testSettersAndGetters() {
        DubboConfigureItem item = new DubboConfigureItem();

        DubboConfigureItem.Registry registry = new DubboConfigureItem.Registry();
        registry.setAddress("zookeeper://127.0.0.1:2181");

        DubboConfigureItem.Reference reference = new DubboConfigureItem.Reference();
        reference.setVersion("1.0.0");

        item.setRegistry(registry);
        item.setReference(reference);
        item.setInterfaceName("com.example.DemoService");
        item.setMethod("sayHello");
        item.setParameterTypes(List.of("java.lang.String"));
        item.setParameters(List.of("test"));

        Map<String, String> attachments = new HashMap<>();
        attachments.put("key", "value");
        item.setAttachmentArgs(attachments);
        item.setRef("testRef");

        Assert.assertEquals(item.getRegistry().getAddress(), "zookeeper://127.0.0.1:2181");
        Assert.assertEquals(item.getReference().getVersion(), "1.0.0");
        Assert.assertEquals(item.getInterfaceName(), "com.example.DemoService");
        Assert.assertEquals(item.getMethod(), "sayHello");
        Assert.assertEquals(item.getParameterTypes().getFirst(), "java.lang.String");
        Assert.assertEquals(item.getParameters().getFirst(), "test");
        Assert.assertEquals(item.getAttachmentArgs().get("key"), "value");
        Assert.assertEquals(item.getRef(), "testRef");
    }
}