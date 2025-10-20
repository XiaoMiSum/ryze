/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
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

package io.github.xiaomisum.ryze.assertion;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.github.xiaomisum.ryze.ApplicationConfig;
import io.github.xiaomisum.ryze.assertion.builtin.matcher.*;
import org.hamcrest.Matcher;

/**
 * 匹配器工厂类
 *
 * <p>该类提供了创建各种匹配器的静态方法，根据规则和期望值动态创建相应的匹配器实例。</p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>根据规则名称查找对应的匹配器类型</li>
 *   <li>通过JSON反序列化创建匹配器实例</li>
 *   <li>提供比较操作符枚举</li>
 * </ul>
 * </p>
 *
 * @author xiaomi
 * @since 2025/8/22
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Matchers extends org.hamcrest.Matchers {

    /**
     * 创建匹配器实例
     *
     * <p>根据指定的规则名称、期望值和严格匹配标志创建相应的匹配器实例。
     * 通过ApplicationConfig获取规则名称与匹配器类型的映射关系，
     * 然后使用JSON反序列化创建匹配器实例。</p>
     *
     * @param rule     规则名称，如"equals"、"contains"等
     * @param expected 期望值
     * @param strict   严格匹配标志
     * @return 创建的匹配器实例
     * @see ApplicationConfig#getMatcherKeyMap()
     */
    public static Matcher<Object> createMatcher(String rule, Object expected, boolean strict) {
        Class<? extends Matcher> type = ApplicationConfig.getMatcherKeyMap().get(rule.toLowerCase());
        String string = JSONObject.of("expectedValue", expected, "strict", strict).toString();
        return JSON.parseObject(string, type);
    }

    /**
     * 创建任意包含匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否包含期望值。
     * 适用于字符串、集合等类型的包含检查。</p>
     *
     * @return AnyContainsMatcher实例
     */
    public static Matcher<Object> anyContains() {
        return new AnyContainsMatcher();
    }

    /**
     * 创建任意相等匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否与期望值相等。
     * 使用更宽松的相等比较逻辑。</p>
     *
     * @return AnyEqualsMatcher实例
     */
    public static Matcher<Object> anyEquals() {
        return new AnyEqualsMatcher();
    }

    /**
     * 创建包含匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否包含期望值。
     * 与anyContains类似，但可能具有不同的匹配逻辑。</p>
     *
     * @return ContainsMatcher实例
     */
    public static Matcher<Object> contains() {
        return new ContainsMatcher();
    }

    /**
     * 创建相等匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否与期望值相等。
     * 使用严格的相等比较逻辑。</p>
     *
     * @return EqualsMatcher实例
     */
    public static Matcher<Object> equals() {
        return new EqualsMatcher();
    }

    /**
     * 创建空值匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否为空。
     * 可以匹配null值或空集合、空字符串等。</p>
     *
     * @return IsEmpty实例
     */
    public static Matcher<Object> isEmpty() {
        return new IsEmpty();
    }

    /**
     * 创建空值匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否为null。</p>
     *
     * @return IsEmpty实例
     */
    public static Matcher<Object> isNull() {
        return new IsEmpty();
    }

    /**
     * 创建非空匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否不为空。
     * 可以匹配非null值、非空集合、非空字符串等。</p>
     *
     * @return IsNotEmpty实例
     */
    public static Matcher<Object> isNotEmpty() {
        return new IsNotEmpty();
    }

    /**
     * 创建非空匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否不为null。</p>
     *
     * @return IsNotEmpty实例
     */
    public static Matcher<Object> isNotNull() {
        return new IsNotEmpty();
    }

    /**
     * 创建不包含匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否不包含期望值。</p>
     *
     * @return NotContainsMatcher实例
     */
    public static Matcher<Object> notContains() {
        return new NotContainsMatcher();
    }

    /**
     * 创建不相等匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否与期望值不相等。</p>
     *
     * @return NotEqualsMatcher实例
     */
    public static Matcher<Object> notEquals() {
        return new NotEqualsMatcher();
    }

    /**
     * 创建不相等匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否与期望值不相等。
     * 与notEquals方法功能相同。</p>
     *
     * @return NotEqualsMatcher实例
     */
    public static Matcher<Object> not() {
        return new NotEqualsMatcher();
    }

    /**
     * 创建正则表达式匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否匹配给定的正则表达式。</p>
     *
     * @return RegexMatcher实例
     */
    public static Matcher<Object> regex() {
        return new RegexMatcher();
    }

    /**
     * 创建相同对象匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否与期望值是同一个对象（引用相等）。</p>
     *
     * @return SameObjectMatcher实例
     */
    public static Matcher<Object> same() {
        return new SameObjectMatcher();
    }

    /**
     * 创建大于匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否大于期望值。</p>
     *
     * @return GreaterMatcher实例
     */
    public static Matcher<Object> greater() {
        return new GreaterMatcher();
    }

    /**
     * 创建大于等于匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否大于等于期望值。</p>
     *
     * @return GreaterOrEqualsMatcher实例
     */
    public static Matcher<Object> greaterOrEquals() {
        return new GreaterOrEqualsMatcher();
    }

    /**
     * 创建小于匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否小于期望值。</p>
     *
     * @return LessMatcher实例
     */
    public static Matcher<Object> less() {
        return new LessMatcher();
    }

    /**
     * 创建小于等于匹配器
     *
     * <p>创建一个匹配器，用于检查实际值是否小于等于期望值。</p>
     *
     * @return LessOrEqualsMatcher实例
     */
    public static Matcher<Object> lessOrEquals() {
        return new LessOrEqualsMatcher();
    }

    /**
     * 比较操作符枚举
     *
     * <p>定义了四种基本的数值比较操作符及其描述符号：
     * <ul>
     *   <li>LESS_THAN: 小于 (&lt;)</li>
     *   <li>LESS_THAN_OR_EQUAL: 小于等于 (≤)</li>
     *   <li>GREATER_THAN: 大于 (&gt;)</li>
     *   <li>GREATER_THAN_OR_EQUAL: 大于等于 (≥)</li>
     * </ul>
     * </p>
     */
    public enum CompareOperator {
        /**
         * 小于操作符
         */
        LESS_THAN("<"),

        /**
         * 小于等于操作符
         */
        LESS_THAN_OR_EQUAL("≤"),

        /**
         * 大于操作符
         */
        GREATER_THAN(">"),

        /**
         * 大于等于操作符
         */
        GREATER_THAN_OR_EQUAL("≥");

        /**
         * 操作符描述符号
         */
        private final String desc;

        /**
         * 构造函数
         *
         * @param desc 操作符描述符号
         */
        CompareOperator(String desc) {
            this.desc = desc;
        }

        /**
         * 获取操作符描述符号
         *
         * @return 操作符描述符号
         */
        public String getDesc() {
            return desc;
        }
    }
}