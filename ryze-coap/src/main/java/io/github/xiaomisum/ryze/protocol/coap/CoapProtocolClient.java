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

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

/**
 * CoAP协议客户端封装
 * <p>
 * 基于Eclipse Californium库封装的CoAP客户端，支持GET/POST/PUT/DELETE四种方法。
 * </p>
 *
 * @author xiaomi
 */
public class CoapProtocolClient {

    private CoapProtocolClient() {
    }

    /**
     * 发送CoAP请求并同步等待响应
     *
     * @param method      请求方法 (GET/POST/PUT/DELETE)
     * @param uriStr      资源URI
     * @param payload     请求体
     * @param confirmable 是否使用CON消息
     * @param timeout     超时毫秒数
     * @return CoAP响应对象
     */
    public static CoapResponse sendRequest(String method, String uriStr, String payload, boolean confirmable, int timeout) {
        CoapClient client = null;
        try {
            client = new CoapClient(uriStr);
            client.setTimeout((long) timeout);

            // 设置消息类型：CON（可靠）或 NON（不可靠）
            if (confirmable) {
                client.useCONs();
            } else {
                client.useNONs();
            }

            // 根据方法类型发送请求
            CoapResponse response = switch (method.toUpperCase()) {
                case "GET" -> client.get();
                case "POST" -> client.post(
                        payload != null ? payload : "",
                        MediaTypeRegistry.APPLICATION_JSON
                );
                case "PUT" -> client.put(
                        payload != null ? payload : "",
                        MediaTypeRegistry.APPLICATION_JSON
                );
                case "DELETE" -> client.delete();
                default -> throw new IllegalArgumentException("Unsupported CoAP method: " + method);
            };

            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("CoAP request failed: " + e.getMessage(), e);
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }
}
