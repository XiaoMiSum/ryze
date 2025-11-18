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

package io.github.xiaomisum.ryze.protocol.proto;

/**
 * Proto协议常量接口
 * <p>
 * 该接口定义了Proto协议相关的核心常量，包括Proto方法、状态码、请求头字段名、
 * 协议版本等。所有Proto协议相关的类都应该实现此接口以确保常量的一致性。
 * </p>
 *
 * @author xiaomi
 */
public interface ProtoConstantsInterface {

    /**
     * 默认引用名称键
     * <p>用于在测试上下文中引用默认Proto配置</p>
     */
    String DEF_REF_NAME_KEY = "__proto_configure_element_default_ref_name__";

    /**
     * 请求头Content-Type
     */
    String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * application/x-protobuf MIME类型
     */
    String APPLICATION_X_PROTOBUF = "application/x-protobuf";

    /**
     * 请求头键名
     * <p>用于在配置中指定请求头</p>
     */
    String HEADERS = "headers";

    /**
     * HTTP/2协议标识
     */
    String HTTP2 = "http/2";

    /**
     * 协议键名
     */
    String PROTOCOL = "protocol";

    /**
     * 主机键名
     */
    String HOST = "host";

    /**
     * 端口键名
     */
    String PORT = "port";

    /**
     * 路径键名
     */
    String PATH = "path";

    /**
     * 默认协议： http
     */
    String DEFAULT_PROTOCOL = "http";

    /**
     * https 协议
     */
    String HTTPS = "https";

    /**
     * ws 协议
     */
    String WS = "ws";

    /**
     * wss 协议
     */
    String WSS = "wss";
    /**
     * 请求方法键名
     */
    String REQUEST_METHOD = "method";

    /**
     * GET 方法
     */
    String GET = "GET";

    /**
     * POST 方法
     */
    String POST = "POST";

    /**
     * PUT 方法
     */
    String PUT = "PUT";

    /**
     * DELETE 方法
     */
    String DELETE = "DELETE";

    /**
     * 查询参数键名
     */
    String QUERY = "query";

    /**
     * 请求体键名
     */
    String BODY = "body";

    /**
     * 超时键名
     */
    String TIMEOUT = "timeout";

    /**
     * proto 配置参数名
     * <p>用于在测试时通过配置文件创建请求&响应数据的描述信息</p>
     */
    String PROTO_DESC = "proto";

    /**
     * proto 描述文件路径
     * <p>用于在测试时通过描述文件创建请求&响应数据的描述信息</p>
     */
    String DESC_PATH = "desc_path";

    /**
     * proto 请求消息名
     * <p>用于在测试时通过描述文件创建请求数据的描述信息</p>
     */
    String REQUEST_MESSAGE_NAME = "request_message_name";

    /**
     * proto 响应消息名
     * <p>用于在测试时通过描述文件创建响应数据的描述信息</p>
     */
    String RESPONSE_MESSAGE_NAME = "response_message_name";

    /**
     * 响应数据匹配模式键名
     */
    String RESPONSE_PATTERN = "response_pattern";
}