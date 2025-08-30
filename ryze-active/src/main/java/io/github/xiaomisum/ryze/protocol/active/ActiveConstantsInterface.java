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

import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;

/**
 * ActiveMQ协议相关常量接口
 * <p>
 * 该接口定义了ActiveMQ协议测试组件所需的各种常量，包括配置参数键名和默认引用名称等。
 * 所有ActiveMQ协议相关的类都应该实现此接口以保持常量的一致性。
 * </p>
 *
 * @author xiaomi
 */
public interface ActiveConstantsInterface extends TestElementConstantsInterface {

    /**
     * ActiveMQ配置元件默认引用名称键
     * <p>
     * 用于标识ActiveMQ配置元件的默认引用名称，在测试框架中用于引用和查找配置元件实例
     * </p>
     */
    String DEF_REF_NAME_KEY = "__active_mq_configure_element_default_ref_name__";

    /**
     * ActiveMQ用户名配置键
     * <p>
     * 用于在配置中指定连接ActiveMQ服务器所需的用户名
     * </p>
     */
    String ACTIVEMQ_USERNAME = "username";

    /**
     * ActiveMQ密码配置键
     * <p>
     * 用于在配置中指定连接ActiveMQ服务器所需的密码
     * </p>
     */
    String ACTIVEMQ_PASSWORD = "password";

    /**
     * ActiveMQ Broker URL配置键
     * <p>
     * 用于在配置中指定ActiveMQ服务器的连接地址
     * </p>
     */
    String BROKER_URL = "broker.url";

    /**
     * ActiveMQ消息内容配置键
     * <p>
     * 用于在配置中指定要发送或接收的ActiveMQ消息内容
     * </p>
     */
    String ACTIVEMQ_MESSAGE = "message";

    /**
     * ActiveMQ主题配置键
     * <p>
     * 用于在配置中指定ActiveMQ的主题名称，用于发布/订阅模式
     * </p>
     */
    String ACTIVEMQ_TOPIC = "topic";

    /**
     * ActiveMQ队列配置键
     * <p>
     * 用于在配置中指定ActiveMQ的队列名称，用于点对点模式
     * </p>
     */
    String ACTIVEMQ_QUEUE = "queue";

}