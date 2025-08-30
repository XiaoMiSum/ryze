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

import com.alibaba.fastjson2.JSONObject;
import io.github.xiaomisum.ryze.context.ContextWrapper;
import io.github.xiaomisum.ryze.testelement.AbstractTestElement;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 框架变量配置类，用于存储和管理测试过程中的变量
 *
 * <p>该类是 {@link ConfigureItem}接口的具体实现，
 * 继承自FastJSON的 {@link JSONObject}类，提供了变量存储和管理功能。
 * 它用于在测试执行过程中存储和传递变量信息。</p>
 *
 * <p>变量配置的主要功能：
 * <ul>
 *   <li>变量存储：以键值对形式存储变量</li>
 *   <li>变量合并：支持将多个变量配置合并</li>
 *   <li>表达式计算：支持在上下文中对变量值进行表达式计算</li>
 *   <li>构建器模式：提供流畅的构建器API</li>
 * </ul></p>
 *
 * <p>使用场景：
 * <ul>
 *   <li>存储测试用例中的局部变量</li>
 *   <li>在测试步骤间传递数据</li>
 *   <li>存储全局变量</li>
 *   <li>支持变量表达式计算</li>
 * </ul></p>
 *
 * @author xiaomi
 */
public class RyzeVariables extends JSONObject implements ConfigureItem<RyzeVariables> {

    /**
     * 默认构造函数，创建空的变量配置
     */
    public RyzeVariables() {
    }

    /**
     * 基于变量映射的构造函数
     *
     * @param variables 变量映射
     */
    public RyzeVariables(Map<? extends String, ?> variables) {
        this.putAll(variables);
    }

    /**
     * 创建变量配置构建器实例
     *
     * @return 变量配置构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 添加所有变量到当前配置中
     *
     * @param variables 变量映射
     */
    public void putAll(Map<? extends String, ?> variables) {
        if (variables != null) {
            super.putAll(variables);
        }
    }

    /**
     * 添加单个变量到当前配置中
     *
     * @param key   变量键
     * @param value 变量值
     * @return 当前变量配置实例
     */
    public RyzeVariables add(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 合并变量配置
     *
     * <p>将当前变量配置与另一个变量配置合并，
     * 如果另一个配置不为null，则将其变量添加到当前配置中，
     * 已存在的键以当前配置为准。</p>
     *
     * @param other 另一个变量配置
     * @return 合并后的变量配置（当前实例）
     */
    @Override
    public RyzeVariables merge(RyzeVariables other) {
        if (other != null) {
            var copy = new RyzeVariables(this);
            this.clear();
            this.putAll(other);
            this.putAll(copy);
        }
        return this;
    }

    /**
     * 创建变量配置的副本
     *
     * <p>创建一个新的变量配置实例，包含当前配置中的所有变量。</p>
     *
     * @return 变量配置的副本
     */
    @Override
    public RyzeVariables copy() {
        return new RyzeVariables(this);
    }

    /**
     * 在上下文中计算变量配置
     *
     * <p>对变量配置中的每个值进行表达式计算，
     * 将包含变量引用和函数调用的表达式替换为实际值。</p>
     *
     * @param context 测试上下文
     * @return 计算后的变量配置
     */
    @Override
    public RyzeVariables evaluate(ContextWrapper context) {
        this.replaceAll((key, value) -> context.evaluate(value));
        return this;
    }

    /**
     * 变量配置构建器类，提供流畅的API用于构建变量配置
     *
     * <p>该构建器继承自 {@link AbstractTestElement.ConfigureBuilder}，
     * 提供了多种方式来添加变量到配置中。</p>
     */
    public static class Builder extends AbstractTestElement.ConfigureBuilder<Builder, RyzeVariables> {

        /**
         * 变量配置实例
         */
        private final RyzeVariables variables = new RyzeVariables();

        /**
         * 添加变量映射到配置中
         *
         * @param variables 变量映射
         * @return 构建器实例
         */
        public Builder put(Map<? extends String, ?> variables) {
            this.variables.putAll(variables);
            return this;
        }

        /**
         * 添加变量映射到配置中
         *
         * @param variables 变量映射
         * @return 构建器实例
         */
        public Builder apply(Map<? extends String, ?> variables) {
            this.variables.putAll(variables);
            return this;
        }

        /**
         * 添加变量配置到当前配置中
         *
         * @param variables 变量配置
         * @return 构建器实例
         */
        public Builder put(RyzeVariables variables) {
            this.variables.merge(variables);
            return this;
        }

        /**
         * 添加变量配置到当前配置中
         *
         * @param variables 变量配置
         * @return 构建器实例
         */
        public Builder apply(RyzeVariables variables) {
            this.variables.merge(variables);
            return this;
        }

        /**
         * 通过消费者函数添加变量
         *
         * @param consumer 消费者函数
         * @return 构建器实例
         */
        public Builder put(Consumer<RyzeVariables> consumer) {
            consumer.accept(variables);
            return this;
        }

        /**
         * 通过消费者函数添加变量
         *
         * @param consumer 消费者函数
         * @return 构建器实例
         */
        public Builder apply(Consumer<RyzeVariables> consumer) {
            consumer.accept(variables);
            return this;
        }

        /**
         * 添加单个变量到配置中
         *
         * @param name  变量名称
         * @param value 变量值
         * @return 构建器实例
         */
        public Builder put(String name, Object value) {
            this.variables.put(name, value);
            return this;
        }

        /**
         * 添加单个变量到配置中
         *
         * @param name  变量名称
         * @param value 变量值
         * @return 构建器实例
         */
        public Builder apply(String name, Object value) {
            this.variables.put(name, value);
            return this;
        }

        /**
         * 构建变量配置实例
         *
         * @return 变量配置实例
         */
        @Override
        public RyzeVariables build() {
            return variables;
        }
    }
}