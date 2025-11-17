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

package io.github.xiaomisum.ryze.protocol.rabbit.config;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.xiaomisum.ryze.protocol.rabbit.RabbitConstantsInterface.DEF_REF_NAME_KEY;

public class RabbitConfigureItemTest {

    @Test
    public void testBuilder() {
        RabbitConfigureItem item = RabbitConfigureItem.builder()
                .ref("testRef")
                .virtualHost("/test")
                .host("localhost")
                .port("5672")
                .username("testUser")
                .password("testPass")
                .message("testMessage")
                .timeout(30000)
                .build();

        Assert.assertEquals(item.getRef(), "testRef");
        Assert.assertEquals(item.getVirtualHost(), "/test");
        Assert.assertEquals(item.getHost(), "localhost");
        Assert.assertEquals(item.getPort(), "5672");
        Assert.assertEquals(item.getUsername(), "testUser");
        Assert.assertEquals(item.getPassword(), "testPass");
        Assert.assertEquals(item.getMessage(), "testMessage");
        Assert.assertEquals(item.getTimeout(), Integer.valueOf(30000));
    }

    @Test
    public void testBuilderWithQueueAndExchange() {
        RabbitConfigureItem item = RabbitConfigureItem.builder()
                .queue(builder -> builder
                        .name("testQueue")
                        .durable()
                        .exclusive())
                .exchange(builder -> builder
                        .name("testExchange")
                        .type("direct")
                        .routingKey("testKey"))
                .build();

        Assert.assertNotNull(item.getQueue());
        Assert.assertEquals(item.getQueue().getName(), "testQueue");
        Assert.assertTrue(item.getQueue().getDurable());
        Assert.assertTrue(item.getQueue().getExclusive());

        Assert.assertNotNull(item.getExchange());
        Assert.assertEquals(item.getExchange().getName(), "testExchange");
        Assert.assertEquals(item.getExchange().getType(), "direct");
        Assert.assertEquals(item.getExchange().getRoutingKey(), "testKey");
    }

    @Test
    public void testBuilderWithProps() {
        RabbitConfigureItem item = RabbitConfigureItem.builder()
                .props(builder -> builder
                        .contentType("application/json")
                        .contentEncoding("utf-8")
                        .deliveryMode(2))
                .build();

        Assert.assertNotNull(item.getProps());
        Assert.assertEquals(item.getProps().getContentType(), "application/json");
        Assert.assertEquals(item.getProps().getContentEncoding(), "utf-8");
        Assert.assertEquals(item.getProps().getDeliveryMode(), Integer.valueOf(2));
    }

    @Test
    public void testBuilderWithDifferentMessageTypes() {
        // Test with Map message
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("key1", "value1");
        messageMap.put("key2", 123);

        RabbitConfigureItem item1 = RabbitConfigureItem.builder()
                .message(messageMap)
                .build();

        Assert.assertTrue(item1.getFormatMessage().contains("key1"));
        Assert.assertTrue(item1.getFormatMessage().contains("value1"));

        // Test with String message
        RabbitConfigureItem item2 = RabbitConfigureItem.builder()
                .message("testStringMessage")
                .build();

        Assert.assertEquals(item2.getMessage(), "testStringMessage");
    }

    @Test
    public void testMerge() {
        RabbitConfigureItem baseItem = RabbitConfigureItem.builder()
                .ref("baseRef")
                .host("localhost")
                .port("5672")
                .username("baseUser")
                .build();

        RabbitConfigureItem otherItem = RabbitConfigureItem.builder()
                .ref("otherRef")
                .host("otherhost")
                .port("5673")
                .build();

        RabbitConfigureItem mergedItem = baseItem.merge(otherItem);

        // Check that properties from baseItem take precedence
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getHost(), "localhost");
        Assert.assertEquals(mergedItem.getPort(), "5672");
        Assert.assertEquals(mergedItem.getUsername(), "baseUser");
    }

    @Test
    public void testMergeWithNull() {
        RabbitConfigureItem baseItem = RabbitConfigureItem.builder()
                .ref("baseRef")
                .host("localhost")
                .build();

        RabbitConfigureItem mergedItem = baseItem.merge(null);

        // Should be a copy of the base item
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getHost(), "localhost");
    }

    @Test
    public void testEvaluate() {
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.getLocalVariablesWrapper().put("ref", "testRef");
        context.getLocalVariablesWrapper().put("host", "localhost");
        context.getLocalVariablesWrapper().put("port", "5672");
        context.getLocalVariablesWrapper().put("message", "testMessage");

        RabbitConfigureItem item = RabbitConfigureItem.builder()
                .ref("${ref}")
                .host("${host}")
                .port("${port}")
                .message("${message}")
                .build();

        RabbitConfigureItem evaluatedItem = item.evaluate(context);

        Assert.assertEquals(evaluatedItem.getRef(), "testRef");
        Assert.assertEquals(evaluatedItem.getHost(), "localhost");
        Assert.assertEquals(evaluatedItem.getPort(), "5672");
        Assert.assertEquals(evaluatedItem.getMessage(), "testMessage");
    }

    @Test
    public void testGettersWithDefaults() {
        RabbitConfigureItem item = new RabbitConfigureItem();

        // Test default values
        Assert.assertEquals(item.getRef(), DEF_REF_NAME_KEY);
        Assert.assertEquals(item.getVirtualHost(), "/");
        Assert.assertNull(item.getHost());
        Assert.assertEquals(item.getPort(), "5672");
        Assert.assertEquals(item.getUsername(), "guest");
        Assert.assertEquals(item.getPassword(), "guest");
        Assert.assertEquals(item.getMessage(), "");
        Assert.assertEquals(item.getTimeout(), Integer.valueOf(60000));
    }

    @Test
    public void testSettersAndGetters() {
        RabbitConfigureItem item = new RabbitConfigureItem();

        item.setRef("testRef");
        item.setVirtualHost("/test");
        item.setHost("localhost");
        item.setPort("5672");
        item.setUsername("testUser");
        item.setPassword("testPass");
        item.setMessage("testMessage");
        item.setTimeout(30000);

        Assert.assertEquals(item.getRef(), "testRef");
        Assert.assertEquals(item.getVirtualHost(), "/test");
        Assert.assertEquals(item.getHost(), "localhost");
        Assert.assertEquals(item.getPort(), "5672");
        Assert.assertEquals(item.getUsername(), "testUser");
        Assert.assertEquals(item.getPassword(), "testPass");
        Assert.assertEquals(item.getMessage(), "testMessage");
        Assert.assertEquals(item.getTimeout(), Integer.valueOf(30000));
    }

    @Test
    public void testQueueMerge() {
        RabbitConfigureItem.Queue baseQueue = RabbitConfigureItem.Queue.Builder.builder()
                .name("baseQueue")
                .durable()
                .build();

        RabbitConfigureItem.Queue otherQueue = RabbitConfigureItem.Queue.Builder.builder()
                .name("otherQueue")
                .exclusive()
                .build();

        RabbitConfigureItem.Queue mergedQueue = baseQueue.merge(otherQueue);

        // Check that properties from baseQueue take precedence
        Assert.assertEquals(mergedQueue.getName(), "baseQueue");
        Assert.assertTrue(mergedQueue.getDurable());
        Assert.assertTrue(mergedQueue.getExclusive());
    }

    @Test
    public void testExchangeMerge() {
        RabbitConfigureItem.Exchange baseExchange = RabbitConfigureItem.Exchange.Builder.builder()
                .name("baseExchange")
                .type("direct")
                .build();

        RabbitConfigureItem.Exchange otherExchange = RabbitConfigureItem.Exchange.Builder.builder()
                .name("otherExchange")
                .type("topic")
                .routingKey("testKey")
                .build();

        RabbitConfigureItem.Exchange mergedExchange = baseExchange.merge(otherExchange);

        // Check that properties from baseExchange take precedence
        Assert.assertEquals(mergedExchange.getName(), "baseExchange");
        Assert.assertEquals(mergedExchange.getType(), "direct");
        Assert.assertEquals(mergedExchange.getRoutingKey(), "testKey");
    }

    @Test
    public void testPropsMerge() {
        RabbitConfigureItem.Props baseProps = RabbitConfigureItem.Props.Builder.builder()
                .contentType("application/json")
                .contentEncoding("utf-8")
                .build();

        RabbitConfigureItem.Props otherProps = RabbitConfigureItem.Props.Builder.builder()
                .contentType("text/plain")
                .deliveryMode(2)
                .build();

        RabbitConfigureItem.Props mergedProps = baseProps.merge(otherProps);

        // Check that properties from baseProps take precedence
        Assert.assertEquals(mergedProps.getContentType(), "application/json");
        Assert.assertEquals(mergedProps.getContentEncoding(), "utf-8");
        Assert.assertEquals(mergedProps.getDeliveryMode(), Integer.valueOf(2));
    }
}