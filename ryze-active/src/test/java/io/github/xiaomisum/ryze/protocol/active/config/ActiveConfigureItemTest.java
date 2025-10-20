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

package io.github.xiaomisum.ryze.protocol.active.config;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.xiaomisum.ryze.protocol.active.ActiveConstantsInterface.*;

public class ActiveConfigureItemTest {

    @Test
    public void testBuilder() {
        ActiveConfigureItem item = ActiveConfigureItem.builder()
                .ref("testRef")
                .brokerUrl("tcp://localhost:61616")
                .topic("testTopic")
                .queue("testQueue")
                .message("testMessage")
                .username("testUser")
                .password("testPass")
                .build();

        Assert.assertEquals(item.getRef(), "testRef");
        Assert.assertEquals(item.getBrokerUrl(), "tcp://localhost:61616");
        Assert.assertEquals(item.getTopic(), "testTopic");
        Assert.assertEquals(item.getQueue(), "testQueue");
        Assert.assertEquals(item.getMessage(), "testMessage");
        Assert.assertEquals(item.getUsername(), "testUser");
        Assert.assertEquals(item.getPassword(), "testPass");
    }

    @Test
    public void testBuilderWithDifferentMessageTypes() {
        // Test with Map message
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("key1", "value1");
        messageMap.put("key2", 123);

        ActiveConfigureItem item1 = ActiveConfigureItem.builder()
                .message(messageMap)
                .build();

        Assert.assertTrue(item1.getMessage().contains("key1"));
        Assert.assertTrue(item1.getMessage().contains("value1"));

        // Test with String message
        ActiveConfigureItem item2 = ActiveConfigureItem.builder()
                .message("testStringMessage")
                .build();

        Assert.assertEquals(item2.getMessage(), "testStringMessage");
    }

    @Test
    public void testMerge() {
        ActiveConfigureItem baseItem = ActiveConfigureItem.builder()
                .ref("baseRef")
                .brokerUrl("tcp://localhost:61616")
                .topic("baseTopic")
                .username("baseUser")
                .build();

        ActiveConfigureItem otherItem = ActiveConfigureItem.builder()
                .ref("otherRef")
                .brokerUrl("tcp://otherhost:61616")
                .queue("otherQueue")
                .build();

        ActiveConfigureItem mergedItem = baseItem.merge(otherItem);

        // Check that properties from baseItem take precedence
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getBrokerUrl(), "tcp://localhost:61616");
        Assert.assertEquals(mergedItem.getTopic(), "baseTopic");
        Assert.assertEquals(mergedItem.getUsername(), "baseUser");
        Assert.assertEquals(mergedItem.getQueue(), "otherQueue");
    }

    @Test
    public void testMergeWithNull() {
        ActiveConfigureItem baseItem = ActiveConfigureItem.builder()
                .ref("baseRef")
                .brokerUrl("tcp://localhost:61616")
                .build();

        ActiveConfigureItem mergedItem = baseItem.merge(null);

        // Should be a copy of the base item
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getBrokerUrl(), "tcp://localhost:61616");
    }

    @Test
    public void testEvaluate() {
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.getLocalVariablesWrapper().put("ref", "testRef");
        context.getLocalVariablesWrapper().put("brokerUrl", "tcp://localhost:61616");
        context.getLocalVariablesWrapper().put("topic", "testTopic");
        context.getLocalVariablesWrapper().put("message", "testMessage");

        ActiveConfigureItem item = ActiveConfigureItem.builder()
                .ref("${ref}")
                .brokerUrl("${brokerUrl}")
                .topic("${topic}")
                .message("${message}")
                .build();

        ActiveConfigureItem evaluatedItem = item.evaluate(context);

        Assert.assertEquals(evaluatedItem.getRef(), "testRef");
        Assert.assertEquals(evaluatedItem.getBrokerUrl(), "tcp://localhost:61616");
        Assert.assertEquals(evaluatedItem.getTopic(), "testTopic");
        Assert.assertEquals(evaluatedItem.getMessage(), "testMessage");
    }

    @Test
    public void testGettersWithDefaults() {
        ActiveConfigureItem item = new ActiveConfigureItem();

        // Test default values
        Assert.assertEquals(item.getRef(), DEF_REF_NAME_KEY);
        Assert.assertTrue(item.getBrokerUrl().contains("tcp://"));
        Assert.assertEquals(item.getUsername(), ACTIVEMQ_DEFAULT_USERNAME);
        Assert.assertEquals(item.getPassword(), ACTIVEMQ_DEFAULT_PASSWORD);
        Assert.assertEquals(item.getMessage(), "");
    }

    @Test
    public void testSettersAndGetters() {
        ActiveConfigureItem item = new ActiveConfigureItem();

        item.setRef("testRef");
        item.setBrokerUrl("tcp://localhost:61616");
        item.setTopic("testTopic");
        item.setQueue("testQueue");
        item.setMessage("testMessage");
        item.setUsername("testUser");
        item.setPassword("testPass");

        Assert.assertEquals(item.getRef(), "testRef");
        Assert.assertEquals(item.getBrokerUrl(), "tcp://localhost:61616");
        Assert.assertEquals(item.getTopic(), "testTopic");
        Assert.assertEquals(item.getQueue(), "testQueue");
        Assert.assertEquals(item.getMessage(), "testMessage");
        Assert.assertEquals(item.getUsername(), "testUser");
        Assert.assertEquals(item.getPassword(), "testPass");
    }
}