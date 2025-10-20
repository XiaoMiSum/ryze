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

package io.github.xiaomisum.ryze.protocol.http.config;

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.protocol.http.HTTPConstantsInterface;
import io.github.xiaomisum.ryze.protocol.http.processor.HTTPPostprocessor;
import io.github.xiaomisum.ryze.protocol.http.processor.HTTPPreprocessor;
import io.github.xiaomisum.ryze.protocol.http.sampler.HTTPSampler;
import io.github.xiaomisum.ryze.support.fastjson.interceptor.JSONInterceptor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP协议JSON拦截器
 * <p>
 * 该类用于处理HTTP协议相关测试元件的JSON反序列化过程。
 * 主要功能是将JSON配置转换为对应的HTTP配置项对象，并处理配置项的兼容性问题。
 * </p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>支持HTTP取样器、数据源、前置/后置处理器的反序列化</li>
 *   <li>处理旧版本配置项的兼容性转换（如statement转sql）</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @since 2025/7/21 22:25
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class HTTPJSONInterceptor implements JSONInterceptor, HTTPConstantsInterface {

    /**
     * 获取支持的类列表
     * <p>
     * 返回该拦截器支持处理的类列表，包括：
     * <ul>
     *   <li>HTTPSampler - HTTP取样器</li>
     *   <li>HTTPDefaults - HTTP数据源</li>
     *   <li>HTTPPreprocessor - HTTP前置处理器</li>
     *   <li>HTTPPostprocessor - HTTP后置处理器</li>
     * </ul>
     * </p>
     *
     * @return 支持的类列表
     */
    @Override
    public List<Class<?>> getSupportedClasses() {
        return List.of(HTTPSampler.class, HTTPDefaults.class, HTTPPreprocessor.class, HTTPPostprocessor.class);
    }

    /**
     * 反序列化配置项
     * <p>
     * 将JSON对象转换为HTTP配置项对象。处理旧版本配置项的兼容性问题，
     * 将废弃的"api"字段转换为"path"字段。
     * </p>
     *
     * @param value JSON对象
     * @return HTTP配置项对象
     */
    @Override
    public ConfigureItem<?> deserializeConfigureItem(Object value) {
        if (value instanceof Map configure) {
            var path = configure.remove(API);
            if (Objects.nonNull(path) && StringUtils.isNotBlank(path.toString())) {
                configure.put(PATH, path);
            }
            var rawData = JSON.toJSONString(configure);
            return JSON.parseObject(rawData, HTTPConfigureItem.class);
        }
        return null;
    }
}