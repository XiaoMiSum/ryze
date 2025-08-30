package io.github.xiaomisum.ryze.config;

import io.github.xiaomisum.ryze.support.*;
import io.github.xiaomisum.ryze.support.Cloneable;

/**
 * 配置项接口，表示框架中最基本的配置单元
 * 
 * <p>配置项是框架配置系统的基础构建块，代表一个具体的配置信息。
 * 每个配置项都具有验证、合并、克隆和计算等基本能力。</p>
 * 
 * <p>配置项的主要特点：
 * <ul>
 *   <li>可验证性：支持对配置内容进行验证</li>
 *   <li>可合并性：支持与其他配置项合并</li>
 *   <li>可克隆性：支持创建自身的副本</li>
 *   <li>可计算性：支持在上下文环境中进行表达式计算</li>
 * </ul></p>
 * 
 * <p>配置项的使用场景：
 * <ul>
 *   <li>存储协议相关的配置信息（如HTTP、JDBC等）</li>
 *   <li>存储测试执行环境的配置</li>
 *   <li>存储拦截器配置</li>
 *   <li>存储变量信息</li>
 * </ul></p>
 *
 * @param <T> 配置项的具体类型
 * @author xiaomi
 */
public interface ConfigureItem<T extends ConfigureItem<T>> extends Validatable, Mergeable<T>, Cloneable<T>, Computable<T> {

    /**
     * 合并配置项，将另一个配置项的内容合并到当前配置项中
     * 
     * <p>具体的合并策略由各实现类定义，通常包括：
     * <ul>
     *   <li>创建当前配置项的副本</li>
     *   <li>将另一个配置项的属性值合并到副本中</li>
     *   <li>返回合并后的配置项</li>
     * </ul></p>
     *
     * @param other 要合并的另一个配置项
     * @return 合并后的配置项
     */
    T merge(T other);

    /**
     * 创建配置项的副本
     * 
     * <p>使用Kryo序列化框架创建当前配置项的深拷贝副本，
     * 确保副本与原对象完全独立，修改副本不会影响原对象。</p>
     *
     * @return 配置项的副本
     */
    @SuppressWarnings({"unchecked"})
    default T copy() {
        return (T) KryoUtil.copy(this);
    }
}