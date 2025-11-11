/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * 'Software'), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.xiaomisum.ryze.protocol.proto;

import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP实际结果抽象基类
 * <p>
 * 该类是HTTP请求和响应结果的抽象基类，提供了HTTP协议特有的属性和方法。
 * 包含HTTP版本信息和响应头列表，为HTTP请求和响应结果提供统一的处理接口。
 * </p>
 *
 * @author xiaomi
 */
public abstract class ProtoRealResult extends SampleResult.Real {

    /**
     * HTTP协议版本
     * <p>例如："HTTP/1.1" 或 "HTTP/2"</p>
     */
    protected String version;

    /**
     * HTTP响应头列表
     * <p>包含响应中的所有HTTP头信息</p>
     */
    protected Map<String, String> headers = new HashMap<>();

    /**
     * 构造HTTP实际结果对象
     *
     * @param bytes 结果字节数组
     */
    public ProtoRealResult(byte[] bytes) {
        super(bytes);
    }

    /**
     * 添加响应头信息到字符串缓冲区
     * <p>将所有响应头以"名称: 值"的格式添加到缓冲区中</p>
     *
     * @param buf 字符串缓冲区
     */
    protected void header(StringBuilder buf) {
        if (headers == null || headers.isEmpty()) {
            return;
        }
        buf.append("\n");
        headers.forEach((key, value) -> buf.append(key).append(": ").append(value).append("\n"));
    }
}