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

package io.github.xiaomisum.ryze.protocol.kafka;

import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.protocol.kafka.config.KafkaConfigureItem;
import org.apache.commons.lang3.StringUtils;

/**
 * Kafka请求实现类，用于格式化和展示Kafka请求信息
 * <p>
 * 该类继承自SampleResult.Real，用于封装和格式化Kafka请求的相关信息，
 * 包括服务器地址、主题、键和消息内容等，提供友好的请求信息展示格式。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>封装Kafka请求信息</li>
 *   <li>格式化请求信息展示</li>
 *   <li>提供请求数据构建方法</li>
 * </ul>
 * </p>
 * <p>
 * 业务处理逻辑：
 * <ol>
 *   <li>存储Kafka请求的基本信息（地址、主题、键等）</li>
 *   <li>根据配置项和消息内容构建请求对象</li>
 *   <li>格式化请求信息用于展示和记录</li>
 * </ol>
 * </p>
 *
 * @author xiaomi
 * @since 2025/7/26 22:24
 * @see SampleResult.Real
 * @see KafkaConfigureItem
 */
public class RealKafkaRequest extends SampleResult.Real {

    /**
     * Kafka服务器地址
     */
    private String address;

    /**
     * Kafka主题名称
     */
    private String topic;

    /**
     * Kafka消息键
     */
    private String key;

    /**
     * 构造函数，使用字节数组初始化请求对象
     *
     * @param bytes 请求内容字节数组
     */
    public RealKafkaRequest(byte[] bytes) {
        super(bytes);
    }

    /**
     * 根据Kafka配置项和消息内容构建RealKafkaRequest实例
     * <p>
     * 这是一个静态工厂方法，用于根据Kafka配置和消息内容创建RealKafkaRequest实例
     * </p>
     *
     * @param config  Kafka配置项
     * @param message 消息内容
     * @return RealKafkaRequest实例
     */
    public static RealKafkaRequest build(KafkaConfigureItem config, String message) {
        var result = new RealKafkaRequest(message.getBytes());
        result.address = config.getBootstrapServers();
        result.topic = config.getTopic();
        result.key = config.getKey();
        return result;
    }

    /**
     * 格式化请求信息用于展示
     * <p>
     * 将Kafka请求的相关信息格式化为易于阅读的字符串格式，
     * 包括服务器地址、主题、键（如果有）和消息内容（如果有）
     * </p>
     *
     * @return 格式化后的请求信息字符串
     */
    @Override
    public String format() {
        var buf = new StringBuilder();
        buf.append("kafka server address: ").append(address).append("\n").append("topic: ").append(topic);
        if (StringUtils.isNotBlank(key)) {
            buf.append("\n").append("key: ").append(key);
        }
        if (StringUtils.isNotBlank(bytesAsString())) {
            buf.append("\n").append("message: ").append(bytesAsString());
        }
        return buf.toString();
    }
}