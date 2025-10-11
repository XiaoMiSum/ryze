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

package io.github.xiaomisum.ryze.protocol.jdbc.config;

import io.github.xiaomisum.ryze.SessionRunner;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JDBCConfigureItemTest {

    @Test
    public void testBuilder() {
        JDBCConfigureItem item = JDBCConfigureItem.builder()
                .datasource("testDS")
                .driver("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/test")
                .username("testUser")
                .password("testPass")
                .maxActive(20)
                .maxWait(10000)
                .sql("SELECT * FROM users")
                .build();

        Assert.assertEquals(item.getDatasource(), "testDS");
        Assert.assertEquals(item.getDriver(), "com.mysql.cj.jdbc.Driver");
        Assert.assertEquals(item.getUrl(), "jdbc:mysql://localhost:3306/test");
        Assert.assertEquals(item.getUsername(), "testUser");
        Assert.assertEquals(item.getPassword(), "testPass");
        Assert.assertEquals(item.getMaxActive(), 20);
        Assert.assertEquals(item.getMaxWait(), 10000);
        Assert.assertEquals(item.getSql(), "SELECT * FROM users");
    }

    @Test
    public void testBuilderWithConfig() {
        JDBCConfigureItem baseConfig = JDBCConfigureItem.builder()
                .driver("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/test")
                .build();

        JDBCConfigureItem item = JDBCConfigureItem.builder()
                .datasource("testDS")
                .username("testUser")
                .config(baseConfig)
                .build();

        Assert.assertEquals(item.getDatasource(), "testDS");
        Assert.assertEquals(item.getDriver(), "com.mysql.cj.jdbc.Driver");
        Assert.assertEquals(item.getUrl(), "jdbc:mysql://localhost:3306/test");
        Assert.assertEquals(item.getUsername(), "testUser");
    }

    @Test
    public void testMerge() {
        JDBCConfigureItem baseItem = JDBCConfigureItem.builder()
                .datasource("baseDS")
                .driver("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/base")
                .username("baseUser")
                .password("basePass")
                .maxActive(10)
                .maxWait(5000)
                .sql("SELECT * FROM base")
                .build();

        JDBCConfigureItem otherItem = JDBCConfigureItem.builder()
                .datasource("otherDS")
                .driver("org.postgresql.Driver")
                .url("jdbc:postgresql://localhost:5432/other")
                .build();

        JDBCConfigureItem mergedItem = baseItem.merge(otherItem);

        // Check that properties from baseItem take precedence
        Assert.assertEquals(mergedItem.getDatasource(), "baseDS");
        Assert.assertEquals(mergedItem.getDriver(), "com.mysql.cj.jdbc.Driver");
        Assert.assertEquals(mergedItem.getUrl(), "jdbc:mysql://localhost:3306/base");
        Assert.assertEquals(mergedItem.getUsername(), "baseUser");
        Assert.assertEquals(mergedItem.getPassword(), "basePass");
        Assert.assertEquals(mergedItem.getMaxActive(), 10);
        Assert.assertEquals(mergedItem.getMaxWait(), 5000);
        Assert.assertEquals(mergedItem.getSql(), "SELECT * FROM base");
    }

    @Test
    public void testMergeWithNull() {
        JDBCConfigureItem baseItem = JDBCConfigureItem.builder()
                .datasource("baseDS")
                .driver("com.mysql.cj.jdbc.Driver")
                .build();

        JDBCConfigureItem mergedItem = baseItem.merge(null);

        // Should be a copy of the base item
        Assert.assertEquals(mergedItem.getDatasource(), "baseDS");
        Assert.assertEquals(mergedItem.getDriver(), "com.mysql.cj.jdbc.Driver");
    }

    @Test
    public void testMergeWithEmptyValues() {
        JDBCConfigureItem baseItem = new JDBCConfigureItem();

        JDBCConfigureItem otherItem = JDBCConfigureItem.builder()
                .driver("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/test")
                .maxActive(20)
                .maxWait(10000)
                .build();

        JDBCConfigureItem mergedItem = baseItem.merge(otherItem);

        Assert.assertEquals(mergedItem.getDriver(), "com.mysql.cj.jdbc.Driver");
        Assert.assertEquals(mergedItem.getUrl(), "jdbc:mysql://localhost:3306/test");
        Assert.assertEquals(mergedItem.getMaxActive(), 20);
        Assert.assertEquals(mergedItem.getMaxWait(), 10000);
    }

    @Test
    public void testEvaluate() {
        ContextWrapper context = new ContextWrapper(SessionRunner.getSessionIfNoneCreateNew());
        context.getLocalVariablesWrapper().put("datasource", "testDS");
        context.getLocalVariablesWrapper().put("driver", "com.mysql.cj.jdbc.Driver");
        context.getLocalVariablesWrapper().put("url", "jdbc:mysql://localhost:3306/test");
        context.getLocalVariablesWrapper().put("username", "testUser");
        context.getLocalVariablesWrapper().put("password", "testPass");
        context.getLocalVariablesWrapper().put("sql", "SELECT * FROM users");

        JDBCConfigureItem item = JDBCConfigureItem.builder()
                .datasource("${datasource}")
                .driver("${driver}")
                .url("${url}")
                .username("${username}")
                .password("${password}")
                .sql("${sql}")
                .build();

        JDBCConfigureItem evaluatedItem = item.evaluate(context);

        Assert.assertEquals(evaluatedItem.getDatasource(), "testDS");
        Assert.assertEquals(evaluatedItem.getDriver(), "com.mysql.cj.jdbc.Driver");
        Assert.assertEquals(evaluatedItem.getUrl(), "jdbc:mysql://localhost:3306/test");
        Assert.assertEquals(evaluatedItem.getUsername(), "testUser");
        Assert.assertEquals(evaluatedItem.getPassword(), "testPass");
        Assert.assertEquals(evaluatedItem.getSql(), "SELECT * FROM users");
    }

    @Test
    public void testGettersWithDefaults() {
        JDBCConfigureItem item = new JDBCConfigureItem();

        // Test default values
        Assert.assertEquals(item.getMaxActive(), 10); // default value
        Assert.assertEquals(item.getMaxWait(), 5000); // default value
    }

    @Test
    public void testSettersAndGetters() {
        JDBCConfigureItem item = new JDBCConfigureItem();

        item.setDatasource("testDS");
        item.setDriver("com.mysql.cj.jdbc.Driver");
        item.setUrl("jdbc:mysql://localhost:3306/test");
        item.setUsername("testUser");
        item.setPassword("testPass");
        item.setMaxActive(20);
        item.setMaxWait(10000);
        item.setSql("SELECT * FROM users");

        Assert.assertEquals(item.getDatasource(), "testDS");
        Assert.assertEquals(item.getDriver(), "com.mysql.cj.jdbc.Driver");
        Assert.assertEquals(item.getUrl(), "jdbc:mysql://localhost:3306/test");
        Assert.assertEquals(item.getUsername(), "testUser");
        Assert.assertEquals(item.getPassword(), "testPass");
        Assert.assertEquals(item.getMaxActive(), 20);
        Assert.assertEquals(item.getMaxWait(), 10000);
        Assert.assertEquals(item.getSql(), "SELECT * FROM users");
    }

    @Test
    public void testMaxActiveDefaultValue() {
        JDBCConfigureItem item = new JDBCConfigureItem();
        item.setMaxActive(0);
        Assert.assertEquals(item.getMaxActive(), 10); // Should return default when <= 1

        item.setMaxActive(1);
        Assert.assertEquals(item.getMaxActive(), 10); // Should return default when <= 1

        item.setMaxActive(5);
        Assert.assertEquals(item.getMaxActive(), 5); // Should return set value when > 1
    }

    @Test
    public void testMaxWaitDefaultValue() {
        JDBCConfigureItem item = new JDBCConfigureItem();
        item.setMaxWait(500);
        Assert.assertEquals(item.getMaxWait(), 5000); // Should return default when < 1000

        item.setMaxWait(1000);
        Assert.assertEquals(item.getMaxWait(), 1000); // Should return set value when >= 1000
    }
}