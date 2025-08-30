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

package io.github.xiaomisum.ryze.protocol.mongo;

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.testelement.sampler.SampleResult;
import io.github.xiaomisum.ryze.protocol.mongo.config.MongoConfigItem;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * MongoDB 真实请求信息类
 * <p>
 * 该类用于封装和格式化 MongoDB 操作的请求信息，包括连接地址、数据库名、集名称、操作类型和条件等。
 * 它继承自 SampleResult.Real，提供了将 MongoDB 配置项转换为可读格式的请求信息的功能。
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>存储 MongoDB 操作的详细信息</li>
 *   <li>提供请求信息的格式化输出</li>
 *   <li>支持从 MongoConfigItem 构建实例</li>
 * </ul>
 * </p>
 */
public class MongoRealRequest extends SampleResult.Real {


    /**
     * MongoDB 连接地址
     */
    private String url;
    
    /**
     * 数据库名称
     */
    private String database;
    
    /**
     * 集合名称
     */
    private String collection;
    
    /**
     * 操作类型（find、insert、update、delete）
     */
    private String action;
    
    /**
     * 操作条件
     */
    private Map<String, Object> condition;

    /**
     * 构造方法，根据字节数组创建 MongoRealRequest 实例
     *
     * @param bytes 字节数组
     */
    public MongoRealRequest(byte[] bytes) {
        super(bytes);
    }

    /**
     * 根据 MongoConfigItem 构建 MongoRealRequest 实例
     * <p>
     * 该静态方法将 MongoConfigItem 配置信息转换为 MongoRealRequest 实例，
     * 用于在采样结果中展示请求信息。
     * </p>
     *
     * @param configure MongoDB 配置项
     * @return MongoRealRequest 实例
     */
    public static MongoRealRequest build(MongoConfigItem configure) {
        var result = new MongoRealRequest(Objects.isNull(configure.getData()) ? new byte[0] :
                JSON.toJSONBytes(configure.getData()));
        result.url = configure.getUrl();
        result.database = configure.getDatabase();
        result.collection = configure.getCollection();
        result.action = configure.getAction();
        result.condition = configure.getCondition();
        return result;
    }

    /**
     * 格式化请求信息
     * <p>
     * 将 MongoDB 操作的详细信息格式化为可读的字符串，包括连接地址、数据库名、
     * 集合名、操作类型、条件和数据等信息。
     * </p>
     *
     * @return 格式化后的请求信息字符串
     */
    @Override
    public String format() {

        var buf = new StringBuilder();
        buf.append(url).append("\n")
                .append("database: ").append(database).append("\n")
                .append("collection: ").append(collection).append("\n")
                .append("action: ").append(action).append("\n");
        if (Objects.nonNull(condition)) {
            buf.append("Condition as JSON: ").append(JSON.toJSONString(condition)).append("\n");
        }
        if (StringUtils.isNotBlank(bytesAsString())) {
            buf.append("\n").append("Data: ").append(bytesAsString());
        }
        return buf.toString();
    }
}