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

package io.github.xiaomisum.ryze.protocol.http.config;

import com.alibaba.fastjson2.annotation.JSONField;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import io.github.xiaomisum.ryze.protocol.http.HTTPConstantsInterface;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.Customizer;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface.REF;
import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * HTTP协议配置项类
 * <p>
 * 该类封装了HTTP协议的所有配置参数，包括协议、主机、端口、路径、方法、
 * 请求头、Cookie、查询参数、请求体等。实现了ConfigureItem接口，
 * 支持配置项的合并和变量求值。
 * </p>
 *
 * @author xiaomi
 * @since 2025/7/19 20:17
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class HTTPConfigureItem implements ConfigureItem<HTTPConfigureItem>, HTTPConstantsInterface {

    /**
     * 引用名称
     * <p>用于在测试上下文中引用该配置项</p>
     */
    @JSONField(name = REF)
    protected String ref;

    /**
     * 协议
     * <p>HTTP协议类型，如"http"或"https"</p>
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
     * <p>HTTP请求头键值对</p>
     */
    @JSONField(name = HEADERS, ordinal = 6)
    protected Map<String, String> headers;

    /**
     * Cookie映射
     * <p>HTTP Cookie信息</p>
     */
    @JSONField(name = COOKIE, ordinal = 7)
    protected Map<String, String> cookie;

    /**
     * 查询参数映射
     * <p>URL查询参数键值对</p>
     */
    @JSONField(name = QUERY, ordinal = 5)
    protected Map<String, Object> query;

    /**
     * 表单数据映射
     * <p>表单数据键值对</p>
     */
    @JSONField(name = DATA, ordinal = 5)
    protected Map<String, Object> data;

    /**
     * 请求体对象
     * <p>HTTP请求体内容</p>
     */
    @JSONField(name = BODY, ordinal = 5)
    protected Object body;

    /**
     * 字节数据
     * <p>二进制请求体数据</p>
     */
    @JSONField(name = BYTES, ordinal = 5)
    protected byte[] bytes;

    /**
     * 二进制文件对象
     * <p>上传文件信息</p>
     */
    @JSONField(name = BINARY, ordinal = 5)
    protected Object binary;

    /**
     * 默认构造函数
     */
    public HTTPConfigureItem() {

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
    public HTTPConfigureItem merge(HTTPConfigureItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.protocol = StringUtils.isBlank(self.protocol) ? localOther.protocol : self.protocol;
        self.host = StringUtils.isBlank(self.host) ? localOther.host : self.host;
        self.port = StringUtils.isBlank(self.port) ? localOther.port : self.port;
        self.path = StringUtils.isBlank(self.path) ? localOther.path : self.path;
        self.method = StringUtils.isBlank(self.method) ? localOther.method : self.method;
        self.http2 = self.http2 == null ? localOther.http2 : self.http2;
        self.headers = handleMap(other.headers, headers);
        self.cookie = handleMap(other.cookie, cookie);
        self.query = self.query == null ? localOther.query : self.query;
        self.data = self.data == null ? localOther.data : self.data;
        self.body = self.body == null ? localOther.body : self.body;
        self.bytes = self.bytes == null ? localOther.bytes : self.bytes;
        self.binary = self.binary == null ? localOther.binary : self.binary;
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
    public HTTPConfigureItem evaluate(ContextWrapper context) {
        protocol = (String) context.evaluate(protocol);
        host = (String) context.evaluate(host);
        port = (String) context.evaluate(port);
        path = (String) context.evaluate(path);
        method = (String) context.evaluate(method);
        headers = (Map<String, String>) context.evaluate(headers);
        cookie = (Map<String, String>) context.evaluate(cookie);
        query = (Map<String, Object>) context.evaluate(query);
        data = (Map<String, Object>) context.evaluate(data);
        body = context.evaluate(body);
        binary = context.evaluate(binary);
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
     * 获取协议
     *
     * @return 协议，默认为"http"
     */
    public String getProtocol() {
        return StringUtils.isBlank(protocol) ? PROTOCOL_HTTP : protocol;
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
     * 获取路径
     *
     * @return 路径，默认为"/"
     */
    public String getPath() {
        return StringUtils.isBlank(path) ? "/" : path;
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
     * 获取Cookie映射
     *
     * @return Cookie映射
     */
    public Map<String, String> getCookie() {
        return cookie;
    }

    /**
     * 设置Cookie映射
     *
     * @param cookie Cookie映射
     */
    public void setCookie(Map<String, String> cookie) {
        this.cookie = cookie;
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
     * 获取表单数据映射
     *
     * @return 表单数据映射
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * 设置表单数据映射
     *
     * @param data 表单数据映射
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
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
     * 获取字节数据
     *
     * @return 字节数据
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * 设置字节数据
     *
     * @param bytes 字节数据
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * 获取二进制文件对象
     *
     * @return 二进制文件对象
     */
    public Object getBinary() {
        return binary;
    }

    /**
     * 设置二进制文件对象
     *
     * @param binary 二进制文件对象
     */
    public void setBinary(Object binary) {
        this.binary = binary;
    }

    /**
     * HTTP协议配置项构建类
     * <p>
     * 提供链式调用方式创建HTTP配置项实例，支持设置所有HTTP相关配置参数。
     * </p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, HTTPConfigureItem> {

        private HTTPConfigureItem configure = new HTTPConfigureItem();

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
         * 设置Cookie映射
         *
         * @param cookie Cookie映射
         * @return 构建器实例
         */
        public Builder cookie(Map<String, String> cookie) {
            configure.cookie = Collections.putAllIfNonNull(configure.cookie, cookie);
            return self;
        }

        /**
         * 通过自定义器设置Cookie映射
         *
         * @param customizer Cookie自定义器
         * @return 构建器实例
         */
        public Builder cookie(Customizer<Map<String, String>> customizer) {
            Map<String, String> cookie = new HashMap<>();
            customizer.customize(cookie);
            configure.cookie = Collections.putAllIfNonNull(configure.cookie, cookie);
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
         * 设置表单数据映射
         *
         * @param data 表单数据映射
         * @return 构建器实例
         */
        public Builder data(Map<String, Object> data) {
            configure.data = Collections.putAllIfNonNull(configure.data, data);
            return self;
        }

        /**
         * 通过自定义器设置表单数据映射
         *
         * @param customizer 表单数据自定义器
         * @return 构建器实例
         */
        public Builder data(Customizer<Map<String, Object>> customizer) {
            var data = new HashMap<String, Object>();
            customizer.customize(data);
            configure.data = Collections.putAllIfNonNull(configure.data, data);
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
        public Builder body(Object body) {
            configure.body = body;
            return self;
        }

        /**
         * 设置字节数据
         *
         * @param bytes 字节数据
         * @return 构建器实例
         */
        public Builder bytes(byte[] bytes) {
            configure.bytes = bytes;
            return self;
        }

        /**
         * 设置二进制文件对象
         *
         * @param binary 二进制文件对象
         * @return 构建器实例
         */
        public Builder binary(Object binary) {
            configure.binary = binary;
            return self;
        }

        /**
         * 通过消费者函数配置
         *
         * @param consumer 配置消费者函数
         * @return 构建器实例
         */
        public Builder config(Consumer<Builder> consumer) {
            var builder = HTTPConfigureItem.builder();
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
            var builder = HTTPConfigureItem.builder();
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
        public Builder config(HTTPConfigureItem config) {
            configure = configure.merge(config);
            return self;
        }

        /**
         * 构建HTTP配置项实例
         *
         * @return HTTP配置项实例
         */
        @Override
        public HTTPConfigureItem build() {
            return configure;
        }
    }
}