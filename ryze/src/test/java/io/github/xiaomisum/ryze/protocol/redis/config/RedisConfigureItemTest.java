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

package io.github.xiaomisum.ryze.protocol.redis.config;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class RedisConfigureItemTest {

    @Test
    public void testBuilder() {
        RedisConfigureItem item = RedisConfigureItem.builder()
                .datasource("testDS")
                .url("redis://localhost:6379")
                .command("GET")
                .args("key1")
                .timeout(5000)
                .maxTotal(20)
                .maxIdle(10)
                .minIdle(2)
                .build();

        Assert.assertEquals(item.getDatasource(), "testDS");
        Assert.assertEquals(item.getUrl(), "redis://localhost:6379");
        Assert.assertEquals(item.getCommand(), "GET");
        Assert.assertNotNull(item.getArgs());
        Assert.assertEquals(item.getArgs().size(), 1);
        Assert.assertEquals(item.getArgs().getFirst(), "key1");
        Assert.assertEquals(item.getTimeout(), 5000);
        Assert.assertEquals(item.getMaxTotal(), 20);
        Assert.assertEquals(item.getMaxIdle(), 10);
        Assert.assertEquals(item.getMinIdle(), 2);
    }

    @Test
    public void testBuilderWithMultipleArgs() {
        RedisConfigureItem item = RedisConfigureItem.builder()
                .datasource("testDS")
                .url("redis://localhost:6379")
                .command("HMGET")
                .args("key1", "field1", "field2")
                .build();

        Assert.assertEquals(item.getDatasource(), "testDS");
        Assert.assertEquals(item.getUrl(), "redis://localhost:6379");
        Assert.assertEquals(item.getCommand(), "HMGET");
        Assert.assertNotNull(item.getArgs());
        Assert.assertEquals(item.getArgs().size(), 3);
        Assert.assertEquals(item.getArgs().get(0), "key1");
        Assert.assertEquals(item.getArgs().get(1), "field1");
        Assert.assertEquals(item.getArgs().get(2), "field2");
    }

    @Test
    public void testBuilderCommandMethods() {
        // Test specific command builder methods
        RedisConfigureItem item1 = RedisConfigureItem.builder().get("key1").build();
        Assert.assertEquals(item1.getCommand(), "GET");
        Assert.assertNotNull(item1.getArgs());
        Assert.assertEquals(item1.getArgs().size(), 1);
        Assert.assertEquals(item1.getArgs().get(0), "key1");

        RedisConfigureItem item2 = RedisConfigureItem.builder().set("key1", "value1").build();
        Assert.assertEquals(item2.getCommand(), "SET");
        Assert.assertNotNull(item2.getArgs());
        Assert.assertEquals(item2.getArgs().size(), 2);
        Assert.assertEquals(item2.getArgs().get(0), "key1");
        Assert.assertEquals(item2.getArgs().get(1), "value1");

        RedisConfigureItem item3 = RedisConfigureItem.builder().del("key1").build();
        Assert.assertEquals(item3.getCommand(), "DEL");
        Assert.assertNotNull(item3.getArgs());
        Assert.assertEquals(item3.getArgs().size(), 1);
        Assert.assertEquals(item3.getArgs().get(0), "key1");
    }

    @Test
    public void testBuilderComplexCommandMethods() {
        RedisConfigureItem item1 = RedisConfigureItem.builder().hget("hashKey", "field1", "field2").build();
        Assert.assertEquals(item1.getCommand(), "HGET");
        Assert.assertNotNull(item1.getArgs());
        Assert.assertEquals(item1.getArgs().size(), 3);
        Assert.assertEquals(item1.getArgs().get(0), "hashKey");
        Assert.assertEquals(item1.getArgs().get(1), "field1");
        Assert.assertEquals(item1.getArgs().get(2), "field2");

        RedisConfigureItem item2 = RedisConfigureItem.builder().hset("hashKey", "field1", "value1").build();
        Assert.assertEquals(item2.getCommand(), "HSET");
        Assert.assertNotNull(item2.getArgs());
        Assert.assertEquals(item2.getArgs().size(), 3);
        Assert.assertEquals(item2.getArgs().get(0), "hashKey");
        Assert.assertEquals(item2.getArgs().get(1), "field1");
        Assert.assertEquals(item2.getArgs().get(2), "value1");

        RedisConfigureItem item3 = RedisConfigureItem.builder().hmset("hashKey", "field1", "value1", "field2", "value2").build();
        Assert.assertEquals(item3.getCommand(), "HMSET");
        Assert.assertNotNull(item3.getArgs());
        Assert.assertEquals(item3.getArgs().size(), 5);
        Assert.assertEquals(item3.getArgs().get(0), "hashKey");
        Assert.assertEquals(item3.getArgs().get(1), "field1");
        Assert.assertEquals(item3.getArgs().get(2), "value1");
        Assert.assertEquals(item3.getArgs().get(3), "field2");
        Assert.assertEquals(item3.getArgs().get(4), "value2");
    }

    @Test
    public void testBuilderListCommandMethods() {
        RedisConfigureItem item1 = RedisConfigureItem.builder().lpush("listKey", "value1", "value2").build();
        Assert.assertEquals(item1.getCommand(), "LPUSH");
        Assert.assertNotNull(item1.getArgs());
        Assert.assertEquals(item1.getArgs().size(), 3);
        Assert.assertEquals(item1.getArgs().get(0), "listKey");
        Assert.assertEquals(item1.getArgs().get(1), "value1");
        Assert.assertEquals(item1.getArgs().get(2), "value2");

        RedisConfigureItem item2 = RedisConfigureItem.builder().rpush("listKey", "value1", "value2").build();
        Assert.assertEquals(item2.getCommand(), "RPUSH");
        Assert.assertNotNull(item2.getArgs());
        Assert.assertEquals(item2.getArgs().size(), 3);
        Assert.assertEquals(item2.getArgs().get(0), "listKey");
        Assert.assertEquals(item2.getArgs().get(1), "value1");
        Assert.assertEquals(item2.getArgs().get(2), "value2");
    }

    @Test
    public void testBuilderSetCommandMethods() {
        RedisConfigureItem item1 = RedisConfigureItem.builder().sadd("setKey", "member1", "member2").build();
        Assert.assertEquals(item1.getCommand(), "SADD");
        Assert.assertNotNull(item1.getArgs());
        Assert.assertEquals(item1.getArgs().size(), 3);
        Assert.assertEquals(item1.getArgs().get(0), "setKey");
        Assert.assertEquals(item1.getArgs().get(1), "member1");
        Assert.assertEquals(item1.getArgs().get(2), "member2");

        RedisConfigureItem item2 = RedisConfigureItem.builder().srem("setKey", "member1", "member2").build();
        Assert.assertEquals(item2.getCommand(), "SREM");
        Assert.assertNotNull(item2.getArgs());
        Assert.assertEquals(item2.getArgs().size(), 3);
        Assert.assertEquals(item2.getArgs().get(0), "setKey");
        Assert.assertEquals(item2.getArgs().get(1), "member1");
        Assert.assertEquals(item2.getArgs().get(2), "member2");
    }

    @Test
    public void testBuilderConfigMethods() {
        // Test config with consumer
        RedisConfigureItem item1 = RedisConfigureItem.builder()
                .config(builder -> builder.url("redis://localhost:6379"))
                .build();
        Assert.assertEquals(item1.getUrl(), "redis://localhost:6379");

        // Test config with another config item
        RedisConfigureItem baseConfig = RedisConfigureItem.builder()
                .url("redis://localhost:6379")
                .timeout(5000)
                .build();

        RedisConfigureItem item2 = RedisConfigureItem.builder()
                .datasource("testDS")
                .config(baseConfig)
                .build();

        Assert.assertEquals(item2.getDatasource(), "testDS");
        Assert.assertEquals(item2.getUrl(), "redis://localhost:6379");
        Assert.assertEquals(item2.getTimeout(), 5000);
    }

    @Test
    public void testMerge() {
        List<String> baseArgs = List.of("baseKey");
        RedisConfigureItem baseItem = RedisConfigureItem.builder()
                .datasource("baseDS")
                .url("redis://localhost:6379")
                .command("GET")
                .args("baseKey")
                .timeout(5000)
                .maxTotal(20)
                .maxIdle(10)
                .minIdle(2)
                .build();

        RedisConfigureItem otherItem = RedisConfigureItem.builder()
                .datasource("otherDS")
                .url("redis://otherhost:6379")
                .command("SET")
                .args("otherKey")
                .build();

        RedisConfigureItem mergedItem = baseItem.merge(otherItem);

        // Check that properties from baseItem take precedence
        Assert.assertEquals(mergedItem.getDatasource(), "baseDS");
        Assert.assertEquals(mergedItem.getUrl(), "redis://localhost:6379");
        Assert.assertEquals(mergedItem.getCommand(), "GET");
        Assert.assertNotNull(mergedItem.getArgs());
        Assert.assertEquals(mergedItem.getArgs().size(), 1);
        Assert.assertEquals(mergedItem.getArgs().get(0), "baseKey");
        Assert.assertEquals(mergedItem.getTimeout(), 5000);
        Assert.assertEquals(mergedItem.getMaxTotal(), 20);
        Assert.assertEquals(mergedItem.getMaxIdle(), 10);
        Assert.assertEquals(mergedItem.getMinIdle(), 2);
    }

    @Test
    public void testMergeWithNull() {
        RedisConfigureItem baseItem = RedisConfigureItem.builder()
                .datasource("baseDS")
                .url("redis://localhost:6379")
                .build();

        RedisConfigureItem mergedItem = baseItem.merge(null);

        // Should be a copy of the base item
        Assert.assertEquals(mergedItem.getDatasource(), "baseDS");
        Assert.assertEquals(mergedItem.getUrl(), "redis://localhost:6379");
    }

    @Test
    public void testMergeWithEmptyValues() {
        RedisConfigureItem baseItem = new RedisConfigureItem();

        RedisConfigureItem otherItem = RedisConfigureItem.builder()
                .url("redis://localhost:6379")
                .command("GET")
                .args("key1")
                .timeout(5000)
                .maxTotal(20)
                .maxIdle(10)
                .minIdle(2)
                .build();

        RedisConfigureItem mergedItem = baseItem.merge(otherItem);

        Assert.assertEquals(mergedItem.getUrl(), "redis://localhost:6379");
        Assert.assertEquals(mergedItem.getCommand(), "GET");
        Assert.assertNotNull(mergedItem.getArgs());
        Assert.assertEquals(mergedItem.getArgs().size(), 1);
        Assert.assertEquals(mergedItem.getArgs().get(0), "key1");
        Assert.assertEquals(mergedItem.getTimeout(), 5000);
        Assert.assertEquals(mergedItem.getMaxTotal(), 20);
        Assert.assertEquals(mergedItem.getMaxIdle(), 10);
        Assert.assertEquals(mergedItem.getMinIdle(), 2);
    }

    @Test
    public void testEvaluate() {
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.getLocalVariablesWrapper().put("datasource", "testDS");
        context.getLocalVariablesWrapper().put("url", "redis://localhost:6379");
        context.getLocalVariablesWrapper().put("command", "GET");
        context.getLocalVariablesWrapper().put("args", "key1");

        RedisConfigureItem item = RedisConfigureItem.builder()
                .datasource("${datasource}")
                .url("${url}")
                .command("${command}")
                .args("${args}")
                .build();

        RedisConfigureItem evaluatedItem = item.evaluate(context);

        Assert.assertEquals(evaluatedItem.getDatasource(), "testDS");
        Assert.assertEquals(evaluatedItem.getUrl(), "redis://localhost:6379");
        Assert.assertEquals(evaluatedItem.getCommand(), "GET");
        Assert.assertNotNull(evaluatedItem.getArgs());
        Assert.assertEquals(evaluatedItem.getArgs().size(), 1);
        Assert.assertEquals(evaluatedItem.getArgs().get(0), "key1");
    }

    @Test
    public void testGettersWithDefaults() {
        RedisConfigureItem item = new RedisConfigureItem();

        // Test default values
        Assert.assertEquals(item.getTimeout(), 10000); // default value
        Assert.assertEquals(item.getMaxTotal(), 10); // default value
        Assert.assertEquals(item.getMaxIdle(), 5); // default value
        Assert.assertEquals(item.getMinIdle(), 1); // default value
        Assert.assertNull(item.getDatasource());
        Assert.assertNull(item.getUrl());
        Assert.assertTrue(StringUtils.isBlank(item.getCommand()));
        Assert.assertNull(item.getArgs());
    }

    @Test
    public void testSettersAndGetters() {
        RedisConfigureItem item = new RedisConfigureItem();

        List<String> args = Arrays.asList("key1", "field1");

        item.setDatasource("testDS");
        item.setUrl("redis://localhost:6379");
        item.setCommand("GET");
        item.setArgs(args);
        item.setTimeout(5000);
        item.setMaxTotal(20);
        item.setMaxIdle(10);
        item.setMinIdle(2);

        Assert.assertEquals(item.getDatasource(), "testDS");
        Assert.assertEquals(item.getUrl(), "redis://localhost:6379");
        Assert.assertEquals(item.getCommand(), "GET");
        Assert.assertEquals(item.getArgs(), args);
        Assert.assertEquals(item.getTimeout(), 5000);
        Assert.assertEquals(item.getMaxTotal(), 20);
        Assert.assertEquals(item.getMaxIdle(), 10);
        Assert.assertEquals(item.getMinIdle(), 2);
    }

    @Test
    public void testCommandUppercase() {
        RedisConfigureItem item = new RedisConfigureItem();
        item.setCommand("get");
        Assert.assertEquals(item.getCommand(), "GET");

        item.setCommand("hget");
        Assert.assertEquals(item.getCommand(), "HGET");
    }

    @Test
    public void testTimeoutDefaultValue() {
        RedisConfigureItem item = new RedisConfigureItem();
        item.setTimeout(0);
        Assert.assertEquals(item.getTimeout(), 10000); // Should return default when <= 0

        item.setTimeout(-1);
        Assert.assertEquals(item.getTimeout(), 10000); // Should return default when <= 0

        item.setTimeout(5000);
        Assert.assertEquals(item.getTimeout(), 5000); // Should return set value when > 0
    }

    @Test
    public void testMaxTotalDefaultValue() {
        RedisConfigureItem item = new RedisConfigureItem();
        item.setMaxTotal(0);
        Assert.assertEquals(item.getMaxTotal(), 10); // Should return default when <= 0

        item.setMaxTotal(-1);
        Assert.assertEquals(item.getMaxTotal(), 10); // Should return default when <= 0

        item.setMaxTotal(20);
        Assert.assertEquals(item.getMaxTotal(), 20); // Should return set value when > 0
    }

    @Test
    public void testMaxIdleDefaultValue() {
        RedisConfigureItem item = new RedisConfigureItem();
        item.setMaxIdle(0);
        Assert.assertEquals(item.getMaxIdle(), 5); // Should return default when <= 0

        item.setMaxIdle(-1);
        Assert.assertEquals(item.getMaxIdle(), 5); // Should return default when <= 0

        item.setMaxIdle(10);
        Assert.assertEquals(item.getMaxIdle(), 10); // Should return set value when > 0
    }

    @Test
    public void testMinIdleDefaultValue() {
        RedisConfigureItem item = new RedisConfigureItem();
        item.setMinIdle(0);
        Assert.assertEquals(item.getMinIdle(), 1); // Should return default when <= 0

        item.setMinIdle(-1);
        Assert.assertEquals(item.getMinIdle(), 1); // Should return default when <= 0

        item.setMinIdle(5);
        Assert.assertEquals(item.getMinIdle(), 5); // Should return set value when > 0
    }

    @Test
    public void testArgsMerge() {
        RedisConfigureItem baseItem = RedisConfigureItem.builder()
                .args("key1", "field1")
                .build();

        RedisConfigureItem otherItem = RedisConfigureItem.builder()
                .args("key2", "field2")
                .build();

        RedisConfigureItem mergedItem = baseItem.merge(otherItem);

        // Base args should take precedence
        Assert.assertNotNull(mergedItem.getArgs());
        Assert.assertEquals(mergedItem.getArgs().size(), 2);
        Assert.assertEquals(mergedItem.getArgs().get(0), "key1");
        Assert.assertEquals(mergedItem.getArgs().get(1), "field1");
    }

    @Test
    public void testArgsMergeWithEmptyBase() {
        RedisConfigureItem baseItem = new RedisConfigureItem();

        RedisConfigureItem otherItem = RedisConfigureItem.builder()
                .args("key2", "field2")
                .build();

        RedisConfigureItem mergedItem = baseItem.merge(otherItem);

        // Other args should be used when base is empty
        Assert.assertNotNull(mergedItem.getArgs());
        Assert.assertEquals(mergedItem.getArgs().size(), 2);
        Assert.assertEquals(mergedItem.getArgs().get(0), "key2");
        Assert.assertEquals(mergedItem.getArgs().get(1), "field2");
    }
}