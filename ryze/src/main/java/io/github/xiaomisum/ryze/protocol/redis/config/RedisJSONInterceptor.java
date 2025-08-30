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

package io.github.xiaomisum.ryze.protocol.redis.config;

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.protocol.redis.RedisConstantsInterface;
import io.github.xiaomisum.ryze.protocol.redis.processor.RedisPostprocessor;
import io.github.xiaomisum.ryze.protocol.redis.processor.RedisPreprocessor;
import io.github.xiaomisum.ryze.protocol.redis.sampler.RedisSampler;
import io.github.xiaomisum.ryze.support.fastjson.interceptor.JSONInterceptor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Redis协议JSON拦截器
 * <p>
 * 该类用于处理Redis协议相关测试元件的JSON反序列化过程。
 * 主要功能是将JSON配置转换为对应的Redis配置项对象，并处理配置项的兼容性问题。
 * </p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>支持Redis取样器、数据源、前置/后置处理器的反序列化</li>
 *   <li>处理旧版本配置项的兼容性转换</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @since 2025/7/21 22:25
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class RedisJSONInterceptor implements JSONInterceptor, RedisConstantsInterface {

    /**
     * 获取支持的类列表
     * <p>
     * 返回该拦截器支持处理的类列表，包括：
     * <ul>
     *   <li>RedisSampler - Redis取样器</li>
     *   <li>RedisDatasource - Redis数据源</li>
     *   <li>RedisPreprocessor - Redis前置处理器</li>
     *   <li>RedisPostprocessor - Redis后置处理器</li>
     * </ul>
     * </p>
     *
     * @return 支持的类列表
     */
    @Override
    public List<Class<?>> getSupportedClasses() {
        return List.of(RedisSampler.class, RedisDatasource.class, RedisPreprocessor.class, RedisPostprocessor.class);
    }

    /**
     * 反序列化配置项
     * <p>
     * 将JSON对象转换为Redis配置项对象。处理旧版本配置项的兼容性问题，
     * 将分散的主机、端口等字段合并为URL字段。
     * </p>
     *
     * @param value JSON对象
     * @return Redis配置项对象
     */
    @Override
    public ConfigureItem<?> deserializeConfigureItem(Object value) {
        if (value instanceof Map configure) {
            standardizeConfig(configure);
            var rawData = JSON.toJSONString(configure);
            return JSON.parseObject(rawData, RedisConfigureItem.class);
        }
        return null;
    }


    /**
     * 标准化配置
     * <p>
     * 如果配置中没有URL字段，则根据HOST、PORT、USERNAME、PASSWORD、DATABASE字段
     * 构造标准的Redis URL格式：redis://[username:password@]host[:port][/db]
     * </p>
     *
     * @param testElementMap 测试元件映射表
     */
    private void standardizeConfig(Map<String, Object> testElementMap) {
        if (StringUtils.isNotBlank((String) testElementMap.get(URL))) {
            return;
        }
        var host = testElementMap.remove(HOST);
        var port = testElementMap.remove(PORT);
        var username = testElementMap.remove(USERNAME);
        var password = testElementMap.remove(PASSWORD);
        var database = testElementMap.remove(DATABASE);
        var url = REDIS_URL_TEMPLATE +
                (Objects.isNull(username) ? "" : username) +
                (Objects.isNull(password) ? "" : ":" + password + "@") +
                host +
                (Objects.isNull(port) ? "" : ":" + port) +
                (Objects.isNull(database) ? "" : "/" + database);
        testElementMap.put(URL, url);
    }
}