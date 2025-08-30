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

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.TestSuiteResult;
import io.github.xiaomisum.ryze.testelement.configure.AbstractConfigureElement;
import io.github.xiaomisum.ryze.protocol.jdbc.JDBCConstantsInterface;
import io.github.xiaomisum.ryze.support.Closeable;
import io.github.xiaomisum.ryze.support.ValidateResult;
import org.apache.commons.lang3.StringUtils;

/**
 * JDBC数据源配置元件
 * <p>
 * 该类实现了JDBC数据源的配置和管理功能，基于Druid连接池实现。
 * 负责初始化数据库连接池，并将其注册到测试上下文中供其他元件使用。
 * </p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>配置和初始化Druid连接池</li>
 *   <li>注册数据源到测试上下文</li>
 *   <li>验证数据源配置的有效性</li>
 *   <li>释放连接池资源</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@KW({"jdbc", "jdbc_datasource", "jdbc_data_source"})
public class JDBCDatasource extends AbstractConfigureElement<JDBCDatasource, JDBCConfigureItem, TestSuiteResult>
        implements Closeable, JDBCConstantsInterface {
    /**
     * Druid数据源实例
     * <p>用于管理数据库连接池</p>
     */
    @JSONField(serialize = false)
    private final DruidDataSource dataSource = new DruidDataSource();

    /**
     * 默认构造函数
     */
    public JDBCDatasource() {
    }

    /**
     * 带构建器的构造函数
     *
     * @param builder 构建器实例
     */
    public JDBCDatasource(Builder builder) {
        super(builder);
    }

    /**
     * 创建JDBC数据源构建器
     *
     * @return JDBC数据源构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 验证数据源配置的有效性
     * <p>
     * 检查必需的配置项是否完整，包括：
     * <ul>
     *   <li>数据源引用名称</li>
     *   <li>数据库连接URL</li>
     *   <li>数据库用户名</li>
     *   <li>数据库密码</li>
     * </ul>
     * </p>
     *
     * @return 验证结果
     */
    @Override
    public ValidateResult validate() {
        var result = super.validate();
        if (StringUtils.isBlank(refName)) {
            result.append("\n数据源引用名称 %s 字段值缺失或为空，当前值：%s", REF_NAME, toString());
        }
        if (StringUtils.isBlank(config.url)) {
            result.append("\n数据源连接 %s 字段值缺失或为空，当前值：%s", URL, toString());
        }
        if (StringUtils.isBlank(config.username)) {
            result.append("\n数据源用户名 %s 字段值缺失或为空，当前值：%s", USERNAME, toString());
        }
        if (StringUtils.isBlank(config.password)) {
            result.append("\n数据源密码 %s 字段值缺失或为空，当前值：%s", PASSWORD, toString());
        }
        return result;
    }

    /**
     * 处理数据源配置
     * <p>
     * 初始化Druid连接池，设置连接参数，并将数据源注册到测试上下文的本地变量中。
     * </p>
     *
     * @param context 测试上下文包装器
     */
    @Override
    protected void doProcess(ContextWrapper context) {
        try {
            // 兼容 没有使用 SPI 的 JDBC驱动
            if (StringUtils.isNotBlank(runtime.getConfig().driver)) {
                Class.forName(runtime.getConfig().driver);
            }
        } catch (Exception ignored) {
        }
        dataSource.setUrl(runtime.getConfig().url);
        dataSource.setUsername(runtime.getConfig().username);
        dataSource.setPassword(runtime.getConfig().password);
        // 使用getter 获取数值，因为在这里面做了数值判断
        dataSource.setMaxActive(runtime.getConfig().getMaxActive());
        dataSource.setMaxWait(runtime.getConfig().getMaxWait());
        context.getSessionRunner().getContext().getLocalVariablesWrapper().put(refName, dataSource);
    }

    /**
     * 获取测试结果对象
     *
     * @return 测试套件结果对象
     */
    @Override
    protected TestSuiteResult getTestResult() {
        return new TestSuiteResult("JDBC数据源配置：" + refName);
    }

    /**
     * 关闭数据源
     * <p>释放Druid连接池占用的资源</p>
     */
    @Override
    public void close() {
        dataSource.close();
    }

    /**
     * JDBC数据源 测试元件 构建类
     * <p>
     * 提供链式调用方式创建JDBC数据源实例。
     * </p>
     */
    public static class Builder extends AbstractConfigureElement.Builder<JDBCDatasource, Builder, JDBCConfigureItem, JDBCConfigureItem.Builder, TestSuiteResult> {

        /**
         * 构建JDBC数据源实例
         *
         * @return JDBC数据源实例
         */
        @Override
        public JDBCDatasource build() {
            return new JDBCDatasource(this);
        }

        /**
         * 获取配置项构建器
         *
         * @return JDBC配置项构建器
         */
        @Override
        protected JDBCConfigureItem.Builder getConfigureItemBuilder() {
            return JDBCConfigureItem.builder();
        }
    }
}