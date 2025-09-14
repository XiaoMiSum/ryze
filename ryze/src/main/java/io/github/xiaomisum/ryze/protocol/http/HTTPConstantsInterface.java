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

package io.github.xiaomisum.ryze.protocol.http;

/**
 * HTTP协议常量接口
 * <p>
 * 该接口定义了HTTP协议相关的核心常量，包括HTTP方法、状态码、请求头字段名、
 * 协议版本等。所有HTTP协议相关的类都应该实现此接口以确保常量的一致性。
 * </p>
 *
 * @author xiaomi
 */
public interface HTTPConstantsInterface {

    /**
     * 默认引用名称键
     * <p>用于在测试上下文中引用默认HTTP配置</p>
     */
    String DEF_REF_NAME_KEY = "__http_configure_element_default_ref_name__";

    /**
     * 请求头键名
     * <p>用于在配置中指定HTTP请求头</p>
     */
    String HEADERS = "headers";

    /**
     * Cookie键名
     * <p>用于在配置中指定HTTP Cookie</p>
     */
    String COOKIE = "cookie";

    /**
     * Cookie名称键名
     */
    String COOKIE_NAME = "name";

    /**
     * Cookie值键名
     */
    String COOKIE_VALUE = "value";

    /**
     * Cookie域键名
     */
    String COOKIE_DOMAIN = "domain";

    /**
     * Cookie路径键名
     */
    String COOKIE_PATH = "path";

    /**
     * HTTP/2协议标识
     */
    String HTTP2 = "http/2";

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
     * 协议键名
     */
    String HTTP = "http";

    /**
     * 协议键名
     */
    String HTTPS = "https";

    /**
     * 基础路径键名
     */
    String BASE_PATH = "base_path";

    /**
     * API路径键名
     */
    String API = "api";

    /**
     * 路径键名
     */
    String PATH = "path";

    /**
     * 请求方法键名
     */
    String REQUEST_METHOD = "method";

    /**
     * 字节数据键名
     */
    String BYTES = "bytes";

    /**
     * 请求体键名
     */
    String BODY = "body";

    /**
     * 表单数据键名
     */
    String DATA = "data";

    /**
     * 二进制文件键名
     */
    String BINARY = "binary";

    /**
     * 查询参数键名
     */
    String QUERY = "query";

    /**
     * 路径分隔符
     */
    String SEPARATOR = "/";

    /**
     * 301 Moved Permanently 状态码
     */
    String SC_MOVED_PERMANENTLY = "301";

    /**
     * 302 Found 状态码
     */
    String SC_MOVED_TEMPORARILY = "302";

    /**
     * 303 See Other 状态码
     */
    String SC_SEE_OTHER = "303";

    /**
     * 307 Temporary Redirect 状态码
     */
    String SC_TEMPORARY_REDIRECT = "307";

    /**
     * HTTPS默认端口
     */
    int DEFAULT_HTTPS_PORT = 443;

    /**
     * HTTPS默认端口字符串表示
     */
    String DEFAULT_HTTPS_PORT_STRING = "443";

    /**
     * HTTP默认端口
     */
    int DEFAULT_HTTP_PORT = 80;

    /**
     * HTTP默认端口字符串表示
     */
    String DEFAULT_HTTP_PORT_STRING = "80";

    /**
     * HTTP协议
     */
    String PROTOCOL_HTTP = "http";

    /**
     * HTTPS协议
     */
    String PROTOCOL_HTTPS = "https";

    /**
     * HEAD方法
     */
    String HEAD = "HEAD";

    /**
     * POST方法
     */
    String POST = "POST";

    /**
     * PUT方法
     */
    String PUT = "PUT";

    /**
     * GET方法
     */
    String GET = "GET";

    /**
     * OPTIONS方法
     */
    String OPTIONS = "OPTIONS";

    /**
     * TRACE方法
     */
    String TRACE = "TRACE";

    /**
     * DELETE方法
     */
    String DELETE = "DELETE";

    /**
     * PATCH方法
     */
    String PATCH = "PATCH";

    /**
     * PROPFIND方法
     */
    String PROPFIND = "PROPFIND";

    /**
     * PROPPATCH方法
     */
    String PROPPATCH = "PROPPATCH";

    /**
     * MKCOL方法
     */
    String MKCOL = "MKCOL";

    /**
     * COPY方法
     */
    String COPY = "COPY";

    /**
     * MOVE方法
     */
    String MOVE = "MOVE";

    /**
     * LOCK方法
     */
    String LOCK = "LOCK";

    /**
     * UNLOCK方法
     */
    String UNLOCK = "UNLOCK";

    /**
     * CONNECT方法
     */
    String CONNECT = "CONNECT";

    /**
     * REPORT方法
     */
    String REPORT = "REPORT";

    /**
     * MKCALENDAR方法
     */
    String MKCALENDAR = "MKCALENDAR";

    /**
     * SEARCH方法
     */
    String SEARCH = "SEARCH";

    /**
     * Authorization请求头
     */
    String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Cookie请求头
     */
    String HEADER_COOKIE = "Cookie";

    /**
     * Cookie请求头（带冒号）
     */
    String HEADER_COOKIE_IN_REQUEST = "Cookie:";

    /**
     * Connection请求头
     */
    String HEADER_CONNECTION = "Connection";

    /**
     * Connection: close 值
     */
    String CONNECTION_CLOSE = "close";

    /**
     * Keep-Alive值
     */
    String KEEP_ALIVE = "keep-alive";

    /**
     * Transfer-Encoding请求头
     */
    String TRANSFER_ENCODING = "transfer-encoding";

    /**
     * Content-Encoding请求头
     */
    String HEADER_CONTENT_ENCODING = "content-encoding";

    /**
     * Set-Cookie响应头
     */
    String HEADER_SET_COOKIE = "set-cookie";

    /**
     * Brotli编码
     */
    String ENCODING_BROTLI = "br";

    /**
     * Deflate编码
     */
    String ENCODING_DEFLATE = "deflate";

    /**
     * Gzip编码
     */
    String ENCODING_GZIP = "gzip";

    /**
     * Content-Disposition响应头
     */
    String HEADER_CONTENT_DISPOSITION = "Content-Disposition";

    /**
     * Content-Type响应头
     */
    String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * Content-Length响应头
     */
    String HEADER_CONTENT_LENGTH = "Content-Length";

    /**
     * Host请求头
     */
    String HEADER_HOST = "Host";

    /**
     * X-LocalAddress请求头
     */
    String HEADER_LOCAL_ADDRESS = "X-LocalAddress";

    /**
     * Location响应头
     */
    String HEADER_LOCATION = "Location";

    /**
     * application/x-www-form-urlencoded MIME类型
     */
    String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    /**
     * multipart/form-data MIME类型
     */
    String MULTIPART_FORM_DATA = "multipart/form-data";

    /**
     * If-None-Match请求头
     */
    String IF_NONE_MATCH = "If-None-Match";

    /**
     * If-Modified-Since请求头
     */
    String IF_MODIFIED_SINCE = "If-Modified-Since";

    /**
     * Etag响应头
     */
    String ETAG = "Etag";

    /**
     * Last-Modified响应头
     */
    String LAST_MODIFIED = "Last-Modified";

    /**
     * Expires响应头
     */
    String EXPIRES = "Expires";

    /**
     * Cache-Control响应头
     */
    String CACHE_CONTROL = "Cache-Control";

    /**
     * Date响应头
     */
    String DATE = "Date";

    /**
     * Vary响应头
     */
    String VARY = "Vary";

}