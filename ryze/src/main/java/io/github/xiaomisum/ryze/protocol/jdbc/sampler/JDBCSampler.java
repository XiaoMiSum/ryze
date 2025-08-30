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

package io.github.xiaomisum.ryze.protocol.jdbc.sampler;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.builder.*;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.sampler.AbstractSampler;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.Sampler;
import io.github.xiaomisum.ryze.protocol.jdbc.JDBC;
import io.github.xiaomisum.ryze.protocol.jdbc.JDBCConstantsInterface;
import io.github.xiaomisum.ryze.protocol.jdbc.RealJDBCRequest;
import io.github.xiaomisum.ryze.protocol.jdbc.config.JDBCConfigureItem;

/**
 * JDBC取样器
 * <p>
 * 该类是JDBC协议的核心测试元件，用于执行SQL语句并收集执行结果。
 * 可以作为测试步骤的主要操作，支持完整的测试生命周期管理。
 * </p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>执行SQL语句</li>
 *   <li>记录请求和响应信息</li>
 *   <li>支持前置/后置处理器</li>
 *   <li>支持断言和结果提取</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@KW(value = {"jdbc", "jdbc_sampler"})
public class JDBCSampler extends AbstractSampler<JDBCSampler, JDBCConfigureItem, DefaultSampleResult> implements Sampler<DefaultSampleResult>, JDBCConstantsInterface {

    /**
     * 数据源实例
     * <p>用于获取数据库连接</p>
     */
    @JSONField(serialize = false)
    private DruidDataSource datasource;

    /**
     * 执行结果字节数组
     * <p>存储SQL执行结果</p>
     */
    private byte[] bytes;

    /**
     * 带构建器的构造函数
     *
     * @param builder 构建器实例
     */
    public JDBCSampler(Builder builder) {
        super(builder);
    }

    /**
     * 默认构造函数
     */
    public JDBCSampler() {
        super();
    }

    /**
     * 创建JDBC取样器构建器
     *
     * @return JDBC取样器构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 获取测试结果对象
     *
     * @return 默认采样结果对象
     */
    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.id, runtime.title);
    }

    /**
     * 执行采样操作
     * <p>调用JDBC.execute方法执行SQL语句</p>
     *
     * @param context 测试上下文包装器
     * @param result 采样结果对象
     */
    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        bytes = JDBC.execute(datasource, runtime.getConfig().getSql(), result);
    }

    /**
     * 处理请求信息
     * <p>设置请求数据源和SQL语句信息</p>
     *
     * @param context 测试上下文包装器
     * @param result 采样结果对象
     */
    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        datasource = (DruidDataSource) context.getAllVariablesWrapper().get(runtime.config.getDatasource());
        result.setRequest(new RealJDBCRequest(datasource.getUrl(), datasource.getUsername(), datasource.getPassword(), runtime.config.getSql()));
    }

    /**
     * 处理响应信息
     * <p>设置响应结果</p>
     *
     * @param context 测试上下文包装器
     * @param result 采样结果对象
     */
    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setResponse(SampleResult.DefaultReal.build(bytes));
    }

    /**
     * JDBC 取样器构建器
     * <p>
     * 提供链式调用方式创建JDBC取样器实例。
     * </p>
     */
    public static class Builder extends AbstractSampler.Builder<JDBCSampler, Builder, JDBCConfigureItem,
            JDBCConfigureItem.Builder, DefaultConfigureElementsBuilder, DefaultPreprocessorsBuilder, DefaultPostprocessorsBuilder,
            DefaultAssertionsBuilder, DefaultExtractorsBuilder, DefaultSampleResult> {
        /**
         * 构建JDBC取样器实例
         *
         * @return JDBC取样器实例
         */
        @Override
        public JDBCSampler build() {
            return new JDBCSampler(this);
        }


        /**
         * 获取配置元件构建器
         *
         * @return 默认配置元件构建器
         */
        @Override
        protected DefaultConfigureElementsBuilder getConfiguresBuilder() {
            return DefaultConfigureElementsBuilder.builder();
        }

        /**
         * 获取断言构建器
         *
         * @return 默认断言构建器
         */
        @Override
        protected DefaultAssertionsBuilder getAssertionsBuilder() {
            return DefaultAssertionsBuilder.builder();
        }

        /**
         * 获取提取器构建器
         *
         * @return 默认提取器构建器
         */
        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        /**
         * 获取前置处理器构建器
         *
         * @return 默认前置处理器构建器
         */
        @Override
        protected DefaultPreprocessorsBuilder getPreprocessorsBuilder() {
            return DefaultPreprocessorsBuilder.builder();
        }

        /**
         * 获取后置处理器构建器
         *
         * @return 默认后置处理器构建器
         */
        @Override
        protected DefaultPostprocessorsBuilder getPostprocessorsBuilder() {
            return DefaultPostprocessorsBuilder.builder();
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