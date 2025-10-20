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

package io.github.xiaomisum.ryze.protocol.kafka.config;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.xiaomisum.ryze.protocol.kafka.KafkaConstantsInterface.DEF_REF_NAME_KEY;

public class KafkaConfigureItemTest {

    @Test
    public void testBuilder() {
        KafkaConfigureItem item = KafkaConfigureItem.builder()
                .ref("testRef")
                .bootstrapServers("localhost:9092")
                .topic("testTopic")
                .key("testKey")
                .message("testMessage")
                .keySerializer("org.apache.kafka.common.serialization.StringSerializer")
                .valueSerializer("org.apache.kafka.common.serialization.StringSerializer")
                .acks(1)
                .retries(3)
                .lingerMs(10)
                .build();

        Assert.assertEquals(item.getRef(), "testRef");
        Assert.assertEquals(item.getBootstrapServers(), "localhost:9092");
        Assert.assertEquals(item.getTopic(), "testTopic");
        Assert.assertEquals(item.getKey(), "testKey");
        Assert.assertEquals(item.getMessage(), "testMessage");
        Assert.assertEquals(item.getKeySerializer(), "org.apache.kafka.common.serialization.StringSerializer");
        Assert.assertEquals(item.getValueSerializer(), "org.apache.kafka.common.serialization.StringSerializer");
        Assert.assertEquals(item.getAcks().intValue(), 1);
        Assert.assertEquals(item.getRetries().intValue(), 3);
        Assert.assertEquals(item.getLingerMs().intValue(), 10);
    }

    @Test
    public void testBuilderWithDifferentMessageTypes() {
        // Test with Map message
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("key1", "value1");
        messageMap.put("key2", 123);

        KafkaConfigureItem item1 = KafkaConfigureItem.builder()
                .message(messageMap)
                .build();

        Assert.assertTrue(item1.getMessage().contains("key1"));
        Assert.assertTrue(item1.getMessage().contains("value1"));

        // Test with String message
        KafkaConfigureItem item2 = KafkaConfigureItem.builder()
                .message("testStringMessage")
                .build();

        Assert.assertEquals(item2.getMessage(), "testStringMessage");
    }

    @Test
    public void testBuilderWithAddressMethod() {
        KafkaConfigureItem item = KafkaConfigureItem.builder()
                .address("localhost:9092")
                .topic("testTopic")
                .build();

        Assert.assertEquals(item.getBootstrapServers(), "localhost:9092");
        Assert.assertEquals(item.getTopic(), "testTopic");
    }

    @Test
    public void testMerge() {
        KafkaConfigureItem baseItem = KafkaConfigureItem.builder()
                .ref("baseRef")
                .bootstrapServers("localhost:9092")
                .topic("baseTopic")
                .key("baseKey")
                .build();

        KafkaConfigureItem otherItem = KafkaConfigureItem.builder()
                .ref("otherRef")
                .bootstrapServers("otherhost:9092")
                .topic("otherTopic")
                .build();

        KafkaConfigureItem mergedItem = baseItem.merge(otherItem);

        // Check that properties from baseItem take precedence
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getBootstrapServers(), "localhost:9092");
        Assert.assertEquals(mergedItem.getTopic(), "baseTopic");
        Assert.assertEquals(mergedItem.getKey(), "baseKey");
    }

    @Test
    public void testMergeWithNull() {
        KafkaConfigureItem baseItem = KafkaConfigureItem.builder()
                .ref("baseRef")
                .bootstrapServers("localhost:9092")
                .build();

        KafkaConfigureItem mergedItem = baseItem.merge(null);

        // Should be a copy of the base item
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getBootstrapServers(), "localhost:9092");
    }

    @Test
    public void testEvaluate() {
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.getLocalVariablesWrapper().put("ref", "testRef");
        context.getLocalVariablesWrapper().put("bootstrapServers", "localhost:9092");
        context.getLocalVariablesWrapper().put("topic", "testTopic");
        context.getLocalVariablesWrapper().put("key", "testKey");
        context.getLocalVariablesWrapper().put("message", "testMessage");

        KafkaConfigureItem item = KafkaConfigureItem.builder()
                .ref("${ref}")
                .bootstrapServers("${bootstrapServers}")
                .topic("${topic}")
                .key("${key}")
                .message("${message}")
                .build();

        KafkaConfigureItem evaluatedItem = item.evaluate(context);

        Assert.assertEquals(evaluatedItem.getRef(), "testRef");
        Assert.assertEquals(evaluatedItem.getBootstrapServers(), "localhost:9092");
        Assert.assertEquals(evaluatedItem.getTopic(), "testTopic");
        Assert.assertEquals(evaluatedItem.getKey(), "testKey");
        Assert.assertEquals(evaluatedItem.getMessage(), "testMessage");
    }

    @Test
    public void testGettersWithDefaults() {
        KafkaConfigureItem item = new KafkaConfigureItem();

        // Test default values
        Assert.assertEquals(item.getRef(), DEF_REF_NAME_KEY);
        Assert.assertNull(item.getBootstrapServers());
        Assert.assertEquals(item.getKeySerializer(), "org.apache.kafka.common.serialization.StringSerializer");
        Assert.assertEquals(item.getValueSerializer(), "org.apache.kafka.common.serialization.StringSerializer");
        Assert.assertEquals(item.getAcks().intValue(), 1);
        Assert.assertEquals(item.getRetries().intValue(), 5);
        Assert.assertEquals(item.getLingerMs().intValue(), 20);
        Assert.assertEquals(item.getMessage(), "");
    }

    @Test
    public void testSettersAndGetters() {
        KafkaConfigureItem item = new KafkaConfigureItem();

        item.setRef("testRef");
        item.setBootstrapServers("localhost:9092");
        item.setTopic("testTopic");
        item.setKey("testKey");
        item.setMessage("testMessage");
        item.setKeySerializer("testKeySerializer");
        item.setValueSerializer("testValueSerializer");
        item.setAcks(2);
        item.setRetries(10);
        item.setLingerMs(50);

        Assert.assertEquals(item.getRef(), "testRef");
        Assert.assertEquals(item.getBootstrapServers(), "localhost:9092");
        Assert.assertEquals(item.getTopic(), "testTopic");
        Assert.assertEquals(item.getKey(), "testKey");
        Assert.assertEquals(item.getMessage(), "testMessage");
        Assert.assertEquals(item.getKeySerializer(), "testKeySerializer");
        Assert.assertEquals(item.getValueSerializer(), "testValueSerializer");
        Assert.assertEquals(item.getAcks().intValue(), 2);
        Assert.assertEquals(item.getRetries().intValue(), 10);
        Assert.assertEquals(item.getLingerMs().intValue(), 50);
    }
}