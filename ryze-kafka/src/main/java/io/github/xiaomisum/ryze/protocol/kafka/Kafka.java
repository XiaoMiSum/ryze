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

package io.github.xiaomisum.ryze.protocol.kafka;

import io.github.xiaomisum.ryze.testelement.sampler.DefaultSampleResult;
import io.github.xiaomisum.ryze.protocol.kafka.config.KafkaConfigureItem;
import io.github.xiaomisum.ryze.support.Collections;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;

import static io.github.xiaomisum.ryze.protocol.kafka.KafkaConstantsInterface.*;

/**
 * Kafka工具类，提供Kafka消息发送的核心执行逻辑
 * <p>
 * 该类是Kafka协议测试的核心执行类，封装了Kafka消息发送的具体实现逻辑，
 * 使用Apache Kafka客户端库执行实际的消息发送操作。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>执行Kafka消息发送操作</li>
 *   <li>管理Kafka生产者实例</li>
 *   <li>处理消息发送结果</li>
 * </ul>
 * </p>
 * <p>
 * 业务处理逻辑：
 * <ol>
 *   <li>根据配置项构建Kafka生产者属性</li>
 *   <li>创建KafkaProducer实例</li>
 *   <li>构建ProducerRecord消息记录</li>
 *   <li>发送消息并等待结果</li>
 *   <li>返回发送结果偏移量</li>
 *   <li>处理异常情况</li>
 * </ol>
 * </p>
 *
 * @author xiaomi
 * @see KafkaProducer
 * @see ProducerRecord
 * @see KafkaConfigureItem
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Kafka {

    /**
     * 执行Kafka消息发送操作
     * <p>
     * 该方法根据提供的配置项和消息内容，使用Kafka生产者发送消息到指定的主题，
     * 并返回发送结果的偏移量信息。整个过程包含完整的异常处理机制。
     * </p>
     *
     * @param config Kafka配置项，包含服务器地址、主题、键、序列化器等配置信息
     * @param message 要发送的消息内容
     * @param result  测试结果对象，用于记录测试执行时间等信息
     * @return 发送结果的偏移量信息字节数组
     * @throws RuntimeException 当消息发送过程中发生异常时抛出
     */
    public static byte[] execute(KafkaConfigureItem config, String message, DefaultSampleResult result) {
        var props = Collections.of(BOOTSTRAP_SERVERS, config.getBootstrapServers(), ACKS, config.getAcks().toString(), RETRIES, config.getRetries(),
                LINGER_MS, config.getLingerMs(), KEY_SERIALIZER, config.getKeySerializer(), VALUE_SERIALIZER, config.getValueSerializer());
        try (var producer = new KafkaProducer<String, String>(props)) {
            result.sampleStart();
            var record = new ProducerRecord<>(config.getTopic(), config.getKey(), message);
            var future = producer.send(record);
            return ("offset: " + future.get().offset()).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            result.sampleEnd();
        }
    }

}