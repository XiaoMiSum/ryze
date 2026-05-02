/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2026.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.interceptor.RyzeInterceptor;
import io.github.xiaomisum.ryze.report.ReporterListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 报告配置项类，用于存储和管理报告列表
 *
 * <p>该类是 {@link ConfigureItem}接口的具体实现，
 * 继承自ArrayList，用于存储特定类型的报告列表。
 * 它允许将多个报告组织在一起，并提供统一的配置项接口。</p>
 *
 * <p>报告配置项的主要功能：
 * <ul>
 *   <li>存储报告列表：可以添加、删除和遍历报告</li>
 *   <li>配置项接口：实现配置项的基本功能，如合并、克隆和计算</li>
 *   <li>报告管理：支持对报告列表进行统一管理</li>
 * </ul></p>
 *
 * <p>使用场景：
 * <ul>
 *   <li>存储测试元件的报告配置</li>
 *   <li>在配置组中作为报告配置项使用</li>
 *   <li>支持报告的继承和合并</li>
 * </ul></p>
 *
 * @param <T> 报告类型
 * @author xiaomi
 * Created at 2025/7/20 14:51
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ReporterConfigureItem<T extends ReporterListener> extends ArrayList<T> implements ConfigureItem<ReporterConfigureItem<T>> {

    /**
     * 默认构造函数，创建空的报告配置项
     */
    public ReporterConfigureItem() {

    }

    /**
     * 基于报告列表的构造函数
     *
     * @param interceptors 报告列表
     */
    public ReporterConfigureItem(List<T> interceptors) {
        super(interceptors);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 合并报告配置项
     *
     * <p>将当前报告列表与另一个报告配置项中的报告列表合并，
     * 创建一个新的报告配置项，包含两个列表中的所有报告。</p>
     *
     * @param other 另一个报告配置项
     * @return 合并后的报告配置项
     */
    @Override
    public ReporterConfigureItem<T> merge(ReporterConfigureItem<T> other) {
        ReporterConfigureItem<T> filterConfigItem = new ReporterConfigureItem<>();
        filterConfigItem.addAll(this);
        if (other != null) {
            filterConfigItem.addAll(other);
        }

        return filterConfigItem;
    }

    /**
     * 创建报告配置项的副本
     *
     * <p>创建一个新的报告配置项，包含当前配置项中的所有报告。</p>
     *
     * @return 报告配置项的副本
     */
    @Override
    public ReporterConfigureItem<T> copy() {
        ReporterConfigureItem<T> filterConfigItem = new ReporterConfigureItem<>();
        filterConfigItem.addAll(this);
        return filterConfigItem;
    }

    /**
     * 在上下文中计算报告配置项
     *
     * <p>对于报告配置项，计算操作是空操作，直接返回自身。</p>
     *
     * @param context 测试上下文
     * @return 当前报告配置项
     */
    @Override
    public ReporterConfigureItem<T> evaluate(ContextWrapper context) {
        return this;
    }

    /**
     * 提供流畅的API用于构建变量配置
     * <p>
     * 提供了多种方式来添加变量到配置中。</p>
     */
    public static class Builder {

        /**
         * 报告配置项实例
         */
        private final ReporterConfigureItem interceptors = new ReporterConfigureItem<>();

        /**
         * 添加报告配置到当前配置中
         *
         * @param interceptor 变量配置
         * @return 构建器实例
         */
        public Builder apply(RyzeInterceptor<?> interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        public ReporterConfigureItem build() {
            return interceptors;
        }

    }
}