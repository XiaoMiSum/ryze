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

package io.github.xiaomisum.ryze.config;

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.testelement.TestElementConstantsInterface;
import io.github.xiaomisum.ryze.support.Cloneable;
import io.github.xiaomisum.ryze.support.Mergeable;
import io.github.xiaomisum.ryze.support.Validatable;
import io.github.xiaomisum.ryze.support.ValidateResult;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/**
 * 配置组接口，用于管理测试元件的配置信息集合
 *
 * <p>配置组是框架中配置管理的核心接口，它继承自Map接口，
 * 用于存储和管理各种类型的配置项。每个配置项通过字符串键进行标识，
 * 值为具体的配置项对象。</p>
 *
 * <p>配置组支持以下核心功能：
 * <ul>
 *   <li>类型安全的配置项获取：通过泛型方法直接获取指定类型的配置项</li>
 *   <li>配置合并：支持将多个配置组合并，实现配置的继承和覆盖机制</li>
 *   <li>配置克隆：支持创建配置组的深拷贝副本</li>
 *   <li>配置验证：支持对配置组中的所有配置项进行统一验证</li>
 * </ul></p>
 *
 * <p>在框架中，配置组通常与上下文(Context)关联，形成多层级的配置管理结构，
 * 包括全局配置、测试套件配置、会话配置和测试步骤配置等不同层级。</p>
 *
 * <p>配置组的使用场景：
 * <ul>
 *   <li>测试元件执行时的配置管理</li>
 *   <li>变量传递和作用域管理</li>
 *   <li>配置继承和覆盖</li>
 *   <li>测试环境的统一配置</li>
 * </ul></p>
 *
 * @author xiaomi
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public interface ConfigureGroup extends Validatable, Map<String, ConfigureItem>, Mergeable<ConfigureGroup>, Cloneable<ConfigureGroup>, TestElementConstantsInterface {

    /**
     * 获取指定键的配置项
     *
     * <p>这是一个类型安全的便捷方法，内部会自动进行类型转换。
     * 如果配置项存在且类型匹配，则直接返回；
     * 如果配置项存在但类型不匹配，则尝试通过JSON序列化/反序列化进行转换。</p>
     *
     * <p>使用示例：
     * <pre>
     * HttpConfig config = configGroup.get("http");
     * </pre></p>
     *
     * @param key 配置项的键，如"http"、"jdbc"等
     * @param <T> 配置项对象的类型，如HttpConfig、JdbcConfig等
     * @return 配置项对象，如果不存在则返回null
     */
    <T extends ConfigureItem<T>> T get(String key);

    /**
     * 合并配置组，将另一个配置组的配置项合并到当前配置组中
     *
     * <p>合并规则：
     * <ul>
     *   <li>创建当前配置组的副本</li>
     *   <li>遍历另一个配置组中的所有键</li>
     *   <li>对于当前配置组中不存在的键，直接添加对应的配置项副本</li>
     *   <li>对于当前配置组中已存在的键，保留当前配置组的配置项（以当前为准）</li>
     * </ul></p>
     *
     * <p>这种合并策略确保了配置的继承性：子级配置不会覆盖父级配置中的同名项，
     * 从而实现配置的分层管理。</p>
     *
     * @param other 要合并的另一个配置组，可以为null
     * @return 合并后的新配置组对象
     */
    default ConfigureGroup merge(ConfigureGroup other) {
        var data = this.copy();
        if (Objects.isNull(other)) {
            return data;
        }
        var keys = new HashSet<>(other.keySet());
        // 过滤当前配置组中已存在的key（重复配置项以当前为准）
        keys.stream().filter(key -> !this.containsKey(key) && other.get(key) != null)
                .forEach(key -> data.put(key, other.get(key).copy()));
        return data;
    }

    /**
     * 获取指定键的配置项并转换为指定类型
     *
     * <p>与 {@link ConfigureGroup#get(String)}方法类似，
     * 但显式指定返回类型，提供更强的类型安全性。</p>
     *
     * @param key   配置项的键
     * @param clazz 配置项对象的类型Class
     * @param <T>   配置项对象的类型
     * @return 指定类型的配置项对象，如果不存在则返回null
     */
    default <T extends ConfigureItem<T>> T get(String key, Class<T> clazz) {
        var v = get(key);
        if (Objects.isNull(v)) {
            return null;
        }
        if (clazz.isAssignableFrom(v.getClass())) {
            return (T) v;
        }
        T t = JSON.parseObject(JSON.toJSONBytes(v), clazz);
        put(key, t);
        return t;
    }

    /**
     * 验证配置组中的所有配置项
     *
     * <p>遍历配置组中的所有配置项，依次调用它们的验证方法，
     * 将验证结果汇总成一个统一的验证结果对象。</p>
     *
     * @return 验证结果，包含所有配置项的验证信息
     */
    @Override
    default ValidateResult validate() {
        ValidateResult result = new ValidateResult();
        this.values().forEach(result::append);
        return result;
    }

    /**
     * 获取变量配置项
     *
     * <p>这是一个便捷方法，用于直接获取配置组中的变量配置项。
     * 变量配置项用于存储测试过程中使用的各种变量。</p>
     *
     * @return RyzeVariables对象，包含所有变量配置
     */
    default RyzeVariables getVariables() {
        return get(VARIABLES, RyzeVariables.class);
    }
}