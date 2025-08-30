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

package io.github.xiaomisum.ryze.protocol.dubbo.config;

import com.alibaba.fastjson2.annotation.JSONField;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import io.github.xiaomisum.ryze.protocol.dubbo.DubboConstantsInterface;
import io.github.xiaomisum.ryze.support.Collections;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * Dubbo配置项实体类
 * <p>
 * 该类用于封装Dubbo协议测试所需的所有配置信息，包括注册中心配置、服务引用配置、接口信息、方法信息、参数信息等。<br>
 * 实现了ConfigureItem接口，支持配置项的合并和上下文求值功能。<br>
 * 通过JSONField注解，支持与JSON配置文件的序列化和反序列化映射。<br>
 * </p>
 * <p>
 * 业务处理逻辑：
 * <ol>
 *   <li>封装Dubbo服务调用所需的完整配置信息</li>
 *   <li>支持配置项的动态合并，实现配置继承和覆盖机制</li>
 *   <li>支持上下文变量求值，实现参数的动态替换</li>
 *   <li>提供参数类型自动推导功能，简化配置</li>
 *   <li>通过内部Builder类提供链式调用的配置构建方式</li>
 * </ol>
 * </p>
 *
 * @author xiaomi
 * @since 6.0.0
 * Created at 2025/7/26 21:03
 */
@SuppressWarnings("unchecked")
public class DubboConfigureItem implements ConfigureItem<DubboConfigureItem>, DubboConstantsInterface {

    /**
     * 注册中心配置
     * <p>
     * 包含注册中心的地址、协议、认证信息等配置。<br>
     * 对应Dubbo配置中的&lt;registry&gt;元素。
     * </p>
     */
    @JSONField(name = REGISTRY)
    protected Registry registry;

    /**
     * 服务引用配置
     * <p>
     * 包含服务引用的版本、分组、超时时间等配置。<br>
     * 对应Dubbo配置中的&lt;reference&gt;元素。
     * </p>
     */
    @JSONField(name = REFERENCE, ordinal = 1)
    protected Reference reference;

    /**
     * 接口名称
     * <p>
     * 要调用的Dubbo服务接口的全限定类名。<br>
     * 例如：com.example.service.DemoService
     * </p>
     */
    @JSONField(name = INTERFACE, ordinal = 2)
    protected String interfaceName;

    /**
     * 方法名称
     * <p>
     * 要调用的Dubbo服务接口中的具体方法名。<br>
     * 例如：getUserInfo、createOrder等
     * </p>
     */
    @JSONField(name = METHOD, ordinal = 3)
    protected String method;

    /**
     * 参数类型列表
     * <p>
     * 方法参数的类型列表，用于准确匹配重载方法。<br>
     * 类型应为全限定类名，例如：java.lang.String、java.lang.Integer等
     * </p>
     */
    @JSONField(name = ARGS_PARAMETER_TYPES, ordinal = 4)
    protected List<String> parameterTypes;

    /**
     * 参数值列表
     * <p>
     * 方法调用时的实际参数值列表。<br>
     * 参数值会按照顺序传递给对应的方法参数。
     * </p>
     */
    @JSONField(name = ARGS_PARAMETERS, ordinal = 5)
    protected List<Object> parameters;

    /**
     * 附加参数映射
     * <p>
     * 服务调用时的附加参数（attachments）。<br>
     * 这些参数不会传递给服务方法，但可以在服务端通过RpcContext获取。<br>
     * 常用于传递调用链追踪信息、用户身份信息等。
     * </p>
     */
    @JSONField(name = ATTACHMENT_ARGS, ordinal = 6)
    protected Map<String, String> attachmentArgs;

    /**
     * 配置引用名称
     * <p>
     * 用于标识该配置项的引用名称，便于在测试用例中引用。<br>
     * 如果未指定，则使用默认引用名称{@link DubboConstantsInterface#DEF_REF_NAME_KEY}。
     * </p>
     */
    @JSONField(name = REF)
    private String ref;

    /**
     * 默认构造函数
     * <p>
     * 创建一个空的Dubbo配置项实例。<br>
     * 各配置项字段默认为null，需要通过setter方法或Builder进行设置。
     * </p>
     */
    public DubboConfigureItem() {
    }

    /**
     * 创建配置项构建器
     * <p>
     * 提供一个静态方法用于创建Dubbo配置项的构建器实例。<br>
     * 通过构建器可以使用链式调用的方式构建复杂的Dubbo配置项。
     * </p>
     *
     * @return Dubbo配置项构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 合并配置项
     * <p>
     * 将当前配置项与另一个配置项进行合并。<br>
     * 合并规则：如果当前配置项中的某个字段为null或空，则使用另一个配置项中的对应字段值。<br>
     * 该方法用于实现配置继承和覆盖机制，支持测试用例级别的配置继承。
     * </p>
     *
     * @param other 要合并的另一个配置项，可以为null
     * @return 合并后的新配置项实例
     */
    @Override
    public DubboConfigureItem merge(DubboConfigureItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.registry = Objects.isNull(self.registry) ? localOther.registry : self.registry;
        self.reference = Objects.isNull(self.reference) ? localOther.reference : self.reference;
        self.interfaceName = StringUtils.isBlank(self.interfaceName) ? localOther.interfaceName : self.interfaceName;
        self.method = StringUtils.isBlank(self.method) ? localOther.method : self.method;
        self.parameterTypes = Objects.isNull(self.parameterTypes) ? localOther.parameterTypes : self.parameterTypes;
        self.parameters = Objects.isNull(self.parameters) ? localOther.parameters : self.parameters;
        self.attachmentArgs = Objects.isNull(self.attachmentArgs) ? localOther.attachmentArgs : self.attachmentArgs;
        return self;
    }

    /**
     * 上下文求值
     * <p>
     * 对配置项中的各个字段进行上下文变量求值。<br>
     * 将包含变量表达式（如${variable}）的字段替换为实际值。<br>
     * 该方法用于实现参数的动态替换，支持测试用例的动态配置。
     * </p>
     *
     * @param context 上下文包装器，包含变量信息
     * @return 求值后的配置项实例（当前实例）
     */
    @Override
    public DubboConfigureItem evaluate(ContextWrapper context) {
        registry = (Registry) context.evaluate(registry);
        reference = (Reference) context.evaluate(reference);
        interfaceName = (String) context.evaluate(interfaceName);
        method = (String) context.evaluate(method);
        parameterTypes = (List<String>) context.evaluate(parameterTypes);
        parameters = (List<Object>) context.evaluate(parameters);
        attachmentArgs = (Map<String, String>) context.evaluate(attachmentArgs);
        return this;
    }

    /**
     * 获取注册中心配置
     *
     * @return 注册中心配置实例，可能为null
     */
    public Registry getRegistry() {
        return registry;
    }

    /**
     * 设置注册中心配置
     *
     * @param registry 注册中心配置实例
     */
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    /**
     * 获取服务引用配置
     *
     * @return 服务引用配置实例，可能为null
     */
    public Reference getReference() {
        return reference;
    }

    /**
     * 设置服务引用配置
     *
     * @param reference 服务引用配置实例
     */
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    /**
     * 获取接口名称
     *
     * @return 接口名称，可能为null或空字符串
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * 设置接口名称
     *
     * @param interfaceName 接口名称
     */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     * 获取方法名称
     *
     * @return 方法名称，可能为null或空字符串
     */
    public String getMethod() {
        return method;
    }

    /**
     * 设置方法名称
     *
     * @param method 方法名称
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 获取参数类型列表
     * <p>
     * 如果参数类型列表为null但参数值列表不为null，则自动根据参数值推导参数类型。<br>
     * 这简化了配置，用户只需提供参数值，系统会自动推导参数类型。
     * </p>
     *
     * @return 参数类型列表，可能为null
     */
    public List<String> getParameterTypes() {
        if (Objects.isNull(parameterTypes) && Objects.nonNull(parameters)) {
            parameterTypes = Collections.newArrayList();
            for (var parameter : parameters) {
                parameterTypes.add(parameter.getClass().getName());
            }
        }
        return parameterTypes;
    }

    /**
     * 设置参数类型列表
     *
     * @param parameterTypes 参数类型列表
     */
    public void setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * 获取参数值列表
     *
     * @return 参数值列表，可能为null
     */
    public List<Object> getParameters() {
        return parameters;
    }

    /**
     * 设置参数值列表
     *
     * @param parameters 参数值列表
     */
    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * 获取附加参数映射
     *
     * @return 附加参数映射，可能为null
     */
    public Map<String, String> getAttachmentArgs() {
        return attachmentArgs;
    }

    /**
     * 设置附加参数映射
     *
     * @param attachmentArgs 附加参数映射
     */
    public void setAttachmentArgs(Map<String, String> attachmentArgs) {
        this.attachmentArgs = attachmentArgs;
    }

    /**
     * 获取配置引用名称
     *
     * @return 配置引用名称，可能为null或空字符串
     */
    public String getRef() {
        return ref;
    }

    /**
     * 设置配置引用名称
     *
     * @param ref 配置引用名称
     */
    public void setRef(String ref) {
        this.ref = ref;
    }


    /**
     * 注册中心配置内部类
     * <p>
     * 用于封装Dubbo注册中心的配置信息，包括地址、协议、认证信息等。<br>
     * 对应Dubbo配置中的&lt;registry&gt;元素。<br>
     * 实现了ConfigureItem接口，支持配置项的合并和上下文求值功能。
     * </p>
     */
    public static class Registry implements ConfigureItem<Registry> {

        /**
         * 协议配置（已过时）
         * <p>
         * 用于指定注册中心的协议类型。<br>
         * 此配置项已被标记为过时，建议使用address配置项替代，因为address已包含协议信息。<br>
         * 格式示例：zookeeper、nacos等
         * </p>
         */
        @JSONField(name = PROTOCOL)
        protected String protocol;

        /**
         * 地址配置
         * <p>
         * 用于指定注册中心的完整地址信息。<br>
         * 包含协议、主机和端口信息，是注册中心连接的核心配置。<br>
         * 格式示例：zookeeper://127.0.0.1:2181、nacos://127.0.0.1:8848
         * </p>
         */
        @JSONField(name = ADDRESS, ordinal = 1)
        protected String address;

        /**
         * 用户名配置
         * <p>
         * 用于指定连接注册中心时的认证用户名。<br>
         * 在需要认证的注册中心（如某些Nacos配置）中使用。<br>
         * 格式示例：admin
         * </p>
         */
        @JSONField(name = USERNAME, ordinal = 2)
        protected String username;

        /**
         * 密码配置
         * <p>
         * 用于指定连接注册中心时的认证密码。<br>
         * 在需要认证的注册中心（如某些Nacos配置）中使用。<br>
         * 格式示例：123456
         * </p>
         */
        @JSONField(name = PASSWORD, ordinal = 3)
        protected String password;

        /**
         * 分组配置
         * <p>
         * 用于指定注册中心的分组信息。<br>
         * 在一些注册中心（如Nacos）中，可以通过分组实现逻辑隔离。<br>
         * 格式示例：DEFAULT_GROUP、DEVELOPMENT_GROUP等
         * </p>
         */
        @JSONField(name = GROUP, ordinal = 4)
        protected String group;

        /**
         * 版本配置
         * <p>
         * 用于指定注册中心的版本信息。<br>
         * 在一些注册中心中，可以通过版本实现配置的版本管理。<br>
         * 格式示例：1.0.0、2.0.0等
         * </p>
         */
        @JSONField(name = VERSION, ordinal = 5)
        protected String version;

        /**
         * 默认构造函数
         * <p>
         * 创建一个空的注册中心配置实例。<br>
         * 各配置项字段默认为null，需要通过setter方法或Builder进行设置。
         * </p>
         */
        public Registry() {
        }

        /**
         * 创建注册中心配置构建器
         * <p>
         * 提供一个静态方法用于创建注册中心配置的构建器实例。<br>
         * 通过构建器可以使用链式调用的方式构建注册中心配置。
         * </p>
         *
         * @return 注册中心配置构建器实例
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * 合并注册中心配置
         * <p>
         * 将当前注册中心配置与另一个注册中心配置进行合并。<br>
         * 合并规则：如果当前配置中的某个字段为null或空，则使用另一个配置中的对应字段值。<br>
         * 该方法用于实现注册中心配置的继承和覆盖机制。
         * </p>
         *
         * @param other 要合并的另一个注册中心配置，可以为null
         * @return 合并后的新注册中心配置实例
         */
        @Override
        public Registry merge(Registry other) {
            if (other == null) {
                return copy();
            }
            var localOther = other.copy();
            var self = copy();
            self.protocol = StringUtils.isBlank(self.protocol) ? localOther.protocol : self.protocol;
            self.address = StringUtils.isBlank(self.address) ? localOther.address : self.address;
            self.username = StringUtils.isBlank(self.username) ? localOther.username : self.username;
            self.password = StringUtils.isBlank(self.password) ? localOther.password : self.password;
            self.group = StringUtils.isBlank(self.group) ? localOther.group : self.group;
            self.version = StringUtils.isBlank(self.version) ? localOther.version : self.version;
            return self;
        }

        /**
         * 上下文求值
         * <p>
         * 对注册中心配置中的各个字段进行上下文变量求值。<br>
         * 将包含变量表达式（如${variable}）的字段替换为实际值。<br>
         * 该方法用于实现注册中心配置参数的动态替换。
         * </p>
         *
         * @param context 上下文包装器，包含变量信息
         * @return 求值后的注册中心配置实例（当前实例）
         */
        @Override
        public Registry evaluate(ContextWrapper context) {
            protocol = (String) context.evaluate(protocol);
            address = (String) context.evaluate(address);
            username = (String) context.evaluate(username);
            password = (String) context.evaluate(password);
            group = (String) context.evaluate(group);
            version = (String) context.evaluate(version);
            return this;
        }

        /**
         * 获取协议配置（小写）
         * <p>
         * 获取协议配置并转换为小写格式。<br>
         * 如果协议配置为空，则返回空字符串。<br>
         * 该方法确保协议配置的一致性，避免大小写敏感问题。
         * </p>
         *
         * @return 协议配置（小写），可能为空字符串
         */
        public String getProtocol() {
            return StringUtils.isNotBlank(protocol) ? protocol.toLowerCase(Locale.ROOT) : "";
        }

        /**
         * 设置协议配置
         *
         * @param protocol 协议配置
         */
        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        /**
         * 获取地址配置
         *
         * @return 地址配置，可能为null
         */
        public String getAddress() {
            return address;
        }

        /**
         * 设置地址配置
         *
         * @param address 地址配置
         */
        public void setAddress(String address) {
            this.address = address;
        }

        /**
         * 获取用户名配置
         *
         * @return 用户名配置，可能为null
         */
        public String getUsername() {
            return username;
        }

        /**
         * 设置用户名配置
         *
         * @param username 用户名配置
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * 获取密码配置
         *
         * @return 密码配置，可能为null
         */
        public String getPassword() {
            return password;
        }

        /**
         * 设置密码配置
         *
         * @param password 密码配置
         */
        public void setPassword(String password) {
            this.password = password;
        }

        /**
         * 获取分组配置
         *
         * @return 分组配置，可能为null
         */
        public String getGroup() {
            return group;
        }

        /**
         * 设置分组配置
         *
         * @param group 分组配置
         */
        public void setGroup(String group) {
            this.group = group;
        }

        /**
         * 获取版本配置
         *
         * @return 版本配置，可能为null
         */
        public String getVersion() {
            return version;
        }

        /**
         * 设置版本配置
         *
         * @param version 版本配置
         */
        public void setVersion(String version) {
            this.version = version;
        }

        /**
         * Dubbo 注册中心配置属性构建器
         * <p>
         * 用于通过链式调用的方式构建注册中心配置。<br>
         * 继承自AbstractTestElement.ConfigureBuilder，提供标准的构建器功能。<br>
         * 支持Groovy闭包和Java函数式接口两种配置方式。
         * </p>
         */
        public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, Registry> {

            private Registry registry = new Registry();

            /**
             * 设置协议配置（已过时）
             * <p>
             * 设置注册中心的协议类型。<br>
             * 此方法已被标记为过时，建议使用address方法替代。<br>
             * 格式示例：zookeeper、nacos等
             * </p>
             *
             * @param protocol 协议配置
             * @return 构建器实例，支持链式调用
             * @deprecated 自6.0.0版本起，建议使用address方法替代
             */
            @Deprecated(since = "6.0.0")
            public Builder protocol(String protocol) {
                registry.protocol = protocol;
                return self;
            }

            /**
             * 设置地址配置
             * <p>
             * 设置注册中心的完整地址信息。<br>
             * 包含协议、主机和端口信息，是注册中心连接的核心配置。<br>
             * 格式示例：zookeeper://127.0.0.1:2181、nacos://127.0.0.1:8848
             * </p>
             *
             * @param address 地址配置
             * @return 构建器实例，支持链式调用
             */
            public Builder address(String address) {
                registry.address = address;
                return self;
            }

            /**
             * 设置用户名配置
             * <p>
             * 设置连接注册中心时的认证用户名。<br>
             * 在需要认证的注册中心（如某些Nacos配置）中使用。<br>
             * 格式示例：admin
             * </p>
             *
             * @param username 用户名配置
             * @return 构建器实例，支持链式调用
             */
            public Builder username(String username) {
                registry.username = username;
                return self;
            }

            /**
             * 设置密码配置
             * <p>
             * 设置连接注册中心时的认证密码。<br>
             * 在需要认证的注册中心（如某些Nacos配置）中使用。<br>
             * 格式示例：123456
             * </p>
             *
             * @param password 密码配置
             * @return 构建器实例，支持链式调用
             */
            public Builder password(String password) {
                registry.password = password;
                return self;
            }

            /**
             * 设置分组配置
             * <p>
             * 设置注册中心的分组信息。<br>
             * 在一些注册中心（如Nacos）中，可以通过分组实现逻辑隔离。<br>
             * 格式示例：DEFAULT_GROUP、DEVELOPMENT_GROUP等
             * </p>
             *
             * @param group 分组配置
             * @return 构建器实例，支持链式调用
             */
            public Builder group(String group) {
                registry.group = group;
                return self;
            }

            /**
             * 设置版本配置
             * <p>
             * 设置注册中心的版本信息。<br>
             * 在一些注册中心中，可以通过版本实现配置的版本管理。<br>
             * 格式示例：1.0.0、2.0.0等
             * </p>
             *
             * @param version 版本配置
             * @return 构建器实例，支持链式调用
             */
            public Builder version(String version) {
                registry.version = version;
                return self;
            }

            /**
             * 合并注册中心配置
             * <p>
             * 将指定的注册中心配置与当前配置进行合并。<br>
             * 合并规则：如果当前配置中的某个字段为null或空，则使用指定配置中的对应字段值。<br>
             * 该方法用于实现注册中心配置的继承和覆盖机制。
             * </p>
             *
             * @param registry 要合并的注册中心配置
             * @return 构建器实例，支持链式调用
             */
            public Builder config(Registry registry) {
                this.registry = this.registry.merge(registry);
                return self;
            }

            /**
             * 构建注册中心配置实例
             * <p>
             * 根据构建器中的配置信息创建注册中心配置实例。<br>
             * 该方法是构建器模式的最后一步，返回构建完成的配置实例。
             * </p>
             *
             * @return 注册中心配置实例
             */
            @Override
            public Registry build() {
                return registry;
            }
        }
    }

    /**
     * 服务引用配置内部类
     * <p>
     * 用于封装Dubbo服务引用的配置信息，包括版本、分组、超时时间等。<br>
     * 对应Dubbo配置中的&lt;reference&gt;元素。<br>
     * 实现了ConfigureItem接口，支持配置项的合并和上下文求值功能。
     * </p>
     */
    public static class Reference implements ConfigureItem<Reference> {

        /**
         * 版本配置
         * <p>
         * 用于指定Dubbo服务的版本号。<br>
         * 在同一个注册中心中，相同接口不同版本的服务可以通过版本号进行区分。<br>
         * 格式示例：1.0.0、2.0.0
         * </p>
         */
        @JSONField(name = VERSION)
        protected String version;

        /**
         * 分组配置
         * <p>
         * 用于指定Dubbo服务的分组信息。<br>
         * 在Dubbo中，服务可以通过分组进行逻辑隔离，同一服务可以属于不同分组。<br>
         * 格式示例：development、production等
         * </p>
         */
        @JSONField(name = GROUP, ordinal = 1)
        protected String group;

        /**
         * 重试次数配置
         * <p>
         * 用于指定Dubbo服务调用失败时的重试次数。<br>
         * 不包括首次调用，0表示不重试。<br>
         * 格式示例：2（表示最多重试2次，总共调用3次）
         * </p>
         */
        @JSONField(name = RETRIES, ordinal = 2)
        protected Integer retries;

        /**
         * 超时配置
         * <p>
         * 用于指定Dubbo服务调用的超时时间（毫秒）。<br>
         * 如果在指定时间内未收到服务响应，则认为调用超时。<br>
         * 格式示例：5000（5秒）
         * </p>
         */
        @JSONField(name = TIMEOUT, ordinal = 3)
        protected Integer timeout;

        /**
         * 异步调用配置
         * <p>
         * 用于指定Dubbo服务调用是否采用异步方式。<br>
         * 异步调用可以提高系统吞吐量，适用于不需要立即获取结果的场景。<br>
         * 格式示例：true、false
         * </p>
         */
        @JSONField(name = ASYNC, ordinal = 4)
        protected Boolean async;

        /**
         * 负载均衡配置
         * <p>
         * 用于指定Dubbo服务调用的负载均衡策略。<br>
         * 支持多种负载均衡算法，如random（随机）、roundrobin（轮询）、leastactive（最少活跃调用）等。<br>
         * 格式示例：random、roundrobin
         * </p>
         */
        @JSONField(name = LOAD_BALANCE, ordinal = 5)
        protected String loadBalance;

        /**
         * 默认构造函数
         * <p>
         * 创建一个空的服务引用配置实例。<br>
         * 各配置项字段默认为null，需要通过setter方法或Builder进行设置。
         * </p>
         */
        public Reference() {
        }

        /**
         * 创建服务引用配置构建器
         * <p>
         * 提供一个静态方法用于创建服务引用配置的构建器实例。<br>
         * 通过构建器可以使用链式调用的方式构建服务引用配置。
         * </p>
         *
         * @return 服务引用配置构建器实例
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * 合并服务引用配置
         * <p>
         * 将当前服务引用配置与另一个服务引用配置进行合并。<br>
         * 合并规则：如果当前配置中的某个字段为null或空，则使用另一个配置中的对应字段值。<br>
         * 该方法用于实现服务引用配置的继承和覆盖机制。
         * </p>
         *
         * @param other 要合并的另一个服务引用配置，可以为null
         * @return 合并后的新服务引用配置实例
         */
        @Override
        public Reference merge(Reference other) {
            if (other == null) {
                return copy();
            }
            var localOther = other.copy();
            var self = copy();
            self.version = StringUtils.isBlank(self.version) ? localOther.version : self.version;
            self.retries = Objects.isNull(retries) ? localOther.retries : self.retries;
            self.timeout = Objects.isNull(timeout) ? localOther.timeout : self.timeout;
            self.async = Objects.isNull(async) ? localOther.async : self.async;
            self.loadBalance = StringUtils.isBlank(self.loadBalance) ? localOther.loadBalance : self.loadBalance;
            return self;
        }

        /**
         * 上下文求值
         * <p>
         * 对服务引用配置中的各个字段进行上下文变量求值。<br>
         * 将包含变量表达式（如${variable}）的字段替换为实际值。<br>
         * 该方法用于实现服务引用配置参数的动态替换。<br>
         * 注意：当前实现为空，因为服务引用配置通常不需要上下文求值。
         * </p>
         *
         * @param context 上下文包装器，包含变量信息
         * @return 求值后的服务引用配置实例（当前实例）
         */
        @Override
        public Reference evaluate(ContextWrapper context) {
            return this;
        }

        /**
         * 获取版本配置
         *
         * @return 版本配置，可能为null
         */
        public String getVersion() {
            return version;
        }

        /**
         * 设置版本配置
         *
         * @param version 版本配置
         */
        public void setVersion(String version) {
            this.version = version;
        }

        /**
         * 获取分组配置
         *
         * @return 分组配置，可能为null
         */
        public String getGroup() {
            return group;
        }

        /**
         * 设置分组配置
         *
         * @param group 分组配置
         */
        public void setGroup(String group) {
            this.group = group;
        }

        /**
         * 获取重试次数配置
         * <p>
         * 获取重试次数配置，如果未设置则返回默认值1。<br>
         * 重试次数不包括首次调用，0表示不重试。
         * </p>
         *
         * @return 重试次数配置，默认为1
         */
        public Integer getRetries() {
            return Objects.isNull(retries) ? 1 : retries;
        }

        /**
         * 设置重试次数配置
         *
         * @param retries 重试次数配置
         */
        public void setRetries(Integer retries) {
            this.retries = retries;
        }

        /**
         * 获取超时配置
         * <p>
         * 获取超时配置（毫秒），如果未设置则返回默认值5000。<br>
         * 如果在指定时间内未收到服务响应，则认为调用超时。
         * </p>
         *
         * @return 超时配置（毫秒），默认为5000（5秒）
         */
        public Integer getTimeout() {
            return Objects.isNull(timeout) ? 5000 : timeout;
        }

        /**
         * 设置超时配置
         *
         * @param timeout 超时配置（毫秒）
         */
        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }

        /**
         * 获取异步调用配置
         * <p>
         * 获取异步调用配置，如果未设置则返回默认值false。<br>
         * 异步调用可以提高系统吞吐量，适用于不需要立即获取结果的场景。
         * </p>
         *
         * @return 异步调用配置，默认为false
         */
        public Boolean getAsync() {
            return Objects.nonNull(async) && async;
        }

        /**
         * 设置异步调用配置
         *
         * @param async 异步调用配置
         */
        public void setAsync(Boolean async) {
            this.async = async;
        }

        /**
         * 获取负载均衡配置
         *
         * @return 负载均衡配置，可能为null
         */
        public String getLoadBalance() {
            return loadBalance;
        }

        /**
         * 设置负载均衡配置
         *
         * @param loadBalance 负载均衡配置
         */
        public void setLoadBalance(String loadBalance) {
            this.loadBalance = loadBalance;
        }

        /**
         * Dubbo 服务引用配置属性构建器
         * <p>
         * 用于通过链式调用的方式构建服务引用配置。<br>
         * 继承自AbstractTestElement.ConfigureBuilder，提供标准的构建器功能。<br>
         * 支持Groovy闭包和Java函数式接口两种配置方式。
         * </p>
         */
        public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, Reference> {

            private Reference reference = new Reference();

            /**
             * 设置版本配置
             * <p>
             * 设置Dubbo服务的版本号。<br>
             * 在同一个注册中心中，相同接口不同版本的服务可以通过版本号进行区分。<br>
             * 格式示例：1.0.0、2.0.0
             * </p>
             *
             * @param version 版本配置
             * @return 构建器实例，支持链式调用
             */
            public Builder version(String version) {
                reference.version = version;
                return self;
            }

            /**
             * 设置分组配置
             * <p>
             * 设置Dubbo服务的分组信息。<br>
             * 在Dubbo中，服务可以通过分组进行逻辑隔离，同一服务可以属于不同分组。<br>
             * 格式示例：development、production等
             * </p>
             *
             * @param group 分组配置
             * @return 构建器实例，支持链式调用
             */
            public Builder group(String group) {
                reference.group = group;
                return self;
            }

            /**
             * 设置重试次数配置
             * <p>
             * 设置Dubbo服务调用失败时的重试次数。<br>
             * 不包括首次调用，0表示不重试。<br>
             * 格式示例：2（表示最多重试2次，总共调用3次）
             * </p>
             *
             * @param retries 重试次数配置
             * @return 构建器实例，支持链式调用
             */
            public Builder retries(int retries) {
                reference.retries = retries;
                return self;
            }

            /**
             * 设置超时配置
             * <p>
             * 设置Dubbo服务调用的超时时间（毫秒）。<br>
             * 如果在指定时间内未收到服务响应，则认为调用超时。<br>
             * 格式示例：5000（5秒）
             * </p>
             *
             * @param timeout 超时配置（毫秒）
             * @return 构建器实例，支持链式调用
             */
            public Builder timeout(int timeout) {
                reference.timeout = timeout;
                return self;
            }

            /**
             * 设置异步调用配置
             * <p>
             * 设置Dubbo服务调用是否采用异步方式。<br>
             * 异步调用可以提高系统吞吐量，适用于不需要立即获取结果的场景。<br>
             * 格式示例：true、false
             * </p>
             *
             * @param async 异步调用配置
             * @return 构建器实例，支持链式调用
             */
            public Builder async(boolean async) {
                reference.async = async;
                return self;
            }

            /**
             * 设置负载均衡配置
             * <p>
             * 设置Dubbo服务调用的负载均衡策略。<br>
             * 支持多种负载均衡算法，如random（随机）、roundrobin（轮询）、leastactive（最少活跃调用）等。<br>
             * 格式示例：random、roundrobin
             * </p>
             *
             * @param loadBalance 负载均衡配置
             * @return 构建器实例，支持链式调用
             */
            public Builder loadBalance(String loadBalance) {
                reference.loadBalance = loadBalance;
                return self;
            }

            /**
             * 合并服务引用配置
             * <p>
             * 将指定的服务引用配置与当前配置进行合并。<br>
             * 合并规则：如果当前配置中的某个字段为null或空，则使用指定配置中的对应字段值。<br>
             * 该方法用于实现服务引用配置的继承和覆盖机制。
             * </p>
             *
             * @param reference 要合并的服务引用配置
             * @return 构建器实例，支持链式调用
             */
            public Builder config(Reference reference) {
                this.reference = this.reference.merge(reference);
                return self;
            }

            /**
             * 构建服务引用配置实例
             * <p>
             * 根据构建器中的配置信息创建服务引用配置实例。<br>
             * 该方法是构建器模式的最后一步，返回构建完成的配置实例。
             * </p>
             *
             * @return 服务引用配置实例
             */
            @Override
            public Reference build() {
                return reference;
            }
        }
    }

    /**
     * Dubbo 配置属性构建器
     * <p>
     * 用于通过链式调用的方式构建Dubbo配置项。<br>
     * 继承自AbstractTestElement.ConfigureBuilder，提供标准的构建器功能。<br>
     * 支持Groovy闭包和Java函数式接口两种配置方式。<br>
     * 提供了对注册中心配置、服务引用配置、接口信息、方法信息、参数信息等的构建支持。
     * </p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, DubboConfigureItem> {

        private DubboConfigureItem configure = new DubboConfigureItem();

        /**
         * 默认构造函数
         * <p>
         * 创建一个Dubbo配置项构建器实例。<br>
         * 初始化一个空的Dubbo配置项作为构建基础。
         * </p>
         */
        public Builder() {
        }

        /**
         * 设置注册中心配置（通过Consumer函数式接口）
         * <p>
         * 通过Consumer函数式接口配置注册中心。<br>
         * 该方法提供了一种函数式编程的方式来配置注册中心，适用于Java 8及以上版本。<br>
         * 例如：registry(builder -> builder.address("zookeeper://127.0.0.1:2181"))
         * </p>
         *
         * @param consumer 注册中心构建器的Consumer函数式接口
         * @return 构建器实例，支持链式调用
         */
        public Builder registry(Consumer<Registry.Builder> consumer) {
            var builder = Registry.builder();
            consumer.accept(builder);
            configure.registry = builder.build();
            return self;
        }

        /**
         * 设置注册中心配置（通过Groovy闭包）
         * <p>
         * 通过Groovy闭包配置注册中心。<br>
         * 该方法提供了一种Groovy DSL的方式来配置注册中心，适用于支持Groovy的环境。<br>
         * 例如：registry { address "zookeeper://127.0.0.1:2181" }
         * </p>
         *
         * @param closure Groovy闭包
         * @return 构建器实例，支持链式调用
         */
        public Builder registry(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = Registry.Builder.class) Closure<?> closure) {
            var builder = Registry.builder();
            call(closure, builder);
            configure.registry = builder.build();
            return self;
        }

        /**
         * 设置注册中心配置（通过构建器）
         * <p>
         * 通过已有的注册中心构建器配置注册中心。<br>
         * 该方法适用于已有构建器实例的场景。<br>
         * 例如：registry(Registry.builder().address("zookeeper://127.0.0.1:2181"))
         * </p>
         *
         * @param registry 注册中心构建器
         * @return 构建器实例，支持链式调用
         */
        public Builder registry(Registry.Builder registry) {
            configure.registry = registry.build();
            return self;
        }

        /**
         * 设置注册中心配置（通过配置实例）
         * <p>
         * 通过已有的注册中心配置实例配置注册中心。<br>
         * 该方法适用于已有配置实例的场景。<br>
         * 例如：registry(new Registry())
         * </p>
         *
         * @param registry 注册中心配置实例
         * @return 构建器实例，支持链式调用
         */
        public Builder registry(Registry registry) {
            configure.registry = registry;
            return self;
        }

        /**
         * 设置服务引用配置（通过Consumer函数式接口）
         * <p>
         * 通过Consumer函数式接口配置服务引用。<br>
         * 该方法提供了一种函数式编程的方式来配置服务引用，适用于Java 8及以上版本。<br>
         * 例如：reference(builder -> builder.timeout(5000))
         * </p>
         *
         * @param consumer 服务引用构建器的Consumer函数式接口
         * @return 构建器实例，支持链式调用
         */
        public Builder reference(Consumer<Reference.Builder> consumer) {
            var builder = Reference.builder();
            consumer.accept(builder);
            configure.reference = builder.build();
            return self;
        }

        /**
         * 设置服务引用配置（通过Groovy闭包）
         * <p>
         * 通过Groovy闭包配置服务引用。<br>
         * 该方法提供了一种Groovy DSL的方式来配置服务引用，适用于支持Groovy的环境。<br>
         * 例如：reference { timeout 5000 }
         * </p>
         *
         * @param closure Groovy闭包
         * @return 构建器实例，支持链式调用
         */
        public Builder reference(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = Reference.Builder.class) Closure<?> closure) {
            var builder = Reference.builder();
            call(closure, builder);
            configure.reference = builder.build();
            return self;
        }

        /**
         * 设置服务引用配置（通过构建器）
         * <p>
         * 通过已有的服务引用构建器配置服务引用。<br>
         * 该方法适用于已有构建器实例的场景。<br>
         * 例如：reference(Reference.builder().timeout(5000))
         * </p>
         *
         * @param reference 服务引用构建器
         * @return 构建器实例，支持链式调用
         */
        public Builder reference(Reference.Builder reference) {
            configure.reference = reference.build();
            return self;
        }

        /**
         * 设置服务引用配置（通过配置实例）
         * <p>
         * 通过已有的服务引用配置实例配置服务引用。<br>
         * 该方法适用于已有配置实例的场景。<br>
         * 例如：reference(new Reference())
         * </p>
         *
         * @param reference 服务引用配置实例
         * @return 构建器实例，支持链式调用
         */
        public Builder reference(Reference reference) {
            configure.reference = reference;
            return self;
        }

        /**
         * 设置接口名称
         * <p>
         * 设置要调用的Dubbo服务接口的全限定类名。<br>
         * 例如：com.example.service.DemoService
         * </p>
         *
         * @param interfaceName 接口名称
         * @return 构建器实例，支持链式调用
         */
        public Builder interfaceName(String interfaceName) {
            configure.interfaceName = interfaceName;
            return self;
        }

        /**
         * 设置方法名称
         * <p>
         * 设置要调用的Dubbo服务接口中的具体方法名。<br>
         * 例如：getUserInfo、createOrder等
         * </p>
         *
         * @param method 方法名称
         * @return 构建器实例，支持链式调用
         */
        public Builder method(String method) {
            configure.method = method;
            return self;
        }

        /**
         * 设置参数值列表（通过Consumer函数式接口）
         * <p>
         * 通过Consumer函数式接口配置参数值列表。<br>
         * 该方法提供了一种函数式编程的方式来配置参数值，适用于Java 8及以上版本。<br>
         * 例如：parameters(list -> list.add("张三"))
         * </p>
         *
         * @param consumer 参数值列表的Consumer函数式接口
         * @return 构建器实例，支持链式调用
         */
        public Builder parameters(Consumer<List<Object>> consumer) {
            List<Object> parameters = Collections.newArrayList();
            consumer.accept(parameters);
            return parameters(parameters);
        }

        /**
         * 设置参数值列表（通过Groovy闭包）
         * <p>
         * 通过Groovy闭包配置参数值列表。<br>
         * 该方法提供了一种Groovy DSL的方式来配置参数值，适用于支持Groovy的环境。<br>
         * 例如：parameters { add "张三" }
         * </p>
         *
         * @param closure Groovy闭包
         * @return 构建器实例，支持链式调用
         */
        public Builder parameters(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = List.class) Closure<?> closure) {
            List<Object> parameters = Collections.newArrayList();
            call(closure, parameters);
            return parameters(parameters);
        }

        /**
         * 设置参数值列表
         * <p>
         * 设置方法调用时的实际参数值列表。<br>
         * 参数值会按照顺序传递给对应的方法参数。<br>
         * 同时会自动推导参数类型列表，简化配置。<br>
         * 如果已存在参数值列表，则进行合并。
         * </p>
         *
         * @param parameters 参数值列表
         * @return 构建器实例，支持链式调用
         */
        public Builder parameters(List<Object> parameters) {
            configure.parameters = Collections.addAllIfNonNull(configure.parameters, parameters);
            if (Objects.nonNull(parameters) && !parameters.isEmpty()) {
                List<String> parameterTypes = Collections.newArrayList();
                for (Object parameter : parameters) {
                    parameterTypes.add(parameter.getClass().getName());
                }
                configure.parameterTypes = Collections.addAllIfNonNull(configure.parameterTypes, Collections.newArrayList(parameterTypes));
            }
            return self;
        }

        /**
         * 设置单个参数值
         * <p>
         * 设置单个方法调用时的参数值。<br>
         * 该方法会将参数值包装成列表进行处理。<br>
         * 同时会自动推导参数类型，简化配置。
         * </p>
         *
         * @param parameter 参数值
         * @return 构建器实例，支持链式调用
         */
        public Builder parameters(Object parameter) {
            return parameters(Collections.newArrayList(parameter));
        }

        /**
         * 设置附加参数映射（通过Consumer函数式接口）
         * <p>
         * 通过Consumer函数式接口配置附加参数映射。<br>
         * 该方法提供了一种函数式编程的方式来配置附加参数，适用于Java 8及以上版本。<br>
         * 例如：attachmentArgs(map -> map.put("traceId", "12345"))
         * </p>
         *
         * @param consumer 附加参数映射的Consumer函数式接口
         * @return 构建器实例，支持链式调用
         */
        public Builder attachmentArgs(Consumer<Map<String, String>> consumer) {
            Map<String, String> attachmentArgs = Collections.newHashMap();
            consumer.accept(attachmentArgs);
            configure.attachmentArgs = Collections.putAllIfNonNull(configure.attachmentArgs, attachmentArgs);
            return self;
        }

        /**
         * 设置附加参数映射（通过Groovy闭包）
         * <p>
         * 通过Groovy闭包配置附加参数映射。<br>
         * 该方法提供了一种Groovy DSL的方式来配置附加参数，适用于支持Groovy的环境。<br>
         * 例如：attachmentArgs { put "traceId", "12345" }
         * </p>
         *
         * @param closure Groovy闭包
         * @return 构建器实例，支持链式调用
         */
        public Builder attachmentArgs(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = List.class) Closure<?> closure) {
            Map<String, String> attachmentArgs = Collections.newHashMap();
            call(closure, attachmentArgs);
            configure.attachmentArgs = Collections.putAllIfNonNull(configure.attachmentArgs, attachmentArgs);
            return self;
        }

        /**
         * 设置附加参数映射
         * <p>
         * 设置服务调用时的附加参数（attachments）映射。<br>
         * 这些参数不会传递给服务方法，但可以在服务端通过RpcContext获取。<br>
         * 常用于传递调用链追踪信息、用户身份信息等。<br>
         * 如果已存在附加参数映射，则进行合并。
         * </p>
         *
         * @param attachmentArgs 附加参数映射
         * @return 构建器实例，支持链式调用
         */
        public Builder attachmentArgs(Map<String, String> attachmentArgs) {
            configure.attachmentArgs = Collections.putAllIfNonNull(configure.attachmentArgs, attachmentArgs);
            return self;
        }

        /**
         * 设置单个附加参数
         * <p>
         * 设置单个服务调用时的附加参数。<br>
         * 该方法会将参数包装成映射进行处理。<br>
         * 如果已存在附加参数映射，则进行合并。
         * </p>
         *
         * @param name  附加参数名称
         * @param value 附加参数值
         * @return 构建器实例，支持链式调用
         */
        public Builder attachmentArgs(String name, String value) {
            configure.attachmentArgs = Collections.putAllIfNonNull(configure.attachmentArgs, (Map<String, String>) Collections.of(name, value));
            return self;
        }

        /**
         * 设置配置引用名称
         * <p>
         * 设置该配置项的引用名称，便于在测试用例中引用。<br>
         * 如果未指定，则使用默认引用名称{@link DubboConstantsInterface#DEF_REF_NAME_KEY}。
         * </p>
         *
         * @param ref 配置引用名称
         * @return 构建器实例，支持链式调用
         */
        public Builder ref(String ref) {
            configure.ref = ref;
            return self;
        }

        /**
         * 合并Dubbo配置项（通过Consumer函数式接口）
         * <p>
         * 通过Consumer函数式接口合并Dubbo配置项。<br>
         * 该方法提供了一种函数式编程的方式来合并配置项，适用于Java 8及以上版本。<br>
         * 例如：config(builder -> builder.interfaceName("com.example.service.DemoService"))
         * </p>
         *
         * @param consumer Dubbo配置项构建器的Consumer函数式接口
         * @return 构建器实例，支持链式调用
         */
        public Builder config(Consumer<Builder> consumer) {
            var builder = DubboConfigureItem.builder();
            consumer.accept(builder);
            configure = configure.merge(builder.build());
            return self;
        }

        /**
         * 合并Dubbo配置项（通过Groovy闭包）
         * <p>
         * 通过Groovy闭包合并Dubbo配置项。<br>
         * 该方法提供了一种Groovy DSL的方式来合并配置项，适用于支持Groovy的环境。<br>
         * 例如：config { interfaceName "com.example.service.DemoService" }
         * </p>
         *
         * @param closure Groovy闭包
         * @return 构建器实例，支持链式调用
         */
        public Builder config(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = Builder.class) Closure<?> closure) {
            var builder = DubboConfigureItem.builder();
            call(closure, builder);
            configure = configure.merge(builder.build());
            return self;
        }

        /**
         * 合并Dubbo配置项（通过构建器）
         * <p>
         * 通过已有的Dubbo配置项构建器合并配置项。<br>
         * 该方法适用于已有构建器实例的场景。<br>
         * 例如：config(DubboConfigureItem.builder().interfaceName("com.example.service.DemoService"))
         * </p>
         *
         * @param builder Dubbo配置项构建器
         * @return 构建器实例，支持链式调用
         */
        public Builder config(Builder builder) {
            configure = configure.merge(builder.build());
            return self;
        }

        /**
         * 合并Dubbo配置项（通过配置实例）
         * <p>
         * 通过已有的Dubbo配置项实例合并配置项。<br>
         * 该方法适用于已有配置实例的场景。<br>
         * 例如：config(new DubboConfigureItem())
         * </p>
         *
         * @param config Dubbo配置项实例
         * @return 构建器实例，支持链式调用
         */
        public Builder config(DubboConfigureItem config) {
            configure = configure.merge(config);
            return self;
        }

        /**
         * 构建Dubbo配置项实例
         * <p>
         * 根据构建器中的配置信息创建Dubbo配置项实例。<br>
         * 该方法是构建器模式的最后一步，返回构建完成的配置实例。
         * </p>
         *
         * @return Dubbo配置项实例
         */
        @Override
        public DubboConfigureItem build() {
            return configure;
        }
    }
}