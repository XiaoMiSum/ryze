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

package io.github.xiaomisum.ryze.protocol.rabbit;

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.protocol.rabbit.config.RabbitConfigureItem;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * RabbitMQ 实际请求对象
 * <p>
 * 该类用于封装和格式化 RabbitMQ 请求的相关信息，包括服务器地址、认证信息、
 * 队列配置、交换机配置以及消息内容等，用于在测试结果中展示请求详情。
 * </p>
 *
 * @author xiaomi
 * @since 2025/7/26 22:24
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class RealRabbitRequest extends SampleResult.Real {

    /**
     * RabbitMQ 服务器地址
     */
    private String address;

    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 虚拟主机
     */
    private String virtualHost;

    /**
     * 队列配置
     */
    private RabbitConfigureItem.Queue queue;

    /**
     * 交换机配置
     */
    private RabbitConfigureItem.Exchange exchange;

    /**
     * 构造函数，使用字节数组初始化请求对象
     *
     * @param bytes 请求内容的字节数组表示
     */
    public RealRabbitRequest(byte[] bytes) {
        super(bytes);
    }

    /**
     * 根据 RabbitMQ 配置和消息内容构建 RealRabbitRequest 对象
     * <p>
     * 该方法用于创建 RealRabbitRequest 实例，并根据配置信息填充相关字段。
     * </p>
     *
     * @param config  RabbitMQ 配置项
     * @param message 消息内容
     * @return 构建好的 RealRabbitRequest 对象
     */
    public static RealRabbitRequest build(RabbitConfigureItem config, String message) {
        var result = new RealRabbitRequest(message.getBytes());
        result.address = config.getHost() + ":" + config.getPort();
        result.username = config.getUsername();
        result.password = config.getPassword();
        result.queue = config.getQueue();
        result.exchange = config.getExchange();
        result.virtualHost = config.getVirtualHost();
        return result;
    }

    /**
     * 格式化请求信息
     * <p>
     * 将 RabbitMQ 请求的相关信息格式化为字符串，包括服务器地址、认证信息、
     * 队列配置、交换机配置以及消息内容等。
     * </p>
     *
     * @return 格式化后的请求信息字符串
     */
    @Override
    public String format() {
        var buf = new StringBuilder();
        buf.append("rabbit server address: ").append(address).append("\n")
                .append("username: ").append(username).append("\n")
                .append("password: ").append(password).append("\n")
                .append("virtualHost: ").append(virtualHost).append("\n");
        if (Objects.nonNull(queue)) {
            buf.append("Queue as JSON: ").append(JSON.toJSONString(queue)).append("\n");
        }
        if (Objects.nonNull(exchange)) {
            buf.append("Exchange as JSON: ").append(JSON.toJSONString(exchange)).append("\n");
        }
        if (StringUtils.isNotBlank(bytesAsString())) {
            buf.append("\n").append("message: ").append(bytesAsString());
        }
        return buf.toString();
    }
}