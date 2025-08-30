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

package io.github.xiaomisum.ryze.protocol.dubbo.processor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import io.github.xiaomisum.ryze.builder.DefaultExtractorsBuilder;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.KW;
import io.github.xiaomisum.ryze.testelement.processor.AbstractProcessor;
import io.github.xiaomisum.ryze.testelement.processor.Preprocessor;
import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.protocol.dubbo.Dubbo;
import io.github.xiaomisum.ryze.protocol.dubbo.DubboConstantsInterface;
import io.github.xiaomisum.ryze.protocol.dubbo.RealDubboRequest;
import io.github.xiaomisum.ryze.protocol.dubbo.config.DubboConfigureItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.Objects;

/**
 * Dubbo前置处理器实现类
 * <p>
 * 该类用于在测试执行前调用Dubbo服务，通常用于准备测试数据或执行前置条件检查。
 * 它继承自AbstractProcessor并实现了Preprocessor接口，提供了完整的Dubbo服务调用能力。
 * </p>
 * <p>
 * 主要功能包括：
 * 1. 配置Dubbo服务引用（ReferenceConfig）
 * 2. 执行Dubbo服务调用
 * 3. 处理请求和响应数据
 * 4. 支持配置合并和上下文变量处理
 * </p>
 * <p>
 * 在测试流程中，DubboPreprocessor会在测试用例主体执行之前运行，
 * 可以用于准备测试环境、初始化数据或执行前置验证。
 * </p>
 *
 * @author mi.xiao
 * @since 2021/4/13 20:08
 */
@KW({"dubbo_preprocessor", "dubbo_pre_processor", "dubbo"})
public class DubboPreprocessor extends AbstractProcessor<DubboPreprocessor, DubboConfigureItem, DefaultSampleResult> implements Preprocessor, DubboConstantsInterface {


    /**
     * Dubbo服务引用配置对象，用于创建和配置Dubbo服务的引用
     * 不参与JSON序列化
     */
    @JSONField(serialize = false)
    private ReferenceConfig<GenericService> request;

    /**
     * Dubbo服务调用的响应结果
     * 不参与JSON序列化
     */
    @JSONField(serialize = false)
    private Object response;

    /**
     * 默认构造函数，创建一个空的Dubbo前置处理器实例
     */
    public DubboPreprocessor() {
        super();
    }

    /**
     * 带构建器的构造函数，使用指定的构建器创建Dubbo前置处理器实例
     *
     * @param builder Dubbo前置处理器构建器
     */
    public DubboPreprocessor(Builder builder) {
        super(builder);
    }

    /**
     * 创建Dubbo前置处理器构建器实例
     *
     * @return Dubbo前置处理器构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 获取测试结果对象，用于记录处理器执行的结果信息
     *
     * @return DefaultSampleResult测试结果对象
     */
    @Override
    protected DefaultSampleResult getTestResult() {
        return new DefaultSampleResult(runtime.getId(), StringUtils.isBlank(runtime.getTitle()) ? "Dubbo 前置处理器" : runtime.getTitle());
    }

    /**
     * 执行Dubbo服务调用的核心方法
     * <p>
     * 该方法通过Dubbo工具类执行实际的服务调用，记录执行开始和结束时间，
     * 并将结果存储在response字段中。
     * </p>
     *
     * @param context 测试上下文包装器，提供变量替换和上下文信息
     * @param result  测试结果对象，用于记录执行时间和结果信息
     */
    @Override
    protected void sample(ContextWrapper context, DefaultSampleResult result) {
        response = Dubbo.execute(request, runtime.getConfig(), result);
    }

    /**
     * 处理Dubbo请求配置
     * <p>
     * 该方法在服务调用前执行，主要完成以下任务：
     * 1. 合并本地配置和上下文中的配置信息
     * 2. 创建Dubbo服务引用配置对象
     * 3. 构建请求信息并设置到测试结果中
     * </p>
     *
     * @param context 测试上下文包装器，提供变量替换和上下文信息
     * @param result  测试结果对象，用于记录请求信息
     */
    @Override
    protected void handleRequest(ContextWrapper context, DefaultSampleResult result) {
        super.handleRequest(context, result);
        // 1. 合并配置项
        var localConfig = Objects.isNull(runtime.getConfig()) ? new DubboConfigureItem() : runtime.getConfig();
        var ref = StringUtils.isBlank(localConfig.getRef()) ? DEF_REF_NAME_KEY : localConfig.getRef();
        var otherConfig = (DubboConfigureItem) context.getLocalVariablesWrapper().get(ref);
        runtime.setConfig(localConfig.merge(otherConfig));
        // 2. 创建Dubbo对象
        request = Dubbo.handleRequest(runtime.getConfig());
        result.setRequest(RealDubboRequest.build(runtime.getConfig(), request.getRegistry().getAddress()));
    }

    /**
     * 处理Dubbo响应结果
     * <p>
     * 该方法在服务调用后执行，主要完成以下任务：
     * 1. 设置请求信息到测试结果中
     * 2. 将响应结果转换为字节数组并设置到测试结果中
     * </p>
     *
     * @param context 测试上下文包装器，提供变量替换和上下文信息
     * @param result  测试结果对象，用于记录响应信息
     */
    @Override
    protected void handleResponse(ContextWrapper context, DefaultSampleResult result) {
        super.handleResponse(context, result);
        result.setRequest(RealDubboRequest.build(runtime.getConfig(), request.getRegistry().getAddress()));
        result.setResponse(SampleResult.DefaultReal.build(JSON.toJSONBytes(response)));
    }

    /**
     * Dubbo前置处理器构建器类
     * <p>
     * 提供构建Dubbo前置处理器实例的方法，继承自AbstractProcessor的预处理器构建器。
     * </p>
     */
    public static class Builder extends PreprocessorBuilder<DubboPreprocessor, Builder, DubboConfigureItem,
            DubboConfigureItem.Builder, DefaultExtractorsBuilder, DefaultSampleResult> {
        /**
         * 构建Dubbo前置处理器实例
         *
         * @return Dubbo前置处理器实例
         */
        @Override
        public DubboPreprocessor build() {
            return new DubboPreprocessor(this);
        }

        /**
         * 获取提取器构建器实例
         *
         * @return 默认提取器构建器
         */
        @Override
        protected DefaultExtractorsBuilder getExtractorsBuilder() {
            return DefaultExtractorsBuilder.builder();
        }

        /**
         * 获取Dubbo配置项构建器实例
         *
         * @return Dubbo配置项构建器
         */
        @Override
        protected DubboConfigureItem.Builder getConfigureItemBuilder() {
            return DubboConfigureItem.builder();
        }
    }
}