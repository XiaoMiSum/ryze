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

package io.github.xiaomisum.ryze.protocol.redis;

import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;

/**
 * Redis协议常量接口
 * <p>
 * 该接口定义了Redis协议相关的核心常量，包括配置参数键名、URL模板等。
 * 所有Redis协议相关的类都应该实现此接口以确保常量的一致性。
 * </p>
 *
 * @author xiaomi
 */
public interface RedisConstantsInterface extends TestElementConstantsInterface {

    /**
     * 数据源引用名称键名
     * <p>用于在测试上下文中引用已配置的Redis数据源</p>
     */
    String DATASOURCE = "datasource";

    /**
     * Redis URL模板
     * <p>Redis连接URL的标准格式前缀</p>
     */
    String REDIS_URL_TEMPLATE = "redis://";
    
    /**
     * Redis连接URL键名
     * <p>Redis连接字符串，格式为：redis://[username:password@]host[:port][/db]</p>
     */
    String URL = "url";
    
    /**
     * 废弃属性，请使用 host @see {@link #URL}
     * <p>旧版本中使用的主机键名，现已废弃</p>
     */
    @Deprecated(since = "6.0.0")
    String HOST = "host";
    
    /**
     * 废弃属性，请使用 host @see {@link #URL}
     * <p>旧版本中使用的端口键名，现已废弃</p>
     */
    @Deprecated(since = "6.0.0")
    String PORT = "port";
    
    /**
     * 废弃属性，请使用 host @see {@link #URL}
     * <p>旧版本中使用的数据库索引键名，现已废弃</p>
     */
    @Deprecated(since = "6.0.0")
    String DATABASE = "database";
    
    /**
     * 废弃属性，请使用 host @see {@link #URL}
     * <p>旧版本中使用的用户名键名，现已废弃</p>
     */
    @Deprecated(since = "6.0.0")
    String USERNAME = "username";
    
    /**
     * 废弃属性，请使用 host @see {@link #URL}
     * <p>旧版本中使用的密码键名，现已废弃</p>
     */
    @Deprecated(since = "6.0.0")
    String PASSWORD = "password";
    
    /**
     * 废弃属性，请使用 timeout @see {@link #TIMEOUT}
     * <p>旧版本中使用的时间超时键名，现已废弃</p>
     */
    @Deprecated(since = "6.0.0", forRemoval = true)
    String TIME_OUT = "time_out";

    /**
     * 连接超时时间键名
     * <p>Redis连接超时时间(毫秒)</p>
     */
    String TIMEOUT = "timeout";

    /**
     * 最大总连接数键名
     * <p>Jedis连接池的最大总连接数</p>
     */
    String MAX_TOTAL = "max_total";

    /**
     * 最大空闲连接数键名
     * <p>Jedis连接池的最大空闲连接数</p>
     */
    String MAX_IDLE = "max_idle";

    /**
     * 最小空闲连接数键名
     * <p>Jedis连接池的最小空闲连接数</p>
     */
    String MIN_IDLE = "min_idle";

    /**
     * Redis命令键名
     * <p>要执行的Redis命令名称</p>
     */
    String COMMAND = "command";

    /**
     * Redis命令参数键名
     * <p>Redis命令的参数列表</p>
     */
    String ARGS = "args";

    /**
     * 废弃属性，请使用 args @see {@link #ARGS}
     * <p>旧版本中使用的发送数据键名，现已废弃</p>
     */
    @Deprecated(since = "6.0.0", forRemoval = true)
    String SEND = "send";
}