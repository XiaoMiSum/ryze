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

package io.github.xiaomisum.ryze.protocol.rabbit;


import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;

/**
 * RabbitMQ 相关常量接口
 * <p>
 * 该接口定义了 RabbitMQ 测试元素使用的各种常量，包括连接参数、队列配置、交换机配置、消息属性等。
 * 这些常量用于在 JSON 配置文件中标识相应的配置项。
 * </p>
 *
 * @author xiaomi
 * @since 2021/11/11 11:08
 */
public interface RabbitConstantsInterface extends TestElementConstantsInterface {

    /**
     * 默认引用名称键值
     * <p>
     * 当未指定配置引用名称时使用的默认键值
     * </p>
     */
    String DEF_REF_NAME_KEY = "__rabbit_configure_element_default_ref_name__";

    /**
     * 虚拟主机配置键名
     * <p>
     * 用于在配置中指定 RabbitMQ 虚拟主机的键名
     * </p>
     */
    String VIRTUAL_HOST = "virtual_host";

    /**
     * 主机地址配置键名
     * <p>
     * 用于在配置中指定 RabbitMQ 服务器主机地址的键名
     * </p>
     */
    String HOST = "host";

    /**
     * 端口配置键名
     * <p>
     * 用于在配置中指定 RabbitMQ 服务器端口的键名
     * </p>
     */
    String PORT = "port";

    /**
     * 用户名配置键名
     * <p>
     * 用于在配置中指定连接 RabbitMQ 服务器的用户名的键名
     * </p>
     */
    String USERNAME = "username";

    /**
     * 密码配置键名
     * <p>
     * 用于在配置中指定连接 RabbitMQ 服务器的密码的键名
     * </p>
     */
    String PASSWORD = "password";

    /**
     * 超时配置键名
     * <p>
     * 用于在配置中指定连接 RabbitMQ 服务器的超时时间的键名
     * </p>
     */
    String TIMEOUT = "timeout";

    /**
     * 队列配置键名
     * <p>
     * 用于在配置中指定队列相关配置的键名
     * </p>
     */
    String QUEUE_CONFIG = "queue";

    /**
     * 队列名称配置键名
     * <p>
     * 用于在队列配置中指定队列名称的键名
     * </p>
     */
    String QUEUE_NAME = "name";

    /**
     * 队列持久化配置键名
     * <p>
     * 用于在队列配置中指定队列是否持久化的键名
     * </p>
     */
    String QUEUE_DURABLE = "durable";

    /**
     * 队列独占配置键名
     * <p>
     * 用于在队列配置中指定队列是否为独占的键名
     * </p>
     */
    String QUEUE_EXCLUSIVE = "exclusive";

    /**
     * 队列自动删除配置键名
     * <p>
     * 用于在队列配置中指定队列是否在无消费者时自动删除的键名
     * </p>
     */
    String QUEUE_AUTO_DELETE = "auto_delete";

    /**
     * 队列参数配置键名
     * <p>
     * 用于在队列配置中指定队列参数的键名
     * </p>
     */
    String QUEUE_ARGUMENTS = "arguments";

    /**
     * 交换机配置键名
     * <p>
     * 用于在配置中指定交换机相关配置的键名
     * </p>
     */
    String EXCHANGE = "exchange";
    
    /**
     * 交换机名称配置键名
     * <p>
     * 用于在交换机配置中指定交换机名称的键名
     * </p>
     */
    String EXCHANGE_NAME = "name";

    /**
     * 交换机类型配置键名
     * <p>
     * 用于在交换机配置中指定交换机类型的键名
     * </p>
     */
    String EXCHANGE_TYPE = "type";
    
    /**
     * 扇出类型交换机标识
     * <p>
     * 标识交换机类型为扇出（fanout）类型的常量值
     * </p>
     */
    String EXCHANGE_TYPE_FANOUT = "fanout";
    
    /**
     * 直连类型交换机标识
     * <p>
     * 标识交换机类型为直连（direct）类型的常量值
     * </p>
     */
    String EXCHANGE_TYPE_DIRECT = "direct";
    
    /**
     * 主题类型交换机标识
     * <p>
     * 标识交换机类型为主题（topic）类型的常量值
     * </p>
     */
    String EXCHANGE_TYPE_TOPIC = "topic";
    
    /**
     * 头部类型交换机标识
     * <p>
     * 标识交换机类型为头部（headers）类型的常量值
     * </p>
     */
    String EXCHANGE_TYPE_HEADERS = "headers";

    /**
     * 路由键配置键名
     * <p>
     * 用于在交换机配置中指定路由键的键名
     * </p>
     */
    String EXCHANGE_ROUTING_KEY = "routing_key";

    /**
     * 消息属性配置键名
     * <p>
     * 用于在配置中指定消息属性相关配置的键名
     * </p>
     */
    String PROPS = "props";

    /**
     * 内容类型属性键名
     * <p>
     * 用于在消息属性配置中指定内容类型的键名
     * </p>
     */
    String PROPS_CONTENT_TYPE = "content_type";
    
    /**
     * 内容编码属性键名
     * <p>
     * 用于在消息属性配置中指定内容编码的键名
     * </p>
     */
    String PROPS_CONTENT_ENCODING = "content_encoding";
    
    /**
     * 头部属性键名
     * <p>
     * 用于在消息属性配置中指定头部信息的键名
     * </p>
     */
    String PROPS_HEADERS = "headers";
    
    /**
     * 传递模式属性键名
     * <p>
     * 用于在消息属性配置中指定传递模式的键名
     * </p>
     */
    String PROPS_DELIVERY_MODE = "delivery_mode";
    
    /**
     * 优先级属性键名
     * <p>
     * 用于在消息属性配置中指定消息优先级的键名
     * </p>
     */
    String PROPS_PRIORITY = "priority";
    
    /**
     * 关联ID属性键名
     * <p>
     * 用于在消息属性配置中指定关联ID的键名
     * </p>
     */
    String PROPS_CORRELATION_ID = "correlation_id";
    
    /**
     * 回复地址属性键名
     * <p>
     * 用于在消息属性配置中指定回复地址的键名
     * </p>
     */
    String PROPS_REPLY_TO = "reply_to";
    
    /**
     * 过期时间属性键名
     * <p>
     * 用于在消息属性配置中指定消息过期时间的键名
     * </p>
     */
    String PROPS_EXPIRATION = "expiration";
    
    /**
     * 消息ID属性键名
     * <p>
     * 用于在消息属性配置中指定消息ID的键名
     * </p>
     */
    String PROPS_MESSAGE_ID = "message_id";
    
    /**
     * 时间戳属性键名
     * <p>
     * 用于在消息属性配置中指定时间戳的键名
     * </p>
     */
    String PROPS_TIMESTAMP = "timestamp";
    
    /**
     * 类型属性键名
     * <p>
     * 用于在消息属性配置中指定消息类型的键名
     * </p>
     */
    String PROPS_TYPE = "type";
    
    /**
     * 用户ID属性键名
     * <p>
     * 用于在消息属性配置中指定用户ID的键名
     * </p>
     */
    String PROPS_USER_ID = "user_id";
    
    /**
     * 应用ID属性键名
     * <p>
     * 用于在消息属性配置中指定应用ID的键名
     * </p>
     */
    String PROPS_APP_ID = "app_id";
    
    /**
     * 集群ID属性键名
     * <p>
     * 用于在消息属性配置中指定集群ID的键名
     * </p>
     */
    String PROPS_CLUSTER_ID = "cluster_id";

    /**
     * 消息内容配置键名
     * <p>
     * 用于在配置中指定要发送的消息内容的键名
     * </p>
     */
    String MESSAGE = "message";


}