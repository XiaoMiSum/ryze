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

package io.github.xiaomisum.ryze.config;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局配置类，用于存储和管理全局配置信息
 *
 * <p>该类是 {@link ConfigureGroup}接口的具体实现，
 * 基于ConcurrentHashMap实现线程安全的配置存储。
 * 它用于存储整个测试会话范围内的全局配置信息。</p>
 *
 * <p>全局配置的特点：
 * <ul>
 *   <li>全局可见：在整个测试会话中都可以访问</li>
 *   <li>线程安全：基于ConcurrentHashMap实现，支持并发访问</li>
 *   <li>动态配置：支持在运行时动态添加和修改配置</li>
 *   <li>类型安全：通过泛型支持类型安全的配置项获取</li>
 * </ul></p>
 *
 * <p>全局配置的使用场景：
 * <ul>
 *   <li>存储全局变量</li>
 *   <li>存储全局拦截器配置</li>
 *   <li>存储全局测试环境配置</li>
 *   <li>存储全局函数配置</li>
 * </ul></p>
 *
 * @author xiaomi
 * Created at 2025/7/20 14:11
 */
@SuppressWarnings({"rawtypes"})
public class GlobalConfigure extends ConcurrentHashMap<String, ConfigureItem> implements ConfigureGroup {

    /**
     * 获取指定键的配置项
     *
     * <p>通过键获取对应的配置项，如果不存在则返回null。
     * 该方法提供了类型安全的配置项获取方式。</p>
     *
     * @param key 配置项的键
     * @param <T> 配置项的类型
     * @return 配置项对象，如果不存在则返回null
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends ConfigureItem<T>> T get(String key) {
        return (T) super.get(key);
    }

    /**
     * 创建全局配置的副本
     *
     * <p>遍历当前全局配置中的所有配置项，
     * 为每个配置项创建副本并添加到新的全局配置中。</p>
     *
     * @return 全局配置的副本
     */
    @Override
    public GlobalConfigure copy() {
        GlobalConfigure globalConfig = new GlobalConfigure();
        this.forEach((k, v) -> globalConfig.put(k, v.copy()));
        return globalConfig;
    }
}