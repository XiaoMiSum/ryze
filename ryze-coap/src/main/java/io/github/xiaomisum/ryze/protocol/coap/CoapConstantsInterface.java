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

package io.github.xiaomisum.ryze.protocol.coap;

import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;

/**
 * CoAP协议常量接口
 * <p>
 * 该接口定义了CoAP协议相关的核心常量，包括CoAP方法、内容格式、请求选项等。
 * 所有CoAP协议相关的类都应该实现此接口以确保常量的一致性。
 * </p>
 *
 * @author xiaomi
 */
public interface CoapConstantsInterface extends TestElementConstantsInterface {

    /**
     * 默认引用名称键
     * <p>用于在测试上下文中引用默认CoAP配置</p>
     */
    String DEF_REF_NAME_KEY = "__coap_configure_element_default_ref_name__";

    /**
     * CoAP资源URI键名
     */
    String URI = "uri";

    /**
     * CoAP请求方法键名
     */
    String METHOD = "method";

    /**
     * 内容格式键名
     */
    String CONTENT_FORMAT = "content_format";

    /**
     * 请求体键名
     */
    String PAYLOAD = "payload";

    /**
     * 是否使用CON消息键名
     */
    String CONFIRMABLE = "confirmable";

    /**
     * 是否观察资源键名
     */
    String OBSERVE = "observe";

    /**
     * 超时时间键名
     */
    String TIMEOUT = "timeout";

    /**
     * 期望响应格式键名
     */
    String ACCEPT = "accept";

    /**
     * 块大小键名
     */
    String BLOCK_SIZE = "block_size";

    /**
     * 是否启用DTLS键名
     */
    String DTLS_ENABLED = "dtls_enabled";

    /**
     * CoAP默认端口
     */
    int DEFAULT_PORT = 5683;

    /**
     * CoAP安全端口（DTLS）
     */
    int DEFAULT_SECURE_PORT = 5684;

    /**
     * 默认超时时间（毫秒）
     */
    int DEFAULT_TIMEOUT = 10000;

    /**
     * CoAP GET方法
     */
    String METHOD_GET = "GET";

    /**
     * CoAP POST方法
     */
    String METHOD_POST = "POST";

    /**
     * CoAP PUT方法
     */
    String METHOD_PUT = "PUT";

    /**
     * CoAP DELETE方法
     */
    String METHOD_DELETE = "DELETE";

    /**
     * 默认内容格式
     */
    String DEFAULT_CONTENT_FORMAT = "application/json";
}
