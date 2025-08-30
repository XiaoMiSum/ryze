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

package io.github.xiaomisum.ryze.testelement;

import com.alibaba.fastjson2.annotation.JSONType;
import io.github.xiaomisum.ryze.config.ConfigureGroup;
import io.github.xiaomisum.ryze.config.ConfigureItem;
import io.github.xiaomisum.ryze.support.fastjson.deserializer.TestElementConfigureObjectReader;

import java.util.HashMap;

/**
 * 测试元件配置数据组
 * <p>
 * 该类用于存储测试元件的配置信息，以键值对的形式管理各种配置项。
 * 继承自HashMap，键为配置项名称，值为具体的配置项对象。该类实现了ConfigureGroup接口，
 * 提供了配置组的复制和合并功能，支持测试执行过程中配置信息的管理和传递。
 * </p>
 * <p>
 * 通过@JSONType注解指定了自定义的反序列化器TestElementConfigureObjectReader，
 * 用于处理JSON格式配置数据的反序列化过程，确保配置数据能正确地转换为Java对象。
 * </p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@JSONType(deserializer = TestElementConfigureObjectReader.class)
public class TestElementConfigureGroup extends HashMap<String, ConfigureItem> implements ConfigureGroup {

    /**
     * 根据键获取指定类型的配置项
     * <p>
     * 通过键从配置组中获取对应的配置项，并将其转换为指定类型。
     * 如果键不存在或对应的值为null，则返回null。
     * </p>
     *
     * @param key 配置项键名
     * @param <T> 配置项类型
     * @return 指定类型的配置项，如果不存在则返回null
     */
    @Override
    public <T extends ConfigureItem<T>> T get(String key) {
        return (T) super.get(key);
    }

    /**
     * 复制配置组
     * <p>
     * 创建当前配置组的一个副本，包含所有非空的配置项。
     * 新的配置组与原配置组内容相同，但彼此独立，修改其中一个不会影响另一个。
     * </p>
     *
     * @return 复制的配置组
     */
    @Override
    public TestElementConfigureGroup copy() {
        TestElementConfigureGroup testElementConfigureGroup = new TestElementConfigureGroup();
        entrySet().stream().filter(entry -> entry.getValue() != null)
                .forEach(entry -> testElementConfigureGroup.put(entry.getKey(), entry.getValue()));
        return testElementConfigureGroup;
    }

    /**
     * 合并配置组
     * <p>
     * 将另一个配置组的内容合并到当前配置组中。如果有相同的键，
     * 则另一个配置组中的值会覆盖当前配置组中的值。
     * </p>
     *
     * @param other 要合并的配置组
     * @return 合并后的配置组
     */
    @Override
    public ConfigureGroup merge(ConfigureGroup other) {
        return ConfigureGroup.super.merge(other);
    }
}