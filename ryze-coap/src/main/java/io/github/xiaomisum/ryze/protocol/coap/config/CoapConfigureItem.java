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

package io.github.xiaomisum.ryze.protocol.coap.config;

import com.alibaba.fastjson2.annotation.JSONField;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.protocol.coap.CoapConstantsInterface;
import io.github.xiaomisum.ryze.support.Customizer;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

import static io.github.xiaomisum.ryze.support.groovy.Groovy.call;

/**
 * CoAP协议配置项类
 * <p>
 * 该类封装了CoAP协议的所有配置参数，包括URI、方法、内容格式、
 * 请求体、确认模式、观察模式、超时等。实现了ConfigureItem接口，
 * 支持配置项的合并和变量求值。
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class CoapConfigureItem implements ConfigureItem<CoapConfigureItem>, CoapConstantsInterface {

    /**
     * 引用名称
     */
    @JSONField(name = REF)
    protected String ref;

    /**
     * CoAP资源URI
     * <p>例如: "coap://host:port/path"</p>
     */
    @JSONField(name = URI, ordinal = 1)
    protected String uri;

    /**
     * 请求方法
     * <p>GET/POST/PUT/DELETE</p>
     */
    @JSONField(name = METHOD, ordinal = 2)
    protected String method;

    /**
     * 内容格式
     * <p>如 "application/json", "text/plain"</p>
     */
    @JSONField(name = CONTENT_FORMAT, ordinal = 3)
    protected String contentFormat;

    /**
     * 请求体
     */
    @JSONField(name = PAYLOAD, ordinal = 4)
    protected String payload;

    /**
     * 是否使用CON消息
     * <p>默认为true</p>
     */
    @JSONField(name = CONFIRMABLE, ordinal = 5)
    protected Boolean confirmable;

    /**
     * 是否观察资源
     * <p>默认为false</p>
     */
    @JSONField(name = OBSERVE, ordinal = 6)
    protected Boolean observe;

    /**
     * 超时时间（毫秒）
     * <p>默认10000</p>
     */
    @JSONField(name = TIMEOUT, ordinal = 7)
    protected Integer timeout;

    /**
     * 期望的响应格式
     */
    @JSONField(name = ACCEPT, ordinal = 8)
    protected String accept;

    /**
     * 块大小
     */
    @JSONField(name = BLOCK_SIZE, ordinal = 9)
    protected Integer blockSize;

    /**
     * 是否启用DTLS
     */
    @JSONField(name = DTLS_ENABLED, ordinal = 10)
    protected Boolean dtlsEnabled;

    /**
     * 默认构造函数
     */
    public CoapConfigureItem() {
    }

    /**
     * 创建CoAP配置项构建器
     *
     * @return CoAP配置项构建器实例
     */
    public static CoapConfigureItem.Builder builder() {
        return new CoapConfigureItem.Builder();
    }

    @Override
    public CoapConfigureItem merge(CoapConfigureItem other) {
        if (other == null) {
            return copy();
        }
        var localOther = other.copy();
        var self = copy();
        self.uri = StringUtils.isBlank(self.uri) ? localOther.uri : self.uri;
        self.method = StringUtils.isBlank(self.method) ? localOther.method : self.method;
        self.contentFormat = StringUtils.isBlank(self.contentFormat) ? localOther.contentFormat : self.contentFormat;
        self.payload = StringUtils.isBlank(self.payload) ? localOther.payload : self.payload;
        self.confirmable = self.confirmable == null ? localOther.confirmable : self.confirmable;
        self.observe = self.observe == null ? localOther.observe : self.observe;
        self.timeout = self.timeout == null ? localOther.timeout : self.timeout;
        self.accept = StringUtils.isBlank(self.accept) ? localOther.accept : self.accept;
        self.blockSize = self.blockSize == null ? localOther.blockSize : self.blockSize;
        self.dtlsEnabled = self.dtlsEnabled == null ? localOther.dtlsEnabled : self.dtlsEnabled;
        return self;
    }

    @Override
    public CoapConfigureItem evaluate(ContextWrapper context) {
        ref = (String) context.evaluate(ref);
        uri = (String) context.evaluate(uri);
        method = (String) context.evaluate(method);
        contentFormat = (String) context.evaluate(contentFormat);
        payload = (String) context.evaluate(payload);
        accept = (String) context.evaluate(accept);
        return this;
    }

    public CoapConfigureItem copy() {
        var item = new CoapConfigureItem();
        item.ref = this.ref;
        item.uri = this.uri;
        item.method = this.method;
        item.contentFormat = this.contentFormat;
        item.payload = this.payload;
        item.confirmable = this.confirmable;
        item.observe = this.observe;
        item.timeout = this.timeout;
        item.accept = this.accept;
        item.blockSize = this.blockSize;
        item.dtlsEnabled = this.dtlsEnabled;
        return item;
    }

    public String getRef() {
        return StringUtils.isBlank(ref) ? DEF_REF_NAME_KEY : ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return StringUtils.isBlank(method) ? METHOD_GET : method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContentFormat() {
        return StringUtils.isBlank(contentFormat) ? DEFAULT_CONTENT_FORMAT : contentFormat;
    }

    public void setContentFormat(String contentFormat) {
        this.contentFormat = contentFormat;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean isConfirmable() {
        return confirmable == null || confirmable;
    }

    public void setConfirmable(Boolean confirmable) {
        this.confirmable = confirmable;
    }

    public boolean isObserve() {
        return observe != null && observe;
    }

    public void setObserve(Boolean observe) {
        this.observe = observe;
    }

    public int getTimeout() {
        return timeout == null ? DEFAULT_TIMEOUT : timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public Integer getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(Integer blockSize) {
        this.blockSize = blockSize;
    }

    public boolean isDtlsEnabled() {
        return dtlsEnabled != null && dtlsEnabled;
    }

    public void setDtlsEnabled(Boolean dtlsEnabled) {
        this.dtlsEnabled = dtlsEnabled;
    }

    /**
     * CoAP协议配置项构建类
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<CoapConfigureItem.Builder, CoapConfigureItem> {

        private CoapConfigureItem configure = new CoapConfigureItem();

        public Builder() {
        }

        public CoapConfigureItem.Builder ref(String ref) {
            configure.ref = ref;
            return self;
        }

        public CoapConfigureItem.Builder uri(String uri) {
            configure.uri = uri;
            return self;
        }

        public CoapConfigureItem.Builder method(String method) {
            configure.method = method;
            return self;
        }

        public CoapConfigureItem.Builder get() {
            configure.method = METHOD_GET;
            return self;
        }

        public CoapConfigureItem.Builder post() {
            configure.method = METHOD_POST;
            return self;
        }

        public CoapConfigureItem.Builder put() {
            configure.method = METHOD_PUT;
            return self;
        }

        public CoapConfigureItem.Builder delete() {
            configure.method = METHOD_DELETE;
            return self;
        }

        public CoapConfigureItem.Builder contentFormat(String contentFormat) {
            configure.contentFormat = contentFormat;
            return self;
        }

        public CoapConfigureItem.Builder payload(String payload) {
            configure.payload = payload;
            return self;
        }

        public CoapConfigureItem.Builder confirmable(boolean confirmable) {
            configure.confirmable = confirmable;
            return self;
        }

        public CoapConfigureItem.Builder observe(boolean observe) {
            configure.observe = observe;
            return self;
        }

        public CoapConfigureItem.Builder timeout(int timeout) {
            configure.timeout = timeout;
            return self;
        }

        public CoapConfigureItem.Builder accept(String accept) {
            configure.accept = accept;
            return self;
        }

        public CoapConfigureItem.Builder blockSize(int blockSize) {
            configure.blockSize = blockSize;
            return self;
        }

        public CoapConfigureItem.Builder dtlsEnabled(boolean dtlsEnabled) {
            configure.dtlsEnabled = dtlsEnabled;
            return self;
        }

        public CoapConfigureItem.Builder config(Consumer<CoapConfigureItem.Builder> consumer) {
            var builder = CoapConfigureItem.builder();
            consumer.accept(builder);
            configure = configure.merge(builder.build());
            return self;
        }

        public CoapConfigureItem.Builder config(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = CoapConfigureItem.Builder.class) Closure<?> closure) {
            var builder = CoapConfigureItem.builder();
            call(closure, builder);
            configure = configure.merge(builder.build());
            return self;
        }

        public CoapConfigureItem.Builder config(CoapConfigureItem.Builder builder) {
            configure = configure.merge(builder.build());
            return self;
        }

        public CoapConfigureItem.Builder config(CoapConfigureItem config) {
            configure = configure.merge(config);
            return self;
        }

        @Override
        public CoapConfigureItem build() {
            return configure;
        }
    }
}
