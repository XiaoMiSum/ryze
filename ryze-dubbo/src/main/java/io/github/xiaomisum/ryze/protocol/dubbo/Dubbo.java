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

package io.github.xiaomisum.ryze.protocol.dubbo;

import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.protocol.dubbo.config.DubboConfigureItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.Objects;

/**
 * Dubbo协议执行核心类
 * <p>
 * 该类负责处理Dubbo服务调用的核心逻辑，包括创建服务引用配置、执行服务调用等。<br>
 * 作为Dubbo协议测试的核心组件，它将配置信息转换为Dubbo框架所需的配置对象，<br>
 * 并执行实际的服务调用，收集调用结果和性能数据。<br>
 * </p>
 * <p>
 * 业务处理逻辑：
 * <ol>
 *   <li>根据测试配置创建Dubbo服务引用配置</li>
 *   <li>设置注册中心、应用、服务引用等相关配置</li>
 *   <li>执行服务调用，处理附加参数和调用参数</li>
 *   <li>收集调用结果和性能数据</li>
 * </ol>
 * </p>
 *
 * @author mi.xiao
 * @since 1.0.0
 */
public class Dubbo {

    /**
     * 处理Dubbo请求配置
     * <p>
     * 根据提供的Dubbo配置项创建Dubbo服务引用配置。<br>
     * 该方法将测试框架的配置对象转换为Dubbo框架所需的配置对象，<br>
     * 包括设置注册中心、应用、服务引用等相关配置。<br>
     * 这是执行Dubbo服务调用的第一步，为后续的服务调用做好准备。
     * </p>
     *
     * @param config Dubbo配置项，包含服务调用所需的所有配置信息
     * @return Dubbo服务引用配置，用于执行实际的服务调用
     */
    public static ReferenceConfig<GenericService> handleRequest(DubboConfigureItem config) {
        var registryConfig = config.getRegistry();
        var referenceConfig = config.getReference();
        var request = new ReferenceConfig<GenericService>();
        // 设置泛化调用
        request.setGeneric("true");
        // 设置应用配置
        request.getApplicationModel().getApplicationConfigManager().setApplication(new ApplicationConfig("ryze-dubbo-consumer"));
        // 设置服务引用配置
        request.setVersion(referenceConfig.getVersion());
        request.setGroup(referenceConfig.getGroup());
        request.setRetries(referenceConfig.getRetries());
        request.setTimeout(referenceConfig.getTimeout());
        request.setAsync(referenceConfig.getAsync());
        request.setLoadbalance(referenceConfig.getLoadBalance());
        request.setInterface(config.getInterfaceName());
        // 设置注册中心配置
        var registry = new RegistryConfig();
        // 如果配置了协议，则构建完整的注册中心地址，否则直接使用地址
        registry.setAddress(StringUtils.isBlank(registryConfig.getProtocol()) ? registryConfig.getAddress()
                : registryConfig.getProtocol() + "://" + registryConfig.getAddress());
        registry.setGroup(registryConfig.getGroup());
        registry.setUsername(registryConfig.getUsername());
        registry.setPassword(registryConfig.getPassword());
        registry.setVersion(registryConfig.getVersion());
        registry.setTimeout(registry.getTimeout());
        request.setRegistry(registry);
        return request;
    }

    /**
     * 执行Dubbo服务调用
     * <p>
     * 执行实际的Dubbo服务调用，处理附加参数和调用参数。<br>
     * 在调用过程中收集性能数据，包括调用开始时间和结束时间。<br>
     * 这是Dubbo服务调用的核心方法，返回服务调用的结果。<br>
     * </p>
     *
     * @param request Dubbo服务引用配置，用于执行服务调用
     * @param config  Dubbo配置项，包含服务调用所需的所有配置信息
     * @param result  测试结果对象，用于收集性能数据
     * @return 服务调用结果，可能为null
     */
    public static Object execute(ReferenceConfig<GenericService> request, DubboConfigureItem config, DefaultSampleResult result) {
        try {
            // 如果配置了附加参数，则设置到RpcContext中
            if (Objects.nonNull(config.getAttachmentArgs()) && !config.getAttachmentArgs().isEmpty()) {
                RpcContext.getClientAttachment().setAttachments(config.getAttachmentArgs());
            }
            // 准备参数类型数组和参数值数组
            var parameterTypes = Objects.isNull(config.getParameterTypes()) ? new String[0] : config.getParameterTypes().toArray(String[]::new);
            var parameters = Objects.isNull(config.getParameters()) ? new Object[0] : config.getParameters().toArray();
            // 标记调用开始时间
            result.sampleStart();
            // 执行服务调用并返回结果
            return request.get().$invoke(config.getMethod(), parameterTypes, parameters);
        } finally {
            // 标记调用结束时间，确保即使出现异常也能正确记录结束时间
            result.sampleEnd();
        }
    }
}