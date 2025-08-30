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

import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;

/**
 * JDBC请求实体类
 * <p>
 * 该类用于封装JDBC请求的相关信息，包括数据库连接信息和SQL语句。
 * 实现了SampleResult.Real接口，用于在测试报告中展示JDBC请求的详细信息。
 * </p>
 *
 * @author xiaomi
 * @since 2025/7/21 19:11
 */
public class RealJDBCRequest extends SampleResult.Real implements JDBCConstantsInterface {

    /**
     * 数据库连接URL
     */
    private final String url;

    /**
     * 数据库用户名
     */
    private final String username;

    /**
     * 数据库密码
     */
    private final String password;

    /**
     * 构造JDBC请求对象
     *
     * @param url 数据库连接URL
     * @param username 数据库用户名
     * @param password 数据库密码
     * @param sql 要执行的SQL语句
     */
    public RealJDBCRequest(String url, String username, String password, String sql) {
        super(sql.getBytes());
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * 格式化JDBC请求信息
     * <p>
     * 将JDBC请求信息格式化为易于阅读的字符串格式，包含数据库连接信息和SQL语句。
     * 格式如下：
     * <pre>
     * jdbc:mysql://localhost:3306/dbname?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
     * username: root
     * password: password
     *
     * SELECT * FROM users WHERE id = 1
     * </pre>
     * </p>
     *
     * @return 格式化后的请求信息字符串
     */
    @Override
    public String format() {
        return url + "\n" +
                USERNAME + ": " + username + "\n" +
                PASSWORD + ": " + password +
                (StringUtils.isNotBlank(bytesAsString()) ? "\n\n" + bytesAsString() : "");
    }

}