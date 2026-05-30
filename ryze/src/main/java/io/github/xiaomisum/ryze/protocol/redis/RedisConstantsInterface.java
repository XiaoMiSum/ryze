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
     * 默认引用名称键
     * <p>
     * 用于在测试上下文中引用默认Redis数据源配置
     * </p>
     */
    String DEF_REF_NAME_KEY = "__redis_configure_element_default_ref_name__";

    /**
     * 数据源引用名称键名
     * <p>
     * 用于在测试上下文中引用已配置的Redis数据源
     * </p>
     */
    String DATASOURCE = "datasource";

    /**
     * Redis URL模板
     * <p>
     * Redis连接URL的标准格式前缀
     * </p>
     */
    String REDIS_URL_TEMPLATE = "redis://";

    /**
     * Redis连接URL键名
     * <p>
     * Redis连接字符串，格式为：redis://[username:password@]host[:port][/db]
     * </p>
     */
    String URL = "url";

    /**
     * 连接超时时间键名
     * <p>
     * Redis连接超时时间(毫秒)
     * </p>
     */
    String TIMEOUT = "timeout";

    /**
     * 最大总连接数键名
     * <p>
     * Jedis连接池的最大总连接数
     * </p>
     */
    String MAX_TOTAL = "max_total";

    /**
     * 最大空闲连接数键名
     * <p>
     * Jedis连接池的最大空闲连接数
     * </p>
     */
    String MAX_IDLE = "max_idle";

    /**
     * 最小空闲连接数键名
     * <p>
     * Jedis连接池的最小空闲连接数
     * </p>
     */
    String MIN_IDLE = "min_idle";

    /**
     * Redis命令键名
     * <p>
     * 要执行的Redis命令名称
     * </p>
     */
    String COMMAND = "command";

    /**
     * Redis命令参数键名
     * <p>
     * Redis命令的参数列表
     * </p>
     */
    String ARGS = "args";

    /**
     * Redis部署模式键名
     * <p>
     * 支持STANDALONE、CLUSTER、SENTINEL三种模式
     * </p>
     */
    String MODE = "mode";

    /**
     * Redis集群/哨兵节点列表键名
     * <p>
     * 格式为 "host:port"，用于Cluster和Sentinel模式
     * </p>
     */
    String NODES = "nodes";

    /**
     * Sentinel主节点名称键名
     * <p>
     * Sentinel模式下的主节点名称
     * </p>
     */
    String MASTER_NAME = "master_name";

    /**
     * Redis密码键名
     * <p>
     * Redis服务器认证密码
     * </p>
     */
    String PASSWORD = "password";

    /**
     * Sentinel密码键名
     * <p>
     * Sentinel节点认证密码
     * </p>
     */
    String SENTINEL_PASSWORD = "sentinel_password";

    /**
     * 连接等待超时键名
     * <p>
     * 从连接池获取连接的最大等待时间(毫秒)
     * </p>
     */
    String MAX_WAIT_MILLIS = "max_wait_millis";

    /**
     * 借用时检测键名
     * <p>
     * 从连接池获取连接时是否进行有效性检测
     * </p>
     */
    String TEST_ON_BORROW = "test_on_borrow";

    /**
     * 归还时检测键名
     * <p>
     * 归还连接到连接池时是否进行有效性检测
     * </p>
     */
    String TEST_ON_RETURN = "test_on_return";

    /**
     * 空闲时检测键名
     * <p>
     * 空闲连接是否进行有效性检测
     * </p>
     */
    String TEST_WHILE_IDLE = "test_while_idle";
}