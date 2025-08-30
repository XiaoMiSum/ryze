/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * 'Software'), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package io.github.xiaomisum.ryze.protocol.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC协议执行器
 * <p>
 * 该类负责执行JDBC SQL语句，支持查询和更新操作。使用Druid连接池管理数据库连接，
 * 提供SQL执行时间统计和结果格式化功能。
 * </p>
 * 
 * <p>主要功能：
 * <ul>
 *   <li>通过Druid连接池执行SQL语句</li>
 *   <li>自动处理查询和更新语句的结果</li>
 *   <li>将查询结果转换为JSON格式</li>
 *   <li>记录SQL执行时间</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @since 2025/7/21 19:31
 */
public class JDBC {

    /**
     * 执行SQL语句并返回结果
     * <p>
     * 该方法会从数据源获取连接，执行SQL语句，并根据语句类型返回相应结果：
     * <ul>
     *   <li>对于查询语句(SELECT等)，返回查询结果的JSON格式字符串</li>
     *   <li>对于更新语句(INSERT/UPDATE/DELETE等)，返回受影响的行数</li>
     * </ul>
     * </p>
     *
     * @param datasource 数据源，使用Druid连接池
     * @param sql 要执行的SQL语句
     * @param result 采样结果对象，用于记录执行时间
     * @return SQL执行结果的字节数组形式
     * @throws RuntimeException 当SQL执行过程中发生异常时抛出
     */
    public static byte[] execute(DruidDataSource datasource, String sql, DefaultSampleResult result) {
        result.sampleStart();
        try (var conn = datasource.getConnection(); var statement = conn.createStatement()) {
            var hasResultSet = statement.execute(sql);
            return (hasResultSet ? toJSONBytes(statement) : "Affected rows: " + statement.getUpdateCount()).getBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            result.sampleEnd();
        }
    }

    /**
     * 将SQL查询结果转换为JSON格式的字符串
     * <p>
     * 该方法将ResultSet中的数据转换为JSON格式：
     * <ul>
 *     <li>当结果只有一行时，返回单个JSON对象</li>
 *     <li>当结果有多行时，返回JSON数组</li>
 *   </ul>
     * </p>
     *
     * @param statement 执行完查询的Statement对象
     * @return JSON格式的结果字符串
     * @throws SQLException 当处理结果集时发生SQL异常
     */
    private static String toJSONBytes(Statement statement) throws SQLException {
        try (var result = statement.getResultSet()) {
            var results = new JSONArray();
            var meta = result.getMetaData();
            while (result.next()) {
                var item = new JSONObject();
                for (var i = 1; i <= meta.getColumnCount(); i++) {
                    String key = meta.getColumnName(i);
                    item.put(key, result.getString(key));
                }
                results.add(item);
            }
            return results.size() == 1 ? results.getJSONObject(0).toJSONString() : results.toJSONString();
        }
    }
}