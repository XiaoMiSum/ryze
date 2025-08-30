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

package io.github.xiaomisum.ryze.protocol.jdbc.processor;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Preprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.protocol.jdbc.JDBC;
import io.github.xiaomisum.ryze.protocol.jdbc.JDBCConstantsInterface;
import io.github.xiaomisum.ryze.protocol.jdbc.RealJDBCRequest;
import io.github.xiaomisum.ryze.protocol.jdbc.config.JDBCConfigureItem;
import org.apache.commons.lang3.StringUtils;

/**
 * JDBC前置处理器
 * <p>
 * 该类在测试步骤执行前运行，用于执行JDBC SQL语句。通常用于准备测试数据、
 * 初始化环境或执行其他预处理操作。
 * </p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>执行SQL语句</li>
 *   <li>记录请求和响应信息</li>
 *   <li>支持结果提取</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
@KW(value = {"jdbc_preprocessor", "jdbc_pre_processor", "jdbc"})
@SuppressWarnings({"unchecked", "rawtypes"})
public class JDBCPreprocessor extends AbstractProcessor<JDBCPreprocessor, JDBCConfigureItem, DefaultSampleResult> implements Preprocessor, JDBCConstantsInterface {

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
    @JSONField(serialize = false)
    private byte[] bytes;

    /**
     * 带构建器的构造函数
     *
     * @param builder 构建器实例
     */
    public JDBCPreprocessor(Builder builder) {
        super(builder);
    }

    /**
     * 默认构造函数
     */
    public JDBCPreprocessor() {
        super();
    }

    /**
     * 创建JDBC前置处理器构建器
     *
     * @return JDBC前置处理器构建器实例
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
        return new DefaultSampleResult(runtime.getId(), StringUtils.isBlank(runtime.getTitle()) ? "JDBC 前置处理器" : runtime.getTitle());

    }

    /**
     * 执行采样操作
     * <p>调用JDBC.execute方法执行SQL语句</p>
     *
     * @param context 测试上下文包装器
     * @param result  采样结果对象
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
     * @param result  采样结果对象
     */
    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        datasource = (DruidDataSource) context.getLocalVariablesWrapper().get(runtime.getConfig().getDatasource());
        result.setRequest(new RealJDBCRequest(datasource.getUrl(), datasource.getUsername(), datasource.getPassword(), runtime.getConfig().getSql()));
    }

    /**
     * 处理响应信息
     * <p>设置响应结果</p>
     *
     * @param context 测试上下文包装器
     * @param result  采样结果对象
     */
    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setResponse(SampleResult.DefaultReal.build(bytes));
    }

    /**
     * JDBC 前置处理器构建器
     * <p>
     * 提供链式调用方式创建JDBC前置处理器实例。
     * </p>
     */
    public static class Builder extends PreprocessorBuilder<JDBCPreprocessor, Builder,
            JDBCConfigureItem, JDBCConfigureItem.Builder, DefaultExtractorsBuilder, DefaultSampleResult> {
        /**
         * 构建JDBC前置处理器实例
         *
         * @return JDBC前置处理器实例
         */
        @Override
        public JDBCPreprocessor build() {
            return new JDBCPreprocessor(this);
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