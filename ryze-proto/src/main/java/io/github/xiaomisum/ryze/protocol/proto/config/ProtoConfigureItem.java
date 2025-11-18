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

package io.github.xiaomisum.ryze.protocol.proto.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.proto.ProtoConstantsInterface;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;
import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.REF;

/**
 * Proto协议配置项类
 * <p>
 * 该类封装了Proto协议的所有配置参数，包括协议、主机、端口、路径、方法、
 * 请求头、查询参数、请求体等。实现了ConfigureItem接口，
 * 支持配置项的合并和变量求值。
 * </p>
 *
 * @author xiaomi
 * @since 2025/10/25 20:17
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProtoConfigureItem implements ConfigureItem<ProtoConfigureItem>, ProtoConstantsInterface {

    /**
     * 引用名称
     * <p>用于在测试上下文中引用该配置项</p>
     */
    @JSONField(name = REF)
    protected String ref;

    @JSONField(name = PROTO_DESC)
    protected Proto protoDesc;

    /**
     * 请求发送协议
     * <p>协议类型，如"http"、"https"、"ws"、"wss"</p>
     */
    @JSONField(name = PROTOCOL, ordinal = 1)
    protected String protocol;

    /**
     * 主机
     * <p>目标服务器主机名或IP地址</p>
     */
    @JSONField(name = HOST, ordinal = 2)
    protected String host;

    /**
     * 端口
     * <p>目标服务器端口号</p>
     */
    @JSONField(name = PORT, ordinal = 2)
    protected String port;

    /**
     * 路径
     * <p>请求路径</p>
     */
    @JSONField(name = PATH, ordinal = 3)
    protected String path;

    /**
     * HTTP方法
     * <p>HTTP请求方法，如"GET"、"POST"等</p>
     */
    @JSONField(name = REQUEST_METHOD, ordinal = 4)
    protected String method;

    /**
     * HTTP/2标识
     * <p>是否使用HTTP/2协议</p>
     */
    @JSONField(name = HTTP2, ordinal = 5)
    protected Boolean http2;

    /**
     * 请求头映射
     * <p>请求头键值对</p>
     */
    @JSONField(name = HEADERS, ordinal = 6)
    protected Map<String, String> headers;

    /**
     * 查询参数映射
     * <p>URL查询参数键值对</p>
     */
    @JSONField(name = QUERY, ordinal = 5)
    protected Map<String, Object> query;

    /**
     * 请求体对象
     * <p>请求体内容</p>
     */
    @JSONField(name = BODY, ordinal = 5)
    protected Object body;

    /**
     * 超时时间
     * <p>超时时间</p>
     */
    @JSONField(name = TIMEOUT, ordinal = 6)
    protected Integer timeout;
    /**
     * 响应数据匹配模式
     * <p>响应数据匹配模式</p>
     */
    @JSONField(name = RESPONSE_PATTERN, ordinal = 7)
    protected String responsePattern;

    /**
     * 响应数据匹配模式
     * <p>响应数据匹配模式</p>
     */
    @JSONField(serialize = false, deserialize = false)
    protected Function<byte[], String> bytesToStringConverter;

    /**
     * 默认构造函数
     */
    public ProtoConfigureItem() {

    }

    /**
     * 创建HTTP配置项构建器
     *
     * @return HTTP配置项构建器实例
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
    public ProtoConfigureItem merge(ProtoConfigureItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.timeout = self.timeout == null ? localOther.timeout : self.timeout;
        self.protoDesc = self.protoDesc == null ? localOther.protoDesc : self.protoDesc.merge(localOther.protoDesc);
        self.protocol = StringUtils.isBlank(self.protocol) ? localOther.protocol : self.protocol;
        self.host = StringUtils.isBlank(self.host) ? localOther.host : self.host;
        self.port = StringUtils.isBlank(self.port) ? localOther.port : self.port;
        self.path = StringUtils.isBlank(self.path) ? localOther.path : self.path;
        self.method = StringUtils.isBlank(self.method) ? localOther.method : self.method;
        self.http2 = self.http2 == null ? localOther.http2 : self.http2;
        self.headers = handleMap(other.headers, headers);
        self.query = self.query == null ? localOther.query : self.query;
        self.body = self.body == null ? localOther.body : self.body;
        self.responsePattern = StringUtils.isBlank(self.responsePattern) ? localOther.responsePattern : self.responsePattern;
        self.bytesToStringConverter = localOther.bytesToStringConverter;
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
    public ProtoConfigureItem evaluate(ContextWrapper context) {
        ref = (String) context.evaluate(ref);
        protocol = (String) context.evaluate(protocol);
        host = (String) context.evaluate(host);
        port = (String) context.evaluate(port);
        path = (String) context.evaluate(path);
        method = (String) context.evaluate(method);
        headers = (Map<String, String>) context.evaluate(headers);
        query = (Map<String, Object>) context.evaluate(query);
        body = context.evaluate(body);
        protoDesc = (Proto) context.evaluate(protoDesc);
        return this;
    }

    /**
     * 处理映射表合并
     *
     * @param other 其他映射表
     * @param self  当前映射表
     * @return 合并后的映射表
     */
    private Map<String, String> handleMap(Map<String, String> other, Map<String, String> self) {
        var resp = new HashMap<String, String>();
        if (other != null) {
            resp.putAll(other);
        }
        if (self != null) {
            resp.putAll(self);
        }
        return resp;
    }

    /**
     * 获取引用名称
     *
     * @return 引用名称
     */
    public String getRef() {
        return StringUtils.isBlank(ref) ? DEF_REF_NAME_KEY : ref;
    }

    /**
     * 设置引用名称
     *
     * @param ref 引用名称
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * 获取Proto配置
     *
     * @return Proto配置
     */
    public Proto getProtoDesc() {
        return protoDesc;
    }

    /**
     * 设置Proto配置
     *
     * @param protoDesc Proto配置
     */
    public void setProtoDesc(Proto protoDesc) {
        this.protoDesc = protoDesc;
    }

    /**
     * 获取协议
     *
     * @return 协议，默认为"http"
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * 设置协议
     *
     * @param protocol 协议
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * 获取协议
     *
     * @return 协议，默认为"http"
     */
    public String getProtocol(String defaultProtocol) {
        return StringUtils.isBlank(protocol) ? defaultProtocol : protocol;
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
    public String getPort() {
        return port;
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
     * 获取完整端口（包含冒号前缀）
     *
     * @return 完整端口字符串
     */
    @JSONField(serialize = false, deserialize = false)
    public String getFullPort() {
        return StringUtils.isBlank(port) ? "" : ":" + port;
    }

    /**
     * 获取路径
     *
     * @return 路径，默认为"/"
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置路径
     *
     * @param path 路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取路径
     *
     * @return 路径，默认为"/"
     */
    @JSONField(serialize = false, deserialize = false)
    public String getFullPath() {
        return StringUtils.isBlank(path) ? "/" : Strings.CS.startsWith(path, "/") ? path : "/" + path;
    }

    /**
     * 获取HTTP方法
     *
     * @return HTTP方法，默认为"GET"
     */
    public String getMethod() {
        return StringUtils.isBlank(method) ? GET : method.toUpperCase();
    }

    /**
     * 设置HTTP方法
     *
     * @param method HTTP方法
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 是否使用HTTP/2协议
     *
     * @return true表示使用HTTP/2，false表示使用HTTP/1.1
     */
    public boolean isHttp2() {
        return Objects.nonNull(http2) && http2;
    }

    /**
     * 设置HTTP/2协议标识
     *
     * @param http2 HTTP/2协议标识
     */
    public void setHttp2(Boolean http2) {
        this.http2 = http2;
    }

    /**
     * 获取请求头映射
     *
     * @return 请求头映射
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * 设置请求头映射
     *
     * @param headers 请求头映射
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * 获取查询参数映射
     *
     * @return 查询参数映射
     */
    public Map<String, Object> getQuery() {
        return query;
    }

    /**
     * 设置查询参数映射
     *
     * @param query 查询参数映射
     */
    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }

    /**
     * 获取请求体对象
     *
     * @return 请求体对象
     */
    public Object getBody() {
        return body;
    }

    /**
     * 设置请求体对象
     *
     * @param body 请求体对象
     */
    public void setBody(Object body) {
        this.body = body;
    }

    /**
     * 获取请求体字符串
     *
     * @return 请求体字符串
     */
    @JSONField(serialize = false, deserialize = false)
    public String getStringBody() {
        return body == null ? "{}" : body instanceof String str ? str : JSON.toJSONString(body);
    }


    /**
     * 获取超时时间
     *
     * @return 超时时间
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * 设置超时时间
     *
     * @param timeout 超时时间
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * 获取响应匹配正则表达式
     *
     * @return 响应匹配正则表达式
     */
    public String getResponsePattern() {
        return responsePattern;
    }

    /**
     * 设置响应匹配正则表达式
     *
     * @param responsePattern 响应匹配正则表达式
     */
    public void setResponsePattern(String responsePattern) {
        this.responsePattern = responsePattern;
    }

    /**
     * HTTP协议配置项构建类
     * <p>
     * 提供链式调用方式创建HTTP配置项实例，支持设置所有HTTP相关配置参数。
     * </p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, ProtoConfigureItem> {

        private ProtoConfigureItem configure = new ProtoConfigureItem();

        /**
         * 默认构造函数
         */
        public Builder() {
        }

        /**
         * 设置引用名称
         *
         * @param ref 引用名称
         * @return 构建器实例
         */
        public Builder ref(String ref) {
            configure.ref = ref;
            return self;
        }

        /**
         * 设置Proto配置
         *
         * @param customizer Proto配置自定义器
         * @return 构建器实例
         */
        public Builder protoDesc(Customizer<Proto.Builder> customizer) {
            Proto.Builder builder = Proto.builder();
            customizer.customize(builder);
            this.configure.protoDesc = builder.build();
            return self;
        }

        /**
         * 设置Proto配置
         *
         * @param closure Proto配置自定义器
         * @return 构建器实例
         */
        public Builder protoDesc(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = Proto.Builder.class) Closure<?> closure) {
            Proto.Builder builder = Proto.builder();
            call(closure, builder);
            this.configure.protoDesc = builder.build();
            return self;
        }

        /**
         * 设置Proto配置
         *
         * @param proto Proto配置对象
         * @return 构建器实例
         */
        public Builder protoDesc(Proto proto) {
            configure.protoDesc = proto;
            return self;
        }

        /**
         * 设置协议
         *
         * @param protocol 协议
         * @return 构建器实例
         */
        public Builder protocol(String protocol) {
            configure.protocol = protocol;
            return self;
        }

        /**
         * 设置协议为HTTP
         *
         * @return 构建器实例
         */
        public Builder http() {
            configure.protocol = DEFAULT_PROTOCOL;
            return self;
        }

        /**
         * 设置协议为HTTPS
         *
         * @return 构建器实例
         */
        public Builder https() {
            configure.protocol = HTTPS;
            return self;
        }

        /**
         * 设置协议为WS
         *
         * @return 构建器实例
         */
        public Builder ws() {
            configure.protocol = WS;
            return self;
        }

        /**
         * 设置协议为WSS
         *
         * @return 构建器实例
         */
        public Builder wss() {
            configure.protocol = WSS;
            return self;
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
         * 设置路径
         *
         * @param path 路径
         * @return 构建器实例
         */
        public Builder path(String path) {
            configure.path = path;
            return self;
        }

        /**
         * 设置HTTP方法
         *
         * @param method HTTP方法
         * @return 构建器实例
         */
        public Builder method(String method) {
            configure.method = method;
            return self;
        }

        /**
         * 设置HTTP方法为GET
         *
         * @return 构建器实例
         */
        public Builder get() {
            configure.method = GET;
            return self;
        }

        /**
         * 设置HTTP方法为POST
         *
         * @return 构建器实例
         */
        public Builder post() {
            configure.method = POST;
            return self;
        }

        /**
         * 设置HTTP方法为PUT
         *
         * @return 构建器实例
         */
        public Builder put() {
            configure.method = PUT;
            return self;
        }

        /**
         * 设置HTTP方法为DELETE
         *
         * @return 构建器实例
         */
        public Builder delete() {
            configure.method = DELETE;
            return self;
        }

        /**
         * 启用HTTP/2协议
         *
         * @return 构建器实例
         */
        public Builder http2() {
            configure.http2 = true;
            return self;
        }

        /**
         * 设置请求头映射
         *
         * @param headers 请求头映射
         * @return 构建器实例
         */
        public Builder headers(Map<String, String> headers) {
            configure.headers = Collections.putAllIfNonNull(configure.headers, headers);
            return self;
        }

        /**
         * 通过自定义器设置请求头映射
         *
         * @param customizer 请求头自定义器
         * @return 构建器实例
         */
        public Builder headers(Customizer<Map<String, String>> customizer) {
            Map<String, String> headers = new HashMap<>();
            customizer.customize(headers);
            configure.headers = Collections.putAllIfNonNull(configure.headers, headers);
            return self;
        }

        /**
         * 设置查询参数映射
         *
         * @param query 查询参数映射
         * @return 构建器实例
         */
        public Builder query(Map<String, Object> query) {
            configure.query = Collections.putAllIfNonNull(configure.query, query);
            return self;
        }

        /**
         * 通过自定义器设置查询参数映射
         *
         * @param customizer 查询参数自定义器
         * @return 构建器实例
         */
        public Builder query(Customizer<Map<String, Object>> customizer) {
            Map<String, Object> query = new HashMap<>();
            customizer.customize(query);
            configure.query = Collections.putAllIfNonNull(configure.query, query);
            return self;
        }

        /**
         * 通过自定义器设置请求体映射
         * <p>仅当请求体是Map类型时生效</p>
         *
         * @param customizer 请求体自定义器
         * @return 构建器实例
         */
        public Builder body(Customizer<Map<String, Object>> customizer) {
            if (configure.body != null && !(configure.body instanceof Map)) {
                return self;
            }
            var body = new HashMap<String, Object>();
            customizer.customize(body);
            configure.body = Collections.putAllIfNonNull((Map) configure.body, body);
            return self;
        }

        /**
         * 设置请求体映射
         * <p>仅当请求体是Map类型时生效</p>
         *
         * @param body 请求体映射
         * @return 构建器实例
         */
        public Builder body(Map<String, Object> body) {
            if (configure.body != null && !(configure.body instanceof Map)) {
                return self;
            }
            configure.body = Collections.putAllIfNonNull((Map) configure.body, body);
            return self;
        }

        /**
         * 设置请求体对象
         *
         * @param body 请求体对象
         * @return 构建器实例
         */
        public Builder body(String body) {
            configure.body = body;
            return self;
        }

        /**
         * 设置超时时间
         *
         * @param timeout 超时时间
         * @return 构建器实例
         */
        public Builder timeout(Integer timeout) {
            configure.timeout = timeout;
            return self;
        }

        /**
         * 设置响应匹配模式
         *
         * @param responsePattern 响应匹配模式
         * @return 构建器实例
         */
        public Builder responsePattern(String responsePattern) {
            configure.responsePattern = responsePattern;
            return self;
        }

        /**
         * 通过消费者函数配置
         *
         * @param consumer 配置消费者函数
         * @return 构建器实例
         */
        public Builder config(Consumer<Builder> consumer) {
            var builder = ProtoConfigureItem.builder();
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
            var builder = ProtoConfigureItem.builder();
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
        public Builder config(ProtoConfigureItem config) {
            configure = configure.merge(config);
            return self;
        }

        /**
         * 构建HTTP配置项实例
         *
         * @return HTTP配置项实例
         */
        @Override
        public ProtoConfigureItem build() {
            return configure;
        }
    }

    /**
     * Proto协议配置内部类
     * <p>封装了Proto协议特有的配置信息，包括描述文件路径和消息名称等</p>
     */
    public static class Proto implements ConfigureItem<Proto>, ProtoConstantsInterface {

        /**
         * proto描述文件路径
         */
        @JSONField(name = DESC_PATH)
        private String descPath;
        /**
         * 请求消息名称
         * <p>如: io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User</p>
         */
        @JSONField(name = REQUEST_MESSAGE_NAME, ordinal = 1)
        private String requestMessageName;
        /**
         * 响应消息名称
         * <p>如: io.github.xiaomisum.ryze.protocol.example.springboot.UserOuterClass.User</p>
         */
        @JSONField(name = RESPONSE_MESSAGE_NAME, ordinal = 2)
        private String responseMessageName;

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
         * 合并Proto配置
         *
         * @param other 其他Proto配置
         * @return 合并后的Proto配置
         */
        @Override
        public Proto merge(Proto other) {
            if (other == null) {
                return copy();
            }
            var localOther = other.copy();
            var self = copy();
            self.descPath = StringUtils.isBlank(self.descPath) ? localOther.descPath : self.descPath;
            self.requestMessageName = StringUtils.isBlank(self.requestMessageName) ? localOther.requestMessageName : self.requestMessageName;
            self.responseMessageName = StringUtils.isBlank(self.responseMessageName) ? localOther.responseMessageName : self.responseMessageName;
            return self;
        }


        /**
         * 对配置项中的变量进行求值
         *
         * @param context 上下文包装器
         * @return 求值后的配置项
         */
        @Override
        public Proto evaluate(ContextWrapper context) {
            descPath = (String) context.evaluate(descPath);
            requestMessageName = (String) context.evaluate(requestMessageName);
            responseMessageName = (String) context.evaluate(responseMessageName);
            return this;
        }

        /**
         * 获取描述文件路径
         *
         * @return 描述文件路径
         */
        public String getDescPath() {
            return descPath;
        }

        /**
         * 设置描述文件路径
         *
         * @param descPath 描述文件路径
         */
        public void setDescPath(String descPath) {
            this.descPath = descPath;
        }

        /**
         * 获取请求消息名称
         *
         * @return 请求消息名称
         */
        public String getRequestMessageName() {
            return requestMessageName;
        }

        /**
         * 设置请求消息名称
         *
         * @param requestMessageName 请求消息名称
         */
        public void setRequestMessageName(String requestMessageName) {
            this.requestMessageName = requestMessageName;
        }

        /**
         * 获取响应消息名称
         *
         * @return 响应消息名称
         */
        public String getResponseMessageName() {
            return responseMessageName;
        }

        /**
         * 设置响应消息名称
         *
         * @param responseMessageName 响应消息名称
         */
        public void setResponseMessageName(String responseMessageName) {
            this.responseMessageName = responseMessageName;
        }

        /**
         * Proto配置构建器
         */
        public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, Proto> {

            private final Proto proto = new Proto();

            /**
             * 设置描述文件路径
             *
             * @param descPath 描述文件路径
             * @return self
             */
            public Builder descPath(String descPath) {
                proto.descPath = descPath;
                return self;
            }

            /**
             * 设置请求消息名称
             *
             * @param requestMessageName 请求消息名称
             * @return self
             */
            public Builder requestMessageName(String requestMessageName) {
                proto.requestMessageName = requestMessageName;
                return self;
            }

            /**
             * 设置响应消息名称
             *
             * @param responseMessageName 响应消息名称
             * @return self
             */
            public Builder responseMessageName(String responseMessageName) {
                proto.responseMessageName = responseMessageName;
                return self;
            }

            /**
             * 构建Proto配置实例
             *
             * @return Proto配置实例
             */
            @Override
            public Proto build() {
                return proto;
            }
        }
    }
}