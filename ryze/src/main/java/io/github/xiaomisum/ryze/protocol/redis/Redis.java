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

package io.github.xiaomisum.ryze.protocol.redis;

import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.protocol.redis.config.RedisDatasource;
import redis.clients.jedis.Protocol;

import java.util.List;

/**
 * Redis协议执行器
 * <p>
 * 该类是Redis协议的核心执行器，负责发送Redis命令并收集响应结果。
 * 使用Jedis客户端库作为底层Redis客户端实现。
 * </p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>执行Redis命令</li>
 *   <li>记录命令执行时间</li>
 *   <li>处理命令执行结果</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 */
public class Redis {

    /**
     * 执行Redis命令并返回结果
     * <p>
     * 该方法会从数据源获取Redis连接，执行指定的Redis命令，并在采样结果中记录执行时间。
     * 支持处理多种类型的返回结果，包括字符串、列表、字节数组等。
     * </p>
     *
     * @param datasource Redis数据源配置
     * @param command Redis命令名称
     * @param args Redis命令参数，以逗号分隔的字符串
     * @param result 采样结果对象，用于记录执行时间
     * @return Redis命令执行结果的字节数组形式
     */
    public static byte[] execute(RedisDatasource datasource, String command, String args, DefaultSampleResult result) {
        result.sampleStart();
        try (var jedis = datasource.getConnection()) {
            var response = jedis.sendCommand(Protocol.Command.valueOf(command), args.split(","));
            return toBytes(response);
        } finally {
            result.sampleEnd();
        }
    }

    /**
     * 将Redis命令执行结果转换为字节数组
     * <p>
     * 根据返回结果的类型进行相应的转换：
     * <ul>
     *   <li>null值：返回空字节数组</li>
     *   <li>列表：转换为JSON数组格式的字符串</li>
     *   <li>字节数组：直接返回</li>
     *   <li>其他类型：转换为字符串后返回其字节数组</li>
     * </ul>
     * </p>
     *
     * @param result Redis命令执行结果
     * @return 结果的字节数组表示
     */
    private static byte[] toBytes(Object result) {
        return switch (result) {
            case null -> new byte[0];
            case List<?> results -> {
                var sb = new StringBuilder("[");
                results.forEach(item -> sb.append(sb.length() > 1 ? "," : "").append(item instanceof String ? "\"" + item + "\"" : item));
                yield sb.append("]").toString().getBytes();
            }
            case byte[] bytes -> bytes;
            default -> result.toString().getBytes();
        };
    }
}