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

package io.github.xiaomisum.ryze.protocol.redis;

import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * Redis实际请求类
 * <p>
 * 该类封装了Redis请求的详细信息，包括连接URL、命令和参数等。
 * 实现了SampleResult.Real接口，用于在测试报告中展示Redis请求的详细信息。
 * </p>
 *
 * @author xiaomi
 * @since 2025/7/21 19:11
 */
public class RealRedisRequest extends SampleResult.Real implements RedisConstantsInterface {

    /**
     * Redis连接URL
     */
    private final String url;

    /**
     * 构造Redis实际请求对象
     *
     * @param url Redis连接URL
     * @param command Redis命令
     * @param send Redis命令参数
     */
    public RealRedisRequest(String url, String command, String send) {
        super((COMMAND + ": " + command + "\n" + SEND + ": " + send + "\n\n").getBytes(StandardCharsets.UTF_8));
        this.url = url;
    }

    /**
     * 设置 Redis请求信息，格式如下
     * <p>
     * redis(s)://username:password@localhost:3306/db
     * command
     * send
     * <p>
     * data
     */
    @Override
    public String format() {
        return url + (StringUtils.isNotBlank(bytesAsString()) ? "\n" + bytesAsString() : "");
    }

}