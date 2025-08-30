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
import com.alibaba.fastjson2.annotation.JSONType;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import io.github.xiaomisum.ryze.protocol.redis.RedisConstantsInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.function.Consumer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * Redis协议配置项类
 * <p>
 * 该类封装了Redis协议的所有配置参数，包括数据源引用、连接URL、命令、参数、
 * 超时时间、连接池配置等。实现了ConfigureItem接口，支持配置项的合并和变量求值。
 * </p>
 *
 * @author xiaomi
 */
@JSONType(deserializer = RedisJSONInterceptor.class)
public class RedisConfigureItem implements ConfigureItem<RedisConfigureItem>, RedisConstantsInterface {

    /**
     * 数据源引用名称
     * <p>用于引用已配置的Redis数据源</p>
     */
    @JSONField(name = DATASOURCE)
    protected String datasource;

    /**
     * Redis连接URL
     * <p>Redis连接字符串，格式为：redis://[username:password@]host[:port][/db]</p>
     */
    @JSONField(name = URL, ordinal = 1)
    protected String url;

    /**
     * Redis命令
     * <p>要执行的Redis命令名称</p>
     */
    @JSONField(name = COMMAND, ordinal = 6)
    protected String command;

    /**
     * Redis命令参数
     * <p>Redis命令的参数列表，以逗号分隔</p>
     */
    @JSONField(name = ARGS, ordinal = 7)
    protected String args;

    /**
     * 连接超时时间
     * <p>Redis连接超时时间(毫秒)，默认值为10000</p>
     */
    @JSONField(name = TIMEOUT, ordinal = 2)
    protected int timeout;

    /**
     * 最大总连接数
     * <p>Jedis连接池的最大总连接数，默认值为10</p>
     */
    @JSONField(name = MAX_TOTAL, ordinal = 3)
    protected int maxTotal;

    /**
     * 最大空闲连接数
     * <p>Jedis连接池的最大空闲连接数，默认值为5</p>
     */
    @JSONField(name = MAX_IDLE, ordinal = 4)
    protected int maxIdle;

    /**
     * 最小空闲连接数
     * <p>Jedis连接池的最小空闲连接数，默认值为1</p>
     */
    @JSONField(name = MIN_IDLE, ordinal = 5)
    protected int minIdle;

    /**
     * 默认构造函数
     */
    public RedisConfigureItem() {
    }

    /**
     * 创建Redis配置项构建器
     *
     * @return Redis配置项构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }


    /**
     * 合并配置项
     * <p>
     * 将当前配置项与另一个配置项合并，当当前配置项的某个属性为空或无效时，
     * 使用另一个配置项的对应属性值。
     * </p>
     *
     * @param other 要合并的另一个配置项
     * @return 合并后的新配置项
     */
    @Override
    public RedisConfigureItem merge(RedisConfigureItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.datasource = StringUtils.isBlank(self.datasource) ? localOther.datasource : self.datasource;
        self.url = StringUtils.isBlank(self.url) ? localOther.url : self.url;
        self.command = StringUtils.isBlank(self.command) ? localOther.command : self.command;
        self.args = StringUtils.isBlank(self.args) ? localOther.args : self.args;
        self.timeout = (self.timeout = self.timeout > 0 ? localOther.timeout : self.timeout) > 0 ? self.timeout : 10000;
        self.maxTotal = (self.maxTotal = self.maxTotal > 0 ? localOther.maxTotal : self.maxTotal) > 0 ? self.maxTotal : 10;
        self.maxIdle = (self.maxIdle = self.maxIdle > 0 ? localOther.maxIdle : self.maxIdle) > 0 ? self.maxIdle : 5;
        self.minIdle = (self.minIdle = self.minIdle > 0 ? localOther.minIdle : self.minIdle) > 0 ? self.minIdle : 1;
        return self;
    }

    /**
     * 对配置项中的变量进行求值
     * <p>
     * 使用测试上下文中的变量值替换配置项中的变量占位符。
     * </p>
     *
     * @param context 测试上下文包装器
     * @return 求值后的配置项
     */
    @Override
    public RedisConfigureItem evaluate(ContextWrapper context) {
        datasource = (String) context.evaluate(datasource);
        url = (String) context.evaluate(url);
        command = (String) context.evaluate(command);
        args = (String) context.evaluate(args);
        return this;
    }

    /**
     * 获取数据源引用名称
     *
     * @return 数据源引用名称
     */
    public String getDatasource() {
        return datasource;
    }

    /**
     * 设置数据源引用名称
     *
     * @param datasource 数据源引用名称
     */
    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    /**
     * 获取Redis连接URL
     *
     * @return Redis连接URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置Redis连接URL
     *
     * @param url Redis连接URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取Redis命令（大写格式）
     *
     * @return Redis命令
     */
    public String getCommand() {
        return command.toUpperCase(Locale.ROOT);
    }

    /**
     * 设置Redis命令
     *
     * @param command Redis命令
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * 获取Redis命令参数
     *
     * @return Redis命令参数
     */
    public String getArgs() {
        return args;
    }

    /**
     * 设置Redis命令参数
     *
     * @param args Redis命令参数
     */
    public void setArgs(String args) {
        this.args = args;
    }

    /**
     * 获取连接超时时间
     *
     * @return 连接超时时间(毫秒)，默认值为10000
     */
    public int getTimeout() {
        return timeout > 0 ? timeout : 10000;
    }

    /**
     * 设置连接超时时间
     *
     * @param timeout 连接超时时间(毫秒)
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 获取最大总连接数
     *
     * @return 最大总连接数，默认值为10
     */
    public int getMaxTotal() {
        return maxTotal > 0 ? maxTotal : 10;
    }

    /**
     * 设置最大总连接数
     *
     * @param maxTotal 最大总连接数
     */
    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    /**
     * 获取最大空闲连接数
     *
     * @return 最大空闲连接数，默认值为5
     */
    public int getMaxIdle() {
        return maxIdle > 0 ? maxIdle : 5;
    }

    /**
     * 设置最大空闲连接数
     *
     * @param maxIdle 最大空闲连接数
     */
    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    /**
     * 获取最小空闲连接数
     *
     * @return 最小空闲连接数，默认值为1
     */
    public int getMinIdle() {
        return minIdle > 0 ? minIdle : 1;
    }

    /**
     * 设置最小空闲连接数
     *
     * @param minIdle 最小空闲连接数
     */
    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    /**
     * Redis 协议配置项构建类
     * <p>
     * 提供链式调用方式创建Redis配置项实例，支持设置所有Redis相关配置参数。
     * </p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, RedisConfigureItem> {

        private RedisConfigureItem configure = new RedisConfigureItem();

        /**
         * 设置数据源引用名称
         *
         * @param datasource 数据源引用名称
         * @return 构建器实例
         */
        public Builder datasource(String datasource) {
            configure.datasource = datasource;
            return self;
        }

        /**
         * 设置Redis连接URL
         *
         * @param url Redis连接URL
         * @return 构建器实例
         */
        public Builder url(String url) {
            configure.url = url;
            return self;
        }

        /**
         * 设置Redis命令
         *
         * @param command Redis命令
         * @return 构建器实例
         */
        public Builder command(String command) {
            configure.command = command;
            return self;
        }

        /**
         * 设置Redis命令参数
         *
         * @param args Redis命令参数
         * @return 构建器实例
         */
        public Builder args(String args) {
            configure.args = args;
            return self;
        }

        /**
         * 设置连接超时时间
         *
         * @param timeout 连接超时时间(毫秒)
         * @return 构建器实例
         */
        public Builder timeout(int timeout) {
            configure.timeout = timeout;
            return self;
        }

        /**
         * 设置最大总连接数
         *
         * @param maxTotal 最大总连接数
         * @return 构建器实例
         */
        public Builder maxTotal(int maxTotal) {
            configure.maxTotal = maxTotal;
            return self;
        }

        /**
         * 设置最大空闲连接数
         *
         * @param maxIdle 最大空闲连接数
         * @return 构建器实例
         */
        public Builder maxIdle(int maxIdle) {
            configure.maxIdle = maxIdle;
            return self;
        }

        /**
         * 设置最小空闲连接数
         *
         * @param minIdle 最小空闲连接数
         * @return 构建器实例
         */
        public Builder minIdle(int minIdle) {
            configure.minIdle = minIdle;
            return self;
        }

        /**
         * 通过消费者函数配置
         *
         * @param consumer 配置消费者函数
         * @return 构建器实例
         */
        public Builder config(Consumer<Builder> consumer) {
            var builder = RedisConfigureItem.builder();
            consumer.accept(builder);
            configure = configure.merge(builder.build());
            return self;
        }

        /**
         * 通过闭包配置
         *
         * @param closure 配置闭包
         * @return 构建器实例
         */
        public Builder config(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = Builder.class) Closure<?> closure) {
            var builder = RedisConfigureItem.builder();
            call(closure, builder);
            configure = configure.merge(builder.build());
            return self;
        }

        /**
         * 通过构建器配置
         *
         * @param builder 配置项构建器
         * @return 构建器实例
         */
        public Builder config(Builder builder) {
            configure = configure.merge(builder.build());
            return self;
        }

        /**
         * 通过配置项配置
         *
         * @param config 配置项
         * @return 构建器实例
         */
        public Builder config(RedisConfigureItem config) {
            configure = configure.merge(config);
            return self;
        }

        /**
         * 构建Redis配置项实例
         *
         * @return Redis配置项实例
         */
        @Override
        public RedisConfigureItem build() {
            return configure;
        }
    }
}