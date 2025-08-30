/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;

/**
 * JDBC协议常量接口
 * <p>
 * 该接口定义了JDBC协议相关的核心常量，包括数据源配置参数、连接属性和SQL执行相关的键名。
 * 所有JDBC协议相关的类都应该实现此接口以确保常量的一致性。
 * </p>
 *
 * @author xiaomi
 */
public interface JDBCConstantsInterface extends TestElementConstantsInterface {

    /**
     * 数据源引用名称键名
     * <p>用于在测试上下文中引用已配置的数据源</p>
     */
    String DATASOURCE = "datasource";

    /**
     * JDBC驱动类名键名
     * <p>指定要使用的JDBC驱动类的全限定名</p>
     */
    String DRIVER = "driver";

    /**
     * 数据库连接URL键名
     * <p>数据库连接字符串，包含协议、主机、端口和数据库名称等信息</p>
     */
    String URL = "url";

    /**
     * 数据库用户名键名
     * <p>用于数据库认证的用户名</p>
     */
    String USERNAME = "username";

    /**
     * 数据库密码键名
     * <p>用于数据库认证的密码</p>
     */
    String PASSWORD = "password";

    /**
     * 最大活跃连接数键名
     * <p>连接池中允许的最大活跃连接数量</p>
     */
    String MAX_ACTIVE = "max_active";

    /**
     * 最大等待时间键名
     * <p>从连接池获取连接时的最大等待时间(毫秒)</p>
     */
    String MAX_WAIT = "max_wait";

    /**
     * SQL语句键名
     * <p>要执行的SQL语句</p>
     */
    String SQL = "sql";

    /**
     * 废弃属性，请使用 sql @see {@link #SQL}
     * <p>旧版本中使用的SQL语句键名，现已废弃</p>
     */
    @Deprecated(since = "6.0.0")
    String STATEMENT = "statement";
}