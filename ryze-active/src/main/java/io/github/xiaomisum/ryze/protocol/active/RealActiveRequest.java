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

package io.github.xiaomisum.ryze.protocol.active;

import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.protocol.active.config.ActiveConfigureItem;
import org.apache.commons.lang3.StringUtils;

/**
 * ActiveMQ实际请求信息类，用于封装和格式化ActiveMQ请求的详细信息
 * <p>
 * 该类继承自SampleResult.Real，用于存储和展示ActiveMQ消息发送的请求信息。
 * 包含了连接参数、目标信息和消息内容等，提供格式化输出功能。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>存储ActiveMQ连接和消息发送的详细信息</li>
 *   <li>提供格式化输出方法，便于查看和记录请求详情</li>
 *   <li>支持通过配置项和消息内容构建实例</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * Created at 2025/7/26 22:24
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class RealActiveRequest extends SampleResult.Real {

    /**
     * ActiveMQ服务器地址
     */
    private String address;

    /**
     * 消息发送目标Topic
     */
    private String topic;

    /**
     * 消息发送目标Queue
     */
    private String queue;

    /**
     * 连接用户名
     */
    private String username;

    /**
     * 连接密码
     */
    private String password;

    /**
     * 构造函数，使用字节数组初始化请求内容
     *
     * @param bytes 请求内容字节数组
     */
    public RealActiveRequest(byte[] bytes) {
        super(bytes);
    }

    /**
     * 根据ActiveMQ配置项和消息内容构建RealActiveRequest实例
     *
     * @param config  ActiveMQ配置项
     * @param message 消息内容
     * @return RealActiveRequest实例
     */
    public static RealActiveRequest build(ActiveConfigureItem config, String message) {
        var result = new RealActiveRequest(message.getBytes());
        result.address = config.getBrokerUrl();
        result.topic = config.getTopic();
        result.queue = config.getQueue();
        result.username = config.getUsername();
        result.password = config.getPassword();
        return result;
    }

    /**
     * 格式化输出请求信息
     * <p>
     * 输出内容包括：
     * <ul>
     *   <li>服务器地址</li>
     *   <li>用户名和密码</li>
     *   <li>Topic（如果设置了）</li>
     *   <li>Queue（如果设置了）</li>
     *   <li>消息内容（如果存在）</li>
     * </ul>
     * </p>
     *
     * @return 格式化后的请求信息字符串
     */
    @Override
    public String format() {
        var buf = new StringBuilder();
        buf.append("kafka server address: ").append(address).append("\n")
                .append("username: ").append(username).append("\n")
                .append("password: ").append(password).append("\n");
        if (StringUtils.isNotBlank(topic)) {
            buf.append("\n").append("topic: ").append(topic);
        }
        if (StringUtils.isNotBlank(queue)) {
            buf.append("\n").append("queue: ").append(queue);
        }
        if (StringUtils.isNotBlank(bytesAsString())) {
            buf.append("\n").append("message: ").append(bytesAsString());
        }
        return buf.toString();
    }
}