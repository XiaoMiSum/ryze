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

package io.github.xiaomisum.ryze.protocol.redis.config;

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.redis.RedisConstantsInterface;
import io.github.xiaomisum.ryze.support.Closeable;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import redis.clients.jedis.*;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.JedisURIHelper;

import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis数据源配置元件
 * <p>
 * 该类实现了Redis数据源的配置和管理功能，基于Jedis连接池实现。
 * 负责初始化Redis连接池，并将其注册到测试上下文中供其他元件使用。
 * </p>
 *
 * <p>
 * 主要功能：
 * <ul>
 * <li>配置和初始化Jedis连接池</li>
 * <li>注册数据源到测试上下文</li>
 * <li>提供Redis连接获取接口</li>
 * <li>释放连接池资源</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@KW(value = { "redis", "redis_datasource", "redis_data_source" })
public class RedisDatasource extends AbstractConfigureElement<RedisDatasource, RedisConfigureItem, TestSuiteResult>
        implements Closeable, RedisConstantsInterface {

    /**
     * Jedis连接池实例
     * <p>
     * 用于Standalone和Sentinel模式管理Redis连接池
     * </p>
     */
    @JSONField(serialize = false)
    private JedisPool jedisPool;

    /**
     * JedisCluster实例
     * <p>
     * 用于Cluster模式管理Redis集群连接
     * </p>
     */
    @JSONField(serialize = false)
    private JedisCluster jedisCluster;

    /**
     * JedisSentinelPool实例
     * <p>
     * 用于Sentinel模式管理Redis哨兵连接池
     * </p>
     */
    @JSONField(serialize = false)
    private JedisSentinelPool jedisSentinelPool;

    /**
     * 当前连接模式
     */
    @JSONField(serialize = false)
    private RedisMode currentMode;

    /**
     * 默认构造函数
     */
    public RedisDatasource() {
        super();
    }

    /**
     * 带构建器的构造函数
     *
     * @param builder 构建器实例
     */
    public RedisDatasource(Builder builder) {
        super(builder);
    }

    /**
     * 创建Redis数据源构建器
     *
     * @return Redis数据源构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 处理数据源配置
     * <p>
     * 根据配置的模式初始化对应的Redis连接，并将数据源注册到测试上下文的本地变量中。
     * 支持Standalone、Cluster、Sentinel三种模式。
     * </p>
     *
     * @param context 测试上下文包装器
     */
    @Override
    protected void doProcess(ContextWrapper context) {
        var config = runtime.getConfig();
        var poolConfig = buildPoolConfig(config);
        var runtimeDs = (RedisDatasource) runtime;
        runtimeDs.currentMode = config.getMode();

        switch (config.getMode()) {
            case cluster -> {
                var nodes = parseHostAndPort(config.getNodes());
                var clusterPoolConfig = new ConnectionPoolConfig();
                clusterPoolConfig.setMaxTotal(config.getMaxTotal());
                clusterPoolConfig.setMaxIdle(config.getMaxIdle());
                clusterPoolConfig.setMinIdle(config.getMinIdle());
                if (config.getMaxWaitMillis() > 0) {
                    clusterPoolConfig.setMaxWait(Duration.ofMillis(config.getMaxWaitMillis()));
                }
                clusterPoolConfig.setTestOnBorrow(config.isTestOnBorrow());
                clusterPoolConfig.setTestOnReturn(config.isTestOnReturn());
                clusterPoolConfig.setTestWhileIdle(config.isTestWhileIdle());
                var clientConfig = DefaultJedisClientConfig.builder()
                        .connectionTimeoutMillis(config.getTimeout())
                        .socketTimeoutMillis(config.getTimeout())
                        .password(config.getPassword())
                        .build();
                runtimeDs.jedisCluster = new JedisCluster(
                        nodes, clientConfig, 5,
                        Duration.ofMillis(5L * config.getTimeout()), clusterPoolConfig);
            }
            case sentinel -> {
                var sentinelNodes = parseHostAndPort(config.getNodes());
                var masterClientConfig = DefaultJedisClientConfig.builder()
                        .connectionTimeoutMillis(config.getTimeout())
                        .socketTimeoutMillis(config.getTimeout())
                        .password(config.getPassword())
                        .build();
                var sentinelClientConfig = DefaultJedisClientConfig.builder()
                        .connectionTimeoutMillis(config.getTimeout())
                        .socketTimeoutMillis(config.getTimeout())
                        .password(config.getSentinelPassword())
                        .build();
                runtimeDs.jedisSentinelPool = new JedisSentinelPool(
                        config.getMasterName(), sentinelNodes,
                        poolConfig, masterClientConfig, sentinelClientConfig);
            }
            default -> {
                var uri = URI.create(config.getUrl());
                var username = JedisURIHelper.getUser(uri);
                var password = JedisURIHelper.getPassword(uri);
                var database = JedisURIHelper.getDBIndex(uri);
                runtimeDs.jedisPool = new JedisPool(
                        poolConfig, uri.getHost(), uri.getPort(),
                        config.getTimeout(), username, password, database);
            }
        }
        context.getLocalVariablesWrapper().put(runtime.getRefName(DEF_REF_NAME_KEY), this);
    }

    /**
     * 构建通用的连接池配置
     *
     * @param config Redis配置项
     * @return Jedis连接池配置
     */
    private JedisPoolConfig buildPoolConfig(RedisConfigureItem config) {
        var poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(config.getMaxTotal());
        poolConfig.setMaxIdle(config.getMaxIdle());
        poolConfig.setMinIdle(config.getMinIdle());
        if (config.getMaxWaitMillis() > 0) {
            poolConfig.setMaxWait(Duration.ofMillis(config.getMaxWaitMillis()));
        }
        poolConfig.setTestOnBorrow(config.isTestOnBorrow());
        poolConfig.setTestOnReturn(config.isTestOnReturn());
        poolConfig.setTestWhileIdle(config.isTestWhileIdle());
        return poolConfig;
    }

    /**
     * 解析节点列表为HostAndPort集合
     *
     * @param nodes 节点列表，格式为 "host:port"
     * @return HostAndPort集合
     */
    private Set<HostAndPort> parseHostAndPort(List<String> nodes) {
        return nodes.stream()
                .map(node -> {
                    var parts = node.split(":");
                    return new HostAndPort(parts[0], Integer.parseInt(parts[1]));
                })
                .collect(Collectors.toSet());
    }

    /**
     * 获取测试结果对象
     *
     * @return 测试套件结果对象
     */
    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult("Redis数据源配置：" + runtime.getRefName());
    }

    /**
     * 关闭数据源
     * <p>
     * 释放Redis连接资源，根据当前模式关闭对应的连接池或集群实例
     * </p>
     */
    @Override
    public void close() {
        var runtimeDs = (RedisDatasource) runtime;
        if (runtimeDs.jedisPool != null) {
            runtimeDs.jedisPool.close();
            runtimeDs.jedisPool = null;
        }
        if (runtimeDs.jedisCluster != null) {
            runtimeDs.jedisCluster.close();
            runtimeDs.jedisCluster = null;
        }
        if (runtimeDs.jedisSentinelPool != null) {
            runtimeDs.jedisSentinelPool.close();
            runtimeDs.jedisSentinelPool = null;
        }
    }

    /**
     * 获取Redis连接
     * <p>
     * 仅适用于Standalone和Sentinel模式
     * </p>
     *
     * @return Redis连接实例
     */
    public Jedis getConnection() {
        var runtimeDs = (RedisDatasource) runtime;
        if (runtimeDs.jedisSentinelPool != null) {
            return runtimeDs.jedisSentinelPool.getResource();
        }
        return runtimeDs.jedisPool.getResource();
    }

    /**
     * 统一执行Redis命令
     * <p>
     * 根据当前连接模式路由命令执行：
     * <ul>
     * <li>STANDALONE/SENTINEL: 使用getResource()获取Jedis实例执行命令</li>
     * <li>CLUSTER: 直接使用JedisCluster实例调用sendCommand()</li>
     * </ul>
     * </p>
     *
     * @param command Redis命令
     * @param args    命令参数
     * @return 命令执行结果
     */
    public Object executeCommand(ProtocolCommand command, String... args) {
        var runtimeDs = (RedisDatasource) runtime;
        if (runtimeDs.currentMode == RedisMode.cluster) {
            return runtimeDs.jedisCluster.sendCommand(args[0], command, args);
        }
        try (var jedis = getConnection()) {
            return jedis.sendCommand(command, args);
        }
    }

    /**
     * 获取Redis连接URL
     *
     * @return Redis连接URL
     */
    public String getUrl() {
        return runtime.getConfig().getUrl();
    }

    /**
     * Redis数据源 测试元件 构建类
     * <p>
     * 提供链式调用方式创建Redis数据源实例。
     * </p>
     */
    public static class Builder extends
            AbstractConfigureElement.Builder<RedisDatasource, Builder, RedisConfigureItem, RedisConfigureItem.Builder, TestSuiteResult> {

        /**
         * 构建Redis数据源实例
         *
         * @return Redis数据源实例
         */
        @Override
        public RedisDatasource build() {
            return new RedisDatasource(this);
        }

        /**
         * 获取配置项构建器
         *
         * @return Redis配置项构建器
         */
        @Override
        protected RedisConfigureItem.Builder getConfigureItemBuilder() {
            return RedisConfigureItem.builder();
        }
    }
}