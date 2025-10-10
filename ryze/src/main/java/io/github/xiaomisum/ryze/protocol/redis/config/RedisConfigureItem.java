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
import io.github.xiaomisum.ryze.protocol.redis.RedisConstantsInterface;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.commands.ProtocolCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;
import static redis.clients.jedis.Protocol.Command.*;

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
@SuppressWarnings("rowtypes")
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
    protected List<String> args;

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
        self.args = self.getArgs() == null || self.args.isEmpty() ? localOther.args : self.args;
        self.timeout = (self.timeout = self.timeout > 0 ? self.timeout : localOther.timeout) > 0 ? self.timeout : 10000;
        self.maxTotal = (self.maxTotal = self.maxTotal > 0 ? self.maxTotal : localOther.maxTotal) > 0 ? self.maxTotal : 10;
        self.maxIdle = (self.maxIdle = self.maxIdle > 0 ? self.maxIdle : localOther.maxIdle) > 0 ? self.maxIdle : 5;
        self.minIdle = (self.minIdle = self.minIdle > 0 ? self.minIdle : localOther.minIdle) > 0 ? self.minIdle : 1;
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
        args = (List<String>) context.evaluate(args);
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
        return StringUtils.isBlank(command) ? "" : command.toUpperCase(Locale.ROOT);
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
    public List<String> getArgs() {
        return args;
    }

    /**
     * 设置Redis命令参数
     *
     * @param args Redis命令参数
     */
    public void setArgs(List<String> args) {
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

        private static String[] parseArgs(String key, String field, String... fields) {
            var args = new String[fields.length + 2];
            args[0] = key;
            args[1] = field;
            System.arraycopy(fields, 0, args, 2, fields.length);
            return args;
        }

        private static String[] parseArgs(String key, String... fields) {
            var args = new String[fields.length + 1];
            args[0] = key;
            System.arraycopy(fields, 0, args, 1, fields.length);
            return args;
        }


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
        public Builder args(String arg, String... args) {
            var localArgs = Collections.newArrayList(arg);
            localArgs.addAll(Arrays.stream(args).toList());
            configure.args = Collections.addAllIfNonNull(configure.args, localArgs);
            return self;
        }


        /**
         * 删除指定的键
         *
         * @param key 键名
         * @return 构建器实例
         */
        public Builder del(String key) {
            return command(DEL, key);
        }

        /**
         * 获取指定键的值
         *
         * @param key 键名
         * @return 构建器实例
         */
        public Builder get(String key) {
            return command(GET, key);
        }

        /**
         * 设置指定键的值
         *
         * @param key   键名
         * @param value 值
         * @return 构建器实例
         */
        public Builder set(String key, String value) {
            return command(SET, key, value);
        }

        /**
         * 检查指定键是否存在
         *
         * @param key 键名
         * @return 构建器实例
         */
        public Builder exists(String key) {
            return command(EXISTS, key);
        }

        /**
         * 设置键的过期时间
         *
         * @param key     键名
         * @param timeout 过期时间(秒)
         * @return 构建器实例
         */
        public Builder expire(String key, int timeout) {
            return command(EXPIRE, key, String.valueOf(timeout));
        }

        /**
         * 查找所有符合给定模式的键
         *
         * @param key 模式
         * @return 构建器实例
         */
        public Builder keys(String key) {
            return command(KEYS, key);
        }

        /**
         * 删除哈希表中一个或多个指定字段
         *
         * @param key    哈希表键名
         * @param field  字段名
         * @param fields 更多字段名
         * @return 构建器实例
         */
        public Builder hdel(String key, String field, String... fields) {
            return command(HDEL, parseArgs(key, field, fields));
        }

        /**
         * 获取存储在哈希表中指定字段的值
         *
         * @param key    哈希表键名
         * @param field  字段名
         * @param fields 更多字段名
         * @return 构建器实例
         */
        public Builder hget(String key, String field, String... fields) {
            return command(HGET, parseArgs(key, field, fields));
        }

        /**
         * 同时将多个 field-value (字段-值)对设置到哈希表中
         *
         * @param key   哈希表键名
         * @param field 字段名
         * @param value 值
         * @return 构建器实例
         */
        public Builder hset(String key, String field, String value) {
            return command(HSET, key, field, value);
        }

        /**
         * 获取在哈希表中指定 key 的所有字段和值
         *
         * @param key 哈希表键名
         * @return 构建器实例
         */
        public Builder hgetAll(String key) {
            return command(HGETALL, key);
        }

        /**
         * 查看哈希表的指定字段是否存在
         *
         * @param key    哈希表键名
         * @param field  字段名
         * @param fields 更多字段名
         * @return 构建器实例
         */
        public Builder hexists(String key, String field, String... fields) {
            return command(HEXISTS, parseArgs(key, field, fields));
        }

        /**
         * 获取哈希表中字段的数量
         *
         * @param key 哈希表键名
         * @return 构建器实例
         */
        public Builder hlen(String key) {
            return command(HLEN, key);
        }

        /**
         * 获取哈希表中所有的字段名
         *
         * @param key 哈希表键名
         * @return 构建器实例
         */
        public Builder hkeys(String key) {
            return command(HKEYS, key);
        }

        /**
         * 获取哈希表中所有的值
         *
         * @param key 哈希表键名
         * @return 构建器实例
         */
        public Builder hvals(String key) {
            return command(HVALS, key);
        }

        /**
         * 同时将多个 field-value (字段-值)对设置到哈希表中
         *
         * @param key      哈希表键名
         * @param field    字段名
         * @param value    值
         * @param keywords 更多字段和值的交替列表
         * @return 构建器实例
         */
        public Builder hmset(String key, String field, String value, String... keywords) {
            var tmp = new String[]{key, field, value};
            var args = new String[tmp.length + keywords.length];
            // 拷贝 key field value
            System.arraycopy(tmp, 0, args, 0, tmp.length);
            // 拷贝 keywords
            System.arraycopy(keywords, 0, args, tmp.length, keywords.length);
            return command(HMSET, args);
        }

        /**
         * 获取所有给定字段的值
         *
         * @param key    哈希表键名
         * @param field  字段名
         * @param fields 更多字段名
         * @return 构建器实例
         */
        public Builder hmget(String key, String field, String... fields) {
            return command(HMGET, parseArgs(key, field, fields));
        }

        /**
         * 通过索引获取列表中的元素
         *
         * @param key   列表键名
         * @param index 索引
         * @return 构建器实例
         */
        public Builder lindex(String key, int index) {
            return command(LINDEX, key, String.valueOf(index));
        }

        /**
         * 获取列表中指定范围的元素
         *
         * @param key   列表键名
         * @param start 起始位置
         * @param end   结束位置
         * @return 构建器实例
         */
        public Builder lrange(String key, int start, int end) {
            return command(LRANGE, key, String.valueOf(start), String.valueOf(end));
        }

        /**
         * 获取列表的长度
         *
         * @param key 列表键名
         * @return 构建器实例
         */
        public Builder llen(String key) {
            return command(LRANGE, key);
        }

        /**
         * 移除并获取列表的第一个元素
         *
         * @param key 列表键名
         * @return 构建器实例
         */
        public Builder lpop(String key) {
            return command(LPOP, key);
        }

        /**
         * 将一个或多个值插入到列表头部
         *
         * @param key    列表键名
         * @param value  值
         * @param values 更多值
         * @return 构建器实例
         */
        public Builder lpush(String key, String value, String... values) {
            return command(LPUSH, parseArgs(key, value, values));
        }

        /**
         * 将值插入到已存在的列表头部
         *
         * @param key   列表键名
         * @param value 值
         * @return 构建器实例
         */
        public Builder lpushx(String key, String value) {
            return command(LPUSHX, key, value);
        }

        /**
         * 通过索引设置列表元素的值
         *
         * @param key   列表键名
         * @param index 索引
         * @param value 值
         * @return 构建器实例
         */
        public Builder lset(String key, int index, String value) {
            return command(LSET, key, String.valueOf(index), value);
        }

        /**
         * 对列表进行修剪
         *
         * @param key   列表键名
         * @param start 起始位置
         * @param end   结束位置
         * @return 构建器实例
         */
        public Builder ltrim(String key, int start, int end) {
            return command(LTRIM, key, String.valueOf(start), String.valueOf(end));
        }

        /**
         * 移除并获取列表的最后一个元素
         *
         * @param key 列表键名
         * @return 构建器实例
         */
        public Builder rpop(String key) {
            return command(RPOP, key);
        }

        /**
         * 将一个或多个值插入到列表尾部
         *
         * @param key    列表键名
         * @param value  值
         * @param values 更多值
         * @return 构建器实例
         */
        public Builder rpush(String key, String value, String... values) {
            return command(RPUSH, parseArgs(key, value, values));
        }

        /**
         * 将值插入到已存在的列表尾部
         *
         * @param key   列表键名
         * @param value 值
         * @return 构建器实例
         */
        public Builder rpushx(String key, String value) {
            return command(RPUSHX, key, value);
        }

        /**
         * 向集合添加一个或多个成员
         *
         * @param key    集合键名
         * @param value  成员
         * @param values 更多成员
         * @return 构建器实例
         */
        public Builder sadd(String key, String value, String... values) {
            return command(SADD, parseArgs(key, value, values));
        }

        /**
         * 获取集合的成员数
         *
         * @param key 集合键名
         * @return 构建器实例
         */
        public Builder scard(String key) {
            return command(SCARD, key);
        }

        /**
         * 返回给定集合的差集
         *
         * @param key  集合键名
         * @param keys 更多集合键名
         * @return 构建器实例
         */
        public Builder sdiff(String key, String... keys) {
            return command(SDIFF, parseArgs(key, keys));
        }

        /**
         * 返回给定集合的差集并存储在指定的集合中
         *
         * @param key  集合键名
         * @param keys 更多集合键名
         * @return 构建器实例
         */
        public Builder sdiffstore(String key, String... keys) {
            return command(SDIFFSTORE, parseArgs(key, keys));
        }

        /**
         * 返回给定所有集合的交集
         *
         * @param key  集合键名
         * @param keys 更多集合键名
         * @return 构建器实例
         */
        public Builder sinter(String key, String... keys) {
            return command(SINTER, parseArgs(key, keys));
        }

        /**
         * 返回集合中的所有成员
         *
         * @param key 集合键名
         * @return 构建器实例
         */
        public Builder smembers(String key) {
            return command(SMEMBERS, key);
        }

        /**
         * 判断 member 元素是否是集合 key 的成员
         *
         * @param key   集合键名
         * @param value 成员
         * @return 构建器实例
         */
        public Builder smismember(String key, String value) {
            return command(SMISMEMBER, key, value);
        }

        /**
         * 移除并返回集合中的一个随机元素
         *
         * @param key 集合键名
         * @return 构建器实例
         */
        public Builder spop(String key) {
            return command(SPOP, key);
        }

        /**
         * 移除集合中一个或多个成员
         *
         * @param key    集合键名
         * @param value  成员
         * @param values 更多成员
         * @return 构建器实例
         */
        public Builder srem(String key, String value, String... values) {
            return command(SREM, parseArgs(key, value, values));
        }

        /**
         * 返回给定所有集合的并集
         *
         * @param key  集合键名
         * @param keys 更多集合键名
         * @return 构建器实例
         */
        public Builder sunion(String key, String... keys) {
            return command(SUNION, parseArgs(key, keys));
        }

        /**
         * 将信息发送到指定的频道。
         * 无法发送 json格式的字符串，因为key 和 message 会以逗号作为分隔符串连为一个新字符串。
         * <p>
         * 以 {@link Jedis#sendCommand(ProtocolCommand, String...)}  发送 command 和 args(key,message) 时，会以逗号作为分隔符重新创建一个 String[]，
         * json格式的 message 会被分割成多个参数，从而导致错误。
         *
         * @param key     频道名
         * @param message 消息内容
         * @return 构建器实例
         */
        public Builder publish(String key, String message) {
            return command(PUBLISH, key, message);
        }

        /**
         * 将信息发送到指定的频道。
         * 无法发送 json格式的字符串，因为key 和 message 会以逗号作为分隔符串连为一个新字符串。
         * <p>
         * 在Redis预处理器、后置处理器和取样器中使用时需要注意此问题。
         * <p>
         * 以 {@link Jedis#sendCommand(ProtocolCommand, String...)}  发送 command 和 args(key,message) 时，会以逗号作为分隔符重新创建一个 String[]，
         * json格式的 message 会被分割成多个参数，从而导致错误。
         *
         * @param key     频道名
         * @param message 消息内容
         * @return 构建器实例
         */
        public Builder xadd(String key, String message) {
            return command(XADD, key, "*", message);
        }

        /**
         * 将信息发送到指定的频道。
         *
         * @param key      频道名
         * @param message  消息内容
         * @param messages 更多消息内容
         * @return 构建器实例
         */
        public Builder xadd(String key, String message, String... messages) {
            var args = new String[]{key, "*", message};
            System.arraycopy(args, 0, args, args.length, messages.length);
            return command(XADD, args);
        }

        private Builder command(Protocol.Command command, String... args) {
            configure.command = command.name();
            if (args != null && args.length > 0) {
                configure.args = Collections.addAllIfNonNull(configure.args, Arrays.stream(args).toList());
            }
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