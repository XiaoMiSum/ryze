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

package io.github.xiaomisum.ryze.protocol.mongo.config;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.xiaomisum.ryze.protocol.mongo.MongoConstantsInterface.DEF_REF_NAME_KEY;

public class MongoConfigItemTest {

    @Test
    public void testBuilder() {
        MongoConfigItem item = MongoConfigItem.builder()
                .ref("testRef")
                .url("mongodb://localhost:27017")
                .database("testDB")
                .collection("testCollection")
                .action("find")
                .build();

        Assert.assertEquals(item.getRef(), "testRef");
        Assert.assertEquals(item.getUrl(), "mongodb://localhost:27017");
        Assert.assertEquals(item.getDatabase(), "testDB");
        Assert.assertEquals(item.getCollection(), "testCollection");
        Assert.assertEquals(item.getAction(), "find");
    }

    @Test
    public void testBuilderWithActionMethods() {
        // Test find action
        MongoConfigItem item1 = MongoConfigItem.builder()
                .find()
                .build();

        Assert.assertEquals(item1.getAction(), "find");

        // Test insert action
        MongoConfigItem item2 = MongoConfigItem.builder()
                .insert(new HashMap<>())
                .build();

        Assert.assertEquals(item2.getAction(), "insert");

        // Test update action
        MongoConfigItem item3 = MongoConfigItem.builder()
                .update(new HashMap<>(), new HashMap<>())
                .build();

        Assert.assertEquals(item3.getAction(), "update");

        // Test delete action
        MongoConfigItem item4 = MongoConfigItem.builder()
                .delete()
                .build();

        Assert.assertEquals(item4.getAction(), "delete");
    }

    @Test
    public void testBuilderWithDataAndCondition() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("age", 30);

        Map<String, Object> condition = new HashMap<>();
        condition.put("id", "123");

        MongoConfigItem item = MongoConfigItem.builder()
                .insert(data)
                .condition(condition)
                .build();

        Assert.assertEquals(item.getAction(), "insert");
        Assert.assertEquals(item.getData(), data);
        Assert.assertEquals(item.getCondition(), condition);
    }

    @Test
    public void testMerge() {
        MongoConfigItem baseItem = MongoConfigItem.builder()
                .ref("baseRef")
                .url("mongodb://localhost:27017")
                .database("baseDB")
                .collection("baseCollection")
                .build();

        MongoConfigItem otherItem = MongoConfigItem.builder()
                .ref("otherRef")
                .url("mongodb://otherhost:27017")
                .database("otherDB")
                .build();

        MongoConfigItem mergedItem = baseItem.merge(otherItem);

        // Check that properties from baseItem take precedence
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getUrl(), "mongodb://localhost:27017");
        Assert.assertEquals(mergedItem.getDatabase(), "baseDB");
        Assert.assertEquals(mergedItem.getCollection(), "baseCollection");
    }

    @Test
    public void testMergeWithNull() {
        MongoConfigItem baseItem = MongoConfigItem.builder()
                .ref("baseRef")
                .url("mongodb://localhost:27017")
                .build();

        MongoConfigItem mergedItem = baseItem.merge(null);

        // Should be a copy of the base item
        Assert.assertEquals(mergedItem.getRef(), "baseRef");
        Assert.assertEquals(mergedItem.getUrl(), "mongodb://localhost:27017");
    }

    @Test
    public void testEvaluate() {
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.getLocalVariablesWrapper().put("ref", "testRef");
        context.getLocalVariablesWrapper().put("url", "mongodb://localhost:27017");
        context.getLocalVariablesWrapper().put("database", "testDB");
        context.getLocalVariablesWrapper().put("collection", "testCollection");

        MongoConfigItem item = MongoConfigItem.builder()
                .ref("${ref}")
                .url("${url}")
                .database("${database}")
                .collection("${collection}")
                .build();

        MongoConfigItem evaluatedItem = item.evaluate(context);

        Assert.assertEquals(evaluatedItem.getRef(), "testRef");
        Assert.assertEquals(evaluatedItem.getUrl(), "mongodb://localhost:27017");
        Assert.assertEquals(evaluatedItem.getDatabase(), "testDB");
        Assert.assertEquals(evaluatedItem.getCollection(), "testCollection");
    }

    @Test
    public void testGettersWithDefaults() {
        MongoConfigItem item = new MongoConfigItem();

        // Test default values
        Assert.assertEquals(item.getRef(), DEF_REF_NAME_KEY);
        Assert.assertEquals(item.getAction(), "find");
    }

    @Test
    public void testSettersAndGetters() {
        MongoConfigItem item = new MongoConfigItem();

        item.setRef("testRef");
        item.setUrl("mongodb://localhost:27017");
        item.setDatabase("testDB");
        item.setCollection("testCollection");
        item.setAction("find");

        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");

        Map<String, Object> condition = new HashMap<>();
        condition.put("id", "123");

        item.setData(data);
        item.setCondition(condition);

        Assert.assertEquals(item.getRef(), "testRef");
        Assert.assertEquals(item.getUrl(), "mongodb://localhost:27017");
        Assert.assertEquals(item.getDatabase(), "testDB");
        Assert.assertEquals(item.getCollection(), "testCollection");
        Assert.assertEquals(item.getAction(), "find");
        Assert.assertEquals(item.getData(), data);
        Assert.assertEquals(item.getCondition(), condition);
    }
}