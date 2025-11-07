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

package io.github.xiaomisum.ryze.protocol.websocket;

import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;

/**
 * Websocket协议常量接口
 * <p>
 * 该接口定义了Websocket协议相关的核心常量，包括Websocket方法、状态码、请求头字段名、
 * 协议版本等。所有Websocket协议相关的类都应该实现此接口以确保常量的一致性。
 * </p>
 *
 * @author xiaomi
 */
public interface WebsocketConstantsInterface extends TestElementConstantsInterface {

    /**
     * 默认引用名称键
     * <p>用于在测试上下文中引用默认Websocket配置</p>
     */
    String DEF_REF_NAME_KEY = "__websocket_configure_element_default_ref_name__";

    /**
     * 请求头键名
     * <p>用于在配置中指定Websocket请求头</p>
     */
    String HEADERS = "headers";

    /**
     * 主机键名
     */
    String HOST = "host";

    /**
     * 端口键名
     */
    String PORT = "port";

    /**
     * 协议键名
     */
    String PROTOCOL = "protocol";

    /**
     * 路径键名
     */
    String PATH = "path";

    /**
     * 字节数据键名
     */
    String BYTES = "bytes";

    /**
     * 请求体键名
     */
    String BODY = "body";

    /**
     * 查询参数键名
     */
    String QUERY = "query";

    /**
     * 响应数据匹配模式键名
     */
    String RESPONSE_PATTERN = "response_pattern";

    /**
     * 路径分隔符
     */
    String SEPARATOR = "/";


    /**
     * WSS默认端口
     */
    int DEFAULT_WSS_PORT = 443;


    /**
     * WS默认端口
     */
    int DEFAULT_WS_PORT = 80;

    /**
     * 默认协议
     */
    String DEFAULT_PROTOCOL = "ws";

    /**
     * 默认协议
     */
    String WSS = "wss";

}