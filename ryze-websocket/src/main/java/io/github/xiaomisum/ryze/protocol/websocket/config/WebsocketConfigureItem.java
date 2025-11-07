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

package io.github.xiaomisum.ryze.protocol.websocket.config;

import com.alibaba.fastjson2.annotation.JSONField;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.websocket.WebsocketConstantsInterface;
import io.github.xiaomisum.ryze.support.Collections;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * Websocket协议配置项类
 * <p>
 * 该类封装了Websocket协议的所有配置参数，包括协议、主机、端口、路径、方法、
 * 请求头、查询参数、请求体等。实现了ConfigureItem接口，
 * 支持配置项的合并和变量求值。
 * </p>
 *
 * @author xiaomi
 * @since 2025/11/06 20:17
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class WebsocketConfigureItem implements ConfigureItem<WebsocketConfigureItem>, WebsocketConstantsInterface {

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
     * 请求头映射
     * <p>HTTP请求头键值对</p>
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
     * 字节数据
     * <p>二进制请求体数据</p>
     */
    @JSONField(name = BYTES, ordinal = 5)
    protected byte[] bytes;

    /**
     * 响应数据匹配模式
     * <p>响应数据匹配模式</p>
     */
    @JSONField(name = RESPONSE_PATTERN, ordinal = 6)
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
    public WebsocketConfigureItem() {

    }

    /**
     * 创建HTTP配置项构建器
     *
     * @return HTTP配置项构建器实例
     */
    public static WebsocketConfigureItem.Builder builder() {
        return new WebsocketConfigureItem.Builder();
    }

    /**
     * 合并属性，如果当前对象的属性值为空，则以 other 对象的属性值替换
     *
     * @param other 其他对象
     * @return 合并后的新对象
     */
    @Override
    public WebsocketConfigureItem merge(WebsocketConfigureItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.protocol = StringUtils.isBlank(self.protocol) ? localOther.protocol : self.protocol;
        self.host = StringUtils.isBlank(self.host) ? localOther.host : self.host;
        self.port = StringUtils.isBlank(self.port) ? localOther.port : self.port;
        self.path = StringUtils.isBlank(self.path) ? localOther.path : self.path;
        self.headers = handleMap(other.headers, headers);
        self.query = self.query == null ? localOther.query : self.query;
        self.body = self.body == null ? localOther.body : self.body;
        self.bytes = self.bytes == null ? localOther.bytes : self.bytes;
        self.timeout = self.timeout == null ? localOther.timeout : self.timeout;
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
    public WebsocketConfigureItem evaluate(ContextWrapper context) {
        ref = (String) context.evaluate(ref);
        protocol = (String) context.evaluate(protocol);
        host = (String) context.evaluate(host);
        port = (String) context.evaluate(port);
        path = (String) context.evaluate(path);
        headers = (Map<String, String>) context.evaluate(headers);
        query = (Map<String, Object>) context.evaluate(query);
        body = context.evaluate(body);
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
        return StringUtils.isBlank(protocol) ? DEFAULT_PROTOCOL : protocol;
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
        return StringUtils.isBlank(path) ? "/" : Strings.CS.startsWith(path, "/") ? path : "/" + path;
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
     * 获取响应匹配模式
     *
     * @return 响应匹配模式
     */
    public String getResponsePattern() {
        return responsePattern;
    }

    /**
     * 设置响应匹配模式
     *
     * @param responsePattern 响应匹配模式
     */
    public void setResponsePattern(String responsePattern) {
        this.responsePattern = responsePattern;
    }

    /**
     * 获取字节数据转换器
     *
     * @return 字节数据转换器
     */
    public Function<byte[], String> getBytesToStringConverter() {
        return bytesToStringConverter;
    }

    /**
     * 设置字节数据转换器
     *
     * @param bytesToStringConverter 字节数据转换器
     */
    public void setBytesToStringConverter(Function<byte[], String> bytesToStringConverter) {
        this.bytesToStringConverter = bytesToStringConverter;
    }


    /**
     * HTTP协议配置项构建类
     * <p>
     * 提供链式调用方式创建HTTP配置项实例，支持设置所有HTTP相关配置参数。
     * </p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<WebsocketConfigureItem.Builder, WebsocketConfigureItem> {

        private WebsocketConfigureItem configure = new WebsocketConfigureItem();

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
        public WebsocketConfigureItem.Builder ref(String ref) {
            configure.ref = ref;
            return self;
        }

        /**
         * 设置协议
         *
         * @param protocol 协议
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder protocol(String protocol) {
            configure.protocol = protocol;
            return self;
        }

        /**
         * 设置协议
         *
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder ws() {
            configure.protocol = DEFAULT_PROTOCOL;
            return self;
        }

        /**
         * 设置协议
         *
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder wss() {
            configure.protocol = WSS;
            return self;
        }

        /**
         * 设置主机
         *
         * @param host 主机
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder host(String host) {
            configure.host = host;
            return self;
        }

        /**
         * 设置端口
         *
         * @param port 端口
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder port(String port) {
            configure.port = port;
            return self;
        }

        /**
         * 设置路径
         *
         * @param path 路径
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder path(String path) {
            configure.path = path;
            return self;
        }

        /**
         * 设置请求头映射
         *
         * @param headers 请求头映射
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder headers(Map<String, String> headers) {
            configure.headers = Collections.putAllIfNonNull(configure.headers, headers);
            return self;
        }

        /**
         * 通过自定义器设置请求头映射
         *
         * @param customizer 请求头自定义器
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder headers(Customizer<Map<String, String>> customizer) {
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
        public WebsocketConfigureItem.Builder query(Map<String, Object> query) {
            configure.query = Collections.putAllIfNonNull(configure.query, query);
            return self;
        }

        /**
         * 通过自定义器设置查询参数映射
         *
         * @param customizer 查询参数自定义器
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder query(Customizer<Map<String, Object>> customizer) {
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
        public WebsocketConfigureItem.Builder body(Customizer<Map<String, Object>> customizer) {
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
        public WebsocketConfigureItem.Builder body(Map<String, Object> body) {
            if (configure.body != null && !(configure.body instanceof Map)) {
                return self;
            }
            configure.body = Collections.putAllIfNonNull((Map) configure.body, body);
            return self;
        }

        /**
         * 通过自定义器设置请求体映射
         * <p>仅当请求体是Map/List类型时生效</p>
         *
         * @param clazz      请求体类型
         * @param customizer 请求体自定义器
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder body(Class<?> clazz, Customizer<Object> customizer) {
            if (Map.class.isAssignableFrom(clazz)) {
                if (configure.body != null && !(configure.body instanceof Map)) {
                    return self;
                }
                var body = new HashMap<String, Object>();
                customizer.customize(body);
                configure.body = Collections.putAllIfNonNull((Map) configure.body, body);
            } else if (List.class.isAssignableFrom(clazz)) {
                if (configure.body != null && !(configure.body instanceof List)) {
                    return self;
                }
                var body = Collections.newArrayList();
                customizer.customize(body);
                configure.body = Collections.addAllIfNonNull((List) configure.body, body);
            }
            return self;
        }

        /**
         * 设置请求体映射
         * <p>仅当请求体是List类型时生效</p>
         *
         * @param body 请求体映射
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder body(List<Object> body) {
            if (configure.body != null && !(configure.body instanceof List)) {
                return self;
            }
            configure.body = Collections.addAllIfNonNull((List) configure.body, body);
            return self;
        }

        /**
         * 设置请求体对象
         *
         * @param body 请求体对象
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder body(Object body) {
            configure.body = body;
            return self;
        }

        /**
         * 设置字节数据
         *
         * @param bytes 字节数据
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder bytes(byte[] bytes) {
            configure.bytes = bytes;
            return self;
        }

        /**
         * 设置字节数据
         *
         * @param bytes 字节数据
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder bytes(Supplier<byte[]> bytes) {
            configure.bytes = bytes.get();
            return self;
        }

        /**
         * 设置超时时间
         *
         * @param timeout 超时时间
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder timeout(Integer timeout) {
            configure.timeout = timeout;
            return self;
        }

        /**
         * 设置响应数据匹配模式
         *
         * @param responsePattern 响应数据匹配模式
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder responsePattern(String responsePattern) {
            configure.responsePattern = responsePattern;
            return self;
        }

        /**
         * 设置字节数据转换器
         *
         * @param bytesToStringConverter 字节数据转换器
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder bytesToStringConverter(Function<byte[], String> bytesToStringConverter) {
            configure.bytesToStringConverter = bytesToStringConverter;
            return self;
        }

        /**
         * 通过消费者函数配置
         *
         * @param consumer 配置消费者函数
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder config(Consumer<WebsocketConfigureItem.Builder> consumer) {
            var builder = WebsocketConfigureItem.builder();
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
        public WebsocketConfigureItem.Builder config(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = WebsocketConfigureItem.Builder.class) Closure<?> closure) {
            var builder = WebsocketConfigureItem.builder();
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
        public WebsocketConfigureItem.Builder config(WebsocketConfigureItem.Builder builder) {
            configure = configure.merge(builder.build());
            return self;
        }

        /**
         * 通过配置项配置
         *
         * @param config 配置项
         * @return 构建器实例
         */
        public WebsocketConfigureItem.Builder config(WebsocketConfigureItem config) {
            configure = configure.merge(config);
            return self;
        }

        /**
         * 构建HTTP配置项实例
         *
         * @return HTTP配置项实例
         */
        @Override
        public WebsocketConfigureItem build() {
            return configure;
        }
    }
}
