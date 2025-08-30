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

import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;

/**
 * Dubbo协议相关常量接口
 * <p>
 * 该接口定义了Dubbo协议测试中使用的所有常量，包括配置项名称、默认值等。<br>
 * 这些常量主要用于在JSON配置文件中标识不同的Dubbo配置项，以及在代码中引用这些配置项。<br>
 * 实现了TestElementConstantsInterface接口，继承了基础测试元素的常量定义。
 * </p>
 * <p>
 * 业务处理逻辑：
 * <ol>
 *   <li>定义Dubbo测试元素中使用的标准常量</li>
 *   <li>提供配置项名称的统一管理</li>
 *   <li>维护向后兼容性，通过@Deprecated标记已过时的配置项</li>
 * </ol>
 * </p>
 * 
 * @author xiaomi
 * @since 6.0.0
 */
public interface DubboConstantsInterface extends TestElementConstantsInterface {

    /**
     * Dubbo配置元素的默认引用名称键值
     * <p>
     * 当用户未指定Dubbo配置的引用名称时，系统将使用此默认值作为键来存储和检索配置。<br>
     * 这确保了即使在没有显式命名的情况下，也能正确识别和使用Dubbo配置元素。
     * </p>
     */
    String DEF_REF_NAME_KEY = "__dubbo_configure_element_default_ref_name__";

    /**
     * 注册中心配置项标识
     * <p>
     * 用于在JSON配置中标识Dubbo注册中心的配置部分。<br>
     * 对应Dubbo配置中的registry元素，包含注册中心的地址、协议、认证信息等配置。
     * </p>
     */
    String REGISTRY = "registry";

    /**
     * 引用配置项标识
     * <p>
     * 用于在JSON配置中标识Dubbo服务引用的配置部分。<br>
     * 对应Dubbo配置中的reference元素，包含服务版本、分组、超时时间、重试次数等配置。
     * </p>
     */
    String REFERENCE = "reference";

    /**
     * 内部引用对象标识
     * <p>
     * 用于标识内部引用对象的配置项。<br>
     * 通常用于存储Dubbo服务引用的实际对象引用，供测试执行时使用。
     * </p>
     */
    String REFERENCE_OBJECT = "inner_reference_object";

    /**
     * 协议配置项标识（已过时）
     * <p>
     * 用于指定注册中心的协议类型，如zookeeper、nacos等。<br>
     * 此配置项已被标记为过时，建议使用ADDRESS配置项替代，因为ADDRESS已包含协议信息。<br>
     * 格式示例：zookeeper、nacos等
     * </p>
     * @deprecated 自6.0.0版本起，建议使用ADDRESS配置项替代
     */
    @Deprecated(since = "6.0.0")
    String PROTOCOL = "protocol";

    /**
     * 分组配置项标识
     * <p>
     * 用于指定Dubbo服务的分组信息。<br>
     * 在Dubbo中，服务可以通过分组进行逻辑隔离，同一服务可以属于不同分组。<br>
     * 格式示例：development、production等
     * </p>
     */
    String GROUP = "group";

    /**
     * 命名空间配置项标识
     * <p>
     * 用于指定注册中心的命名空间。<br>
     * 在一些注册中心（如Nacos）中，可以通过命名空间实现环境隔离。<br>
     * 格式示例：dev、test、prod等
     * </p>
     */
    String NAMESPACE = "namespace";

    /**
     * 应用名称配置项标识
     * <p>
     * 用于指定Dubbo消费者应用的名称。<br>
     * 在Dubbo注册中心中，会显示此应用名称以标识服务消费者。<br>
     * 格式示例：ryze-dubbo-consumer
     * </p>
     */
    String APP_NAME = "app_name";

    /**
     * 用户名配置项标识
     * <p>
     * 用于指定连接注册中心时的认证用户名。<br>
     * 在需要认证的注册中心（如某些Nacos配置）中使用。<br>
     * 格式示例：admin
     * </p>
     */
    String USERNAME = "username";

    /**
     * 密码配置项标识
     * <p>
     * 用于指定连接注册中心时的认证密码。<br>
     * 在需要认证的注册中心（如某些Nacos配置）中使用。<br>
     * 格式示例：123456
     * </p>
     */
    String PASSWORD = "password";

    /**
     * 地址配置项标识
     * <p>
     * 用于指定注册中心的完整地址信息。<br>
     * 包含协议、主机和端口信息，是注册中心连接的核心配置。<br>
     * 格式示例：zookeeper://127.0.0.1:2181、nacos://127.0.0.1:8848
     * </p>
     */
    String ADDRESS = "address";

    /**
     * 超时配置项标识
     * <p>
     * 用于指定Dubbo服务调用的超时时间（毫秒）。<br>
     * 如果在指定时间内未收到服务响应，则认为调用超时。<br>
     * 格式示例：5000（5秒）
     * </p>
     */
    String TIMEOUT = "timeout";

    /**
     * 负载均衡配置项标识
     * <p>
     * 用于指定Dubbo服务调用的负载均衡策略。<br>
     * 支持多种负载均衡算法，如random（随机）、roundrobin（轮询）、leastactive（最少活跃调用）等。<br>
     * 格式示例：random、roundrobin
     * </p>
     */
    String LOAD_BALANCE = "load_balance";

    /**
     * 重试次数配置项标识
     * <p>
     * 用于指定Dubbo服务调用失败时的重试次数。<br>
     * 不包括首次调用，0表示不重试。<br>
     * 格式示例：2（表示最多重试2次，总共调用3次）
     * </p>
     */
    String RETRIES = "retries";

    /**
     * 异步调用配置项标识
     * <p>
     * 用于指定Dubbo服务调用是否采用异步方式。<br>
     * 异步调用可以提高系统吞吐量，适用于不需要立即获取结果的场景。<br>
     * 格式示例：true、false
     * </p>
     */
    String ASYNC = "async";

    /**
     * 接口配置项标识
     * <p>
     * 用于指定要调用的Dubbo服务接口的全限定类名。<br>
     * 这是Dubbo服务调用的核心配置项。<br>
     * 格式示例：com.example.service.DemoService
     * </p>
     */
    String INTERFACE = "interface";

    /**
     * 版本配置项标识
     * <p>
     * 用于指定Dubbo服务的版本号。<br>
     * 在同一个注册中心中，相同接口不同版本的服务可以通过版本号进行区分。<br>
     * 格式示例：1.0.0、2.0.0
     * </p>
     */
    String VERSION = "version";

    /**
     * 方法配置项标识
     * <p>
     * 用于指定要调用的Dubbo服务接口中的具体方法名。<br>
     * 这是Dubbo服务调用的另一个核心配置项。<br>
     * 格式示例：getUserInfo、createOrder
     * </p>
     */
    String METHOD = "method";

    /**
     * 参数类型配置项标识
     * <p>
     * 用于指定Dubbo服务方法参数的类型列表。<br>
     * 在调用重载方法时，需要通过参数类型来准确匹配方法。<br>
     * 格式示例：["java.lang.String", "java.lang.Integer"]
     * </p>
     */
    String ARGS_PARAMETER_TYPES = "parameter_types";

    /**
     * 参数值配置项标识
     * <p>
     * 用于指定Dubbo服务方法调用时的实际参数值列表。<br>
     * 参数值会按照顺序传递给对应的方法参数。<br>
     * 格式示例：["张三", 25]
     * </p>
     */
    String ARGS_PARAMETERS = "parameters";

    /**
     * 附加参数配置项标识
     * <p>
     * 用于指定Dubbo服务调用时的附加参数（attachments）。<br>
     * 这些参数不会传递给服务方法，但可以在服务端通过RpcContext获取。<br>
     * 常用于传递调用链追踪信息、用户身份信息等。<br>
     * 格式示例：{"traceId": "12345", "userId": "user001"}
     * </p>
     */
    String ATTACHMENT_ARGS = "attachment_args";
}