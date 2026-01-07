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

package io.github.xiaomisum.ryze.protocol.email.config;

import com.alibaba.fastjson2.annotation.JSONField;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.email.EMailConstantsInterface;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Consumer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * email 配置项类
 * <p>
 * 该类封装了email的所有配置参数，包括协议、主机、端口、路径、方法、
 * 请求头、Cookie、查询参数、请求体等。实现了ConfigureItem接口，
 * 支持配置项的合并和变量求值。
 * </p>
 *
 * @author xiaomi
 * @since 2025/7/19 20:17
 */
public class EMailConfigureItem implements ConfigureItem<EMailConfigureItem>, EMailConstantsInterface {

    /**
     * 邮件发送服务器主机
     * <p>邮件发送服务器主机名 或 IP地址</p>
     */
    @JSONField(name = HOST, ordinal = 2)
    protected String host;

    /**
     * 邮件发送服务器端口
     * <p>邮件发送服务器端口号</p>
     */
    @JSONField(name = PORT, ordinal = 3)
    protected String port;

    @JSONField(name = USE_SSL, ordinal = 4)
    protected Boolean useSSL;

    @JSONField(name = USE_STRATTLS, ordinal = 4)
    protected Boolean useStarttls;

    @JSONField(name = USERNAME, ordinal = 5)
    protected String username;

    @JSONField(name = PASSWORD, ordinal = 6)
    protected String password;

    @JSONField(name = TITLE, ordinal = 9)
    protected String to;

    @JSONField(name = TITLE, ordinal = 7)
    protected String title;

    @JSONField(name = CONTENT, ordinal = 8)
    protected String content;

    /**
     * 默认构造函数
     */
    public EMailConfigureItem() {

    }

    /**
     * 创建email 配置项构建器
     *
     * @return email 配置项构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 合并属性，如果当前对象的属性值为空，则以 other 对象的属性值替换
     *
     * @param other 其他对象
     * @return 合并后的新对象
     */
    @Override
    public EMailConfigureItem merge(EMailConfigureItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.host = StringUtils.isBlank(self.host) ? localOther.host : self.host;
        self.port = StringUtils.isBlank(self.port) ? localOther.port : self.port;
        self.useSSL = Objects.isNull(self.useSSL) ? localOther.useSSL : self.useSSL;
        self.useStarttls = Objects.isNull(self.useStarttls) ? localOther.useStarttls : self.useStarttls;
        self.username = StringUtils.isBlank(self.username) ? localOther.username : self.username;
        self.password = StringUtils.isBlank(self.password) ? localOther.password : self.password;
        self.to = StringUtils.isBlank(self.to) ? localOther.to : self.to;
        self.title = StringUtils.isBlank(self.title) ? localOther.title : self.title;
        self.content = StringUtils.isBlank(self.content) ? localOther.content : self.content;
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
    public EMailConfigureItem evaluate(ContextWrapper context) {
        host = (String) context.evaluate(host);
        port = (String) context.evaluate(port);
        useSSL = (Boolean) context.evaluate(useSSL);
        useStarttls = (Boolean) context.evaluate(useStarttls);
        username = (String) context.evaluate(username);
        password = (String) context.evaluate(password);
        to = (String) context.evaluate(to);
        title = (String) context.evaluate(title);
        content = (String) context.evaluate(content);
        return this;
    }

    /**
     * 获取主机
     *
     * @return 主机
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置主机
     *
     * @param host 主机
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取端口
     *
     * @return 端口
     */
    public String getPort(int def) {
        return StringUtils.isBlank(port) ? String.valueOf(def) : port;
    }

    /**
     * 设置端口
     *
     * @param port 端口
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * 获取是否使用 SSL
     *
     * @return 是否使用 SSL
     */
    public Boolean getUseSSL() {
        return useSSL;
    }

    /**
     * 设置是否使用 SSL
     *
     * @param useSSL 是否使用 SSL
     */
    public void setUseSSL(Boolean useSSL) {
        this.useSSL = useSSL;
    }

    /**
     * 获取是否使用 Starttls
     *
     * @return 是否使用 Starttls
     */
    public Boolean getUseStarttls() {
        return useStarttls;
    }

    /**
     * 设置是否使用 Starttls
     *
     * @param useStarttls 是否使用 Starttls
     */
    public void setUseStarttls(Boolean useStarttls) {
        this.useStarttls = useStarttls;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取收件人
     *
     * @return 收件人
     */
    public String getTo() {
        return to;
    }

    /**
     * 设置收件人
     *
     * @param to 收件人
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * 获取标题
     *
     * @return 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取内容
     *
     * @return 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * email 配置项构建类
     * <p>
     * 提供链式调用方式创建email 配置项实例，支持设置所有HTTP相关配置参数。
     * </p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, EMailConfigureItem> {

        private EMailConfigureItem configure = new EMailConfigureItem();

        /**
         * 默认构造函数
         */
        public Builder() {
        }

        /**
         * 设置主机
         *
         * @param host 主机
         * @return 构建器实例
         */
        public Builder host(String host) {
            configure.host = host;
            return self;
        }

        /**
         * 设置端口
         *
         * @param port 端口
         * @return 构建器实例
         */
        public Builder port(String port) {
            configure.port = port;
            return self;
        }

        /**
         * 设置 启用 Starttls
         *
         * @return 构建器实例
         */
        public Builder useStarttls() {
            configure.useStarttls = Boolean.TRUE;
            return self;
        }

        /**
         * 设置 是否启用 Starttls
         *
         * @param useStarttls 启用 ssl
         * @return 构建器实例
         */
        public Builder useStarttls(Boolean useStarttls) {
            configure.useStarttls = useStarttls;
            return self;
        }

        /**
         * 设置 启用ssl
         *
         * @return 构建器实例
         */
        public Builder useSSL() {
            configure.useSSL = Boolean.TRUE;
            return self;
        }

        /**
         * 设置 是否启用 SSL
         *
         * @param useSSL 启用 ssl
         * @return 构建器实例
         */
        public Builder useSSL(Boolean useSSL) {
            configure.useSSL = useSSL;
            return self;
        }

        /**
         * 设置 用户名
         *
         * @param username 用户名
         * @return 构建器实例
         */
        public Builder username(String username) {
            configure.username = username;
            return self;
        }

        /**
         * 设置 密码
         *
         * @param password 密码
         * @return 构建器实例
         */
        public Builder password(String password) {
            configure.password = password;
            return self;
        }

        /**
         * 添加收件人
         *
         * @param to 收件人
         * @return 构建器实例
         */
        public Builder to(String to) {
            configure.to = to;
            return self;
        }

        /**
         * 添加标题
         *
         * @param title 标题
         * @return 构建器实例
         */
        public Builder title(String title) {
            configure.title = title;
            return self;
        }

        /**
         * 添加内容
         *
         * @param content 内容
         * @return 构建器实例
         */
        public Builder content(String content) {
            configure.content = content;
            return self;
        }

        /**
         * 通过消费者函数配置
         *
         * @param consumer 配置消费者函数
         * @return 构建器实例
         */
        public Builder config(Consumer<Builder> consumer) {
            var builder = EMailConfigureItem.builder();
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
            var builder = EMailConfigureItem.builder();
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
        public Builder config(EMailConfigureItem config) {
            configure = configure.merge(config);
            return self;
        }

        /**
         * 构建email 配置项实例
         *
         * @return email 配置项实例
         */
        @Override
        public EMailConfigureItem build() {
            return configure;
        }
    }
}