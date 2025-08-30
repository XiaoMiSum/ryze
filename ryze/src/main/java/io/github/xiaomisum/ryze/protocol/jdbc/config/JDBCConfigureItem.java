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

package io.github.xiaomisum.ryze.protocol.jdbc.config;

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import io.github.xiaomisum.ryze.protocol.jdbc.JDBCConstantsInterface;
import org.apache.commons.lang3.StringUtils;

/**
 * JDBC协议配置项类
 * <p>
 * 该类封装了JDBC协议的所有配置参数，包括数据源引用、驱动类名、连接URL、认证信息、
 * 连接池配置和SQL语句等。实现了ConfigureItem接口，支持配置项的合并和变量求值。
 * </p>
 *
 * @author xiaomi
 */
public class JDBCConfigureItem implements ConfigureItem<JDBCConfigureItem>, JDBCConstantsInterface {

    /**
     * 数据源引用名称
     * <p>用于引用已配置的数据源</p>
     */
    @JSONField(name = DATASOURCE)
    protected String datasource;

    /**
     * JDBC驱动类名
     * <p>指定要使用的JDBC驱动类的全限定名</p>
     */
    @JSONField(name = DRIVER, ordinal = 1)
    protected String driver;

    /**
     * 数据库连接URL
     * <p>数据库连接字符串，包含协议、主机、端口和数据库名称等信息</p>
     */
    @JSONField(name = URL, ordinal = 2)
    protected String url;

    /**
     * 数据库用户名
     * <p>用于数据库认证的用户名</p>
     */
    @JSONField(name = USERNAME, ordinal = 3)
    protected String username;

    /**
     * 数据库密码
     * <p>用于数据库认证的密码</p>
     */
    @JSONField(name = PASSWORD, ordinal = 4)
    protected String password;

    /**
     * 最大活跃连接数
     * <p>连接池中允许的最大活跃连接数量，默认值为10</p>
     */
    @JSONField(name = MAX_ACTIVE, ordinal = 5)
    protected int maxActive;

    /**
     * 最大等待时间
     * <p>从连接池获取连接时的最大等待时间(毫秒)，默认值为5000</p>
     */
    @JSONField(name = MAX_WAIT, ordinal = 6)
    protected int maxWait;

    /**
     * SQL语句
     * <p>要执行的SQL语句</p>
     */
    @JSONField(name = SQL, ordinal = 7)
    protected String sql;

    /**
     * 默认构造函数
     */
    public JDBCConfigureItem() {
    }

    /**
     * 创建JDBC配置项构建器
     *
     * @return JDBC配置项构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 合并配置项
     * <p>
     * 将当前配置项与另一个配置项合并，当当前配置项的某个属性为空时，
     * 使用另一个配置项的对应属性值。
     * </p>
     *
     * @param other 要合并的另一个配置项
     * @return 合并后的新配置项
     */
    @Override
    public JDBCConfigureItem merge(JDBCConfigureItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.datasource = StringUtils.isBlank(self.datasource) ? localOther.datasource : self.datasource;
        self.driver = StringUtils.isBlank(self.driver) ? localOther.driver : self.driver;
        self.url = StringUtils.isBlank(self.url) ? localOther.url : self.url;
        self.username = StringUtils.isBlank(self.username) ? localOther.username : self.username;
        self.password = StringUtils.isBlank(self.password) ? localOther.password : self.password;
        self.sql = StringUtils.isBlank(self.sql) ? localOther.sql : self.sql;
        self.maxActive = (self.maxActive = self.maxActive > 0 ? localOther.maxActive : self.maxActive) > 0 ? self.maxActive : 10;
        self.maxWait = (self.maxWait = self.maxWait > 0 ? localOther.maxWait : self.maxWait) > 0 ? self.maxWait : 5000;
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
    public JDBCConfigureItem evaluate(ContextWrapper context) {
        datasource = (String) context.evaluate(datasource);
        driver = (String) context.evaluate(driver);
        url = (String) context.evaluate(url);
        username = (String) context.evaluate(username);
        password = (String) context.evaluate(password);
        sql = (String) context.evaluate(sql);
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
     * 获取JDBC驱动类名
     *
     * @return JDBC驱动类名
     */
    public String getDriver() {
        return driver;
    }

    /**
     * 设置JDBC驱动类名
     *
     * @param driver JDBC驱动类名
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * 获取数据库连接URL
     *
     * @return 数据库连接URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置数据库连接URL
     *
     * @param url 数据库连接URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取数据库用户名
     *
     * @return 数据库用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置数据库用户名
     *
     * @param username 数据库用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取数据库密码
     *
     * @return 数据库密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置数据库密码
     *
     * @param password 数据库密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取最大活跃连接数
     * <p>如果设置的值小于等于1，则返回默认值10</p>
     *
     * @return 最大活跃连接数
     */
    public int getMaxActive() {
        return maxActive > 1 ? maxActive : 10;
    }

    /**
     * 设置最大活跃连接数
     *
     * @param maxActive 最大活跃连接数
     */
    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    /**
     * 获取最大等待时间
     * <p>如果设置的值小于1000，则返回默认值5000</p>
     *
     * @return 最大等待时间(毫秒)
     */
    public int getMaxWait() {
        return maxWait < 1000 ? 5000 : maxWait;
    }

    /**
     * 设置最大等待时间
     *
     * @param maxWait 最大等待时间(毫秒)
     */
    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    /**
     * 获取SQL语句
     *
     * @return SQL语句
     */
    public String getSql() {
        return sql;
    }

    /**
     * 设置SQL语句
     *
     * @param sql SQL语句
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * JDBC 协议配置项构建类
     * <p>
     * 提供链式调用方式创建JDBC配置项实例，支持设置所有JDBC相关配置参数。
     * </p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, JDBCConfigureItem> {

        private JDBCConfigureItem configure = new JDBCConfigureItem();

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
         * 设置JDBC驱动类名
         *
         * @param driver JDBC驱动类名
         * @return 构建器实例
         */
        public Builder driver(String driver) {
            configure.driver = driver;
            return self;
        }

        /**
         * 设置数据库连接URL
         *
         * @param url 数据库连接URL
         * @return 构建器实例
         */
        public Builder url(String url) {
            configure.url = url;
            return self;
        }

        /**
         * 设置数据库用户名
         *
         * @param username 数据库用户名
         * @return 构建器实例
         */
        public Builder username(String username) {
            configure.username = username;
            return self;
        }

        /**
         * 设置数据库密码
         *
         * @param password 数据库密码
         * @return 构建器实例
         */
        public Builder password(String password) {
            configure.password = password;
            return self;
        }

        /**
         * 设置最大活跃连接数
         *
         * @param maxActive 最大活跃连接数
         * @return 构建器实例
         */
        public Builder maxActive(int maxActive) {
            configure.maxActive = maxActive;
            return self;
        }

        /**
         * 设置最大等待时间
         *
         * @param maxWait 最大等待时间(毫秒)
         * @return 构建器实例
         */
        public Builder maxWait(int maxWait) {
            configure.maxWait = maxWait;
            return self;
        }

        /**
         * 设置SQL语句
         *
         * @param sql SQL语句
         * @return 构建器实例
         */
        public Builder sql(String sql) {
            configure.sql = sql;
            return self;
        }

        /**
         * 合并配置项
         *
         * @param config 要合并的配置项
         * @return 构建器实例
         */
        public Builder config(JDBCConfigureItem config) {
            configure = configure.merge(config);
            return self;
        }

        /**
         * 构建JDBC配置项实例
         *
         * @return JDBC配置项实例
         */
        @Override
        public JDBCConfigureItem build() {
            return configure;
        }
    }
}