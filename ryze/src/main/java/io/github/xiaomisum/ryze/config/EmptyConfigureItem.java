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

import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;

/**
 * 空配置项实现类，作为默认配置项使用
 *
 * <p>该类是 {@link ConfigureItem}接口的空实现，
 * 用于在不需要特殊配置时作为占位符使用。
 * 它不包含任何实际的配置信息，所有操作都是空操作或返回默认值。</p>
 *
 * <p>主要用途：
 * <ul>
 *   <li>作为默认配置项，避免null值处理</li>
 *   <li>在构建器模式中作为默认配置</li>
 *   <li>在配置合并时作为基础配置</li>
 * </ul></p>
 *
 * @author xiaomi
 * Created at 2025/7/20 12:29
 */
public class EmptyConfigureItem implements ConfigureItem<EmptyConfigureItem> {

    /**
     * 创建空配置项构建器实例
     *
     * @return 空配置项构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 合并配置项，对于空配置项，直接返回自身
     *
     * @param other 要合并的另一个配置项
     * @return 当前配置项
     */
    @Override
    public EmptyConfigureItem merge(EmptyConfigureItem other) {
        return this;
    }

    /**
     * 在上下文中计算配置项，对于空配置项，直接返回自身
     *
     * @param context 测试上下文
     * @return 当前配置项
     */
    @Override
    public EmptyConfigureItem evaluate(ContextWrapper context) {
        return this;
    }


    /**
     * 空配置项构建器类，用于构建空配置项实例
     *
     * <p>该构建器继承自 {@link AbstractTestElement.ConfigureBuilder}，
     * 提供了构建空配置项的通用方法。</p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, EmptyConfigureItem> {

        /**
         * 构建空配置项实例
         *
         * @return 空配置项实例
         */
        public EmptyConfigureItem build() {
            return new EmptyConfigureItem();
        }
    }
}