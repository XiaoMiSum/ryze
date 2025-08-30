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

package io.github.xiaomisum.ryze.protocol.dubbo;

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;
import io.github.xiaomisum.ryze.protocol.dubbo.config.DubboConfigureItem;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 真实Dubbo请求信息类
 * <p>
 * 该类用于封装和格式化Dubbo服务调用的请求信息，继承自SampleResult.Real。<br>
 * 它包含了Dubbo服务调用的所有关键信息，如注册中心地址、接口名称、方法名称、参数类型、<br>
 * 参数值和附加参数等，并提供格式化输出功能，便于在测试报告中展示。<br>
 * </p>
 * <p>
 * 业务处理逻辑：
 * <ol>
 *   <li>封装Dubbo服务调用的请求信息</li>
 *   <li>提供请求信息的格式化输出功能</li>
 *   <li>支持从字节数组构造实例，便于序列化和反序列化</li>
 *   <li>通过静态工厂方法从配置项构建实例</li>
 * </ol>
 * </p>
 *
 * @author xiaomi
 * @since 6.0.0
 * Created at 2025/7/26 22:24
 */
public class RealDubboRequest extends SampleResult.Real {

    /**
     * 注册中心地址
     * <p>
     * Dubbo服务注册中心的完整地址信息。<br>
     * 包含协议、主机和端口信息。<br>
     * 格式示例：zookeeper://127.0.0.1:2181、nacos://127.0.0.1:8848
     * </p>
     */
    private String address;

    /**
     * 接口名称
     * <p>
     * 要调用的Dubbo服务接口的全限定类名。<br>
     * 例如：com.example.service.DemoService
     * </p>
     */
    private String interfaceName;

    /**
     * 方法名称
     * <p>
     * 要调用的Dubbo服务接口中的具体方法名。<br>
     * 例如：getUserInfo、createOrder等
     * </p>
     */
    private String method;

    /**
     * 参数类型列表
     * <p>
     * 方法参数的类型列表，用于准确匹配重载方法。<br>
     * 类型应为全限定类名，例如：java.lang.String、java.lang.Integer等
     * </p>
     */
    private List<String> parameterTypes;

    /**
     * 附加参数映射
     * <p>
     * 服务调用时的附加参数（attachments）。<br>
     * 这些参数不会传递给服务方法，但可以在服务端通过RpcContext获取。<br>
     * 常用于传递调用链追踪信息、用户身份信息等。
     * </p>
     */
    private Map<String, String> attachmentArgs;

    /**
     * 构造函数
     * <p>
     * 从字节数组创建RealDubboRequest实例。<br>
     * 通常用于从序列化的请求数据恢复实例。<br>
     * 字节数组应包含参数值信息，通过父类SampleResult.Real进行处理。
     * </p>
     *
     * @param bytes 包含参数值信息的字节数组
     */
    public RealDubboRequest(byte[] bytes) {
        super(bytes);
    }

    /**
     * 静态工厂方法：从配置项构建实例
     * <p>
     * 根据Dubbo配置项和注册中心地址创建RealDubboRequest实例。<br>
     * 这是创建RealDubboRequest实例的主要方式，用于在测试执行过程中记录请求信息。<br>
     * 该方法会提取配置项中的关键信息并设置到新创建的实例中。
     * </p>
     *
     * @param config  Dubbo配置项，包含服务调用所需的所有配置信息
     * @param address 注册中心地址
     * @return RealDubboRequest实例
     */
    public static RealDubboRequest build(DubboConfigureItem config, String address) {
        var result = new RealDubboRequest(JSON.toJSONBytes(config.getParameters()));
        result.address = address;
        result.interfaceName = config.getInterfaceName();
        result.method = config.getMethod();
        result.parameterTypes = config.getParameterTypes();
        result.attachmentArgs = config.getAttachmentArgs();
        return result;
    }

    /**
     * 格式化请求信息
     * <p>
     * 将Dubbo服务调用的请求信息格式化为可读的字符串。<br>
     * 包含注册中心地址、接口信息、方法信息、参数类型、参数值和附加参数等。<br>
     * 该方法的输出主要用于测试报告中展示请求详情。<br>
     * 格式示例：<br>
     * registry center address: zookeeper://127.0.0.1:2181<br>
     * interface: com.example.service.DemoService.getUserInfo()<br>
     * ["java.lang.String"]<br>
     * parameters: ["张三"]<br>
     * attachmentArgs: {"traceId":"12345"}
     * </p>
     *
     * @return 格式化后的请求信息字符串
     */
    @Override
    public String format() {
        var buf = new StringBuilder();
        buf.append("registry center address: ").append(address).append("\n")
                .append("interface: ").append(interfaceName).append(".").append(method).append("()");
        if (Objects.nonNull(parameterTypes)) {
            buf.append("\n").append(JSON.toJSONString(parameterTypes));
        }
        if (StringUtils.isNotBlank(bytesAsString())) {
            buf.append("\n").append("parameters: ").append(bytesAsString());
        }
        if (Objects.nonNull(attachmentArgs) && !attachmentArgs.isEmpty()) {
            buf.append("\n").append("attachmentArgs: ").append(JSON.toJSONString(attachmentArgs));
        }
        return buf.toString();
    }
}