/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze.protocol.active;

import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.protocol.active.config.ActiveConfigureItem;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import org.apache.commons.lang3.StringUtils;

/**
 * ActiveMQ消息发送核心类，封装了ActiveMQ消息发送的具体实现
 * <p>
 * 该类提供了ActiveMQ消息发送的核心功能，封装了连接创建、会话管理、消息发送和资源释放等操作。
 * 作为工具类，提供了静态方法供其他组件调用。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>创建ActiveMQ连接和会话</li>
 *   <li>根据配置确定消息目标（Topic或Queue）</li>
 *   <li>发送文本消息</li>
 *   <li>自动管理资源（连接、会话、生产者）</li>
 *   <li>处理异常情况</li>
 * </ul>
 * </p>
 * <p>
 * 执行流程：
 * <ol>
 *   <li>创建连接和会话</li>
 *   <li>根据配置确定消息目标（优先使用Queue，否则使用Topic）</li>
 *   <li>创建消息生产者</li>
 *   <li>创建文本消息</li>
 *   <li>发送消息</li>
 *   <li>关闭生产者</li>
 * </ol>
 * </p>
 *
 * @author mi.xiao
 * @since 2021/4/13 20:08
 */
public class Active {

    /**
     * 执行ActiveMQ消息发送操作
     * <p>
     * 该方法完成完整的消息发送流程，包括连接管理、消息发送和资源释放。
     * 使用try-with-resources确保连接和会话能被正确关闭。
     * </p>
     *
     * @param config   ActiveMQ配置项，包含连接参数和消息目标信息
     * @param factory  连接工厂，用于创建ActiveMQ连接
     * @param message  要发送的消息内容
     * @param result   测试结果，用于记录执行时间和结果状态
     * @throws RuntimeException 当消息发送过程中发生异常时抛出
     */
    public static void execute(ActiveConfigureItem config, ConnectionFactory factory, String message, DefaultSampleResult result) {
        MessageProducer producer = null;
        result.sampleStart();
        try (var connection = factory.createConnection(); var session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            var destination = StringUtils.isNotBlank(config.getQueue()) ? session.createQueue(config.getQueue()) : session.createTopic(config.getTopic());
            producer = session.createProducer(destination);
            var textMessage = session.createTextMessage(message);
            producer.send(textMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            result.sampleEnd();
            if (producer != null) {
                try {
                    producer.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}