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

package io.github.xiaomisum.ryze.protocol.jdbc.config;

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.protocol.jdbc.JDBCConstantsInterface;
import io.github.xiaomisum.ryze.protocol.jdbc.processor.JDBCPostprocessor;
import io.github.xiaomisum.ryze.protocol.jdbc.processor.JDBCPreprocessor;
import io.github.xiaomisum.ryze.protocol.jdbc.sampler.JDBCSampler;
import io.github.xiaomisum.ryze.support.fastjson.interceptor.JSONInterceptor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JDBC协议JSON拦截器
 * <p>
 * 该类用于处理JDBC协议相关测试元件的JSON反序列化过程。
 * 主要功能是将JSON配置转换为对应的JDBC配置项对象，并处理配置项的兼容性问题。
 * </p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>支持JDBC取样器、数据源、前置/后置处理器的反序列化</li>
 *   <li>处理旧版本配置项的兼容性转换（如statement转sql）</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @since 2025/7/21 22:25
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class JDBCJSONInterceptor implements JSONInterceptor, JDBCConstantsInterface {

    /**
     * 获取支持的类列表
     * <p>
     * 返回该拦截器支持处理的类列表，包括：
     * <ul>
     *   <li>JDBCSampler - JDBC取样器</li>
     *   <li>JDBCDatasource - JDBC数据源</li>
     *   <li>JDBCPreprocessor - JDBC前置处理器</li>
     *   <li>JDBCPostprocessor - JDBC后置处理器</li>
     * </ul>
     * </p>
     *
     * @return 支持的类列表
     */
    @Override
    public List<Class<?>> getSupportedClasses() {
        return List.of(JDBCSampler.class, JDBCDatasource.class, JDBCPreprocessor.class, JDBCPostprocessor.class);
    }

    /**
     * 反序列化配置项
     * <p>
     * 将JSON对象转换为JDBC配置项对象。处理旧版本配置项的兼容性问题，
     * 将废弃的"statement"字段转换为"sql"字段。
     * </p>
     *
     * @param value JSON对象
     * @return JDBC配置项对象
     */
    @Override
    public ConfigureItem<?> deserializeConfigureItem(Object value) {
        if (value instanceof Map configure) {
            var statement = configure.remove(STATEMENT);
            if (Objects.nonNull(statement)) {
                configure.put(SQL, statement);
            }
            var rawData = JSON.toJSONString(configure);
            return JSON.parseObject(rawData, JDBCConfigureItem.class);
        }
        return null;
    }
}