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

package io.github.xiaomisum.ryze.protocol.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Ignore
public class JDBCTest {

    private DruidDataSource mockDataSource;

    @BeforeMethod
    public void setUp() throws SQLException {
        mockDataSource = new DruidDataSource();
        mockDataSource.setUrl("jdbc:mysql://localhost:3306/ryze-test");
        mockDataSource.setUsername("root");
        mockDataSource.setPassword("123456qq!");
    }

    @AfterMethod
    public void tearDown() {
        // 清理资源
        JDBC.execute(mockDataSource, "truncate table t_001;", null, new DefaultSampleResult("test"));
    }

    @Test()
    public void testExecuteWithPreparedStatement() {
        // 准备测试数据
        String sql = "insert into t_001 (id, tick, name) values (?, ?, ?);";
        List<Object> args = new ArrayList<>();
        args.add(1);
        args.add("tick_1");
        args.add("name_1");

        // 执行测试
        byte[] result = JDBC.execute(mockDataSource, sql, args, new DefaultSampleResult("test"));

        // 验证结果
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length > 0);

    }

    @Test
    public void testExecuteWithInsertResult() {
        // 准备测试数据
        String sql = "insert into t_001 (id, tick, name) value (1, 'tick_1', 'name_1')";
        List<Object> args = new ArrayList<>();

        // 执行测试
        byte[] result = JDBC.execute(mockDataSource, sql, args, new DefaultSampleResult("test"));

        // 验证结果
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length > 0);
        Assert.assertTrue(new String(result).contains("Affected rows: 1"));
    }

    @Test
    public void testExecuteWithSelectResult() throws SQLException {
        // 准备测试数据
        String sql = "insert into t_001 (id, tick, name) value (1, 'tick_1', 'name_1')";
        List<Object> args = new ArrayList<>();

        JDBC.execute(mockDataSource, sql, args, new DefaultSampleResult("test"));

        // 执行测试
        byte[] result = JDBC.execute(mockDataSource, "select * from t_001;", args, new DefaultSampleResult("test"));

        // 验证结果
        Assert.assertNotNull(result);
        String resultStr = new String(result);
        Assert.assertEquals(JSON.parseObject(resultStr).getString("name"), "name_1");
    }

    @Test
    public void testExecuteWithUpdateResult() throws SQLException {
        // 准备测试数据
        String sql = "insert into t_001 (id, tick, name) value (1, 'tick_1', 'name_1')";
        List<Object> args = new ArrayList<>();

        JDBC.execute(mockDataSource, sql, args, new DefaultSampleResult("test"));

        // 执行测试
        byte[] update = JDBC.execute(mockDataSource, "update t_001 set name = 'name_test' where id = 1;", args, new DefaultSampleResult("test"));
        Assert.assertTrue(new String(update).contains("Affected rows: 1"));
        // 执行测试
        byte[] result = JDBC.execute(mockDataSource, "select * from t_001 where id = 1;", args, new DefaultSampleResult("test"));

        // 验证结果
        Assert.assertNotNull(result);
        String resultStr = new String(result);

        Assert.assertEquals(JSON.parseObject(resultStr).getString("name"), "name_test");
    }
}