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
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import io.github.xiaomisum.ryze.protocol.redis.RedisConstantsInterface;
import io.github.xiaomisum.ryze.support.Closeable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.util.JedisURIHelper;

import java.net.URI;
import java.util.Objects;

/**
 * Redis数据源配置元件
 * <p>
 * 该类实现了Redis数据源的配置和管理功能，基于Jedis连接池实现。
 * 负责初始化Redis连接池，并将其注册到测试上下文中供其他元件使用。
 * </p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>配置和初始化Jedis连接池</li>
 *   <li>注册数据源到测试上下文</li>
 *   <li>提供Redis连接获取接口</li>
 *   <li>释放连接池资源</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@KW(value = {"redis", "redis_datasource", "redis_data_source"})
public class RedisDatasource extends AbstractConfigureElement<RedisDatasource, RedisConfigureItem, TestSuiteResult>
        implements Closeable, RedisConstantsInterface {

    /**
     * Jedis连接池实例
     * <p>用于管理Redis连接池</p>
     */
    @JSONField(serialize = false)
    private JedisPool jedisPool;

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
     * 初始化Jedis连接池，设置连接参数，并将数据源注册到测试上下文的本地变量中。
     * </p>
     *
     * @param context 测试上下文包装器
     */
    @Override
    protected void doProcess(ContextWrapper context) {
        var poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(runtime.getConfig().getMaxTotal());
        poolConfig.setMaxIdle(runtime.getConfig().getMaxIdle());
        poolConfig.setMinIdle(runtime.getConfig().getMinIdle());
        var uri = URI.create(runtime.getConfig().url);
        var username = JedisURIHelper.getUser(uri);
        var password = JedisURIHelper.getPassword(uri);
        var database = JedisURIHelper.getDBIndex(uri);
        jedisPool = new JedisPool(poolConfig, uri.getHost(), uri.getPort(), runtime.getConfig().getTimeout(), username, password, database);
        context.getSessionRunner().getContext().getLocalVariablesWrapper().put(refName, this);
    }

    /**
     * 获取测试结果对象
     *
     * @return 测试套件结果对象
     */
    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult("Redis数据源配置：" + refName);
    }

    /**
     * 关闭数据源
     * <p>释放Jedis连接池占用的资源</p>
     */
    @Override
    public void close() {
        if (Objects.isNull(jedisPool)) {
            return;
        }
        jedisPool.close();
    }

    /**
     * 获取Redis连接
     *
     * @return Redis连接实例
     */
    public Jedis getConnection() {
        return jedisPool.getResource();
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
    public static class Builder extends AbstractConfigureElement.Builder<RedisDatasource, Builder, RedisConfigureItem, RedisConfigureItem.Builder, TestSuiteResult> {

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