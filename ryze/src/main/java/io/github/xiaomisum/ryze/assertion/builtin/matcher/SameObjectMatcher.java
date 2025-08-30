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

package io.github.xiaomisum.ryze.assertion.builtin.matcher;

import com.alibaba.fastjson2.JSON;
import io.github.xiaomisum.ryze.assertion.ProxyMatcher;
import io.github.xiaomisum.ryze.support.Comparator;
import io.github.xiaomisum.ryze.testelement.KW;
import org.hamcrest.Description;

/**
 * 相同对象匹配器
 *
 * <p>该匹配器用于验证实际值是否与期望值为相同的对象（深度相等）。
 * 支持基本类型、值类型和复杂对象的深度比较。</p>
 *
 * <p>匹配规则：
 * <ul>
 *   <li>如果期望值或实际值为null，则两者都必须为null才返回true</li>
 *   <li>在严格模式下，检查两个对象的类型是否完全相同</li>
 *   <li>递归比较对象的所有字段</li>
 * </ul>
 * </p>
 *
 * <p>支持的关键字: same, object, same_object</p>
 *
 * @author xiaomi
 * @see Comparator#areEqual(Object, Object, boolean)
 * @since 2025/8/22
 */
@KW({"same", "object", "same_object"})
public class SameObjectMatcher extends ProxyMatcher {

    /**
     * 构造函数，使用默认的非严格模式
     *
     * @param expectedValue 期望值
     */
    public SameObjectMatcher(Object expectedValue) {
        this(expectedValue, false);
    }

    /**
     * 构造函数，可指定是否严格匹配
     *
     * @param expectedValue 期望值
     * @param strict        严格匹配标志，true表示严格匹配类型，false表示宽松匹配
     */
    public SameObjectMatcher(Object expectedValue, boolean strict) {
        super(expectedValue, strict);
    }

    /**
     * 执行相同对象匹配逻辑
     *
     * <p>根据以下步骤进行匹配：
     * <ol>
     *   <li>基本类型和值类型的比较：如果期望值或实际值为null，则两者都必须为null</li>
     *   <li>严格类型检查：在严格模式下，检查两个对象的类型是否完全相同</li>
     *   <li>递归比较所有字段：使用Comparator.areEqual方法进行深度比较</li>
     * </ol>
     * </p>
     *
     * @param actualValue 实际值
     * @return 如果实际值与期望值相同（深度相等）返回true，否则返回false
     * @see Comparator#areEqual(Object, Object, boolean)
     */
    @Override
    public boolean matches(Object actualValue) {
        // 基本类型和值类型的比较
        if (expectedValue == null || actualValue == null) {
            return expectedValue == actualValue;
        }

        // 1. 严格类型检查
        if (strict && !expectedValue.getClass().equals(actualValue.getClass())) {
            return false;
        }

        // 2. 递归比较所有字段
        return Comparator.areEqual(actualValue, expectedValue, !strict);
    }

    /**
     * 描述匹配器的期望值和匹配条件
     *
     * <p>生成匹配器的描述信息，包括期望值的类型和属性</p>
     *
     * @param description 描述对象
     */
    @Override
    public void describeTo(Description description) {
        description.appendText("an object of type ").appendValue(JSON.toJSONString(expectedValue))
                .appendText(" with the same properties");
    }
}