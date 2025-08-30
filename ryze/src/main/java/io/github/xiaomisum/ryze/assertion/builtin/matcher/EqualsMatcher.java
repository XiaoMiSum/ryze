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
import io.github.xiaomisum.ryze.support.PrimitiveTypeChecker;
import io.github.xiaomisum.ryze.testelement.KW;
import org.hamcrest.Description;

/**
 * 相等匹配器
 *
 * <p>该匹配器用于验证实际值与期望值是否相等。支持多种数据类型的比较，
 * 包括基本类型、字符串、集合、数组、Map和自定义对象等。</p>
 *
 * <p>匹配规则：
 * <ul>
 *   <li>对于基本类型和字符串，支持严格模式（区分大小写）和非严格模式（忽略大小写）</li>
 *   <li>对于数字类型，支持不同类型间的比较（如Integer与Double）</li>
 *   <li>对于集合和数组，逐个比较元素</li>
 *   <li>对于Map，比较键值对</li>
 *   <li>对于自定义对象，递归比较所有字段</li>
 * </ul>
 * </p>
 *
 * <p>支持的关键字: equals, equal, qe, is, =, ==, ===, 等于, 相等</p>
 *
 * @author xiaomi
 * @see Comparator#areEqual(Object, Object, boolean)
 * @since 2025/8/22
 */
@KW({"equals", "equal", "qe", "is", "=", "==", "===", "等于", "相等"})
public class EqualsMatcher extends ProxyMatcher {

    /**
     * 构造函数，使用默认的非严格模式
     *
     * @param expected 期望值
     */
    public EqualsMatcher(Object expected) {
        this(expected, false);
    }

    /**
     * 构造函数，可指定是否严格匹配
     *
     * @param expected 期望值
     * @param strict   严格匹配标志，true表示区分大小写，false表示忽略大小写
     */
    public EqualsMatcher(Object expected, boolean strict) {
        super(expected, strict);
    }

    /**
     * 执行相等匹配逻辑
     *
     * <p>使用Comparator工具类的areEqual方法进行比较，支持多种数据类型的深度比较。
     * 根据strict参数决定是否严格匹配（区分大小写）。</p>
     *
     * @param actualValue 实际值
     * @return 如果实际值与期望值相等返回true，否则返回false
     * @see Comparator#areEqual(Object, Object, boolean)
     */
    @Override
    public boolean matches(Object actualValue) {
        return Comparator.areEqual(actualValue, expectedValue, !strict);
    }

    /**
     * 描述匹配器的期望值和匹配条件
     *
     * <p>生成匹配器的描述信息，包括期望值和匹配模式（严格/非严格）</p>
     *
     * @param description 描述对象
     */
    @Override
    public void describeTo(Description description) {
        var isPrimitiveOrWrapper = PrimitiveTypeChecker.isPrimitiveOrWrapper(expectedValue);
        description.appendText("equals ").appendValue(isPrimitiveOrWrapper ? expectedValue : JSON.toJSONString(expectedValue))
                .appendText(!strict ? " ignore case" : "");

    }
}